package org.tom.weather.astro;

/**
 * ParseParameter contains number parsers for applet initialization.
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
 * @version 1.1 1996.09.15 Set tabs every 4 characters.
 */
// package Classes;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.net.*;

public class ParseParameter {
  String paramString;
  int index = 0;

  /**
   * Initialize a parameter parse.
   * 
   * @param applet
   *          Where to fetch the parameter from.
   * @param paramName
   *          The name of the parameter to fetch.
   * @throws NumberFormatException
   *           if the parameter is missing.
   */
  public ParseParameter(Applet applet, String paramName)
      throws NumberFormatException {
    this(applet.getParameter(paramName));
  }

  /**
   * Initialize a parameter parse.
   * 
   * @param paramString
   *          The parameter string.
   * @throws NumberFormatException
   *           if the parameter is null.
   */
  public ParseParameter(String paramString) throws NumberFormatException {
    if (paramString == null) {
      throw new NumberFormatException("no parameter");
    }
    this.paramString = paramString;
  }

  /**
   * Parse the fraction part.
   */
  private double fraction() {
    double fraction = 0.0;
    if (index < paramString.length()) {
      char c = paramString.charAt(index);
      // System.out.println(paramName + " (fraction) at " + index + ", \"" +
      // paramString.substring(index) + "\"");
      if (c == '.') {
        double power = 1.0;
        index++;
        for (; index < paramString.length(); index++) {
          c = paramString.charAt(index);
          if (c >= '0' && c <= '9') {
            fraction = (fraction * 10.0) + ((double) (c - '0'));
            power *= 10.0;
          } else {
            break;
          }
        }
        fraction /= power;
      } else if (c == ':') { /* "fraction" is in 1/60's units */
        index++;
        fraction = integer() / 60.0;
      }
    }
    return (fraction);
  }

  /**
   * Parse an integer.
   */
  private double integer() {
    double result = 0.0;
    // System.out.println(paramName + " (integer) at " + index + ", \"" +
    // paramString.substring(index) + "\"");
    while (index < paramString.length()) {
      char c = paramString.charAt(index);
      if (c >= '0' && c <= '9') {
        result = (result * 10.0) + ((double) (c - '0'));
        index++;
      } else {
        break;
      }
    }
    return (result);
  }

  /**
   * Parse a number.
   */
  private double number() {
    double sign = sign();
    double result = integer();
    result += fraction();
    result *= sign;
    return (result);
  }

  /**
   * Parse an integer value.
   * 
   * @return the parsed (or default) value.
   * @throws NumberFormatException
   *           if the parameter is misformatted.
   */
  public int parseInt() throws NumberFormatException {
    return (Integer.valueOf(paramString).intValue());
  }

  /**
   * Parse a floating-point latitude or longitude value.
   * 
   * @param tagString
   *          "NS" or "EW" (the first is positive, the second negative). The
   *          function call paraemter must be given in upper case. The tag in
   *          the actual parameter may be given in either case.
   * @param defaultValue
   *          A value to return in case of missing data or parse errors..
   * @return the parsed (or default) value. The format is:
   * 
   * <pre>
   * 		[N S]
   * 		[+ -] degrees.fraction
   * 		[+ -] degrees:minutes
   * 		[N S]
   * </pre>
   * 
   * Only one [N S] may be specified.
   */
  public double parseLatLong(String tagString) throws NumberFormatException {
    char tag = 0;
    char c = Character.toUpperCase(paramString.charAt(index));
    if (c == tagString.charAt(0) || c == tagString.charAt(1)) {
      tag = c;
      index++;
    }
    double result = number();
    if (index < paramString.length()) {
      if (tag == 0) {
        c = Character.toUpperCase(paramString.charAt(index));
        if (c == tagString.charAt(0) || c == tagString.charAt(1)) {
          tag = c;
          index++;
        }
      }
    }
    if (index != paramString.length()) {
      throw new NumberFormatException(paramString);
    }
    if (tag == tagString.charAt(1)) {
      result = (-result);
    }
    return (result);
  }

  /**
   * Parse the +/- sign.
   */
  private double sign() {
    double sign = 1.0;
    // System.out.println(paramName + " (sign) at " + index + ", \"" +
    // paramString.substring(index) + "\"");
    if (index < paramString.length()) {
      if (paramString.charAt(index) == '-') {
        sign = -1.0;
        index++;
      } else if (paramString.charAt(index) == '+') {
        index++;
      }
    }
    return (sign);
  }
}
