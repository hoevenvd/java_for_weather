/*
 * Created on 12-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.datatypes;

import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * @author Jonathan Stott
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Pressure implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 8594012454101301655L;
  public static final Logger LOGGER = Logger.getLogger(Pressure.class);
  public static final int FALLING_RAPIDLY = 196;
  public static final int FALLING_SLOWLY = 236;
  public static final int STEADY = 0;
  public static final int RISING_SLOWLY = 20;
  public static final int RISING_RAPIDLY = 60;
  public static final int UNIT_IN_HG = 0;
  public static final int UNIT_MM_HG = 1;
  public static final int UNIT_MB = 2;
  public static final int UNIT_HPA = 3;
  private int pressure = 0;
  private int trend = -1;

  public Pressure(int pressure) {
    setPressure(pressure);
  }

  public Pressure(int pressure, int trend) {
    setPressure(pressure);
    setTrend(trend);
  }

  private void setTrend(int trend) {
    this.trend = trend;
  }

  public String getTrend() {
    switch (trend) {
      case -1:
        return "Not Set";
      case FALLING_RAPIDLY:
        return "Falling Rapidly";
      case FALLING_SLOWLY:
        return "Falling";
      case STEADY:
        return "Steady";
      case RISING_SLOWLY:
        return "Rising";
      case RISING_RAPIDLY:
        return "Rising Rapidly";
    }
    return "invalid";
  }

  private void setPressure(int pressure) {
    this.pressure = pressure;
  }

  public int getPressureRaw() {
    return pressure;
  }

  public double getPressureInches() {
    return ((double) pressure) / 1000D;
  }

  public double getPressureMB() {
    return ((double) pressure) / 1000D;
  }

  public String toString() {
    return Double.toString(pressure / 1000.0D) + " - " + getTrend();
  }
}
