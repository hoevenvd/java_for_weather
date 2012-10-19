#!/bin/bash

#echo "running wunder-forecast.sh"

# assume run from rails root (~/apps/wx-services/current)

./script/runner lib/wunder_forecast.rb
