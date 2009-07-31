package org.tom.weather.graph;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Grapher extends TimerTask {
  
  private static final Logger LOGGER = Logger.getLogger(Grapher.class);
  public static final long OFFSET = (new Date().getTimezoneOffset() * 60 * 1000);
  private java.sql.Timestamp start_2;
  private java.sql.Timestamp start_15;
  private java.sql.Timestamp end;
  private String baseDir = null;
  private DataSource dataSource;
  
  
  public Grapher() {
    setDates();
  }

  public static void main(String[] args) {
    try {
      ApplicationContext factory = new FileSystemXmlApplicationContext(
          "weather-graphs.xml");
      Grapher g = (Grapher)factory.getBean("grapher");
      g.buildCharts();
    } catch (BeansException e) {
      LOGGER.error(e);
    }
  }

  private void buildCharts() {
    new WindChart(getDataSource(), getBaseDir() + "/wind24.jpg", getStart_2(), getEnd());
    new WindChart(getDataSource(), getBaseDir() + "/windweek.jpg", getStart_15(), getEnd());

    new TempDewpointChart(getDataSource(), getBaseDir() + "/temp24.jpg", getStart_2(), getEnd());
    new TempDewpointChart(getDataSource(), getBaseDir() + "/tempweek.jpg", getStart_15(), getEnd());

    new PressureChart(getDataSource(), getBaseDir() + "/pressure24.jpg", getStart_2(), getEnd());
    new PressureChart(getDataSource(), getBaseDir() + "/pressureweek.jpg", getStart_15(), getEnd());

    new SolarRadiationChart(getDataSource(), getBaseDir() + "/solar24.jpg", getStart_2(), getEnd());
    new SolarRadiationChart(getDataSource(), getBaseDir() + "/solarweek.jpg", getStart_15(), getEnd());

    new WindSpeedDirectionChart(getDataSource(), getBaseDir() + "/windspddir24.jpg", getStart_2(), getEnd());
    new WindSpeedDirectionChart(getDataSource(), getBaseDir() + "/windspddirweek.jpg", getStart_15(), getEnd());

    new WindDirectionChart(getDataSource(), getBaseDir() + "/winddir24.jpg", getStart_2(), getEnd());
    new WindDirectionChart(getDataSource(), getBaseDir() + "/winddirweek.jpg", getStart_15(), getEnd());

    new RainRateChart(getDataSource(), getBaseDir() + "/rainrate24.jpg", getStart_2(), getEnd());
    new RainRateChart(getDataSource(), getBaseDir() + "/rainrateweek.jpg", getStart_15(), getEnd());

  }

  private void setDates() {
    Timestamp ts = null;
    Calendar cal = Calendar.getInstance();
    ts = new Timestamp(cal.getTime().getTime() + OFFSET);
    LOGGER.debug("setting end to: " + ts);
    setEnd(ts);
    cal.add(Calendar.HOUR, -48);
    ts = new Timestamp(cal.getTime().getTime() + OFFSET);
    LOGGER.debug("setting start_2 to: " + ts);
    setStart_2(ts);
    cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -15);
    ts = new Timestamp(cal.getTime().getTime() + OFFSET);
    LOGGER.debug("setting start_15 to: " + ts);
    setStart_15(ts);
    
  }

  private void setStart_2(java.sql.Timestamp start_2) {
    this.start_2 = start_2;
  }

  private java.sql.Timestamp getStart_2() {
    return start_2;
  }

  private void setStart_15(java.sql.Timestamp start_15) {
    this.start_15 = start_15;
  }

  private java.sql.Timestamp getStart_15() {
    return start_15;
  }

  private void setEnd(java.sql.Timestamp end) {
    this.end = end;
  }

  private java.sql.Timestamp getEnd() {
    return end;
  }

  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  public String getBaseDir() {
    return baseDir;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

//  @Override
  public void run() {
    setDates();
    buildCharts();
  }
  
}
