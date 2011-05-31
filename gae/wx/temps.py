import math
import convert

def calc_apparent_temp (temp, rh, wind):
  if temp is None:
    raise ValueError('temperature can not be nil')
  elif temp < 68 and wind is not None:
    return int(calc_windchill(temp, wind))
  elif temp > 80 and rh is not None:
    return int(calc_heat_index(temp, rh))
  else:
    return int(temp)

def calc_heat_index (temp, rh):
  # http://en.wikipedia.org/wiki/Heat_index
  if temp < 80.0 or rh < 40.0: return temp

  temp = int(temp)
  rh = int(rh)
  
  c1 = -42.379
  c2 = 2.04901523
  c3 = 10.1433127
  c4 = -0.22475541
  c5 = -6.83783 * (10 ** -3)
  c6 = -5.481717 * (10 ** -2)
  c7 = 1.22874 * (10 ** -3)
  c8 = 8.5282 * (10 ** -4)
  c9 = -1.99 * (10 ** -6)
    
  hi = c1 + \
      (c2 * temp) + \
      (c3 * rh) + \
      (c4 * temp * rh) + \
      (c5 * (temp ** 2)) + \
      (c6 * (rh ** 2)) + \
      (c7 * ((temp ** 2) * rh)) + \
      (c8 * (temp * (rh ** 2))) + \
      (c9 * ((temp ** 2) * (rh ** 2)))
  if hi < temp:
    hi = temp
  return int(hi)

def calc_windchill (temp, wind):
  # http://lwf.ncdc.noaa.gov/oa/climate/conversion/windchillchart.html
  if temp > 68 or wind == 0: return temp
  temp = int(temp)
  wind = int(wind)
  wc = 35.74 + (0.6215 * temp) - 35.75 * (wind ** 0.16) + (0.4275 * temp * (wind ** 0.16))
  if int(wc) > temp:
    return int(temp)
  else:
    return int(wc)

def calc_dewpoint(temp, humidity):
  #http://en.wikipedia.org/wiki/Dewpoint
  temp = convert.to_c(temp)
  humidity = float(humidity) / 100.0
  lam = ((17.27 * temp) / (237.7 + temp)) + math.log(humidity)
  dew = (237.37 * lam) / (17.27 - lam)
  return int(convert.to_f(dew))

