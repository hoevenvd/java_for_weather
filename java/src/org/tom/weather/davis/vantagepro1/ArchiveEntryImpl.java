/*
 * Created on Oct 13, 2005
 *
 */
package org.tom.weather.davis.vantagepro1;

import java.util.Date;
import java.util.TimeZone;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.Direction;

public class ArchiveEntryImpl implements ArchiveEntry {
  /**
   * 
   */
  private static final long serialVersionUID = 4791131356110457253L;

  public ArchiveEntryImpl(double inTemp, double outTemp, int avgWindSpeed,
      double pressure, Date date, double hiOutTemp, int insideHumidity,
      double lowOutTemp, int windDirection, int outsideHumidity, double rain,
      int windGust) {
  }

  public double getAvgInTemp() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getAvgOutTemp() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getHiOutTemp() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getLowOutTemp() {
    // TODO Auto-generated method stub
    return 0;
  }

  public double getRain() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getAvgWindSpeed() {
    // TODO Auto-generated method stub
    return 0;
  }

  public Direction getWindDirection() {
    // TODO Auto-generated method stub
    return null;
  }

  public int getNativeWindDirection() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getWindGust() {
    // TODO Auto-generated method stub
    return 0;
  }

  public Date getDate() {
    // TODO Auto-generated method stub
    return null;
  }

  public double getBarometer() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getInHumidity() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getOutHumidity() {
    // TODO Auto-generated method stub
    return 0;
  }

  public Date getDate(TimeZone tz) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public boolean isValid() {
    return true;
  }

  public int getAverageUVIndex() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getHighUVIndex() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getAverageSolarRadiation() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getHighSolarRadiation() {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getNumberOfWindSamples() {
    return 0;
  }
}
