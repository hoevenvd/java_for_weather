import logging
from google.appengine.ext import db
from google.appengine.api import memcache
import simplejson
from dateutil import parser
from wx import convert
from wx import temps

class DbCondition(db.Model): # location is the key for this in the data store
  as_of = db.DateTimeProperty(auto_now=True)
  json = db.TextProperty(required=True)
  station = db.StringProperty(required=True)
  updated_at = db.DateTimeProperty(auto_now=True)
  created_at = db.DateTimeProperty(auto_now_add=True)
  

class ConditionFactory:
  @staticmethod
  def find(location, metric=False):
    return memcache.get(location, namespace='conditions')

class Condition:
    def __init__(self, location, station, json, metric=False):
      self.location = location
      self.station = station
      json = simplejson.loads(json)
      self.date = parser.parse(json['date'])
      self.pressure = round(json['pressure']['pressure'] / 1000.0, 2)
      if metric: self.pressure = wx.Conversion.inches_of_hg_to_mb(self.pressure)
      self.inside_temp = round(json['insideTemp']['temperature'] / 10.0, 1)
      if metric: self.inside_temp = wx.Conversion.to_c(self.inside_temp)
      self.inside_humidity = json['insideHumidity']['humidity']
      self.outside_temp = round(json['outsideTemperature']['temperature'] / 10.0, 1)
      if metric: self.outside_temp = wx.Conversion.to_c(self.outside_temp)
      self.wind_speed = json['wind']['speed']
      self.ten_min_wind_speed = json['wind']['tenMinAverage']
      self.wind_direction = json['wind']['direction']['degrees']
      self.outside_humidity = json['outsideHumidity']['humidity']
      self.rain_rate = round(json['rainRate'], 1)
      self.solar_radiation = json['solarRadiation']
      self.storm_rain = round(json['stormRain'], 2)
      self.day_rain = round(json['dayRain'], 2)
      self.month_rain = round(json['monthRain'], 2)
      self.year_rain = round(json['yearRain'], 2)
      self.is_raining = json['isRaining']
      self.sunrise = parser.parse(json['sunrise'])
      self.sunset = parser.parse(json['sunset'])
      self.dewpoint = temps.calc_dewpoint(self.outside_temp, self.outside_humidity)
      self.apparent_temp = temps.calc_apparent_temp(self.outside_temp, self.outside_humidity, self.wind_speed)
      self.pressure_trend = self.pressure_trend(json['pressure']['trend'])
      
    # TODO I18N
    def pressure_trend(self, raw_trend):
      if raw_trend == 0:
        return 'Steady'
      elif raw_trend == 196:
        return 'Falling Rapidly'
      elif raw_trend == 236:
        return 'Falling Slowly'
      elif raw_trend == 20:
        return 'Rising Slowly'
      elif raw_trend == 60:
        return 'Rising Rapidly'
      else:
        return 'Invalid'
