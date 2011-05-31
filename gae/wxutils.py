from google.appengine.ext import db
from google.appengine.api import memcache
import pytz
import datetime

def update_last_rain(loc, d):
  # set the last rain key
  if d: memcache.set(key='lastrain', value=d.astimezone(pytz.utc), namespace=loc)

def last_rain(loc):
  d = memcache.get(key='lastrain', namespace=loc)
  if not d:
    q = db.GqlQuery('SELECT * FROM DbArchive WHERE location = :1 and rainfall > 0.0 order by rainfall, date desc', loc)
    result = q.get()
    if result: d = result.date.replace(tzinfo=pytz.utc)
    update_last_rain(loc, d)
    # get it from the database
  if d: return d

def update_high_low_averages(archive=None, condition=None):
  pass
  # check highs and lows across all stored periods (presumably stored in memcache)
  # update current averages - just from archives, not from conditions
  # look in memcache = need to store both sums and counts to get averages
  # if nothing is in memcache, initialize from the data store
