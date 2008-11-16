require 'time'
require 'parsedate'
require 'net/http'
require 'rexml/document'
include REXML

# example: http://forecast.weather.gov//MapClick.php?textField1=42.56212&textField2=-70.84997&TextType=3

class NOAAForecastUtils
  log = Logger.new(STDOUT)
  log.level = Logger::INFO

  LOCATION = AppConfig.noaa_location
  HOST = "forecast.weather.gov"
  PORT = 80
  LONG = AppConfig.longitude  # -70.8405
  LAT = AppConfig.latitude   # 42.5577
  QUERY = "/MapClick.php?textField1=#{LAT}&textField2=#{LONG}&TextType=3"
  
  begin
    Net::HTTP.start(HOST, PORT) do | http |
      response = http.get(QUERY)
      log.debug(response)
      if response.class == Net::HTTPOK
        log.debug("response OK")
        @xml = response.body
        @doc = Document.new(@xml)
        log.debug("doc = " + @doc.to_s)
        forecast = NoaaForecast.find_or_create_by_location(LOCATION)
        forecast.forecast_periods.destroy_all
        icon_base = (@doc.elements['//icon-location'].text + '/').to_s
        forecast.forecast_xml = @xml
        log.debug("creation time raw: #{@doc.elements['//creationTime']}")
        forecast.creation_time = Time.local(*ParseDate.parsedate(@doc.elements['//creationTime'].text)).utc
        forecast.save
        @doc.elements.each("//period") do | pd |
          forecast.forecast_periods << ForecastPeriod.new(
            :name => pd.elements['valid'].text, 
            :text => pd.elements['text'].text,
            :icon_location => (icon_base + pd.elements['image'].text),
            :temp => pd.elements['temp'].text.to_i,
            :weather => pd.elements['weather'].text,
            :pop => (pd.elements['pop'] == nil ? 0 : pd.elements['pop'].text.to_i))
        end
      else
        log.info(response.to_s)
      end
    end
  end
end
