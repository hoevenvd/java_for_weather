LOG = Logger.new(STDOUT) 
LOG.level = Logger::DEBUG

CURRENT = "production"
NEW = "a2prod"

def close_current_connection
  ActiveRecord::Base.connection.disconnect!
end

def sync_current
  ActiveRecord::Base.establish_connection(CURRENT)
  curr = CurrentCondition.find(:first)
  close_current_connection
  ActiveRecord::Base.establish_connection(NEW)
  old = CurrentCondition.find(:first)
  old.update_attributes!(curr.attributes)
  LOG.debug(old.to_yaml)
  close_current_connection
end

def sync_archive
  ActiveRecord::Base.establish_connection(NEW)
  last = ArchiveRecord.find(:first, :order => "date desc")
  LOG.debug("last record from #{last.date}")
  close_current_connection
  ActiveRecord::Base.establish_connection(CURRENT)
  later = ArchiveRecord.find_all_by_location("01915", :conditions => "date > \"#{last.date.to_s(:db)}\"")
  LOG.debug("found #{later.size} records to add")
  close_current_connection
  ActiveRecord::Base.establish_connection(NEW)
  later.each do |r|
    LOG.debug(r.to_yaml)
    a = ArchiveRecord.new(r.attributes)
    a.save!
  end
  close_current_connection
end

while true do
  sync_archive
  20.times do
    sync_current
    sleep 2
  end
end

#require 'soap/wsdlDriver'
#require 'rexml/document'
#include REXML

#def make_struct(src_struct)
#  InputSampleStruct.new( 
#    :sample_date => Time.now, 
#    :temp => src_struct.temp, 
#    :windspeed => src_struct.windspeed, 
#    :humidity => src_struct.humidity, 
#    :pressure => src_struct.pressure,
#    :bar_status => src_struct.bar_status,
#    :rain_rate => src_struct.rain_rate,
#    :ten_min_avg_wind => src_struct.ten_min_avg_wind,
#    :wind_direction => src_struct.wind_direction)
#end

#src_url="http://www.tom.org/weather/wsdl"
#src_soap = SOAP::WSDLDriverFactory.new(src_url).create_rpc_driver

#dest_url="http://www.tommitchell.net/weather/wsdl"
#dest_soap = SOAP::WSDLDriverFactory.new(dest_url).create_rpc_driver
#src_conditions = dest_soap.GetCurrentConditions("01915")
#puts src_conditions

#dest_soap.PutCurrentConditions("wx", "01915", src_conditions)

