#import os
#os.environ['DJANGO_SETTINGS_MODULE'] = 'settings'

#from google.appengine.dist import use_library
#use_library('django', '1.2')

import logging
import datetime
import pytz

from google.appengine.api import memcache
from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from protorpc.webapp import service_handlers
import simplejson
import conditionsservice
import archiveservice
import condition
import archive
import wxutils
import appconfig
import wunder_forecast

from weather.units import wind

class MainHandler(webapp.RequestHandler):
    def get(self):
      if len(self.request.get("location")):
        location = self.request.get("location")
        station_settings = appconfig.load_settings(location)
        if station_settings and 'tz' in station_settings: localtz = pytz.timezone(station_settings['tz'])
      else:
        location = "01915"
        localtz = pytz.timezone('America/New_York')
      forecast = wunder_forecast.ForecastFactory.get(location)
      self.response.out.write('<html><head><meta HTTP-EQUIV="Refresh" CONTENT="3">')
      
      self.response.out.write("""<script type="text/javascript">"""
                              """var _gaq = _gaq || [];"""
                              """_gaq.push(['_setAccount', 'UA-11819295-3']);"""
                              """_gaq.push(['_trackPageview']);"""
                              """(function() {"""
                              """var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;"""
                              """ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';"""
                              """var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);"""
                              """})();""")
      self.response.out.write('</script></head><body>')
          
      if len(self.request.get("metric")):
        metric = True
      else:
        metric = False
      cond = condition.ConditionFactory.find(location, metric)
      if cond:
        self.response.out.write("current date: " + datetime.datetime.strftime(datetime.datetime.now(tz=localtz),"%Y-%m-%d %H:%M:%S%z") + "<p>")
        self.response.out.write("Conditions for: " + location + "<p>")
        self.response.out.write("station type: " + cond.station + "<p>")
        self.response.out.write(str(cond.date) + '<p>')
        self.response.out.write('temp: ' + str(cond.outside_temp) + '<p>')
        self.response.out.write('feels like: ' + str(cond.apparent_temp) + '<p>')
        self.response.out.write('dewpoint: ' + str(cond.dewpoint) + '<p>')
        if not cond.wind_speed:
          self.response.out.write('wind: calm <p>')
        else:
          self.response.out.write('wind: ' + str(cond.wind_speed) + ' from ' +
                    wind.deg_to_short_str(cond.wind_direction) + '<p>')
        self.response.out.write('ten min wind avg: ' + str(cond.ten_min_wind_speed) + '<p>')
        self.response.out.write('pressure: ' + str(cond.pressure) + ' - ' + cond.pressure_trend + '<p>')
        self.response.out.write('humidity: ' + str(cond.outside_humidity) + '<p>')
        if cond.is_raining:
          self.response.out.write('raining at ' + str(cond.rain_rate) + ' inches per hour' + '<p>')
        self.response.out.write('this rain event: ' + str(cond.storm_rain) + '<p>')
        self.response.out.write('rain today: ' + str(cond.day_rain) + ' inches' + '<p>')
        self.response.out.write('rain this month: ' + str(cond.month_rain) + ' inches' + '<p>')
        self.response.out.write('rain this year: ' + str(cond.year_rain) + ' inches' + '<p>')
        if cond.solar_radiation <> 32767:
          self.response.out.write('solar radiation: ' + str(cond.solar_radiation) + ' watts per square meter' + '<p>')
        self.response.out.write('sunrise: ' + str(cond.sunrise) + '<p>')
        self.response.out.write('sunset: ' + str(cond.sunset) + '<p>')
      else:
        self.response.out.write('no conditions found' + "<p>")
        logging.info('no conditions found')
      self.response.out.write('latest archive record: ' + str(archive.ArchiveFactory.find_latest_datetime(location)) + '<p>')
      self.response.out.write('last rain: ' + str(wxutils.last_rain(location)) + '<p>')
      if forecast:
        self.response.out.write('\nForecast:')
      self.response.out.write('</body></html>')

current_conditions_factory = service_handlers.ServiceHandlerFactory.default(
        conditionsservice.ConditionsService)
archive_factory = service_handlers.ServiceHandlerFactory.default(
        archiveservice.ArchiveService)

def main():
  # Register mapping with application.
  application = webapp.WSGIApplication([current_conditions_factory.mapping('/conditionsservice'),
                                        archive_factory.mapping('/archiveservice'),
                                        ("/", MainHandler)],
  debug=True)
  util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
