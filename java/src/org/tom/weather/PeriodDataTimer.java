/*
 * PeriodDataTimer.java
 *
 * Created on January 23, 2001, 11:10 AM
 */
package org.tom.weather;

import org.apache.log4j.Logger;
import org.tom.weather.posting.DataPoster;
import java.util.*;
import javax.naming.*;

/**
 * 
 * @author administrator
 * @version
 */
public class PeriodDataTimer extends Thread {
  /** Creates new PeriodDataTimer */
  // need period, delegate class name and props
  PeriodData data = null;
  Properties props = null;
  Period period;
  Context ctx = null;
  long expire = -1L;
  DataPoster[] dataPosters = null;
  int MAX_DELEGATES = 10;
  private static Logger logger;
  static {
    logger = Logger.getLogger(PeriodDataTimer.class);
  }

  public PeriodData getPeriodData() {
    return data;
  }

  public Period getPeriod() {
    return period;
  }

  public PeriodDataTimer(Period period, Properties props)
      throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, PeriodHistoryDelegate.PersistenceException {
    this.props = props;
    this.period = period;
  }

  public PeriodDataTimer(Period period, Properties props, long expire)
      throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, PeriodHistoryDelegate.PersistenceException {
    this(period, props);
    this.expire = expire;
  }

  public void run() {
    long sleepTime;
    long periodLength;
    while (true) {
      try {
        data = new PeriodDataImpl(period, props);
        writeToContext();
        if (expire != -1) {
          sleepTime = expire;
        } else {
          periodLength = period.getEnd().getTime()
              - period.getStart().getTime();
          sleepTime = period.getEnd().getTime() + periodLength
              - System.currentTimeMillis();
        }
        if (sleepTime <= 0) {
          logger.warn(new Date() + " - Negative sleep time for period: "
              + period.toShortString() + " (" + period.toLongString() + ")"
              + ": " + sleepTime);
        } else {
          sleep(sleepTime);
        }
        period = new Period(period.getType());
      } catch (InterruptedException i) {
        logger.error("New Data... waking up thread: " + period.toShortString(),
            i);
        period = new Period(period.getType());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void writeToContext() {
    if (dataPosters == null) {
      dataPosters = org.tom.weather.davis.wm2.WeatherMonitor
          .getDataPosters(props);
    }
    if (dataPosters != null) {
      for (int i = 0; i < MAX_DELEGATES; i++) {
        if (dataPosters[i] != null) {
          dataPosters[i].post(data);
        }
      }
    }
  }

  public PeriodData getData() {
    return data;
  }
}
