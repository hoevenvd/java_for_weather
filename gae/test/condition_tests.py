
#2. Write your test case scripts in Python modules, each containing unittest.TestCase subclasses, in the 'test' directory and launch dev_appserver.py

#3. Browse to the following URL (change the port if necessary):

#http://localhost:8080/test

#Your test results will be displayed in the browser:

#If you wish to run a single module, execute it like this:

#http://localhost:8080/test?name=my_test_module

#You can also specify a single test class, like name=my_test_module.MyTestCase or even a single test method, like name=my_test_module.MyTestCase.test_stuff 
import unittest
import logging
from google.appengine.ext import testbed

from google.appengine.api import memcache
from google.appengine.ext import db
from dateutil import parser

import condition


#class DbCondition(db.Model): # location is the key for this in the data store
#  as_of = db.DateTimeProperty(auto_now=True)
#  json = db.TextProperty(required=True)
#  station = db.StringProperty(required=True)
#  updated_at = db.DateTimeProperty(auto_now=True)
#  created_at = db.DateTimeProperty(auto_now_add=True)
  

class ModelTest(unittest.TestCase):

  inbound_json = '{"valid":true,\
 "date":"2011-04-27T06:28:23-0400",\
 "pressure":{"pressure":29938,"trend":236},\
 "insideTemp":{"temperature":651},\
 "insideHumidity":{"humidity":43},\
 "outsideTemperature":{"temperature":466},\
 "wind":{"speed":5,"tenMinAverage":1,"direction":{"degrees":157}},\
 "outsideHumidity":{"humidity":97},\
 "rainRate":0.3,\
 "uv":-9999,\
 "solarRadiation":9,\
 "stormRain":0.9,\
 "dayRain":0.01,\
 "monthRain":4.68,\
 "yearRain":11.4,\
 "dayET":0,\
 "monthET":0,\
 "yearET":0,\
 "transmitterBatteryStatus":0,\
 "consoleBatteryVoltage":0,\
 "forecastIcon":0,\
 "forecastRuleNumber":0,\
 "isRaining":false,\
 "sunrise":"2011-04-27T04:45:00-0400",\
 "sunset":"2011-04-27T18:40:00-0400",\
 "crc":8608,\
 "unsignedData":[{"b":76},{"b":79},{"b":79},{"b":-20},{"b":0},{"b":19},{"b":5},{"b":-14},{"b":116},{"b":-117},{"b":2},{"b":43},{"b":-46},{"b":1},{"b":0},{"b":1},{"b":-99},{"b":0},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":97},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":0},{"b":0},{"b":-1},{"b":9},{"b":0},{"b":0},{"b":0},{"b":-1},{"b":-1},{"b":1},{"b":0},{"b":-44},{"b":1},{"b":116},{"b":4},{"b":0},{"b":0},{"b":-33},{"b":0},{"b":126},{"b":2},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":-1},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":0},{"b":32},{"b":3},{"b":6},{"b":45},{"b":-67},{"b":1},{"b":48},{"b":7},{"b":10},{"b":13},{"b":33},{"b":-96}]}'
 
  def clear_memcache(self):
    memcache.flush_all()
      
  def clear_conditions(self):
    q = db.GqlQuery("SELECT * from DbCondition")
    db.delete(q)
      
  def setUp(self):
    logging.info('In setUp()')
    # Populate test entities.
# First, create an instance of the Testbed class.
    self.testbed = testbed.Testbed()
    # Then activate the testbed, which prepares the service stubs for use.
    self.testbed.activate()
    # Next, declare which service stubs you want to use.
    self.testbed.init_datastore_v3_stub()
    self.testbed.init_memcache_stub()

  def tearDown(self):
    # There is no need to delete test entities.
    #self.testbed.deactivate()
    pass

  def test_00_empty_cache_and_datastore(self):
    cond = condition.ConditionFactory.find(location="01915")
    self.assertFalse(cond)
    
#  def test_01_empty_cache_but_stored_record(self):
#    cond = condition.DbCondition(key_name="01915", json=self.inbound_json, station='vp2',).put()
#    self.clear_memcache()
#    cond = condition.ConditionFactory.find(location="01915")
#    self.assertTrue(cond)
#    self.assertEqual(cond, self.inbound_json)
    
  def test_02_new_condition_english_vp2(self):
    logging.info("creating new condition object")
    cond = condition.Condition(location = '01915', station = 'vp2', json = self.inbound_json)
    self.assertTrue(cond)
    self.assertEqual('01915', cond.location)
    local_date = parser.parse("2011-04-27T06:28:23-0400")
    logging.info(str(local_date))
    self.assertEqual(local_date, cond.date)
    self.assertEqual(29.94, cond.pressure)
    self.assertEqual(65.1, cond.inside_temp)
    self.assertEqual(43, cond.inside_humidity)
    self.assertEqual(46.6, cond.outside_temp)
    self.assertEqual(5, cond.wind_speed)
    self.assertEqual(157, cond.wind_direction)
    self.assertEqual(1, cond.ten_min_wind_speed)
    self.assertEqual(97, cond.outside_humidity)
    self.assertEqual(0.3, cond.rain_rate)
    self.assertEqual(9, cond.solar_radiation)
    self.assertEqual(0.9, cond.storm_rain)
    self.assertEqual(0.01, cond.day_rain)
    self.assertEqual(4.68, cond.month_rain)
    self.assertEqual(11.40, cond.year_rain)
    self.assertFalse(cond.is_raining)
    self.assertEqual(parser.parse("2011-04-27T04:45:00-0400"), cond.sunrise)
    self.assertEqual(parser.parse("2011-04-27T18:40:00-0400"), cond.sunset)
    self.assertEqual(45, cond.dewpoint)
    self.assertEqual('vp2', cond.station)

      
  def test_98_new_entity(self):
    cond = condition.DbCondition(key_name="01915", json=self.inbound_json, station='vp2',)
    self.assertEqual('vp2', cond.station)

  def test_99_saved_enitity(self):
    cond = condition.DbCondition(key_name="01915", json=self.inbound_json, station='vp2',)
    key = cond.put()
    self.assertEqual('vp2', db.get(key).station)
