/*
 * Created on 12-Sep-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.datatypes;

import java.io.Serializable;
import org.tom.weather.Direction;

/**
 * @author Jonathan Stott
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Wind implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -2663891292352824475L;
  public static final int UNIT_MPH = 0;
  public static final int UNIT_KTS = 1;
  public static final int UNIT_KMH = 2;
  public static final int UNIT_MS = 3;
  public static final int N = 0;
  public static final int NNE = 1;
  public static final int NE = 2;
  public static final int ENE = 3;
  public static final int E = 4;
  public static final int ESE = 5;
  public static final int SE = 6;
  public static final int SSE = 7;
  public static final int S = 8;
  public static final int SSW = 9;
  public static final int SW = 10;
  public static final int WSW = 11;
  public static final int W = 12;
  public static final int WNW = 13;
  public static final int NW = 14;
  public static final int NNW = 15;
  private int speed = 0;
  private int tenMinAverage = 0;
  private Direction direction;

  public Wind(int speed, int tenMinAverage, int dir) {
    this.speed = speed;
    this.tenMinAverage = tenMinAverage;
    this.setDirection(new Direction(dir));
  }

  public int getSpeed() {
    return speed;
  }
  
  public void setSpeed(int speed) {
	  this.speed = speed;
  }

  public int getTenMinAverage() {
    return tenMinAverage;
  }

  public Direction getDirection() {
    return direction;
  }

  public String toString() {
    return "speed: " + getSpeed() + " from " + getDirection();
  }

public void setDirection(Direction direction) {
	this.direction = direction;
}
}
