/*
 * DataUploader.java
 *
 * Created on February 1, 2001, 8:18 PM
 */
package org.tom.weather.upload.gae;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.Cacheable;
import org.tom.weather.upload.DataUploader;
//import org.tom.weather.ws.client.generated.ArchiveStruct;
import org.tom.weather.ws.client.WxWsClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataUploaderImpl implements DataUploader, Cacheable {
  private static final Logger LOGGER = Logger.getLogger(DataUploaderImpl.class);

  private String password;
  private String location;
  private String station;
  private String uploadUrl;
  private String lastDateTarget;
  
  private DataUploaderImpl() {
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword() {
    return password;
  }

  public synchronized void upload(org.tom.weather.ArchiveEntry[] entries) throws Exception {
    for (int i = 0; i < entries.length; i++) {
      uploadData(entries[i]);
    }
  }

  private void uploadData(ArchiveEntry entry) throws Exception {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    String json = gson.toJson(entry);
    if (LOGGER.isDebugEnabled()) {
        //LOGGER.debug("uploading: " + json);
    }
    if (LOGGER.isInfoEnabled()) {
        LOGGER.info(entry);
    }
    sendReceiveData(getUploadUrl(), gson.toJson(new PutArchiveRequestEnvelope(entry.getDate(), location, password, json, station)));

    /*
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
    struct.setDirection_of_high_wind_speed(entry.getHighWindSpeedDirection().getDegrees());
    struct.setAverage_uv_index(entry.getAverageUVIndex());
    struct.setHigh_uv_index(entry.getHighUVIndex());
    struct.setAverage_solar_radiation(entry.getAverageSolarRadiation());
    struct.setHigh_solar_radiation(entry.getHighSolarRadiation());
    struct.setNumber_of_wind_samples(entry.getNumberOfWindSamples());
    if (entry.isValid()) {
      WxWsClient.postArchiveEntry(password, getLocation(), struct);
      LOGGER.info(entry);
    }
    */
  }

	public void setLocation(String location) {
	  this.location = location;
	}
	
	public String getLocation() {
	  return location;
	}
	
	public void resetCache() throws Exception {
	}
	
	public void setStation(String station) {
		this.station = station;
	}
		
	public String getStation() {
		return station;
	}

	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}

	public String getUploadUrl() {
		return uploadUrl;
	}

	public String sendReceiveData(String target, String content) {
		    StringBuffer response = new StringBuffer();
		    try {
		      URL url = new URL(target);
		      if (LOGGER.isDebugEnabled()) {
		    	  LOGGER.debug("sending to: " + url.toString());
		    	  LOGGER.debug("sending: " + content);
		      }
		      URLConnection conn = url.openConnection();
		      // Set connection parameters.
		      conn.setDoInput (true);
		      conn.setDoOutput (true);
		      conn.setUseCaches (false);
		      // Make server believe we are form data...
		      conn.setRequestProperty("Content-Type", "application/json");
		      DataOutputStream out = new DataOutputStream (conn.getOutputStream ());
		      // Write out the bytes of the content string to the stream.
		      out.writeBytes(content);
		      out.flush ();
		      out.close ();
		      // Read response from the input stream.
		      BufferedReader in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
		      String temp;
		      while ((temp = in.readLine()) != null){
			    	LOGGER.debug("received: [" + temp + "]");
		        response.append(temp);
		    	LOGGER.debug("response so far: [" + response + "]");
		       }
		      temp = null;
		      in.close ();
		    } catch (Exception e) {
		      LOGGER.error("** Exception caught:", e);
		    }
		    LOGGER.debug("[" + response + "]");
		    return response.toString();
		  }
	
	  class PutArchiveRequestEnvelope {
		  	private final String date;
		    private final String location;
		    private final String password;
		    private final String json;
		    private final String station;

		    PutArchiveRequestEnvelope(Date date, String location, String password, String json, String station) {
		      this.date = getDateString(date);
		      this.location = location;
		      this.password = password;
		      this.json = json;
		      this.station = station;
		    }
	  }

	  class GetLastArchiveRequestEnvelope {
		    private final String location;
		    private final String password;

		    GetLastArchiveRequestEnvelope(String location, String password) {
		      this.location = location;
		      this.password = password;
		    }
	  }

	  class GetLastArchiveResponseEnvelope {
		    private final String location;
		    private final String date;

		    GetLastArchiveResponseEnvelope(String location, String date) {
		      this.location = location;
		      this.date = date;
		    }

			public String getLastDate() {
				return date;
			}
			
			public String toString() {
				return "[" + this.getClass() + "]" +
				"[" + this.location  + "]" + "[" + this.date + "]";
			}
	  }

	@Override
	public Date getLatestArchiveRecord() {
	    Gson gson = new GsonBuilder().serializeNulls().create();
		String resp = 
			sendReceiveData(getLastDateTarget(), gson.toJson(new GetLastArchiveRequestEnvelope(location, password)));
		LOGGER.debug("[" + resp + "]");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		GetLastArchiveResponseEnvelope response = gson.fromJson(resp, GetLastArchiveResponseEnvelope.class);
		if (response == null) {
			LOGGER.error("null returned from getLastArchiveRecord()");
		}
		LOGGER.debug(response);
	    try {
	    	Date returnDate = (Date)formatter.parse(response.date);
	    	LOGGER.debug("returning: " + returnDate.toGMTString());
			return new Date(returnDate.getTime() + returnDate.getTimezoneOffset()); 
		} catch (ParseException e1) {
			LOGGER.error("", e1);
			return (Date)null;
		}

		//return new Date(new Date().getTime() - 360 * 1000L);
	}

	public void setLastDateTarget(String lastDateTarget) {
		this.lastDateTarget = lastDateTarget;
	}

	public String getLastDateTarget() {
		return lastDateTarget;
	}

	static String getDateString(Date date) {
		TimeZone gmt = TimeZone.getTimeZone("UTC");
    	DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    	formatter.setTimeZone(gmt);
    	return formatter.format(date);
	}

}
