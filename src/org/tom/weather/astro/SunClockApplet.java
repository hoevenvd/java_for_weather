package org.tom.weather.astro;

/**
 * SunClock displays a world map showing the sunrise and sunset terminators.
 * <p>
 * 
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.
 * <p>
 * World map copyright &copy; 1992-97 Apple Computer Inc. All Rights Reserved.
 * Used by permission.
 * <p>
 * Moon Image from Michael Myers
 * 
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided
 * that this copyright notice and appropriate documentation appears in all
 * copies. This software may not be distributed for fee or as part of
 * commercial, "shareware," and/or not-for-profit endevors including, but not
 * limited to, CD-ROM collections, online databases, and subscription services
 * without specific license.
 * <p>
 * 
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @author <a href="http://www.netavs.com:80/~mhmyers/moon.html">Michael Myers
 *         (moon image)</a>
 * @version 2.0 1996.06.16 Minor bug-fixes 1997.11.27 Rewritten for Java 1.1 Set
 *          tabs every 4 characters.
 */
// package Classes;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class SunClockApplet extends Applet implements ActionListener, Runnable,
    Constants {
  public static final String copyright = "Copyright 1996-98 Martin Minow. All Rights Reserved";
  public static final String author = "mailto:minow@merrymeet.com";
  public static final String name = "SunClock";
  public static final String version = "2.0";
  public static final boolean testing = false;
  /**
   * Parameter names.
   */
  public static final String panelWidthParam = "width";
  public static final String panelHeightParam = "height";
  public static final String locationParam = "location";
  public static final String timezoneParam = "timezone";
  public static final String latitudeParam = "latitude";
  public static final String longitudeParam = "longitude";
  public static final String parameterInfo[][] = {
      { panelWidthParam, "int", "SunClock applet width" },
      { panelHeightParam, "int", "SunClock applet height" },
      { locationParam, "String", "Observer's location" },
      { timezoneParam, "double", "Timezone offset (HH:MM, East positive)" },
      { latitudeParam, "double", "Observer's latitude (degrees North)" },
      { longitudeParam, "double", "Observer's longitude (degrees East)" }, };
  /**
   * Image file names
   */
  public static final String earthImageName = "EarthImage360.gif";
  public static final String moonImageName = "FullMoon64.gif";
  private int currentHour = (int) ((System.currentTimeMillis() / 3600000) % 24);
  /*
   * Define the panel thread and the update (sleep) time.
   */
  Thread runner;
  public static final int updateInterval = 60000; /* One minute */
  private SunClockData sunClockData = new SunClockData(this);
  /*
   * Here are the user interface components.
   */
  private Button optionButton = new Button("Set Options...");
  private Button infoButton = new Button("About...");
  private SunClockDisplay sunClockDisplay;
  private SunClockText sunClockText;
  private boolean newJavaFeatures = false;
  public static final String displayFontNames[] = {
  /* "NoSuchFont 10", */
  "Tekton 12", "Geneva 9", "Verdana 10", "SansSerif 10", "Helvetica 10" };

  /*
   * public void testMoonLimbAngle() { Date testDate = new Date(79, 1, 2, 21 -
   * 7, 0); System.out.println(AstroFormat.getGMTDateString(testDate)); double
   * MJD = Astro.MJD(testDate); double solar[] = Astro.solarEphemeris(MJD);
   * double lunar[] = Astro.lunarEphemeris(MJD); MoonPhase moonPhase = new
   * MoonPhase(testDate); Format.log("solar", solar); Format.log("lunar",
   * lunar); Format.log("limbAngle", moonPhase.limbPositionAngle); }
   */
  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == optionButton) {
      OptionDialog dialog = new OptionDialog(sunClockData);
    } else if (source == infoButton) {
      AboutSunClock dialog = new AboutSunClock(sunClockData);
    } else {
      System.out.println("Strange action event: " + event);
    }
  }

  /**
   * We're all done for now. Make sure that the thread is deleted.
   */
  public void destroy() {
    stop();
  }

  /**
   * Return information about the applet.
   * 
   * @return a string describing the applet writer.
   */
  public String getAppletInfo() {
    String result = name + " version " + version + ". " + copyright;
    return (result);
  }

  public void getBackgroundColor() {
    String colorName = getParameter("bgcolor");
    if (colorName != null) {
      if (colorName.charAt(0) == '#') {
        colorName = colorName.substring(1);
      }
      try {
        int color = Integer.parseInt(colorName, 16);
        setBackground(new Color(color));
      } catch (NumberFormatException e) {
      }
    }
  }

  /**
   * Parse a floating-point value representing latitude or longitude.
   * 
   * @param paramName
   *          The applet parameter name.
   * @param tagString
   *          "NS" or "EW" (the first is positive, the second negative). The
   *          function call paraemter must be given in upper case. The tag in
   *          the actual parameter may be given in either case.
   * @param defaultValue
   *          A value to return in case of missing data or parse errors..
   * @return the parsed (or default) value. The format is:
   * 
   * <pre>
   * 		[N S]
   * 		[+ -] degrees.fraction
   * 		[+ -] degrees:minutes
   * 		[N S]
   * </pre>
   * 
   * Only one [N S] may be specified.
   */
  public double getLocationParam(String paramName, String tagString,
      double defaultValue) {
    double result = defaultValue;
    try {
      ParseParameter parse = new ParseParameter(this, paramName);
      result = parse.parseLatLong(tagString);
    } catch (NumberFormatException e) {
      System.err.println("Bad " + paramName + ":" + e);
    }
    return (result);
  }

  /**
   * Return the applet supported parameters.
   * 
   * @return a two-dimension vector of parameter strings.
   */
  public String[][] getParameterInfo() {
    return (parameterInfo);
  }

  public void getParameters() {
    String locationName = getStringParam(locationParam, sunClockData
        .getLocationName());
    double latitude = getLocationParam(latitudeParam, "NS", sunClockData
        .getLatitude());
    double longitude = getLocationParam(longitudeParam, "EW", sunClockData
        .getLongitude());
    String timeZoneID = getStringParam(timezoneParam, sunClockData
        .getTimeZoneID());
    TimeZone zone = Astro.getTimeZone(timeZoneID);
    sunClockData.setLocation(locationName, latitude, longitude, TimeZone
        .getTimeZone(timeZoneID));
  }

  /**
   * Extract a parameter as a string.
   * 
   * @param paramName
   *          The applet parameter name
   * @param defaultString
   *          The default value (string format)
   */
  public String getStringParam(String paramName, String defaultString) {
    String result = getParameter(paramName);
    if (result == null) {
      result = defaultString;
    }
    return (result);
  }

  /**
   * Applet initialization.
   */
  public void init() {
    showStatus(getAppletInfo());
    try {
      new Thread().sleep(100L); /* Force the copyright to the display */
    } catch (Exception e) {
    }
    showStatus("");
    try {
      String version = System.getProperty("java.version");
      newJavaFeatures = (version.compareTo("1.1") >= 0);
    } catch (SecurityException e) {
      newJavaFeatures = false;
    }
    selectDefaultFont();
    getBackgroundColor();
    if (newJavaFeatures == false) {
      setLayout(new BorderLayout());
      TextField message = new TextField(
          "Sorry, but this applet requires Java 1.1 support."
              + " Please run the SimpleSunClock applet instead.");
      message.setEditable(false);
      add("Center", message);
    } else {
      /* */
      sunClockData.setDisplayFont(getFont());
      Font dialogFont = new Font("SansSerif", Font.PLAIN, 12);
      infoButton.setFont(dialogFont);
      optionButton.setFont(dialogFont);
      getParameters();
      infoButton.setBackground(Color.white);
      optionButton.setBackground(Color.white);
      Panel buttonPanel = new Panel();
      buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
      buttonPanel.add(infoButton);
      buttonPanel.add(optionButton);
      Label versionLabel = new Label(version);
      versionLabel.setFont(new Font("Helvetica", Font.ITALIC, 9));
      sunClockDisplay = new SunClockDisplay(sunClockData, getImage(
          getCodeBase(), earthImageName),
          getImage(getCodeBase(), moonImageName));
      sunClockText = new SunClockText(sunClockData);
      sunClockText.setBackground(Color.white);
      /* */
      GridBagLayout gridbag = new GridBagLayout();
      setLayout(gridbag);
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 2;
      c.gridheight = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.CENTER;
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.insets = new Insets(0, 0, 0, 0);
      gridbag.setConstraints(sunClockDisplay, c);
      add(sunClockDisplay);
      c.gridx = 0;
      c.gridy = 1;
      c.gridwidth = 2;
      c.gridheight = 1;
      c.fill = GridBagConstraints.BOTH;
      c.anchor = GridBagConstraints.CENTER;
      c.weightx = 1.0;
      c.weighty = 1.0;
      c.insets = new Insets(0, 0, 0, 0);
      gridbag.setConstraints(sunClockText, c);
      add(sunClockText);
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 1;
      c.gridheight = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.CENTER;
      c.weightx = 1.0;
      c.weighty = 0.0;
      c.insets = new Insets(2, 2, 2, 2);
      gridbag.setConstraints(buttonPanel, c);
      add(buttonPanel);
      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 2;
      c.gridheight = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.anchor = GridBagConstraints.WEST;
      c.weightx = 1.0;
      c.weighty = 0.0;
      c.insets = new Insets(2, 4, 2, 2);
      gridbag.setConstraints(versionLabel, c);
      add(versionLabel);
      optionButton.addActionListener(this);
      infoButton.addActionListener(this);
    }
  }

  /**
   * Run the applet.
   */
  public void run() {
    for (long cycles = 0; runner != null; cycles++) {
      showStatus("");
      Date date = new Date();
      if (testing) {
        long instant = date.getTime();
        instant += (cycles * 3600000 * 25); /* 25 hours */
        date = new Date(instant);
      }
      sunClockData.setDate(date);
      /*
       * Round the time down to the update interval, then add the update
       * interval. This makes the time display look better.
       */
      long nextTick = System.currentTimeMillis();
      nextTick -= nextTick % updateInterval;
      nextTick += updateInterval;
      long sleepTime = nextTick - System.currentTimeMillis();
      if (testing || sleepTime < 1000) {
        sleepTime = 1000;
      }
      try {
        runner.sleep(sleepTime);
      } catch (InterruptedException e) {
      }
    }
  }

  /**
   * This doesn't work the way I want it to: AWT doesn't give me access to the
   * Macintosh fonts; only to the AWT list.
   */
  public void selectDefaultFont() {
    String fontName = "";
    String fontSize = "";
    loop: for (int i = 0; i < displayFontNames.length; i++) {
      try {
        StringTokenizer t = new StringTokenizer(displayFontNames[i], " ");
        fontName = t.nextToken();
        fontSize = t.nextToken();
        Font font = new Font(fontName, Font.PLAIN, Integer.parseInt(fontSize));
        if (false) {
          System.out.println("Set font to \"" + fontName + "\" at " + fontSize
              + " size");
          System.out.println("Font = " + font.toString());
        }
        setFont(font);
        break loop;
      } catch (Exception e) {
        if (false) {
          System.out.println("Font \"" + fontName + "\" at " + fontSize
              + " size: " + e);
        }
      }
    }
    sunClockData.setDisplayFont(getFont());
  }

  /**
   * Start the Applet
   */
  public void start() {
    if (newJavaFeatures && runner == null) {
      runner = new Thread(this);
      runner.start();
    }
  }

  /**
   * Stop the Applet (temporarily, perhaps).
   */
  public void stop() {
    runner = null;
  }
}
