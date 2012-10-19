package org.tom.weather.astro;

/**
 * Format
 * <p>
 * 
 * This file contains a collection of static methods that can be used to format
 * integer and floating-point data. They roughly duplicate many C printf
 * formatting conventions. Use by "Format.format(...)"
 * 
 * Copyright &copy; 1996-1998 Martin Minow. All Rights Reserved.
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
 * @version 1.0 Set tabs every 4 characters.
 */
// package Classes;
import java.util.*;

public class Format {
  public static final char NO_CHAR = (char) 0x00;

  /**
   * Format a double-precision vector.
   * 
   * @param vector
   *          The vector to format.
   * @param fillChar
   *          The fill character, generally space.
   * @param fieldWidth
   *          A fixed-size field width (includes the fraction part, if
   *          specified). A positive fieldWidth right-justifies the result while
   *          a negative fieldWidth left-justifies it.
   * @param precision
   *          The number of fractional digits to include.
   * @param trailer
   *          A character to append to the result. The value Format.NO_CHAR does
   *          not append anything.
   * @result The string representation.
   */
  public static String format(double[] vector, char fillChar, int fieldWidth,
      int precision, char trailer) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < vector.length; i++) {
      result
          .append(format(vector[i], fillChar, fieldWidth, precision, trailer));
    }
    return (result.toString());
  }

  /**
   * Format a double-precision vector.
   * 
   * @param vector
   *          The vector to format.
   * @param fieldWidth
   *          A fixed-size field width (includes the fraction part, if
   *          specified). A positive fieldWidth right-justifies the result while
   *          a negative fieldWidth left-justifies it.
   * @param precision
   *          The number of fractional digits to include.
   * @result The string representation.
   */
  public static String format(double[] vector, int fieldWidth, int precision) {
    return (format(vector, ' ', fieldWidth, precision, NO_CHAR));
  }

  /**
   * Format the integer part of a double-precision value.
   * 
   * @param value
   *          The value to format.
   * @param fillChar
   *          The fill character, generally space.
   * @param fieldWidth
   *          A fixed-size field width. A positive fieldWidth right-justifies
   *          the result while a negative fieldWidth left-justifies it.
   * @result The string representation.
   */
  public static String format(double value, char fillChar, int fieldWidth) {
    return (format(value, fillChar, fieldWidth, 0, NO_CHAR));
  }

  /**
   * Format a double-precision value.
   * 
   * @param value
   *          The value to format.
   * @param fillChar
   *          The fill character, generally space.
   * @param fieldWidth
   *          A fixed-size field width (includes the fraction part, if
   *          specified). A positive fieldWidth right-justifies the result while
   *          a negative fieldWidth left-justifies it.
   * @param precision
   *          The number of fractional digits to include.
   * @param trailer
   *          A character to append to the result. Format.NO_CHAR does not
   *          append anything.
   * @result The string representation.
   */
  public static String format(double value, char fillChar, int fieldWidth,
      int precision, char trailer) {
    long integerPart;
    long fraction;
    StringBuffer result = new StringBuffer();
    /*
     * Make sure we can format the value.
     */
    if (Double.isInfinite(value) || Double.isNaN(value)
        || (value > 0 && value > (double) (Long.MAX_VALUE - 1))
        || (value < 0 && value < (double) (Long.MIN_VALUE + 1))) {
      /*
       * The value is out of range for our formatting function. Return the
       * built-in conversion.
       */
      result.append(new String().valueOf(value));
    } else {
      if (value < 0) {
        result.append('-');
        value = (-value);
      }
      /*
       * This is an ugly hack. However it does preserve accuracy and, for small
       * values of "precision," is probably faster than the exponential
       * function. precision == 0 must not recurse through format!.
       */
      if (precision == 0) {
        integerPart = (long) (value + 0.5);
        result.append(integerPart);
      } else {
        double roundoff = 0.5;
        double tenPower = 1.0;
        for (int i = 0; i < precision; i++) {
          roundoff /= 10.0;
          tenPower *= 10.0;
        }
        value += roundoff;
        integerPart = (long) value;
        fraction = (long) ((value - ((double) integerPart)) * tenPower);
        result.append(integerPart);
        result.append('.');
        result.append(format(fraction, '0', precision, 0, NO_CHAR));
      }
    }
    if (fieldWidth < 0) {
      fieldWidth = (-fieldWidth);
      while (result.length() < fieldWidth)
        result.append(fillChar);
    } else {
      while (result.length() < fieldWidth)
        result.insert(0, fillChar);
    }
    if (trailer != NO_CHAR)
      result.append(trailer);
    return (result.toString());
  }

  /**
   * Format a floating-point number.
   * 
   * @param value
   *          The value to format.
   * @param fieldWidth
   *          A fixed-size field width (includes the fraction part, if
   *          specified). A positive fieldWidth right-justifies the result while
   *          a negative fieldWidth left-justifies it.
   * @param precision
   *          The number of decimal digits (may equal zero).
   * @result The string representation.
   */
  public static String format(double value, int fieldWidth, int precision) {
    return (format(value, ' ', fieldWidth, precision, NO_CHAR));
  }

  /**
   * Format an integer.
   * 
   * @param value
   *          The value to format.
   * @param fillChar
   *          The fill character, generally space.
   * @param fieldWidth
   *          A fixed-size field width. A positive fieldWidth right-justifies
   *          the result while a negative fieldWidth left-justifies it.
   * @result The string representation.
   */
  public static String format(int value, char fillChar, int fieldWidth) {
    return (format((long) value, fillChar, fieldWidth, NO_CHAR));
  }

  /**
   * Format the integer part of a double-precision value.
   * 
   * @param value
   *          The value to format.
   * @param fillChar
   *          The fill character, generally space.
   * @param fieldWidth
   *          A fixed-size field width. A positive fieldWidth right-justifies
   *          the result while a negative fieldWidth left-justifies it.
   * @param trailer
   *          A character to append to the result. Format.NO_CHAR does not
   *          append anything.
   * @result The string representation.
   */
  public static String format(int value, char fillChar, int fieldWidth,
      char trailer) {
    return (format((long) value, fillChar, fieldWidth, trailer));
  }

  /**
   * Format an integer.
   * 
   * @param value
   *          The value to format.
   * @param fieldWidth
   *          A fixed-size field width. A positive fieldWidth right-justifies
   *          the result while a negative fieldWidth left-justifies it.
   * @result The string representation.
   */
  public static String format(int value, int fieldWidth) {
    return (format((long) value, ' ', fieldWidth, NO_CHAR));
  }

  /**
   * Format an integer value.
   * 
   * @param value
   *          The value to format.
   * @param fillChar
   *          The fill character, generally space.
   * @param fieldWidth
   *          A fixed-size field width (includes the fraction part, if
   *          specified). A positive fieldWidth right-justifies the result while
   *          a negative fieldWidth left-justifies it.
   * @param trailer
   *          A character to append to the result. Format.NO_CHAR does not
   *          append anything.
   * @result The string representation.
   */
  public static String format(long value, char fillChar, int fieldWidth,
      char trailer) {
    long integerPart;
    long fraction;
    StringBuffer result = new StringBuffer(Long.toString(value));
    if (fieldWidth < 0) {
      fieldWidth = (-fieldWidth);
      while (result.length() < fieldWidth)
        result.append(fillChar);
    } else {
      while (result.length() < fieldWidth)
        result.insert(0, fillChar);
    }
    if (trailer != NO_CHAR)
      result.append(trailer);
    return (result.toString());
  }

  /**
   * Format a long integer.
   * 
   * @param value
   *          The value to format.
   * @param fieldWidth
   *          A fixed-size field width. A positive fieldWidth right-justifies
   *          the result while a negative fieldWidth left-justifies it.
   * @result The string representation.
   */
  public static String format(long value, int fieldWidth) {
    return (format(value, ' ', fieldWidth, NO_CHAR));
  }

  /**
   * Format a floating-point number with a pre-pended label
   * 
   * @param header
   *          A label for the value.
   * @param value
   *          The value to format.
   */
  public static String format(String header, double value) {
    StringBuffer work = new StringBuffer(header);
    while (work.length() < 14)
      work.insert(0, ' ');
    return (work.toString() + " = " + format(value, 12, 8));
  }

  /**
   * Write a vector and its header to the log file.
   * 
   * @param objectName
   *          The value's label
   * @param objectVector
   *          The values to display.
   */
  public static void log(String objectName, double[] objectVector) {
    StringBuffer work = new StringBuffer(objectName);
    while (work.length() < 12)
      work.insert(0, ' ');
    System.out.println(work + " = " + format(objectVector, 12, 6));
  }

  /**
   * Write a vector and its header to the log file.
   * 
   * @param objectName
   *          The value's label
   * @param objectVector
   *          The values to display.
   */
  public static void log(String objectName, int[] objectVector) {
    log(objectName, objectVector.toString()); /* Hack */
  }

  /**
   * Write a value and its header to the log file.
   * 
   * @param objectName
   *          The value's label
   * @param objectValue
   *          The value to display.
   */
  public static void log(String objectName, double objectValue) {
    System.out.println(format(objectName, objectValue));
  }

  /**
   * Write a value and its header to the log file.
   * 
   * @param objectName
   *          The value's label
   * @param objectValue
   *          The value to display.
   */
  public static void log(String objectName, int objectValue) {
    log(objectName, format(objectValue, 6));
  }

  /**
   * Write a value and its header to the log file.
   * 
   * @param objectName
   *          The value's label
   * @param objectValue
   *          The value to display.
   */
  public static void log(String objectName, String objectValue) {
    StringBuffer work = new StringBuffer(objectName);
    while (work.length() < 12)
      work.insert(0, ' ');
    work.append(" = " + objectValue);
    System.out.println(work);
  }
}
