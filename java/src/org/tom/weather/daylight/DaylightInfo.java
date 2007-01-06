/*
 * DaylightInfo.java
 *
 * Created on January 5, 2001, 7:38 PM
 */
package org.tom.weather.daylight;

/**
 * 
 * @author administrator
 * @version
 */
public class DaylightInfo extends java.lang.Object {
  private String sunrise;
  private String sunset;
  private String daylight;

  /** Creates new DaylightInfo */
  public DaylightInfo(String rise, String set, String light) {
    sunrise = rise;
    sunset = set;
    daylight = light;
  }

  public String getSunrise() {
    return sunrise;
  }

  public String getSunset() {
    return sunset;
  }

  public String getDaylight() {
    return daylight;
  }
}
