require 'time'
require 'parsedate'
require 'net/http'
require 'open-uri'
require 'rexml/document'
include REXML


#mysql> describe noaa_forecasts;
#+----------------+-------------+------+-----+---------+----------------+
#| Field          | Type        | Null | Key | Default | Extra          |
#+----------------+-------------+------+-----+---------+----------------+
#| id             | int(11)     | NO   | PRI | NULL    | auto_increment |
#| forecast_xml   | text        | YES  |     | NULL    |                |
#| created_at     | datetime    | YES  | MUL | NULL    |                |
#| location       | varchar(30) | NO   | MUL |         |                |
#| updated_at     | datetime    | YES  |     | NULL    |                |
#| creation_time  | datetime    | YES  |     | NULL    |                |
#| last_retrieved | datetime    | YES  |     | NULL    |                |
#+----------------+-------------+------+-----+---------+----------------+
#7 rows in set (0.00 sec)

#mysql> describe forecast_periods;
#+------------------+--------------+------+-----+---------+----------------+
#| Field            | Type         | Null | Key | Default | Extra          |
#+------------------+--------------+------+-----+---------+----------------+
#| id               | int(11)      | NO   | PRI | NULL    | auto_increment |
#| noaa_forecast_id | bigint(20)   | NO   | MUL | 0       |                |
#| name             | varchar(20)  | NO   |     |         |                |
#| text             | text         | YES  |     | NULL    |                |
#| created_at       | datetime     | YES  |     | NULL    |                |
#| icon_location    | varchar(255) | NO   |     |         |                |
#| updated_at       | datetime     | YES  |     | NULL    |                |
#| temp             | bigint(20)   | YES  |     | NULL    |                |
#| weather          | text         | YES  |     | NULL    |                |
#| pop              | bigint(20)   | YES  |     | NULL    |                |
#+------------------+--------------+------+-----+---------+----------------+
#10 rows in set (0.01 sec)


# example: http://forecast.weather.gov//MapClick.php?textField1=42.56212&textField2=-70.84997&TextType=3

class NOAAForecastUtils
  log = Logger.new(STDOUT)
  log.level = Logger::INFO

  LOCATION = AppConfig.noaa_location
  HOST = "forecast.weather.gov"
#  PORT = 80
  LONG = AppConfig.longitude  # -70.8405
  LAT = AppConfig.latitude   # 42.5577
  QUERY = "/MapClick.php?textField1=#{LAT}&textField2=#{LONG}&TextType=3"
  URL = "http://" + HOST + QUERY

  begin
    f = open(URL) # open-uri - treat the URL as an input stream
    @xml = f.read
    log.debug(@xml)
    @doc = Document.new(@xml)
    log.debug("doc = " + @doc.to_s)
    forecast = NoaaForecast.find_or_create_by_location(LOCATION)
    forecast.forecast_periods.destroy_all
    icon_base = (@doc.elements['//icon-location'].text + '/').to_s
    forecast.forecast_xml = @xml
    log.debug("creation time raw: #{@doc.elements['//creationTime']}")
    forecast.creation_time = Time.local(*ParseDate.parsedate(@doc.elements['//creationTime'].text)).utc
    forecast.last_retrieved = Time.now.utc
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
  end
end
