/*
 * Created on 12-Sep-2004
 */
package uk.me.jstott.jweatherstation.datatypes;

import java.io.Serializable;
import uk.me.jstott.jweatherstation.util.UnsignedByte;

/**
 * This data type represents a temperature. Methods are provided to return the
 * temperature in either degrees Celsius, degrees Fahrenheit or the raw value as
 * stored in the database.
 * 
 * @author Jonathan Stott
 * @version 1.0
 * @since 1.0
 */
public class Temperature implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 3032007513082155196L;
  private int temperature = 0;

  /**
   * Create a new Temperature data type with the given raw value for the
   * temperature. The value should be 10x the actual temperature in degrees
   * Fahrenheit
   * 
   * @param temp
   *          the raw value of the temperature
   */
  public Temperature(int temp) {
    temperature = temp;
  }

  /**
   * Create a new Temperature data type using the given values of lsb and msb
   * forming a 16-bit value. This constructor is used by the backend package to
   * create a temperature from the data read from the weather station.
   * 
   * @param lsb
   *          least significant byte
   * @param msb
   *          most significant byte
   */
  public Temperature(UnsignedByte lsb, UnsignedByte msb) {
    temperature = (msb.getByte() << 8) | lsb.getByte();
  }

  public Temperature(double temp) {
    temperature = (int) temp * 10;
    // TODO Auto-generated constructor stub
  }

  /**
   * Return the raw value of the temperature as stored in the database. This is
   * actually 10x the actual temperature in degrees Fahrenheit.
   * 
   * @return the raw value of the temperature
   */
  public int getTemperatureRaw() {
    return temperature;
  }

  /**
   * Return the value of the temperature in degrees Fahrenheit
   * 
   * @return the value of the temperature in degrees Fahrenheit
   */
  public double getTemperatureFahrenheit() {
    return ((double) temperature) / 10.0;
  }

  /**
   * Retur the value of the temperature in degrees Celsius
   * 
   * @return the value of the temperature in degrees Celsius
   */
  public double getTemperatureCelsius() {
    return (5.0 / 9.0) * ((((double) temperature) / 10.0) - 32.0);
  }

  public String toString() {
    return Float.toString(temperature / 10.0f);
  }
}
