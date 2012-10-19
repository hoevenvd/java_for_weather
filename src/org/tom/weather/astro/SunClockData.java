package org.tom.weather.astro;

/**
 * SunClockData holds the data that controls all displays.<p>
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
 * @version 1.2
 * 1996.09.27
 * Set tabs every 4 characters.
 */
import java.util.*;
import java.awt.*;
import java.text.*;

public class SunClockData extends Observable implements Constants {
  /**
   * This hack is needed to pass the applet to a dialog via an ActionListener.
   */
  SunClockApplet sunClockApplet;
  /**
   * The current instant is defined by two values: the Java date as returned by
   * the operating system and the actual timezone offset, initialized to the
   * system timezone and, potentially, overriden by the applet page or user
   * dialog.
   */
  private Date javaDate = new Date();
  private TimeZone timeZone = TimeZone.getDefault();
  /**
   * The current observer's location. North latitude and East longitude are
   * positive, South and West are negative. locationName is null if the location
   * is unknown.
   */
  private double latitude = 0.0;
  private double longitude = 0.0;
  private String locationName = "A Wet Spot in the Middle of the Atlantic Ocean";
  /**
   * Layout information (here, so it's in one place).
   */
  private Font displayFont = new Font("SansSerif", Font.PLAIN, 10);
  /**
   * This may be set true to supress the hour chime.
   */
  private boolean silentFlag = false;
  /**
   * lastMoonChange and lastSunChange are the times (Java epoch) when the object
   * was last updated. These values are not exported.
   */
  private long lastMoonChange = 0;
  private long lastSunChange = 0;
  /*
   * These parameters determine display update frequency. They are in getTime
   * values (milliseconds)
   */
  private static final long MOON_INTERVAL = 4L * 60L * 60000L; /* Four hours */
  private static final double SUN_INTERVAL = 15L * 60000L; /* 15 minutes */

  public SunClockData(SunClockApplet sunClockApplet) {
    super();
    this.sunClockApplet = sunClockApplet;
  }

  public Font getDisplayFont() {
    return (displayFont);
  }

  public String getGMTDateString() {
    return (AstroFormat.getGMTDateString(javaDate));
  }

  /**
   * Return the Java date.
   */
  public Date getJavaDate() {
    return (javaDate);
  }

  public double getLatitude() {
    return (latitude);
  }

  public String getLatLongString() {
    String locationText = ((locationName == null) ? "" : locationName + ": ")
        + AstroFormat.dm(latitude, "NS") + " "
        + AstroFormat.dm(longitude, "EW") + ", timezone  "
        + AstroFormat.timeZoneString(getTimeZoneOffset()) + " ("
        + timeZone.getID() + ")";
    return (locationText.toString());
  }

  /**
   * Return the date for the user-specified timezone
   */
  public String getLocalDateString() {
    return (AstroFormat.getLocalDateString(javaDate, timeZone));
  }

  public String getLocationName() {
    if (locationName == null)
      return ("");
    else {
      return (locationName);
    }
  }

  public double getLongitude() {
    return (longitude);
  }

  public boolean getSilentFlag() {
    return (silentFlag);
  }

  public SunClockApplet getSunClockApplet() {
    return (sunClockApplet);
  }

  /**
   * Return the selected timezone
   */
  public TimeZone getTimeZone() {
    return (timeZone);
  }

  public String getTimeZoneID() {
    return (timeZone.getID());
  }

  /**
   * Return the timezone offset in msec East of Greenwich.
   */
  public long getTimeZoneOffset() {
    return (Astro.getTimeZoneOffset(javaDate, timeZone));
  }

  public void setDate(Date javaDate) {
    this.javaDate = javaDate;
    long now = javaDate.getTime();
    int whatChanged = TIME_CHANGED;
    if ((now - lastMoonChange) >= MOON_INTERVAL) {
      whatChanged |= MOON_UPDATE_NEEDED;
      lastMoonChange = now;
    }
    if ((now - lastSunChange) >= SUN_INTERVAL) {
      whatChanged |= SUN_UPDATE_NEEDED;
      lastSunChange = now;
    }
    setChanged();
    notifyObservers(new Integer(whatChanged));
  }

  public void setDisplayFont(Font displayFont) {
    this.displayFont = displayFont;
    setChanged();
    notifyObservers(new Integer(DISPLAY_FONT_CHANGED));
  }

  /**
   * setLocation (called by SunImageCanvas)
   */
  public void setLocation(double latitude, double longitude) {
    TimeZone newZone = CityList.findNearestTimezone(latitude, longitude);
    if (newZone == null) {
      newZone = timeZone;
    }
    setLocation(null, latitude, longitude, newZone);
  }

  public void setLocation(String locationName, double latitude,
      double longitude, TimeZone timeZone) {
    this.locationName = locationName;
    this.latitude = latitude;
    this.longitude = longitude;
    if (timeZone == null) {
      timeZone = TimeZone.getDefault();
    }
    this.timeZone = timeZone;
    setChanged();
    notifyObservers(new Integer(LOCATION_CHANGED | TIME_CHANGED));
  }

  public void setSilentFlag(boolean silentFlag) {
    this.silentFlag = silentFlag;
  }

  /**
   * Change the timezone
   */
  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
    setChanged();
    notifyObservers(new Integer(TIME_CHANGED));
  }

  /**
   * The stop method will shut down all child processes. It is called from
   * stop() in the applet.
   */
  public void stop() {
    setChanged();
    notifyObservers(new Integer(STOP_NOW));
  }

  public String toString() {
    return (getLatLongString() + ", " + getLocalDateString());
  }
}
