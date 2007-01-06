/*
 * MutableArchiveEntry.java
 *
 * Created on February 2, 2001, 7:28 PM
 */
package org.tom.weather.davis.wm2;

import java.util.*;

/**
 * 
 * @author administrator
 * @version
 */
public class MutableArchiveEntry extends ArchiveEntryImpl {
  /** Creates new MutableArchiveEntry */
  public MutableArchiveEntry(byte[] b) {
    super(b);
  }

  public void setAvgInTemp(double in) {
    avgInTemp = (int) (in * 10);
  }

  public void setAvgOutTemp(double in) {
    avgOutTemp = (int) (in * 10);
  }

  public void setHiOutTemp(double in) {
    hiOutTemp = (int) (in * 10);
  }

  public void setLowOutTemp(double in) {
    lowOutTemp = (int) (in * 10);
  }

  public void setRain(double in) {
    lowOutTemp = (int) (in * 100);
  }

  public void setAvgWindSpeed(int in) {
    avgWindSpeed = in;
  }

  public void setWindDirection(int in) {
    windDirection = in;
  }

  public void setWindGust(int in) {
    windGust = in;
  }

  public void setDate(Date in) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(in);
    day = cal.get(Calendar.DATE);
    month = cal.get(Calendar.MONTH) + 1;
    year = cal.get(Calendar.YEAR);
    hour = cal.get(Calendar.HOUR_OF_DAY);
    minute = cal.get(Calendar.MINUTE);
  }

  public void setBarometer(double in) {
    barometer = (int) (in * 1000);
  }

  public void setInHumidity(int in) {
    inHumidity = in;
  }

  public void setOutHumidity(int in) {
    outHumidity = in;
  }

  public void toConsole() {
    super.toConsole();
  }
}
