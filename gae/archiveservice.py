import logging
import datetime
import pytz

from google.appengine.api import memcache
from google.appengine.ext import db

from protorpc import message_types
from protorpc import messages
from protorpc import remote

import simplejson
from dateutil import parser

import archive
import wxutils
import appconfig
import wu

class ArchiveMsg(messages.Message):
    date = messages.StringField(1, required=True)
    location = messages.StringField(2, required=True)
    password = messages.StringField(3, required=True)
    json = messages.StringField(4, required=True)
    station = messages.StringField(5, required=True)
# this will be interesting for posting archive_entries, not Condition objects
#class Conditions(messages.Message):
#  conditions = messages.MessageField(Condition, 1, repeated=True)
class LastDateMsg(messages.Message):
    location = messages.StringField(1, required=True)
    password = messages.StringField(2, required=True)
    
class LastDateResponseMsg(messages.Message):
    location = messages.StringField(1, required=True)
    date = messages.StringField(2, required=True)
    
class ArchiveService(remote.Service):
  @remote.method(ArchiveMsg, message_types.VoidMessage)
  def upload_archive(self, request):
#    logging.info(request.json)
    app_config = appconfig.load_settings()
    config = app_config['locations'][request.location]
    logging.info('archive record date: ' + str(parser.parse(request.date)))
    #archive.DbArchive(location=request.location, 
    #                  date=parser.parse(request.date), json=request.json, station=request.station).put()
    archive_record = json_to_dbarchive(request.location, request.date, request.station, request.json)
    # check for dups (location + date)?
    # populate all fields
    # store the record
    if archive_record:
      archive.ArchiveFactory.put(archive_record)
      # update last rain
      if archive_record.rainfall > 0: wxutils.update_last_rain(archive_record.location, archive_record.date)
      if config and 'wunderground' in config:
        wu_config = config['wunderground']
        if 'archives' in wu_config and wu_config['archives']:
          wu.post(archive=archive_record, station=wu_config['station'], password=wu_config['password'])

      wxutils.update_high_low_averages(archive=archive_record)

    return message_types.VoidMessage()

  @remote.method(LastDateMsg, LastDateResponseMsg)
  def get_last_archive_date(self, request):
    format = '%Y-%m-%d %H:%M:%S%z'
    utc = pytz.timezone("UTC")
    d = archive.ArchiveFactory.find_latest_datetime(request.location)
    if d:
        #d = d = utc.localize(a)
        resp = LastDateResponseMsg(location=request.location, date=datetime.datetime.strftime(d, format))
    else: # if there are no records, then use two days ago (all times UTC)
        d = datetime.datetime.now()
        d = utc.localize(d)
        difference = datetime.timedelta(days=-2)
        ret = datetime.datetime.strftime(d + difference, format)
        resp = LastDateResponseMsg(location=request.location, date=ret)
        
    return resp
    
def json_to_dbarchive(location, date, station, raw_json):
    # parse doc
    json = simplejson.loads(raw_json)
    if not json["valid"]:
        return None
    else:
        d = parser.parse(date)
        record = archive.DbArchive(key_name=location + ':' + str(d),
                                   location=location, date=d, station=station)
        record.outside_temp = json['outsideTemperature']['temperature'] / 10.0
        record.high_outside_temp = json['highOutsideTemperature']['temperature'] / 10.0
        record.low_outside_temp = json['lowOutsideTemperature']['temperature'] / 10.0
        record.rainfall = json['rainfall'] / 100.0
        record.high_rain_rate = json['highRainfallRate'] / 100.0
        record.pressure = json['pressure']['pressure'] / 1000.0
        record.pressure_trend = json['pressure']['trend']
        record.high_solar_radiation = json['highSolarRadiation']
        record.number_of_wind_samples = json['numberOfWindSamples']
        record.inside_temp = json['insideTemperature']['temperature'] / 10.0
        record.inside_humidity = json['insideHumidity']['humidity']
        record.outside_humidity = json['outsideHumidity']['humidity']
        record.avg_wind_speed = json['averageWindSpeed']
        record.high_wind_speed = json['highWindSpeed']
        record.high_wind_speed_direction = json["highWindSpeedDirection"]["degrees"]
        record.prevailing_wind_direction = json["windDirection"]["degrees"]
        return record

def last_hour_rain(location, as_of_date):
  return 0