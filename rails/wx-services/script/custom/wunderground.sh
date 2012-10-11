#!/bin/bash

#echo "running wunderground.sh"

#cd ~/apps/wx-services/current

export RAILS_ENV=production

./script/runner components/wunder_conditions.rb >/dev/null
./script/runner components/wunder_forecast.rb >/dev/null
