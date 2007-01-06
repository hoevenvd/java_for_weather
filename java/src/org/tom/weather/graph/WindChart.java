package org.tom.weather.graph;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;


public class WindChart extends BaseChart {

  private static final Logger LOGGER = Logger.getLogger(WindChart.class);
  public WindChart(DataSource source, String filename, Timestamp start, Timestamp end) {
    buildChart(source, filename, start, end);
  }
  
  private void buildChart(DataSource source, String filename, Timestamp start, Timestamp end) {
    XYDataset data = readData(source, start, end);

    // create the chart...
    JFreeChart chart = 
      ChartFactory.createTimeSeriesChart("Wind Gusts",
        "Date", "MPH", data, // data
        true, // include legend
        true, // tool tips
        false); // urls

    writeChart(chart, filename);
  }

  private XYDataset readData(DataSource source, Timestamp start, Timestamp end) {
    JDBCXYDataset data = null;
 
    try {
      Connection con = source.getConnection();
      data = new JDBCXYDataset(con);
      String sql = "SELECT date, high_wind_speed as Gust FROM archive_records"
          + " where date >= '" + start + "' and date < '" + end + "' order by date desc;";
      data.executeQuery(sql);
      con.close();
    }
    catch (SQLException e) {
      LOGGER.error(e);
    }
    catch (Exception e) {
      LOGGER.error(e);
    }
    return data;
  }
}
