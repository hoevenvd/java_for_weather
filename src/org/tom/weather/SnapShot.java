/*
 * SnapShot.java
 *
 * Created on February 6, 2001, 3:10 PM
 */
package org.tom.weather;

import java.io.Serializable;

/**
 * 
 * @author administrator
 * @version
 */
public interface SnapShot extends Serializable {
  public float getPressure();

  public float getOutsideHumidity();

  public int getInsideHumidity();

  public String getBarStatus();

  public float getDewpoint();

  public float getOutsideTemp();

  public float getInsideTemp();

  public int getWindspeed();

  public Direction getWindDirection();

  public java.util.Date getDate();
  
  public boolean isRaining();
  
  public double getRainRate();

  public double getStormRain();

  public float getWindchill();
  
  public boolean isValid();

  public float getDayRain();

  public float getMonthRain();

  public float getYearRain();

  public int getTenMinAvgWind();
  
  public int getUV();
  
  public int getSolarRadiation();

  public java.util.Date getSunrise();

  public java.util.Date getSunset();

  public int getExtraTemp1();
}
