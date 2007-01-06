package org.tom.weather.astro;

/**
 * SunClock displays a world map showing the sunrise and sunset terminators.
 * <p>
 * 
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.
 * <p>
 * World map copyright &copy; 1992-97 Apple Computer Inc. All Rights Reserved.
 * Used by permission.
 * <p>
 * Moon Image from Michael Myers
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
 * @author <a href="http://www.netavs.com:80/~mhmyers/moon.html">Michael Myers
 *         (moon image)</a>
 * @version 1.2 1996.09.15, revised 1997.11.21 Set tabs every 4 characters.
 */
// package Classes;
import java.util.*;
import java.awt.*;
import java.text.*;

public class AboutSunClock extends OutlineDialog implements Constants {
  private static final String sunClockInfoText = "0\tCopyright \u00A9 1996-1998 Martin Minow\n"
      + "0\tAll rights reserved.\n"
      + "0\tmailto:minow@merrymeet.com\n"
      + "0\tAbout SunClock\n"
      + "1\tSunClock displays the current time, phase of the\n"
      + "1\tmoon, times of sunrise and sunset, and a map of the\n"
      + "1\tworld showing the areas of night and day and the\n"
      + "1\textent of twilight.\n"
      + "1\tSunclock also displays the times of sunrise, sunset,\n"
      + "1\tand twilight, as well as times associated with the moon.\n"
      + "0\tConfiguring SunClock\n"
      + "1\tYou can specify your location by entering latitude,\n"
      + "1\tlongitude, and the timezone offset in the entry fields,\n"
      + "1\tor in HTML parameters on the Web page.\n"
      + "1\tLatitude is in degrees, North is positive.\n"
      + "2\tLatitude may be entered as a fractional degree or\n"
      + "2\tas Degree:Minute, tagged with 'N' or 'S'. For example,\n"
      + "2\tSan Francisco may be entered as \"37:48N\" or \"37.8\".\n"
      + "2\tThe colon is necessary, and there may be no spaces in the entry.\n"
      + "1\tLongitude is in degrees, East is positive.\n"
      + "2\tLongitude may be entered as a fractional degree or\n"
      + "2\tas Degree:Minute, tagged with 'E' or 'W'. For example,\n"
      + "2\tSan Francisco may be entered as \"122:24W\" or \"-122.4\".\n"
      + "2\tThe colon is necessary, and there may be no spaces in the entry.\n"
      + "1\tTimezone offset is in minutes, East is positive.\n"
      + "2\tTimezone may also be entered as \"HH:MM\" where\n"
      + "2\tHH is hours and MM is minutes. For example, San\n"
      + "2\tFrancisco (Pacific Standard Time) may be entered\n"
      + "2\tas -8:00 or -480.\n"
      + "0\tReligous Observation\n"
      + "1\tFor several religons, notably Judiasm and Islam,\n"
      + "1\ttimes associated with sunrise and sunset are used\n"
      + "1\tto determine the time of religious observation and\n"
      + "1\tobligation. For example, the Jewish Sabbath ends at\n"
      + "1\tthe time when \"three stars\" are observable after\n"
      + "1\tsunset, which is specified by the Talmud as when\n"
      + "1\tthe sun is 9 degrees below the horizon.\n"
      + "1\tWhile SunClock can be used to compute these times,\n"
      + "1\tit does not presume to provide theologically-correct\n"
      + "1\tresults. It should be noted that the rules are\n"
      + "1\textremely complex, especially at high latitudes when\n"
      + "1\tthe sun does not set or rise during part of the year.\n"
      + "1\tYou should consult a religous advisor for guidance in\n"
      + "1\tinterpreting the results of this program.\n";
  private SunClockData sunClockData;

  public AboutSunClock(SunClockData sunClockData) {
    super(sunClockData.getSunClockApplet(), "About SunClock");
    this.sunClockData = sunClockData;
    setFont(sunClockData.getDisplayFont());
    StringBuffer text = new StringBuffer(sunClockInfoText);
    text.append("0\tJava support information" + "\n");
    try {
      text.append("1\tJava version: " + System.getProperty("java.version")
          + "\n");
    } catch (SecurityException e) {
    }
    try {
      text.append("1\tJava class version: "
          + System.getProperty("java.class.version") + "\n");
    } catch (SecurityException e) {
    }
    text.append("0\tKnown bugs:\n"
        + "1\tThe moon should be upside down if you are in the\n"
        + "1\tSouthern Hemisphere.\n"
        + "1\tThe angle of the moon's bright limb depends on the\n"
        + "1\tsun's position. This requires code and thought.\n"
        + "1\tThe Muslim and Jewish prayer times should be checked with\n"
        + "1\tappropriate religious experts.\n");
    addText(text.toString());
    makeDialogVisible();
  }
}
