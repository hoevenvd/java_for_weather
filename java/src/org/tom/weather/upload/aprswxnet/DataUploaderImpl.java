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
package org.tom.weather.upload.aprswxnet;

import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.tom.util.DateUtils;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.Constants;
import org.tom.weather.Converter;
import org.tom.weather.upload.DataUploader;

public class DataUploaderImpl implements DataUploader {
  public static final String DATE_FORMAT = "ddHHmm";
  public static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
  public static final String WXNET = ">APRS,TCPXX*:";
  TimeZone tz = null;
  private int interval;
  private Vector hourlyRainEntries = new Vector();
  private Vector dailyRainEntries = new Vector();
  private static String longitude;
  private static String latitude;
  private static String id;
  private static String equipment;
  private static String host;
  private static int port;
  private static boolean enabled = false;
  private static DataUploaderImpl singleton;
  private Properties props = null;
  private static Logger logger;
  /** Creates new DataUploader */
  static {
    logger = Logger.getLogger(DataUploaderImpl.class);
  }

  private DataUploaderImpl(Properties props) {
    this.props = props;
    String enableFlag = (String) props.getProperty(Constants.APRSWXNET_ENABLE);
    if (enableFlag != null
        && Boolean.valueOf(enableFlag).booleanValue() == true) {
      enabled = true;
      tz = TimeZone.getTimeZone(props.getProperty(Constants.TIMEZONE));
      interval = new Integer(props.getProperty(Constants.APRSWXNET_INTERVAL))
          .intValue();
      longitude = props.getProperty(Constants.LONGITUDE);
      latitude = props.getProperty(Constants.LATITUDE);
      id = props.getProperty(Constants.APRSWXNET_ID);
      equipment = props.getProperty(Constants.APRSWXNET_EQUIPMENT);
      host = props.getProperty(Constants.APRSWXNET_HOST);
      port = new Integer(props.getProperty(Constants.APRSWXNET_PORT))
          .intValue();
    } else {
      enabled = false;
    }
  }

  public static void setProperties(Properties props) {
    if (singleton == null) {
      singleton = new DataUploaderImpl(props);
    }
  }

  public static synchronized DataUploaderImpl getInstance() {
    if (singleton == null) {
      String msg = "illegal call - not initialized";
      logger.error("getInstance() called before getInstance(Properties)");
      throw new IllegalStateException();
    } else {
      return singleton;
    }
  }

  /*
   * 
   * user CW0169 pass -1 vers org.tom.weather 1.00
   * 
   * CW0003>APRS,TCPXX*:@241505z4220.45N/07128.59W_032/005g008t054p078r001e1w
   * Field Meaning CW0003 Your CW number >APRS,TCPXX*: Boilerplate @241505z The
   * ddhhmm in UTC of the time that you generate the report 4220.45N/07128.59W
   * Your location. This is ddmm.hh -- i.e. degrees, minutes and hundreths of
   * minutes. The Longitude has three digits of degrees and leading zero digits
   * cannot be omitted. _032 The direction of the wind from true north (in
   * degrees). /005 The average windspeed in mph g008 The maximum gust windspeed
   * in mph (over the last five minutes) t054 The temperature in degrees
   * Farenheit -- if not available, then use '...' Temperatures below zero are
   * expressed as -01 to -99. p078 Rain in the last 24 hours (in hundreths of an
   * inch) -- this can be omitted r001 The rain in the last 1 hour (in hundreths
   * of an inch) -- this can be omitted b10245 The barometric pressure in tenths
   * of millbars -- this can be omitted. This is the sea-level pressure and not
   * the actual pressure as measured at your weatherstation. h50 The humidity in
   * percent. '00' => 100%. -- this can be omitted e1w The equipment you are
   * using. I'm not sure what you can put here.
   */
  private String constructQuery(org.tom.weather.ArchiveEntry entry) {
    double hourlyRain = getHourlyRain(entry);
    double dailyRain = getDailyRain(entry);
    StringBuffer sb = new StringBuffer();
    String query = null;
    DecimalFormat fmt;
    sb.append(id);
    sb.append(WXNET);
    java.util.Date d = DateUtils.getDateUtc(entry.getDate());
    sb.append("@");
    sb.append(sdf.format(d));
    sb.append("z");
    sb.append(latitude);
    sb.append("/");
    sb.append(longitude);
    fmt = new DecimalFormat("000");
    sb.append("_");
    sb.append(fmt.format(entry.getWindDirection().getDegrees()));
    sb.append("/");
    fmt = new DecimalFormat("000");
    sb.append(fmt.format(entry.getAvgWindSpeed()));
    sb.append("g");
    sb.append(fmt.format(entry.getWindGust()));
    if (entry.getAvgOutTemp() < 0) {
      fmt = new DecimalFormat("-00");
    } else {
      fmt = new DecimalFormat("000");
    }
    sb.append("t");
    sb.append(fmt.format(entry.getAvgOutTemp()));
    // rain
    fmt = new DecimalFormat("000");
    sb.append("p");
    sb.append(fmt.format(dailyRain * 100.0f));
    sb.append("r");
    sb.append(fmt.format(hourlyRain * 100.0f));
    fmt = new DecimalFormat("00000");
    sb.append("b");
    double pressure = Converter.toHPa((float) (entry.getBarometer() * 10.0));
    sb.append(fmt.format(pressure));
    sb.append("h");
    int humidity = entry.getOutHumidity();
    if (humidity >= 100) {
      humidity = 99;
    }
    sb.append(new Integer(humidity).toString());
    sb.append("e1w");
    // sb.append(equipment);
    return sb.toString();
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
    logger.debug("getting rain from: " + startDate + " to " + endDate);
    trimEntries(hourlyRainEntries, startDate, endDate);
    // make sure that startDate is not earlier than the earliest entry
    if (!hourlyRainEntries.isEmpty()) {
      if (logger.isDebugEnabled()) {
        logger.debug(hourlyRainEntries.size()
            + " entries in hourly rainEntries");
      }
      Iterator iter = hourlyRainEntries.iterator();
      while (iter.hasNext()) {
        ArchiveEntry entry = (ArchiveEntry) iter.next();
        hourlyRain += entry.getRain();
      }
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("no entries in hourly rainEntries");
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("returning: " + hourlyRain);
    }
    return hourlyRain;
  }

  private double getDailyRain(ArchiveEntry endEntry) {
    double dailyRain = 0.0;
    // trim the collection to the last hour (subtract an hour from entry)
    // total the rain
    Calendar cal = Calendar.getInstance();
    cal.setTime(endEntry.getDate());
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Date endDate = cal.getTime();
    cal.add(Calendar.DATE, -1);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Date startDate = cal.getTime();
    logger.debug("getting daily rain from: " + startDate + " to " + endDate);
    trimEntries(dailyRainEntries, startDate, endDate);
    // make sure that startDate is not earlier than the earliest entry
    if (!dailyRainEntries.isEmpty()) {
      if (logger.isDebugEnabled()) {
        logger.debug(dailyRainEntries.size() + " entries in daily rainEntries");
      }
      Iterator iter = dailyRainEntries.iterator();
      while (iter.hasNext()) {
        ArchiveEntry entry = (ArchiveEntry) iter.next();
        dailyRain += entry.getRain();
      }
    } else {
      logger.debug("no entries in dailyRainEntries");
    }
    logger.debug("returning: " + dailyRain);
    return dailyRain;
  }

  private void trimEntries(Collection coll, Date start, Date end) {
    logger.debug("start: " + start);
    logger.debug("end: " + end);
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

  public synchronized void upload(org.tom.weather.ArchiveEntry[] entries) {
    if (enabled) {
      for (int i = 0; i < entries.length; i++) {
        updateRain(entries[i]);
        uploadData(entries[i]);
      }
    }
  }

  private void updateRain(ArchiveEntry e) {
    logger.debug("adding rain from: " + e.getDate());
    hourlyRainEntries.add(e);
    dailyRainEntries.add(e);
  }

  private boolean uploadData(ArchiveEntry entry) {
    String connectString = "user " + id + " pass -1 vers org.tom.weather 1.00";
    Calendar cal = Calendar.getInstance();
    boolean ok = true;
    cal.setTime(entry.getDate());
    if ((cal.get(Calendar.MINUTE) % interval) == 0) {
      logger.debug("Connect String: " + connectString);
      // construct a url query
      String query = constructQuery(entry);
      try {
        logger.info("Posting data to aprswxnet for: " + entry.getDate());
        logger.info(query);
        Socket s = new Socket(host, port);
        s.setSoTimeout(2000);
        PrintWriter os = new PrintWriter(s.getOutputStream());
        os.println(connectString);
        os.println(query);
        os.flush();
        Thread.sleep(5 * 1000);
        if (os.checkError()) {
          ok = false;
        }
        os.close();
        logger.info("closing connection");
        s.close();
      } catch (Exception e) {
        logger.error(e);
        ok = false;
      }
    }
    return ok;
  }
}
