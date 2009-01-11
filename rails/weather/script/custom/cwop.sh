#!/usr/bin/env bash

export PATH=$PATH:/usr/local/bin

#echo "running cwop.sh"

cd ~/apps/weather/current
export RAILS_ENV=production
./script/runner components/cwop.rb

