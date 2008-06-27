require 'time'
require 'net/http'
require 'rexml/document'
include REXML

# example: http://forecast.weather.gov//MapClick.php?textField1=42.56212&textField2=-70.84997&TextType=3

class Forecast
  HOST = "forecast.weather.gov"
  PORT = 80
  LONG = -70.8405
  LAT = 42.5577
  QUERY = "/MapClick.php?textField1=#{LAT}&textField2=#{LONG}&TextType=3"
  attr_reader :doc

  def source= src
    @doc = Document.new(src)
  end

  def self.local_source(name)
    xml = File.new(name)
  end

  def self.http_source
    Net::HTTP.start(HOST, PORT) do | http |
      response = http.get(QUERY)
      result = response.class == Net::HTTPOK
      @doc = response.body
    end
    @doc
  end

  def periods
    periods = Array.new
    doc.elements.each("//period") do | pd |
      periods << ForecastPeriod.new(pd.elements['valid'].text, 
                                    pd.elements['text'].text,
                                    (icon_location + '/' + pd.elements['image'].text),
                                    pd.elements['temp'].text.to_i,
                                    pd.elements['weather'].text,
                                    (pd.elements['pop'] == nil ? 0 : pd.elements['pop'].text.to_i))
    end
    periods
  end

  def icon_location
    (@doc.elements['//icon-location'].text + '/').to_s
  end

  def create_time
    Time.parse(@doc.elements['//creationTime'].text)
  end
end

class ForecastPeriod
  attr_reader :name, :text, :icon_url, :temp_f, :wx, :pop
  def initialize (name, text, icon_url, temp_f, wx, pop)
    @name = name ; @text = text ; @icon_url = icon_url ; @temp_f = temp_f ; @wx = wx ; @pop = pop
  end
end


