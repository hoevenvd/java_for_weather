require 'logger'
require 'date'
require 'memcache'
require 'net/http'
require 'rexml/document'
include REXML

class CacheWriter

  LOCATION = "01915-obs"
  URL = 'www.weather.gov'
  PREFIX = "/data/current_obs/"
  PORT = 80
  POSTFIX = ".xml"
  STATION = "KBVY"
  CACHE_URL = '192.168.1.3:11211'
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

  begin
    log = Logger.new(STDOUT)
    log.level = Logger::DEBUG
    h = Net::HTTP.new(URL, PORT)
    resp, data = h.get(PREFIX + STATION + POSTFIX, nil)
    doc = Document.new(data)
    value = { 'weather' => doc.elements[1].elements["weather"].text }
    date_str = doc.elements[1].elements["observation_time"].text
    value['observation_time'] = date_str
    value['visibility_mi'] = doc.elements[1].elements["visibility_mi"].text.to_f
    # log.debug(value)
    write_entry(LOCATION, value)
  rescue Exception
    log.error($!)
  end
end
