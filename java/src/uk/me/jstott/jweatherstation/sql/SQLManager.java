/*
 * Created on 18-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.sql;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class SQLManager {
  private static final Logger LOGGER = Logger.getLogger(SQLManager.class);
  
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final SimpleDateFormat sdf;
  
  static {
    sdf = new SimpleDateFormat(DATE_FORMAT);
  }

  public static synchronized String getSqlDate(java.util.Date d) {
    return sdf.format(d);
  }

}
