import logging
import datetime
import pytz

from google.appengine.ext import webapp
from google.appengine.ext.webapp import util

import appconfig
from wunder_forecast import ForecastFactory

#    forecast_location: KBVY
#    conditions_location: KBVY

class WundergroundForecastHandler(webapp.RequestHandler):
    def get(self):
      if len(self.request.get("location")):
        location = self.request.get("location")
      else:
        location = "01915"
        localtz = pytz.timezone('America/New_York')
      # defaults
      station_settings = appconfig.load_settings(location)
      if station_settings and 'tz' in station_settings: localtz = pytz.timezone(station_settings['tz'])
      ForecastFactory.put(location)

def main():
  # Register mapping with application.
  application = webapp.WSGIApplication([("/store_forecast", WundergroundForecastHandler)], debug=True)
  util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
