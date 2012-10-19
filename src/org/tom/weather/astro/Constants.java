package org.tom.weather.astro;

/**
 * Constants<p>
 *
 * These constants are used throughout the SunClock package.
 *
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.<p>
 *
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies. This
 * software may not be distributed for fee or as part of commercial, "shareware,"
 * and/or not-for-profit endevors including, but not limited to, CD-ROM collections,
 * online databases, and subscription services without specific license.<p>
 *
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 1.0
 * Set tabs every 4 characters.
 */
// package Classes;
import java.awt.*;

/**
 * Constants used throughout the SunClock applet. To use these values, a class
 * should implement the Constants class.
 */
public interface Constants {
  /**
   * These bit-set values are passed from SunClockData to notification clients
   * to indicate changes in the underlying data.
   */
  public static final int LOCATION_CHANGED = 0x0001;
  public static final int TIME_CHANGED = 0x0002;
  public static final int SUN_UPDATE_NEEDED = 0x0004;
  public static final int MOON_UPDATE_NEEDED = 0x0008;
  public static final int DISPLAY_FONT_CHANGED = 0x0010;
  public static final int WORLD_MAP_SIZE_CHANGED = 0x0020;
  /**
   * All observers watch for STOP_NOW and kill all asynchronous processes
   * (including image loading) when it is received.
   */
  public static final int STOP_NOW = 0x10;
  /**
   * The sunrise and moonrise algorithms return values in a double[] vector
   * where result[RISE] is the time of sun (moon) rise and result[SET] is the
   * value of sun (moon) set. Sun.riseSetLST() also returns result[ASIMUTH_RISE]
   * and result[ASIMUTH_SET].
   */
  public static final int RISE = 0;
  public static final int SET = 1;
  public static final int ASIMUTH_RISE = 2;
  public static final int ASIMUTH_SET = 3;
  /**
   * ABOVE_HORIZON and BELOW_HORIZON are returned for sun and moon calculations
   * where the astronomical object does not cross the horizon.
   */
  public static final double ABOVE_HORIZON = Double.POSITIVE_INFINITY;
  public static final double BELOW_HORIZON = Double.NEGATIVE_INFINITY;
  /**
   * RA and DEC are indexes into the result vector from several astronomical
   * functions.
   */
  public static final int RA = 0;
  public static final int DEC = 1;
  /**
   * Degrees -> Radians: degree * DegRad Radians -> Degrees: radians / DegRad
   */
  public static final double DegRad = (Math.PI / 180.0);
  /**
   * These values define the location of the horizon for an observer at sea
   * level. -0¡50' Sunrise/Sunset -6¡ Civil Twilight (default twilight) -12¡
   * Nautical Twilight -18¡ Astronomical Twilight Note that these values are
   * related to the horizon (90¡ from the azimuth). If the observer is above or
   * below the horizon, the correct adopted true sunrise/sunset altitude in
   * degrees is -(50/60) - 0.0353 * sqrt(height in meters);
   * 
   * Several religions use the times of twilight, sunrise, and sunset, and other
   * times derived from these times such as midday, to determine the times of
   * religious observation and obligation. For example, the Jewish Sabbath ends
   * at the time when "three stars" are observable after sunset, which was
   * specified by the Talmud as when the sun is 9 degrees below the horizon.
   * 
   * This program does not presume to provide theologically-correct results and
   * it should be noted that the rules are extremely complex, especially at high
   * latitudes: i.e., "when does Sabbath end when the sun never sets?" You
   * should consult a religious advisor for guidance in interpreting the results
   * of this program.
   * 
   * The following are relevant to Jewish religious observance, as described in
   * Andrew H. Shooman's sunrise_prg.c, 26 January 1992.
   * 
   * Morning Alot HaShachar -15¡ (The earliest time to begin Shacharit) Taalit
   * v'Tfilin -12¡40' (Morning only) Sof Z'man Kriat Sh'ma Sunrise + 3 hours
   * Midday The midpoint between sunrise and sunset Mincha K'tana Midday + 1/2
   * hour: the earliest time to begin Mincha. Sabbath/holiday end -9¡ (The end
   * of Sabbath or holiday observation, derived from Rav Yehuda's Tzeit
   * ha-Kochavim from Shabbat 24b, 35a
   * 
   * The following are relevant to Moslim religious observance, as described in
   * "Astronomy of Islamic Times," by Mohammad Ilyas, 1988. ISBN 0-7201-1983-9:
   * Sunset -0¡50' (Sunrise/Sunset). Isha -18¡ (End of astronomical twilight)
   * Fajr -18¡ (Beginning of astronomical twilight) Sunrise -0¡50'
   * (Sunrise/Sunset) Midday (Declination - Right Ascension) Asr (Shafi) 90¡ -
   * arccotangent(1 + cotangent(An)) where An = 90¡ - Zn and Zn is the zenith
   * angle at noon. Asr (Hanafi) 90¡ - arccotangent(2 + cotangent(An))
   * 
   * Sunrise is defined as the time when the apparent altitlude of the upper
   * limb of the Sun will be -50 arc minutes below the horizon. This takes into
   * account refraction and solar semi-diameter effects.
   * 
   * Twilight is defined as -6 degrees (civil), -12 degrees (nautical), and -18
   * degrees (astronomical).
   */
  public static final double SUNRISE = -(50.0 / 60.0);
  public static final double CIVIL_TWILIGHT = -6.0;
  public static final double NAUTICAL_TWILIGHT = -12.0;
  public static final double ASTRONOMICAL_TWILIGHT = -18.0;
  public static final double TWILIGHT = CIVIL_TWILIGHT;
  public static final double JEWISH_SABBATH_END = -9.0;
  public static final double JEWISH_MORNING_ALOT = -15.0;
  public static final double JEWISH_TFILIN = -(12 + 40.0 / 60.0);
}
