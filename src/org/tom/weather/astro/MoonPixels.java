package org.tom.weather.astro;

/**
 * MoonPixels<p>
 *
 * Compute the shape of the moon's shadow and darken the image.<p>
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
 * MoonPixels manages all tasks relating to determining the location of the
 * moon's shadow.
 * 
 * Initialization: moonPixels = new SunrisePixels(originalMoonImage, observer);
 * Where originalMoonImage The full moon image itself. observer A component that
 * can track image-loading progress. Date or time changes:
 * moonPixels.setDate(new Date()); g.drawImage(moonPixels.getImage(), x, y,
 * observer); Interesting accessors: getImagePixels Return the converted pixel
 * vector. This is the parameter passed to MemoryImageSource.
 * 
 */
public class MoonPixels extends MoonPhase {
  /*
   * These are the coordinates (in pixels) of the Apollo 11 Tranquility Base,
   * displayed on July 20 local time when showing the large icon. Derivation
   * from MoonCalc.c: Tranquility base [ 41, 29 ] Moon center [ 27, 28 ] Base
   * offset [ 14 1 ] tranquilityBaseX and tranquilityBaseY are the proportional
   * offset from the center of the moon.
   */
  private static final double tranquilityBaseX = 0.5185; /* 14 / 27 */
  private static final double tranquilityBaseY = 0.0357; /* 1 / 28 */
  private static final int darken = 3; /* Was 4 */
  /*
   * This is the radius of the moon. I edited the image so that the moon is
   * centered in the display area.
   */
  private int moonRadius;
  /*
   * isApollo11 is true if this is July 20th. If so, we display the Apollo 11
   * Commerative Red Dot at tranquility base.
   */
  private Date date;
  private boolean isApollo11;
  private int apolloX;
  private int apolloY;
  private int left[];
  private int right[];
  protected Image moonImage; /* Transformed image */
  protected int[] moonPixels; /* Source image (pixel vector) */
  protected int[] resultPixels; /* Image after transformation */
  protected int imageWidth; /* Image width in pixels */
  protected int imageHeight; /* Image height in pixels */
  protected boolean animationSupported;
  protected MemoryImageSource memoryImageSource;

  public MoonPixels(Image originalImage, Component observer) {
    super();
    /*
     * Load the image - wait for completion
     */
    MediaTracker tracker = new MediaTracker(observer);
    tracker.addImage(originalImage, 0);
    try {
      tracker.waitForAll();
    } catch (InterruptedException e) {
      System.err.println("MoonPixels: image load interrupted: " + e);
    }
    imageWidth = originalImage.getWidth(null);
    imageHeight = originalImage.getHeight(null);
    moonRadius = Math.max(imageWidth, imageHeight) / 2;
    left = new int[moonRadius];
    right = new int[moonRadius];
    moonPixels = new int[imageWidth * imageHeight];
    /*
     * Convert the image pixels to an unrolled vector of pixels
     */
    PixelGrabber pg = new PixelGrabber(originalImage, /* Input image */
    0, 0, imageWidth, imageHeight, /* Source rectangle */
    moonPixels, /* Destination vector */
    0, /* Destination offset */
    imageWidth /* Width of one row */
    );
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      System.err.println("MoonPixels: image conversion interrupted: " + e);
    }
    if ((pg.status() & ImageObserver.ABORT) != 0) {
      System.err.println("MoonPixels: image conversion failed");
    }
    resultPixels = new int[moonPixels.length];
    memoryImageSource = new MemoryImageSource(imageWidth, imageHeight,
        resultPixels, 0, imageWidth);
    memoryImageSource.setAnimated(true);
    moonImage = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
  }

  /**
   * Return the converted image.
   */
  public Image getImage() {
    return (moonImage);
  }

  /*
   * Return the converted image pixel vector
   */
  public int[] getImagePixels() {
    return (resultPixels);
  }

  /**
   * Return true if this is the date of the first manned landing on the moon.
   * (Requires Java 1.1)
   */
  public static boolean isApollo11(Date javaDate) {
    Calendar calendar = Calendar.getInstance();
    int month = calendar.get(Calendar.MONTH); /* Jan == 0 */
    int date = calendar.get(Calendar.DATE);
    return (month == 6 && date == 20); /* Note: 6 == Java.July */
  }

  /**
   * Compute the parameters that we need to draw the moon's bright limb.
   */
  private void recomputeBrightLimb() {
    if (isApollo11) {
      apolloX = moonRadius + ((int) (((double) moonRadius) * tranquilityBaseX));
      apolloY = moonRadius + ((int) (((double) moonRadius) * tranquilityBaseY));
    }
    double phase = getPhase();
    if (phase < 0.01) {
      /*
       * New moon.
       */
      for (int i = 0; i < moonRadius; i++) {
        left[i] = 0;
        right[i] = 0;
      }
    } else {
      double radius = (double) moonRadius;
      double xScale = Math.cos(2.0 * Math.PI * phase);
      for (int i = 0; i < moonRadius; i++) {
        double cp = radius * Math.cos(Math.asin(((double) i) / radius));
        int edge = (int) cp;
        int limb = (int) (xScale * cp);
        /*
         * From the new moon to full moon, the bright part of the moon is from
         * the terminator to the right edge (the left edge is dark). From the
         * full moon to the next new moon, the bright part of the moon is from
         * the left edge to the terminator.
         */
        if (phase < 0.5) {
          left[i] = moonRadius + limb;
          right[i] = moonRadius + edge;
        } else {
          left[i] = moonRadius - edge;
          right[i] = moonRadius - limb;
        }
      }
    }
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
    if (moonImage == null) {
      return;
    }
    super.setDate(date);
    isApollo11 = isApollo11(date);
    /*
     * Recompute the pixels
     */
    recomputeBrightLimb();
    int i = 0; /* Pixel index */
    for (int y = 0; y < imageHeight; y++) { /* Latitude (pixels) */
      int r = Math.abs(y - moonRadius); /* Index in limb edges */
      if (r >= moonRadius) { /* Fell off the edge? */
        for (int x = 0; x < imageWidth; x++) {
          resultPixels[i] = moonPixels[i];
          i++;
        }
      } else { /* Still in the image */
        int leftLimb = left[r];
        int rightLimb = right[r];
        for (int x = 0; x < imageWidth; x++) { /* For this pixel row */
          int rgb = moonPixels[i]; /* Original pixel */
          if (x >= leftLimb && x <= rightLimb) {
            resultPixels[i] = rgb; /* In bright area */
          } else if (isApollo11 && x == apolloX && y == apolloY) {
            rgb = (rgb & 0xFF000000) | 0xFFFF0000; /* Red spot */
          } else {
            int alpha = rgb & 0xFF000000;
            int red = ((rgb & 0x00FF0000) >> 16) / darken;
            int green = ((rgb & 0x0000FF00) >> 8) / darken;
            int blue = ((rgb & 0x000000FF)) / darken;
            resultPixels[i] = (alpha | (red << 16) | (green << 8) | blue);
          }
          i++;
        } /* For all columns in this row */
      } /* If we're still on the moon */
    } /* For all rows in the image */
    memoryImageSource.newPixels();
  }
}
