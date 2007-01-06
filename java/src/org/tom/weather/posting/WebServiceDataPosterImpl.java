package org.tom.weather.posting;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.tom.weather.PeriodData;
import org.tom.weather.SnapShot;
import org.tom.weather.ws.client.generated.InputSampleStruct;
import org.tom.weather.ws.client.WxWsClient;

public class WebServiceDataPosterImpl implements DataPoster {

  private static final Logger LOGGER = Logger.getLogger(WebServiceDataPosterImpl.class);
  private String password;
  private String location;
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword() {
    return password;
  }

  public void post(SnapShot snap) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(snap.getDate());
    InputSampleStruct sample = new InputSampleStruct();
    sample.setTemp((double)snap.getOutsideTemp());
    sample.setWind_direction(snap.getWindDirection().getDegrees());
    sample.setSample_date(cal);
    sample.setPressure((double)snap.getPressure());
    sample.setBar_status(snap.getBarStatus());
    sample.setRain_rate(snap.getRainRate());
    sample.setWindspeed(snap.getWindspeed());
    sample.setHumidity((int)snap.getOutsideHumidity());
    
    try {
      WxWsClient.postSample(password, location, sample);
    } catch (RemoteException e) {
      LOGGER.error(e);
    }
  }

  public void post(PeriodData periodData) {
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }

}
