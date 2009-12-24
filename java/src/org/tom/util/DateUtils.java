/*
 * DateUtils.java
 *
 * Created on January 27, 2001, 5:58 PM
 */
package org.tom.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import uk.me.jstott.jweatherstation.util.UnsignedByte;

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

  public static Calendar parseDate(UnsignedByte dateLSB, UnsignedByte dateMSB,
      UnsignedByte timeLSB, UnsignedByte timeMSB) {
    Calendar cal = Calendar.getInstance(); // today by default
    cal.set(Calendar.MILLISECOND, 0);

    if (dateLSB != null && dateMSB != null) {
      int year, month, date;

      year = (dateMSB.getByte() >> 1) + 2000;
      month = ((dateMSB.getByte() & 1) << 3) | (dateLSB.getByte() >> 5);
      date = dateLSB.getByte() & 31;
      cal.set(year, month - 1, date);
    }

    int timeInt = (timeMSB.getByte() * 256) + timeLSB.getByte();
    int hours = timeInt / 100;
    int mins = timeInt - (hours * 100);
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hours, mins, 0);

    return cal;
  }

}
