require 'parsedate'

module WxUtils
  def calc_apparent_temp (temp, rh, wind)
    if (temp == nil)
      raise("temperature can not be nil")
    elsif temp < 68 && wind != nil
      return calc_windchill(temp, wind).to_f.round(1)
    elsif temp > 80 && rh != nil
      return calc_heat_index(temp, rh).to_f.round(1)
    else
      return temp.to_f.round(1)
    end
  end

  def calc_heat_index (temp, rh)
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
    hi.to_f.round_with_precision(1)
  end

  def calc_windchill (temp, wind)
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

  def calc_dewpoint(temp, humidity)
    #http://en.wikipedia.org/wiki/Dewpoint

    temp = to_c(temp)
    humidity = humidity / 100.0
    lam = ((17.27 * temp) / (237.7 + temp)) + Math.log(humidity)
    dew = (237.37 * lam) / (17.27 - lam)
    dew = to_f(dew)
  end

  #1 mph = 0.45 m/s
  #5 mph = 2,24 m/s
  #8 mph = 3,58 m/s  # 5 m/s = 11.18468146 mph
  # 2.23214286
  def mph_to_mps(mph)
    return mph / 2.2369362920544
  end

  def to_f(c)
    ((9.0/5.0) * c) + 32
  end

  def to_c(f)
    (5.0 / 9.0) * (f - 32)
  end

  # 1 inch of mercury = 25.4 mm of mercury = 33.86 millibars
  # = 33.86 hectoPascals
  # To convert inches of mercury to millibars, multiply the inches value by 33.8637526
  # To convert millibars to inches of mercury, multiply the millibar value by 0.0295301.
  #  29.79 in / 1008.7 hPa
  def inches_of_hg_to_mb(inches)
    inches * 33.8637526
  end

  # rain conversion
  # 1 inches = 25.4 millimeters
  def inches_to_mm(inches)
    (inches * 25.4).to_f.round_with_precision(2)
  end

end
