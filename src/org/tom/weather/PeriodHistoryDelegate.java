/*
 * PeriodHistoryDelegate.java
 *
 * Created on January 5, 2001, 10:48 AM
 */
package org.tom.weather;

import java.io.Serializable;
import java.util.Properties;

/**
 * 
 * @author administrator
 * @version
 */
public interface PeriodHistoryDelegate extends Serializable {
  public static final int TEMP = 0;
  public static final int PRESSURE = 1;
  public static final int HUMIDITY = 2;
  public static final int DEWPOINT = 3;
  public static final int WINDCHILL = 4;
  public static final int WINDSPEED = 5;
  public static final int RAIN = 6;

  public Event getHighTemp(Period pd) throws PersistenceException;

  public Event getLowTemp(Period pd) throws PersistenceException;

  public double getAvgTemp(Period pd) throws PersistenceException;

  public Event getHighPressure(Period pd) throws PersistenceException;

  public Event getLowPressure(Period pd) throws PersistenceException;

  public double getAvgPressure(Period pd) throws PersistenceException;

  public Event getHighWindChill(Period pd) throws PersistenceException;

  public Event getLowWindChill(Period pd) throws PersistenceException;

  public double getAvgWindChill(Period pd) throws PersistenceException;

  public Event getHighDewpoint(Period pd) throws PersistenceException;

  public Event getLowDewpoint(Period pd) throws PersistenceException;

  public double getAvgDewpoint(Period pd) throws PersistenceException;

  public Event getHighHumidity(Period pd) throws PersistenceException;

  public Event getLowHumidity(Period pd) throws PersistenceException;

  public int getAvgHumidity(Period pd) throws PersistenceException;

  public int getAvgWind(Period pd) throws PersistenceException;

  public Event getHighGust(Period pd) throws PersistenceException;

  public double getRain(Period pd) throws PersistenceException;

  public void setProperties(Properties props);

  public double[] getAllAverages(Period pd) throws PersistenceException;

  public void close();

  public class PersistenceException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -4607706678616329050L;

    public PersistenceException() {
      super();
    }

    public PersistenceException(String msg) {
      super(msg);
    }
  }
}
