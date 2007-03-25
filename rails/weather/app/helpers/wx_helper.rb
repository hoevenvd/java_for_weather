require 'parsedate'

module WxHelper
  def self.apparent_temp (temp, rh, wind)
    if (temp == nil)
      raise("temperature can not be nil")
    elsif temp < 68 && wind != nil
      return self.windchill(temp, wind)
    elsif temp > 80 && rh != nil
      return self.heat_index(temp, rh)
    else
      return temp
    end
  end
    
  def minutes_to_hhmm(start_tm, end_tm)
    interval = ((end_tm - start_tm) / 60).to_i # get minutes
    tmp = interval.divmod(60)
    hours = tmp[0]
    minutes = tmp[1]
    return sprintf("%d:%02d", hours, minutes)
  end
  
  
  def self.heat_index (temp, rh)
    # http://en.wikipedia.org/wiki/Heat_index
    return temp unless (temp > 80.0 && rh > 40.0) # not relevant
    
    temp = temp.to_i
    rh = rh.to_i
    
    c1 = -42.379
    c2 = 2.04901523
    c3 = 10.1433127
    c4 = -0.22475541
    c5 = -6.83783 * 10.power!(-3)
    c6 = -5.481717 * 10.power!(-2)
    c7 = 1.22874 * 10.power!(-3)
    c8 = 8.5282 * 10.power!(-4)
    c9 = -1.99 * 10.power!(-6)
    
    hi = c1 + 
        (c2 * temp) + 
        (c3 * rh) + 
        (c4 * temp * rh) + 
        (c5 * temp.power!(2)) + 
        (c6 * rh.power!(2)) + 
        (c7 * (temp.power!(2) * rh)) +
        (c8 * (temp * rh.power!(2))) + 
        (c9 * (temp.power!(2) * rh.power!(2)))
    if hi < temp
      hi = temp
    end
    hi.to_i
  end
  
  def self.windchill (temp, wind)
    # http://lwf.ncdc.noaa.gov/oa/climate/conversion/windchillchart.html
    return temp unless temp < 68 && wind > 0  
    temp = temp.to_i
    wind = wind.to_i
    wc = 35.74 + (0.6215 * temp) - 35.75 * (wind.power!(0.16)) + (0.4275 * temp * wind.power!(0.16))
    if wc.to_i > temp
      temp
    else
      wc.to_i
    end
  end
  
  def self.dewpoint(temp, humidity)
    #http://en.wikipedia.org/wiki/Dewpoint
    
    temp = self.to_c(temp)
    humidity = humidity / 100.0
    lam = ((17.27 * temp) / (237.7 + temp)) + Math.log(humidity)
    dew = (237.37 * lam) / (17.27 - lam)
    dew = self.to_f(dew)
  end
  
  def self.to_f(c)
    ((9.0/5.0) * c) + 32 
  end
  
  def self.to_c(f)
    (5.0 / 9.0) * (f - 32)
  end
  
end
