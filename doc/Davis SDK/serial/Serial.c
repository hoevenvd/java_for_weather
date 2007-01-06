#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <dos.h>
#include <time.h>
#include <math.h>
#include "serial.h"
#include "ccitt.h"

static void crc_accum (unsigned char data);

#define RBUFFER_SIZE    1024           // ISR receive buffer.
#define TBUFFER_SIZE    256            // ISR transmit buffer.

#define RETRY  2                       // Number of times to try before 
                                       // command fails.

static int current_port = -1;          // Port used for station connection.

/*
   For get_inside_....() determines source of information.
0 - Weather Link 1 - Weather Station.
*/

static int current_chip = 0;

static unsigned short crc = 0;         // Hold CRC checksums.

unsigned char *receive_buffer;
static int bptr = 0;

struct loop_block sensor_image;


/*
+----------------------------------------------------------------------------+
   Initialize serial port...
*/
int initialize_serial_port (int com_no, int irqLine, int baud_rate)
{
   int error;
   int port_address;

   POPT port_options;

   switch (com_no)
   {
   case 1:
      port_address = 0x3f8;
      break;
   case 2:
      port_address = 0x2f8;
      break;
   case 3:
      port_address = 0x3e8;
      break;
   case 4:
      port_address = 0x2e8;
      break;
   }

   if ( (receive_buffer = (unsigned char *) malloc (50)) == NULL)
      {
//      printf ("Error allocating receive buffer.\n");
      return -1;
      }
      
/*
   Open serial port com_no with input buffer of 1k and output buffer of 256.
*/
   current_port = com_no;
   if ((error = open_a2 (com_no, RBUFFER_SIZE, TBUFFER_SIZE,
                                             irqLine, port_address)) != A_OK )
      {
//      printf ("Error %d opening serial port. %d %x\n", error, com_no, port_address);
      return -1;
      }
/*
   Set the port options...see page 261 of C Asynch Manual.
   if ((error = retop_a2 (com_no, &port_options)) != A_OK)   // Get port options...
      {
      close_a2 (com_no);
//      printf ("Error %d querying port. %d %x\n", error, com_no, port_address);
      return -1;
      }
*/

   port_options.baud_rate = baud_rate;
   port_options.parity = 0;
   port_options.data_bits = 8;
   port_options.stop_bits = 1;
   port_options.remote_flow_ctrl = 0;
   port_options.local_flow_ctrl = 0;
   port_options.bit_trimming = 0;
   port_options.bit_forcing = 0;
   port_options.require_cts = 1;
   port_options.break_time = 250;
   
   if ((error = setop_a2 (com_no, &port_options)) != A_OK)
      {
      close_a2 (com_no);
//      printf ("Error %d setting up serial port.%d %x\n", error, com_no, port_address);
      return -1;
      }

/*
typedef struct                  
{
   int baud_rate;
   int parity;
   int data_bits;
   int stop_bits;
   int remote_flow_ctrl;
   int local_flow_ctrl;
   int bit_trimming;
   int bit_forcing;
   int require_cts;
   int break_time;
} POPT;
*/

   return 0;
}  

/*
+----------------------------------------------------------------------------+
*/
void close_serial_io ()
{
   close_a2 (current_port);
   free (receive_buffer);
}


/*
+----------------------------------------------------------------------------+
  Output a character to current serial port.
*/
int put_serial_char (unsigned char c)
{
   if ( wrtch_a1 (current_port, c) != A_OK )
      return -1;

   return 0;
}      

/*
+----------------------------------------------------------------------------+
  Output a string to current serial port.
*/
static int put_serial_string (char *s)
{
   int i;

   for (i = 0; s [i] != '\0'; i++)
       if ( put_serial_char (s [i]) != 0 )
          return -1;

   return 0;
}


/*
+----------------------------------------------------------------------------+
*/
static int send_unsigned (unsigned d)
{
   unsigned char *cp;

   cp = (unsigned char *) &d;
   wrtch_a1 (current_port, *cp);
   wrtch_a1 (current_port, *(cp+1));
   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
void clear_receive_buffer ()
{
   iflsh_a1 (current_port);
}



/*
+----------------------------------------------------------------------------+
*/
static int crc_fill_buffer (unsigned n)
{
   unsigned i;
   int  c;
   unsigned signal = 0, block = 0;

   crc = 0;
   bptr = 0;

   for (i = 0; i < n; i++)
      {
      if ((c = get_serial_char ()) == -1 )
         return -4;

      receive_buffer [bptr++] = c;
      crc_accum ((unsigned char) c);
//      if ( (signal++ % 128) == 0)
         
//         printf ("Transfered block %d\n", block++);

      }

   return (crc == 0) ? (0) : (-3);
}

/*
+----------------------------------------------------------------------------+
*/
static void crc_accum (unsigned char data)
{
   crc =  crc_table [(crc >> 8) ^ data] ^ (crc << 8);
}

/*
+----------------------------------------------------------------------------+

  Return : 
  1 if full block not in yet...
  -2 for a crc error...
  -1 for a time out or other error...
   
*/
int get_sensor_image ()
{
   unsigned i;
   int c;
   int  n = 0, error;

   if (qsize_a1 (current_port, &n, &i) != A_OK)
      return -1;

   if (n < 18)
      return 1;
   
   if ( (c = get_serial_char ()) != 1 )          // Start of image block not received.
      {
      if (c == -1 )
        return -1;
// Go ahead and get data even if start block not around.
      }

   if ( (error = crc_fill_buffer (17)) < 0 ) 
        return -2;

   sensor_image.inside_temperature =  *((int *)(receive_buffer+0));
   sensor_image.outside_temperature =  *((int *)(receive_buffer+2)); 
   sensor_image.wind_speed =  *((unsigned char *)(receive_buffer+4));
   sensor_image.wind_direction =  *((int *)(receive_buffer+5));
   sensor_image.barometer =  *((int *)(receive_buffer+7));
   sensor_image.inside_humidity = *((unsigned char *)(receive_buffer+9));
   sensor_image.outside_humidity =  *((unsigned char *)(receive_buffer+10));
   sensor_image.rain =  *((unsigned *)(receive_buffer+11));
   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
int fill_buffer (unsigned n)
{
   unsigned i;
   int  c;

   bptr = 0;
   for (i = 0; i < n; i++)
      {
      if ((c = get_serial_char ()) == -1 )
         {
         return -1;
         }

      receive_buffer [bptr++] = c;
      }

   receive_buffer [bptr] = '\0';

   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
static int get_acknowledge ()
{
   int c;

   if ((c = get_serial_char ()) == ACK)
      return 1;
   else
      {
//      printf ("Looking for ACK and got %d (%x)\n", c);
      return 0;
      }
}
   
/*
+----------------------------------------------------------------------------+
  Get a character from current serial port.
*/
int get_serial_char ()
{
   unsigned  pstatus;
   unsigned  char c;
   int       input_q_size = 0, error;

/*
   See if character is in queue.  If not wait 40 clock ticks (2 seconds).  If still no
character, have a TIMEOUT.
*/
   error = iwait_a1 (current_port, (long) 180, &pstatus, 0) ;

   if ( error == A_OK )
      {
      if ( (error = rdch_a1 (current_port, (char *) &c, &input_q_size, &pstatus))
                                                                      != A_OK )
         {
//         printf ("Error reading character...%d\n", error);
         return -1;
         }
      }
   else 
      {
// printf ("Error waiting..%d\n", error);
      return -1;
      }

   return c;                        // No sign extend using unsigned char c.
}      
    

/*
+----------------------------------------------------------------------------+
*/
void select_weather_chip ()
{
   current_chip = 1;
}

/*
+----------------------------------------------------------------------------+
*/
void select_comms_chip ()
{
   current_chip = 0;
}

/*
+----------------------------------------------------------------------------+
*/
int get_barometer (int *b)
{
   if ( current_chip == 0 )         // Read comms chip...
      {
      if ( get_comm_chip_ram (1, 0x2a, 4) == -1 )
         return -1;
      }
   else                             // Read weather chip...
      {
      if ( get_weather_chip_ram (0x44, 0x0) == -1 )
         return -1;
      }
      
   *b =  *((int *) receive_buffer);

   if (*b < 20000 || *b > 32500)
      return -1;

   return 0;
}


/*
+----------------------------------------------------------------------------+
*/
int get_rain (int *daily, int *yearly)
{
   if ( get_weather_chip_ram (0x84, 0xce) == -1)
      return -1;

   *daily =  *((int *)(receive_buffer + 2));
   *yearly =  *((int *)(receive_buffer));
   return 0;
}

/*
+----------------------------------------------------------------------------+
   Read communication chip ram and place in receive_buffer.
*/
int get_comm_chip_ram (unsigned char bank,
                              unsigned char address,
                              unsigned char number_of_nibbles)      
{
   int i;

   for (i = 0; i < RETRY; i++)
       {
       clear_receive_buffer ();
       put_serial_string ("RRD");
       put_serial_char (bank);                // Bank one..
       put_serial_char (address);              
       put_serial_char (number_of_nibbles-1); // Get number_of_nibbles..
       put_serial_char (0x0d);                // Send CR...
       if ( get_acknowledge () == 1)          // Look for acknowledge...
          break;
       }

   if ( i == RETRY )                          // Command failed...
      return -1;
                                         
/*
   If getting odd number of nibbles, get nibbles/2 + 1 bytes....
*/
   if ( fill_buffer (number_of_nibbles/2 + number_of_nibbles % 2)  == -1 )
      return -1;

   return 0;
}

/*
+----------------------------------------------------------------------------+
   'wrd nc XX'  Read from weather chip n nibbles (1-8) in bank (c=2,4 for 
bank 0,1) at address XX.
*/
int get_weather_chip_ram (unsigned char command,  unsigned char address)
{
   int n, i;

   for (i = 0; i < RETRY; i++)
       {
       clear_receive_buffer ();
       put_serial_string ("WRD");
       put_serial_char (command);              // Send the command.
       put_serial_char (address);              // Send the address.
       put_serial_char (0x0d);
       if ( get_acknowledge () == 1)          // Look for acknowledge...
          break;
       }

   if ( i == RETRY )                          // Command failed...
      {
printf ("Did not get the command...\n");
      return -1;
      }

   n = (((command) >> 4) + 1) / 2;

   return fill_buffer (n);
}


/*
+----------------------------------------------------------------------------+
*/
int send_sensor_images (int n)    
{
   int  i;

//   stop_sending_images ();

   for (i = 0; i < 3; i++)
       {
       clear_receive_buffer ();
       put_serial_string ("LOOP");
       send_unsigned ((unsigned) (65536 - n));
       put_serial_char (0x0d);
       if ( get_acknowledge () == 1)          // Look for acknowledge...
          break;
       }

   if ( i == 3 )                             // Command failed...
      return -1;

   return 0;
}


/*
+----------------------------------------------------------------------------+
   Any command stops the communication chip from looping.  Send two
read commands.....
*/
void stop_sending_images ()    
{
   int  i;
/*
   If board is spewing out data, not worth doing a get_comm_chip_ram () which
will retry if an acknowledge is not received.
*/
   clear_receive_buffer ();
   put_serial_string ("RRD");
   put_serial_char (1);                   // Bank one..
   put_serial_char (0);              
   put_serial_char (0);                   // Get number_of_nibbles..
   put_serial_char (0x0d);                // Send CR...

/*
   for (i = 0; i < 10; i++)
      {
      if ( get_comm_chip_ram (1, 0, 1) == 0)
         return;
      }
*/
}


/*
+----------------------------------------------------------------------------+
   Check and see if the wrap flag has been set...
*/
int buffer_has_wrapped ()
{
   if ( get_comm_chip_ram (0, 0x42, 1) == -1)
      return -1;

   return receive_buffer [0] & 0x1;
}   


/*
+----------------------------------------------------------------------------+
*/
int GetNumberOfBlocks (int *numberOfEntrys)
{
   int numberOfBlocks, numberOfBytes;
   if ( get_comm_chip_ram (1, 0x0, 7) == -1)
      return -1;
/*
   New pointer points at byte for next entry.
*/
   numberOfBytes =  *((int *) receive_buffer);   

   *numberOfEntrys = numberOfBytes / 21;

   return (numberOfBytes % 128 == 0) ? 
                                 (numberOfBytes/128) : (numberOfBytes/128 + 1);
}


/*
+----------------------------------------------------------------------------+
   'wrd nc XX'  Read from weather chip n nibbles (1-8) in bank (c=2,4 for 
bank 0,1) at address XX.
int get_sram (unsigned start_address,  unsigned n)
{
   int i;
   char *new_buffer;

   if ((new_buffer = realloc (receive_buffer, n)) == 0)
      return -1;                             // Could not accomadate new size.
   else
      receive_buffer = new_buffer;           // Have new block.

   for (i = 0; i < RETRY; i++)
       {
       clear_receive_buffer ();
       put_serial_string ("SRD");
       send_unsigned (start_address);
       send_unsigned (n-1);
       put_serial_char (0x0d);
       if ( get_acknowledge () == 1)          // Look for acknowledge...
          break;
       }

   if ( i == RETRY )                          // Command failed...
      return -1;

   return crc_fill_buffer (n + 2);
}
*/


/*
+----------------------------------------------------------------------------+
*/
int SendDumpCommand ()
{
   int i;
/*
   Send the "DMP" command..
*/
   for (i = 0; i < RETRY; i++)
       {
       clear_receive_buffer ();
       put_serial_string ("DMP");
       put_serial_char (0x0d);
       if ( get_acknowledge () == 1)          // Look for acknowledge...
          break;
       }

   if ( i == RETRY )                          // Command failed...
      return -5;

   bptr = 0;
   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
int GetDumpBuffer (int numberOfBlocks)
{
   char *new_buffer;

   if ((new_buffer = realloc (receive_buffer, numberOfBlocks * 128)) == 0)
      return -1;                             // Could not accomadate new size.
   else
      receive_buffer = new_buffer;           // Have new block.

   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
int ResizeDumpBuffer ()
{
   char *new_buffer;

   if ((new_buffer = realloc (receive_buffer, 50)) == 0)
      return -1;                             // Could not accomadate new size.
   else
      receive_buffer = new_buffer;           // Have new block.

   return 0;
}


static unsigned char packet [128];

/*
+----------------------------------------------------------------------------+
*/
int GetBlock (int n)
{
   int i, c;

/*
   Get start of header character 01, block number, and one's complement of
block number.
*/
   for (i = 0; i < 3; i++)
      if ((c = get_serial_char ()) == -1)
         return -1;
      else
         packet [i] = c;

/*
   Get 128 data bytes and 2 byte CRC code.
*/
   for (i = 3; i < 133; i++)
      if ((c = get_serial_char ()) == -1)
         return -2;
      else 
         {
         packet [i] = c;
         crc_accum (packet [i]);
         }

   clear_receive_buffer ();

   if (packet [0] != 1)
      {
printf ("Bad start %d\n", packet [0]);
      return -1;
      }
   else if ((packet [1] != ( (unsigned char)~packet [2])) || (packet [1] != n))
      {
printf ("P1 %x P2 %x ~P2 %x N %x\n", packet [1], packet [2], ~packet [2], n);
      return -1;
      }
   else if (crc == 0)
      {
      for (i = 0; i < 128; i++)
         receive_buffer [bptr++] = packet [i+3];
      return 0;
      }

   return -3;                                // Have CRC error.
}

/*
+----------------------------------------------------------------------------+
*/
static int DisableTimer ()
{
   clear_receive_buffer ();
   put_serial_string ("DBT");
   put_serial_char (0x0d);
   if ( get_acknowledge () == 0)
      return -1;

   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
static int EnableTimer ()
{
   clear_receive_buffer ();
   put_serial_string ("EBT");
   put_serial_char (0x0d);
   if ( get_acknowledge () == 0)
      return -1;

   return 0;
}



/*
+----------------------------------------------------------------------------+
  Returns archive period in minutes or -1 if command fails.
*/
int get_archive_period ()
{
   int i;

   if ( get_comm_chip_ram (1, 0x3c, 2) == -1 )
      return -1;
   else
      return receive_buffer [0];
}


/*
+----------------------------------------------------------------------------+
  Returns sample period in minutes or -1 if command fails.
*/
int get_sample_period ()
{
   int i;

   if ( get_comm_chip_ram (1, 0x3a, 2) == -1 )
      return -1;
   else
      return receive_buffer [0];
}


/*
+----------------------------------------------------------------------------+
*/
int SetArchiveInterval (int intervalCode)
{
   unsigned char newSamplePeriod;        // Number of seconds between samples.
   unsigned char archivePeriod;

   clear_receive_buffer ();

/*
   switch (interval)
   {
   case 1:
      newSamplePeriod = 5;
      break;
   case 5:
      newSamplePeriod = 5;
      break;
   case 15:
      newSamplePeriod = 5;
      break;
   case 30:
      newSamplePeriod = 15;
      break;
   case 60:
      newSamplePeriod = 30;
      break;
   case 120:
      newSamplePeriod = 60;
      break;
   default:
      break;
   }
*/
   switch (intervalCode)
   {
   case 0:
      archivePeriod = 1;
      newSamplePeriod = 5;
      break;
   case 1:
      archivePeriod = 5;
      newSamplePeriod = 5;
      break;
   case 2:
      archivePeriod = 15;
      newSamplePeriod = 8;
      break;
   case 3:
      archivePeriod = 30;
      newSamplePeriod = 15;
      break;
   case 4:
      archivePeriod = 60;
      newSamplePeriod = 30;
      break;
   case 5:
      archivePeriod = 120;
      newSamplePeriod = 60;
      break;
   default:
      return -1;
   }
/*
   Set the new sample period.
*/
   put_serial_string ("SSP");
   put_serial_char ((unsigned char) (256 - newSamplePeriod));              
   put_serial_char (0xd);              
   if ( get_acknowledge () == 0)
      {
      return -1;
      }
/*
   Set the new archive period.
*/
   put_serial_string ("SAP");
   put_serial_char ((unsigned char) archivePeriod);              
   put_serial_char (0xd);              
   if ( get_acknowledge () == 0)
      {
      return -1;
      }
/*
   Can not have two different archive intervals in memory...
*/
   if ( ResetArchive () == -1)
      {
      return -1;
      }
}

/*
+----------------------------------------------------------------------------+
*/
void ExtractHiTime (int *hour, int *minute)
{
   char line[25];

   sprintf (line, "%x", *((receive_buffer))  );
   *hour = atoi (line);                     // Get hour..

   sprintf (line, "%x", *((receive_buffer + 1))  );
   *minute = atoi (line);                   // Get minutes...
}

/*
+----------------------------------------------------------------------------+
*/
void ExtractLowTime (int *hour, int *minute)
{
   char line[25];

   sprintf (line, "%x", *((receive_buffer + 2))  );
   *hour = atoi (line);                     // Get hour..

   sprintf (line, "%x", *((receive_buffer + 3))  );
   *minute = atoi (line);                   // Get minutes...
}


/*
+----------------------------------------------------------------------------+
*/
static int get_time (int *hour, int *minute, int *second)
{
   char line[25];

   if( get_weather_chip_ram (0x64, 0xbe) == -1)
      return -1;

   ExtractHiTime (hour, minute);

   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
void ExtractHiDate (int *month, int *day)
{
   char line[25];

   sprintf (line, "%x", *((receive_buffer))  );
   *day = atoi (line);                 // Get day...

   *month =  *((receive_buffer + 1)) & 0x0f;
}

/*
+----------------------------------------------------------------------------+
Each date is 3 nibbles.  First 2 nibbles are day in binary coded decimal.
Month is integer from 1 .. 12;
*/
void ExtractLowDate (int *month, int *day)
{
   char line[25];

   sprintf (line, "%x", ((*(receive_buffer + 1) & 0xf0) >> 4) |
                                       ((*(receive_buffer + 2) & 0x0f) << 4));    

//printf ("day %s", line);
   *day = atoi (line);                                // Get day...

   *month = (*(receive_buffer + 2) & 0xf0)  >> 4;     // Get month...
//printf ("mon %d", *month);
}


/*
+----------------------------------------------------------------------------+
   Adjust last archive time so it is a multiple of the archive period.
*/
static int PutLastArchiveTime (int currentHour, int currentMin)
{
   int archivePeriod;
   unsigned lastArchiveTime;           // 0 ... 1439 minutes

   if ((archivePeriod = get_archive_period ()) == -1)
      return -1;

   if (archivePeriod == 1)
      return 0;

   lastArchiveTime = (currentHour * 60 + currentMin) / archivePeriod;

   lastArchiveTime *= archivePeriod;

   put_serial_string("RWR");
   put_serial_char(0x13);
   put_serial_char(0x48);
   send_unsigned (lastArchiveTime);
   put_serial_char(0xd);
   
   if ( get_acknowledge () == 0)
      {
      return -1;
      }

   return 0;
}

/*
+----------------------------------------------------------------------------+
   Call TimerDisable () before this call...
*/
static int SynchArchive ()
{
   int hour, min, sec;

   if (get_time (&hour, &min, &sec) == -1)
      return -1;

   if ( PutLastArchiveTime (hour, min) == -1)
      return -1;
}


/*
+----------------------------------------------------------------------------+
*/
int ClearHiInsideTemperature ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x34);             // Send Address.
   send_unsigned (0x8000);             // -32768
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearLowInsideTemperature ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x38);             // Send Address.
   send_unsigned (0x7fff);             // 32767
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearHiOutsideTemperature ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x5a);             // Send Address.
   send_unsigned (0x8000);             // -32768
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}
/*
+----------------------------------------------------------------------------+
*/
int ClearLowOutsideTemperature ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x5e);             // Send Address.
   send_unsigned (0x7fff);             // 32767
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearHiOutsideHumidity ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x23);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x9a);             // Send Address.
   send_unsigned (0x0);                // 0.
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearLowOutsideHumidity ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x23);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x9c);             // Send Address.
   put_serial_char (0x7f);             // Clear to 128.
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearHiInsideHumidity ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x23);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x82);             // Send Address.
   send_unsigned (0x0);                // 0.
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearLowInsideHumidity ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x23);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x84);             // Send Address.
   put_serial_char (0x7f);             // Clear to 128.
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}


/*
+----------------------------------------------------------------------------+
*/
int ClearHiDewPoint ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x41);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x8e);             // Send Address.
   send_unsigned (0x8000);             // -32768
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int ClearLowDewPoint ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x41);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x92);             // Send Address.
   send_unsigned (0x7fff);             // 32767
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
Firmware stores hi wind speed in 4 nibbles...but only first two count..
*/
int ClearHiWindSpeed ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x21);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x60);             // Send Address.
   send_unsigned (0x80);               // -128
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}


/*
+----------------------------------------------------------------------------+
*/
int ClearLowChill ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x41);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0xac);             // Send Address.
   send_unsigned (0x7fff);             // 32767
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}


/*
+----------------------------------------------------------------------------+
*/
int ClearYearlyRain ()
{
   if ( DisableTimer () == -1)
      return -1;

   clear_receive_buffer ();
   put_serial_string("RWR");
   put_serial_char(0x17);              // Bank 1... 8 nibbles.
   put_serial_char(0x40);              // Address....
   send_unsigned (0);                  // Zero out previous rain.
   send_unsigned (0);
   put_serial_char(0xd);

   if ( get_acknowledge () == 0)       // Look for acknowledge...
      {
      EnableTimer ();
      return -1;
      }

   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0xce);             // Send Address.
   send_unsigned (0x0);            
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)       // Look for acknowledge...
      {
      EnableTimer ();
      return 0;
      }
   else
      {
      EnableTimer ();
      return -1;
      }
}

/*
+----------------------------------------------------------------------------+
*/
int ClearDailyRain ()
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0xd2);             // Send Address.
   send_unsigned (0x0);            
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
*/
int PutBarometerCal (int calNumber)
{
   clear_receive_buffer ();
   put_serial_string ("WWR");          // Send write command.
   put_serial_char (0x43);             // Nibbles | Bank (1,3 for bank 0,1)
   put_serial_char (0x2c);             // Send Address.
   send_unsigned ( (unsigned) calNumber);            
   put_serial_char (0xd);

   if ( get_acknowledge () == 1)          // Look for acknowledge...
      return 0;
   else
      return -1;
}

/*
+----------------------------------------------------------------------------+
   Synch remote station so archiving on the hour...
*/
int SynchRemote ()
{
   if ( DisableTimer () == -1)
      return -1;

   if (SynchArchive () == -1)
      {
      EnableTimer ();
      return -1;
      }
      
   if ( EnableTimer () == -1 )
      return -1;

   return 0;
}

/*
+----------------------------------------------------------------------------+
*/
int ResetArchive ()
{
   int i;

   if ( DisableTimer () == -1)
      return -1;
/*
   Clear new and old buffer pointers.
*/
   clear_receive_buffer();
   put_serial_string("RWR");
   put_serial_char(0x17);                  // bank 1 , nibbles 8 
   put_serial_char(00);                    // start address 00 
   for(i = 0; i < 4;++i)
       put_serial_char(00);
   put_serial_char(0x0d);

   if ( get_acknowledge () == 0)
      {
      EnableTimer ();
      return -1;
      }
/*
   Do read command to set data lines in input state.   So P67InOutFlag is in
correct state.
*/
   put_serial_string ("SRD");
   send_unsigned (0);          // Arbitrarily read address 0.
   send_unsigned (0);          // Go for 1 byte.
   put_serial_char (0x0d);

   if ( get_acknowledge () == 0)
      {
      EnableTimer ();
      return -1;
      }

   crc_fill_buffer (1+2);      // Data, CRC code.

   clear_receive_buffer();

/*
   Set the WrapFlag, P67InOutFlag, and TimeOutFlag to 0.
*/
   put_serial_string("RWR");
   put_serial_char(0x00);
   put_serial_char(0x42);
   put_serial_char(0x0);    
   put_serial_char(0x0d);

   if ( get_acknowledge () == 0)
      {
      EnableTimer ();
      return -1;
      }

   if (SynchArchive () == -1)
      {
      EnableTimer ();
      return -1;
      }
      
   if ( EnableTimer () == -1 )
      return -1;

   return 0;
}


/*
+----------------------------------------------------------------------------+
*/
int SetStationTime (int month, int day, int hour, int min)
{
    int sec = 0;

    clear_receive_buffer ();
  
/*
   Update the weather station date.
*/
    put_serial_string("WWR");
    put_serial_char(0x43);
    put_serial_char(0xc8);
    receive_buffer[1] = (unsigned char)(month);
    receive_buffer[0] = (unsigned char)(((day/10)*16)+(day%10));
    put_serial_char(receive_buffer[0]);
    put_serial_char(receive_buffer[1]);
    put_serial_char(0x0d);

    if ( get_acknowledge () == 0)
       return -1;
/*
   Update the weather station time.
*/

   if ( DisableTimer () == -1)
      return -1;

    receive_buffer[0] = (unsigned char)(((hour/10)*16)+(hour%10));
    receive_buffer[1] = (unsigned char)(((min/10)*16)+(min%10));
    receive_buffer[2] = (unsigned char)(((sec/10)*16)+(sec%10));
    put_serial_string("WWR");
    put_serial_char(0x63);  
    put_serial_char(0xbe);
    put_serial_char(receive_buffer[0]);
    put_serial_char(receive_buffer[1]);
    put_serial_char(receive_buffer[2]);
    put_serial_char(0x0d);

    if ( get_acknowledge () == 0)
       {
       EnableTimer ();
       return -1;
       }

/*
   Set last archive time so next archive entry's will occur on the hour.
*/
    if ( PutLastArchiveTime (hour, min) == -1)
       {
       EnableTimer ();
       return -1;
       }


    if ( EnableTimer () == -1 )
       return -1;

    return 0;
}
