require 'pp'

class Sample < ActiveRecord::Base

  Sample.establish_connection(:old_weather)

  def windspeed
    return "calm" if self[:windspeed].eql? 0
    return self[:windspeed]
  end
  
  def apparent_temp
    WxHelper.apparent_temp(outside_temperature, outside_humidity, self[:windspeed])
  end
	
  def wind_direction
	if self[:windspeed].eql? 0
      return "n/a"
    else
      return Direction.to_s(self[:wind_direction])
    end
  end
  
  def wind
    return "calm" if (self[:windspeed].eql?(0))
    return wind_direction + " at " + windspeed.to_s + " mph"
  end

  def rain_rate
    if (!self[:is_raining])
      return "n/a"
    else
      return self[:rain_rate]
    end
  end
  
  def raining_str
    if (self[:is_raining])
      raining + " at " + rain_rate.to_s + " in./hr"
    else
      ""
    end
  end

  def raining
    self[:is_raining] ? "Raining " : ""
  end	
  
  def pressure
    self[:pressure].to_s + " and " + self[:bar_status]
  end
end
