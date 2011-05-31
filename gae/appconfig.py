import yaml
from google.appengine.api import memcache

# TODO define an action to delete it from the memcache

def load_settings(loc=None):
  settingsfile='appconfig.yaml'
  settings = memcache.get('app',namespace='config')
  if not settings:
    settings = yaml.load(open(settingsfile,'r').read())
    memcache.set('app',settings,namespace='config')
  if not loc:
    return settings
  else:
    if loc in settings['locations']:
      return settings['locations'][loc]
    else:
      return None

def reload_settings():
  memcache.delete('app',namespace='config')
  return load_settings()
