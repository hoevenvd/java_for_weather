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

  public String getBarStatus();

  public float getDewpoint();

  public float getOutsideTemp();

  public int getWindspeed();

  public Direction getWindDirection();

  public java.util.Date getDate();
  
  public boolean isRaining();
  
  public double getRainRate();

  public float getWindchill();
  
  public boolean isValid();

  public float getDayRain();
  
  public int getTenMinAvgWind();
}
