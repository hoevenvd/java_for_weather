#1 mph = 0.45 m/s
#5 mph = 2,24 m/s
#8 mph = 3,58 m/s  # 5 m/s = 11.18468146 mph
# 2.23214286
def mph_to_mps(mph):
  return round(mph / 2.2369362920544, 2)

def to_f(c):
  return round(((9.0/5.0) * c) + 32, 1)

def to_c(f):
  return round((5.0 / 9.0) * (f - 32))

# 1 inch of mercury = 25.4 mm of mercury = 33.86 millibars
# = 33.86 hectoPascals
# To convert inches of mercury to millibars, multiply the inches value by 33.8637526
# To convert millibars to inches of mercury, multiply the millibar value by 0.0295301.
#  29.79 in / 1008.8 hPa
def inches_of_hg_to_mb(inches):
  return round(inches * 33.8637526, 1)

  # rain conversion
  # 1 inches = 25.4 millimeters
def inches_to_mm(inches):
  return round((inches * 25.4), 2)
