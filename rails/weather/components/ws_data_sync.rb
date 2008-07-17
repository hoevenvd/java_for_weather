#<xsd:complexType name="InputSampleStruct">
#<xsd:element name="uv" type="xsd:int"/>
#<xsd:element name="windspeed" type="xsd:int"/>
#<xsd:element name="pressure" type="xsd:double"/>
#<xsd:element name="rain_rate" type="xsd:double"/>
#<xsd:element name="sample_date" type="xsd:dateTime"/>
#<xsd:element name="bar_status" type="xsd:string"/>
#<xsd:element name="solar_radiation" type="xsd:int"/>
#<xsd:element name="ten_min_avg_wind" type="xsd:int"/>
#<xsd:element name="temp" type="xsd:double"/>
#<xsd:element name="humidity" type="xsd:int"/>
#<xsd:element name="wind_direction" type="xsd:int"/>
#<xsd:element name="daily_rain" type="xsd:double"/>

#<xsd:complexType name="SampleStruct">
#<xsd:element name="uv" type="xsd:int"/>
#<xsd:element name="pressure" type="xsd:double"/>
#<xsd:element name="windspeed" type="xsd:int"/>
#<xsd:element name="rain_rate" type="xsd:double"/>
#<xsd:element name="sample_date" type="xsd:dateTime"/>
#<xsd:element name="dewpoint" type="xsd:double"/>
#<xsd:element name="bar_status" type="xsd:string"/>
#<xsd:element name="solar_radiation" type="xsd:int"/>
#<xsd:element name="humidity" type="xsd:int"/>
#<xsd:element name="temp" type="xsd:double"/>
#<xsd:element name="ten_min_avg_wind" type="xsd:int"/>
#<xsd:element name="wind_direction" type="xsd:int"/>
#<xsd:element name="is_raining" type="xsd:boolean"/>
#<xsd:element name="apparent_temp" type="xsd:int"/>

require 'soap/wsdlDriver'

log = Logger.new(STDOUT)
log.level = Logger::INFO

src_url="http://servers:3000/weather/wsdl"
src_soap = SOAP::WSDLDriverFactory.new(src_url).create_rpc_driver
log.debug(src_soap)
dest_url="http://dev.henrymitchell.org/weather/wsdl"
dest_soap = SOAP::WSDLDriverFactory.new(dest_url).create_rpc_driver
log.debug(dest_soap)


while true do

  20.times do
    log.debug("getting conditions.")
    conditions = src_soap.GetCurrentConditions("01915")
    log.debug(conditions)
    log.debug("writing")
    dest_soap.PutCurrentConditions("wx", "01915", conditions)
    log.debug("done.")
    sleep 2
  end

  last_archive = dest_soap.GetLastArchive("01915")
  log.debug(last_archive.date)
  
  archive_structs = src_soap.GetArchiveSince("wx", "01915", last_archive.date)
  log.debug("returned #{archive_structs.length} entries")
  
  archive_structs.each do |s|
    dest_soap.PutArchiveEntry("wx", "01915", s)
    log.debug(s.date)
  end
end
