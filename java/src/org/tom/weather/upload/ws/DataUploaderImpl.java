/*
 * DataUploader.java
 *
 * Created on February 1, 2001, 8:18 PM
 */
package org.tom.weather.upload.ws;

import java.rmi.RemoteException;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.upload.DataUploader;
import org.tom.weather.ws.client.generated.ArchiveStruct;
import org.tom.weather.ws.client.WxWsClient;

public class DataUploaderImpl implements DataUploader {
  private static final Logger LOGGER = Logger.getLogger(DataUploaderImpl.class);

  private String password;
  private String location;
  
  private DataUploaderImpl() {
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword() {
    return password;
  }

  public synchronized void upload(org.tom.weather.ArchiveEntry[] entries) {
      for (int i = 0; i < entries.length; i++) {
        try {
          uploadData(entries[i]);
        } catch (RemoteException e) {
          LOGGER.error(e);
        }
      }
  }

  private void uploadData(ArchiveEntry entry)  throws RemoteException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("uploading: " + entry);
    }
    ArchiveStruct struct = new ArchiveStruct();
    Calendar cal = Calendar.getInstance();
    cal.setTime(entry.getDate());
    struct.setLocation(getLocation());
    struct.setDate(cal);
    struct.setAverage_wind_speed(entry.getAvgWindSpeed());
    struct.setInside_temp(entry.getAvgInTemp());
    struct.setOutside_temp(entry.getAvgOutTemp());
    struct.setHigh_outside_temp(entry.getHiOutTemp());
    struct.setInside_humidity(entry.getInHumidity());
    struct.setLow_outside_temp(entry.getLowOutTemp());
    struct.setOutside_humidity(entry.getOutHumidity());
    struct.setPressure(entry.getBarometer());
    struct.setRainfall(entry.getRain());
    struct.setHigh_rain_rate(entry.getHighRainRate());
    struct.setPrevailing_wind_direction(entry.getWindDirection().getDegrees());
    struct.setHigh_wind_speed(entry.getWindGust());
    struct.setAverage_uv_index(entry.getAverageUVIndex());
    struct.setHigh_uv_index(entry.getHighUVIndex());
    struct.setAverage_solar_radiation(entry.getAverageSolarRadiation());
    struct.setHigh_solar_radiation(entry.getHighSolarRadiation());
    struct.setNumber_of_wind_samples(entry.getNumberOfWindSamples());
    if (entry.isValid()) {
      WxWsClient.postArchiveEntry(password, getLocation(), struct);
      LOGGER.info(entry);
    }
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }
}
