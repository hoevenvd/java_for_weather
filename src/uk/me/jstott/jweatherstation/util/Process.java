/*
 * Created on 18-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.util;

import java.text.DecimalFormat;
import org.apache.log4j.Logger;

/**
 * @author jms30
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Process {
  private static final Logger LOGGER = Logger.getLogger(Process.class);

  public static int bytesToInt(UnsignedByte lsb, UnsignedByte msb) {
    // return ((int)msb)*256 + ((int)lsb);
    return (msb.getByte() << 8) | lsb.getByte();
  }

  public static int[] unsign(byte[] bytes) {
    int[] chars = new int[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      int c = (int) b;
      if (b < 0)
        c = 257 + b;
      chars[i] = c;
    }
    return chars;
  }

  public static String printUnsignedByteArray(UnsignedByte[] ubs) {
    String str = "{ ";
    for (int i = 0; i < ubs.length; i++) {
      str = str + ubs[i].toHexString() + ",";
    }
    str = str + " }";
    return str;
  }

  public static String printByteArray(byte[] bs) {
    int[] is = Process.unsign(bs);
    String str = "{ ";
    for (int i = 0; i < is.length; i++) {
      str = str + Integer.toString(is[i]) + ",";
    }
    str = str + " }";
    return str;
  }

  public static String printIntArray(int[] is) {
    String str = "{ ";
    for (int i = 0; i < is.length; i++) {
      str = str + "0x" + Integer.toHexString(is[i]) + ",";
    }
    str = str + " }";
    return str;
  }

  public static byte[] intArrayToByteArray(int[] ints) {
    byte[] result = new byte[ints.length * 2];
    for (int i = 0; i < ints.length; i++) {
      result[i * 2] = (byte) (ints[i] >> 8);
      result[(i * 2) + 1] = (byte) (ints[i] & 255);
    }
    return result;
  }

  private static DecimalFormat format = new DecimalFormat("#.0");

  public static String roundDouble(double d) {
    return format.format(d);
  }

  public static UnsignedByte[] dmpTimeStamp(int day, int month, int year,
      int hour, int minute) {
    int d = day + (month * 32) + ((year - 2000) * 512);
    int t = (100 * hour) + minute;
    int datetime = (d * 65536) + t;
    UnsignedByte[] udatetime = UnsignedByte.splitInt(datetime,
        UnsignedByte.FOUR_BYTES);
    LOGGER.debug("process: day = " + day + " month = " + month + " year = "
        + year);
    LOGGER.debug("process: hour = " + hour + " minute = " + minute);
    // System.out.println("datetime = " + datetime);
    // System.out.println(printUnsignedByteArray(udatetime));
    // System.exit(0);
    return udatetime;
  }
}
