package org.tom.weather.graph;

import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CompassFormat;
import org.jfree.chart.axis.ModuloAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.Range;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;


public class WindDirectionChart extends BaseChart {

  private static final Logger LOGGER = Logger.getLogger(WindDirectionChart.class);
  
  public WindDirectionChart(DataSource source, String filename, Timestamp start, Timestamp end) {
    buildChart(source, filename, start, end);
  }
  
  private void buildChart(DataSource source, String filename, Timestamp start, Timestamp end) {
    XYDataset data = readData(source, start, end);

    // create the chart...
    JFreeChart chart = 
      ChartFactory.createTimeSeriesChart("Wind Direction",
        "Date", "Direction", data, // data
        true, // include legend
        true, false);
    XYPlot plot = chart.getXYPlot();
    XYDotRenderer renderer = new XYDotRenderer();
    plot.setRenderer(renderer);
//    plot.getDomainAxis().setLowerMargin(0.0);
//    plot.getDomainAxis().setUpperMargin(0.0);
    // configure the range axis to display directions...
    // NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    ModuloAxis rangeAxis;
    rangeAxis = new ModuloAxis("Direction", new Range(0, 360));
    TickUnits units = new TickUnits();
    units.add(new NumberTickUnit(180.0, new CompassFormat()));
    units.add(new NumberTickUnit(90.0, new CompassFormat()));
    units.add(new NumberTickUnit(45.0, new CompassFormat()));
    units.add(new NumberTickUnit(22.5, new CompassFormat()));
    rangeAxis.setStandardTickUnits(units);
    // add the wind force with a secondary dataset/renderer/axis
    plot.setRangeAxis(rangeAxis);
    rangeAxis.setDisplayRange(0, 360);

    writeChart(chart, filename);
  }

  private XYDataset readData(DataSource source, Timestamp start, Timestamp end) {
    JDBCXYDataset data = null;
 
    try {
      Connection con = source.getConnection();

      data = new JDBCXYDataset(con);
      String sql = "SELECT date - INTERVAL  " + Grapher.OFFSET / 1000 + " second, prevailing_wind_direction as Direction FROM archive_records"
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
