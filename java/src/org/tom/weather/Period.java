package org.tom.weather;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Period implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -6779957794392488530L;
  private Date start;
  private Date end;
  private int type;
  public static final int THIS_HOUR = 0;
  public static final int LAST_HOUR = 1;
  public static final int TODAY = 2;
  public static final int YESTERDAY = 3;
  public static final int THIS_WEEK = 4;
  public static final int THIS_MONTH = 5;
  public static final int LAST_MONTH = 6;
  public static final int THIS_SEASON = 7;
  public static final int LAST_SEASON = 8;
  public static final int THIS_YEAR = 9;
  public static final int LAST_YEAR = 10;
  public static final int LAST_WEEK = 11;
  public static final int FOREVER = 12;
  private Calendar cal;

  public Period(int periodType) {
    setType(periodType);
    cal = getCalendarInstance();
    //System.out.println(cal.getTime());
    //calibrate();
  }
  
  private void calibrate() {
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    switch (getType()) {
      case THIS_HOUR:
        setStart(cal.getTime());
        cal.add(Calendar.HOUR, 1);
        setEnd(cal.getTime());
        break;
      case LAST_HOUR:
        setEnd(cal.getTime());
        cal.add(Calendar.HOUR, -1);
        setStart(cal.getTime());
        break;
      case TODAY:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        setStart(cal.getTime());
        cal.add(Calendar.DATE, 1);
        setEnd(cal.getTime());
        break;
      case YESTERDAY:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        setEnd(cal.getTime());
        cal.add(Calendar.DATE, -1);
        setStart(cal.getTime());
        break;
      case THIS_WEEK:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        setStart(cal.getTime());
        cal.add(Calendar.DATE, 7);
        setEnd(cal.getTime());
        break;
      case LAST_WEEK:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        setEnd(cal.getTime());
        cal.add(Calendar.DATE, -7);
        setStart(cal.getTime());
        break;
      case THIS_MONTH:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DATE, 1);
        setStart(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        setEnd(cal.getTime());
        break;
      case LAST_MONTH:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DATE, 1);
        setEnd(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        setStart(cal.getTime());
        break;
      case THIS_SEASON:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DATE, 1);
        switch (cal.get(Calendar.MONTH)) {
          case Calendar.JANUARY:
          case Calendar.FEBRUARY:
            cal.set(Calendar.MONTH, Calendar.MARCH);
            break;
          case Calendar.MARCH:
          case Calendar.APRIL:
          case Calendar.MAY:
            cal.set(Calendar.MONTH, Calendar.JUNE);
            break;
          case Calendar.JUNE:
          case Calendar.JULY:
          case Calendar.AUGUST:
            cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
            break;
          case Calendar.SEPTEMBER:
          case Calendar.OCTOBER:
          case Calendar.NOVEMBER:
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            break;
          case Calendar.DECEMBER:
            cal.add(Calendar.MONTH, 3);
        }
        setEnd(cal.getTime());
        cal.add(Calendar.MONDAY, -3);
        setStart(cal.getTime());
        break;
      case LAST_SEASON:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DATE, 1);
        switch (cal.get(Calendar.MONTH)) {
          case Calendar.DECEMBER:
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            break;
          case Calendar.JANUARY:
          case Calendar.FEBRUARY:
            cal.add(Calendar.YEAR, -1);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            break;
          case Calendar.MARCH:
          case Calendar.APRIL:
          case Calendar.MAY:
            cal.set(Calendar.MONTH, Calendar.MARCH);
            break;
          case Calendar.JUNE:
          case Calendar.JULY:
          case Calendar.AUGUST:
            cal.set(Calendar.MONTH, Calendar.JUNE);
            break;
          case Calendar.SEPTEMBER:
          case Calendar.OCTOBER:
          case Calendar.NOVEMBER:
            cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
            break;
        }
        setEnd(cal.getTime());
        cal.add(Calendar.MONTH, -3);
        setStart(cal.getTime());
        break;
      case THIS_YEAR:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        setStart(cal.getTime());
        cal.roll(Calendar.YEAR, true);
        setEnd(cal.getTime());
        break;
      case LAST_YEAR:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        setEnd(cal.getTime());
        cal.add(Calendar.YEAR, -1);
        setStart(cal.getTime());
        break;
      case FOREVER:
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.YEAR, 1900);
        setStart(cal.getTime());
        cal.set(Calendar.YEAR, 3000);
        setEnd(cal.getTime());
        break;
    }
    
  }

  protected Calendar getCalendarInstance() {
    setCal(Calendar.getInstance());
    return getCal();
  }

  public Date getEnd() {
    return end;
  }

  public Date getStart() {
    return start;
  }

  public int getType() {
    return type;
  }

  public String toShortString() {
    String desc = null;
    switch (type) {
      case THIS_HOUR:
        desc = "This Hour";
        break;
      case LAST_HOUR:
        desc = "Last Hour";
        break;
      case TODAY:
        desc = "Today";
        break;
      case YESTERDAY:
        desc = "Yesterday";
        break;
      case THIS_WEEK:
        desc = "This Week";
        break;
      case LAST_WEEK:
        desc = "Last Week";
        break;
      case THIS_MONTH:
        desc = "This Month";
        break;
      case LAST_MONTH:
        desc = "Last Week";
        break;
      case THIS_SEASON:
        desc = "This Season";
        break;
      case LAST_SEASON:
        desc = "Last Season";
        break;
      case THIS_YEAR:
        desc = "This Year";
        break;
      case LAST_YEAR:
        desc = "Last Year";
        break;
      case FOREVER:
        desc = "Forever";
        break;
    }
    return desc;
  }

  public static void main(String argv[]) {
    System.out.println("This Hour: " + new Period(Period.THIS_HOUR));
    System.out.println("Last Hour: " + new Period(Period.LAST_HOUR));
    System.out.println("Today: " + new Period(Period.TODAY));
    System.out.println("Yesterday: " + new Period(Period.YESTERDAY));
    System.out.println("This Week: " + new Period(Period.THIS_WEEK));
    System.out.println("Last Week: " + new Period(Period.LAST_WEEK));
    System.out.println("This Month: " + new Period(Period.THIS_MONTH));
    System.out.println("Last Month: " + new Period(Period.LAST_MONTH));
    System.out.println("This Season: " + new Period(Period.THIS_SEASON));
    System.out.println("Last Season: " + new Period(Period.LAST_SEASON));
    System.out.println("This Year: " + new Period(Period.THIS_YEAR));
    System.out.println("Last Year: " + new Period(Period.LAST_YEAR));
  }

  private void setEnd(Date end) {
    this.end = end;
  }

  private void setStart(Date start) {
    this.start = start;
  }

  private void setType(int type) {
    this.type = type;
  }

  public String toLongString() {
    return getStart() + " until " + getEnd();
  }

  public String toString() {
    return toLongString();
  }

  public void setCal(Calendar cal) {
    this.cal = cal;
    calibrate();
  }

  public Calendar getCal() {
    return cal;
  }
}
