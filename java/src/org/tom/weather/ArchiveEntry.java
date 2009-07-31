/*
 * ArchiveEntry.java
 *
 * Created on February 6, 2001, 3:15 PM
 */
package org.tom.weather;

import java.util.Date;
import java.io.Serializable;
import java.util.TimeZone;

/**
 * 
 * @author administrator
 * @version
 */
public interface ArchiveEntry extends Serializable {
  public double getAvgInTemp();

  public double getAvgOutTemp();

  public double getHiOutTemp();

  public double getLowOutTemp();

  public double getRain();

  public double getHighRainRate();

  public int getAvgWindSpeed();

  public Direction getWindDirection();

  // public int getNativeWindDirection();
  public int getWindGust();

  public Date getDate();

  public double getBarometer();

  public int getInHumidity();

  public int getOutHumidity();

  public Date getDate(TimeZone tz);
  
  public boolean isValid();

  public int getAverageUVIndex();

  public int getHighUVIndex();
  
  public int getAverageSolarRadiation();

  public int getHighSolarRadiation();
  
  public int getNumberOfWindSamples();
}
