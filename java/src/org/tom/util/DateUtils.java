/*
 * DateUtils.java
 *
 * Created on January 27, 2001, 5:58 PM
 */
package org.tom.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
  static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
  static SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");

  public static String getTime(Date d) {
    return timeFormat.format(d);
  }

  public static String getShortTime(Date d) {
    return shortTimeFormat.format(d);
  }

  public static Date getDateUtc(Date d) {
    int offset = TimeZone.getDefault().getOffset(d.getTime());
    return new Date(d.getTime() - offset);
  }
}
