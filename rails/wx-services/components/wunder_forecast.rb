require 'time'
require 'parsedate'
require 'net/http'
require 'open-uri'
require 'rexml/document'
include REXML

# example: http://forecast.weather.gov//MapClick.php?textField1=42.56212&textField2=-70.84997&TextType=3

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

class WunderForecastUtils
  log = Logger.new(STDOUT)
  log.level = Logger::INFO

  url='http://api.wunderground.com/auto/wui/geo/ForecastXML/index.xml?query=' + AppConfig.noaa_location

  begin
    f = open(url) # open-uri - treat the URL as an input stream
    @xml = f.read
    log.debug(@xml)
    @doc = Document.new(@xml)
    log.debug("doc = " + @doc.to_s)
    forecast = WunderForecast.find_or_create_by_location(AppConfig.location)
    txt_forecasts = (@doc.elements['//txt_forecast'])
    forecast.wunder_forecast_periods.destroy_all
    forecast.forecast_xml = @xml
    forecast.as_of = (@doc.elements['//date']).text
    forecast.save
    txt_forecasts.elements.each("forecastday") do | pd |
      forecast.wunder_forecast_periods << WunderForecastPeriod.new(
        :name => pd.elements['title'].text, 
        :forecast => pd.elements['fcttext'].text,
        :icon_url => pd.elements['icons'][1].elements['icon_url'].text)
    end
  end
end
