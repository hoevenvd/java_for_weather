/*
 * PostgresJdbcHelper.java
 *
 * Created on January 30, 2001, 9:18 AM
 */
package org.tom.weather.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.tom.weather.Constants;
import org.tom.weather.SqlHelper;

/**
 * 
 * @author administrator
 * @version
 */
public class MySqlJdbcHelper extends SqlHelper {
  private static Logger logger;
  static {
    logger = Logger.getLogger(MySqlJdbcHelper.class);
  }

  public MySqlJdbcHelper() {
    super();
  }

  public Connection getConnection() {
    try {
      Class.forName(props.getProperty(Constants.JDBC_DRIVER));
    } catch (Exception e) {
      logger.error("Error: unable to load "
          + props.getProperty(Constants.JDBC_DRIVER), e);
      e.printStackTrace();
      System.exit(1);
    }
    Connection con = null;
    try {
      con = DriverManager.getConnection(props.getProperty(Constants.JDBC_URL),
          props.getProperty(Constants.JDBC_USER), props
              .getProperty(Constants.JDBC_PASSWORD));
    } catch (SQLException e) {
      e.printStackTrace();
      con = null;
    }
    return con;
  }
}
