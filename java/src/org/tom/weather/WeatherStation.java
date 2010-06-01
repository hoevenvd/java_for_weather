/*
 * Created on Aug 3, 2005
 *
 */
package org.tom.weather;

public interface WeatherStation {
  void readCurrentConditions() throws Exception;
  void readArchiveMemory() throws Exception;
  boolean test() throws Exception;
}
