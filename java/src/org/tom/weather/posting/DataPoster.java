package org.tom.weather.posting;

import java.rmi.RemoteException;
import org.tom.weather.PeriodData;
import org.tom.weather.SnapShot;

/**
 * 
 * @author administrator
 * @version
 */
public interface DataPoster {
  public void post(SnapShot snap) throws RemoteException;

  public void post(PeriodData periodData);
}
