package org.tom.weather.posting;

import org.apache.log4j.Logger;
import org.tom.weather.PeriodData;
import org.tom.weather.SnapShot;

import uk.me.jstott.jweatherstation.sql.SQLManager;

public class DatabasePosterImpl implements DataPoster {
  private static final Logger LOGGER = Logger.getLogger(DatabasePosterImpl.class);
  private static final String COMMA = ",";
  private SQLManager sqlManager;

  public void post(SnapShot snap) {
    if (snap.isValid()) {
      String sql = getSnapshotPostSql(snap);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("posting: " + snap + " with sql: " + sql);
      }
      getSqlManager().update(sql);
    }

  }

  //todo - add location here
  
  private String getSnapshotPostSql(SnapShot snap) {
    StringBuffer sql = new StringBuffer("REPLACE into samples (id, sample_date, outside_temperature, outside_humidity, dewpoint, windchill, pressure, bar_status, windspeed, wind_direction, is_raining, rain_rate, tenMinAvgWind) values (1, ");
    sql.append("'" + new java.sql.Date(snap.getDate().getTime()) + 
        " " + new java.sql.Time(snap.getDate().getTime()) + "'" + COMMA);
    sql.append(snap.getOutsideTemp() + COMMA);
    sql.append(snap.getOutsideHumidity() + COMMA);
    sql.append(snap.getDewpoint() + COMMA);
    sql.append(snap.getWindchill() + COMMA);
    sql.append(snap.getPressure() + COMMA);
    sql.append("'" + snap.getBarStatus() + "'" + COMMA);
    sql.append(snap.getWindspeed() + COMMA);
    sql.append(snap.getWindDirection().getDegrees() + COMMA);
    sql.append((snap.isRaining() ? "1" : "0") + COMMA);
    sql.append(snap.getRainRate() + COMMA);
    sql.append(snap.getTenMinAvgWind());
    sql.append(");");
    return sql.toString();
  }

  public void post(PeriodData periodData) {
    LOGGER.error("noop - posting: " + periodData);
  }

  public void setSqlManager(SQLManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public SQLManager getSqlManager() {
    return sqlManager;
  }

}
