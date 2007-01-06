/*
 * DaylightCalculator.java
 *
 * Created on January 5, 2001, 7:23 PM
 */
package org.tom.weather.daylight;

import java.util.Date;
import org.tom.weather.astro.*;

/**
 * 
 */
public class DaylightCalculator extends Object {
  String locale = "EST";
  double longitude = -71.1D;
  double latitude = 41.5D;
  Sun sun;

  /** Creates new DaylightCalculator */
  public DaylightCalculator(String locale, double longitude, double latitude) {
    this.locale = locale;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public DaylightInfo getDaylightInfo(Date date) {
    double anArray[];
    String rise;
    String set;
    String hours;
    sun = new Sun(date, latitude, longitude, Sun.getOffset(date, locale));
    anArray = sun.riseSet(sun.getLatitude(), Constants.SUNRISE);
    rise = Sun.timeString(anArray[0]);
    set = Sun.timeString(anArray[1]);
    hours = Sun.timeString(anArray[1] - anArray[0]);
    return new DaylightInfo(rise, set, hours);
  }

  public DaylightInfo getDaylightInfo() {
    return getDaylightInfo(new Date());
  }

  public static void main(String argv[]) {
    /*
     * DaylightCalculator calc = new DaylightCalculator("EST", -71.1D, 41.5D);
     * DaylightInfo info = calc.getDaylightInfo(new Date(2001,8,5));
     * System.out.println("rise: " + info.getSunrise());
     * System.out.println("set: " + info.getSunset());
     * System.out.println("hours: " + info.getDaylight());
     */
  }
}
