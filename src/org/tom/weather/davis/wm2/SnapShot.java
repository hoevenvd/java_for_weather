package org.tom.weather.davis.wm2;

import java.util.Date;
import org.tom.util.Rounding;
import org.tom.weather.Converter;
import org.tom.weather.Direction;

public class SnapShot implements org.tom.weather.SnapShot {
  /**
   * 
   */
  private static final long serialVersionUID = 8898823908527694405L;
  float insideTemp;
  float outsideTemp;
  int windSpeed;
  Direction windDirection = null;
  float pressure;
  float insideHumidity;
  float outsideHumidity;
  float totalRain;
  float dewpoint;
  String barStatus;
  byte data[];
  static float outsideTempCAL = 0;
  static float insideTempCAL = 0;
  static float outsideHumidityCAL = 0;
  private java.util.Date date;
  private boolean valid;
  static double convertRain = 0;
  int extraTemp1;

  public float getPressure() {
    return pressure;
  }

  public float getOutsideHumidity() {
	  if (outsideHumidity > 100) // reported as missing
	  return -9999;
	  else
	return outsideHumidity;
  }

  public String getBarStatus() {
    return barStatus;
  }

  public float getDewpoint() {
	  if (getWindDirection().getDegrees() == -32768)
		  return -9999; // no windsensor, so dew cannot be calculated
	  else
    return dewpoint;
  }

  public float getOutsideTemp() {
	  if (outsideTemp > 1000)
		  return -9999;
	  else
    return outsideTemp;
  }

  public int getWindspeed() {
	  if (getWindDirection().getDegrees() == -9999)
		  return -9999; // no windsensor, so windspeed cannot be calculated
	  else
    return windSpeed;
  }

  public Direction getWindDirection() {
    return windDirection;
  }

  public SnapShot() {
    super();
  }

  public SnapShot(byte[] loopData, String barFlag) {
    date = new java.util.Date();
    data = loopData;
    int adder;
    int addee;
    addee = (loopData[2] << 8);
    if ((int) loopData[1] < 0) {
      adder = (loopData[1]);
      adder += 255;
    } else {
      adder = loopData[1];
    }
    addee += adder;
    insideTemp = (float) addee / 10.0f + insideTempCAL;
    addee = (loopData[4] << 8);
    if ((int) loopData[3] < 0) {
      adder = (loopData[3]);
      adder += 255;
    } else {
      adder = loopData[3];
    }
    addee += adder;
    outsideTemp = (float) addee / 10.0f + outsideTempCAL;
    addee = (loopData[7] << 8);
    if ((int) loopData[6] < 0) {
      adder = (loopData[6]);
      adder += 255;
    } else {
      adder = loopData[6];
    }
    addee += adder;
    windDirection = new Direction(addee);
    addee = (loopData[9] << 8);
    if ((int) loopData[8] < 0) {
      adder = (loopData[8]);
      adder += 255;
    } else {
      adder = loopData[8];
    }
    addee += adder;
    pressure = (float) addee / 1000.0f;
    try {
      pressure = (float) Rounding.round(pressure, 3);
    } catch (NumberFormatException e) {
      System.err.println("Garbage in pressure - throwing out the SnapShot");
      pressure = -1;
      throw e;
    }
    insideHumidity = (float) (loopData[10]);
    if (insideHumidity < 0) {
      insideHumidity += 256;
    }
    outsideHumidity = (float) (loopData[11]) + outsideHumidityCAL;
    if (outsideHumidity < 0) {
      outsideHumidity += 256;
    }
    windSpeed = (loopData[5]);
    if (windSpeed < 0) {
      windSpeed += 256;
    }
    // totalRain = new Float((float)(((int)(loopData[13] << 8)) +
    // (int)(loopData[12])) / 100.0f);
    addee = (loopData[13] << 8);
    if ((int) loopData[12] < 0) {
      adder = (loopData[12]);
      adder += 255;
    } else {
      adder = loopData[12];
    }
    addee += adder;
    totalRain = ((float) addee / 100.0f)*(float)0.7874015748031496;
    setBarFlag(barFlag);
    setValid(true);
    try {
      dewpoint = Converter.getDewpoint(outsideHumidity, outsideTemp);
      dewpoint = (float) Rounding.round(dewpoint, 1);
    } catch (NumberFormatException e) {
      setValid(false);
    }
    validate(); // perform further edits
    return;
  }
  
  private void validate() {
    // go figure, sometimes the davis wm2 shows humidity as 108%
    // There is a known hardware-failure in the wm2; so this code beneath
    // is to fix humidity on 100% when the read value is between
    // 100% and 110%
    // if humidity < 0 or humidity > 110 there's certain garbage on the
    // line that must be ignored
    // This fix is for the snapshot. The fix when inserting the archive
    // into the database is in ArchiveEnrtyImpl
/*    if (getOutsideHumidity() > 100
        && getOutsideHumidity() <= 110) {
      setValid(false);
    }
    if (getOutsideHumidity() < 0
        || getOutsideHumidity() > 110) {
      setValid(false);
    }
*/    if (getPressure() < 27.0 || getPressure() > 33.0) {
      setValid(false);
    }

  }

  public static int getInsideTempCAL() {
    return (int) (insideTempCAL);
  }

  public static int getOutsideHumidityCAL() {
	    return (int) (outsideHumidityCAL);
	  }
  
  public static int getOutsideTempCAL() {
    return (int) (outsideTempCAL);
  }

  public synchronized void setBarFlag(String s) {
    barStatus = new String(s);
  }

  public synchronized void setDewpoint(float f) {
    dewpoint = f;
  }

  public static synchronized void setInsideTempCAL(float f) {
    insideTempCAL = f;
  }

  public static synchronized void setOutsideHumidityCAL(float f) {
    outsideHumidityCAL = f;
  }

  public static synchronized void setOutsideTempCAL(float f) {
    outsideTempCAL = f;
  }

  public byte[] getData() {
    return data;
  }

  public java.util.Date getDate() {
    return date;
  }
  
  public boolean isRaining() {
	  return false; // value not provided by WM2
  }

  public double getRainRate() {
	  return -9999; // vaue not provided by WM2
  }

  public float getWindchill() {
	  if (getWindDirection().getDegrees() == -9999)
		  return -9999;
	  else
    return Converter.getWindChill((int)getWindspeed(), getOutsideTemp());
  }

  public float getDayRain() {
	  return -9999; // value not provided by WM2 
  }

  public int getTenMinAvgWind() {
	  return -9999; // value not provided by WM2
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public boolean isValid() {
    return valid;
  }

  public int getUV() {
    return -9999; // value not provided by WM2
  }

  public int getSolarRadiation() {
    return -9999; // value not provided by WM2
  }

    public int getInsideHumidity() {
        return (int)insideHumidity;
    }

    public float getInsideTemp() {
        return insideTemp;
    }

    public Date getSunrise() {
        return getDate();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getSunset() {
	return getDate();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getMonthRain() {
        return -9999; // value not provided by WM2
     }

    public float getYearRain() {
        return -9999; // value not provided by WM2
     }

    public double getStormRain() {
        return -9999; // value not provided by WM2
    }
    
    public int getExtraTemp1() {
	return extraTemp1;
   }

}