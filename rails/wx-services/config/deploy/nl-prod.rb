# steps
# 0 - mkdir /home/maarten/apps, configure apache to serve from /home/maarten/apps/wx-services/current
# 1 - from workstation containing cap, run 'cap nl-prod deploy:setup' - look for issues
# 2 - ssh into webserver and copy the directory weather/shared/config (and its contents) to wx-services/shared/config
# 3 - run 'cap nl-prod deploy'

set :application, "wx-services"

set :rails_env, "production"

set :user, "maarten"
ssh_options[:keys] = [File.join(ENV["HOME"], ".ssh", "id_rsa")] 
ssh_options[:port] = 22

# fixes host verification problem
default_run_options[:pty] = true

set :use_sudo, false

# Site5 blocks execution of scripts (i.e., dispatch.fcgi) that are
# group writable.  By default, Capistrano sets everything group
# writable.  This stops that.
set :group_writable, false

set :deploy_subdir, "rails/wx-services"
set :scm, :git
set :deploy_via, :remote_cache
set :repository_cache, "git_cache"
set :ssh_options, { :forward_agent => true }
set :repository, "git@github.com:mitct02/weather.git"
set :branch, "master"


# If you aren't deploying to /u/apps/#{application} on the target
# servers (which is the default), you can specify the actual location
# via the :deploy_to variable:
set :deploy_to, "~/apps/#{application}"

role :app, "weerinwelsum.synology.me"
role :web, "weerinwelsum.synology.me"
role :db,  "weerinwelsum.synology.me", :primary => true

role :app, "webserver"
role :web, "webserver"
role :db,  "webserver", :primary => true


task :symlink_config_yml, :roles => :app do
  run "ln -nsf #{shared_path}/config/database.yml
       #{release_path}/config/database.yml"
  run "ln -nsf #{shared_path}/config/config.yml
       #{release_path}/config/config.yml"
  run "ln -nsf #{shared_path}/config/service_providers.yml
       #{release_path}/config/service_providers.yml"
end

after 'deploy:update_code', 'symlink_config_yml'

namespace(:deploy) do
  desc "Shared phusion passenger restart"
  task :restart, :roles => :app do
    run "touch #{current_path}/tmp/restart.txt"
  end
end

desc "tail -f development log"
task :tail_dev_log, :roles => :app do 
  stream "tail -f #{shared_path}/log/development.log" 
end 

desc "tail -f production log"
task :tail_prod_log, :roles => :app do
  stream "tail -f #{shared_path}/log/production.log"
end
