/*
 * DataUploader.java
 *
 * Created on February 1, 2001, 8:18 PM
 */
package org.tom.weather.upload.wunderground;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tom.util.DateUtils;
import org.tom.util.Rounding;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.Converter;
import org.tom.weather.Period;
import org.tom.weather.PeriodData;
import org.tom.weather.upload.DataUploader;

import uk.me.jstott.jweatherstation.sql.SQLManager;

public class DataUploaderImpl implements DataUploader {
  private static final Logger LOGGER = Logger.getLogger(DataUploaderImpl.class);
  TimeZone tz = null;
  private int interval;
  private boolean enabled = false;
  private String url;
  private String stationId;
  private String password;
  private Date lastUpload = new Date();
  private Vector hourlyRainEntries = new Vector();
  private int windGust;
  private int numWindSamples;
  private int windSum;

  // private Properties props = null;
  /** Creates new DataUploader */
  public DataUploaderImpl() {
    resetAccumulators();
  }

  public DataUploaderImpl(String url, boolean enabled, String station,
      String password, int interval) {
    this.url = url;
    this.enabled = enabled;
    this.stationId = station;
    this.password = password;
    this.interval = interval;
    resetAccumulators();
  }

  // private DataUploaderImpl(Properties props) {
  // this.props = props;
  // tz = TimeZone.getTimeZone(props.getProperty(Constants.TIMEZONE));
  // interval =
  // new Integer(props.getProperty(Constants.WUNDERGROUND_INTERVAL))
  // .intValue();
  // }
  //	
  // public static void setProperties(Properties props) {
  // if (singleton == null) {
  // singleton = new DataUploaderImpl(props);
  // }
  // if (new
  // Boolean(props.getProperty(Constants.WUNDERGROUND_ENABLE)).booleanValue() ==
  // true) {
  // enabled = true;
  // } else {
  // enabled = false;
  // }
  // }
  //	
  // public static synchronized DataUploaderImpl getInstance() {
  // if (singleton == null) {
  // String msg = "illegal call - not initialized";
  // LOGGER.error("getInstance() called before getInstance(Properties)");
  // throw new IllegalStateException();
  // } else {
  // return singleton;
  // }
  // }
  // http://weatherstation.wunderground.com/weatherstation/updateweatherstation.php
  // \
  // ?ID=KCASANFR5&PASSWORD=sunfish&dateutc=2000-01-01+10%3A32%3A35&winddir=230&
  // \
  // windspeedmph=12&windgustmph=12&tempf=70&rainin=0&baromin=29.1&dewptf=68.2&
  // \
  // humidity=90&weather=&clouds=&softwaretype=vws%20versionxx&action=updateraw
  private String constructQuery(org.tom.weather.ArchiveEntry entry) {
    StringBuffer sb = new StringBuffer();
    String query = null;
    double hourlyRain = getHourlyRain(entry);
    PeriodData thisHourData;
    Period thisHour = new Period(Period.THIS_HOUR);
    LOGGER.debug("rain = " + hourlyRain);
    try {
      sb.append("?ID=");
      sb.append(URLEncoder.encode(stationId, "UTF-8"));
      sb.append("&PASSWORD=");
      sb.append(URLEncoder.encode(password, "UTF-8"));
      java.util.Date d = DateUtils.getDateUtc(entry.getDate());
      sb.append("&dateutc=" + URLEncoder.encode(SQLManager.getSqlDate(d), "UTF-8"));
      sb.append("&winddir="
          + URLEncoder.encode(
              new Integer(entry.getWindDirection().getDegrees()).toString(),
              "UTF-8"));
      sb.append("&windspeedmph="
          + URLEncoder.encode(new Integer(windSum / numWindSamples).toString(),
              "UTF-8"));
      sb.append("&windgustmph="
          + URLEncoder.encode(new Integer(windGust).toString(), "UTF-8"));
      sb.append("&tempf="
          + URLEncoder.encode(Rounding.toString(new Double(entry
              .getAvgOutTemp()).doubleValue(), 1), "UTF-8"));
      sb.append("&rainin="
          + URLEncoder.encode(Rounding.toString(hourlyRain, 2), "UTF-8"));
      sb.append("&baromin="
          + URLEncoder.encode(Rounding.toString(entry.getBarometer(), 3),
              "UTF-8"));
      sb.append("&dewptf="
          + URLEncoder.encode(Rounding.toString(new Double(Converter
              .getDewpoint((float) entry.getOutHumidity(), (float) entry
                  .getAvgOutTemp())).doubleValue(), 2), "UTF-8"));
      sb.append("&humidity="
          + URLEncoder.encode(new Integer(entry.getOutHumidity()).toString(),
              "UTF-8"));
      sb.append("&weather=");
      sb.append("&clouds=");
      sb.append("&softwaretype=org.tom.weather");
      sb.append("&archive=1");
      sb.append("&action=updateraw");
    } catch (NumberFormatException e) {
    } catch (UnsupportedEncodingException e) {
    }
    LOGGER.debug("query: " + sb);
    return sb.toString();
  }

  public synchronized void upload(org.tom.weather.ArchiveEntry[] entries) {
    if (enabled) {
      for (int i = 0; i < entries.length; i++) {
        if (entries[i] != null) {
          if (entries[i].isValid()) {
            updateHourlyRain(entries[i]);
            uploadData(entries[i]);
          }
        }
      }
    }
  }

  private void updateAccumulators(ArchiveEntry entry) {
    windSum += entry.getAvgWindSpeed();
    numWindSamples++;
    if (entry.getWindGust() > windGust) {
      windGust = entry.getWindGust();
    }
  }

  private boolean uploadData(org.tom.weather.ArchiveEntry entry) {
    Calendar cal = Calendar.getInstance();
    boolean ok = true;
    updateAccumulators(entry);
    cal.setTime(entry.getDate());
    if ((cal.get(Calendar.MINUTE) % interval) == 0
        || (entry.getDate().after(new Date(
            lastUpload.getTime() + 1000 * 60 * 10)))) {
      // construct a url query
      String query = constructQuery(entry);
      try {
        LOGGER.info("Posting data to wunderground.com for: " + entry.getDate());
        URL postUrl = new URL(url + query);
        postUrl.getContent();
      } catch (Exception e) {
        LOGGER.error(e);
        ok = false;
      }
      resetAccumulators();
      lastUpload = entry.getDate();
    }
    return ok;
  }

  private void resetAccumulators() {
    windGust = 0;
    numWindSamples = 0;
    windSum = 0;
  }

//  @SuppressWarnings("unchecked")
  private void updateHourlyRain(ArchiveEntry e) {
    if (e != null) {
      LOGGER.debug("adding rain from: " + e.getDate());
      hourlyRainEntries.add(e);
    }
  }

  private double getHourlyRain(ArchiveEntry endEntry) {
    double hourlyRain = 0.0;
    // trim the collection to the last hour (subtract an hour from entry)
    // total the rain
    Calendar cal = Calendar.getInstance();
    cal.setTime(endEntry.getDate());
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Date endDate = cal.getTime();
    cal.add(Calendar.HOUR_OF_DAY, -1);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Date startDate = cal.getTime();
    LOGGER.debug("getting rain from: " + startDate + " to " + endDate);
    trimEntries(hourlyRainEntries, startDate, endDate);
    // make sure that startDate is not earlier than the earliest entry
    if (!hourlyRainEntries.isEmpty()) {
      LOGGER.debug(hourlyRainEntries.size() + " entries in rainEntries");
      Iterator iter = hourlyRainEntries.iterator();
      while (iter.hasNext()) {
        ArchiveEntry entry = (ArchiveEntry) iter.next();
        hourlyRain += entry.getRain();
      }
    } else {
      LOGGER.debug("no entries in rainEntries");
    }
    LOGGER.debug("returning: " + hourlyRain);
    return hourlyRain;
  }

  private void trimEntries(Collection coll, Date start, Date end) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("start: " + start);
      LOGGER.debug("end: " + end);
      LOGGER.debug("rain entries size: " + hourlyRainEntries.size());
    }
    synchronized (coll) {
      Iterator iter = coll.iterator();
      while (iter.hasNext()) {
        ArchiveEntry entry = (ArchiveEntry) iter.next();
        if (entry.getDate().before(start) || entry.getDate().after(end)) {
          iter.remove();
        }
      }
    }
  }
}
