import datetime
import logging

from weather.services import wunderground
import pytz
from wx import temps
import condition
import archiveservice
import archive

def post(condition=None, archive=None, station=None, password=None):
  if not station or not password:
    logging.error("attempt to post to wunderground without station or password")
    return
  # get last hour's rain from the archives
  if condition:
    #rainin = archiveservice.last_hour_rain(condition.location, c.date.astimezone(pytz.utc))
    w = wunderground.Wunderground(station, password, 2.5)
    w.set(dateutc=datetime.datetime.utcnow().replace(tzinfo=pytz.utc).strftime("%Y-%m-%d %H:%M:%S"),
          pressure=condition.pressure,rainday=condition.day_rain,dewpoint=condition.dewpoint,
          humidity=condition.outside_humidity,tempf=condition.outside_temp,winddir=condition.wind_direction,
          rainin=condition.rain_rate,windspeed=condition.wind_speed,solarradiation=condition.solar_radiation)
    w.publish()
  if archive:
    rainin = archiveservice.last_hour_rain(archive.location, archive.date.astimezone(pytz.utc))
    w = wunderground.Wunderground(station, password)
    dewpoint = temps.calc_dewpoint(archive.outside_temp, archive.outside_humidity)
    w.set(dateutc=archive.date.astimezone(pytz.utc).strftime("%Y-%m-%d %H:%M:%S"),
          pressure=archive.pressure,dewpoint=dewpoint,humidity=archive.outside_humidity,
          tempf=archive.outside_temp,winddir=archive.prevailing_wind_direction,
          rainin=archive.high_rain_rate,windspeed=archive.avg_wind_speed,windgust=archive.high_wind_speed,
          windgustdir=archive.high_wind_speed_direction,solarradiation=archive.high_solar_radiation)
    w.publish()
