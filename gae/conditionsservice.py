import logging
from google.appengine.api import memcache
from google.appengine.ext import db
import simplejson

from dateutil.parser import *
from dateutil.tz import *
from datetime import *

import condition
import wu
import appconfig
import wxutils

from protorpc import message_types
from protorpc import messages
from protorpc import remote

class ConditionMsg(messages.Message):
  location = messages.StringField(1, required=True)
  password = messages.StringField(2, required=True)
  json = messages.StringField(3, required=True)
  station = messages.StringField(4, required=True)

class ConditionsService(remote.Service):
  @remote.method(ConditionMsg, message_types.VoidMessage)
  def post_condition(self, request):
    app_config = appconfig.load_settings()
    config = app_config['locations'][request.location]
    c = condition.Condition(location=request.location, station=request.station, json=request.json)
    if c:
      memcache.set(key=c.location, value=c, namespace='conditions')
      # if it's raining, update the last rain datetime in the cache
      if c.is_raining: wxutils.update_last_rain(c.location, c.date)
      wxutils.update_high_low_averages(condition=c)
      #self.store_condition(request)
      if config and 'wunderground' in config:
        wu_config = config['wunderground']
        if 'conditions' in wu_config and wu_config['conditions']:
          wu.post(condition=c, station=wu_config['station'], password=wu_config['password'])
    return message_types.VoidMessage()

  def store_condition(self, request):
    if memcache.decr(key=request.location, namespace='conditions-count', initial_value=10) < 1:
      # TODO - do some error handling -
      # CapabilityDisabledError: Datastore writes are temporarily unavailable.
      # Please see http://code.google.com/status/appengine for more information.
      #
      condition.DbCondition(key_name=request.location, json=request.json, station=request.station).put()
      memcache.delete(key=request.location, namespace='conditions-count')
      logging.info("Stored Condition:" + request.location)
