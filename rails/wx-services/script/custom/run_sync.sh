#!/bin/sh

export PATH=$PATH:/usr/local/bin

#assume run from RAILS_ROOT (/home/weather/apps/weather/current)

while true; do
  ./script/runner ./lib/ws_data_sync.rb
  echo "sync stopped" | mail -s "sync stopped" $MAILTO
  sleep 300
done

