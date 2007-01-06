/*
 * PeriodTimerController.java
 *
 * Created on January 23, 2001, 1:32 PM
 */
package org.tom.weather;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// fix up how the period data are accessed.
// index by period type
public class PeriodTimerController {
  static PeriodTimerController instance;
  Properties props;
  static Map timers = new HashMap();

  public PeriodDataTimer addTimer(Period pd) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException,
      PeriodHistoryDelegate.PersistenceException {
    return addTimer(pd, -1L);
  }

//  @SuppressWarnings("unchecked")
  public PeriodDataTimer addTimer(Period pd, long ttl)
      throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, PeriodHistoryDelegate.PersistenceException {
    PeriodDataTimer timer = new PeriodDataTimer(pd, props, ttl);
    timer.start();
    timers.put(new Integer(pd.getType()), timer);
    return timer;
  }

  /** gets singleton PeriodTimerController */
  synchronized public static PeriodTimerController getInstance(Properties props) {
    if (instance == null) {
      instance = new PeriodTimerController();
      instance.props = props;
    }
    return instance;
  }

  private PeriodTimerController() {
  }

  public static PeriodData getData(Period pd) {
    PeriodDataTimer timer = (PeriodDataTimer) timers.get(new Integer(pd
        .getType()));
    return timer == null ? null : timer.getData();
  }
}
