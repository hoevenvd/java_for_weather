#!/bin/bash

#echo "running noaa.sh"

export PATH=$PATH:/usr/local/bin

cd ~/apps/weather/current

export RAILS_ENV=development

./script/runner components/noaa.rb KBVY
./script/runner components/noaa_utils.rb KBVY


