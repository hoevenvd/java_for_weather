/*
 * Created on 18-Oct-2004
 */
package org.tom.weather.davis.wm2;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.tom.util.DateUtils;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.Converter;
import org.tom.weather.Direction;
import org.tom.weather.comm.Station;
import org.tom.weather.comm.WeatherMonitor2;

import uk.me.jstott.jweatherstation.datatypes.Humidity;
import uk.me.jstott.jweatherstation.datatypes.Pressure;
import uk.me.jstott.jweatherstation.datatypes.Temperature;
import uk.me.jstott.jweatherstation.util.Process;
import uk.me.jstott.jweatherstation.util.UnsignedByte;

/**
 * 
 * 
 * @author Jonathan Stott
 * @version 1.1
 * @since 1.0
 */
public class Wm2DmpRecord implements ArchiveEntry {

    /**
     *
     */
    private static final long serialVersionUID = 1091094347739181210L;
    static final int ARCHIVE_RECORD_SIZE = 21;
    private static final Logger LOGGER = Logger.getLogger(Wm2DmpRecord.class);
    private boolean valid = true;
    private List invalidReasons = new ArrayList();
    private UnsignedByte[] unsignedRawData;
    private Calendar date;
    private double outsideTemperature = -9999;
    private double highOutsideTemperature = -9999;
    private double lowOutsideTemperature = -9999;
    private int rainfall = -9999;
    private double highRainfallRate = -9999.0;
    private double pressure = -9999.0;
    private int solarRadiation = -9999;
    private int numberOfWindSamples = -9999;
    private int insideTemperature = -9999;
    private int insideHumidity = -9999;
    private int outsideHumidity = -9999;
    private int averageWindSpeed = -9999;
    private int highWindSpeed = -9999;
    private Direction highWindSpeedDirection;
    private Direction windDirection;
    private int averageUVIndex = -9999;
    private int ET = -9999;
    private int highSolarRadiation = -9999;
    private int highUVIndex = -9999;
    private int forecastRule = -9999;
    private int leafTemperature1 = -9999;
    private int leafTemperature2 = -9999;
    private int leafWetness1 = -9999;
    private int leafWetness2 = -9999;
    private int soilTemperature1 = -9999;
    private int soilTemperature2 = -9999;
    private int soilTemperature3 = -9999;
    private int soilTemperature4 = -9999;
    private int downloadRecordType = -9999;
    private int extraHumidity1 = -9999;
    private int extraHumidity2 = -9999;
    private int extraTemperature1 = -9999;
    private int extraTemperature2 = -9999;
    private int extraTemperature3 = -9999;
    private int soilMoisture1 = -9999;
    private int soilMoisture2 = -9999;
    private int soilMoisture3 = -9999;
    private int soilMoisture4 = -9999;
    static double convertRain = 0;

    public Wm2DmpRecord(byte[] data) {
              
        byte[] intBuffer = new byte[2];
        
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        int month = data[18] & 0xf;

        int tempInt = new Integer((int) data[17]).intValue();
        int day = new Integer(Integer.toHexString(tempInt)).intValue();

        tempInt = new Integer((int) data[15]).intValue();
        int hour = new Integer(Integer.toHexString(tempInt)).intValue();

        tempInt = new Integer((int) data[16]).intValue();
        int minute = new Integer(Integer.toHexString(tempInt)).intValue();

        date = Calendar.getInstance(); // today by default
        date.set(Calendar.MILLISECOND, 0);
        date.set(year, month - 1, day, hour, minute, 0);

        intBuffer[0] = data[8];
        intBuffer[1] = data[9];
        outsideTemperature = bytesToInt(intBuffer);
        
        intBuffer[0] = data[12];
        intBuffer[1] = data[13];
        highOutsideTemperature = bytesToInt(intBuffer);

        intBuffer[0] = data[19];
        intBuffer[1] = data[20];
        lowOutsideTemperature = bytesToInt(intBuffer);
        
        intBuffer[0] = data[4];
        intBuffer[1] = data[5];
        rainfall = getUnsigned(intBuffer);
        
        highRainfallRate = -9999.0;
        
        intBuffer[0] = data[0];
        intBuffer[1] = data[1];
        pressure = bytesToInt(intBuffer);
        
        solarRadiation = -9999;
        numberOfWindSamples = -9999;
        
        intBuffer[0] = data[6];
        intBuffer[1] = data[7];
        insideTemperature = bytesToInt(intBuffer);

        insideHumidity = (int)data[2];
        outsideHumidity = (int)data[3];

        averageWindSpeed = (int)data[10];

        highWindSpeed = (int)data[14];
        
        highWindSpeedDirection = new Direction((int)data[11]);
        windDirection = new Direction((int)data[11]);

        averageUVIndex = -9999;
        ET = -9999;
        highSolarRadiation = -9999;
        highUVIndex = -9999;
        forecastRule = -9999;
        leafTemperature1 = -9999;
        leafTemperature2 = -9999;
        leafWetness1 = -9999;
        leafWetness2 = -9999;
        soilTemperature1 = -9999;
        soilTemperature2 = -9999;
        soilTemperature3 = -9999;
        soilTemperature4 = -9999;
        downloadRecordType = -9999;
        extraHumidity1 = -9999;
        extraHumidity2 = -9999;
        extraTemperature1 = -9999;
        extraTemperature2 = -9999;
        extraTemperature3 = -9999;
        soilMoisture1 = -9999;
        soilMoisture2 = -9999;
        soilMoisture3 = -9999;
        soilMoisture4 = -9999;
        checkNA();
        checkValidDate();
        applyCalibration();
        //if (isValid()) {
         //   cleanupData();
        //}
    }

    public static Wm2DmpRecord[] parseByteArray(byte[] bytes) {
        Vector entries = new Vector();
        byte[] buffer = new byte[ARCHIVE_RECORD_SIZE];
        int recordCount = bytes.length / ARCHIVE_RECORD_SIZE;
        for (int i = 0; i < recordCount; i++) {
            for (int j = ARCHIVE_RECORD_SIZE * i, k = 0; j < (ARCHIVE_RECORD_SIZE * i) + ARCHIVE_RECORD_SIZE; j++) {
                buffer[k++] = bytes[j];
            }
            Wm2DmpRecord e = new Wm2DmpRecord(buffer);
            entries.addElement(e);
        }
        return (Wm2DmpRecord[]) entries.toArray(new Wm2DmpRecord[0]);
    }

     private void checkNA() {
        if (outsideTemperature < -1500 || outsideTemperature > 1500) { // temps in 0.1 values
        	outsideTemperature = -9999;
        }

        if (highOutsideTemperature < -1500 || highOutsideTemperature > 1500) { // temps in 0.1 values
        	highOutsideTemperature = -9999;
        }
        
        if (lowOutsideTemperature < -1500 || lowOutsideTemperature > 1500) { //temps in 0.1 values
        	lowOutsideTemperature = -9999;
        }
        
        if (outsideHumidity > 100 && outsideHumidity <= 110)
        { outsideHumidity = 100;}
        
        if (outsideHumidity < 0 || outsideHumidity > 100) {
        	outsideHumidity = -9999;
        }
        
        if (windDirection.getDegrees() == -9999)
        {
        		averageWindSpeed = -9999;
        		highWindSpeed = -9999;
        }
        
    }
    
    private void checkValidDate() {
        if (date.getTime().getTime() > new Date().getTime()) {
           valid = false;
            setInvalidReason("date in future");
        }
    }
    
    private void applyCalibration() {
        outsideTemperature += (SnapShot.getOutsideTempCAL() * 10);
        highOutsideTemperature += (SnapShot.getOutsideTempCAL() * 10);
        lowOutsideTemperature += (SnapShot.getOutsideTempCAL() * 10);
        insideTemperature += (SnapShot.getInsideTempCAL()*10);    
        outsideHumidity += (SnapShot.getOutsideHumidityCAL());
    }
    
    public int bytesToInt(byte[] bytes) {
        int adder, addee;

        addee = (bytes[1] << 8);
        if ((int) bytes[0] < 0) {
            adder = (bytes[0]);
            adder += 256;
        } else {
            adder = bytes[0];
        }
        addee += adder;
        return addee;
    }
    
  

    public int getUnsigned(byte[] cal) {
      int adder;
      int addee;
      int count;
      addee = (cal[1] << 8);
      if ((int) cal[0] < 0) {
        adder = (cal[0]);
        adder += 256;
      } else {
        adder = cal[0];
      }
      addee += adder;
      return addee;
    }

    /**
     *
     *
     * @since 1.0
     */
    public String toString() {
        Date sqlDate = new java.sql.Date(date.getTimeInMillis());
        Time sqlTime = new java.sql.Time(date.getTimeInMillis());

        String str = new String();

        if (!isValid()) {

            for (Iterator i = getInvalidReasons().iterator();
                    i.hasNext();) {
                String reason = (String) i.next();
                str += "\ninvalid DmpRecord: " + reason;
            }
            str += "\n";
        }

        str += sqlDate.toString() + "\t" + sqlTime.toString() + "\t" + outsideTemperature + "\t" 
        + insideTemperature + "\t" + getAvgWindSpeed() + "\t" + getWindDirection();
        return str;
    }

    /**
     *
     *
     * @return
     * @since 1.0
     */
    public String toSQLUpdate() {
        java.sql.Date sqlDate = new java.sql.Date(date.getTimeInMillis());
        java.sql.Time sqlTime = new java.sql.Time(date.getTimeInMillis());
        return "INSERT INTO dmprecords (date, recordedDate, outsideTemperature, highOutTemperature, " 
        + "lowOutTemperature, rainfall, highRainRate, barometer, solarR" 
        + "adiation, numberOfWindSamples, insideTemperature, insideHumidity," 
        + "outsideHumidity, averageWindSpeed, highWindSpeed, directionOfHiWi" 
        + "ndSpeed, prevailingWindDirection, averageUVIndex, ET, highSolarRa" 
        + "diation, highUVIndex, forecastRule, leafTemperature1, leafTempera" 
        + "ture2, leafWetness1, leafWetness2, soilTemperature1, soilTemperat" 
        + "ure2, soilTemperature3, soilTemperature4, downloadRecordType, ext" 
        + "raHumidity1, extraHumidity2, extraTemperature1, extraTemperature2" 
        + ", extraTemperature3, soilMoisture1, soilMoisture2, soilMoisture3," 
        + "soilMoisture4, dewpoint, windchill) VALUES ('" + sqlDate.toString() 
        + " " + sqlTime.toString() + "'," + "'" + new java.sql.Date(new Date().getTime()).toString() 
        + " " + new java.sql.Time(new Date().getTime()).toString() 
        + "'," + getAvgOutTemp() + "," + getHiOutTemp() + "," 
        + getLowOutTemp() + "," + getRain() + "," + getHighRainRate() 
        + "," + getBarometer() + "," + solarRadiation + "," + numberOfWindSamples 
        + "," + getAvgInTemp() + "," + getInHumidity() + "," + getOutHumidity() 
        + "," + getAvgWindSpeed() + "," + getWindGust() + "," + highWindSpeedDirection 
        + "," + getWindDirection().getDegrees() + "," + averageUVIndex + "," + ET 
        + "," + highSolarRadiation + "," + highUVIndex + "," + forecastRule + "," 
        + leafTemperature1 + "," + leafTemperature2 + "," + leafWetness1 + "," 
        + leafWetness2 + "," + soilTemperature1 + "," + soilTemperature2 + "," 
        + soilTemperature3 + "," + soilTemperature4 + "," + downloadRecordType + "," 
        + extraHumidity1 + "," + extraHumidity2 + "," + extraTemperature1 + "," 
        + extraTemperature2 + "," + extraTemperature3 + "," + soilMoisture1 + "," 
        + soilMoisture2 + "," + soilMoisture3 + "," + soilMoisture4 + "," 
        + Converter.getDewpoint((float) getOutHumidity(), (float) getOutsideTemperature()) 
        + "," + Converter.getWindChill(getWindGust(), (float) getOutsideTemperature()) + ")";
    }

    public Date getDate() {
        return date.getTime();
    }

    public double getOutsideTemperature() {
    	if (outsideTemperature == -9999) {return -9999;}
    	else
        return (float)outsideTemperature/10;
    }

    public boolean isValid() {
        return valid;
    }

    public double getAvgInTemp() {
        return (float)insideTemperature/10;
    }

    public double getAvgOutTemp() {
    	if (outsideTemperature == -9999) {return -9999;}
    	else
        return (float)outsideTemperature/10;
    }

    public double getHiOutTemp() {
    	if (highOutsideTemperature == -9999) {return -9999;}
    	else
        return (float)highOutsideTemperature/10;
    }

    public double getLowOutTemp() {
    	if (lowOutsideTemperature == -9999) {return -9999;}
        return (float)lowOutsideTemperature/10;
    }

    public double getRain() {
    	if (convertRain != -9999)
    	{
        	return (rainfall / 100.0)*convertRain;
    	}
    	else
    		return -9999;
    }

    public double getHighRainRate() {
    	if (convertRain != -9999)
    	{
    		return highRainfallRate*convertRain;
    	}
    	else
    		return -9999;
    }

    public int getAvgWindSpeed() {
        return averageWindSpeed;
    }

    public Direction getWindDirection() {
        return windDirection;
    }

    public int getWindGust() {
        return highWindSpeed;
    }

    public double getBarometer() {
        return pressure/1000.0D;
    }

    public int getInHumidity() {
        return insideHumidity;
    }

    public int getOutHumidity() {
        return outsideHumidity;
    }

    public java.util.Date getDate(TimeZone tz) {
        Calendar c = (Calendar) date.clone();
        c.setTimeZone(tz);
        return c.getTime();
    }

//  @SuppressWarnings("unchecked")
    private void setInvalidReason(String invalidReason) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("invalid archive entry: " + invalidReason);
            LOGGER.debug("entry: " + this);
        }
        invalidReasons.add(invalidReason);
    }

    private List getInvalidReasons() {
        return invalidReasons;
    }

    public int getAverageUVIndex() {
        return averageUVIndex;
    }

    public int getHighUVIndex() {
        return highUVIndex;
    }

    public int getHighSolarRadiation() {
        return highSolarRadiation;
    }

    public int getAverageSolarRadiation() {
        return solarRadiation;
    }

    public int getNumberOfWindSamples() {
        return numberOfWindSamples;
    }

    /**
     * @return the highWindSpeedDirection
     */
    public Direction getHighWindSpeedDirection() {
        return highWindSpeedDirection;
    }

    /**
     * @param highWindSpeedDirection the highWindSpeedDirection to set
     */
    public void setHighWindSpeedDirection(Direction highWindSpeedDirection) {
        this.highWindSpeedDirection = highWindSpeedDirection;
    }
    
    public static synchronized void setRainValue(int rainGauge) {
    	convertRain = -9999; // initial
    	if (rainGauge == 0) {convertRain = -9999;}
    	if (rainGauge == 1) {convertRain = 1;}
    	if (rainGauge == 2) {convertRain = 0.7874015748031496;}
    }
}
