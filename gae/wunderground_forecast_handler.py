#import logging

from google.appengine.ext import webapp
from google.appengine.ext.webapp import util

from weather.services.wunder_forecast import ForecastFactory
from weather.services.wunder_conditions import ConditionsFactory

#    forecast_location: KBVY
#    conditions_location: KBVY

class WundergroundForecastHandler(webapp.RequestHandler):
    def get(self):
      if len(self.request.get("location")):
        location = self.request.get("location")
      else:
        location = "KBVY"
      ForecastFactory.put(location)
      ConditionsFactory.put(location)

def main():
  # Register mapping with application.
  application = webapp.WSGIApplication([("/store_forecast", WundergroundForecastHandler)], debug=True)
  util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
