import datetime
import pytz

# TODO think of a better name for this method
# returns a datetime object representing midnight of the date passed
# in, defaulting to "midnight last night"
def at_midnight(d=datetime.datetime.utcnow()):
  # TODO - make this utc
  return d.replace(hour=0, minute=0, second=0, microsecond=0, tzinfo=pytz.utc)

def this_rolling_hour():
  end = datetime.datetime.utcnow().replace(tzinfo=pytz.utc)
  start = end - datetime.timedelta(hours=1)
  return Period(start, end)

def within_the_last_hour(t):
  return t.astimezone(pytz.utc) >= (datetime.datetime.utcnow().replace(tzinfo=pytz.utc) - datetime.timedelta(hours=1))

def one_hour_ago():
  return datetime.datetime.utcnow().replace(tzinfo=pytz.utc) - datetime.timedelta(hours=1)

def one_day_ago():
  return datetime.datetime.utcnow().replace(tzinfo=pytz.utc) - datetime.timedelta(days=1)

def today():
  start = at_midnight()
  end = start + datetime.timedelta(days=1)
  return Period(start=start, end=end)

def yesterday():
  end = at_midnight()
  start = end - datetime.timedelta(days=1)
  return Period(start, end)

def this_week():
  end = datetime.datetime.utcnow().replace(tzinfo=pytz.utc)
  start = at_midnight(end - datetime.timedelta(days=7-end.weekday()))
  return Period(start, end)

def last_week():
  end = this_week().start
  start = at_midnight(end - datetime.timedelta(days=7))
  return Period(start, end)

def this_rolling_week():
  end = d=datetime.datetime.utcnow()
  start = end - datetime.timedelta(weeks=1)
  return Period(start, end)
  
def last_rolling_week():
  end = d=datetime.datetime.utcnow().replace(tzinfo=pytz.utc) - datetime.timedelta(weeks=1)
  start = end - datetime.timedelta(weeks=1)
  return Period(start, end)
  
def this_rolling_month():
  end = d=datetime.datetime.utcnow().replace(tzinfo=pytz.utc)
  start = end - datetime.timedelta(weeks=4)
  return Period(start, end)
  
def last_rolling_month():
  end = d=datetime.datetime.utcnow().replace(tzinfo=pytz.utc) - datetime.timedelta(weeks=4)
  start = end - datetime.timedelta(weeks=4)
  return Period(start, end)

class Period:
  def __init__(self, start, end):
    self.start = start
    self.end = end
