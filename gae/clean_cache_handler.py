#import os
#os.environ['DJANGO_SETTINGS_MODULE'] = 'settings'

#from google.appengine.dist import use_library
#use_library('django', '1.2')

import logging

from google.appengine.api import memcache
from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
from protorpc import service_handlers
import simplejson
import conditionsservice
import archiveservice
import condition
from weather.units import temp
from weather.units import wind

class CleanCacheHandler(webapp.RequestHandler):
    def get(self):
        #memcache.delete('01915', namespace='archive')
        #memcache.delete('WARING', namespace='archive')
        memcache.flush_all()

def main():
  # Register mapping with application.
  application = webapp.WSGIApplication([("/clean_cache", CleanCacheHandler)], debug=True)
  util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
