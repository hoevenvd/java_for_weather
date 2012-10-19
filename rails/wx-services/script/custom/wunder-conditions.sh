#!/bin/bash

#echo "running wunder-conditions.sh"

# assume run from rails root (~/apps/wx-services/current)

./script/runner lib/wunder_conditions.rb
