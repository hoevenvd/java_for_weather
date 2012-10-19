/*
 * Constants.java
 *
 * Created on February 5, 2001, 7:05 PM
 */
package org.tom.weather;

/**
 * 
 * @author administrator
 * @version
 */
public interface Constants {
  public static final String JINI_JAVASPACE_HOST = "weather.info.jini.javaSpaceHost";
  public static final String JINI_TRAN_HOST = "weather.info.jini.tranHost";
  public static final String JINI_JAVASPACE_NAME = "jini.spaceName=WeatherSpace";
  public static final String JAVASPACE_HOST = "weather.info.spaceHost";
  public static final String JAVASPACE_NAME = "weather.info.spaceName";
  public static final String RECEIVE_URL = "weather.receiveServlet.url";
  public static final String PORT = "weather.station.port";
  public static final String WRITE_TO_CONTEXT = "weather.info.writeToContext";
  public static final String CURRENT_SNAPSHOT = "SnapShot-CURRENT";
  public static final String PERIOD_KEY = "Period-";
  public static final String USE_ARCHIVE_MEMORY = "weather.station.useArchiveMemory";
  public static final String LOCATION = "weather.db.location";
  public static final String SQL_CLASS = "weather.db.sql.class";
  public static final String GRAPH_SQL_CLASS = "weather.db.graph.sql.class";
  public static final String TIMEZONE = "weather.db.timeZone";
  public static final String TABLE_NAME = "weather.db.tableName";
  public static final String POSTING_DELEGATE = "weather.info.persistence.delegate";
  public static final String WUNDERGROUND_URL = "weather.wunderground.uploadUrl";
  public static final String WUNDERGROUND_ENABLE = "weather.wunderground.enable";
  public static final String WUNDERGROUND_USER = "weather.wunderground.user";
  public static final String WUNDERGROUND_PASSWORD = "weather.wunderground.password";
  public static final String WUNDERGROUND_INTERVAL = "weather.wunderground.interval";
  public static final String PERIOD_DELEGATE_KEY = "weather.history.persistenceDelegate";
  public static final String PERIOD_THIS_HOUR_FREQUENCY = "weather.history.refresh.thisHour";
  public static final String PERIOD_LAST_HOUR_FREQUENCY = "weather.history.refresh.lastHour";
  public static final String PERIOD_TODAY_FREQUENCY = "weather.history.refresh.today";
  public static final String PERIOD_YESTERDAY_FREQUENCY = "weather.history.refresh.yesterday";
  public static final String PERIOD_THIS_WEEK_FREQUENCY = "weather.history.refresh.thisWeek";
  public static final String PERIOD_LAST_WEEK_FREQUENCY = "weather.history.refresh.lastWeek";
  public static final String PERIOD_THIS_MONTH_FREQUENCY = "weather.history.refresh.thisMonth";
  public static final String PERIOD_LAST_MONTH_FREQUENCY = "weather.history.refresh.lastMonth";
  public static final String PERIOD_THIS_SEASON_FREQUENCY = "weather.history.refresh.thisSeason";
  public static final String PERIOD_LAST_SEASON_FREQUENCY = "weather.history.refresh.lastSeason";
  public static final String PERIOD_THIS_YEAR_FREQUENCY = "weather.history.refresh.thisYear";
  public static final String PERIOD_LAST_YEAR_FREQUENCY = "weather.history.refresh.lastYear";
  public static final String PERIOD_FOREVER_FREQUENCY = "weather.history.refresh.forever";
  public static final String JDBC_URL = "weather.db.jdbc.url";
  public static final String JDBC_DRIVER = "weather.db.jdbc.driver";
  public static final String JDBC_USER = "weather.db.jdbc.user";
  public static final String JDBC_PASSWORD = "weather.db.jdbc.password";
  public static final String DEBUG = "weather.debugOutput";
  public static final String APRSWXNET_ID = "weather.aprswxnet.station";
  public static final String LONGITUDE = "weather.station.longitude";
  public static final String LATITUDE = "weather.station.latitude";
  public static final String APRSWXNET_INTERVAL = "weather.aprswxnet.interval";
  public static final String APRSWXNET_ENABLE = "weather.aprswxnet.enable";
  public static final String APRSWXNET_HOST = "weather.aprswxnet.host";
  public static final String APRSWXNET_PORT = "weather.aprswxnet.port";
  public static final String APRSWXNET_EQUIPMENT = "weather.aprswxnet.equipment";
  public static final String METRIC = "weather.metric";
  public static final String CACHE_NAME = "ArchiveCache";
}
