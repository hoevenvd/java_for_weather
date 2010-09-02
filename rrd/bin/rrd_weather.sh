#!/bin/sh           

# Get config information from rrd config file
. ./conf/rrd_weather.cfg

#
# rrd_weather - weather driver script for RRD logging and graphing
# Copyright (C) 2009  pablo@blueoakdb.com
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
# 

#
# $Id: rrd_weather,v 1.11 2009/11/06 20:37:53 pablo Exp $
#

#
# This script is used to both log data to an RRD and generate graphs from the
# data.  The script can e invoked from a CGI or put in a loop to periodically 
# create the graphs (and have them ftp'd - see SYNC_GRAPHS below).
#
# High-level, we have two modes of operation:
#
#    o logger
#    o graphing
#
# We can also (ftp/ssh/rsh) copy the graphs to another location.  Set the SYNC_GRAPHS 
# variable below with the corresponding program to invoke.  Ideally, the program should 
# be run in the background to not all our execution.  But it's up to you.
#
# An example may be you create another shell script to ftp your graphs.  Set SYNC_GRAPHS
# to that shell script and we'll execute the script after we generate the graphs.
#
# Basically, we run anything you tell us to run.
#
# To log data, we expect to read STDIN.  Use `mon_wdconsole'
# to pipe data to us.
#
#   % mon_wdconsole_any temp 2,4,5,6,7,8,45,46,73 | rrd_weather logger
#
# Better yet, see the `run_rrd_weather_log' script which calls us.  :)
#


#
# Given a date/time range, compute the RRD VRULE to print a vertical 
# line at midnight
#
compute_midnight()
{
   FROM_TIME="$1"
   TO_TIME="$2"
   MIDNIGHT_COLOR="$3"

   FROM_TIME=`date -d "$1" '+%s'`
   if [ $? -ne 0 ] ; then
      echo "bailing ..."
      return
   fi

   TO_TIME=`date -d "$2" '+%s'`
   if [ $? -ne 0 ] ; then
      echo "bailing ..."
      return
   fi

   VRULE_GLOBAL=`echo 'hey you, get to work' | awk -v from_time=$FROM_TIME -v to_time=$TO_TIME -v color=$MIDNIGHT_COLOR '{
      debug = 0;

      if ( debug )
      {
         printf "DEBUG:  from_time = %d; to_time = %d\n", from_time, to_time;
      }

      day_index = strftime ("%d", from_time);
      yyyy_mm = strftime ("%Y %m", from_time);

      day_index++;
      if ( debug )  printf "DEBUG:  %s\n", yyyy_mm " " day_index " 00 00 00"
      midnight = mktime ( yyyy_mm " " day_index " 00 00 00" );
      if ( midnight == -1 )
      {
         printf "Error computing time.\n";
         exit 1;
      }

      if ( debug && midnight < to_time )
      {
         printf "DEBUG:  %s (%d)\n", strftime ("%m/%d/%Y", midnight ), midnight;
      }

      while ( midnight < to_time )
      {
         printf "VRULE:%d#%s ", midnight, color;

         day_index++;
         midnight = mktime ( yyyy_mm " " day_index " 00 00 00" );
         if ( midnight == -1 )
         {
            printf "Error computing time.\n";
            exit 1;
         }

         if ( debug && midnight < to_time )
         {
            printf "DEBUG:  %s (%d)\n", strftime ("%m/%d/%Y", midnight ), midnight;
         }
      }
   }'`
   return $?
}


#
# $1 - graph file name
# $2 - period to graph (subtracted from `now')
#
# Computations
# ------------
# Wind Chil:  if wind_speed  > 0 and temperature <= 1.67, return wind chill else unknown
# Humidex  :  if temperature > 9 and humidex > temperature, return humidex else unknown
#
do_temperature()
{
   WIDTH="$1"
   HEIGHT="$2"
   GRAPH_FILE_NAME="$3" # " - needed for emacs colorization.  :p
   PERIOD="$4"
   TITLE="$5"
   FOOTER="$6"
   UOM="$7"

   #
   # We store our data in Centigrade, convert it to Fahrenheit
   #
   if [ "$UOM" = "c" ] ; then
      F2C_FORMULA=''
   else
      F2C_FORMULA=',1.8,*,32,+'
   fi

   $RRDTOOL_BIN/rrdtool graph $GRAPH_FILE_NAME \
      -s "$PERIOD" \
      --imgformat PNG \
      -w $WIDTH -h $HEIGHT \
      --title "$TITLE" \
      $LAZY \
      $PANGO_MARKUP \
      --tabwidth $TEMPERATURE_TABWIDTH \
      -R mono \
      --y-grid 5:1 \
      --no-minor \
      --color MGRID#$MAJOR_GRID_COLOR --color ARROW#$ARROW_COLOR \
      $VRULE_GLOBAL \
      DEF:temperature=$OUTSIDETEMP_FILE:outsidetempm:AVERAGE \
      DEF:apparent=$APPARENT_FILE:apparenttempm:AVERAGE \
      DEF:dewpoint=$DEWPOINT_FILE:dewpointm:AVERAGE \
      CDEF:conv_temperature="temperature${F2C_FORMULA}" \
      CDEF:conv_apparent="apparent${F2C_FORMULA}" \
      CDEF:conv_dew_point="dewpoint${F2C_FORMULA}" \
      VDEF:last_temperature=conv_temperature,LAST \
      VDEF:max_temperature=conv_temperature,MAXIMUM \
      VDEF:min_temperature=conv_temperature,MINIMUM \
      VDEF:last_dew_point=conv_dew_point,LAST \
      VDEF:max_dew_point=conv_dew_point,MAXIMUM \
      VDEF:min_dew_point=conv_dew_point,MINIMUM \
      VDEF:last_apparent=conv_apparent,LAST \
      VDEF:max_apparent=conv_apparent,MAXIMUM \
      VDEF:min_apparent=conv_apparent,MINIMUM \
      AREA:conv_temperature#$TEMPERATURE_AREA_COLOR \
      LINE1:conv_temperature#$TEMPERATURE_LINE_COLOR:"$TEMPERATURE"\
      GPRINT:max_temperature:"${TEMPERATURE_MAX_SHIM}$MAX"' %-4.1lf' \
      GPRINT:max_temperature:"$TIMESTAMP:strftime" \
      GPRINT:min_temperature:"${TEMPERATURE_MIN_SHIM}$MIN"' %-4.1lf' \
      GPRINT:min_temperature:"$TIMESTAMP"':strftime' \
      GPRINT:last_temperature:"${TEMPERATURE_LAST_SHIM}$LAST"' %-4.1lf' \
      COMMENT:'\l' \
      LINE1:conv_apparent#$APPARENT_LINE_COLOR:"$APPARENT" \
      GPRINT:max_apparent:"${APPARENT_MAX_SHIM}$MAX"' %-4.1lf' \
      GPRINT:max_apparent:"$TIMESTAMP"':strftime' \
      GPRINT:min_apparent:"${APPARENT_MIN_SHIM}$MIN"' %-4.1lf' \
      GPRINT:min_apparent:"$TIMESTAMP"':strftime' \
      GPRINT:last_apparent:"${APPARENT_LAST_SHIM}$LAST"' %-4.1lf' \
      COMMENT:'\l' \
      LINE1:conv_dew_point#$DEW_POINT_LINE_COLOR:"$DEW_POINT" \
      GPRINT:max_dew_point:"${DEW_POINT_MAX_SHIM}$MAX"' %-4.1lf' \
      GPRINT:max_dew_point:"$TIMESTAMP"':strftime' \
      GPRINT:min_dew_point:"${DEW_POINT_MIN_SHIM}$MIN"' %-4.1lf' \
      GPRINT:min_dew_point:"$TIMESTAMP"':strftime' \
      GPRINT:last_dew_point:"${DEW_POINT_LAST_SHIM}$LAST"' %-4.1lf' \
      COMMENT:'\l' COMMENT:'\s'
}

do_solar()
{
   WIDTH="$1"
   HEIGHT="$2"
   GRAPH_FILE_NAME="$3" # " - needed for emacs colorization.  :p
   PERIOD="$4"
   TITLE="$5"
   FOOTER="$6"
   UOM="$7"

   $RRDTOOL_BIN/rrdtool graph $GRAPH_FILE_NAME \
      -s "$PERIOD" \
      --imgformat PNG \
      -w $WIDTH -h $HEIGHT \
      --title "$TITLE" \
      $LAZY \
      $PANGO_MARKUP \
      --tabwidth $SOLAR_TABWIDTH \
      -R mono \
      --y-grid 100:1 \
      --no-minor \
      --units-exponent 0 \
      --color MGRID#$MAJOR_GRID_COLOR --color ARROW#$ARROW_COLOR \
      $VRULE_GLOBAL \
      DEF:solar=$SOLAR_FILE:solarradiation:AVERAGE \
      VDEF:last_solar=solar,LAST \
      VDEF:max_solar=solar,MAXIMUM \
      VDEF:min_solar=solar,MINIMUM \
      VDEF:avg_solar=solar,AVERAGE \
      AREA:solar#$SOLAR_AREA_COLOR \
      LINE1:solar#$SOLAR_LINE_COLOR:"$SOLAR"\
      GPRINT:max_solar:"${SOLAR_MAX_SHIM}$MAX"' %-4.0lf' \
      GPRINT:max_solar:"$TIMESTAMP:strftime" \
      GPRINT:avg_solar:"${SOLAR_AVG_SHIM}$AVG"' %-4.0lf' \
      GPRINT:last_solar:"${SOLAR_LAST_SHIM}$LAST"' %-4.0lf' \
      COMMENT:'\l' COMMENT:'\s' 
}




#
# Relative Humidity is valid only above -10C
#
do_humidity()
{
   WIDTH="$1"
   HEIGHT="$2"
   GRAPH_FILE_NAME="$3"
   PERIOD="$4"
   TITLE="$5"
   FOOTER="$6"

   $RRDTOOL_BIN/rrdtool graph $GRAPH_FILE_NAME \
      -s "$PERIOD" \
      --imgformat PNG \
      -w $WIDTH -h $HEIGHT \
      --title "$TITLE" \
      $LAZY \
      $PANGO_MARKUP \
      --tabwidth $HUMIDITY_TABWIDTH \
      -R mono \
      --y-grid 20:1 \
      --no-minor \
      --color MGRID#$MAJOR_GRID_COLOR --color ARROW#$ARROW_COLOR \
      $VRULE_GLOBAL \
      DEF:temperature=$OUTSIDETEMP_FILE:outsidetempm:AVERAGE \
      DEF:humidity=$OUTSIDEHUM_FILE:outsidehumidity:AVERAGE \
      CDEF:valid_humidity=temperature,-10,GE,humidity,UNKN,IF \
      VDEF:last_valid_humidity=valid_humidity,LAST \
      VDEF:max_valid_humidity=valid_humidity,MAXIMUM \
      VDEF:min_valid_humidity=valid_humidity,MINIMUM \
      AREA:valid_humidity#$HUMIDITY_AREA_COLOR \
      LINE1:valid_humidity#$HUMIDITY_LINE_COLOR:"$HUMIDITY" \
      GPRINT:max_valid_humidity:"${HUMDITY_MAX_SHIM}$MAX"' %3.lf' \
      GPRINT:max_valid_humidity:"$TIMESTAMP:strftime" \
      GPRINT:min_valid_humidity:"${HUMDITY_MIN_SHIM}$MIN"' %3.0lf' \
      GPRINT:min_valid_humidity:"$TIMESTAMP:strftime" \
      GPRINT:last_valid_humidity:"${HUMDITY_LAST_SHIM}$LAST"' %3.0lf\l' \
      COMMENT:'\s'
}

do_barometer()
{
   WIDTH="$1"
   HEIGHT="$2"
   GRAPH_FILE_NAME="$3"
   PERIOD="$4"
   TITLE="$5"
   UOM="$6"

   #
   # Handle UOM-specifics
   #
   if [ "$UOM" = "hPa" ] ; then
      FORMAT="-4.1lf"
      HPA2INHG_FORMULA=''
   else # inHg
      FORMAT="-4.2lf"
      HPA2INHG_FORMULA=',33.86388158,/'
   fi

   $RRDTOOL_BIN/rrdtool graph $GRAPH_FILE_NAME \
      -s "$PERIOD" \
      --imgformat PNG \
      -w $WIDTH -h $HEIGHT \
      --title "$TITLE" \
      --rigid \
      --alt-y-grid \
      --alt-autoscale \
      --no-minor \
      --units-exponent 0 \
      $LAZY \
      $PANGO_MARKUP \
      --tabwidth $BAROMETRIC_PRESSURE_TABWIDTH \
      -R mono \
      --color MGRID#$MAJOR_GRID_COLOR --color ARROW#$ARROW_COLOR \
      $VRULE_GLOBAL \
      DEF:barometer=$PRESSURE_FILE:pressurem:AVERAGE \
      CDEF:conv_barometer="barometer${HPA2INHG_FORMULA}" \
      VDEF:last_barometer=conv_barometer,LAST \
      VDEF:max_barometer=conv_barometer,MAXIMUM \
      VDEF:min_barometer=conv_barometer,MINIMUM \
      AREA:conv_barometer#$PRESSURE_AREA_COLOR \
      LINE1:conv_barometer#$PRESSURE_LINE_COLOR:"$BAROMETER" \
      GPRINT:max_barometer:"${BAROMETRIC_PRESSURE_MAX_SHIM}$MAX"' %'$FORMAT \
      GPRINT:max_barometer:"$TIMESTAMP:strftime" \
      GPRINT:min_barometer:"${BAROMETRIC_PRESSURE_MIN_SHIM}$MIN"' %'$FORMAT \
      GPRINT:min_barometer:"$TIMESTAMP:strftime" \
      GPRINT:last_barometer:"${BAROMETRIC_PRESSURE_LAST_SHIM}$LAST"' %'$FORMAT'\l' \
      COMMENT:'\s'

}

do_daily_rain()
{
   WIDTH="$1"
   HEIGHT="$2"
   GRAPH_FILE_NAME="$3"
   PERIOD="$4"
   TITLE="$5"
   UOM="$6"

   #
   # Handle UOM-specifics
   #
   if [ "$UOM" = "mm" ] ; then
      FORMAT="-4.1lf"
      MM2INCH_FORMULA=',25.4,*'
   else # in
      FORMAT="-5.2lf"
      MM2INCH_FORMULA=''
      #MM2INCH_FORMULA=',0.039369091,*'
   fi

   $RRDTOOL_BIN/rrdtool graph $GRAPH_FILE_NAME \
      -s "$PERIOD" \
      --imgformat PNG \
      -w $WIDTH -h $HEIGHT \
      --title "$TITLE" \
      $LAZY \
      $PANGO_MARKUP \
      --tabwidth $RAIN_TABWIDTH \
      -R mono \
      --rigid \
      --alt-y-grid \
      --alt-autoscale \
      --lower-limit 0 \
      --units-exponent 0 \
      --no-minor \
      --color MGRID#$MAJOR_GRID_COLOR --color ARROW#$ARROW_COLOR \
      $VRULE_GLOBAL \
      DEF:rain_rate=$RAINRATE_FILE:rainrate:MAX \
      DEF:rain_tick=$RAINFALL_FILE:rainfall:MAX \
      CDEF:conv_rain_rate="rain_rate${MM2INCH_FORMULA}" \
      CDEF:conv_rain_tick="rain_tick${MM2INCH_FORMULA}" \
      VDEF:last_rain_rate=conv_rain_rate,LAST \
      VDEF:max_rain_rate=conv_rain_rate,MAXIMUM \
      VDEF:last_rain_tick=conv_rain_tick,LAST \
      VDEF:max_rain_tick=conv_rain_tick,MAXIMUM \
      AREA:conv_rain_tick#$RAINTICK_AREA_COLOR:"$RAINTICK" \
      LINE1:conv_rain_tick#$RAINTICK_LINE_COLOR \
      GPRINT:max_rain_tick:"${RAINTICK_MAX_SHIM}$MAX"' %'$FORMAT \
      GPRINT:max_rain_tick:"$TIMESTAMP:strftime" \
      GPRINT:last_rain_tick:"${RAINTICK_LAST_SHIM}$LAST"' %'$FORMAT'\l' \
      COMMENT:'\l' \
      AREA:conv_rain_rate#$RAINRATE_AREA_COLOR \
      LINE2:conv_rain_rate#$RAINRATE_LINE_COLOR:"$RAINRATE" \
      GPRINT:max_rain_rate:"${RAINRATE_MAX_SHIM}$MAX"' %'$FORMAT \
      GPRINT:max_rain_rate:"$TIMESTAMP:strftime" \
      GPRINT:last_rain_rate:"${RAINRATE_LAST_SHIM}$LAST"' %'$FORMAT'\l'
}

#
# If the wind speed is zero, we don't plot anything
#
# Note:  wind speed is in mph, we convert it to m/s
#
# We encode the wind direction as area
#
do_wind()
{
   WIDTH="$1"
   HEIGHT="$2"
   GRAPH_FILE_NAME="$3"
   PERIOD="$4"
   TITLE="$5"
   UOM="$6"

   if [ "$UOM" = "m/s" ] ; then
      FORMAT="-3.1lf"
      KTS2TARGET_FORMULA=',0.44704,*'
   fi
   if [ "$UOM" = "mph" ] ; then
      FORMAT="-3.1lf"
      KTS2TARGET_FORMULA=',1.0,*'
   fi
   if [ "$UOM" = "kts" ] ; then
      FORMAT="-3.1lf"
      KTS2TARGET_FORMULA=',0.86897624,*'
   fi

   $RRDTOOL_BIN/rrdtool graph $GRAPH_FILE_NAME \
      -s "$PERIOD" \
      --imgformat PNG \
      -w $WIDTH -h $HEIGHT \
      --title "$TITLE" \
      $LAZY \
      $PANGO_MARKUP \
      --tabwidth $WIND_TABWIDTH \
      -R mono \
      --lower-limit 0 \
      --rigid \
      --alt-y-grid \
      --alt-autoscale \
      --no-minor \
      --color MGRID#$MAJOR_GRID_COLOR --color ARROW#$ARROW_COLOR \
      $VRULE_GLOBAL \
      DEF:wind_speed=$WINDSPEED_FILE:windspeed:MAX \
      DEF:gust_speed=$WINDGUST_FILE:windgust:MAX \
      CDEF:conv_wind_speed="wind_speed${KTS2TARGET_FORMULA}" \
      CDEF:conv_gust_speed="gust_speed${KTS2TARGET_FORMULA}" \
      DEF:wind_direction=$WINDDIR_FILE:winddirection:AVERAGE \
      CDEF:N='wind_direction,315,GT,wind_direction,22.5,LE,+,conv_wind_speed,UNKN,IF' \
      CDEF:NE='wind_direction,22.5,GT,wind_direction,67.5,LE,*,conv_wind_speed,UNKN,IF' \
      CDEF:E='wind_direction,67.5,GT,wind_direction,112.5,LE,*,conv_wind_speed,UNKN,IF' \
      CDEF:SE='wind_direction,112.5,GT,wind_direction,157.5,LE,*,conv_wind_speed,UNKN,IF' \
      CDEF:S='wind_direction,157.5,GT,wind_direction,202.5,LE,*,conv_wind_speed,UNKN,IF' \
      CDEF:SW='wind_direction,202.5,GT,wind_direction,247.5,LE,*,conv_wind_speed,UNKN,IF' \
      CDEF:W='wind_direction,247.5,GT,wind_direction,292.5,LE,*,conv_wind_speed,UNKN,IF' \
      CDEF:NW='wind_direction,292.5,GT,wind_direction,337.5,LE,*,conv_wind_speed,UNKN,IF' \
      VDEF:last_conv_wind_speed=conv_wind_speed,LAST \
      VDEF:max_conv_wind_speed=conv_wind_speed,MAXIMUM \
      VDEF:last_conv_gust_speed=conv_gust_speed,LAST \
      VDEF:max_conv_gust_speed=conv_gust_speed,MAXIMUM \
      AREA:conv_gust_speed#$WIND_GUST_AREA_COLOR:"$WIND_GUST" \
      COMMENT:'\j' \
      LINE1:conv_gust_speed#$WIND_GUST_LINE_COLOR \
      AREA:N#$NORTH_AREA_COLOR:"$NORTH" \
      AREA:NW#$NORTH_WEST_AREA_COLOR:"$NORTH_WEST" \
      AREA:W#$WEST_AREA_COLOR:"$WEST" \
      AREA:SW#$SOUTH_WEST_AREA_COLOR:"$SOUTH_WEST" \
      AREA:S#$SOUTH_AREA_COLOR:"$SOUTH" \
      AREA:SE#$SOUTH_EAST_AREA_COLOR:"$SOUTH_EAST" \
      AREA:E#$EAST_AREA_COLOR:"$EAST" \
      AREA:NE#$NORTH_EAST_AREA_COLOR:"$NORTH_EAST" \
      COMMENT:'\j' \
      COMMENT:'\s' \
      LINE1:conv_wind_speed#$WIND_SPEED_LINE_COLOR \
      GPRINT:max_conv_gust_speed:"${WIND_GUST}${WIND_GUST_MAX_SHIM}$MAX"' %'$FORMAT \
      GPRINT:max_conv_gust_speed:"$TIMESTAMP"':strftime' \
      GPRINT:last_conv_gust_speed:"${WIND_GUST_LAST_SHIM}$LAST"' %'$FORMAT \
      COMMENT:'\l' \
      GPRINT:max_conv_wind_speed:"${WIND_SPEED}${WIND_SPEED_MAX_SHIM}$MAX"' %'$FORMAT \
      GPRINT:max_conv_wind_speed:"$TIMESTAMP"':strftime' \
      GPRINT:last_conv_wind_speed:"${WIND_SPEED_LAST_SHIM}$LAST"' %'$FORMAT'\l' \

# debug
#      VDEF:speed=conv_wind_speed,AVERAGE \
#      VDEF:dir=wind_direction,AVERAGE \
#      PRINT:speed:'speed is %-4.1lf' \
#      PRINT:dir:'dir is %-4.1lf' \
}

do_graph_general_24h()
{
   UOM=$1
   XPERIOD="24 HOURS"

   compute_midnight "-24 hours" "now" "$MIDNIGHT_LINE"
   if [ $? -ne 0 ] ; then
      VRULE_GLOBAL=""
   fi

   if [ "$UOM" = "metric" ] ; then
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_24H_TEMP_M_FILE"        now-24h "${TEMPERATURE_TITLE_M:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_M:-Unset}"   "${TEMPERATURE_UOM_M:-c}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_24H_SOLAR_M_FILE"       now-24h "${SOLAR_TITLE_M:-Unset} $XPERIOD"               "${SOLAR_FOOTER_M:-Unset}"         "${SOLAR_UOM_M:-c}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_24H_HUMIDITY_M_FILE"    now-24h "${HUMIDITY_TITLE_M:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_M:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_24H_BAROMETER_M_FILE"   now-24h "${BAROMETRIC_PRESSURE_TITLE_M:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_M:-hPa}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_24H_DAILY_RAIN_M_FILE"  now-24h "${RAIN_TITLE_M:-Unset} $XPERIOD"                "${RAIN_UOM_M:-mm}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_24H_WIND_M_FILE"        now-24h "${WIND_TITLE_M:-Unset} $XPERIOD"                "${WIND_UOM_M:-ms}"
   else
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_24H_TEMP_I_FILE"        now-24h "${TEMPERATURE_TITLE_I:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_I:-Unset}"            "${TEMPERATURE_UOM_I:-f}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_24H_SOLAR_I_FILE"       now-24h "${SOLAR_TITLE_I:-Unset} $XPERIOD"               "${SOLAR_FOOTER_I:-Unset}"                  "${SOLAR_UOM_I:-f}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_24H_HUMIDITY_I_FILE"    now-24h "${HUMIDITY_TITLE_I:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_I:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_24H_BAROMETER_I_FILE"   now-24h "${BAROMETRIC_PRESSURE_TITLE_I:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_I:-inHg}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_24H_DAILY_RAIN_I_FILE"  now-24h "${RAIN_TITLE_I:-Unset} $XPERIOD"                "${RAIN_UOM_I:-in}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_24H_WIND_I_FILE"        now-24h "${WIND_TITLE_I:-Unset} $XPERIOD"                "${WIND_UOM_I:-mph}"
   fi
}

do_graph_general_72h()
{
   UOM=$1
   XPERIOD="72 HOURS"

   compute_midnight "-72 hours" "now" "$MIDNIGHT_LINE"
   if [ $? -ne 0 ] ; then
      VRULE_GLOBAL=""
   fi

   if [ "$UOM" = "metric" ] ; then
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_72H_TEMP_M_FILE"         now-72h "${TEMPERATURE_TITLE_M:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_M:-Unset}"    "${TEMPERATURE_UOM_M:-c}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_72H_SOLAR_M_FILE"        now-72h "${SOLAR_TITLE_M:-Unset} $XPERIOD"               "${SOLAR_FOOTER_M:-Unset}"    "${SOLAR_UOM_M:-c}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_72H_HUMIDITY_M_FILE"     now-72h "${HUMIDITY_TITLE_M:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_M:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_72H_BAROMETER_M_FILE"    now-72h "${BAROMETRIC_PRESSURE_TITLE_M:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_M:-hPa}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_72H_DAILY_RAIN_M_FILE"   now-72h "${RAIN_TITLE_M:-Unset} $XPERIOD"                "${RAIN_UOM_M:-mm}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_72H_WIND_M_FILE"         now-72h "${WIND_TITLE_M:-Unset} $XPERIOD"                "${WIND_UOM_M:-ms}"
   else
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_72H_TEMP_I_FILE"         now-72h "${TEMPERATURE_TITLE_I:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_I:-Unset}"             "${TEMPERATURE_UOM_I:-f}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_72H_SOLAR_I_FILE"        now-72h "${SOLAR_TITLE_I:-Unset} $XPERIOD"               "${SOLAR_FOOTER_I:-Unset}"                   "${SOLAR_UOM_I:-f}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_72H_HUMIDITY_I_FILE"     now-72h "${HUMIDITY_TITLE_I:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_I:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_72H_BAROMETER_I_FILE"    now-72h "${BAROMETRIC_PRESSURE_TITLE_I:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_I:-inHg}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_72H_DAILY_RAIN_I_FILE"   now-72h "${RAIN_TITLE_I:-Unset} $XPERIOD"                "${RAIN_UOM_I:-in}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_72H_WIND_I_FILE"         now-72h "${WIND_TITLE_I:-Unset} $XPERIOD"                "${WIND_UOM_I:-mph}"
   fi
}


do_graph_general_1w()
{
   UOM=$1
   XPERIOD="1 WEEK"

   compute_midnight "-168 hours" "now" "$MIDNIGHT_LINE"
   if [ $? -ne 0 ] ; then
      VRULE_GLOBAL=""
   fi

   if [ "$UOM" = "metric" ] ; then
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_1W_TEMP_M_FILE"         now-1w "${TEMPERATURE_TITLE_M:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_M:-Unset}"    "${TEMPERATURE_UOM_M:-c}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_1W_SOLAR_M_FILE"        now-1w "${SOLAR_TITLE_M:-Unset} $XPERIOD"               "${SOLAR_FOOTER_M:-Unset}"    "${SOLAR_UOM_M:-c}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_1W_HUMIDITY_M_FILE"     now-1w "${HUMIDITY_TITLE_M:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_M:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_1W_BAROMETER_M_FILE"    now-1w "${BAROMETRIC_PRESSURE_TITLE_M:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_M:-hPa}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_1W_DAILY_RAIN_M_FILE"   now-1w "${RAIN_TITLE_M:-Unset} $XPERIOD"                "${RAIN_UOM_M:-mm}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_1W_WIND_M_FILE"         now-1w "${WIND_TITLE_M:-Unset} $XPERIOD"                "${WIND_UOM_M:-ms}"
   else
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_1W_TEMP_I_FILE"         now-1w "${TEMPERATURE_TITLE_I:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_I:-Unset}"             "${TEMPERATURE_UOM_I:-f}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_1W_SOLAR_I_FILE"        now-1w "${SOLAR_TITLE_I:-Unset} $XPERIOD"               "${SOLAR_FOOTER_I:-Unset}"                   "${SOLAR_UOM_I:-f}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_1W_HUMIDITY_I_FILE"     now-1w "${HUMIDITY_TITLE_I:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_I:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_1W_BAROMETER_I_FILE"    now-1w "${BAROMETRIC_PRESSURE_TITLE_I:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_I:-inHg}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_1W_DAILY_RAIN_I_FILE"   now-1w "${RAIN_TITLE_I:-Unset} $XPERIOD"                "${RAIN_UOM_I:-in}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_1W_WIND_I_FILE"         now-1w "${WIND_TITLE_I:-Unset} $XPERIOD"                "${WIND_UOM_I:-mph}"
   fi
}

do_graph_general_2w()
{
   UOM=$1
   XPERIOD="2 WEEK"

   compute_midnight "-336 hours" "now" "$MIDNIGHT_LINE"
   if [ $? -ne 0 ] ; then
      VRULE_GLOBAL=""
   fi

   if [ "$UOM" = "metric" ] ; then
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_2W_TEMP_M_FILE"         now-2w "${TEMPERATURE_TITLE_M:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_M:-Unset}"    "${TEMPERATURE_UOM_M:-c}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_2W_SOLAR_M_FILE"        now-2w "${SOLAR_TITLE_M:-Unset} $XPERIOD"               "${SOLAR_FOOTER_M:-Unset}"    "${SOLAR_UOM_M:-c}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_2W_HUMIDITY_M_FILE"     now-2w "${HUMIDITY_TITLE_M:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_M:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_2W_BAROMETER_M_FILE"    now-2w "${BAROMETRIC_PRESSURE_TITLE_M:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_M:-hPa}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_2W_DAILY_RAIN_M_FILE"   now-2w "${RAIN_TITLE_M:-Unset} $XPERIOD"                "${RAIN_UOM_M:-mm}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_2W_WIND_M_FILE"         now-2w "${WIND_TITLE_M:-Unset} $XPERIOD"                "${WIND_UOM_M:-ms}"
   else
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_2W_TEMP_I_FILE"         now-2w "${TEMPERATURE_TITLE_I:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_I:-Unset}"             "${TEMPERATURE_UOM_I:-f}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_2W_SOLAR_I_FILE"        now-2w "${SOLAR_TITLE_I:-Unset} $XPERIOD"               "${SOLAR_FOOTER_I:-Unset}"                   "${SOLAR_UOM_I:-f}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_2W_HUMIDITY_I_FILE"     now-2w "${HUMIDITY_TITLE_I:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_I:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_2W_BAROMETER_I_FILE"    now-2w "${BAROMETRIC_PRESSURE_TITLE_I:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_I:-inHg}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_2W_DAILY_RAIN_I_FILE"   now-2w "${RAIN_TITLE_I:-Unset} $XPERIOD"                "${RAIN_UOM_I:-in}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_2W_WIND_I_FILE"         now-2w "${WIND_TITLE_I:-Unset} $XPERIOD"                "${WIND_UOM_I:-mph}"
   fi
}



do_graph_general_month()
{
   UOM=$1
   XPERIOD="THIS MONTH"
   
   #
   # Start graphing on the first of the month
   #
   FIRST_OF_MONTH="`date '+%b 1'`999999999"
   FIRST_OF_MONTH="`date '+12am %m/01/%Y'`"
   compute_midnight "$FIRST_OF_MONTH" "now" "$MIDNIGHT_LINE"
   if [ $? -ne 0 ] ; then
      VRULE_GLOBAL=""
   fi
  VRULE_GLOBAL=""

   FIRST_OF_MONTH="now-1month"
   
   if [ "$UOM" = "metric" ] ; then
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_MONTH_TEMP_M_FILE"        "$FIRST_OF_MONTH" "${TEMPERATURE_TITLE_M:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_M:-Unset}"   "${TEMPERATURE_UOM_M:-c}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_MONTH_SOLAR_M_FILE"       "$FIRST_OF_MONTH" "${SOLAR_TITLE_M:-Unset} $XPERIOD"               "${SOLAR_FOOTER_M:-Unset}"   "${SOLAR_UOM_M:-c}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_MONTH_HUMIDITY_M_FILE"    "$FIRST_OF_MONTH" "${HUMIDITY_TITLE_M:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_M:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_MONTH_BAROMETER_M_FILE"   "$FIRST_OF_MONTH" "${BAROMETRIC_PRESSURE_TITLE_M:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_M:-hPa}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_MONTH_DAILY_RAIN_M_FILE"  "$FIRST_OF_MONTH" "${RAIN_TITLE_M:-Unset} $XPERIOD"                "${RAIN_UOM_M:-mm}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_MONTH_WIND_M_FILE"        "$FIRST_OF_MONTH" "${WIND_TITLE_M:-Unset} $XPERIOD"                "${WIND_UOM_M:-ms}"
   else
      do_temperature  "${TEMPERATURE_X_DIMENSION:-700}" "${TEMPERATURE_Y_DIMENSION:-160}" "$GRAPH_MONTH_TEMP_I_FILE"        "$FIRST_OF_MONTH" "${TEMPERATURE_TITLE_I:-Unset} $XPERIOD"         "${TEMPERATURE_FOOTER_I:-Unset}"   "${TEMPERATURE_UOM_I:-f}"
      do_solar        "${SOLAR_X_DIMENSION:-700}"       "${SOLAR_Y_DIMENSION:-160}"       "$GRAPH_MONTH_SOLAR_I_FILE"       "$FIRST_OF_MONTH" "${SOLAR_TITLE_I:-Unset} $XPERIOD"               "${SOLAR_FOOTER_I:-Unset}"   "${SOLAR_UOM_I:-f}"
      do_humidity     "${HUMIDITY_X_DIMENSION:-700}"    "${HUMIDITY_Y_DIMENSION:-80}"     "$GRAPH_MONTH_HUMIDITY_I_FILE"    "$FIRST_OF_MONTH" "${HUMIDITY_TITLE_I:-Unset} $XPERIOD"            "${HUMIDITY_FOOTER_I:-Unset}"
      do_barometer    "${BAROMETRIC_X_DIMENSION:-700}"  "${BAROMETRIC_Y_DIMENSION:-100}"  "$GRAPH_MONTH_BAROMETER_I_FILE"   "$FIRST_OF_MONTH" "${BAROMETRIC_PRESSURE_TITLE_I:-Unset} $XPERIOD" "${BAROMETRIC_PRESSURE_UOM_I:-inHg}"
      do_daily_rain   "${RAIN_X_DIMENSION:-700}"        "${RAIN_Y_DIMENSION:-80}"         "$GRAPH_MONTH_DAILY_RAIN_I_FILE"  "$FIRST_OF_MONTH" "${RAIN_TITLE_I:-Unset} $XPERIOD"                "${RAIN_UOM_I:-in}"
      do_wind         "${WIND_X_DIMENSION:-700}"        "${WIND_Y_DIMENSION:-100}"        "$GRAPH_MONTH_WIND_I_FILE"        "$FIRST_OF_MONTH" "${WIND_TITLE_I:-Unset} $XPERIOD"                "${WIND_UOM_I:-mph}"
   fi
}

do_graph_wind_detailed()
{
   UOM=$1

   #
   # We over compute the VRULE_GLOBAL and let RRD filter
   #
   compute_midnight "-72 hours" "now" "$MIDNIGHT_LINE"
   if [ $? -ne 0 ] ; then
      VRULE_GLOBAL=""
   fi

   if [ "$UOM" = "metric" ] ; then
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_30M_WIND_M_FILE"  now-30m "${WIND_TITLE_M:-Unset} 30 MINUTE" "${WIND_UOM_M:-ms}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_1H_WIND_M_FILE"   now-1h  "${WIND_TITLE_M:-Unset} 1 HOUR" "${WIND_UOM_M:-ms}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_4H_WIND_M_FILE"   now-4h  "${WIND_TITLE_M:-Unset} 4 HOUR" "${WIND_UOM_M:-ms}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_8H_WIND_M_FILE"   now-8h  "${WIND_TITLE_M:-Unset} 8 HOUR" "${WIND_UOM_M:-ms}"
   elif [ "$UOM" = "imperial" ] ; then
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_30M_WIND_I_FILE"  now-30m "${WIND_TITLE_I:-Unset} 30 MINUTE" "${WIND_UOM_I:-mph}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_1H_WIND_I_FILE"   now-1h  "${WIND_TITLE_I:-Unset} 1 HOUR" "${WIND_UOM_I:-mph}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_4H_WIND_I_FILE"   now-4h  "${WIND_TITLE_I:-Unset} 4 HOUR" "${WIND_UOM_I:-mph}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_8H_WIND_I_FILE"   now-8h  "${WIND_TITLE_I:-Unset} 8 HOUR" "${WIND_UOM_I:-mph}"
   else # kts (knots)
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_30M_WIND_K_FILE"  now-30m "${WIND_TITLE_K:-Unset} 30 MINUTE" "${WIND_UOM_K:-kts}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_1H_WIND_K_FILE"   now-1h  "${WIND_TITLE_K:-Unset} 1 HOUR" "${WIND_UOM_K:-kts}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_4H_WIND_K_FILE"   now-4h  "${WIND_TITLE_K:-Unset} 4 HOUR" "${WIND_UOM_K:-kts}"
      do_wind "${WIND_X_DIMENSION:-700}" "${WIND_Y_DIMENSION:-100}" "$GRAPH_8H_WIND_K_FILE"   now-8h  "${WIND_TITLE_K:-Unset} 8 HOUR" "${WIND_UOM_K:-kts}"
   fi
}

#
# Main
#

#
# Use pango-markup for legends and labels?  It'll depend on your version of rrd 
# installed.
#
if [ "$ENABLE_PANGO_MARKUP" = "no" ] ; then
   PANGO_MARKUP=""
else
   PANGO_MARKUP="--pango-markup"
fi

#
# Is our configuration forcing graph generation?
#
if [ "$FORCE_GRAPH_GENERATION" = "no" ] ; then
   LAZY="--lazy"
else
   LAZY=""
fi

#
# Debug
#
# echo "`date` - called ..." >> /var/tmp/rrd_weather.log

#
# Are we symlink'd?  If so, we use our symlink name for our argument
#
ARGUMENT="$1"
#FILE_NAME="`basename $0`"
#if [ "$FILE_NAME" != "rrd_weather" ] ; then
#   ARGUMENT=`echo $FILE_NAME | sed -e "s/rrd_weather_//" -e 's/.sh//g'`
#fi

#
# Oops!
#
#if [ $# -ne 1 -a -z "$ARGUMENT" ] ; then
#   print_usage;
#   exit 1
#fi

#
# If our argument starts with do_*, then we're invoked as a CGI so we need to spit some CGI-friendly
# information.
#
case "$ARGUMENT" in
   graph_*) echo 'Content-type: text/html'
            echo
            echo '<!-- Calling RRD'
            ;;
esac;

case "$1" in
   init)              do_init;;
   log)               do_logging;;
   *_general_24h_m)   do_graph_general_24h metric;;
   *_general_24h_i)   do_graph_general_24h imperial;;
   *_general_72h_m)   do_graph_general_72h metric;;
   *_general_72h_i)   do_graph_general_72h imperial;;
   *_general_1w_m)    do_graph_general_1w metric;;
   *_general_1w_i)    do_graph_general_1w imperial;;
   *_general_2w_m)    do_graph_general_2w metric;;
   *_general_2w_i)    do_graph_general_2w imperial;;
   *_general_month_m) do_graph_general_month metric;;
   *_general_month_i) do_graph_general_month imperial;;
   *_wind_detailed_m) do_graph_wind_detailed metric;;
   *_wind_detailed_i) do_graph_wind_detailed imperial;;
   *_wind_detailed_k) do_graph_wind_detailed knots;;
   *)                 echo "unknown parameter supplied"
                      print_usage;
                      exit 1;;
esac

#
# Spew HTML-comment terminator so our RRD output doesn't hork the page
#
case "$ARGUMENT" in
   graph_*) echo '-->'
            ;;
esac;

exit 0
