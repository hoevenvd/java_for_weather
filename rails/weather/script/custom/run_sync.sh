#!/bin/sh

export PATH=$PATH:/usr/local/bin

cd /home/weather/apps/weather/current

while true; do
  ./script/runner ./components/ws_data_sync.rb
  echo "sync stopped" | mail -s "sync stopped" tom@tom.org
  sleep 300
done
