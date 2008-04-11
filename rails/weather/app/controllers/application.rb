#require_dependency 'sparklines'

# Filters added to this controller will be run for all controllers in the application.
# Likewise, all the methods added will be available for all controllers.
class ApplicationController < ActionController::Base
  helper :sparklines
  session :off
  #ActiveRecord::Base.verification_timeout = 300 # get ActiveRecord to check w/ the db every 5 mins
end
