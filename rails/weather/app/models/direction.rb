class Direction
  def Direction.to_s(degrees)
    return "N" if degrees <= 11
    return "NNE" if degrees <= 34
    return "NE" if degrees <= 56
    return "ENE" if degrees <= 79
    return "E" if degrees <= 101
    return "ESE" if degrees <= 124
    return "SE" if degrees <= 146
    return "SSE" if degrees <= 169
    return "S" if degrees <= 191
    return "SSW" if degrees <= 214
    return "WSW" if degrees <= 259
    return "W" if degrees <= 281
    return "WNW" if degrees <= 304
    return "NW" if degrees <= 326
    return "NNW" if degrees <= 349
    return "N"
  end
end
