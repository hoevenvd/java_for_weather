package org.tom.weather.mysql;

import java.util.Properties;
import java.sql.*;
import org.tom.weather.*;
import org.apache.log4j.Logger;
import org.tom.util.Rounding;

/**
 * 
 * @author administrator
 * @version
 */
public class MySqlPeriodHistoryDelegate implements PeriodHistoryDelegate {
  /**
   * 
   */
  private static final long serialVersionUID = -2853078902311328260L;
  private SqlHelper helper;
  private Properties props;
  static String HIGH_TEMP_NESTED = "select snapdate, hiouttemp from weathersnapshots where snapdate >= ? and snapdate < ? and hiouttemp = (select max(hiouttemp) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String LOW_TEMP_NESTED = "select snapdate, lowouttemp from weathersnapshots where snapdate >= ? and snapdate < ? and lowouttemp = (select min(lowouttemp) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String HIGH_TEMP = "select snapdate, hiouttemp from weathersnapshots where (snapdate >= ? and snapdate < ?) order by hiouttemp desc, snapdate desc limit 1";
  static String LOW_TEMP = "select snapdate, lowouttemp from weathersnapshots where (snapdate >= ? and snapdate < ?) order by lowouttemp, snapdate desc limit 1";
  static String HIGH_PRESSURE_NESTED = "select snapdate, barometer from weathersnapshots where snapdate >= ? and snapdate < ? and barometer = (select max(barometer) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String LOW_PRESSURE_NESTED = "select snapdate, barometer from weathersnapshots where snapdate >= ? and snapdate < ? and barometer = (select min(barometer) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String HIGH_PRESSURE = "select snapdate, barometer from weathersnapshots where (snapdate >= ? and snapdate < ?) order by barometer desc, snapdate desc limit 1";
  static String LOW_PRESSURE = "select snapdate, barometer from weathersnapshots where (snapdate >= ? and snapdate < ?) order by barometer, snapdate desc limit 1";
  static String HIGH_HUMIDITY_NESTED = "select snapdate, outhumidity from weathersnapshots where snapdate >= ? and snapdate < ? and outhumidity = (select max(outhumidity) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String LOW_HUMIDITY_NESTED = "select snapdate, outhumidity from weathersnapshots where snapdate >= ? and snapdate < ? and outhumidity = (select min(outhumidity) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String HIGH_HUMIDITY = "select snapdate, outhumidity from weathersnapshots where (snapdate >= ? and snapdate < ?) order by outhumidity desc, snapdate desc limit 1";
  static String LOW_HUMIDITY = "select snapdate, outhumidity from weathersnapshots where (snapdate >= ? and snapdate < ?) order by outhumidity, snapdate desc limit 1";
  static String HIGH_WINDCHILL_NESTED = "select snapdate, windchill from weathersnapshots where snapdate >= ? and snapdate < ? and windchill = (select max(windchill) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String LOW_WINDCHILL_NESTED = "select snapdate, windchill from weathersnapshots where snapdate >= ? and snapdate < ? and windchill = (select min(windchill) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String HIGH_WINDCHILL = "select snapdate, windchill from weathersnapshots where (snapdate >= ? and snapdate < ?) order by windchill desc, snapdate desc limit 1";
  static String LOW_WINDCHILL = "select snapdate, windchill from weathersnapshots where (snapdate >= ? and snapdate < ?) order by windchill, snapdate desc limit 1";
  static String HIGH_DEWPOINT_NESTED = "select snapdate, dewpoint from weathersnapshots where snapdate >= ? and snapdate < ? and dewpoint = (select max(dewpoint) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String LOW_DEWPOINT_NESTED = "select snapdate, dewpoint from weathersnapshots where snapdate >= ? and snapdate < ? and dewpoint = (select min(dewpoint) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String HIGH_DEWPOINT = "select snapdate, dewpoint from weathersnapshots where (snapdate >= ? and snapdate < ?) order by dewpoint desc, snapdate desc limit 1";
  static String LOW_DEWPOINT = "select snapdate, dewpoint from weathersnapshots where (snapdate >= ? and snapdate < ?) order by dewpoint, snapdate desc limit 1";
  static String HIGH_GUST_NESTED = "select snapdate, windgust from weathersnapshots where snapdate >= ? and snapdate < ? and windgust = (select max(windgust) from weathersnapshots where snapdate >= ? and snapdate < ? )";
  static String HIGH_GUST = "select snapdate, windgust from weathersnapshots where (snapdate >= ? and snapdate < ?) order by windgust desc, snapdate desc limit 1";
  static String AVG_TEMP = "select avg(avgouttemp) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String AVG_PRESSURE = "select avg(barometer) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String AVG_HUMIDITY = "select avg(outhumidity) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String AVG_DEWPOINT = "select avg(dewpoint) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String AVG_WINDCHILL = "select avg(windchill) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String AVG_WIND = "select avg(avgwindspeed) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String TOTAL_RAIN = "select sum(rain) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  static String ALL_AVERAGES = "select avg(avgouttemp), avg(barometer), avg(outhumidity), avg(dewpoint), avg(windchill), avg(avgwindspeed), sum(rain) from weathersnapshots where (snapdate >= ? and snapdate < ?)";
  private static Logger logger;
  static {
    logger = Logger.getLogger(MySqlPeriodHistoryDelegate.class);
  }

  private SqlHelper getHelper() {
    if (helper == null) {
      try {
        helper = (SqlHelper) Class.forName(
            props.getProperty(Constants.SQL_CLASS)).newInstance();
        helper.setProps(props);
      } catch (Exception e) {
        logger.error("Error: unable to instantiate: "
            + props.getProperty(Constants.SQL_CLASS), e);
        e.printStackTrace();
        System.exit(1);
      }
    }
    return helper;
  }

  public MySqlPeriodHistoryDelegate(Properties props) {
  }

  public MySqlPeriodHistoryDelegate() {
    super();
  }

  public Event getEvent(Period pd, String sql) throws PersistenceException {
    Object data;
    java.util.Date date;
    Event ev = null;
    Connection con = null;
    try {
      con = getHelper().getConnection();
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setTimestamp(1, new java.sql.Timestamp(pd.getStart().getTime()));
      ps.setTimestamp(2, new java.sql.Timestamp(pd.getEnd().getTime()));
      // only for nested calls
      // ps.setTimestamp(3, new java.sql.Timestamp(pd.getStart().getTime()));
      // ps.setTimestamp(4, new java.sql.Timestamp(pd.getEnd().getTime()));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        date = (java.util.Date) (rs.getObject(1));
        data = rs.getObject(2);
        ev = new Event(data, date);
      } else {
        rs.close();
        ps.close();
        System.err.println("No rows returned from query: " + sql
            + " for Period: " + pd.toString());
        try {
          Thread.sleep(60 * 1000L);
        } catch (InterruptedException e) {
        }
        throw new PersistenceException("No rows returned from query: " + sql
            + "\nStart: " + pd.getStart() + "\nEnd: " + pd.getEnd());
      }
      rs.close();
      ps.close();
      return ev;
    } catch (SQLException e) {
      e.printStackTrace();
      helper = null; // get another one after this
      throw new PersistenceException(e.getMessage());
    } finally {
      try {
        if (con != null)
          con.close();
      } catch (SQLException e) {
      }
    }
  }

  public double[] getAllAverages(Period pd) throws PersistenceException {
    double averages[] = new double[7];
    Connection con = null;
    try {
      con = getHelper().getConnection();
      PreparedStatement ps = con
          .prepareStatement(MySqlPeriodHistoryDelegate.ALL_AVERAGES);
      ps.setTimestamp(1, new java.sql.Timestamp(pd.getStart().getTime()));
      ps.setTimestamp(2, new java.sql.Timestamp(pd.getEnd().getTime()));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        averages[TEMP] = Rounding.round(rs.getDouble(TEMP + 1), 1);
        averages[PRESSURE] = Rounding.round(rs.getDouble(PRESSURE + 1), 3);
        averages[HUMIDITY] = Rounding.round(rs.getDouble(HUMIDITY + 1), 0);
        averages[DEWPOINT] = Rounding.round(rs.getDouble(DEWPOINT + 1), 1);
        averages[WINDCHILL] = Rounding.round(rs.getDouble(WINDCHILL + 1), 0);
        averages[WINDSPEED] = Rounding.round(rs.getDouble(WINDSPEED + 1), 0);
        averages[RAIN] = Rounding.round(rs.getDouble(RAIN + 1), 2);
      }
      rs.close();
      ps.close();
    } catch (SQLException e) {
      helper = null;
      throw new PersistenceException(e.getMessage());
    } finally {
      try {
        if (con != null)
          con.close();
      } catch (SQLException e) {
      }
    }
    return averages;
  }

  public Object getValue(Period pd, String sql) throws PersistenceException {
    Object data = null;
    Connection con = null;
    try {
      con = getHelper().getConnection();
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setTimestamp(1, new java.sql.Timestamp(pd.getStart().getTime()));
      ps.setTimestamp(2, new java.sql.Timestamp(pd.getEnd().getTime()));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        data = rs.getObject(1);
      }
      rs.close();
      ps.close();
      return data;
    } catch (SQLException e) {
      helper = null;
      throw new PersistenceException(e.getMessage());
    } finally {
      try {
        if (con != null)
          con.close();
      } catch (SQLException e) {
      }
    }
  }

  public Event getHighTemp(Period pd) throws PersistenceException {
    return getEvent(pd, HIGH_TEMP);
  }

  public Event getLowTemp(Period pd) throws PersistenceException {
    return getEvent(pd, LOW_TEMP);
  }

  public Event getHighDewpoint(Period pd) throws PersistenceException {
    Event ev = getEvent(pd, HIGH_DEWPOINT);
    ev.setValue(Rounding.round((Float) ev.getValue(), 1));
    return ev;
  }

  public Event getLowDewpoint(Period pd) throws PersistenceException {
    Event ev = getEvent(pd, LOW_DEWPOINT);
    ev.setValue(Rounding.round((Float) ev.getValue(), 1));
    return ev;
  }

  public Event getHighWindChill(Period pd) throws PersistenceException {
    Event ev = getEvent(pd, HIGH_WINDCHILL);
    ev.setValue(Rounding.round((Float) ev.getValue(), 0));
    return ev;
  }

  public Event getLowWindChill(Period pd) throws PersistenceException {
    Event ev = getEvent(pd, LOW_WINDCHILL);
    ev.setValue(Rounding.round((Float) ev.getValue(), 0));
    return ev;
  }

  public double getAvgTemp(Period pd) throws PersistenceException {
    return Rounding.round(((Float) getValue(pd, AVG_TEMP)).doubleValue(), 2);
  }

  public Event getHighPressure(Period pd) throws PersistenceException {
    Event ev = getEvent(pd, HIGH_PRESSURE);
    ev.setValue((Rounding.round((Float) ev.getValue(), 3)));
    return ev;
  }

  public Event getLowPressure(Period pd) throws PersistenceException {
    Event ev = getEvent(pd, LOW_PRESSURE);
    ev.setValue(Rounding.round((Float) ev.getValue(), 3));
    return ev;
  }

  public double getAvgPressure(Period pd) throws PersistenceException {
    return Rounding
        .round(((Float) getValue(pd, AVG_PRESSURE)).doubleValue(), 3);
  }

  public Event getHighHumidity(Period pd) throws PersistenceException {
    return getEvent(pd, HIGH_HUMIDITY);
  }

  public Event getLowHumidity(Period pd) throws PersistenceException {
    return getEvent(pd, LOW_HUMIDITY);
  }

  public int getAvgHumidity(Period pd) throws PersistenceException {
    return ((Integer) getValue(pd, AVG_HUMIDITY)).intValue();
  }

  public int getAvgWind(Period pd) throws PersistenceException {
    return ((Integer) getValue(pd, AVG_WIND)).intValue();
  }

  public double getAvgDewpoint(Period pd) throws PersistenceException {
    return Rounding
        .round(((Float) getValue(pd, AVG_DEWPOINT)).doubleValue(), 1);
  }

  public double getAvgWindChill(Period pd) throws PersistenceException {
    return Rounding.round(((Float) getValue(pd, AVG_WINDCHILL)).doubleValue(),
        1);
  }

  public Event getHighGust(Period pd) throws PersistenceException {
    return getEvent(pd, HIGH_GUST);
  }

  public double getRain(Period pd) throws PersistenceException {
    return Rounding.round(((Float) getValue(pd, TOTAL_RAIN)).doubleValue(), 2);
  }

  public void setProperties(Properties props) {
    this.props = props;
  }

  public void close() {
  }
}
