/*
 * PeriodDataImpl.java
 *
 * Created on January 5, 2001, 1:10 PM
 */
package org.tom.weather;

import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * 
 * @author administrator
 * @version
 */
public class PeriodDataImpl implements PeriodData {
  /**
   * 
   */
  private static final long serialVersionUID = 6178427332644208807L;
  /**
   * 
   */
  // private static final long serialVersionUID = -4801131814086459425L;
  private Period pd;
  private Event highTemp;
  private Event lowTemp;
  private double avgTemp;
  private Event highPressure;
  private Event lowPressure;
  private double avgPressure;
  private double rain;
  private Event highGust;
  private int avgWind;
  private int avgHumidity;
  private Event highHumidity;
  private Event lowHumidity;
  private Event highDewpoint;
  private Event lowDewpoint;
  private double avgDewpoint;
  private Event highWindChill;
  private Event lowWindChill;
  private double avgWindChill;
  private Date asOfDate;
  private static Logger logger;
  static {
    logger = Logger.getLogger(PeriodDataImpl.class);
  }

  /** Creates new PeriodDataImpl */
  public PeriodDataImpl(Period period, Properties props)
      throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, PeriodHistoryDelegate.PersistenceException {
    pd = period;
    PeriodHistoryDelegate persistenceDelegate = (PeriodHistoryDelegate) Class
        .forName(props.getProperty(Constants.PERIOD_DELEGATE_KEY))
        .newInstance();
    persistenceDelegate.setProperties(props);
    // avgPressure = persistenceDelegate.getAvgPressure(pd);
    // avgDewpoint = persistenceDelegate.getAvgDewpoint(pd);
    // avgWindChill = persistenceDelegate.getAvgWindChill(pd);
    // avgHumidity = persistenceDelegate.getAvgHumidity(pd);
    // avgTemp = persistenceDelegate.getAvgTemp(pd);
    // avgWind = persistenceDelegate.getAvgWind(pd);
    // rain = persistenceDelegate.getRain(pd);
    double averages[] = persistenceDelegate.getAllAverages(pd);
    avgTemp = averages[PeriodHistoryDelegate.TEMP];
    avgWind = (int) averages[PeriodHistoryDelegate.WINDSPEED];
    rain = averages[PeriodHistoryDelegate.RAIN];
    avgHumidity = (int) averages[PeriodHistoryDelegate.HUMIDITY];
    avgWindChill = averages[PeriodHistoryDelegate.WINDCHILL];
    avgDewpoint = averages[PeriodHistoryDelegate.DEWPOINT];
    avgPressure = averages[PeriodHistoryDelegate.PRESSURE];
    highTemp = persistenceDelegate.getHighTemp(pd);
    lowTemp = persistenceDelegate.getLowTemp(pd);
    highGust = persistenceDelegate.getHighGust(pd);
    highHumidity = persistenceDelegate.getHighHumidity(pd);
    lowHumidity = persistenceDelegate.getLowHumidity(pd);
    highWindChill = persistenceDelegate.getHighWindChill(pd);
    lowWindChill = persistenceDelegate.getLowWindChill(pd);
    highDewpoint = persistenceDelegate.getHighDewpoint(pd);
    lowDewpoint = persistenceDelegate.getLowDewpoint(pd);
    highPressure = persistenceDelegate.getHighPressure(pd);
    lowPressure = persistenceDelegate.getLowPressure(pd);
    persistenceDelegate.close();
    asOfDate = new Date();
    logger.info("** Refreshed: " + pd.toShortString());
  }

  public Event getHighHumidity()
      throws PeriodHistoryDelegate.PersistenceException {
    return highHumidity;
  }

  public Event getLowHumidity()
      throws PeriodHistoryDelegate.PersistenceException {
    return lowHumidity;
  }

  public int getAvgHumidity() throws PeriodHistoryDelegate.PersistenceException {
    return avgHumidity;
  }

  public int getAvgWind() throws PeriodHistoryDelegate.PersistenceException {
    return avgWind;
  }

  public Event getHighGust() throws PeriodHistoryDelegate.PersistenceException {
    return highGust;
  }

  public double getRain() throws PeriodHistoryDelegate.PersistenceException {
    return rain;
  }

  public boolean isStale() {
    return (System.currentTimeMillis() > pd.getEnd().getTime());
  }

  public Event getHighTemp() throws PeriodHistoryDelegate.PersistenceException {
    return highTemp;
  }

  public Event getLowTemp() throws PeriodHistoryDelegate.PersistenceException {
    return lowTemp;
  }

  public double getAvgTemp() throws PeriodHistoryDelegate.PersistenceException {
    return avgTemp;
  }

  public Event getHighPressure()
      throws PeriodHistoryDelegate.PersistenceException {
    return highPressure;
  }

  public Event getLowPressure()
      throws PeriodHistoryDelegate.PersistenceException {
    return lowPressure;
  }

  public double getAvgPressure()
      throws PeriodHistoryDelegate.PersistenceException {
    return avgPressure;
  }

  public Period getPeriod() {
    return pd;
  }

  public Date getAsOfDate() {
    return asOfDate;
  }

  public double getAvgWindChill()
      throws PeriodHistoryDelegate.PersistenceException {
    return avgWindChill;
  }

  public Event getHighWindChill()
      throws PeriodHistoryDelegate.PersistenceException {
    return highWindChill;
  }

  public Event getLowWindChill()
      throws PeriodHistoryDelegate.PersistenceException {
    return lowWindChill;
  }

  public Event getHighDewpoint()
      throws PeriodHistoryDelegate.PersistenceException {
    return highDewpoint;
  }

  public Event getLowDewpoint()
      throws PeriodHistoryDelegate.PersistenceException {
    return lowDewpoint;
  }

  public double getAvgDewpoint()
      throws PeriodHistoryDelegate.PersistenceException {
    return avgDewpoint;
  }

  public String getKey() {
    return Constants.PERIOD_KEY + getPeriod().getType();
  }

  public String toString() {
    return "Period Data for " + pd.toString();
  }
}
