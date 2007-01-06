package org.tom.weather.astro;

/**
 * SunClockDisplay displays the time, earth, and moon images.<p>
 *
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.<p>
 * World map copyright &copy; 1992-97 Apple Computer Inc. All Rights Reserved.
 * Used by permission.<p>
 *
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies. This
 * software may not be distributed for fee or as part of commercial, "shareware,"
 * and/or not-for-profit endevors including, but not limited to, CD-ROM collections,
 * online databases, and subscription services without specific license.<p>
 *
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 2.0
 * 1997.11.27.
 * Set tabs every 4 characters.
 */
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.awt.event.*;

public class SunClockDisplay extends Component implements MouseListener, /*
                                                                           * Detect
                                                                           * mouse
                                                                           * position
                                                                           * and
                                                                           * clicks
                                                                           */
MouseMotionListener, /* Detect mouse movement */
Observer, /* Respond to our events */
Constants /* Our common constants */
{
  private SunClockData sunClockData;
  Date instant = new Date();
  /*
   * The display is drawn into displayImage which is associated with
   * displayGraphics.
   */
  private Image displayImage;
  private Graphics displayGraphics;
  Rectangle worldRect = new Rectangle();
  Rectangle moonRect = new Rectangle();
  Rectangle clockRect = new Rectangle();
  Rectangle timeBarRect = new Rectangle();
  int timeBarOffset;
  /* */
  SunrisePixels sunrisePixels;
  MoonPixels moonPixels;
  boolean mouseInWorldMap = false;
  boolean mousePressed = false;
  /*
   * moonOffset is used to adjust the position of the world map so it is
   * precisely tangent to the moon and clock images.
   * 
   * The formula is <pre> radius = diameter / 2 offset = radius * (1 - sin(45)); =
   * diameter * ((1 - sin(45)) / 2); The constant == (1 - sin(45 degrees)) / 2.
   * sin(45) = 0.707106781 1 - sin(45) = .292893218 (1 - sin(45)) / 2 =
   * 0.146446609 </pre>
   */
  private static final double offsetFactor = (1.0 - Math.sin(Math.PI / 4.0)) / 2.0;

  public SunClockDisplay(SunClockData sunClockData, Image earthImage,
      Image moonImage) {
    this.sunClockData = sunClockData;
    /*
     * Load both image before calling the filter constructors. This ensures that
     * we can draw the images immediately.
     */
    MediaTracker tracker = new MediaTracker(this);
    tracker.addImage(earthImage, 0);
    tracker.addImage(moonImage, 0);
    try {
      tracker.waitForAll();
    } catch (InterruptedException e) {
      System.err.println("SunClockDiaplay: image load interrupted: " + e);
    }
    sunrisePixels = new SunrisePixels(earthImage, this);
    moonPixels = new MoonPixels(moonImage, this);
    worldRect.setSize(earthImage.getWidth(null), earthImage.getHeight(null));
    moonRect.setSize(moonImage.getWidth(null), moonImage.getHeight(null));
    clockRect.setSize(moonRect.width, moonRect.height);
    sunClockData.addObserver(this);
    addMouseListener(this);
  }

  private void createDisplayImage() {
    int offset = (int) (((double) moonRect.width) * offsetFactor);
    getPreferredSize();
    Dimension d = getSize();
    worldRect.setLocation((d.width - worldRect.width) / 2, d.height
        - worldRect.height - timeBarRect.height);
    timeBarRect.setLocation(worldRect.x - timeBarOffset, d.height
        - timeBarRect.height);
    clockRect.setLocation(worldRect.x - clockRect.width + offset, worldRect.y
        - clockRect.height + offset);
    moonRect.setLocation(worldRect.x + worldRect.width - offset, worldRect.y
        - moonRect.height + offset);
    displayImage = createImage(d.width, d.height);
    if (displayImage != null) {
      if (displayGraphics != null) {
        displayGraphics.dispose();
      }
      displayGraphics = displayImage.getGraphics();
      displayGraphics.setFont(getFont());
    }
  }

  public Dimension getMinimumSize() {
    return (getPreferredSize());
  }

  public Dimension getPreferredSize() {
    FontMetrics fm = getFontMetrics(getFont());
    timeBarOffset = fm.stringWidth("00:00") / 2;
    timeBarRect.setSize(worldRect.width + timeBarOffset * 2, fm.getHeight());
    int moonWidth = moonRect.width
        - (int) (((double) moonRect.width) * offsetFactor);
    int displayWidth = timeBarRect.width + (moonWidth * 2);
    int displayHeight = moonWidth + worldRect.height + timeBarRect.height;
    return (new Dimension(displayWidth, displayHeight));
  }

  /**
   * Convert the latitude to the equivalent pixel offset. This is used for
   * debugging to draw meridians on the map. This function is only valid for
   * EarthImageXXX.gif.
   * 
   * @param latitude
   *          The value to convert (+90.0 .. -90.0)
   * @return the corresponding pixel offset from the top of the map.
   */
  public int latitudeToYPixel(double latitude) {
    int y = (int) (((double) worldRect.height) * ((90.0 - latitude) / 180.0));
    return (y + worldRect.y);
  }

  /**
   * Convert the longitude to the equivalent pixel offset. This is used for
   * debugging to draw meridians on the map.
   * 
   * @param longitude
   *          The value to convert (-180.0 .. +180.0)
   * @return the corresponding pixel offset from the left edge of the image.
   */
  public int longitudeToXPixel(double longitude) {
    double xFraction = (longitude + 180.0) / 360.0; /* East plus */
    int x = (int) (((double) worldRect.width) * xFraction);
    return (x + worldRect.x);
  }

  /**
   * Called by the MouseListener interface.
   */
  /*
   * The Mac isn't giving us mouseMoved events (or we aren't processing them)
   */
  public void mouseClicked(MouseEvent event) {
    // System.out.println("Clicked at " + event.getPoint());
    mousePressed = true;
    mouseReleased(event);
  }

  public void mouseDragged(MouseEvent event) {
    // System.out.println("Dragged at " + event.getPoint());
    mouseMoved(event);
  }

  public void mouseEntered(MouseEvent event) {
    // System.out.println("Entered at " + event.getPoint());
    mouseMoved(event);
  }

  public void mouseExited(MouseEvent event) {
    // System.out.println("Exited at " + event.getPoint());
    mouseMoved(event);
  }

  public void mouseMoved(MouseEvent event) {
    Point mousePoint = event.getPoint();
    boolean nowInWorldMap = worldRect.contains(mousePoint);
    // System.out.println("mouse at " + mousePoint + ", " + nowInWorldMap);
    if (nowInWorldMap != mouseInWorldMap) {
      mouseInWorldMap = nowInWorldMap;
      int cursor = (mouseInWorldMap) ? Cursor.CROSSHAIR_CURSOR
          : Cursor.DEFAULT_CURSOR;
      setCursor(Cursor.getPredefinedCursor(cursor));
    }
  }

  public void mousePressed(MouseEvent event) {
    // System.out.println("Pressed at " + event.getPoint());
    mousePressed = true;
    mouseMoved(event);
  }

  public void mouseReleased(MouseEvent event) {
    // System.out.println("Released at " + event.getPoint());
    /*
     * The mousePressed stuff prevents both click and release events from (both)
     * sending a new location to sunClockData.
     */
    mouseMoved(event);
    Point pt = event.getPoint();
    if (mousePressed && worldRect.contains(pt)) {
      double longitude = sunrisePixels.pixelToLongitude(pt.x - worldRect.x);
      double latitude = sunrisePixels.pixelToLatitude(pt.y - worldRect.y);
      sunClockData.setLocation(latitude, longitude);
      mousePressed = false;
    }
  }

  public void paint(Graphics g) {
    update(g);
  }

  private void paintClock() {
    long now = instant.getTime();
    SimpleTimeZone tz = (SimpleTimeZone) TimeZone.getDefault();
    long tzOffset = tz.getRawOffset();
    if (tz.inDaylightTime(instant)) {
      tzOffset += 3600000;
    }
    now += tzOffset;
    now /= 60000;
    int thisMinute = (int) (now % 60L);
    now /= 60;
    int thisHour = (int) (now % 12);
    int faceDiameter = Math.min(clockRect.width, clockRect.height);
    int faceRadius = faceDiameter / 2;
    int xCenter = clockRect.width / 2;
    int yCenter = clockRect.height / 2;
    int xOrigin = xCenter - faceRadius;
    int yOrigin = yCenter - faceRadius;
    int hourDiameter = faceDiameter / 4;
    int hourRadius = hourDiameter / 2;
    int hourOffset = (faceDiameter - hourDiameter) / 2;
    /*
     * Convert the hour and minute to the corresponding angles (0..360)
     */
    double minute = (double) (thisMinute * 6);
    double hour = ((double) (thisHour * 30)) + (minute / 12.0);
    /*
     * Paint the clock back to front. First, the face.
     */
    displayGraphics.setColor(Color.black);
    displayGraphics.fillOval(clockRect.x + xOrigin, clockRect.y + yOrigin,
        faceDiameter, faceDiameter);
    /*
     * Next, paint the minute (a pie-arc starting at 12 o'clock, clockwise).
     */
    displayGraphics.setColor(Color.gray);
    displayGraphics.fillArc(clockRect.x + xOrigin, clockRect.y + yOrigin,
        faceDiameter, faceDiameter, 90, (int) -minute);
    /*
     * Paint the hour (a circle tangent to the edge of the clock face): use the
     * hour, expressed as an angle, to position the hour circle and the
     * difference between the hour circle diameter and the clock face diameter
     * to determine where the hour circle's center is located with respect to
     * the clock face center.
     */
    double rho = ((double) hourOffset);
    double theta = hour * Math.PI / 180.0;
    int x = xCenter + ((int) (rho * Math.sin(theta)));
    int y = yCenter - ((int) (rho * Math.cos(theta)));
    displayGraphics.setColor(Color.white);
    displayGraphics.fillOval(clockRect.x + x - hourRadius, clockRect.y + y
        - hourRadius, hourDiameter, hourDiameter);
  }

  private void paintMoonImage() {
    Image moonImage = moonPixels.getImage();
    if (moonImage != null) { /* Avoid asynchronous race condition */
      displayGraphics.drawImage(moonImage, moonRect.x, moonRect.y, this);
    }
  }

  /*
   * Render the sunrise image.
   */
  private void paintSunriseImage() {
    Image sunriseImage = sunrisePixels.getImage();
    if (sunriseImage != null) { /* Avoid asynchronous race condition */
      displayGraphics.drawImage(sunriseImage, worldRect.x, worldRect.y, this);
      displayGraphics.setColor(Color.red);
      displayGraphics.fillOval(
          longitudeToXPixel(sunClockData.getLongitude()) - 2,
          latitudeToYPixel(sunClockData.getLatitude()) - 2, 4, 4);
    }
  }

  private void paintTimeBar() {
    displayGraphics.setColor(Color.black);
    FontMetrics fm = displayGraphics.getFontMetrics();
    /*
     * Set "now" to the time at 180 degrees West longitude.
     */
    double now = (((double) (instant.getTime() % 86400000L)) / 3600000.0) - 12.0;
    while (now < 0.0) {
      now += 24.0;
    }
    int stepSize = worldRect.width / 6; /* 6 steps == 4 hours */
    if (fm.stringWidth(" 00:00") < stepSize) {
      int xOffset = worldRect.x - (timeBarOffset / 2);
      int y = timeBarRect.y + fm.getAscent();
      /*
       * The <= in the for loop is intentional: we need to traverse the loop an
       * extra time to avoid a fencepost error.
       */
      for (int x = 0; x <= worldRect.width; x += stepSize) {
        while (now >= 24.0) {
          now -= 24.0;
        }
        displayGraphics.drawString(timeString(now), x + xOffset, y);
        now += 4.0;
      }
    }
  }

  /*
   * Convert a time (in the range 0.0 .. 24.0) to a time string.
   */
  public static String timeString(double value) {
    int hour = (int) Math.floor(value);
    value = (value - (double) hour) * 60.0;
    int minute = (int) (value + 0.5);
    if (minute >= 60) {
      ++hour;
      minute -= 60;
    }
    if (hour >= 24) {
      hour -= 24;
    }
    StringBuffer result = new StringBuffer();
    if (hour < 10) {
      result.append('0');
    }
    result.append(new Integer(hour).toString());
    result.append(':');
    if (minute < 10) {
      result.append('0');
    }
    result.append(new Integer(minute).toString());
    return (result.toString());
  }

  public void update(Graphics g) {
    Dimension d = getSize();
    if (displayImage == null || displayImage.getWidth(null) != d.width
        || displayImage.getHeight(null) != d.height) {
      createDisplayImage();
    }
    displayGraphics.setColor(getBackground());
    displayGraphics.fillRect(0, 0, d.width, d.height);
    /* We must draw the image in this order */
    paintSunriseImage();
    paintClock();
    paintMoonImage();
    paintTimeBar();
    g.drawImage(displayImage, 0, 0, this);
  }

  /**
   * Called by the Observable interface
   */
  public void update(Observable observable, Object object) {
    if (observable == sunClockData) {
      int whatChanged = ((Integer) object).intValue();
      if ((whatChanged & (TIME_CHANGED | LOCATION_CHANGED)) != 0) {
        instant = sunClockData.getJavaDate();
        sunrisePixels.setDate(instant);
        moonPixels.setDate(instant);
        repaint();
      }
    }
  }
}
