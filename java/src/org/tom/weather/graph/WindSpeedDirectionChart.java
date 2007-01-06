/* -----------------------
 * CompassFormatDemo2.java
 * -----------------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 */

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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.xy.XYDataset;

public class WindSpeedDirectionChart extends BaseChart {

  private static final Logger LOGGER = Logger.getLogger(WindSpeedDirectionChart.class);

  public WindSpeedDirectionChart(DataSource source, String filename, Timestamp start, Timestamp end) {
    buildChart(source, filename, start, end);
  }

  private XYDataset[] readData(DataSource source, Timestamp start, Timestamp end) {
    XYDataset[] data = new JDBCXYDataset[2];
    data[0] = createDirectionDataset(source, start, end);
    data[1] = createForceDataset(source, start, end);
    return data;
  }

  private void buildChart(DataSource source, String filename, Timestamp start, Timestamp end) {
    XYDataset[] data = readData(source, start, end);

    JFreeChart chart = ChartFactory.createTimeSeriesChart("Time", "Date",
        "Direction", data[0], true, true, false);

    XYPlot plot = chart.getXYPlot();
    plot.getDomainAxis().setLowerMargin(0.0);
    plot.getDomainAxis().setUpperMargin(0.0);
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

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setBaseLinesVisible(false);
    renderer.setBaseShapesVisible(true);
    plot.setRenderer(renderer);
    // add the wind force with a secondary dataset/renderer/axis
    plot.setRangeAxis(rangeAxis);
    rangeAxis.setDisplayRange(0, 359);
    XYItemRenderer renderer2 = new XYAreaRenderer();
    ValueAxis axis2 = new NumberAxis("MPH");
    //axis2.setRange(0.0, 60.0);
    renderer2.setSeriesPaint(0, new Color(0, 0, 255, 128));
    plot.setDataset(1, data[1]);
    plot.setRenderer(1, renderer2);
    plot.setRangeAxis(1, axis2);
    plot.mapDatasetToRangeAxis(1, 1);

    writeChart(chart, filename);

  }

  private XYDataset createDirectionDataset(DataSource source, Timestamp start, Timestamp end) {
    JDBCXYDataset data = null;
    try {
      Connection con = source.getConnection();
      data = new JDBCXYDataset(con);
      String sql = "SELECT date, prevailing_wind_direction as Direction FROM archive_records"
          + " where date >= '"
          + start
          + "' and date < '"
          + end
          + "' order by date desc;";
      data.executeQuery(sql);
      con.close();
    } catch (SQLException e) {
      LOGGER.error(e);
    }
    return data;
    // TimeSeriesCollection dataset = new TimeSeriesCollection();
    // TimeSeries s1 = new TimeSeries("Wind Direction", Minute.class);
    // RegularTimePeriod start = new Minute();
    // double direction = 0.0;
    // for (int i = 0; i < count; i++) {
    // s1.add(start, direction);
    // start = start.next();
    // direction = direction + (Math.random() - 0.5) * 15.0;
    // if (direction < 0.0) {
    // direction = direction + 360.0;
    // }
    // else if (direction > 360.0) {
    // direction = direction - 360.0;
    // }
    // }
    // dataset.addSeries(s1);
    // return dataset;
  }

  /**
   * Creates a sample dataset.
   * 
   * @param count
   *          the item count.
   * 
   * @return the dataset.
   */
  private XYDataset createForceDataset(DataSource source, Timestamp start, Timestamp end) {
    JDBCXYDataset data = null;
    try {
      Connection con = source.getConnection();
      data = new JDBCXYDataset(con);
      String sql = "SELECT date, average_wind_speed FROM archive_records"
          + " where date >= '" + start + "' and date < '" + end
          + "' order by date desc;";
      data.executeQuery(sql);
      con.close();
    } catch (SQLException e) {
      LOGGER.error(e);
    }
    return data;
    // TimeSeriesCollection dataset = new TimeSeriesCollection();
    // TimeSeries s1 = new TimeSeries("Wind Speed", Minute.class);
    // RegularTimePeriod start = new Minute();
    // double force = 3.0;
    // for (int i = 0; i < count; i++) {
    // s1.add(start, force);
    // start = start.next();
    // force = Math.max(0.5, force + (Math.random() - 0.5) * 0.5);
    // }
    // dataset.addSeries(s1);
    // return dataset;
  }
}
