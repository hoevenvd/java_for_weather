require 'logger'
require 'date'
require 'net/http'
require 'open-uri'
require 'rexml/document'
include REXML

# http://www.weather.gov/xml/current_obs/KBVY.xml

class NOAAConditionsWriter
  log = Logger.new(STDOUT)
  log.level = Logger::INFO

  LOCATION_FORECAST = "01915-forecast"
  HOST = 'www.weather.gov'
  PREFIX = "/xml/current_obs/"
  PORT = 80
  POSTFIX = ".xml"
#  STATION = ARGV[0] # Appconfig.noaa_location # "KBVY" #
  STATION = ARGV[0] ||= AppConfig.noaa_location
  URL = "http://" + HOST + PREFIX + STATION + POSTFIX
  f = open(URL) # open-uri - treat the URL as an input stream
  data = f.read
  log.debug(data)
  doc = Document.new(data)
  location = doc.elements[1].elements["station_id"].text
  log.debug("location: " + location)
  as_of = Time.rfc822(doc.elements[1].elements["observation_time_rfc822"].text).utc
  log.debug("as_of: " + as_of.to_s)

  visibility = doc.elements[1].elements["visibility_mi"].text.to_i
  log.debug("visibility: " + visibility.to_s)

  conditions = doc.elements[1].elements["weather"].text
  log.debug("conditions: " + conditions)

  record = NoaaConditions.find_or_create_by_location(location)
  log.debug(record)
  record[:conditions_xml] = data
  record[:location] = location
  record[:as_of] = as_of
  record[:conditions] = conditions
  record[:visibility] = visibility
  record[:updated_at] = Time.now
  log.debug(record)
  record.save!
end
