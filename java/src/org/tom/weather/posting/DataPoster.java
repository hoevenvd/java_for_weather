package org.tom.weather.posting;

import org.tom.weather.PeriodData;
import org.tom.weather.SnapShot;

/**
 * 
 * @author administrator
 * @version
 */
public interface DataPoster {
  public void post(SnapShot snap);

  public void post(PeriodData periodData);
}
