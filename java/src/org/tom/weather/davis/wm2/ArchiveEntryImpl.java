package org.tom.weather.davis.wm2;

import java.util.*;
import java.text.*;
import org.apache.log4j.Logger;
import org.tom.weather.BaseArchiveEntryImpl;
import org.tom.weather.Direction;

public class ArchiveEntryImpl extends BaseArchiveEntryImpl implements
    org.tom.weather.ArchiveEntry {
  /**
   * 
   */
  private static final long serialVersionUID = 6325890235352573713L;
  static final int ARCHIVE_RECORD_SIZE = 21;
  int avgInTemp;
  int avgOutTemp;
  int hiOutTemp;
  int lowOutTemp;
  int rain;
  int avgWindSpeed;
  int windDirection;
  int windGust;
  int month;
  int day;
  int year;
  int hour;
  int minute;
  int barometer;
  int inHumidity;
  int outHumidity;
  byte[] originalImage;
  private static Logger logger;
  static {
    logger = Logger.getLogger(ArchiveEntryImpl.class);
  }

  public ArchiveEntryImpl(byte[] image) {
    Integer tempInt;
    originalImage = image;
    byte[] intBuffer = new byte[2];
    intBuffer[0] = image[0];
    intBuffer[1] = image[1];
    barometer = getUnsigned(intBuffer);
    inHumidity = (int) image[2];
    outHumidity = (int) image[3];
    intBuffer[0] = image[4];
    intBuffer[1] = image[5];
    rain = getUnsigned(intBuffer);
    intBuffer[0] = image[6];
    intBuffer[1] = image[7];
    avgInTemp = getInt(intBuffer);
    avgInTemp += (SnapShot.getInsideTempCAL() * 10);
    intBuffer[0] = image[8];
    intBuffer[1] = image[9];
    avgOutTemp = getInt(intBuffer);
    avgOutTemp += (SnapShot.getOutsideTempCAL() * 10);
    avgWindSpeed = (int) image[10];
    windDirection = (int) image[11];
    intBuffer[0] = image[12];
    intBuffer[1] = image[13];
    hiOutTemp = getInt(intBuffer);
    hiOutTemp += (SnapShot.getOutsideTempCAL() * 10);
    intBuffer[0] = image[19];
    intBuffer[1] = image[20];
    lowOutTemp = getInt(intBuffer);
    lowOutTemp += (SnapShot.getOutsideTempCAL() * 10);
    windGust = (int) image[14];
    year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    month = image[18] & 0xf;
    tempInt = new Integer((int) image[17]);
    day = new Integer(Integer.toHexString(tempInt.intValue())).intValue();
    tempInt = new Integer((int) image[15]);
    hour = new Integer(Integer.toHexString(tempInt.intValue())).intValue();
    tempInt = new Integer((int) image[16]);
    minute = new Integer(Integer.toHexString(tempInt.intValue())).intValue();
  }

  public int getInt(byte[] data) {
    int adder;
    int addee;
    int count;
    addee = (data[1] << 8);
    if ((int) data[0] < 0) {
      adder = (data[0]);
      adder += 256;
    } else {
      adder = data[0];
    }
    addee += adder;
    return addee;
  }

  public int getUnsigned(byte[] cal) {
    int adder;
    int addee;
    int count;
    addee = (cal[1] << 8);
    if ((int) cal[0] < 0) {
      adder = (cal[0]);
      adder += 256;
    } else {
      adder = cal[0];
    }
    addee += adder;
    return addee;
  }

  public static ArchiveEntryImpl[] parseByteArray(byte[] bytes) {
    Vector entries = new Vector();
    byte[] buffer = new byte[ARCHIVE_RECORD_SIZE];
    int recordCount = bytes.length / ARCHIVE_RECORD_SIZE;
    for (int i = 0; i < recordCount; i++) {
      for (int j = ARCHIVE_RECORD_SIZE * i, k = 0; j < (ARCHIVE_RECORD_SIZE * i)
          + ARCHIVE_RECORD_SIZE; j++) {
        buffer[k++] = bytes[j];
      }
      ArchiveEntryImpl e = new ArchiveEntryImpl(buffer);
      entries.addElement(e);
    }
    return (ArchiveEntryImpl[]) entries.toArray(new ArchiveEntryImpl[0]);
  }

  public double getAvgInTemp() {
    return avgInTemp / 10.0;
  }

  public double getAvgOutTemp() {
    return avgOutTemp / 10.0;
  }

  public double getHiOutTemp() {
    return hiOutTemp / 10.0;
  }

  public double getLowOutTemp() {
    return lowOutTemp / 10.0;
  }

  public double getRain() {
    return rain / 100.0;
  }

  public int getAvgWindSpeed() {
    return avgWindSpeed;
  }

  public Direction getWindDirection() {
    return new Direction((short) windDirection);
  }

  public int getNativeWindDirection() {
    return windDirection;
  }

  public int getWindGust() {
    return windGust;
  }

  public Date getDate() {
    return getDate(TimeZone.getDefault());
  }

  public Date getDate(TimeZone tz) {
    Calendar cal = Calendar.getInstance(tz);
    cal.setTime(new java.util.Date());
    cal.set(Calendar.DATE, day);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  public double getBarometer() {
    return barometer / 1000.0;
  }

  public int getInHumidity() {
    return inHumidity;
  }

  public int getOutHumidity() {
    // A fix for a flaw in the humidity-equipment of the wm2. When a value of
    // greater than a 100% is read from the archive, it sure must be a 100%
    // Another fix for realtime-measures (snapshots) is put into WeatherMonitor
    if (outHumidity > 100) {
      outHumidity = 100;
    }
    return outHumidity;
  }

  public void toConsole() {
    StringBuffer sb = new StringBuffer();
    sb.append(month + "/" + day + " " + hour + ":" + minute);
    sb.append("\tavgInTemp: " + avgInTemp / 10.0f);
    sb.append("\tavgOutTemp: " + avgOutTemp / 10.0f);
    sb.append("\thiOutTemp: " + hiOutTemp / 10.0f);
    sb.append("\tlowOutTemp: " + lowOutTemp / 10.0f);
    sb.append("\train: " + rain / 100.0f);
    sb.append("\tavgWindSpeed: " + avgWindSpeed);
    sb.append("\twindDirection: " + windDirection);
    sb.append("\twindGust: " + windGust);
    sb.append("\tpressure: " + barometer / 1000.0f);
    sb.append("\tinHumidity: " + inHumidity);
    sb.append("\toutHumidity: " + outHumidity);
    logger.info(sb.toString());
  }
}
