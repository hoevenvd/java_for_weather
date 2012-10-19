/*
 * Created on 12-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.datatypes;

import java.io.Serializable;

/**
 * @author Jonathan Stott
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Humidity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -5390150035972966077L;
  private int humidity = 0;

  public Humidity(int humidity) {
    this.humidity = humidity;
  }

  public int getHumidity() {
    return humidity;
  }

  public String toString() {
    return Integer.toString(humidity);
  }
}
