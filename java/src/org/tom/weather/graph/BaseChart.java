package org.tom.weather.graph;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.time.DateRange;

public class BaseChart {

  private static final Logger LOGGER = Logger.getLogger(BaseChart.class);
  static final double THREE_DAYS = 1000 * 60 *60 * 24 * 3;

  protected void writeChart(JFreeChart chart, String filename) {
    try {
      formatChart(chart);
      File file = new File(filename);
      ChartUtilities.saveChartAsJPEG(file, chart, 600, 300);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(e);
    } catch (FileNotFoundException e) {
      LOGGER.error(e);
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }

  protected void formatChart(JFreeChart chart) {
    
    // set the background color for the chart...
    chart.setBackgroundPaint(Color.LIGHT_GRAY);
    
    XYPlot plot = chart.getXYPlot();
    DateAxis axis = (DateAxis)plot.getDomainAxis();
    DateRange range = (DateRange)axis.getRange();
    if (range.getLength() < THREE_DAYS) {
      axis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR, 6));   
      axis.setTickLabelsVisible(true);
      DateFormat formatter = new SimpleDateFormat("EE HH:mm");
      axis.setDateFormatOverride(formatter );
    }
  }
}
