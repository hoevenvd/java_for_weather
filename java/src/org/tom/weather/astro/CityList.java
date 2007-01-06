package org.tom.weather.astro;

/**
 * CityList holds the static location list.<p>
 *
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.<p>
 *
 * Permission to use, copy, modify, and redistribute this software and its
 * documentation for personal, non-commercial use is hereby granted provided that
 * this copyright notice and appropriate documentation appears in all copies. This
 * software may not be distributed for fee or as part of commercial, "shareware,"
 * and/or not-for-profit endevors including, but not limited to, CD-ROM collections,
 * online databases, and subscription services without specific license.<p>
 *
 * @author <a href="mailto:minow@merrymeet.com">Martin Minow</a>
 * @version 1.2
 * 1996.09.27
 * 1997.10.14: Dacca Bangladesh is now spelled Dhaka
 * Set tabs every 4 characters.
 */
import java.util.*;
import java.awt.*;

public class CityList extends OutlinePanel {
  /**
   * cityList text format:
   * 
   * <pre>
   * 	Cities (with latitude, longitude, timezone, and names):
   * 		Latitude (north is positive)
   * 		&quot;:&quot; Longitude	East is positive
   * 		&quot;:&quot; Timezone ID (&quot;XXX&quot; means &quot;unknown&quot;)
   * 		&quot;:&quot; Timezone	Minutes East of Greenwich
   * 		&quot;:&quot; Location	Text
   * 		&lt;newline&gt;
   * 	Groupings (headings):
   * 		Latitude must equal 100
   * 		&quot;:&quot;	Indentation level
   * 		&quot;:&quot; Heading name
   * 		&lt;newline&gt;
   * 
   */
  public static final String cityList = "100:0:North America\n"
      + "100:1:United States\n" + "61.22:-149.88:AST:-540:Anchorage, U.S.A.\n"
      + "33.75:-84.38:EST:-300:Atlanta, U.S.A.\n"
      + "42.33:-71.08:EST:-300:Boston, U.S.A.\n"
      + "40.03:-105.50:MST:-420:Boulder, U.S.A.\n"
      + "41.83:-87.75:CST:-360:Chicago, U.S.A.\n"
      + "37.35:-121.95:PST:-480:Cupertino, U.S.A.\n"
      + "39.73:-104.98:MST:-420:Denver, U.S.A.\n"
      + "42.38:-83.08:EST:-300:Detroit, U.S.A.\n"
      + "21.32:-157.87:HST:-600:Honolulu, U.S.A.\n"
      + "34.07:-118.25:PST:-480:Los Angeles, U.S.A.\n"
      + "25.77:-80.20:EST:-300:Miami, U.S.A.\n"
      + "44.98:-93.22:CST:-360:Minneapolis, U.S.A.\n"
      + "40.72:-74.02:EST:-300:New York City, U.S.A.\n"
      + "39.62:-75.12:EST:-300:Philadelphia, U.S.A.\n"
      + "33.30:-112.03:PNT:-420:Phoenix, U.S.A.\n"
      + "71.38:-156.3:AST:-540:Point Barrow, Alaska, U.S.A.\n"
      + "45.55:-122.60:PST:-480:Portland, U.S.A.\n"
      + "38.63:-90.18:CST:-360:Saint Louis, U.S.A.\n"
      + "40.77:-111.88:MST:-420:Salt Lake City, U.S.A.\n"
      + "32.72:-117.15:PST:-480:San Diego, U.S.A.\n"
      + "37.80:-122.40:PST:-480:San Francisco, U.S.A.\n"
      + "47.35:-122.20:PST:-480:Seattle, U.S.A.\n"
      + "38.63:-90.18:PST:-360:St. Louis, U.S.A.\n"
      + "38.92:-77.00:EST:-300:Washington, U.S.A.\n" + "100:1:Canada\n"
      + "51.05:-114.05:PST:-420:Calgary, Canada\n"
      + "44.65:-63.58:PRT:-240:Halifax, Canada\n"
      + "45.50:-73.60:EST:-300:Montr\u00e9al, Canada\n"
      + "45.42:-75.70:EST:-300:Ottawa, Canada\n"
      + "43.65:-79.33:EST:-300:Toronto, Canada\n"
      + "49.27:-123.12:PST:-480:Vancouver, Canada\n"
      + "49.90:-97.15:CST:-360:Winnipeg, Canada\n"
      + "100:0:Central America, Carribian\n"
      + "4.92:-52.30:AGT:-180:Cayenne, French Guiana\n"
      + "19.40:-99.15:CST:-360:Ciudad de M\u00e9xico\n"
      + "14.67:-90.37:CST:-360:Guatemala, Guatemala\n"
      + "22.13:-82.37:EST:-300:Havana, Cuba\n"
      + "10.50:-66.93:PRT:-240:Caracas, Venezuela\n"
      + "12.10:-86.33:CST:-360:Managua, Nicaragua\n"
      + "19.40:-99.15:CST:-360:Mexico City, Mexico\n"
      + "9.00:-79.42:EST:-300:Panama City, Panama\n"
      + "18.67:-72.33:EST:-300:Port-au-Prince, Haiti\n"
      + "9.98:-84.07:CST:-360:San Jos\u00e9, Costa Rica\n"
      + "13.67:-89.17:CST:-360:San Salvador\n"
      + "18.50:-64.90:PRT:-240:Santo Domingo, Dominican Republic\n"
      + "14.08:-87.23:CST:-360:Tegucigalpa, Honduras\n"
      + "100:0:South America\n" + "4.60:-74.08:CST:-300:Bogot\u00e1\n"
      + "-15.78:-47.92:XXX:-180:Bras\u00edlia, Brazil\n"
      + "-34.62:-58.40:XXX:-180:Buenos Aires, Argentina\n"
      + "-16.50:-68.15:XXX:-240:La Paz, Bolivia\n"
      + "-12.05:-77.05:XXX:-300:Lima, Peru\n"
      + "-34.92:-56.17:XXX:-180:Montevideo\n"
      + "-0.23:-78.50:XXX:-300:Quito, Ecuador\n"
      + "-22.88:-43.28:XXX:-180:Rio de Janeiro\n"
      + "-33.40:-70.67:XXX:-240:Santiago, Chile\n"
      + "-23.55:-46.65:XXX:-180:S\u00e3o Paulo, Brazil\n" + "100:0:Europe\n" /*
                                                                               * Southern
                                                                               * and
                                                                               * Eastern
                                                                               * are
                                                                               * political
                                                                               * as
                                                                               * much
                                                                               * as
                                                                               * geographical
                                                                               */
      + "100:1:Northern Europe\n"
      + "55.72:12.57:XXX:60:K\u00f8benhavn, Denmark\n"
      + "60.17:24.97:XXX:120:Helsinki, Finland\n"
      + "60.3:19.13:XXX:120:Mariehamm, \u00c5land\n"
      + "71.18:25.8:XXX:60:Nordkapp, Norway\n"
      + "59.93:10.75:XXX:60:Oslo, Norway\n"
      + "64.15:-21.95:XXX:0:Reykjav\u00edk, Iceland\n"
      + "59.33:18.05:XXX:60:Stockholm, Sweden\n"
      + "100:1:Western and Central Europe\n"
      + "52.35:4.90:XXX:60:Amsterdam, Netherlands\n"
      + "52.52:13.40:XXX:60:Berlin, Germany\n"
      + "46.95:7.50:XXX:60:Bern. Switzerland\n"
      + "50.73:7.10:XXX:60:Bonn, Germany\n"
      + "50.83:4.35:XXX:60:Brussels, Belgium\n"
      + "51.50:-3.22:XXX:0:Cardiff, Wales\n"
      + "53.33:-6.25:XXX:0:Dublin, Ireland\n"
      + "55.95:-3.22:XXX:0:Edinburgh, Scotland\n"
      + "46.17:6.15:XXX:60:Geneva, Switzerland\n"
      + "57.48:-4.20:XXX:0:Inverness, Scotland\n"
      + "38.73:-9.13:XXX:0:Lisbon, Portugal\n"
      + "51.50:-0.17:XXX:0:London, England\n"
      + "40.42:-3.72:XXX:60:Madrid, Spain\n"
      + "48.87:2.33:XXX:60:Paris, France\n"
      + "48.22:16.37:XXX:60:Vienna, Austria\n" + "100:1:Southern Europe\n"
      + "39.92:32.83:XXX:120:Ankara, Turkey\n"
      + "38.00:23.73:XXX:120:Athens, Greece\n"
      + "41.03:28.98:XXX:120:Istanbul, Turkey\n"
      + "41.88:12.50:XXX:60:Rome, Italy\n" + "100:1:Eastern Europe\n"
      + "44.83:20.50:XXX:60:Belgrade, Serbia\n"
      + "44.83:20.50:XXX:60:Beograd, Serbia\n"
      + "44.45:26.17:XXX:120:Bucharest, Romania\n"
      + "47.50:19.08:XXX:60:Budapest, Hungary\n"
      + "50.50:30.47:XXX:120:Kiev, Ukraine\n"
      + "55.75:37.58:XXX:180:Moscow, Russia\n"
      + "55.03:83.05:XXX:420:Novosibirsk, Russia\n"
      + "50.08:14.43:XXX:60:Prague, Czech Republic\n"
      + "42.75:23.33:XXX:120:Sofia, Bulgaria\n"
      + "59.92:30.25:XXX:180:St. Petersburg, Russia\n"
      + "52.25:21.00:XXX:60:Warsaw, Poland\n" + "100:0:Africa\n"
      + "5.58:0.10:XXX:0:Accra, Ghana\n"
      + "9.05:38.70:XXX:180:Addis Ababa, Ethiopia\n"
      + "12.77:45.02:XXX:180:Aden\n" + "36.83:3.00:XXX:60:Algiers, Algeria\n"
      + "-33.93:18.37:XXX:120:Cape Town, South Africa\n"
      + "9.48:13.82:XXX:0:Conakry, Guinea\n"
      + "14.57:17.48:XXX:0:Dakar, Senegal\n"
      + "-6.83:39.20:XXX:180:Dar es Salaam, Tanzania\n"
      + "11.50:43.08:XXX:180:Djibouti\n"
      + "8.50:-13.28:XXX:0:Freetown, Sierra Leone\n"
      + "-17.72:31.03:XXX:120:Harare\n"
      + "0.33:32.50:XXX:180:Kampala, Uganda\n"
      + "-4.33:15.25:XXX:60:Kinshasa\n" + "6.45:3.38:XXX:60:Lagos, Nigeria\n"
      + "-25.97:32.53:XXX:120:Maputo\n"
      + "2.03:45.35:XXX:180:Mogadisho, Somalia\n"
      + "6.30:10.78:XXX:0:Monrovia, Liberia\n"
      + "-1.28:36.83:XXX:180:Nairobi, Kenya\n"
      + "34.03:6.85:XXX:0:Rabat, Morocco\n"
      + "15.45:44.20:XXX:180:Sanaa, Yemen\n"
      + "32.88:13.20:XXX:120:Tripoli, Libya\n"
      + "36.80:10.18:XXX:60:Tunis, Tunisia\n" + "100:0:Middle East\n"
      + "24.47:54.42:XXX:240:Abu Dhabi, U.A.E.\n"
      + "31.95:35.93:XXX:120:Amman, Jordan\n"
      + "33.33:44.43:XXX:180:Baghdad, Iraq\n"
      + "33.87:35.50:XXX:120:Beirut, Lebanon\n"
      + "30.05:31.25:XXX:120:Cairo, Egypt\n"
      + "33.50:36.25:XXX:120:Damascus, Syria\n"
      + "31.78:35.23:XXX:120:Jerusalem, Israel\n"
      + "29.33:48.00:XXX:180:Kuwait\n" + "26.20:50.58:XXX:180:Manama\n"
      + "21.45:39.82:XXX:180:Mecca, Saudia Arabia\n"
      + "23.48:58.55:XXX:240:Muscat\n" + "24.65:46.72:XXX:180:Riyadh\n"
      + "35.67:51.43:XXX:210:Tehran\n" + "100:0:Asia\n" /*
                                                         * How do I break this
                                                         * up (South, East,
                                                         * West?)
                                                         */
      + "100:1:Indian Sub-continent\n" + "18.93:72.85:XXX:330:Bombay, India\n"
      + "22.58:88.35:XXX:330:Calcutta, India\n"
      + "6.93:79.97:XXX:330:Colombo, Sri Lanka\n"
      + "23.70:90.37:XXX:360:Dhaka (Dacca), Bangladesh\n"
      + "33.67:73.17:XXX:300:Islamabad, Pakistan\n"
      + "34.50:69.20:XXX:270:Kabul, Afghanistan\n"
      + "24.85:67.03:XXX:300:Karachi, Pakistan\n"
      + "13.13:80.32:XXX:330:Madras, India\n"
      + "28.62:77.22:XXX:330:New Delhi, India\n" + "100:1:South Asia\n"
      + "13.73:100.50:XXX:420:Bangkok, Thailand\n"
      + "-6.13:106.75:XXX:420:Djakarta, Indonesia\n"
      + "21.08:105.92:XXX:420:Hanoi, Vietnam\n"
      + "10.97:106.67:XXX:480:Ho Chi Minh City, Vietnam\n"
      + "3.15:101.68:XXX:480:Kuala Lumpur\n"
      + "11.55:104.92:XXX:420:Phnom Penh, Cambodia\n"
      + "16.75:96.33:XXX:390:Rangoon, Burma\n"
      + "1.28:103.85:XXX:480:Singapore\n" + "100:1:East Asia\n"
      + "39.92:116.42:XXX:480:Beijing, China\n"
      + "22.25:114.17:XXX:480:Hong Kong, China\n"
      + "34.67:135.50:XXX:540:Osaka, Japan\n"
      + "39.92:116.42:XXX:480:Peking, China\n"
      + "39.00:125.50:XXX:540:Pyongyang, North Korea\n"
      + "37.55:126.97:XXX:540:Seoul, Korea\n"
      + "31.23:121.50:XXX:480:Shanghai, China\n"
      + "25.07:121.48:XXX:480:Taipei\n" + "41.33:69.17:XXX:300:Tashkent\n"
      + "35.70:139.77:XXX:540:Tokyo, Japan\n"
      + "47.92:106.88:XXX:480:Ulaanbaator, Mongolia\n" + "100:0:Oceana\n"
      + "-34.87:138.50:XXX:570:Adelaide, Australia\n"
      + "-35.25:149.13:XXX:600:Canberra, Australia\n"
      + "13.45:144.75:XXX:600:Guam\n"
      + "14.60:120.98:XXX:480:Manila, Philippines\n"
      + "-37.83:145.00:XXX:600:Melbourne, Australia\n"
      + "-77.325:-167.09:XXX:720:Mt. Erebus, Antartica\n"
      + "-31.93:115.83:XXX:480:Perth, Australia\n"
      + "-33.87:151.22:XXX:600:Sydney, Australia\n"
      + "-41.28:174.78:XXX:720:Wellington, New Zealand\n";
  private double latitude;
  private double longitude;
  private int timezone;
  private String location;

  public CityList() {
    super(true, true);
    StringTokenizer t = new StringTokenizer(cityList, ":\n");
    try {
      int indent = 0;
      while (t.hasMoreTokens()) {
        double latitude = new Double(t.nextToken()).doubleValue();
        if (latitude == 100.0) {
          indent = Integer.parseInt(t.nextToken());
          // System.out.print("heading " + indent);
          String heading = t.nextToken();
          // System.out.println(", " + heading);
          addText(indent, heading);
          indent++;
        } else {
          double longitude = new Double(t.nextToken()).doubleValue();
          String tzName = t.nextToken();
          int timezone = Integer.parseInt(t.nextToken());
          String location = t.nextToken();
          CityObject object = new CityObject(latitude, longitude, timezone,
              tzName);
          // System.out.println(indent + ", " + location);
          addText(indent, location, object);
        }
      }
    } catch (NoSuchElementException e) {
      System.err.println("CityList text error");
    }
  }

  /**
   * Find the nearest city to the given location. This is called when the user
   * selects a location by clicking the mouse. If we don't have a good
   * sprinkling of cities, we'll might get unlucky. To prevent this, we only
   * look at cities that are +/- 15 degrees of latitude (one hour) from the
   * mouse click.
   */
  public static TimeZone findNearestTimezone(double latitude, double longitude) {
    /*
     * Avoid strange negative number effects.
     */
    double minLong = (longitude - 15.0) + 360.0;
    double maxLong = (longitude + 15.0) + 360.0;
    double minDistance = Double.MAX_VALUE;
    String minTZName = null;
    int minTZOffset = 0;
    TimeZone timeZone = null;
    StringTokenizer t = new StringTokenizer(cityList, ":\n");
    try {
      while (t.hasMoreTokens()) {
        double testLat = new Double(t.nextToken()).doubleValue();
        if (testLat == 100.0) {
          int indent = Integer.parseInt(t.nextToken());
          String heading = t.nextToken();
        } else {
          double testLong = new Double(t.nextToken()).doubleValue();
          String tzName = t.nextToken();
          int tzOffset = Integer.parseInt(t.nextToken());
          String location = t.nextToken();
          if ((testLong + 360.0) > minLong && (testLong + 360.0) < maxLong) {
            /*
             * Here's a possible city.
             */
            double distance = Astro.greatCircle(latitude, longitude, testLat,
                testLong);
            if (distance < minDistance) {
              minDistance = distance;
              minTZName = tzName;
              minTZOffset = tzOffset;
            }
          } /* Possible city */
        } /* Look in cityList for cities */
      } /* More tokens */
    } catch (Exception e) {
      System.err.println("CityList text or format error");
    }
    if (minTZName != null) {
      if (minTZName.equals("XXX")) {
        /*
         * We have no timezone name for this city. Cobble one out of the
         * built-in list.
         */
        String ids[] = TimeZone.getAvailableIDs(minTZOffset * 60000);
        if (ids != null && ids.length > 0) {
          timeZone = TimeZone.getTimeZone(ids[0]);
        }
      } else {
        timeZone = TimeZone.getTimeZone(minTZName);
      }
    }
    return (timeZone);
  }

  public double getLatitude() {
    CityObject thisCity = (CityObject) getSelectedValue();
    return ((thisCity == null) ? 0.0 : thisCity.latitude);
  }

  public String getLocationName() {
    String thisLocation = getSelectedName();
    return ((thisLocation == null) ? "" : thisLocation);
  }

  public double getLongitude() {
    CityObject thisCity = (CityObject) getSelectedValue();
    return ((thisCity == null) ? 0.0 : thisCity.longitude);
  }

  public String getTimeZoneID() {
    String result = null;
    CityObject thisCity = (CityObject) getSelectedValue();
    if (thisCity != null) {
      result = thisCity.timeZoneID;
      if (result == null || result.equals("XXX")) {
        /*
         * We have no timezone name for this city. Cobble one out of the
         * built-in list.
         */
        String ids[] = TimeZone
            .getAvailableIDs(thisCity.timezoneOffset * 60000);
        if (ids != null && ids.length > 0) {
          result = ids[0];
        }
      }
    }
    return (result);
  }
}
