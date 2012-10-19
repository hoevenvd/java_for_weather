package org.tom.weather.astro;

/**
 * CityObject contains the information needed to locate a city.
 * <p>
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
import java.awt.*;

public class CityObject {
  public double latitude;
  public double longitude;
  public int timezoneOffset;
  public String timeZoneID;

  CityObject(double latitude, double longitude, int timezoneOffset,
      String timeZoneID) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.timezoneOffset = timezoneOffset;
    this.timeZoneID = timeZoneID;
  }

  public String toString() {
    return (latitude + ", " + longitude + ", " + timezoneOffset + ", " + timeZoneID);
  }
}
