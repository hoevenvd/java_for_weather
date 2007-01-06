/*
 * PeriodData.java
 *
 * Created on January 5, 2001, 12:51 PM
 */
package org.tom.weather;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author administrator
 * @version
 */
public interface PeriodData extends Serializable {
  public Event getHighTemp() throws PeriodHistoryDelegate.PersistenceException;

  public Event getLowTemp() throws PeriodHistoryDelegate.PersistenceException;

  public double getAvgTemp() throws PeriodHistoryDelegate.PersistenceException;

  public Event getHighPressure()
      throws PeriodHistoryDelegate.PersistenceException;

  public Event getLowPressure()
      throws PeriodHistoryDelegate.PersistenceException;

  public double getAvgPressure()
      throws PeriodHistoryDelegate.PersistenceException;

  public Event getHighHumidity()
      throws PeriodHistoryDelegate.PersistenceException;

  public Event getLowHumidity()
      throws PeriodHistoryDelegate.PersistenceException;

  public int getAvgHumidity() throws PeriodHistoryDelegate.PersistenceException;

  public int getAvgWind() throws PeriodHistoryDelegate.PersistenceException;

  public Event getHighGust() throws PeriodHistoryDelegate.PersistenceException;

  public Event getHighWindChill()
      throws PeriodHistoryDelegate.PersistenceException;

  public Event getLowWindChill()
      throws PeriodHistoryDelegate.PersistenceException;

  public double getAvgWindChill()
      throws PeriodHistoryDelegate.PersistenceException;

  public Event getHighDewpoint()
      throws PeriodHistoryDelegate.PersistenceException;

  public Event getLowDewpoint()
      throws PeriodHistoryDelegate.PersistenceException;

  public double getAvgDewpoint()
      throws PeriodHistoryDelegate.PersistenceException;

  public double getRain() throws PeriodHistoryDelegate.PersistenceException;

  public boolean isStale();

  public Date getAsOfDate();

  public Period getPeriod();

  public String getKey();
}
