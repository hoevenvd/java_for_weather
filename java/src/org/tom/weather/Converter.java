package org.tom.weather;

import org.tom.util.Rounding;
import org.tom.weather.davis.wm2.WeatherMonitor;

public class Converter {
  /*
   * Outdoor comfort during the winter season depends on several factors, only
   * one of them being temperature. Factors such as whether it's sunny or
   * cloudy, windy or calm, damp or dry, can also play an important role. One
   * popular approach in measuring the level of discomfort (and potential danger
   * from frostbite) is the wind chill index. The wind chill index takes into
   * account both the temperature and wind speed.
   * 
   * The original work on wind chill was done by Antarctic explorers Paul Siple
   * and Charles Passel in the 1940s. They measured the amount of time it took a
   * pan of water to freeze and found that the rate of heat loss from the
   * container could be determined from the air temperature and wind speed. From
   * this observation they empirically developed a formula that describes the
   * rate of heat loss from human skin when exposed to wind. The result is
   * called the Wind Chill Index.
   * 
   * The formula used to calculate the wind chill index is:
   * 
   * WC = 91.4 - (0.474677 - 0.020425 * V + 0.303107 * sqrt(V)) * (91.4 - T)
   * 
   * where:
   * 
   * WC = wind chill V = wind speed (mph) T = temperature (F)
   */
  /*
   * select snapdate, lowouttemp, windgust, (91.4 - (0.474677 - 0.020425 *
   * windgust + 0.303107 * sqrt(windgust * 1.0)) * (91.4 - lowouttemp)) as
   * windchill from weathersnapshots order by windchill limit 5;
   */
  static final int chillTableOne[] = { 156, 151, 146, 141, 133, 123, 110, 87,
      61, 14, 0 };
  static final int chillTableTwo[] = { 0, 16, 16, 16, 25, 33, 41, 74, 82, 152,
      0 };
  static final int THI_Table[][] = {
  // Humidity %
      // 0 10 20 30 40 50 60 70 80 90 100 F
      { 61, 63, 63, 64, 66, 66, 68, 68, 70, 70, 70 }, // 68
      { 63, 64, 65, 65, 67, 67, 69, 69, 71, 71, 72 }, // 69
      { 65, 65, 66, 66, 68, 68, 70, 70, 72, 72, 74 }, // 70
      { 66, 66, 67, 67, 69, 69, 71, 71, 73, 73, 75 }, // 71
      { 67, 67, 68, 69, 70, 71, 72, 72, 74, 74, 76 }, // 72
      { 68, 68, 69, 71, 71, 73, 73, 74, 75, 75, 77 }, // 73
      { 69, 69, 70, 72, 72, 74, 74, 76, 76, 76, 78 }, // 74
      { 70, 71, 71, 73, 73, 75, 75, 77, 77, 78, 79 }, // 75
      { 71, 72, 73, 74, 74, 76, 76, 78, 79, 80, 80 }, // 76
      { 72, 73, 75, 75, 75, 77, 77, 79, 81, 81, 82 }, // 77
      { 74, 74, 76, 76, 77, 78, 79, 80, 82, 83, 84 }, // 78
      { 75, 75, 77, 77, 79, 79, 81, 81, 83, 85, 87 }, // 79
      { 76, 76, 78, 78, 80, 80, 82, 83, 85, 87, 90 }, // 80
      { 77, 77, 79, 79, 81, 81, 83, 85, 87, 89, 93 }, // 81
      { 78, 78, 80, 80, 82, 83, 84, 87, 89, 92, 96 }, // 82
      { 79, 79, 81, 81, 83, 85, 85, 89, 91, 95, 99 }, // 83
      { 79, 80, 81, 82, 84, 86, 87, 91, 94, 98, 103 }, // 84
      { 80, 81, 81, 83, 85, 87, 89, 93, 97, 101, 108 }, // 85
      { 81, 82, 82, 84, 86, 88, 91, 95, 99, 104, 113 }, // 86
      { 82, 83, 83, 85, 87, 90, 93, 97, 102, 109, 120 }, // 87
      { 83, 84, 84, 86, 88, 92, 95, 99, 105, 114, 131 }, // 88
      { 84, 84, 85, 87, 90, 94, 97, 102, 109, 120, 144 }, // 89
      { 84, 85, 86, 89, 92, 95, 99, 105, 113, 128, 150 }, // 90
      { 84, 86, 87, 91, 93, 96, 101, 108, 118, 136, 150 }, // 91
      { 85, 87, 88, 92, 94, 98, 104, 112, 124, 144, 150 }, // 92
      { 86, 88, 89, 93, 96, 100, 107, 116, 130, 150, 150 }, // 93
      { 87, 89, 90, 94, 98, 102, 110, 120, 137, 150, 150 }, // 94
      { 88, 90, 91, 95, 99, 104, 113, 124, 144, 150, 150 }, // 95
      { 89, 91, 93, 97, 101, 107, 117, 128, 150, 150, 150 }, // 96
      { 90, 92, 95, 99, 103, 110, 121, 132, 150, 150, 150 }, // 97
      { 90, 93, 96, 100, 105, 113, 125, 150, 150, 150, 150 }, // 98
      { 90, 94, 97, 101, 107, 116, 129, 150, 150, 150, 150 }, // 99
      { 91, 95, 98, 103, 110, 119, 133, 150, 150, 150, 150 }, // 100
      { 92, 96, 99, 105, 112, 122, 137, 150, 150, 150, 150 }, // 101
      { 93, 97, 100, 106, 114, 125, 150, 150, 150, 150, 150 }, // 102
      { 94, 98, 102, 107, 117, 128, 150, 150, 150, 150, 150 }, // 103
      { 95, 99, 104, 109, 120, 132, 150, 150, 150, 150, 150 }, // 104
      { 95, 100, 105, 111, 123, 135, 150, 150, 150, 150, 150 }, // 105
      { 95, 101, 106, 113, 126, 150, 150, 150, 150, 150, 150 }, // 106
      { 96, 102, 107, 115, 130, 150, 150, 150, 150, 150, 150 }, // 107
      { 97, 103, 108, 117, 133, 150, 150, 150, 150, 150, 150 }, // 108
      { 98, 104, 110, 119, 137, 150, 150, 150, 150, 150, 150 }, // 109
      { 99, 105, 112, 122, 142, 150, 150, 150, 150, 150, 150 }, // 110
      { 100, 106, 113, 125, 150, 150, 150, 150, 150, 150, 150 }, // 111
      { 100, 107, 115, 128, 150, 150, 150, 150, 150, 150, 150 }, // 112
      { 100, 108, 117, 131, 150, 150, 150, 150, 150, 150, 150 }, // 113
      { 101, 109, 119, 134, 150, 150, 150, 150, 150, 150, 150 }, // 114
      { 102, 110, 121, 136, 150, 150, 150, 150, 150, 150, 150 }, // 115
      { 103, 111, 123, 140, 150, 150, 150, 150, 150, 150, 150 }, // 116
      { 104, 112, 125, 143, 150, 150, 150, 150, 150, 150, 150 }, // 117
      { 105, 113, 127, 150, 150, 150, 150, 150, 150, 150, 150 }, // 118
      { 106, 114, 129, 150, 150, 150, 150, 150, 150, 150, 150 }, // 119
      { 107, 116, 131, 150, 150, 150, 150, 150, 150, 150, 150 }, // 120
      { 108, 117, 133, 150, 150, 150, 150, 150, 150, 150, 150 }, // 121
      { 108, 118, 136, 150, 150, 150, 150, 150, 150, 150, 150 } // 122
  };

  public static float getDewpoint(float rh, float temp)
      throws NumberFormatException {
    double dp, ews, num, den;
    double celsiusTemp = (5.0 / 9.0) * (temp - 32.0);
    ews = rh * 0.01 * Math.exp((17.502 * celsiusTemp) / (240.9 + celsiusTemp));
    num = 240.9 * Math.log(ews);
    den = 17.5 - Math.log(ews);
    dp = num / den;
    dp = (9.0 / 5.0 * dp) + 32;
    dp = Rounding.round(dp, 1);
    return (float) dp;
  }

  /*****************************************************************************
   * Temperature-Humidity Index Interpolation Table Each row is for the listed
   * temperature in degrees F from 68 degrees to 122 degrees. Each column is for
   * the humidity in 10% increments from 0% to 100% Values larger than 125 are
   * present to facilitate interpolation only.
   ****************************************************************************/
  public static int getHeatIndex(float temp, int humidity) {
    int x, y;
    if (temp < 68) {
      return (int) temp;
    } else {
      x = (int) temp - 68;
      if (humidity == 0) {
        y = 0;
      } else {
        if (humidity < 10) {
          y = 1;
        } else {
          if (humidity < 20) {
            y = 2;
          } else {
            if (humidity < 30) {
              y = 3;
            } else {
              if (humidity < 40) {
                y = 4;
              } else {
                if (humidity < 50) {
                  y = 5;
                } else {
                  if (humidity < 60) {
                    y = 6;
                  } else {
                    if (humidity < 70) {
                      y = 7;
                    } else {
                      if (humidity < 80) {
                        y = 8;
                      } else {
                        if (humidity < 90) {
                          y = 9;
                        } else {
                          y = 10;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      return THI_Table[x][y];
    }
  }

  public static float getWindChill(int windSpeed, float temp) {
    int i;
    double cf, chill;
    if (windSpeed > 50) {
      windSpeed = 50;
    }
    i = 10 - windSpeed / 5;
    cf = chillTableOne[i] + (chillTableTwo[i] / 16.0) * (windSpeed % 5);
    if (temp < 91.4) {
      chill = cf * ((temp - 91.4) / 256.0) + temp;
    } else {
      chill = temp;
    }
    chill = Rounding.round(chill, 1);
    return (float) chill;
  }

  public static float toCelsius(float farenheit) {
    return (float) Rounding.round((5f / 9f) * (farenheit - 32f), 1);
  }

  public static float toFarenheit(float celsius) {
    return (float) Rounding.round((celsius * 9f / 5f) + 32f, 1);
  }

  public static float toMps(float windspeed) {
    return (float) Rounding.round(windspeed / 2.237f, 1);
  }

  public static float toHPa(float pressure) {
    return (float) Rounding.round((pressure / 29.53f) * 1000, 1);
  }

  public static float toMm(float precipitation) {
    return (float) Rounding.round((precipitation / 0.03937f), 1);
  }

  public static String showMeasureValue(float value, int kindOfValue) {
    String valueString = "";
    if (WeatherMonitor.useMetrics()) {
      if (kindOfValue == 1) {
        valueString = new Float(toCelsius(value)).toString();
      }
      if (kindOfValue == 2) {
        valueString = new Float(toHPa(value)).toString();
      }
      if (kindOfValue == 3) {
        valueString = new Float(toMps(value)).toString();
      }
      if (kindOfValue == 4) {
        valueString = new Integer((int) value).toString();
      }
      return (String) valueString;
    } else {
      if (kindOfValue == 1) {
        valueString = new Float(Rounding.round(value, 1)).toString();
      }
      if (kindOfValue == 2) {
        valueString = new Float(Rounding.round(value, 1)).toString();
      }
      if (kindOfValue == 3) {
        valueString = new Integer((int) value).toString();
      }
      if (kindOfValue == 4) {
        valueString = new Integer((int) value).toString();
      }
      return (String) valueString;
    }
  }

  public static String showMeasureLabel(int kindOfValue) {
    String labelString = "";
    if (WeatherMonitor.useMetrics()) {
      if (kindOfValue == 1) {
        labelString = " C";
      }
      if (kindOfValue == 2) {
        labelString = " hPa";
      }
      if (kindOfValue == 3) {
        labelString = " m/s";
      }
      ;
      if (kindOfValue == 4) {
        labelString = " %";
      }
      ;
      return (String) labelString;
    } else {
      if (kindOfValue == 1) {
        labelString = " F";
      }
      if (kindOfValue == 2) {
        labelString = " inch";
      }
      if (kindOfValue == 3) {
        labelString = " mph";
      }
      ;
      if (kindOfValue == 4) {
        labelString = " %";
      }
      ;
      return (String) labelString;
    }
  }
}
