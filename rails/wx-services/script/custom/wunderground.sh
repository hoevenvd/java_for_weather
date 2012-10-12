#!/bin/bash

#echo "running wunderground.sh"

#cd ~/apps/wx-services/current

./script/runner components/wunder_conditions.rb
./script/runner components/wunder_forecast.rb
