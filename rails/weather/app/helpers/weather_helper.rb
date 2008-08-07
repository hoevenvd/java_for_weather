require 'net/http'

#      sb.append("&action=updateraw&realtime=1&rtfreq=3.0");
# weather.wunderground.realtime.uploadUrl=http://rtupdate.wunderground.com/weatherstation/updateweatherstation.php

URL = "rtupdate.wunderground.com"

module WeatherHelper


  def self.post_to_wunderground(location)
    conf = AppConfig.wunderground[location]
    if conf != nil
      sample = CurrentCondition.find_by_location(location)
      raise ArgumentError if sample == nil
      new_sample = 
        WundergroundStruct.new(
          :id => conf["id"],
          :password => conf["password"],
          :dateutc => sample.sample_date,
          :winddir => sample.wind_direction,
          :windspeedmph => sample.windspeed,
          :windgustmph => sample.gust,
          :humidity => sample.outside_humidity,
          :tempf => sample.outside_temperature,
          :rainin => sample.hourly_rain,
          :dailyrainin => sample.daily_rain,  
          :baromin => sample.pressure,
          :dewptf => sample.dewpoint,
          :solarradiation => sample.solar_radiation,
          :weather => NoaaConditions.find_all_by_location(AppConfig.noaa_location, :limit => 1, :order => "as_of desc").conditions,
          :softwaretype => "org.tom.weather")
     end

    post_url = "/weatherstation/updateweatherstation.php?ID=" + CGI::escape(new_sample[:id])
    post_url += "&PASSWORD=" + CGI::escape(new_sample[:password])
    post_url += "&dateutc=" + CGI::escape(new_sample[:dateutc].to_s(:db))
    post_url += "&winddir=" + CGI::escape(new_sample[:winddir].to_s)
    post_url += "&windspeedmph=" + CGI::escape(new_sample[:windspeedmph].to_s)
    post_url += "&windgustmph=" + CGI::escape(new_sample[:windgustmph].to_s)
    post_url += "&humidity=" + CGI::escape(new_sample[:humidity].to_s)
    post_url += "&tempf=" + CGI::escape(new_sample[:tempf].to_s)
    post_url += "&rainin=" + CGI::escape(new_sample[:rainin].to_s)
    post_url += "&dailyrainin=" + CGI::escape(new_sample[:dailyrainin].to_s)
    post_url += "&baromin=" + CGI::escape(new_sample[:baromin].to_s)
    post_url += "&dewptf=" + CGI::escape(new_sample[:dewptf].to_s)
    post_url += "&solarradiation=" + CGI::escape(new_sample[:solarradiation].to_s)
    post_url += "&weather=" + CGI::escape(new_sample[:weather].to_s)
    post_url += "&softwaretype=" + CGI::escape(new_sample[:softwaretype].to_s)
    post_url += "&action=updateraw&realtime=1&rtfreq=3.0"

    response = Net::HTTP.get_response(URL, post_url)
  end


end
