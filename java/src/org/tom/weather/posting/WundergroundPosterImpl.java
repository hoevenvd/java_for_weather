/*
 * DataUploader.java
 *
 * Created on February 1, 2001, 8:18 PM
 */
package org.tom.weather.posting;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.tom.util.DateUtils;
import org.tom.util.Rounding;
import org.tom.weather.Converter;
import org.tom.weather.PeriodData;
import org.tom.weather.SnapShot;

import uk.me.jstott.jweatherstation.sql.SQLManager;

public class WundergroundPosterImpl implements DataPoster {
  private static final Logger LOGGER = Logger.getLogger(WundergroundPosterImpl.class);
  TimeZone tz = null;
  private String url;
  private String stationId;
  private String password;
  boolean enable;
  
  public WundergroundPosterImpl() {
  }

  public WundergroundPosterImpl(String url, boolean enable, String stationId, String password) {
    this.url = url;
    this.enable = enable;
    this.stationId = stationId;
    this.password = password;
  }

  private String constructQuery(org.tom.weather.SnapShot snapshot) {
    StringBuffer sb = new StringBuffer();
    try {
      sb.append("?ID=");
      sb.append(URLEncoder.encode(stationId, "UTF-8"));
      sb.append("&PASSWORD=");
      sb.append(URLEncoder.encode(password, "UTF-8"));
      java.util.Date d = DateUtils.getDateUtc(snapshot.getDate());
      sb.append("&dateutc=" + URLEncoder.encode(SQLManager.getSqlDate(d), "UTF-8"));
      sb.append("&winddir="
          + URLEncoder.encode(
              new Integer(snapshot.getWindDirection().getDegrees()).toString(),
              "UTF-8"));
      sb.append("&windspeedmph="
          + URLEncoder.encode(Integer.toString(snapshot.getWindspeed()),
              "UTF-8"));
//      sb.append("&windgustmph="
//          + URLEncoder.encode(Integer.toString(snapshot.getWindspeed()), "UTF-8"));
      sb.append("&tempf="
          + URLEncoder.encode(Rounding.toString(new Double(snapshot.getOutsideTemp()).doubleValue(), 1), "UTF-8"));
      sb.append("&dailyrainin="
          + URLEncoder.encode(Rounding.toString(snapshot.getDayRain(), 2), "UTF-8"));
      sb.append("&baromin="
          + URLEncoder.encode(Rounding.toString(snapshot.getPressure(), 3),
              "UTF-8"));
      sb.append("&dewptf="
          + URLEncoder.encode(Rounding.toString(new Double(Converter
              .getDewpoint(snapshot.getOutsideHumidity(), (float)snapshot.getOutsideTemp())).doubleValue(), 2), "UTF-8"));
      sb.append("&humidity="
          + URLEncoder.encode(new Integer((int)snapshot.getOutsideHumidity()).toString(),
              "UTF-8"));
      sb.append("&weather=");
      sb.append("&clouds=");
      sb.append("&softwaretype=org.tom.weather");
      sb.append("&archive=0");
      sb.append("&action=updateraw&realtime=1&rtfreq=3.0");
    } catch (NumberFormatException e) {
    } catch (UnsupportedEncodingException e) {
    }
    LOGGER.debug("query: " + sb);
    return sb.toString();
  }

  public void post(SnapShot snap) {
    String query = constructQuery(snap);
    try {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Posting data to wunderground.com for: " + snap.getDate());
      }
      URL postUrl = new URL(url + query);
      postUrl.getContent();
    } catch (Exception e) {
      LOGGER.error(e);
    }
  }

  public void post(PeriodData periodData) {
  }
}
