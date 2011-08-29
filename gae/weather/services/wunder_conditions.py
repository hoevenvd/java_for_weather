import datetime
import pytz
from google.appengine.api import memcache
from xml.etree import ElementTree
import urllib

#http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=kbvy

URL='http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query='

class WunderConditions:
  def __init__(self, as_of, weather, visibility):
    self.as_of = as_of
    self.weather = weather
    self.visibility = visibility
    self.retrieved = datetime.datetime.utcnow().replace(tzinfo=pytz.utc)

class ConditionsFactory:
  @staticmethod
  def get(location):
    return memcache.get(location, namespace='conditions')

  @staticmethod
  def put(location):
    feed = urllib.urlopen(URL+location)
    tree = ElementTree.parse(feed)
    as_of = tree.find('/observation_time').text
    weather = tree.find('/weather').text
    visibility = tree.find('/visibility_mi').text
    if as_of:
      conditions = WunderConditions(as_of, weather, visibility)
      memcache.set(location, conditions, time=7200, namespace='conditions')
      return conditions
