#!/bin/bash

#echo "running wunderground.sh"

# assume run from rails root (~/apps/wx-services/current)

./script/runner lib/wunder_conditions.rb
./script/runner lib/wunder_forecast.rb
