package org.tom.weather.davis.wm2;

import java.io.*;

import javax.naming.*;

import java.util.*;
//import org.tom.util.Logger;
import org.tom.weather.*;
import org.tom.weather.posting.DataPoster;
import org.apache.log4j.Logger;


public class WeatherMonitor extends Station {
  private static final Logger LOGGER = Logger.getLogger(WeatherMonitor.class);

  private Properties props = new Properties();
  public static boolean metricsInPropertiesFile = false;
  Context ctx = null;
  // private Port _serialPort;
  private static final byte CR = 0x0d;
  private static final byte FFFF[] = { (byte) 0xff, (byte) 0xff };
  private static final byte LOOP_CMD[] = new String("LOOP").getBytes();
  private static final int ACK = 6;
  private static final int LOOP_SIZE = 18;
  private static final int BLOCK_SIZE = 6;
  static final int ARCHIVE_RECORD_SIZE = 21;
  private static final int MAX_DELEGATES = 10;
  private static float outsideHumidityCAL;
  private static float outsideTempCAL;
  private static float pressureCAL;
  private static float insideTempCAL;
  private PeriodTimerController controller = null;
  private static SqlHelper sqlHelper = null;
  private PeriodDataTimer[] timers = new PeriodDataTimer[Period.FOREVER + 1];
  private static DataPoster dataPosters[] = null;

  public PeriodData getPeriodData(int pd) {
    return timers[pd].getPeriodData();
  }

  public PeriodDataTimer[] getPeriodDataTimers() {
    return timers;
  }

  public static DataPoster[] getDataPosters(Properties props) {
    if (dataPosters == null) {
      DataPoster array[] = new DataPoster[MAX_DELEGATES];
      for (int i = 0; i < MAX_DELEGATES; i++) {
        try {
          String className = props.getProperty(Constants.POSTING_DELEGATE + "."
              + (i + 1));
          if (className != null) {
            array[i] = (DataPoster) Class.forName(className).newInstance();
          }
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      dataPosters = array;
    }
    return dataPosters;
  }

  public WeatherMonitor(Properties props) throws Exception {
    super((String) props.getProperty(Constants.PORT), 2400);
    this.props = props;
    boolean calibrated = false;

    // todo - spring the port number

    // _serialPort = new Port(props.getProperty(Constants.PORT));

    while (!calibrated) {
      try {
        outsideHumidityCAL = getOutsideHumidityCALFromDevice();
        LOGGER.debug("outside humidity cal: " + outsideHumidityCAL);
        SnapShot.setOutsideHumidityCAL(outsideHumidityCAL);
        delay(500);
        outsideTempCAL = getOutsideTempCALFromDevice();
        LOGGER.debug("outside temp cal: " + outsideTempCAL);
        SnapShot.setOutsideTempCAL(outsideTempCAL);
        delay(500);
        pressureCAL = getPressureCALFromDevice();
        LOGGER.debug("pressure cal: " + pressureCAL);
        SnapShot.setPressureCAL(pressureCAL);
        delay(500);
        insideTempCAL = getInsideTempCALFromDevice();
        LOGGER.debug("inside temp cal: " + insideTempCAL);
        SnapShot.setInsideTempCAL(insideTempCAL);
        delay(500);
        calibrated = true;
      } catch (IOException e) {
        calibrated = false;
        LOGGER.warn(e);
        throw e;
      }
    }
  }

  // private void closePort() {
  // _serialPort.close();
  // }
  //
  private byte[] getLoopData() throws IOException, WeatherMonitorException {
    byte loopData[] = new byte[LOOP_SIZE];
    boolean done = false;
    while (!done) {
      LOGGER.debug("getting loop data");
      delay(500);
      sendBytes(LOOP_CMD);
      byte num = new Integer(65536 - 1).byteValue();
      sendByte((byte)0xFF);
      sendByte(num);
      //sendBytes(FFFF);
      sendByte(CR);
      if (getAck()) {
        delay(500);
        readBytes(loopData);
        done = true;
      } else {
        LOGGER.info("no ack");
        clearInputBuffer();
        delay(2000);
      }
    }
    clearInputBuffer();
    return loopData;
  }

  private void writeToContext(org.tom.weather.SnapShot snapshot) {
    DataPoster[] posters = WeatherMonitor.getDataPosters(props);
    if (posters != null) {
      for (int i = 0; i < MAX_DELEGATES; i++) {
        if (posters[i] != null) {
          posters[i].post(snapshot);
        }
      }
    }
  }

  private static synchronized SqlHelper getSqlHelper(Properties props) {
    if (sqlHelper == null) {
      sqlHelper = SqlHelper.getInstance(props);
    }
    return sqlHelper;
  }

  private PeriodDataTimer addTimer(Period pd, String intervalString,
      PeriodTimerController controller) {
    PeriodDataTimer timer = null;
    int interval;
    try {
      interval = Integer.parseInt(props.getProperty(intervalString));
      if (interval != 0) {
        timer = controller.addTimer(pd,
            (interval == -1 ? -1 : interval * 1000L));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return timer;
  }

  // public void createHistoryControllers() {
  // PeriodDataTimer timer = null;
  // controller = PeriodTimerController.getInstance(props);
  // try {
  // timers[Period.THIS_HOUR] = addTimer(new Period(Period.THIS_HOUR),
  // Constants.PERIOD_THIS_HOUR_FREQUENCY, controller);
  // timers[Period.LAST_HOUR] = addTimer(new Period(Period.LAST_HOUR),
  // Constants.PERIOD_LAST_HOUR_FREQUENCY, controller);
  // timers[Period.TODAY] = addTimer(new Period(Period.TODAY),
  // Constants.PERIOD_TODAY_FREQUENCY, controller);
  // timers[Period.YESTERDAY] = addTimer(new Period(Period.YESTERDAY),
  // Constants.PERIOD_YESTERDAY_FREQUENCY, controller);
  // timers[Period.THIS_WEEK] = addTimer(new Period(Period.THIS_WEEK),
  // Constants.PERIOD_THIS_WEEK_FREQUENCY, controller);
  // timers[Period.LAST_WEEK] = addTimer(new Period(Period.LAST_WEEK),
  // Constants.PERIOD_LAST_WEEK_FREQUENCY, controller);
  // timers[Period.THIS_MONTH] = addTimer(new Period(Period.THIS_MONTH),
  // Constants.PERIOD_THIS_MONTH_FREQUENCY, controller);
  // timers[Period.LAST_MONTH] = addTimer(new Period(Period.LAST_MONTH),
  // Constants.PERIOD_LAST_MONTH_FREQUENCY, controller);
  // timers[Period.THIS_SEASON] = addTimer(new Period(Period.THIS_SEASON),
  // Constants.PERIOD_THIS_SEASON_FREQUENCY, controller);
  // timers[Period.LAST_SEASON] = addTimer(new Period(Period.LAST_SEASON),
  // Constants.PERIOD_LAST_SEASON_FREQUENCY, controller);
  // timers[Period.THIS_YEAR] = addTimer(new Period(Period.THIS_YEAR),
  // Constants.PERIOD_THIS_YEAR_FREQUENCY, controller);
  // timers[Period.LAST_YEAR] = addTimer(new Period(Period.LAST_YEAR),
  // Constants.PERIOD_LAST_YEAR_FREQUENCY, controller);
  // timers[Period.FOREVER] = addTimer(new Period(Period.FOREVER),
  // Constants.PERIOD_FOREVER_FREQUENCY, controller);
  // } catch (Exception e) {
  // LOGGER.error(e);
  // }
  // }
  
  public SnapShot loop() throws IOException, WeatherMonitorException {
    SnapShot snapshot = null;
    delay(2000);
    try {
      byte[] loopData = getLoopData();
      String barFlag = null;
      for (int i = 0; i < 3; i++) {
        try {
          barFlag = getBarFlag();
        } catch (IOException ioe) {
          LOGGER.warn(ioe);
        }
      }
      snapshot = new SnapShot(loopData, barFlag);
    } catch (IOException ioe) {
      delay(500);
      clearInputBuffer();
      throw ioe;
    }
    return snapshot;
  }

  public static void main(String[] args) {
    Properties props = new Properties();
    boolean writeToContext = false;
    int i = 0;
    byte archiveData[];
    ArchiveEntry archiveEntries[];
    int newNumberOfArchiveBytes;
    if (args.length != 1) {
      System.out
          .println("usage: org.tom.weather.davis.wm2.WeatherMonitor propsFile");
      System.exit(1);
    }

    try {
      props.load(new FileInputStream(args[0]));
      metricsInPropertiesFile = new Boolean(props.getProperty(Constants.METRIC))
          .booleanValue();
    } catch (IOException e) {
      LOGGER.error("Unable to load properties from " + args[2]);
      System.exit(1);
    }
    writeToContext = new Boolean(props.getProperty(Constants.WRITE_TO_CONTEXT))
        .booleanValue();
    writeToContext = false;
    WeatherMonitor weatherMonitor = null;
    try {
      weatherMonitor = new WeatherMonitor(props);
    } catch (Exception e2) {
      LOGGER.error(e2);
      System.exit(1);
    }
    if (writeToContext) {
      // weatherMonitor.createHistoryControllers();
    }
    while (true) {
      try {
        SnapShot snapshot = weatherMonitor.loop();
        if (writeToContext) {
          // weatherMonitor.writeToContext(snapshot);
        }
        LOGGER.info(snapshot);
         if (i++ % 10 == 0
         && (new Boolean(props.getProperty(Constants.USE_ARCHIVE_MEMORY))
         .booleanValue() == true)) {
           weatherMonitor.readArchiveMemory();
         }
      } catch (Exception e) {
        LOGGER.error(e);
        sqlHelper = null;
        try {
          LOGGER.info("sleeping...");
          Thread.sleep(5000);
        } catch (InterruptedException e1) {
          LOGGER.warn(e1);
          System.exit(1);
        }
      }
    }
  }

  private void readArchiveMemory() throws IOException {
    int numberOfArchiveBytes = getNumberOfArchiveEntries();
    byte archiveData[] = getArchiveData(numberOfArchiveBytes, 0);
    ArchiveEntry archiveEntries[] = ArchiveEntryImpl.parseByteArray(archiveData);
    // try {
    // getSqlHelper(props).insertArchiveEntries(archiveEntries);
    // getSqlHelper(props).closeConnection();
    // wundergroundLoader.upload(archiveEntries);
    // aprswxnetLoader.upload(archiveEntries);
    // newNumberOfArchiveBytes = weatherMonitor
    // .getNumberOfArchiveEntries();
    // while (newNumberOfArchiveBytes > numberOfArchiveBytes) {
    // int bytesToGet = newNumberOfArchiveBytes - numberOfArchiveBytes;
    // LOGGER.info("missed " + bytesToGet
    // + " bytes of archive data - going back in..");
    // archiveData = weatherMonitor.getArchiveData(bytesToGet,
    // numberOfArchiveBytes);
    // archiveEntries = ArchiveEntryImpl.parseByteArray(archiveData);
    // getSqlHelper(props).insertArchiveEntries(archiveEntries);
    // weatherMonitor.resetArchiveMemory();
    // getSqlHelper(props).closeConnection();
    // try {
    // wundergroundLoader.upload(archiveEntries);
    // aprswxnetLoader.upload(archiveEntries);
    // } catch (Exception e) {
    // LOGGER.error(e);
    // }
    // numberOfArchiveBytes = newNumberOfArchiveBytes;
    // newNumberOfArchiveBytes = weatherMonitor
    // .getNumberOfArchiveEntries();
    // }
    // weatherMonitor.resetArchiveMemory();
    // } catch (Exception e) {
    // LOGGER.error(e); // don't erase archive memory
    // throw e;
    // }

  }
  public static boolean useMetrics() {
    return metricsInPropertiesFile;
  }

  public float getOutsideHumidityCAL() {
    return outsideHumidityCAL;
  }

  private float getOutsideHumidityCALFromDevice() throws IOException {
    byte[] cmd = { (byte) 'W', (byte) 'R', (byte) 'D', 0x44, (byte) 0xda };
    return (getIntAt(cmd) * 10.0f);
  }

  public float getOutsideTempCAL() {
    return outsideTempCAL;
  }

  private float getOutsideTempCALFromDevice() throws IOException {
    byte[] cmd = { (byte) 'W', (byte) 'R', (byte) 'D', 0x44, 0x78 };
    return getIntAt(cmd);
  }

  public float getPressureCAL() {
    return pressureCAL;
  }

  private float getPressureCALFromDevice() throws IOException {
    byte[] cmd = { (byte) 'W', (byte) 'R', (byte) 'D', 0x44, 0x2c };
    return getIntAt(cmd) / 100.0f;
  }

  public float getInsideTempCAL() {
    return insideTempCAL;
  }

  private float getInsideTempCALFromDevice() throws IOException {
    byte[] cmd = { (byte) 'W', (byte) 'R', (byte) 'D', 0x44, 0x52 };
    return getIntAt(cmd);
  }

  private String getBarFlag() throws IOException {
    byte[] cmd = { (byte) 'W', (byte) 'R', (byte) 'D', 0x22, 0x79 };
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

  void resetArchiveMemory() throws IOException {
    int i;
    byte data[] = { (byte) 'R', (byte) 'W', (byte) 'R', 0x17, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x0d };
    // disableTimer();
    while (true) {
      sendBytes(data);
      if (getAck()) {
        break;
      }
    }
    // enableTimer();
  }

  /*
   * void disableTimer() throws IOException {
   * 
   * byte data[] = {(byte) 'D', (byte) 'B', (byte) 'T', 0x0d };
   * 
   * while (true) { _serialPort.putBytes(data); if (getAck() == true) { break; } } }
   * 
   * void enableTimer() throws IOException { byte data[] = {(byte) 'E', (byte)
   * 'B', (byte) 'T', 0x0d };
   * 
   * while (true) { _serialPort.putBytes(data); if (getAck() == true) { break; } } }
   */
  private float getIntAt(byte[] cmd) throws IOException {
    byte[] cal = new byte[2];
    int adder;
    int addee;
    while (true) {
      sendBytes(cmd);
      sendByte(CR);
      if (getAck()) {
        delay(500);
        readBytes(cal);
        addee = (cal[1] << 8);
        if ((int) cal[0] < 0) {
          adder = (cal[0]);
          adder += 256;
        } else {
          adder = cal[0];
        }
        addee += adder;
        return (float) addee / 10.0f;
      }
    }
  }

  private byte getByteAt(byte[] cmd) throws IOException {
    while (true) {
      sendBytes(cmd);
      sendByte(CR);
      if (getAck()) {
        delay(250);
        return readByte();
      }
    }
  }

  /*
   * +----------------------------------------------------------------------------+
   * Read communication chip ram and place in receive_buffer.
   */
  byte[] getCommChipRam(byte bank, byte address, byte number_of_nibbles)
      throws IOException {
    int errorCount = 0;
    int MAX_ERRORS = 10;
    byte[] ret = null;
    while (++errorCount < MAX_ERRORS) { // send command
      sendBytes(new String("RRD").getBytes());
      sendByte(bank); // Send the command.
      sendByte(address); // Send the address.
      sendByte((byte) (number_of_nibbles - 1));
      sendByte((byte) 0x0d);
      if (getAck()) { // Look for acknowledge...
        break;
      }
    }
    int n = (number_of_nibbles / 2) + (number_of_nibbles % 2);
    ret = new byte[n];
    readBytes(ret);
    if (errorCount >= MAX_ERRORS) {
      throw new IOException("error count exceeded");
    }
    return ret;
  }

  /*
   * +----------------------------------------------------------------------------+
   * 'wrd nc XX' Read from weather chip n nibbles (1-8) in bank (c=2,4 for bank
   * 0,1) at address XX.
   */
  byte[] getWeatherChipRam(byte command, byte address) throws IOException {
    byte[] ret = null;
    while (true) { // send command
      sendBytes(new String("WRD").getBytes());
      sendByte(command); // Send the command.
      sendByte(address); // Send the address.
      sendByte((byte) 0x0d);
      if (getAck()) { // Look for acknowledge...
        break;
      }
    }
    int n = (((command) >> 4) + 1) / 2;
    ret = new byte[n];
    readBytes(ret);
    return ret;
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
    // int numberOfArchiveBytes = 0;
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    try {
      LOGGER.info("checking archive memory");
      int numPasses = numberOfArchiveBytes / (BLOCK_SIZE * ARCHIVE_RECORD_SIZE);
      int numRecords = numberOfArchiveBytes / ARCHIVE_RECORD_SIZE;
      LOGGER.info("inserting " + numRecords + " archive entries");
      int extraRecords = (numberOfArchiveBytes % (BLOCK_SIZE * ARCHIVE_RECORD_SIZE))
          / ARCHIVE_RECORD_SIZE;
      for (int i = 0; i < numPasses; i++) {
        buffer = new byte[(BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + 2 + 1];
        buffer = getArchiveRecord((short) (i
            * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes),
            (short) ((i * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE))
                + (BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes));
        LOGGER.info("getting " + (i + 1) + " of " + numPasses + " batches");
        outBytes.write(buffer, 0, buffer.length - 2);
        // omit checksum at the end
      }
      if (extraRecords != 0) {
        LOGGER.info("getting " + extraRecords + " extra records");
        buffer = new byte[(numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE))
            + (extraRecords * ARCHIVE_RECORD_SIZE)
            - (numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE)) + 2 + 1];
        buffer = getArchiveRecord((short) (numPasses
            * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes),
            (short) ((numPasses * (BLOCK_SIZE * ARCHIVE_RECORD_SIZE))
                + (extraRecords * ARCHIVE_RECORD_SIZE) + offsetNumberOfBytes));
        outBytes.write(buffer, 0, buffer.length - 2);
      }
      outBytes.flush();
    } catch (IOException e) {
      buffer = null;
      e.printStackTrace();
      ok = false;
    }
    return outBytes.toByteArray();
  }

  byte[] getArchiveRecord(short startAddress, short endAddress)
      throws IOException {
    byte[] ret = null;
    byte lowOrder, highOrder;
    int numberWanted = (endAddress - startAddress + 2);
    clearInputBuffer();
    sendBytes(new String("SRD").getBytes());
    lowOrder = (byte) ((startAddress) & 0x00ff);
    highOrder = (byte) ((startAddress) >> 8);
    sendByte(lowOrder);
    sendByte(highOrder);
    lowOrder = (byte) ((endAddress - startAddress - 1) & 0x00ff);
    highOrder = (byte) ((endAddress - startAddress - 1) >> 8);
    sendByte(lowOrder);
    sendByte(highOrder);
    sendByte(CR);
    getAck(); // Look for acknowledge...
    LOGGER.info("missed ack");
    ret = new byte[numberWanted]; // account for crc
    readBytes(ret);
    clearInputBuffer();
    return ret;
  }

  /*
   * int fill_crc_buffer (int n) // n includes the CRC bytes { int i; int crc =
   * 0; // initialize the CRC checksum to 0 byte c; for (i = 0; i < n; i++) { c =
   * get_serial_char(); if (c >= 0) { recieve_buffer[i] = c; // Store the data
   * crc = crc_table [(crc >> 8) ^ c] ^ (crc << 8); } else { // there has been
   * an error getting the next character return serial_error_code; } } if (crc ==
   * 0) { return OK; // we have read in n bytes and the CRC is OK } else {
   * return CRC_ERROR; // The CRC check failed. } }
   */
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
}
