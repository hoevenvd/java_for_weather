/*
        Definition of serial port interface routines....
*/
#if !defined( __SERIAL_H )
#define __SERIAL_H

// If a C++ compile, tell compiler asynch functions use C naming convention.
#ifdef __cplusplus
extern "C" {
#endif
#include <asynch_2.h>
#include <asynch_h.h>

#define ACK 6
#define NAK 21
#define ESC 27                            


int initialize_serial_port (int com_no, int irqLine, int baud_rate);
void close_serial_io ();

void select_weather_chip ();
void select_comms_chip ();
void clear_receive_buffer ();

int buffer_has_wrapped ();
int get_buffer_size ();    
int xmodem_transfer (int n, int (*f) (int, int, int));
int send_sensor_images (int n);            // Continually send sensor images.
void stop_sending_images ();

int set_archive_period (int minutes);
int set_station_time ();                   // Sets station to current PC time.
int reset_archive ();
int synchronize_archive ();
int clear_hi_lows ();

int get_sensor_image ();
int get_archive_period ();                 // Return archive period in minutes.
int get_sample_period ();
int get_inside_temperature (float *);
int get_outside_temperature (float *);
int get_inside_humidity (int *);
int get_outside_humidity (int *);
int get_wind_speed (int *);
int get_wind_direction (int *);
int get_barometer (int *);
int get_dew_point (float *);
int get_rain (int *daily, int *year);
int get_inside_tempcal (int *);
int get_outside_tempcal (int *);
int get_barometer_offset (int *);
int get_weather_chip_ram (unsigned char command,  unsigned char address);
int put_serial_string (char *);
int put_serial_char (unsigned char);
int send_unsigned (unsigned);
int get_serial_char ();
int get_weather_chip_ram (unsigned char command,  unsigned char address);
int get_sram (unsigned start_address, unsigned nbytes);
int get_comm_chip_ram (unsigned char bank, unsigned char address,
                                               unsigned char number_of_nibbles);

int SendDumpCommand ();
int GetBlock (int blockNum);
int GetNumberOfBlocks (int *numberOfEntrys);
int GetDumpBuffer (int numberOfBlocks);
int ResizeDumpBuffer ();
int SetStationTime (int month, int day, int hour, int min);
int ResetArchive ();
int SynchRemote ();
int SetArchiveInterval (int intervalCode);
void ExtractLowTime (int *hour, int *min);
void ExtractHiTime (int *hour, int *min);
void ExtractLowDate (int *month, int *day);
void ExtractHiDate (int *month, int *day);

int ClearHiInsideTemperature ();
int ClearLowInsideTemperature ();
int ClearHiOutsideTemperature ();
int ClearLowOutsideTemperature ();
int ClearHiOutsideHumidity ();
int ClearLowOutsideHumidity ();
int ClearHiInsideHumidity ();
int ClearLowInsideHumidity ();
int ClearHiDewPoint ();
int ClearLowDewPoint ();
int ClearHiWindSpeed ();
int ClearLowChill ();

int ClearDailyRain ();
int ClearYearlyRain ();

int PutBarometerCal (int calNumber);

#ifdef  __cplusplus
}
#endif

struct loop_block 
{
   int   inside_temperature,           // øF 
         outside_temperature;          // øF

   int           inside_humidity,
                 outside_humidity;

   int barometer;
   int wind_speed;
   int wind_direction;

   int rain;                          // Total rain on weather station.

};

extern struct loop_block sensor_image;

/*
 CCR_* means Comm Chip Ram   bank,address,# of nibbles 

#define CCR_ARCHIVE_PERIOD       1,0x3c,2
#define CCR_LAST_ARCHIVE_PERIOD  1,0x48,4
#define CCR_SAMPLE_PERIOD        1,0x3a,2
#define CCR_TEMPERATURE_INSIDE   1,0x1c,4
#define CCR_TEMPERATURE_OUTSIDE  1,0x20,4
#define CCR_HUMIDITY_INSIDE      1,0x2e,2
#define CCR_HUMIDITY_OUTSIDE     1,0x30,2
#define CCR_SPEED                1,0x24,2
#define CCR_DIRECTION            1,0x26,4
#define CCR_BAROMETER            1,0x2a,4


   WCR_* means Weather Chip Ram  0xnc,0x##
   n  = number of nibbles
   b  = bank(2 = bank 0,4 = bank 1)
   ## = address

#define WCR_TEMPERATURE_INSIDE   0x44,0x30
#define WCR_TEMPERATURE_OUTSIDE  0x44,0x56
#define WCR_HUMIDITY_INSIDE      0x24,0x80
#define WCR_HUMIDITY_OUTSIDE     0x24,0x98
#define WCR_SPEED                0x22,0x5e
#define WCR_DIRECTION            0x44,0xb4
#define WCR_BAROMETER            0x44,0x00
#define WCR_TIME                 0x64,0xbe
#define WCR_DATE                 0x64,0xc8
#define WCR_KEYDOWN              0x12,0x44
#define WCR_OLDKEY               0x12,0x7b
*/

#endif

