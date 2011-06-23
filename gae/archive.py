import logging
import pytz

from google.appengine.ext import db
from google.appengine.api import memcache

class DbArchive(db.Model):
    location = db.StringProperty(required=True)
    date = db.DateTimeProperty(required=True)
    outside_temp=db.FloatProperty() # degrees
    high_outside_temp=db.FloatProperty() # degrees
    low_outside_temp=db.FloatProperty() # degrees
    pressure=db.FloatProperty() # inches of mercury
    pressure_trend=db.IntegerProperty() # lets store the integer and decode it later
    outside_humidity=db.IntegerProperty()
    rainfall=db.FloatProperty() # inches TODO - make this metric aware
    high_rain_rate=db.FloatProperty()
    avg_wind_speed=db.IntegerProperty() # mph
    high_wind_speed=db.IntegerProperty()
    high_wind_speed_direction=db.IntegerProperty()
    prevailing_wind_direction=db.IntegerProperty() # degrees f
    number_of_wind_samples=db.IntegerProperty()
    inside_temp=db.FloatProperty() # degrees f
    inside_humidity=db.IntegerProperty()
    high_solar_radiation=db.IntegerProperty() # watts / meter^2
    #json = db.TextProperty(required=True)
    station = db.StringProperty(required=True)
    updated_at = db.DateTimeProperty(auto_now=True)
    created_at = db.DateTimeProperty(auto_now_add=True)

class ArchiveFactory:
    @staticmethod
    def find_latest_datetime(location):
        a = memcache.get(location, namespace='archive')
        logging.info("retrieved: " + str(a) + " from cache")
        if not a:
            logging.info("no cache hit for last archive - querying data store")
            q = DbArchive.all()
            q.filter("location =", location)
            q.order("-date")
            result = q.get()
            if result:
              d = result.date
              utc = pytz.timezone("UTC")
              a = utc.localize(d)
              logging.info("last date is: " + str(a))
              memcache.set(key=location, value=a, namespace='archive')
        logging.info("returning: " + str(a))
        return a
            
    @staticmethod
    def delete_all(location):
        q = DbArchive.all()
        q.filter("location =", location)
        q.order("-date")
        result = q.get()
        return result
    
    @staticmethod
    def put(archive):
        # put a record in the memcache w/ location as key and date as value
        #logging.info(archive.location + str(archive.date))
        archive.put()
        memcache.set(key=archive.location, value=archive.date, namespace='archive')

            
class Archive:
    pass
