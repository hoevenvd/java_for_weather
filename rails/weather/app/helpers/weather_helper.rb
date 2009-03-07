require 'net/http'
require 'socket'

#      sb.append("&action=updateraw&realtime=1&rtfreq=3.0");
# weather.wunderground.realtime.uploadUrl=http://rtupdate.wunderground.com/weatherstation/updateweatherstation.php

URL = "rtupdate.wunderground.com"

module WeatherHelper

  def self.post_to_cwop(location)
     c = CurrentCondition.find_by_location(location)
     req = AppConfig.cwop_id
     req += ">APRS,TCPXX*:"
     req += c.sample_date.utc.strftime("@%d%H%Mz")
     req += AppConfig.cwop_lat_long
     req += sprintf("_%03d", c[:wind_direction])
     req += sprintf("/%03d", c[:windspeed])
     req += sprintf("g%03d", c[:gust])
     req += sprintf("t%03d", c[:outside_temperature].to_i)

     if !c.solar_radiation.nil?
       # Luminescence (Solar Radiation). 
       # Requires solar radiation sensor. 
       # Data omitted if no solar radition sensor is present in the station.
       # A capital L, "L", is used to report values between 0 and 999 w/m2.
       #  A lower case L, "l", used to report values from 1000 to 1999 w/m2.
       if (c.solar_radiation) > 1000
         rads = sprintf("l%d", c.solar_radiation)
       else
         rads = sprintf("L%03d", c.solar_radiation)
       end
       req += rads
     end
     req += sprintf("")
     req += sprintf("r%03d", c.hourly_rain * 100)
     req += sprintf("p%03d", c.twentyfour_hour_rain * 100)
     req += sprintf("P%03d", c.daily_rain * 100)
     req += sprintf("h%02d", c.outside_humidity)
     req += sprintf("b%05d", c.pressure * 33.864 * 10)
     req += "eTomOrgDavisVP2"
     init_str = "user #{AppConfig.cwop_id} pass -1 vers linux-1wire 1.00"
     # rotate.aprs.net:14580
     s = TCPSocket.open("rotate.aprs.net", 14580)
     s.puts(init_str)
     sleep 3
     s.gets
     s.puts(req)
     sleep 3
     s.gets
  end

  def self.post_to_wunderground(location)
    conf = AppConfig.wunderground[location]
    if conf != nil
      sample = CurrentCondition.find_by_location(location)
      noaa = NoaaConditions.find_all_by_location(AppConfig.noaa_location, :limit => 1, :order => "as_of desc")[0]
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
#          :weather => noaa.conditions,
#          :visibility => noaa.visibility,
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
    post_url += "&weather=" + CGI::escape(noaa.conditions) unless noaa.nil?
    post_url += "&visibility=" + CGI::escape(noaa.visibility.to_s) unless noaa.nil?
    post_url += "&softwaretype=" + CGI::escape(new_sample[:softwaretype].to_s)
    post_url += "&action=updateraw&realtime=1&rtfreq=3.0"

    response = Net::HTTP.get_response(URL, post_url)
  end

end
