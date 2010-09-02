#!/usr/bin/env bash

export PATH=$PATH:/usr/local/bin

#echo "running update_rrd.sh"

cd /develop/maarten-wx-webapp-branch/
export RAILS_ENV=production
./script/runner components/update_rrd.rb

