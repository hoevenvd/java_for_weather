package org.tom.weather;

import java.io.Serializable;

public class Direction implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -4804303711647302726L;
  /**
   * 
   */
  int degrees = 0;

  public Direction(int whichWay) {
    if (whichWay == 22.5 * 255) { // if n/a, station sends 255
      degrees = -9999;
    }
    else
    degrees = whichWay;
  }

  public int getDegrees() {
      if (degrees == -32768) {
          return -9999;
      }
	  if (degrees == -1) {
      return -9999;
    }
    return degrees;
  }

  public String toLongString() {
    return toShortString() + " (" + degrees + " degrees)";
  }

  public String toDegrees() {
    return Integer.toString(degrees);
  }

  public String toShortString() {
    if (degrees == -1) {
      return "n/a";
    }
    if (degrees <= 11) {
      return "N";
    }
    if (degrees <= 34) {
      return "NNE";
    }
    if (degrees <= 56) {
      return "NE";
    }
    if (degrees <= 79) {
      return "ENE";
    }
    if (degrees <= 101) {
      return "E";
    }
    if (degrees <= 124) {
      return "ESE";
    }
    if (degrees <= 146) {
      return "SE";
    }
    if (degrees <= 169) {
      return "SSE";
    }
    if (degrees <= 191) {
      return "S";
    }
    if (degrees <= 214) {
      return "SSW";
    }
    if (degrees <= 236) {
      return "SW";
    }
    if (degrees <= 259) {
      return "WSW";
    }
    if (degrees <= 281) {
      return "W";
    }
    if (degrees <= 304) {
      return "WNW";
    }
    if (degrees <= 326) {
      return "NW";
    }
    if (degrees <= 349) {
      return "NNW";
    }
    return "N";
  }

  public String toString() {
    return this.toShortString();
  }
}
