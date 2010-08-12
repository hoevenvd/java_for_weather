#!/bin/sh

export CHARTS_DIR=/home/tom/tmp/charts

scp -q $CHARTS_DIR/*.jpg tomorg@tommitchell.net:/home/tomorg/public_html/images/weather/charts/

