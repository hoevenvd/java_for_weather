 /*
  *
 */
package org.tom.weather.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

import org.tom.util.Rounding;
import org.tom.weather.ArchiveEntry;
import org.tom.weather.WeatherStation;
import org.tom.weather.upload.DataUploader;
import org.tom.weather.upload.ws.DataUploaderImpl;
import org.tom.weather.ws.client.WxWsClient;

import org.tom.weather.davis.wm2.Wm2DmpRecord;
import org.tom.weather.davis.wm2.SnapShot;
import org.tom.weather.posting.DataPoster;
import uk.me.jstott.jweatherstation.util.Process;
import uk.me.jstott.jweatherstation.util.UnsignedByte;

/**
 * 
 * 
 * @author Jonathan Stott
 * @version 1.0
 * @since 1.0
 */
public class WeatherMonitor2 extends Station implements WeatherStation {

    static protected final int RECORD_SIZE = 21;
    static protected final int BUFFER_SIZE = 6;
    static protected final int LOOP_SIZE = 18;
    static final int ARCHIVE_RECORD_SIZE = 21;
    static protected final byte FFFF[] = {(byte) 0xff, (byte) 0xff};
    static protected final byte CR = 0x0d;
    private static final Logger LOGGER = Logger.getLogger(WeatherMonitor2.class);
    private static final Logger DATA_PROBLEMS_LOGGER = Logger.getLogger("DATA_PROBLEMS_LOGGER");
    private static String barFlag = "unknown";
    private static float outsideHumidityCAL;
    private static float outsideTempCAL;
    private static float insideTempCAL;

    public WeatherMonitor2(String portName, int baudRate, int rainGauge) throws PortInUseException,
            NoSuchPortException, IOException {
        super(portName, baudRate, rainGauge);
		Wm2DmpRecord.setRainValue(rainGauge);
    }

    public boolean test() throws IOException {
        clearInputBuffer();
        outsideHumidityCAL = getOutsideHumidityCALFromDevice();
        SnapShot.setOutsideHumidityCAL(outsideHumidityCAL);
        outsideTempCAL = getOutsideTempCALFromDevice();
        SnapShot.setOutsideTempCAL(outsideTempCAL);
        insideTempCAL = getInsideTempCALFromDevice();
        SnapShot.setInsideTempCAL(insideTempCAL);
    	LOGGER.info("Calibration inside temperature : "+Rounding.round(insideTempCAL,1));
    	LOGGER.info("Calibration outside temperature: "+Rounding.round(outsideTempCAL,1));
		LOGGER.info("Calibration outside humidity   : "+Rounding.round(outsideHumidityCAL,0));
        return true;
        }
        
        public float getOutsideHumidityCAL()
        {
           return outsideHumidityCAL;
        }
        
        private float getOutsideHumidityCALFromDevice() throws IOException
        {
           byte[] cmd =
           { (byte)'W', (byte)'R', (byte)'D', 0x44, (byte)0xda };
           return (getIntAt(cmd) * 10.0f);
        }
        
        public float getOutsideTempCAL()
        {
           return outsideTempCAL;
        }
        
        private float getOutsideTempCALFromDevice() throws IOException
        {
           byte[] cmd =
           { (byte)'W', (byte)'R', (byte)'D', 0x44, 0x78 };
           return getIntAt(cmd);
        }
        
        public float getInsideTempCAL()
        {
           return insideTempCAL;
        }
        
        private float getInsideTempCALFromDevice() throws IOException
        {
           byte[] cmd =
           { (byte)'W', (byte)'R', (byte)'D', 0x44, 0x52 };
           return getIntAt(cmd);
        }

    protected SnapShot readLoopData() throws IOException {
        barFlag = getBarFlag();
        SnapShot snap = null;
        sendString("LOOP");
        sendBytes(FFFF);
        sendByte(CR);
        if (getAck()) {
            byte loopData[] = new byte[LOOP_SIZE];
            readBytes(loopData);
            snap = new SnapShot(loopData, barFlag);
        }
        return snap;
    }

    private Calendar getLastDate() {
        return lastDate;
    }

    protected void sendString(String str) throws IOException {
        sendBytes(str.getBytes());
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    private String getBarFlag() throws IOException {
        byte[] cmd = {(byte) 'W', (byte) 'R', (byte) 'D', 0x22, 0x79};
        byte ret = getByteAt(cmd);
        if ((ret & 0x1) != 0) {
            return "Steady";
        }
        if ((ret & 0x2) != 0) {
            return "Rising";
        }
        if ((ret & 0x4) != 0) {
            return "Falling";
        }
        return "Invalid";
    }

    private byte getByteAt(byte[] cmd) throws IOException {

        while (true) {
            sendBytes(cmd);
            sendByte(CR);
            if (getAck()) {
                return readByte();
            }
        }
    }
    
    private float getIntAt(byte[] cmd)  throws IOException
    {
       
       byte[] cal = new byte[2];
       int adder;
       int addee;
       int count;
       
       while (true)
       {
          sendBytes(cmd);
          sendByte(CR);
          if (getAck())
          {
        	  count = getInputStream().available();
        	  readBytes(cal);
        	  if (count == 2)
             //if ((count = (readBytes(cal))) == 2)
             {
                addee = (cal[1] << 8);
                if ((int)cal[0] < 0)
                {
                   adder = (cal[0]);
                   adder += 256;
                }
                else
                {
                   adder = cal[0];
                }
                addee += adder;
                return (float)addee / 10.0f;
             }
          }
       }
    }

    public void readCurrentConditions() throws Exception {
        SnapShot snap = readLoopData();
        if (snap != null)
        {
        	String outsideTempTxt="";
        	String insideTempTxt="";
        	String outsideHumTxt="";
        	String insideHumTxt="";
        	String windchillTxt="";
        	String pressureTxt="";
        	String windDirTxt="";
        	String windSpeedTxt="";
        	String barFlagTxt="";

        	outsideTempTxt=Double.toString(Rounding.round(snap.getOutsideTemp(),1));
        	insideTempTxt=Double.toString(Rounding.round(snap.getInsideTemp(),1));
        	outsideHumTxt=Integer.toString((int)Rounding.round(snap.getOutsideHumidity(),0));
        	insideHumTxt=Integer.toString((int)Rounding.round(snap.getInsideHumidity(),0));
        	windchillTxt=Double.toString(Rounding.round(snap.getWindchill(),1));
        	pressureTxt=Double.toString(Rounding.round(snap.getPressure(),3));
        	windDirTxt=Double.toString(Rounding.round(snap.getWindDirection().getDegrees(),0));
        	windSpeedTxt=Double.toString(Rounding.round(snap.getWindspeed(),1));
        	barFlagTxt=snap.getBarStatus();
        	
        	if (snap.getOutsideTemp() == -9999)                {outsideTempTxt="n/a";}
        	if (snap.getInsideTemp() == -9999)                 {insideTempTxt="n/a";}
        	if (snap.getOutsideHumidity() == -9999)            {outsideHumTxt="n/a";}
        	if (snap.getInsideHumidity() == -9999)             {insideHumTxt="n/a";}
        	if (snap.getWindchill() == -9999)                  {windchillTxt="n/a";}
        	if (snap.getPressure() == -9999)                   {pressureTxt="n/a";}
        	if (snap.getWindDirection().getDegrees() == -9999) {windDirTxt="n/a";}
        	if (snap.getWindspeed() == -9999)                  {windSpeedTxt="n/a";}
        	if (snap.getBarStatus().equals("unknown"))         {barFlagTxt="n/a";}
        	
        LOGGER.info("==============================================================================");
        LOGGER.info("[\t"+snap.getDate());
        LOGGER.info("[\tout temp: "+outsideTempTxt+"\t\tout hum:   "+outsideHumTxt+"\t\twindchill: "+windchillTxt+"\t");
        LOGGER.info("[\tin temp:  "+insideTempTxt+"\t\tin hum:    "+insideHumTxt+"\t\tpressure:  "+pressureTxt+"\t\t");
        LOGGER.info("[\twinddir:  "+windDirTxt+"\t\twindspeed: "+windSpeedTxt+"\t\tbarflag:   "+barFlagTxt+"\t\t");
        post(snap);
        }
    }

    public void readArchiveMemory() throws Exception {
        int i = 0;
        byte archiveData[];
        Wm2DmpRecord archiveEntries[];
        DataUploader myupload = null;
        //Wm2DmpRecord dmpRecord[];
        //dmpRecord = null;
        List dmpRecords = new ArrayList();
        int newNumberOfArchiveBytes;
        int numberOfArchiveBytes = getNumberOfArchiveEntries();
        archiveData = getArchiveData(numberOfArchiveBytes, 0);
        archiveEntries = Wm2DmpRecord.parseByteArray(archiveData);
        uploadDmpRecords(archiveEntries);
        try {
            newNumberOfArchiveBytes = getNumberOfArchiveEntries();
            while (newNumberOfArchiveBytes > numberOfArchiveBytes) {
                int bytesToGet = newNumberOfArchiveBytes - numberOfArchiveBytes;
                LOGGER.info("missed " + bytesToGet + " bytes of archive data - going back in..");
                archiveData = getArchiveData(bytesToGet, numberOfArchiveBytes);
                archiveEntries = Wm2DmpRecord.parseByteArray(archiveData);
                uploadDmpRecords(archiveEntries);
                numberOfArchiveBytes = newNumberOfArchiveBytes;
                newNumberOfArchiveBytes = getNumberOfArchiveEntries();
            }
            resetArchiveMemory();
        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace();
            // don't erase archive memory
        }
    }

    public int getNumberOfArchiveEntries() throws IOException {
        byte[] rawNumber = getCommChipRam((byte) 1, (byte) 0, (byte) 7);
        // byte address = rawNumber[1];
        return bytesToInt(rawNumber);
    }

    public byte[] getArchiveData(int numberOfArchiveBytes, int offsetNumberOfBytes) {
        boolean ok = false;
        byte[] buffer = null;

        int BLOCK_SIZE = 6;
        //int numberOfArchiveBytes = 0;
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();

        try {
            LOGGER.info("checking archive memory");
            int numPasses = numberOfArchiveBytes / (BLOCK_SIZE * ARCHIVE_RECORD_SIZE);
            //numPasses = 1;
            int numRecords = numberOfArchiveBytes / ARCHIVE_RECORD_SIZE;
            LOGGER.info("inserting " + numRecords + " archive entries");

            int extraRecords = (numberOfArchiveBytes % (BLOCK_SIZE * ARCHIVE_RECORD_SIZE)) / ARCHIVE_RECORD_SIZE;
            for (int i = 0; i < numPasses; i++) {
                buffer = new byte[(BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + 2 + 1];
                buffer = getArchiveRecord((short) (i * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes), (short) ((i * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE)) + (BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes));
                LOGGER.info("getting " + (i + 1) + " of " + numPasses + " batches");
                outBytes.write(buffer, 0, buffer.length - 2);   // omit checksum at the end
            }
            if (extraRecords != 0) {
                buffer = new byte[(numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE)) + (extraRecords * ARCHIVE_RECORD_SIZE) - (numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE)) + 2 + 1];
                buffer = getArchiveRecord((short) (numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes), (short) ((numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE)) + (extraRecords * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes));
                outBytes.write(buffer, 0, buffer.length - 2);
            }

            outBytes.flush();
        } catch (IOException e) {
            buffer = null;
            LOGGER.error(e);
            ok = false;
        }
        return outBytes.toByteArray();
    }

    byte[] getArchiveRecord(short startAddress, short endAddress) throws IOException {
        byte[] ret = null;
        byte lowOrder, highOrder;

        int numberWanted = (endAddress - startAddress + 2);
        while (true) {
            while (true) {	// send command
                //clearCommBuffer();
                sendString("SRD"); //_serialPort.putBytes(new String("SRD").getBytes());
                lowOrder = (byte) ((startAddress) & 0x00ff);
                highOrder = (byte) ((startAddress) >> 8);
                sendByte(lowOrder);//_serialPort.putByte(lowOrder);
                sendByte(highOrder);//_serialPort.putByte(highOrder);
                lowOrder = (byte) ((endAddress - startAddress - 1) & 0x00ff);
                highOrder = (byte) ((endAddress - startAddress - 1) >> 8);
                sendByte(lowOrder);//_serialPort.putByte(lowOrder);
                sendByte(highOrder);//_serialPort.putByte(highOrder);
                sendByte(CR);//_serialPort.putByte(CR);
                if (getAck()) {        // Look for acknowledge...
                    break;
                }
                LOGGER.info("missed ack");
            }
            ret = new byte[numberWanted];	// account for crc
            //int count = _serialPort.getBytes(ret);
            int count = getInputStream().available();
            readBytes(ret); //int count = _serialPort.getBytes(ret);
            if (count == numberWanted) {
                break;
            }
            //clearCommBuffer();
        }
        return ret;
    }

    int clearCommBuffer() throws IOException {

        boolean more = true;

        int num = 0;
        int val = -1;

        while (more) {
            val = readByte(); //val = getByte();
            if (val == -1) {
                break;
            } else {
                num++;
            }
        }
        if (num > 0) {
            System.out.println("cleared " + num + " bytes");
        }
        return num;
    }

    void resetArchiveMemory() throws IOException {
        int i;
        byte data[] = {(byte) 'R', (byte) 'W', (byte) 'R', 0x17, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0d};
        //disableTimer();
        while (true) {
            sendBytes(data);
            if (getAck()) {
                break;
            }
        }
        //enableTimer();
    }

    byte[] getCommChipRam(byte bank, byte address, byte number_of_nibbles) throws IOException {
        int errorCount = 0;
        int MAX_ERRORS = 10;

        byte[] ret = null;

        while (true) {
            while (++errorCount < MAX_ERRORS) {	// send command

                sendString("RRD"); //_serialPort.putBytes(new String("RRD").getBytes());
                sendByte(bank); //_serialPort.putByte(bank);              // Send the command.
                sendByte(address); //_serialPort.putByte(address);              // Send the address.
                sendByte((byte) (number_of_nibbles - 1)); //_serialPort.putByte(number_of_nibbles - 1);
                sendByte(CR); //_serialPort.putByte(0x0d);
                if (getAck()) {        // Look for acknowledge...
                    break;
                }
            }
            int n = (number_of_nibbles / 2) + (number_of_nibbles % 2);
            ret = new byte[n];
            int count = getInputStream().available();
            readBytes(ret); //int count = _serialPort.getBytes(ret);
            if (count == n) {
                break;
            }
            if (errorCount >= MAX_ERRORS) {
                System.exit(1);
            }
        }
        return ret;
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
    

   private void post(SnapShot snap) throws RemoteException {
        for (Iterator iter = getPosterList().iterator(); iter.hasNext();) {
            DataPoster poster = (DataPoster) iter.next();
            poster.post(snap);
        }
    }

    private void uploadDmpRecords(Wm2DmpRecord[] dmpRecords2) throws Exception {

        for (Iterator iter = getUploaderList().iterator(); iter.hasNext();) {
            DataUploader myUploader = (DataUploader) iter.next();
            myUploader.upload(dmpRecords2);
        }
    }
}
