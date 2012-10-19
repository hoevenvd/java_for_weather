/*
 * Created on 18-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package uk.me.jstott.jweatherstation.util;

import java.io.Serializable;

/**
 * @author jms30
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class UnsignedByte implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 5312447180808825053L;
  int b = 0;
  public static final int ONE_BYTE = 1;
  public static final int TWO_BYTES = 2;
  public static final int FOUR_BYTES = 4;

  public static UnsignedByte[] getUnsignedBytes(byte[] signedBytes) {
    UnsignedByte[] returnedBytes = new UnsignedByte[signedBytes.length];
    for (int i = 0; i < signedBytes.length; i++) {
      returnedBytes[i] = new UnsignedByte(signedBytes[i]);
    }
    return returnedBytes;
  }

  public UnsignedByte(byte b) {
    // System.out.println("New byte (byte)" + b);
    this.b = b;
  }

  public UnsignedByte(int i) {
    // System.out.println("New byte (int)" + i);
    b = (byte) (i & 0xFF);
  }

  public static UnsignedByte[] splitInt(int newB, int bytesInInt) {
    UnsignedByte[] bytes = new UnsignedByte[bytesInInt];
    switch (bytesInInt) {
      case FOUR_BYTES:
        bytes[1] = new UnsignedByte((newB & 0xFF000000) >> 24);
        bytes[0] = new UnsignedByte((newB & 0xFF0000) >> 16);
        bytes[3] = new UnsignedByte((newB & 0xFF00) >> 8);
        bytes[2] = new UnsignedByte(newB & 0xFF);
        // System.out.println("4b b = " + newB + " : " + ((newB & 0xFF000000) >>
        // 24));
        // System.out.println("3b b = " + newB + " : " + ((newB & 0xFF0000) >>
        // 16));
        // System.out.println("2b b = " + newB + " : " + ((newB & 0xFF00) >>
        // 8));
        // System.out.println("1b b = " + newB + " : " + (newB & 0xFF));
        break;
      case TWO_BYTES:
        bytes[1] = new UnsignedByte((newB & 0xFF00) >> 8);
      // System.out.println("2b b = " + newB + " : " + ((newB & 0xFF00) >> 8));
      case ONE_BYTE:
        bytes[0] = new UnsignedByte(newB & 0xFF);
    // System.out.println("1b b = " + newB + " : " + (newB & 0xFF));
    }
    return bytes;
  }

  public int getByte() {
    // System.out.println("int is " + toHexString() + ", byte is 0x" +
    // Integer.toHexString((b & 0xFF)));
    return (b & 0xFF);
  }

  public String toBinaryString() {
    return Integer.toBinaryString((b & 0xFF));
  }

  public String toHexString() {
    return "0x" + Integer.toHexString((b & 0xFF));
  }
}
