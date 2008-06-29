require 'logger'
require 'date'
require 'memcache'
require 'net/http'
require 'rexml/document'
require 'noaa_forecast'
include REXML

class CacheWriter

  LOCATION_FORECAST = "01915-forecast"
  URL = 'www.weather.gov'
  PREFIX = "/data/current_obs/"
  PORT = 80
  POSTFIX = ".xml"
  STATION = ARGV[0] # Appconfig.noaa_location # "KBVY" # 
  CACHE_URL = AppConfig.memcache_url
  CACHE_DEBUG = false

  def CacheWriter.get_cache
    cache = MemCache::new  CACHE_URL,
      :debug => CACHE_DEBUG,
      :c_threshold => 100_000,
      :compression => false
  end

  def CacheWriter.write_entry(key, value)
    CacheWriter.get_cache[key] = value	  
  end
	
  def CacheWriter.read_entry(key)
    CacheWriter.get_cache[key]	  
  end

  def CacheWriter.get_forecast
    fc = Forecast.new
    fc.source = Forecast.http_source
    fc.periods
  end

  begin
    log = Logger.new(STDOUT)
    log.level = Logger::INFO
    h = Net::HTTP.new(URL, PORT)
    resp, data = h.get(PREFIX + STATION + POSTFIX, nil)
    log.debug(resp)
    log.debug(data)
    doc = Document.new(data)
    location = doc.elements[1].elements["station_id"].text
    as_of = Time.rfc822(doc.elements[1].elements["observation_time_rfc822"].text).utc
    record = NoaaConditions.find_by_as_of_and_location(as_of, location)
    #log.debug(record)
    if (record.nil?)
      conditions = NoaaConditions.new
      conditions.location = location
      conditions.as_of = as_of
      conditions.conditions = doc.elements[1].elements["weather"].text
      conditions.visibility = doc.elements[1].elements["visibility_mi"].text.to_i
      conditions.save!
      log.debug(conditions)
    end
    #write_entry(LOCATION_FORECAST, get_forecast)
  end

end
