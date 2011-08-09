# config/initializers/load_config.rb

require 'ostruct'
require 'yaml'

ConfigFile = "#{RAILS_ROOT}/config/config.yml"
if File.exist?(ConfigFile)
  config = OpenStruct.new(YAML.load_file(ConfigFile))
else
  raise "config file not found: config.yml"
end
env_config = config.send(RAILS_ENV)
config.common.update(env_config) unless env_config.nil?
AppConfig = OpenStruct.new(config.common)

# TODO - DRY

# stuff related to wunderground, google apps and cwop

SVC_FILE = "#{RAILS_ROOT}/config/service_providers.yml"
if File.exist?(SVC_FILE)
  SVC_CONFIG = YAML.load_file(SVC_FILE)["#{RAILS_ENV}"]
else
  SVC_CONFIG = Hash.new
end

# application.rb
#def authenticate
#  if APP_CONFIG['perform_authentication']
#    authenticate_or_request_with_http_basic do |username, password|
#      username == APP_CONFIG['username'] && password == APP_CONFIG['password']
#    end
#  end
#end

