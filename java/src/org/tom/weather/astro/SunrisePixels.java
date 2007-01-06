package org.tom.weather.astro;

/**
 * SunrisePixels<p>
 *
 * Compute the shape of the sun's terminator (sunrise and sunset lines)
 * and darken the earth map accordingly.<p>
 *
 * Based, in part, on sample code in The Java Class Libraries (Chen and
 * Lee), pp. 753 ff.
 *
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.<p>
 * World map Copyright © 1992-1998 Apple Computer Inc. All Rights Reserved.<p>
 *
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies. This
 * software may not be distributed for fee or as part of commercial, "shareware,"
 * and/or not-for-profit endevors including, but not limited to, CD-ROM collections,
 * online databases, and subscription services without specific license.<p>
 *
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 1.1
 * 1996.09.17
 */
import java.util.*;
import java.awt.*;
import java.awt.image.*;

/**
 * SunrisePixels manages all tasks relating to determining the location of the
 * sunrise/sunset/twilight terminator and constructing a (rectangular) world map
 * showing the terminator in its proper location. Note that the terminator
 * location is only dependent on GMT time: SunrisePixels does not provide an
 * accurate sunrise/sunset time for any particular location.
 * 
 * Initialization: sunrisePixels = new SunrisePixels(originalImage, observer);
 * Where originalImage The world map itself. observer A component that can track
 * image-loading progress. Date or time changes: sunrisePixels.setDate(new
 * Date()); displayCanvas.repaint(); This sets the date and time. The image
 * needs to be repainted. Repaint: g.drawImage(sunrisePixels.getImage(), x, y,
 * observer); Interesting accessors: getImagePixels Return the converted pixel
 * vector. This is the parameter passed to MemoryImageSource.
 * 
 */
public class SunrisePixels {
  /**
   * These values define the location of the horizon for an observer at sea
   * level. -0¡50' Sunrise/Sunset -6¡ Civil Twilight (default twilight)
   */
  public static final double SUNRISE = -(50.0 / 60.0);
  public static final double CIVIL_TWILIGHT = -6.0;
  /**
   * The sunrise and moonrise algorithms return values in a double[] vector
   * where result[RISE] is the time of sunrise and result[SET] is the value of
   * sunset.
   */
  public static final int RISE = 0;
  public static final int SET = 1;
  /**
   * ABOVE_HORIZON and BELOW_HORIZON are returned for sun and moon calculations
   * where the astronomical object does not cross the horizon.
   */
  public static final double ABOVE_HORIZON = Double.POSITIVE_INFINITY;
  public static final double BELOW_HORIZON = Double.NEGATIVE_INFINITY;
  /*
   * Special index values for the image conversion.
   */
  protected static final int DARK = -1;
  protected static final int BRIGHT = -2;
  /*
   * DAYLIGHT, TWILIGHT, and NIGHT are divisors used to darken pixels.
   */
  protected static final int DAYLIGHT = 0; /* No darkening */
  protected static final int TWILIGHT = 2; /* Pixel / 2 */
  protected static final int NIGHT = 3; /* Pixel / 3 */
  protected static final int MAX_EVENTS = 5;
  protected static final int LOC = 0;
  protected static final int DIV = 1;
  /*
   * Astronomical variables for sunrise/sunset computation. These change during
   * the computation.
   */
  protected Date date; /* Java date */
  protected double DATE; /* Modified Julian Date */
  protected double timeOfDayGMT; /* Time (to warp the image) */
  protected double latitude; /* North is positive */
  protected double longitude; /* East is positive */
  protected double sinHorizon; /* sin(horizon) */
  protected double sinLatitude; /* sin(latitude) */
  protected double cosLatitude; /* cos(latitude) */
  /*
   * These values are computed from the solar ephemeris
   */
  protected double GHA; /* Greenwich Hour Angle */
  protected double sinDeclin; /* sin(declination) */
  protected double cosDeclin; /* cos(delination) */
  /*
   * Each call to riseSet() computes these times.
   */
  protected double[] riseSet = new double[2];
  protected double[] twilight = new double[2];
  protected static final double RISING = +1.0; /* RISING and SETTING */
  protected static final double SETTING = -1.0; /* configure ephemeris */
  /**
   * Degrees -> Radians: degree * DegRad Radians -> Degrees: radians / DegRad
   */
  public static final double DegRad = (Math.PI / 180.0);
  /**
   * javaEpochMJD: Modified Julian Date of the Java Epoch msecPerDay Number of
   * Java "ticks" in a day.
   */
  public static final double javaEpochMJD = 40587.0;
  public static final double msecPerDay = 86400000;
  /*
   * Variables for the map image eventIndex and eventDivisor designate the
   * transition between day, twilight, and night. eventIndex is the x-pixel
   * value, eventDivisor is one of DAYLIGHT, TWILIGHT, or NIGHT. These values
   * are computed on a row by row (i.e., latitude) basis. nEvents is the number
   * of events.
   */
  protected int[] eventIndex = new int[MAX_EVENTS];
  protected int[] eventDivisor = new int[MAX_EVENTS];
  protected int nEvents;
  protected int[] mapPixels; /* Source image (pixel vector) */
  protected int[] resultPixels; /* Image after transformation */
  protected int imageWidth; /* Image width in pixels */
  protected int imageHeight; /* Image height in pixels */
  protected boolean animationSupported;
  protected MemoryImageSource memoryImageSource;
  protected Image sunriseImage;
  /*
   * Debug variables.
   */
  protected static final boolean drawLatLongLines = false; /* Debug */
  protected static final int latLongFraction = 8; /* Debug */
  protected int latOffset = 0;
  protected int longOffset = 0;

  public SunrisePixels(Image originalImage, Component observer) {
    /*
     * Load the image - wait for completion
     */
    MediaTracker tracker = new MediaTracker(observer);
    tracker.addImage(originalImage, 0);
    try {
      tracker.waitForAll();
    } catch (InterruptedException e) {
      System.err.println("SunrisePixels: image load interrupted: " + e);
    }
    imageWidth = originalImage.getWidth(null);
    imageHeight = originalImage.getHeight(null);
    latOffset = imageHeight / latLongFraction;
    longOffset = imageWidth / latLongFraction;
    mapPixels = new int[imageWidth * imageHeight];
    /*
     * Convert the image pixels to an unrolled vector of pixels
     */
    PixelGrabber pg = new PixelGrabber(originalImage, /* Input image */
    0, 0, imageWidth, imageHeight, /* Source rectangle */
    mapPixels, /* Destination vector */
    0, /* Destination offset */
    imageWidth /* Width of one row */
    );
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      System.err.println("SunrisePixels: image conversion interrupted: " + e);
      mapPixels = null;
    }
    if ((pg.status() & ImageObserver.ABORT) != 0) {
      System.err.println("SunrisePixels: image conversion failed");
      mapPixels = null;
    }
    resultPixels = new int[mapPixels.length];
    try {
      String version = System.getProperty("java.version");
      animationSupported = (version.compareTo("1.1") >= 0);
    } catch (SecurityException e) {
      animationSupported = false;
    }
    memoryImageSource = new MemoryImageSource(imageWidth, imageHeight,
        resultPixels, 0, imageWidth);
    if (animationSupported) {
      memoryImageSource.setAnimated(true);
    }
    sunriseImage = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
    setDate(new Date());
  }

  public static double acos(double value) {
    return (Math.acos(value) / DegRad);
  }

  /**
   * The Explanatory Supplement defines an iterative algorithm.
   * 
   * @param riseSet
   *          +1.0 for rise, -1.0 for set.
   * @return The time that the sun crosses the horizon.
   * 
   * <pre>
   * 		ABOVE_HORIZON	Returned if the sun never rises
   * 		BELOW_HORIZON	Returned if the sun never sets
   * </pre>
   */
  protected double computeRiseSet(double riseSet) {
    double UT = 0.0;
    int aboveCount = 0;
    int belowCount = 0;
    /*
     * Ensure that there is a rise/set at this latitude. This will be the case
     * if startBelow is below the horizon and endBelow above the horizon or
     * vice-versa
     */
    double UT0 = 12.0;
    for (int i = 0; i < 20 && aboveCount < 3 && belowCount < 3; i++) {
      solarEphemeris(UT0);
      /*
       * The hour angle at time UT0 is T, where cosT is
       */
      double T;
      double cosT = (sinHorizon - (sinLatitude * sinDeclin))
          / (cosLatitude * cosDeclin);
      if (cosT > +1.0) {
        ++belowCount;
        T = 0;
      } else if (cosT < -1.0) {
        ++aboveCount;
        T = 180.0;
      } else {
        aboveCount = 0;
        belowCount = 0;
        T = Math.acos(cosT) / DegRad;
      }
      UT = UT0 - (GHA + longitude + (riseSet * T)) / 15.0;
      UT = mod(UT, 24.0);
      if (Math.abs(UT - UT0) < 0.0008)
        break;
      UT0 = UT;
    }
    if (belowCount > 0)
      UT = BELOW_HORIZON;
    else if (aboveCount > 0)
      UT = ABOVE_HORIZON;
    else {
      UT = mod(UT, 24.0);
    }
    return (UT);
  }

  public static double cos(double value) {
    return (Math.cos(value * DegRad));
  }

  /**
   * Return the converted image.
   */
  public Image getImage() {
    return (sunriseImage);
  }

  public int getImageHeight() {
    return (imageHeight);
  }

  /*
   * Return the converted image pixel vector
   */
  public int[] getImagePixels() {
    return (resultPixels);
  }

  /**
   * Accessors.
   */
  public int getImageWidth() {
    return (imageWidth);
  }

  /**
   * Return the system timezone in milliseconds, adjusted for Daylight Savings
   * Time if necessary. Use Java 1.1 getRawOffset if it's available.
   */
  public static long getSystemTimezone() {
    long tzOffset = 0;
    try {
      if (false) {
        throw new ClassNotFoundException(); /* Hack for Java 1.0.2 */
      }
      Calendar newtime = Calendar.getInstance();
      int tzMinutes = newtime.get(Calendar.ZONE_OFFSET)
          + newtime.get(Calendar.DST_OFFSET);
      ;
      tzOffset = (long) (tzMinutes * 60000);
    } catch (ClassNotFoundException e) {
      System.err.println("Hiero getTimeZone: " + e);
    }
    return (tzOffset);
  }

  public static boolean isAbove(double value[]) {
    return (value[RISE] == ABOVE_HORIZON || value[SET] == ABOVE_HORIZON);
  }

  public static boolean isBelow(double value[]) {
    return (value[RISE] == BELOW_HORIZON || value[SET] == BELOW_HORIZON);
  }

  public static boolean isEvent(double value[]) {
    return (value[RISE] != ABOVE_HORIZON && value[RISE] != BELOW_HORIZON
        && value[SET] != ABOVE_HORIZON && value[SET] != BELOW_HORIZON);
  }

  /**
   * Convert a javaDate at midnight of this day to the equivalent Modified
   * Julian Date. Note that this will be referenced to the local timezone.
   * 
   * @param javaDate
   *          The Java date (this cannot be used for historical dates)
   * @return modified Julian Date
   */
  public static double midnightMJD(Date javaDate) {
    double result = Math.floor(MJD(javaDate));
    return (result);
  }

  /**
   * Convert a javaDate to the equivalent Modified Julian Date. Note that this
   * will be referenced to the local timezone.
   * 
   * @param javaDate
   *          The Java date (this cannot be used for historical dates)
   * @return modified Julian Date
   */
  public static double MJD(Date javaDate) {
    long localTime = javaDate.getTime() + getSystemTimezone();
    double result = ((double) localTime) / msecPerDay;
    result += javaEpochMJD;
    return (result);
  }

  /*
   * Modulus function that always returns a positive value. For example,
   * AstroMath.mod(-3, 24) == 21
   */
  public static double mod(double numerator, double denomenator) {
    double result = Math.IEEEremainder(numerator, denomenator);
    if (result < 0.0)
      result += denomenator;
    return (result);
  }

  /**
   * Return the latitude corresponding to a a pixel. This function is specific
   * to the Apple world map, which uses a non-standard (and non-recommended)
   * equirectangular cylindrical projection.
   * 
   * @param yPixel
   *          The pixel offset (0 .. imageHeight)
   * @param imageHeight
   *          The image height.
   * @return The latitude in the range of +90 (North Pole) to -90 (South Pole)
   */
  protected double pixelToLatitude(int yPixel) {
    double result = 90.0 - (((double) yPixel) / ((double) imageHeight) * 180.0);
    return (result);
  }

  /**
   * Return the longitude corresponding to a a pixel. This function is specific
   * to the Apple world map, which uses a non-standard (and non-recommended)
   * equirectangular cylindrical projection.
   * 
   * @param xPixel
   *          The pixel offset (0 .. imageWidth)
   * @param imageWidth
   *          The image width.
   * @return The longitude in the range of -180 (far West) to +180 (far East)
   */
  protected double pixelToLongitude(int xPixel) {
    double result = (((double) xPixel) / ((double) imageWidth) * 360.0) - 180.0;
    return (result);
  }

  /*
   * Astronomical formulae for sunrise/sunset computation
   */
  /**
   * Compute the time of sunrise and sunset for this date. This is an iterative
   * algorithm following the "systematic" approach outlined in the Explanatory
   * Supplement to the Astronomical Almanac. The times are returned in the
   * observer's local time.
   * 
   * @param latitude
   *          The observer's latitude
   * @param horizon
   *          The adopted true altitude of the horizon in degrees. Use one of
   *          the following values defined in Constants.java:
   * 
   * <pre>
   * 	SUNRISE					 -0¡50'
   * 	CIVIL_TWILIGHT			 -6¡00'
   * 	NAUTICAL_TWILIGHT		-12¡00'
   * 	ASTRONOMICAL_TWILIGHT	-18¡00'
   * </pre>
   * 
   * Here are some test values. The "correct" values were taken from
   *          the <a href="http://tycho.usno.navy.mil/"> United States Naval
   *          Observatory</a> Web page.
   *          <p>
   *          1997.01.01, latitude 0.0, longitude 0.0, zone 0.0
   *          <p>
   * 
   * <pre>
   *   SunRiseSet   Civil Twil.    Naut Twil.
   *  06:00 18:07   05:37 18:30   05:11 18:56
   * <p>
   *  1997.02.21, latitude 37.8N, longitude 122.4W, zone -8.0
   * <p>
   * &lt;pre&gt;
   *   SunRiseSet   Civil Twil.
   *  06:52 17:56   06:25 18:22
   *  &lt;/pre&gt;
   * 
   */
  public void riseSet(double[] result, double horizon) {
    this.sinLatitude = Math.sin(latitude * DegRad);
    this.cosLatitude = Math.cos(latitude * DegRad);
    this.sinHorizon = Math.sin(horizon * DegRad);
    result[RISE] = computeRiseSet(RISING);
    result[SET] = computeRiseSet(SETTING);
  }

  /**
   * Set the date - this will redraw the map.
   */
  public void setDate(Date date) {
    /*
     * There can be a race condition at the start when the JVM is trying to load
     * all of the images. To avoid problems, we stall here until the image load
     * is complete.
     */
    if (sunriseImage == null) {
      return;
    }
    this.date = date;
    this.DATE = midnightMJD(date);
    double timeOfDay = (double) ((date.getTime() / 60000L) % 1440L);
    this.timeOfDayGMT = mod(((timeOfDay + 720.0)) / 60.0, 24.0);
    /*
     * It would be faster to compute sunrise/sunset from the equator outwards.
     * That way, we don't need to recompute higher latitudes part of the year.
     * This, however, is much simpler.
     */
    int i = 0; /* mapPixels[] index */
    if (false) {
      System.out.println("getPixels: imageWidth " + imageWidth
          + ", imageHeight " + imageHeight);
    }
    for (int y = 0; y < imageHeight; y++) { /* Latitude (in pixels) */
      /*
       * pixelToLatitude is specific to the image projection.
       */
      latitude = pixelToLatitude(y);
      /*
       * The sunrise algorithm breaks down for the North and South poles. Fiddle
       * the extreme latitudes just a bit. This would only affect a single
       * y-pixel line.
       */
      if (latitude >= 89.9) {
        latitude = 89.9;
      } else if (latitude <= -89.9) {
        latitude = -89.0;
      }
      nEvents = 0;
      riseSet(riseSet, SUNRISE);
      riseSet(twilight, CIVIL_TWILIGHT);
      if (isAbove(twilight) && isAbove(riseSet)) {
        store(imageWidth, DAYLIGHT);
      } else if (isBelow(twilight) && isBelow(riseSet)) {
        store(imageWidth, NIGHT);
      } else {
        if (isEvent(twilight)) {
          /*
           * if (y == (imageHeight / 2)) { System.out.println("riseSet " +
           * SimpleTimeBarCanvas.timeString(riseSet[0]) + ", " +
           * SimpleTimeBarCanvas.timeString(riseSet[1]) ); }
           */
          store(timeToPixel(twilight[RISE]), NIGHT);
          if (isEvent(riseSet)) {
            store(timeToPixel(riseSet[RISE]), TWILIGHT);
            store(timeToPixel(riseSet[SET]), DAYLIGHT);
          }
          store(timeToPixel(twilight[SET]), TWILIGHT);
        } else {
          store(timeToPixel(riseSet[RISE]), TWILIGHT);
          store(timeToPixel(riseSet[SET]), DAYLIGHT);
        }
        store(imageWidth, eventDivisor[0]); /* Wrap around */
      }
      int j = 0;
      for (int x = 0; x < imageWidth; x++) {
        int rgb = mapPixels[i];
        while (j < MAX_EVENTS && x > eventIndex[j]) {
          j++;
        }
        int divisor = eventDivisor[j];
        if (divisor != DAYLIGHT) {
          int alpha = rgb & 0xFF000000;
          int red = (rgb & 0x00FF0000) >> 16;
          int green = (rgb & 0x0000FF00) >> 8;
          int blue = (rgb & 0x000000FF);
          red /= divisor;
          green /= divisor;
          blue /= divisor;
          rgb = (alpha | (red << 16) | (green << 8) | blue);
        }
        if (drawLatLongLines && ((x % longOffset) == 0 || (y % latOffset) == 0)) {
          rgb |= 0x00FF0000;
        }
        resultPixels[i++] = rgb;
        if (false && x == imageWidth / 2) {
          System.out.println("y = " + y + ", x = " + x + ", i = " + i
              + ", map = " + Integer.toHexString(mapPixels[i]) + ", dst = "
              + Integer.toHexString(resultPixels[i]));
        }
      }
    }
    if (animationSupported) {
      memoryImageSource.newPixels();
    } else {
      sunriseImage.flush();
    }
  }

  /**
   * Trignometric classes that take degree arguments.
   */
  public static double sin(double value) {
    return (Math.sin(value * DegRad));
  }

  /**
   * Compute the altitude of the sun for a given Universal Time at a specified
   * latitude.
   * 
   * @param UT
   *          Universal time in hours (0.0 .. 24.0)
   * @param latitude
   *          Observer's latitude (degrees)
   * @return altitude above the horizon (degrees)
   */
  protected void solarEphemeris(double UT) {
    /*
     * T Number of centuries from J2000. (Original used JD, we use MJD).
     */
    double T = (DATE + (UT / 24.0) - 51544.5) / 36525.0;
    /*
     * Solar arguments L Mean longitude corrected for aberration (degrees) G
     * Mean anomaly (radians) Lambda Ecliptic longitude (radians) Epsilon The
     * obliquity of the ecliptic (radians)
     */
    double L = mod(280.460 + 36000.770 * T, 360.0);
    double G = mod(357.528 + 35999.050 * T, 360.0);
    double gRad = G * DegRad;
    double sinG = Math.sin(gRad);
    double sin2G = Math.sin(gRad * 2.0);
    double gFactor = 1.915 * sinG + 0.020 * sin2G;
    double Lambda = L + gFactor;
    double lambdaRad = Lambda * DegRad;
    double Epsilon = 23.4393 - 0.01300 * T;
    double epsilonRad = Epsilon * DegRad;
    /*
     * Compute the ephemeris E Equation of time GHA Greenwich Hour Angle
     * (degrees) sinDeclin sin(Declination) cosDeclin cos(Declination)
     */
    double E = -gFactor + 2.466 * Math.sin(lambdaRad * 2.0) - 0.053
        * Math.sin(lambdaRad * 4.0);
    GHA = ((15.0 * UT) - 180.0 + E);
    sinDeclin = Math.sin(epsilonRad) * Math.sin(lambdaRad);
    double Sigma = Math.asin(sinDeclin);
    cosDeclin = Math.cos(Sigma);
  }

  protected void store(int imageX, int divisor) {
    int i;
    for (i = nEvents; i > 0 && imageX < eventIndex[i - 1]; --i) {
      eventIndex[i] = eventIndex[i - 1];
      eventDivisor[i] = eventDivisor[i - 1];
    }
    eventIndex[i] = imageX;
    eventDivisor[i] = divisor;
    nEvents++;
  }

  /**
   * Convert a time in the range 0.0 to 24.0 to a pixel longitude in the current
   * image. This function is only valid for the SunClock world map projection,
   * in which the longitude is proportionate to the pixel offset from the
   * midpoint of the image. Note, further, that this function is not valid for
   * ABOVE_HORIZON or BELOW_HORIZON.
   */
  protected int timeToPixel(double theTime) {
    /*
     * Convert theTime fraction to a fraction of a day then convert it to a
     * pixel offset within the current image
     */
    theTime = mod((theTime - timeOfDayGMT) / 24.0, 1.0);
    return ((int) (theTime * imageWidth));
  }
}
