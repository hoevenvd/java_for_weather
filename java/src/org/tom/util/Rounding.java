package org.tom.util;

import java.lang.Math;
import java.math.BigDecimal;

public class Rounding {
  public static String toString(double d, int place) {
    if (place <= 0) {
      return "" + (int) (d + ((d > 0) ? 0.5 : -0.5));
    }
    String s = "";
    if (d < 0) {
      s += "-";
      d = -d;
    }
    d += 0.5 * Math.pow(10, -place);
    if (d > 1) {
      int i = (int) d;
      s += i;
      d -= i;
    } else
      s += "0";
    if (d > 0) {
      d += 1.0;
      String f = "" + (int) (d * Math.pow(10, place));
      s += "." + f.substring(1);
    }
    return s;
  }

  public static double round(double d, int places) throws NumberFormatException {
    try {
      BigDecimal num = new BigDecimal(d);
      num = num.setScale(places, BigDecimal.ROUND_HALF_EVEN);
      return num.doubleValue();
    } catch (NumberFormatException e) {
      return d;
    }
  }

  public static Double round(Double d, int places) throws NumberFormatException {
    return new Double(round(d.doubleValue(), places));
  }

  public static Double round(Float f, int places) throws NumberFormatException {
    return new Double(round((double) f.floatValue(), places));
  }

  public static void main(String argv[]) {
    System.out.println(123.45 + " is " + round(123.45, 1));
  }
}
