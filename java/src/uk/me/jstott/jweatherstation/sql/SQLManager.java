/*
 * Created on 18-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class SQLManager {
  private static final Logger LOGGER = Logger.getLogger(SQLManager.class);
  
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final SimpleDateFormat sdf;
  private DataSource dataSource;
  
  static {
    sdf = new SimpleDateFormat(DATE_FORMAT);
  }

  Connection connection = null;
  public SQLManager(DataSource src) {
    dataSource = src;
  }
  
  public static synchronized String getSqlDate(java.util.Date d) {
    return sdf.format(d);
  }

  /**
   * Connect to the MySQL database.
   * 
   * @since 1.0
   */
  private Connection getConnection() {
    if (connection == null) {
      try {
        connection = dataSource.getConnection();
      } catch (java.sql.SQLException e) {
        LOGGER.error(e);
      }
    }
    return connection;
  }

  /**
   * This method executes an update statement.
   * 
   * @param sqlStatement
   *          SQL DDL or DML statement to execute.
   * @since 1.0
   */
  public void update(String sqlStatement) {
    if (getConnection() != null) {
      try {
        LOGGER.debug(sqlStatement);
        Statement s = getConnection().createStatement();
        boolean result = s.execute(sqlStatement);
        if (!result) {
          // LOGGER.debug("Statement completed successfully");
        }
        s.close();
      } catch (SQLException e) {
        if (e.getErrorCode() == 1062) {
          LOGGER.debug("ignoring duplicate key for sql: " + sqlStatement);
          // Duplicate entry
        } else {
          LOGGER.error(e);
        }
      }
    } else {
      LOGGER.warn("getConnection() returning null - database problem?");
    }
  }

  /**
   * This method executes a select statement and displays the result.
   * 
   * @param sqlStatement
   *          SQL SELECT statement to execute.
   * @return
   * @since 1.0
   */
  public ResultSet query(String sqlStatement) {
    if (getConnection() != null) {
      try {
        Statement s = getConnection().createStatement();
        ResultSet rs = s.executeQuery(sqlStatement);
        return rs;
      } catch (SQLException e) {
        LOGGER.error(e);
      }
    } else {
      LOGGER.warn("getConnection() returning null - database problem?");
    }
    return null;
  }
  
  protected void finalize() throws Throwable {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("finalize() - connection = " + getConnection());
    }
    
    try {
      Connection con = getConnection();
      if (con != null) {
        con.close();
      }
    } catch (SQLException e) {
      // ignore SQL exceptions since this 
      // method is a way to clean up, not guaranteee anything.
    }
    
    super.finalize();
  }
}
