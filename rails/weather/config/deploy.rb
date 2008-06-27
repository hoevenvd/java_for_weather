set :application, "weather"
set :repository,  "http://www.tom.org:8081/svn-repos/weather/trunk/rails/weather"

# If you aren't deploying to /u/apps/#{application} on the target
# servers (which is the default), you can specify the actual location
# via the :deploy_to variable:
# set :deploy_to, "/var/www/#{application}"

# If you aren't using Subversion to manage your source code, specify
# your SCM below:
# set :scm, :subversion

role :app, "dev"
role :web, "dev"
role :db,  "dev", :primary => true