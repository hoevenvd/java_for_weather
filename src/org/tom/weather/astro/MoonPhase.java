package org.tom.weather.astro;

/**
 * MoonPhase
 * <p>
 * 
 * Compute the phase of the moon for a specified date.
 * <p>
 * 
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.
 * <p>
 * Based on algorithms "Practical Astronomy With Your Calculator" by Patrick
 * Duffett-Smith, Second Edition, Cambridge University Press, 1981 and MoonTool
 * Copyright &copy; 1992 <a href="http://www.formilab.ch/">John Walker</a>.
 * "The source code for Moontool is in the public domain. You are free to use it
 * in any manner you wish."
 * <p>
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
 * @version 1.1 1996.07.24 Set tabs every 4 characters.
 */
// package Classes;
import java.util.*;

public class MoonPhase {
  /*
   * Astronomical constants
   * 
   * 1980 January 0.0 (as a MJD value).
   */
  private static final double epoch = 2444238.5 - 2400000.5;
  /*
   * Constants defining the Sun's apparent orbit
   * 
   * Ecliptic longitude of the Sun at epoch 1980.0
   */
  private static final double elonge = 278.833540;
  /*
   * Ecliptic longitude of the Sun at perigee
   */
  private static final double elongp = 282.596403;
  /*
   * Eccentricity of Earth's orbit
   */
  private static final double eccent = 0.016718;
  /*
   * Semi-major axis of Earth's orbit, km
   */
  private static final double sunsmax = 1.495985e8;
  /*
   * Sun's angular size, degrees, at semi-major axis distance
   */
  private static final double sunangsiz = 0.533128;
  /*
   * Elements of the Moon's orbit, epoch 1980.0
   * 
   * Moon's mean longitude at the epoch
   */
  private static final double mmlong = 64.975464;
  /*
   * Mean longitude of the perigee at the epoch
   */
  private static final double mmlongp = 349.383063;
  /*
   * Mean longitude of the node at the epoch
   */
  private static final double mlnode = 151.950429;
  /*
   * Inclination of the Moon's orbit
   */
  private static final double minc = 5.145396;
  /*
   * Eccentricity of the Moon's orbit
   */
  private static final double mecc = 0.054900;
  /*
   * Moon's angular size at distance a from Earth
   */
  private static final double mangsiz = 0.5181;
  /*
   * Semi-major axis of Moon's orbit in km
   */
  private static final double msmax = 384401.0;
  /*
   * Parallax at distance a from Earth
   */
  private static final double mparallax = 0.9507;
  /*
   * Synodic month (new Moon to new Moon)
   */
  private static final double synmonth = 29.53058868;
  /*
   * Base date for E. W. Brown's numbered series of lunations (1923 January 16)
   */
  private static final double lunatbase = 2423436.0;
  /*
   * Properties of the Earth Radius of Earth in kilometres
   */
  private static final double earthrad = 6378.16;
  /*
   * Limiting parameter for the Kepler equation.
   */
  private static final double kEpsilon = 1.0e-6;
  /*
   * Convert between degrees and radians.
   */
  private static double DegRad = Math.PI / 180.0;
  /*
   * Input variable
   */
  public Date javaDate; /* Java date */
  public double illuminatedFraction; /* pphase Illuminated fraction */
  public double moonAge; /* mage Moon age in days */
  public double distance; /* dist Distance from earth in km */
  public double angularDiameter; /* angdia Angular diameter in degrees */
  public double sunDistance; /* sundist Distance to sun in km */
  public double sunAngularDiameter; /* suangdia Sun's angular diameter */
  public double moonFraction; /* phase Moon fraction (0.0 .. 1.0) */
  public double limbPositionAngle; /* Position angle of the moon bright limb */

  /**
   * MoonPhase computes the phase of the moon (and a few other constants) for a
   * specific Java date. Usage:
   * <p>
   * 
   * <pre>
   * MoonPhase moonPhase = new MoonPhase(someJavaDate);
   * double thePhase = moonPhase.getPhase();
   * double fraction = moonPhase.moonFraction;
   * </pre>
   * 
   * The following public values are available
   * 
   * <pre>
   *  illuminatedFraction	  The illuminated fraction of the moon's disk.
   *  moonPhase			  The phase (a fraction between 0.0 and 1.0)
   *  moonAge				  The moon's age in days and fraction.
   *  distance				  The distance of the moon from the center of the earth
   *  angularDiameter		  The angular diameter subtended by the moon as seen by
   * 						  an observer at the center of the earth.
   *  sunDistance			  The distance (from what) to the sun in kilometers.
   *  sunAngularDiameter	  The angular diameter subtended by the sun as seen by
   * 						  an observer at the center of the earth.
   *  limbPositionAngle	  The position angle of the moon's bright limb, measured
   * 						  counter-clockwise from North.
   * </pre>
   */
  public MoonPhase() {
    this(new Date());
  }

  public MoonPhase(Date javaDate) {
    setDate(javaDate);
  }

  private double fixAngle(double angle) {
    double result = angle - 360.0 * (Math.floor(angle / 360.0));
    return (result);
  }

  public double getPhase() {
    return (moonFraction);
  }

  /**
   * Solve the equation of Kepler
   */
  private double kepler(double m, double ecc) {
    m *= DegRad;
    double e = m;
    for (double delta = 1.0; Math.abs(delta) > kEpsilon;) {
      delta = e - ecc * Math.sin(e) - m;
      e -= delta / (1 - ecc * Math.cos(e));
    }
    return (e);
  }

  /**
   * Insert the method's description here. Creation date: (6/11/00 7:29:01 AM)
   * 
   * @param args
   *          java.lang.String[]
   */
  public static void main(String[] args) {
    MoonPhase p = new MoonPhase();
    System.out.println(p);
  }

  public void setDate(Date javaDate) {
    double MJD = Astro.MJD(javaDate); /* Modified Julian Date */
    double Day = MJD - epoch; /* Date within epoch */
    double N = fixAngle((360.0 / 365.2422) * Day); /* Mean anomaly of the Sun */
    double M = fixAngle(N + elonge - elongp); /* Convert from perigee */
    /* co-ordinates to epoch 1980.0 */
    double Ec = kepler(M, eccent); /* Solve equation of Kepler */
    Ec = Math.sqrt((1.0 + eccent) / (1.0 - eccent)) * Math.tan(Ec / 2.0);
    Ec = 2.0 * Math.atan(Ec) / DegRad; /* True anomaly in degrees */
    double Lambdasun = fixAngle(Ec + elongp); /* Sun's geocentric ecliptic */
    /* longitude */
    /* Orbital distance factor */
    double F = ((1.0 + eccent * Astro.cos(Ec)) / (1.0 - eccent * eccent));
    double SunDist = sunsmax / F; /* Distance to Sun in km */
    double SunAng = F * sunangsiz; /* Sun's angular size in degrees */
    /* Calculation of the Moon's position */
    double ml = fixAngle(13.1763966 * Day + mmlong); /* Moon's mean longitude */
    double MM = fixAngle(ml - 0.1114041 * Day - mmlongp); /* Moon's mean anomaly */
    double MN = fixAngle(mlnode - 0.0529539 * Day); /*
                                                     * Ascending node mean
                                                     * anomaly
                                                     */
    double Ev = 1.2739 * Astro.sin(2.0 * (ml - Lambdasun) - MM); /* Evection */
    double Ae = 0.1858 * Astro.sin(M); /* Annual equation */
    double A3 = 0.37 * Astro.sin(M); /* Correction term */
    double MmP = MM + Ev - Ae - A3; /* Corrected anomaly */
    double mEc = 6.2886 * Astro.sin(MmP); /* Correction for center equation */
    double A4 = 0.214 * Astro.sin(2 * MmP); /* Yet another correction term */
    double lP = ml + Ev + mEc - Ae + A4; /* Corrected longitude */
    double V = 0.6583 * Astro.sin(2 * (lP - Lambdasun)); /* Variation */
    double lPP = lP + V; /* True longitude */
    double NP = MN - 0.16 * Astro.sin(M); /* Corrected longitude of the node */
    double y = Astro.sin(lPP - NP) * Astro.cos(minc); /* Y inclin coord. */
    double x = Astro.cos(lPP - NP); /* X inclination coordinate */
    double Lambdamoon = Astro.atan2Deg(y, x); /* Ecliptic longitude */
    Lambdamoon += NP;
    double BetaM = Math.asin(Astro.sin(lPP - NP) * Astro.sin(minc)) / DegRad; /*
                                                                               * Ecliptic
                                                                               * latitude
                                                                               */
    /* Calculation of the phase of the Moon */
    double MoonAgeDegrees = lPP - Lambdasun; /* Moon age in degrees */
    double MoonPhase = (1.0 - Astro.cos(MoonAgeDegrees)) / 2.0; /* True phase */
    double MoonDist = (msmax * (1.0 - mecc * mecc))
        / (1.0 + mecc * Astro.cos(MmP + mEc)); /* Distance from earth ctr */
    double MoonDFrac = MoonDist / msmax; /* Moon diameter fraction */
    double MoonAng = mangsiz / MoonDFrac; /* Moon angular diameter */
    double MoonPar = mparallax / MoonDFrac; /* Moon parallax */
    /*
     * Store results in object public variables.
     */
    illuminatedFraction = MoonPhase;
    moonAge = synmonth * (fixAngle(MoonAgeDegrees) / 360.0);
    distance = MoonDist;
    angularDiameter = MoonAng;
    sunDistance = SunDist;
    sunAngularDiameter = SunAng;
    moonFraction = fixAngle(MoonAgeDegrees) / 360.0;
    /*
     * Compute the position angle of the moon's bright limb. From Meeus,
     * Algorithm from Astronomical Formulae for Calculators, 2nd Edition. p. 61.
     */
    double solar[] = Astro.solarEphemeris(MJD);
    double lunar[] = Astro.lunarEphemeris(MJD);
    double cosSunDec = Astro.cos(solar[Astro.DEC]);
    double cosMoonDec = Astro.cos(lunar[Astro.DEC]);
    double cosDeltaRA = Astro.cos(lunar[Astro.RA] - solar[Astro.RA]);
    double sinDeltaRA = Astro.sin(lunar[Astro.RA] - solar[Astro.RA]);
    double sinSunDec = Astro.sin(solar[Astro.DEC]);
    double sinMoonDec = Astro.sin(lunar[Astro.DEC]);
    limbPositionAngle = Astro.atan2Deg((cosSunDec * sinDeltaRA), (cosMoonDec
        * sinSunDec - cosSunDec * cosDeltaRA));
    if (false) {
      System.out.println("                 MJD: " + MJD);
      System.out.println("Illuminated Fraction: " + illuminatedFraction);
      System.out.println("            Moon Age: " + moonAge);
      System.out.println("       Moon distance: " + distance);
      System.out.println("        Sun Distance: " + sunDistance);
      System.out.println("Sun Angular diameter: " + sunAngularDiameter);
      System.out.println("       Moon Fraction: " + moonFraction);
    }
  }

  /**
   * Return the age of the moon in days, hours, minutes as a string.
   */
  public String toString() {
    getPhase();
    int days = (int) Math.floor(moonAge);
    double fraction = moonAge - Math.floor(moonAge);
    int hours = (int) Math.floor(24.0 * fraction);
    int minutes = ((int) (1440.0 * fraction)) % 60;
    String result = new String(days + ((days == 1) ? " day, " : " days, ")
        + hours + ((hours == 1) ? " hour, " : " hours, ") + minutes
        + ((minutes == 1) ? " minute" : " minutes") + ", "
        + ((int) (illuminatedFraction * 100.0)) + "% full");
    return (result);
  }
}
