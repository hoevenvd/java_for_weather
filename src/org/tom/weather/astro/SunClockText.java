package org.tom.weather.astro;

/**
 * SunClockText displays the time, earth, and moon images.<p>
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
import java.text.*;

/*
 * For Java 1.0.2 compatibility, this must be a Canvas subclass
 */
public class SunClockText extends OutlinePanel implements Observer, Constants {
  private SunClockData sunClockData;
  private OutlineElement localeName;
  private OutlineElement localTimeString;
  private OutlineElement gmtTimeString;
  private OutlineElement localeLatLongString;
  private OutlineElement sunName;
  private OutlineElement sunRiseSetString;
  private OutlineElement sunTwilightString;
  private OutlineElement sunNauticalString;
  private OutlineElement sunAstroString;
  private OutlineElement sunMiddayString;
  private OutlineElement moonName;
  private OutlineElement moonRiseSetString;
  private OutlineElement moonAgeString;
  /* */
  private OutlineElement jewishName;
  private OutlineElement jewishMorningAlot;
  private OutlineElement jewishMorningTfilin;
  private OutlineElement jewishSofZman;
  private OutlineElement jewishMidday;
  private OutlineElement jewishMincha;
  private OutlineElement jewishSabbathEnd;
  private OutlineElement muslimName;
  private OutlineElement muslimDawn;
  private OutlineElement muslimSunrise;
  private OutlineElement muslimMidday;
  private OutlineElement muslimAsrShafi;
  private OutlineElement muslimAsrHanafi;
  private OutlineElement muslimSunset;
  private OutlineElement muslimEvening;

  public SunClockText(SunClockData sunClockData) {
    super(false, false);
    this.sunClockData = sunClockData;
    setBackground(Color.white);
    setFont(sunClockData.getDisplayFont());
    outlineComponent.setFont(sunClockData.getDisplayFont());
    localeName = addText(0, "Location");
    localTimeString = addText(1, "");
    gmtTimeString = addText(1, "");
    localeLatLongString = addText(1, "");
    sunName = addText(0, "Sun");
    sunRiseSetString = addText(1, "");
    sunTwilightString = addText(1, "");
    sunNauticalString = addText(1, "");
    sunAstroString = addText(1, "");
    sunMiddayString = addText(1, "");
    moonName = addText(0, "Moonrise and Moonset");
    moonRiseSetString = addText(1, "");
    moonAgeString = addText(1, "");
    /*
     * Jewish times of religious observation
     */
    jewishName = addText(0, "Jewish Religious Observance");
    addText(1,
        "Caution: these values have not been verified by a religious authority");
    jewishMorningAlot = addText(1, "");
    jewishMorningTfilin = addText(1, "");
    jewishSofZman = addText(1, "");
    jewishMidday = addText(1, "");
    jewishMincha = addText(1, "");
    jewishSabbathEnd = addText(1, "");
    addText(1, "Notes on Jewish Religious Times");
    addText(2, "Morning Alot HaShachar: the earliest time to begin Shacharit");
    addText(2, "Tallit v'Tfilin: the time to put on tallit and tfilin");
    addText(2, "Midday: the midpoint between sunrise and sunset");
    addText(2, "Mincha K'tana: the earliest time to begin Mincha");
    addText(2, "Sabbath end: the end of Sabbath or holiday observation");
    addText(2, "The times are not correct for high latitudes when the");
    addText(2, "sun can be above or below the horizon all day.");
    /*
     * Muslim times of religious observation
     */
    muslimName = addText(0, "Muslim Religous Observance");
    addText(1,
        "Caution: these values have not been verified by a religious authority");
    muslimDawn = addText(1, "");
    muslimSunrise = addText(1, "");
    muslimMidday = addText(1, "");
    muslimAsrShafi = addText(1, "");
    muslimAsrHanafi = addText(1, "");
    muslimSunset = addText(1, "");
    muslimEvening = addText(1, "");
    addText(1, "Notes on Muslim Religious Times");
    addText(2, "This program computes midday as the midpoint between sunrise");
    addText(2, "and sunset. The correct computation uses the sun's highest"
        + " point in the sky.");
    addText(2, "Fajr (dawn) is the beginning of astronomical morning twilight.");
    addText(2, "Shurooq (sunrise) is the time of civil sunrise.");
    addText(2,
        "Zhuhr (midday) is the midpoint between sunrise and sunset. It would be");
    addText(2,
        "more correct to use the time of the sun's highest point in the sky.");
    addText(2, "Asr (Shafi) is 1/3 of the way between midday and sunset.");
    addText(2, "Asr (Hanafi) is 2/3 of the way between midday and sunset.");
    addText(2, "Maghrib (sunset) is the time of civil sunset.");
    addText(
        2,
        "Isha (evenng) is the end of evening astronomical twilight and beginning of night.");
    addText(2, "The times are not correct for high latitudes when the");
    addText(2, "sun can be above or below the horizon all day.");
    addText(2,
        "This program computes the times of dawn and evening using the time");
    addText(2,
        "of astronomical twilight, when the sun is 18\u00B0 below the horizon,");
    addText(2, "Note, however, that some American practice uses 15\u00B0.");
    /*
     * By default, we open some of the interior sections.
     */
    localeName.setOpen(true);
    sunName.setOpen(true);
    moonName.setOpen(true);
    sunClockData.addObserver(this);
  }

  private String eventString(Sun sun, double horizon, int which, String label) {
    double[] rise = sun.riseSet(sunClockData.getLatitude(), horizon);
    if (Astro.isEvent(rise[which])) {
      return (getRiseSetString(rise[which], label + " at "));
    } else {
      return (label + " cannot be computed.");
    }
  }

  /**
   * Compute the string corresponding to this Sunrise/Sunset.
   * 
   * @param riseSetValue
   *          the time of sunrise or sunset
   * @param whichEvent
   *          If there is an event, this will be prepended to the time.
   * @return The proper string: null string if no event.
   */
  public static String getRiseSetString(double eventValue, String whichEvent /*
                                                                               * "Sunrise"
                                                                               * or
                                                                               * "Sunset"
                                                                               */
  ) {
    if (Astro.isEvent(eventValue)) {
      return (whichEvent + AstroFormat.hm(eventValue));
    } else {
      return ("");
    }
  }

  private String midday(Sun sun) {
    String result;
    double[] midday = sun.riseSet(sunClockData.getLatitude(), SUNRISE);
    if (Astro.isEvent(midday)) {
      result = getRiseSetString((midday[RISE] + midday[SET]) / 2, "Mid-day: ");
    } else {
      result = "Cannot determine mid-day.";
    }
    return (result);
  }

  /**
   * Formatters.
   */
  public static String riseSetString(double rise[]) {
    StringBuffer text = new StringBuffer();
    if (rise[RISE] == ABOVE_HORIZON && rise[SET] == ABOVE_HORIZON) {
      text.append("Sun is above horizon all day");
    } else if (rise[RISE] == BELOW_HORIZON && rise[SET] == BELOW_HORIZON) {
      text.append("Sun does not rise today");
    } else {
      if (Astro.isEvent(rise[RISE])) {
        text.append(getRiseSetString(rise[RISE], "Sunrise at "));
        if (Astro.isEvent(rise[SET])) {
          text.append(getRiseSetString(rise[SET], ", Sunset at "));
        } else {
          text.append(", Sun does not set");
        }
      } else {
        text.append(getRiseSetString(rise[SET], "Sunset at "));
      }
    }
    return (text.toString());
  }

  private String twilightString(Sun sun, double horizon, String label) {
    double[] rise = sun.riseSet(sunClockData.getLatitude(), horizon);
    if (Astro.isEvent(rise[RISE])) {
      if (Astro.isEvent(rise[SET])) {
        return (label + getRiseSetString(rise[RISE], " begins at ") + getRiseSetString(
            rise[SET], " and ends at "));
      } else {
        return (label + getRiseSetString(rise[RISE], " begins at "));
      }
    } else if (Astro.isEvent(rise[SET])) {
      return (label + getRiseSetString(rise[SET], " ends at "));
    } else {
      return (label + " does not occur");
    }
  }

  public void update(Observable observable, Object object) {
    if (observable == sunClockData) {
      int whatChanged = ((Integer) object).intValue();
      if ((whatChanged & (LOCATION_CHANGED | TIME_CHANGED)) != 0) {
        updateLocaleStrings();
        updateSunStrings();
        updateMoonStrings();
        updateJewishObservation();
        updateMuslimObservation();
        repaint();
      }
    }
  }

  protected void updateJewishObservation() {
    Sun sun = new Sun(sunClockData.getJavaDate(), sunClockData.getLongitude(),
        sunClockData.getTimeZoneOffset());
    double rise[] = sun.riseSet(sunClockData.getLatitude(), SUNRISE);
    if (Astro.isEvent(rise[RISE]) && Astro.isEvent(rise[SET])) {
      jewishMorningAlot.setName(eventString(sun, JEWISH_MORNING_ALOT, RISE,
          "Morning Alot HaShachar"));
      jewishMorningTfilin.setName(eventString(sun, JEWISH_TFILIN, RISE,
          "Taalit v'Tfilin"));
      jewishSofZman.setName(getRiseSetString(rise[RISE] + 3.00,
          "Sof Z'man Kriat Sh'ma at "));
      double midday = (rise[RISE] + rise[SET]) / 2.0;
      jewishMidday.setName(getRiseSetString(midday, "Midday at "));
      jewishMincha.setName(getRiseSetString(midday + 0.5,
          "Mincha K'tana starts at "));
      double[] twilight = sun.riseSet(sunClockData.getLatitude(),
          JEWISH_SABBATH_END);
      jewishSabbathEnd.setName(getRiseSetString(twilight[SET],
          "Sabbath or holiday ends at "));
    } else {
      jewishMorningAlot.setName("Cannot compute morning Alot HaShachar");
      jewishMorningTfilin.setName("Cannot compute Taalit v'Tfilin");
      jewishSofZman.setName("Cannot compute Sof Z'man Kriat Sh'ma");
      jewishMidday.setName("Cannot compute midday");
      jewishMincha.setName("Cannot compute Mincha K'tana");
      jewishSabbathEnd.setName("Cannot compute end of Sabbath or holiday");
    }
  }

  /**
   * Store: localeTimeString -- Local time gmtTimeString -- GMT time
   * localePlaceString -- User location from param list
   */
  protected void updateLocaleStrings() {
    localTimeString.setName(sunClockData.getLocalDateString());
    gmtTimeString.setName(sunClockData.getGMTDateString());
    localeLatLongString.setName(sunClockData.getLatLongString());
  }

  protected void updateMoonStrings() {
    moonRiseSetString.setName(Moon.riseSetString(sunClockData.getJavaDate(),
        sunClockData.getTimeZoneOffset(), sunClockData.getLatitude(),
        sunClockData.getLongitude()));
    moonAgeString.setName("Moon age: "
        + new MoonPhase(sunClockData.getJavaDate()));
  }

  protected void updateMuslimObservation() {
    Sun sun = new Sun(sunClockData.getJavaDate(), sunClockData.getLongitude(),
        sunClockData.getTimeZoneOffset());
    double rise[] = sun.riseSet(sunClockData.getLatitude(), SUNRISE);
    double astro[] = sun.riseSet(sunClockData.getLatitude(),
        ASTRONOMICAL_TWILIGHT);
    if (Astro.isEvent(astro[RISE]) && Astro.isEvent(astro[SET])) {
      muslimDawn.setName(getRiseSetString(astro[RISE], "Fajr (dawn) at "));
      muslimSunrise.setName(getRiseSetString(rise[RISE],
          "Shurooq (sunrise) at "));
      double midday = (rise[RISE] + rise[SET]) / 2.0;
      muslimMidday.setName(getRiseSetString(midday,
          "Zhuhr (midday) (approx.) at "));
      /*
       * To compute Asr, first compute the altitude at noon, Al at noon
       * (altitudeNoon) is 90¡ - zenith angle. Now, asrShafi is 90¡ - arccot(1 +
       * cot(altitudeNoon)) and asrHanafi is 90¡ - arccot(2 + cot(altitudeNoon))
       * Then, compute the setting times at these angles.
       */
      double rad90 = Math.PI / 4.0;
      double sinAltitude = sun.sinAltitude(midday);
      double altitude = Math.asin(sinAltitude); /* Radians */
      double cotAltitude = 1.0 / Math.tan(altitude);
      double asrShafi = 90.0 - Astro.atan2Deg((1.0 + cotAltitude), 1.0);
      double asrHanafi = 90.0 - Astro.atan2Deg((2.0 + cotAltitude), 1.0);
      if (false) {
        Format.log("rad90", rad90);
        Format.log("midday", midday);
        Format.log("sinAltitude", sinAltitude);
        Format.log("altitude", altitude / DegRad);
        Format.log("cotAltitude", cotAltitude);
        Format.log("asrShafi", asrShafi);
        Format.log("asrHanafi", asrHanafi);
      }
      muslimAsrShafi.setName(eventString(sun, asrShafi, SET, "Asr (Shafi)"));
      muslimAsrHanafi.setName(eventString(sun, asrHanafi, SET, "Asr (Hanafi)"));
      muslimSunset.setName(getRiseSetString(rise[SET], "Maghrib (sunset) at "));
      muslimEvening.setName(getRiseSetString(astro[SET], "Isha (evening) at "));
    } else {
      muslimDawn.setName("Cannot compute Fajr (dawn)");
      muslimSunrise.setName("Cannot compute Shurooq (sunrise)");
      muslimMidday.setName("Cannot compute Zhuhr (midday)");
      muslimAsrShafi.setName("Cannot compute Asr (Shafi)");
      muslimAsrHanafi.setName("Cannot compute Asr (Hanafi)");
      muslimSunset.setName("Cannot compute Maghrib (sunset)");
      muslimEvening.setName("Cannot compute Isha (evening)");
    }
  }

  protected void updateSunStrings() {
    Sun sun = new Sun(sunClockData.getJavaDate(), sunClockData.getLongitude(),
        sunClockData.getTimeZoneOffset());
    double rise[] = sun.riseSet(sunClockData.getLatitude(), SUNRISE);
    sunRiseSetString.setName(riseSetString(rise));
    sunTwilightString.setName(twilightString(sun, CIVIL_TWILIGHT,
        "Civil Twilight"));
    sunNauticalString.setName(twilightString(sun, NAUTICAL_TWILIGHT,
        "Nautical Twilight"));
    sunAstroString.setName(twilightString(sun, ASTRONOMICAL_TWILIGHT,
        "Astronomical Twilight"));
    sunMiddayString.setName(midday(sun));
  }
}
