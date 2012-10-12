require 'time'
require 'parsedate'
require 'net/http'
require 'open-uri'
require 'rexml/document'
include REXML


# example: http://forecast.weather.gov//MapClick.php?textField1=42.56212&textField2=-70.84997&TextType=3

#mysql> describe wunder_forecast_periods;
#+--------------------+--------------+------+-----+---------+----------------+
#| Field              | Type         | Null | Key | Default | Extra          |
#+--------------------+--------------+------+-----+---------+----------------+
#| name               | varchar(255) | NO   |     | NULL    |                |
#| forecast           | text         | NO   |     | NULL    |                |
#| icon_url           | varchar(255) | YES  |     | NULL    |                |
#| wunder_forecast_id | int(11)      | YES  |     | NULL    |                |
#| created_at         | datetime     | YES  |     | NULL    |                |
#| updated_at         | datetime     | YES  |     | NULL    |                |
#+--------------------+--------------+------+-----+---------+----------------+
#7 rows in set (0.00 sec)

#mysql> describe forecast_periods;
#+------------------+--------------+------+-----+---------+----------------+
#| Field            | Type         | Null | Key | Default | Extra          |
#+------------------+--------------+------+-----+---------+----------------+
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
    forecast = WunderForecast.find_or_create_by_location(AppConfig.noaa_location)
    txt_forecasts = (@doc.elements['//txt_forecast'])
    simple_forecasts = (@doc.elements['//simpleforecast'])
    forecast.wunder_forecast_periods.destroy_all
    forecast.wunder_forecast_period_longs.destroy_all
    forecast.forecast_xml = @xml
    forecast.creation_time = Time.parse((@doc.elements['//date']).text).utc
    forecast.creation_time = forecast.creation_time - 1.day if forecast.creation_time > Time.now.utc
    forecast.last_retrieved = Time.now.utc
    forecast.save
    txt_forecasts.elements.each("forecastday") do | pd |
      forecast.wunder_forecast_periods << WunderForecastPeriod.new(
        :name => pd.elements['title'].text, 
        :text => pd.elements['fcttext'].text,
        :icon_location => pd.elements['icons'][1].elements['icon_url'].text)
    end


    simple_forecasts.elements.each("forecastday") do | pd |
      forecast.wunder_forecast_period_longs << WunderForecastPeriodLong.new(
        :date => Time.at(pd.elements['date'].elements['epoch'].text.to_i).to_datetime,
        :high => pd.elements['high'].elements['fahrenheit'].text.to_f,
        :high_m => pd.elements['high'].elements['celsius'].text.to_f,
        :low => pd.elements['low'].elements['fahrenheit'].text.to_f,
        :low_m => pd.elements['low'].elements['celsius'].text.to_f,
        :conditions => pd.elements['conditions'].text,
        :icon_location => pd.elements['icons'][1].elements['icon_url'].text)
    end


  end
end
