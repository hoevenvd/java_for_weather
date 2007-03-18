/*
 * DataUploader.java
 *
 * Created on February 1, 2001, 8:18 PM
 APRSWXNET

 This page describes random bits of information about how I hooked my weather station up to APRSWXNET. I am CW0003 -- you can
 find my data at findu.com.

 The basic idea is that you send a single record at regular intervals (I currently do this every 5 minutes). This record contains your identity
 (CW number), location and the various readings. I believe in 'document by example', so here goes:

 CW0003>APRS,TCPXX*:@241505z4220.45N/07128.59W_032/005g008t054p078r001e1w

 Field
 Meaning
 CW0003
 Your CW number
 >APRS,TCPXX*:
 Boilerplate
 @241505z
 The ddhhmm in UTC of the time that you generate the report
 4220.45N/07128.59W
 Your location. This is ddmm.hh -- i.e. degrees, minutes and hundreths of minutes. The Longitude has three
 digits of degrees and leading zero digits cannot be omitted.
 _032
 The direction of the wind from true north (in degrees).
 /005
 The average windspeed in mph
 g008
 The maximum gust windspeed in mph (over the last five minutes)
 t054
 The temperature in degrees Farenheit -- if not available, then use '...' Temperatures below zero are expressed
 as -01 to -99.
 p078
 Rain in the last 24 hours (in hundreths of an inch) -- this can be omitted
 r001
 The rain in the last 1 hour (in hundreths of an inch) -- this can be omitted
 b10245
 The barometric pressure in tenths of millbars -- this can be omitted. This is the sea-level pressure and not the
 actual pressure as measured at your weatherstation.
 h50
 The humidity in percent. '00' => 100%. -- this can be omitted
 e1w
 The equipment you are using. I'm not sure what you can put here.


 Note that most fields are fixed width. The letters are all case sensitive.

 Once you have formed this record, open a connection to port 23 on 'second.aprs.net' and then send the following:

 user CW0003 pass -1 vers linux-1wire 1.00

 (substitute your CW number), followed by a newline, followed by your data record. Then disconnect. Note that you will receive a whole
 bunch of stuff that you can ignore. I have not had much luck with first.aprs.net!

 Debugging

 The page APRS Search contains a bunch of useful search boxes that you can enter your CW number into, and then see what data has
 been received.

 Other useful resources can be found from Joining APRSWXNET. The actual APRS protocol specification is available in a PDF file. The
 information presented above actually works, and is somewhat different to what is in this document.

 Philip Gladstone
 */
package org.tom.weather.upload.ws;

import java.rmi.RemoteException;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.upload.DataUploader;
import org.tom.weather.ws.client.generated.ArchiveStruct;
import org.tom.weather.ws.client.WxWsClient;

import uk.me.jstott.jweatherstation.util.Process;

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
    struct.setPrevailing_wind_direction(entry.getWindDirection().getDegrees());
    struct.setHigh_wind_speed(entry.getWindGust());
    struct.setAverage_uv_index(entry.getAverageUVIndex());
    struct.setHigh_uv_index(entry.getHighUVIndex());
    struct.setAverage_solar_radiation(entry.getAverageSolarRadiation());
    struct.setHigh_solar_radiation(entry.getHighSolarRadiation());
    struct.setNumber_of_wind_samples(entry.getNumberOfWindSamples());
    if (entry.isValid()) {
      WxWsClient.postArchiveEntry(password, getLocation(), struct);
    }
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }
}
