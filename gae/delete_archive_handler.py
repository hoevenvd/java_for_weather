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

class DeleteArchiveHandler(webapp.RequestHandler):
    def get(self):
        import time
        start = time.clock()
        self.response.headers['Content-Type'] = 'text/plain'
        q = db.GqlQuery("SELECT * from DbArchive order by date limit 500")
        self.response.out.write("deleting...")
        db.delete(q)

def main():
  # Register mapping with application.
  application = webapp.WSGIApplication([("/delete_archive", DeleteArchiveHandler)], debug=True)
  util.run_wsgi_app(application)

if __name__ == '__main__':
    main()
