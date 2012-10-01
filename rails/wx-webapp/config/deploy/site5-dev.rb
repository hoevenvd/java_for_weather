set :application, "wx-webapp"

set :deploy_dir, "/wx-webapp"

set :user, "henry"
ssh_options[:keys] = [File.join(ENV["HOME"], ".ssh", "id_rsa")] 
#ssh_options[:port] = 7822

# fixes host verification problem
default_run_options[:pty] = true

set :use_sudo, false

# Site5 blocks execution of scripts (i.e., dispatch.fcgi) that are
# group writable.  By default, Capistrano sets everything group
# writable.  This stops that.
set :group_writable, false

<<<<<<< HEAD
set :repository,  "svn+ssh://tomorg@tommitchell.net/home/tomorg/svn/weather/trunk/rails/#{application}"
=======
set :deploy_subdir, "rails/wx-webapp"
set :scm, :git
set :deploy_via, :remote_cache
set :repository_cache, "git_cache"
set :ssh_options, { :forward_agent => true }
set :repository, "git@github.com:mitct02/weather.git"
set :branch, "master"
>>>>>>> e7b2c0d5172885d29c1b164bb937ce7915c8cc7e

# If you aren't deploying to /u/apps/#{application} on the target
# servers (which is the default), you can specify the actual location
# via the :deploy_to variable:
set :deploy_to, "~/apps/#{application}"

<<<<<<< HEAD
# If you aren't using Subversion to manage your source code, specify
# your SCM below:
set :scm, :subversion
# set :scm_username, "#{user}"
#set :scm_password, proc{Capistrano::CLI.password_prompt('SVN pass:')} 

set :deploy_via, :export 

=======
>>>>>>> e7b2c0d5172885d29c1b164bb937ce7915c8cc7e
role :app, "henrymitchell.org"
role :web, "henrymitchell.org"
role :db,  "henrymitchell.org", :primary => true

#role :app, "dev"
#role :web, "dev"
#role :db,  "dev", :primary => true

#todo: copy database.yml
# cp ~/apps/weather/config/database.yml ~/cap/weather/shared/system/
# make system the permanent place and do a ln on deployment
#desc "Symlink config.yml and database.yml from shared to  current directory
#      since it should not be kept in version control"
task :symlink_config_yml, :roles => :app do
  run "ln -nsf #{shared_path}/config/database.yml
       #{release_path}/config/database.yml"
  run "ln -nsf #{shared_path}/config/config.yml
       #{release_path}/config/config.yml"
  run "ln -nsf #{shared_path}/config/service_providers.yml
       #{release_path}/config/service_providers.yml"
end

#desc "Symlink root directory under public_html"
task :symlink_public, :roles => :app do
  run "ln -nsf #{current_path}/public
       /home/#{user}/public_html/#{deploy_dir}"

# fixup .htaccess for passenger
# example can be found in config/dot_htaccess_passenger
  run "cp #{shared_path}/config/dot_htaccess
       #{release_path}/public/.htaccess"
end

after 'deploy:update_code', 'symlink_config_yml', 'symlink_public'

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

#desc "reset awstats config"
#task :reset_awstata, :roles => :app do
#    send(run_method, "cp ~/tmp/awstats/awstats.tom.org.conf.good ~/tmp/awstats/awstats.tom.org.conf")
#end
