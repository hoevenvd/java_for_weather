#!/bin/sh

# Get config information from rrd config file
. ./conf/rrd_weather.cfg

sh $RRD_SCRIPT/rrd_weather.sh do_general_24h_m
sh $RRD_SCRIPT/rrd_weather.sh do_general_24h_i
sh $RRD_SCRIPT/rrd_weather.sh do_general_72h_m
sh $RRD_SCRIPT/rrd_weather.sh do_general_72h_i
sh $RRD_SCRIPT/rrd_weather.sh do_general_1w_m
sh $RRD_SCRIPT/rrd_weather.sh do_general_1w_i
sh $RRD_SCRIPT/rrd_weather.sh do_general_2w_m
sh $RRD_SCRIPT/rrd_weather.sh do_general_2w_i
sh $RRD_SCRIPT/rrd_weather.sh do_general_month_m
sh $RRD_SCRIPT/rrd_weather.sh do_general_month_i
sh $RRD_SCRIPT/rrd_weather.sh do_wind_detailed_m
sh $RRD_SCRIPT/rrd_weather.sh do_wind_detailed_i
sh $RRD_SCRIPT/rrd_weather.sh do_wind_detailed_k

# rm VRULE*

# cp /data/website/graphs/*.png /home/maarten/apps/weather/current/public/graphs
