import datetime
import pytz
from google.appengine.api import memcache
from xml.etree import ElementTree
import urllib

# http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query=Chicago,IL

URL='http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query='

class ForecastDay:
    def __init__(self, dayname, forecast):
      self.dayname = dayname
      self.forecast = forecast
  
class Forecast:
    def __init__(self, as_of):
      self.retrieved = datetime.datetime.utcnow().replace(tzinfo=pytz.utc)
      self.as_of = as_of
      self.days = []
    def append(self, dayname, forecast):
      self.days.append(ForecastDay(dayname, forecast))
      
class ForecastFactory:
  @staticmethod
  def get(location):
    return memcache.get(location, namespace='forecast')

  @staticmethod
  def put(location):
    feed = urllib.urlopen(URL+location)
    tree = ElementTree.parse(feed)
    as_of = tree.find('txt_forecast/date').text
    if as_of:
      forecast = Forecast(as_of)
      forecasts = tree.findall('txt_forecast/forecastday')
      for day in forecasts:
        pd = day.find('title').text
        fc = day.find('fcttext').text
        forecast.append(pd, fc)
      memcache.set(location, forecast, time=7200, namespace='forecast')
      return forecast
