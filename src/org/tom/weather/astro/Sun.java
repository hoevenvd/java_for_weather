package org.tom.weather.astro;

/**
 * Sun
 * <p>
 * 
 * Compute the solar ephemeris and times of sunrise and sunset for a specified
 * date and location.
 * 
 * Algorithms from "Astronomy on the Personal Computer" by Oliver Montenbruck
 * and Thomas Pfleger. Springer Verlag 1994. ISBN 3-540-57700-9. This is a
 * reasonably accurate and very robust procedure for sunrise that will handle
 * unusual cases, such as the one day in the year in arctic latitudes that the
 * sun rises, but does not set. It is, however, very computationally-intensive,
 * and we will need a faster, if less accurate, procedure for the terminator
 * image.
 * 
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.
 * <p>
 * 
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided
 * that this copyright notice and appropriate documentation appears in all
 * copies. This software may not be distributed for fee or as part of
 * commercial, "shareware," and/or not-for-profit endevors including, but not
 * limited to, CD-ROM collections, online databases, and subscription services
 * without specific license.
 * <p>
 * 
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 1.0 Set tabs every 4 characters.
 */
// package Classes;
import java.util.*;

public class Sun implements Constants {
  /*
   * These values can be set by the user to compute the ephemeris.
   */
  private Date javaDate; /* Save for debugging */
  private long timezoneOffset; /* Msec */
  private double latitude; /* Latitude in degrees */
  private double longitude; /* Longitude, East positive */
  /*
   * These values are derived from the caller parameters. MJD Modified Julian
   * Date at midnight at the local timezone. rightAscension Solar ephemeris.
   * declination Solar ephemeris.
   */
  private double MJD; /* midnight at local timezone */

  /**
   * Create the Sun class. The caller must then set date and location parameters
   * in order to carry out a computation.
   */
  public Sun() {
    setDate(new Date());
    setLatitude(0.0);
    setLongitude(0.0);
    setTimezoneOffset(Astro.getTimeZoneOffset());
  }

  /**
   * Configure the Sun class for a specific Java Date, observer timezone, and
   * observer location..
   * 
   * @param date
   *          Java date
   * @param latitude
   *          Observer's latitude (North is positive)
   * @param longitude
   *          Observer's longitude (East is positive)
   * @param timezoneOffset
   *          Observer's timezone (msec East of Greenwich)
   */
  public Sun(Date javaDate, double latitude, double longitude,
      long timezoneOffset) {
    setDate(javaDate);
    setLatitude(latitude);
    setLongitude(longitude);
    setTimezoneOffset(timezoneOffset);
  }

  /**
   * Configure the Sun class for a specific Java Date, observer timezone, and
   * observer location..
   * 
   * @param date
   *          Java date
   * @param longitude
   *          Observer's longitude (East is positive)
   * @param timezoneOffset
   *          Observer's timezone (msec East of Greenwich)
   */
  public Sun(Date javaDate, double longitude, long timezoneOffset) {
    this(javaDate, 0.0, longitude, timezoneOffset);
  }

  /**
   * Accessors.
   */
  public Date getDate() {
    return (javaDate);
  }

  public double getLatitude() {
    return (latitude);
  }

  public double getLongitude() {
    return (longitude);
  }

  public long getTimeZoneOffset() {
    return (timezoneOffset);
  }

  public static int getOffset(Date date, String zone) {
    TimeZone tz = TimeZone.getTimeZone(zone);
    java.util.Calendar cal = Calendar.getInstance(tz);
    cal.setTime(date);
    return tz.getOffset(cal.get(Calendar.ERA), cal.get(Calendar.YEAR), cal
        .get(Calendar.MONTH), cal.get(Calendar.DATE), cal
        .get(Calendar.DAY_OF_WEEK), 0);
  }

  public static void main(String[] args) {
    Sun sun = new Sun(new Date(), 41, -71, 1000 * 60 * 60 * 4 * -1);
    double[] array = sun.riseSet(sun.getLatitude(), Constants.SUNRISE);
    Double sunRise = new Double(array[0]);
    System.out.println("sunrise: " + sunRise);
    Double sunSet = new Double(array[1]);
    System.out.println("sunset: " + Sun.timeString(sunSet.doubleValue()));
    MoonPhase moon = new MoonPhase(new Date());
    System.out.println(moon.getPhase());
  }

  public double[] riseSet(double horizon) {
    double sinHorizon = Astro.sin(horizon);
    double yMinus = sinAltitude(0.0) - sinHorizon;
    boolean aboveHorizon = (yMinus > 0.0);
    double rise = BELOW_HORIZON;
    double set = BELOW_HORIZON;
    if (aboveHorizon) {
      rise = ABOVE_HORIZON;
      set = ABOVE_HORIZON;
    }
    for (double hour = 1.0; hour <= 24.0; hour += 2.0) {
      double yThis = sinAltitude(hour) - sinHorizon;
      double yPlus = sinAltitude(hour + 1.0) - sinHorizon;
      /*
       * System.out.println( Format.format(((int) hour), 2) + ": " +
       * Format.format(yMinus, 12, 8) + ", " + Format.format(yThis, 12, 8) + ", " +
       * Format.format(yPlus, 12, 8) );
       */
      /*
       * ._________________________________________________________________. |
       * Quadratic interpolation through the three points: | | [-1, yMinus], [0,
       * yThis], [+1, yNext] | | (These must not lie on a straight line.) | |
       * Note: I've in-lined this as it returns several values. |
       * ._________________________________________________________________.
       */
      double root1 = 0.0;
      double root2 = 0.0;
      int nRoots = 0;
      double A = (0.5 * (yMinus + yPlus)) - yThis;
      double B = (0.5 * (yPlus - yMinus));
      double C = yThis;
      double xExtreme = -B / (2.0 * A);
      double yExtreme = (A * xExtreme + B) * xExtreme + C;
      double discriminant = (B * B) - 4.0 * A * C;
      if (discriminant >= 0.0) { /* Intersects x-axis? */
        double DX = 0.5 * Math.sqrt(discriminant) / Math.abs(A);
        root1 = xExtreme - DX;
        root2 = xExtreme + DX;
        if (Math.abs(root1) <= +1.0)
          nRoots++;
        if (Math.abs(root2) <= +1.0)
          nRoots++;
        if (root1 < -1.0)
          root1 = root2;
      }
      /*
       * .________________________________________________________________. |
       * Quadratic interpolation result: | | nRoots Number of roots found (0, 1,
       * or 2) | | If nRoots == zero, there is no event in this range. | | root1
       * First root (nRoots >= 1) | | root2 Second root (nRoots == 2) | | yMinus
       * Y-value at interpolation start. If < 0, root1 is | | a moonrise event. | |
       * yExtreme Maximum value of y (nRoots == 2) -- this determines | |
       * whether a 2-root event is a rise-set or a set-rise. |
       * .________________________________________________________________.
       */
      switch (nRoots) {
        case 0: /* No root at this hour */
          break;
        case 1: /* Found either a rise or a set */
          if (false) {
            Format.log("hour", hour);
            Format.log("root1", root1);
          }
          if (yMinus < 0.0) {
            rise = hour + root1;
            if (false) {
              Format.log("rise", rise);
            }
          } else {
            set = hour + root1;
            if (false) {
              Format.log("set", set);
            }
          }
          break;
        case 2: /* Found both a rise and a set */
          if (yExtreme < 0.0) {
            rise = hour + root2;
            set = hour + root1;
          } else {
            rise = hour + root1;
            set = hour + root2;
          }
          break;
      } /* root switch */
      yMinus = yPlus;
      if (Astro.isEvent(rise) && Astro.isEvent(set))
        break;
    } /* for loop */
    double result[] = new double[2];
    if (Astro.isEvent(rise)) {
      rise = Astro.mod(rise, 24.0);
    }
    if (Astro.isEvent(set)) {
      set = Astro.mod(set, 24.0);
    }
    result[RISE] = rise;
    result[SET] = set;
    return (result);
  }

  /**
   * Compute the time of sunrise and sunset for this date. This uses an
   * exhaustive search algorithm described in Astronomy on the Personal
   * Computer. Consequently, it is rather slow. The times are returned in the
   * observer's local time.
   * 
   * @param latitude
   *          The observer's latitude
   * @param horizon
   *          The adopted true altitude of the horizon in degrees. Use one of
   *          the following values defined in Constants.java:
   * 
   * <pre>
   * 	SUNRISE					 -0¡50'
   * 	CIVIL_TWILIGHT			 -6¡00'
   * 	NAUTICAL_TWILIGHT		-12¡00'
   * 	ASTRONOMICAL_TWILIGHT	-18¡00'
   * </pre>
   * 
   * Here are some test values. The "correct" values were taken from
   *          the <a href="http://tycho.usno.navy.mil/"> United States Naval
   *          Observatory</a> Web page.
   *          <p>
   *          1997.01.01, latitude 0.0, longitude 0.0, zone 0.0
   *          <p>
   * 
   * <pre>
   *   SunRiseSet   Civil Twil.    Naut Twil.
   *  06:00 18:07   05:37 18:30   05:11 18:56
   * <p>
   *  1997.02.21, latitude 37.8N, longitude 122.4W, zone -8.0
   * <p>
   * &lt;pre&gt;
   *   SunRiseSet   Civil Twil.
   *  06:52 17:56   06:25 18:22
   *  &lt;/pre&gt;
   * 
   */
  public double[] riseSet(double latitude, double horizon) {
    setLatitude(latitude);
    return (riseSet(horizon));
  }

  public void setDate(Date javaDate) {
    this.javaDate = javaDate;
    double ZONE = ((double) timezoneOffset) / 86400000.0;
    MJD = Astro.midnightMJD(javaDate) - ZONE;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setTimezoneOffset(long timezoneOffset) {
    this.timezoneOffset = timezoneOffset;
    double ZONE = ((double) timezoneOffset) / 86400000.0;
    MJD = Astro.midnightMJD(javaDate) - ZONE;
  }

  /**
   * Compute the sine of the altitude of the object for this date, hour, and
   * location.
   * 
   * @param hour
   *          Hour past midnight (for the current MJD)
   * @result The sine of the object's altitude above the horizon.
   * 
   * Note: this overrides Sun.sinAltitude and contains the moon orbital
   * computation.
   */
  public double sinAltitude(double hour) {
    /*
     * Compute rightAscension and declination
     */
    double mjd = MJD + (hour / 24.0);
    double[] sun = Astro.solarEphemeris(mjd);
    double TAU = 15.0 * (Astro.LMST(mjd, longitude) - sun[Astro.RA]);
    double result = Astro.sin(latitude) * Astro.sin(sun[Astro.DEC])
        + (Astro.cos(latitude) * Astro.cos(sun[Astro.DEC]) * Astro.cos(TAU));
    /*
     * System.out.println("sinAltitude at " + hour + ", MJD = " + (MJD + (hour /
     * 24.0)) + ", RA = " + sun[Astro.RA] + ", DEC = " + sun[Astro.DEC] + ", TAU = " +
     * TAU );
     */
    return (result);
  }

  /*
   * Convert a time (in the range 0.0 .. 24.0) to a time string.
   */
  public static String timeString(double value) {
    int hour = (int) Math.floor(value);
    value = (value - (double) hour) * 60.0;
    int minute = (int) (value + 0.5);
    if (minute >= 60) {
      ++hour;
      minute -= 60;
    }
    if (hour >= 24) {
      hour -= 24;
    }
    StringBuffer result = new StringBuffer();
    if (hour < 10) {
      result.append('0');
    }
    result.append(new Integer(hour).toString());
    result.append(':');
    if (minute < 10) {
      result.append('0');
    }
    result.append(new Integer(minute).toString());
    return (result.toString());
  }
}
