/*
 * Event.java
 *
 * Created on January 5, 2001, 11:15 AM
 */
package org.tom.weather;

import java.util.Date;
import java.io.Serializable;

/**
 * 
 * @author administrator
 * @version
 */
public class Event implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 6494649025214465592L;
  private Object value;
  private java.util.Date occurrenceDate;

  /** Creates new Event */
  public Event() {
  }

  /** Creates new Event */
  public Event(Object value, Date occurrenceDate) {
    super();
    this.value = value;
    this.occurrenceDate = occurrenceDate;
  }

  public Object getValue() {
    return value;
  }

  public java.util.Date getOccurrenceDate() {
    return occurrenceDate;
  }

  public void setValue(Object newValue) {
    value = newValue;
  }
}
