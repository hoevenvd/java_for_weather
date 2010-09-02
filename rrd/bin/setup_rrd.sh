#!/bin/sh

# Get config information from rrd config file
. ./conf/rrd_weather.cfg

if [ ! -d "$RRD_DATABASE" ]; then
    mkdir -p $RRD_DATABASE
    echo "$RRD_DATABASE created"
fi


if [ ! -f "$RRD_DATABASE/outside_temp.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/outside_temp.rrd \
    --start=now \
    --step=60 \
    DS:outsidetemp:GAUGE:527040:-100:140 \
    DS:outsidetempm:GAUGE:527040:-50:50 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/apparent_temp.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/apparent_temp.rrd \
    --start=now \
    --step=60 \
    DS:apparenttemp:GAUGE:527040:-100:140 \
    DS:apparenttempm:GAUGE:527040:-50:50 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/inside_temp.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/inside_temp.rrd \
    --start=now \
    --step=60 \
    DS:insidetemp:GAUGE:527040:-100:140 \
    DS:insidetempm:GAUGE:527040:-50:50 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/dewpoint.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/dewpoint.rrd \
    --start=now \
    --step=60 \
    DS:dewpoint:GAUGE:527040:-100:140 \
    DS:dewpointm:GAUGE:527040:-50:50 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/outside_hum.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/outside_hum.rrd \
    --start=now \
    --step=60 \
    DS:outsidehumidity:GAUGE:527040:0:100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/inside_hum.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/inside_hum.rrd \
    --start=now \
    --step=60 \
    DS:insidehumidity:GAUGE:527040:0:100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/windspeed.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/windspeed.rrd \
    --start=now \
    --step=60 \
    DS:windspeed:GAUGE:527040:0:200 \
    DS:windspeedm:GAUGE:527040:0:100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/windgust.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/windgust.rrd \
    --start=now \
    --step=60 \
    DS:windgust:GAUGE:527040:0:200 \
    DS:windgustm:GAUGE:527040:0:100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/winddirection.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/winddirection.rrd \
    --start=now \
    --step=60 \
    DS:winddirection:GAUGE:527040:0:360 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/rainfall.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/rainfall.rrd \
    --start=now \
    --step=60 \
    DS:rainfall:GAUGE:527040:0:40 \
    DS:rainfallm:GAUGE:527040:0:100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/rainrate.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/rainrate.rrd \
    --start=now \
    --step=60 \
    DS:rainrate:GAUGE:527040:0:40 \
    DS:rainratem:GAUGE:527040:0:100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/pressure.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/pressure.rrd \
    --start=now \
    --step=60 \
    DS:pressure:GAUGE:527040:25:35 \
    DS:pressurem:GAUGE:527040:900:1100 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

if [ ! -f "$RRD_DATABASE/solarradiation.rrd" ]; then
    $RRDTOOL_BIN/rrdtool create $RRD_DATABASE/solarradiation.rrd \
    --start=now \
    --step=60 \
    DS:solarradiation:GAUGE:527040:0:1600 \
    RRA:AVERAGE:0.5:1:527040 \
    RRA:MIN:0.5:1:527040 \
    RRA:MAX:0.5:1:527040 \
    RRA:LAST:0.5:1:527040 
fi

echo "RRD files created in $RRD_DATABASE"

if [ ! -d "$RRD_GRAPH" ]; then
    mkdir -p $RRD_GRAPH
    echo "$RRD_GRAPH created"
fi

echo "RRD graphs will be created in $RRD_GRAPH"
echo "All done."
    
