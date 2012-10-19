package org.tom.weather.astro;

/**
 * AstroFormat
 * <p>
 * 
 * Various static methods.
 * 
 * Algorithm 46 and 47 from "Practical Astronomy with Your Calculator" (3rd
 * Edition), by Patrick Duffett-Smith ISBN 0-521-35699-7
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
import java.text.*;

public class AstroFormat implements Constants {
  public static final boolean CIVIL_FORMAT = true;
  public static final boolean ASTRO_FORMAT = false;
  /*
   * We want to display the degree sign, but this presumes that the compiler and
   * Applet Runner properly -- and consistently -- converts the Macintosh degree
   * character to/from Unicode. For now, we'll use 'd', 'm' and 's'
   */
  private static final char degreeSign = '\u00B0'; // 'd';
  private static final char minuteSign = '\''; // 'm';
  private static final char secondSign = '"'; // 's';

  /**
   * Convert a fractional degree value to its string representation.
   * 
   * @param value
   *          A fractional degree.
   * @result The string representation. For example, 90.5 yields 90¡30'"
   */
  public static String dm(double value) {
    boolean isNegative = (value < 0.0);
    if (isNegative)
      value = (-value);
    int degree = (int) Math.floor(value);
    value = (value - ((double) degree)) * 60.0;
    int minute = (int) Math.floor(value);
    int second = (int) ((value - (double) minute) * 60.0);
    if (second > 30 || second == 30 && (minute & 0x01) == 1) {
      ++minute;
    }
    if (minute >= 60) {
      ++degree;
      minute -= 60;
    }
    if (isNegative) {
      if (degree > 0)
        degree = (-degree);
      else if (minute > 0)
        minute = (-minute);
    }
    StringBuffer result = new StringBuffer().append(
        Format.format(degree, ' ', 4, 0, degreeSign)).append(
        Format.format(minute, '0', 2, 0, minuteSign));
    return (result.toString());
  }

  /**
   * Convert a fractional degree value to its string representation.
   * 
   * @param value
   *          A fractional degree.
   * @param tag
   *          Either "WE" or "NS"
   * @result The string representation. For example, 90.5 yields N90¡30'"
   */
  public static String dm(double value, String tag) {
    StringBuffer text = new StringBuffer(dm(Math.abs(value)));
    if (value == 0.0)
      ;
    else if (value > 0.0)
      text.insert(0, tag.charAt(0));
    else {
      text.insert(0, tag.charAt(1));
    }
    return (text.toString());
  }

  /**
   * Convert a fractional degree value to its string representation.
   * 
   * @param value
   *          A fractional degree.
   * @result The string representation. For example, 90.5 yields 90¡30'00"
   */
  public static String dms(double value) {
    boolean isNegative = (value < 0.0);
    if (isNegative)
      value = (-value);
    int degree = (int) Math.floor(value);
    value = (value - (double) degree) * 60.0;
    int minute = (int) Math.floor(value);
    int second = (int) ((value - (double) minute) * 60.0);
    if (isNegative) {
      if (degree > 0)
        degree = (-degree);
      else if (minute > 0)
        minute = (-minute);
      else
        second = (-second);
    }
    StringBuffer result = new StringBuffer().append(
        Format.format(degree, ' ', 4, degreeSign)).append(
        Format.format(minute, '0', 2, minuteSign)).append(
        Format.format(second, '0', 4, secondSign));
    return (result.toString());
  }

  /**
   * Convert a fractional degree value to its string representation.
   * 
   * @param value
   *          A fractional degree.
   * @param tag
   *          Either "WE" or "NS"
   * @result The string representation. For example, 90.5 yields W90¡30'00"
   */
  public static String dms(double value, String tag) {
    StringBuffer text = new StringBuffer(dms(Math.abs(value)));
    if (value == 0.0)
      ;
    else if (value > 0.0)
      text.insert(0, tag.charAt(0));
    else {
      text.insert(0, tag.charAt(1));
    }
    return (text.toString());
  }

  /**
   * Convert degree, minute, second to degree.fraction. By convention, if
   * degrees is zero, a negative sign, if any, is attached attached to the
   * minute value.
   * 
   * @param degree
   *          Degrees (West is negative)
   * @param minute
   *          Minutes
   * @param second
   *          Seconds
   * @return The corresponding value. For example, 0,-30,0 yields -0.5
   */
  public static double dms(int degree, int minute, int second) {
    return (hms(degree, minute, second));
  }

  public static String getGMTDateString(Date instant) {
    try {
      SimpleTimeZone tzGMT = (SimpleTimeZone) TimeZone.getTimeZone("GMT");
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm zz");
      dateFormat.setTimeZone(tzGMT);
      return (dateFormat.format(instant));
    } catch (Exception e) {
      return ("");
    }
  }

  /**
   * Return the date string for the system TimeZone
   */
  public static String getLocalDateString(Date instant) {
    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    return (dateFormat.format(instant));
  }

  /**
   * Return the date string for a user-specified TimeZone.
   */
  public static String getLocalDateString(Date instant, TimeZone timeZone) {
    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    dateFormat.setTimeZone(timeZone);
    return (dateFormat.format(instant));
  }

  /**
   * Convert a fractional hour value to its string representation
   * 
   * @param value
   *          A fractional hour.
   * @result The string representation (hours and minutes only)
   */
  public static String hm(double value) {
    if (value == ABOVE_HORIZON)
      return ("above");
    else if (value == BELOW_HORIZON)
      return ("below");
    else {
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
      StringBuffer result = new StringBuffer().append(
          Format.format(hour, '0', 2, ':')).append(
          Format.format(minute, '0', 2, Format.NO_CHAR));
      return (result.toString());
    }
  }

  /**
   * Convert a fractional hour value to its string representation.
   * 
   * @param value
   *          A fractional hour.
   * @result The string representation. For example, 12.5 yields 12:30:00
   */
  public static String hms(double value) {
    return (hms(value, 0, CIVIL_FORMAT));
  }

  /**
   * Convert a fractional hour value to its string representation using either a
   * civil or astronomical format convention.
   * 
   * @param value
   *          A fractional hour.
   * @param secondPrecision
   *          The number of digits of precision for the seconds.
   * @param isCivil
   *          Either Astro.CIVIL_FORMAT or Astro.ASTRO_FORMAT to distingish
   *          between 12:00:00 and 12h00m00s.
   * @result The string representation.
   */
  public static String hms(double value, int secondPrecision, boolean isCivil) {
    int second = (int) Math.floor(value * 3600.0);
    double fract = (value * 3600.0) - (double) second;
    if (secondPrecision == 0) {
      if (fract > 0.5 || fract == 0.5 && (second & 0x01) == 1) {
        ++second;
      }
    }
    int hour = second / 1440;
    second %= 1440;
    int minute = second / 60;
    second %= 60;
    char hourChar = (isCivil) ? ':' : 'h';
    char minuteChar = (isCivil) ? ':' : 'm';
    char secondChar = (isCivil) ? Format.NO_CHAR : 's';
    StringBuffer result = new StringBuffer().append(
        Format.format(hour, ' ', 2, hourChar)).append(
        Format.format(minute, '0', 2, minuteChar));
    if (secondPrecision == 0) {
      result.append(Format.format(second, '0', 2, secondChar));
    } else {
      fract += (double) second;
      result.append(Format.format(fract, '0', 2, secondPrecision, secondChar));
    }
    return (result.toString());
  }

  /**
   * Convert hours, minute, second to hours.fraction.
   * 
   * @param hours
   *          Hours
   * @param minute
   *          Minutes
   * @param second
   *          Seconds
   * @return The corresponding value. For example, 6,30,0 yields 6.5
   */
  public static double hms(int hour, int minute, double second) {
    return (((double) hour) + (((double) minute) / 60.0) + (second / 3600.0));
  }

  /**
   * Convert hours, minute, second to hours.fraction.
   * 
   * @param hours
   *          Hours
   * @param minute
   *          Minutes
   * @param second
   *          Seconds
   * @return The corresponding value. For example, 6,30,0 yields 6.5
   */
  public static double hms(int hour, int minute, int second) {
    return (hms(hour, minute, (double) second));
  }

  /**
   * Convert a fractional hour value to its string representation using an
   * astronomical format convention.
   * 
   * @param value
   *          A fractional hour.
   * @result The string representation. For example, 12.5 yields 12h30m00.0s
   */
  public static String hmsAstro(double value) {
    return (hms(value, 1, ASTRO_FORMAT));
  }

  /**
   * Convert a timezone offset to its string representation.
   * 
   * @param timezone
   *          The timezone value (msec offset from Greenwich)
   * @result The string representation in hours and minutes
   */
  public static String timeZoneString(long timezone) {
    StringBuffer result = new StringBuffer();
    timezone /= 60000;
    if (timezone > 0) {
      result.append(Format.format(timezone / 60, '0', 2, 0, ':')).append(
          Format.format(timezone % 60, '0', 2)).append(" East");
    } else if (timezone == 0)
      result.append("00:00");
    else {
      timezone = (-timezone);
      result.append(Format.format(timezone / 60, '0', 2, 0, ':')).append(
          Format.format(timezone % 60, '0', 2)).append(" West");
    }
    return (result.toString());
  }
}
