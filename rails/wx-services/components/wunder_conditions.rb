require 'logger'
require 'date'
require 'net/http'
require 'open-uri'
require 'rexml/document'
include REXML

# http://www.weather.gov/xml/current_obs/KBVY.xml

#mysql> describe noaa_conditions;
#+----------------+-------------+------+-----+---------+----------------+
#| Field          | Type        | Null | Key | Default | Extra          |
#+----------------+-------------+------+-----+---------+----------------+
#| id             | int(11)     | NO   | PRI | NULL    | auto_increment |
#| created_at     | datetime    | YES  | MUL | NULL    |                |
#| location       | varchar(30) | NO   | MUL |         |                |
#| updated_at     | datetime    | YES  |     | NULL    |                |
#| conditions     | text        | NO   |     | NULL    |                |
#| as_of          | datetime    | YES  | MUL | NULL    |                |
#| visibility     | bigint(20)  | YES  |     | NULL    |                |
#| conditions_xml | text        | YES  |     | NULL    |                |
#+----------------+-------------+------+-----+---------+----------------+
#8 rows in set (0.01 sec)

class WunderConditionsWriter
  log = Logger.new(STDOUT)
  log.level = Logger::INFO

  URL='http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=' + AppConfig.noaa_location
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

  record = WunderConditions.find_or_create_by_location(location)
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
