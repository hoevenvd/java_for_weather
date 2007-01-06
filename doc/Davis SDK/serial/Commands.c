/*
  Program to test the rs232 firmware.
*/

#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <dos.h>
#include <time.h>
#include <math.h>

extern unsigned bptr;
extern unsigned char *receive_buffer;

void show_arc_entry ();
void initialize_serial_port (unsigned char params);
unsigned char get_serial_status ();
int put_serial_charnw (unsigned char c);
int put_serial_char (unsigned char c);
int send_unsigned (unsigned);
int fill_buffer (unsigned);
void crc_accum (unsigned char data);
FILE *outfile;
char line [80], command [25];

unsigned char Wsp, Hin, Hout, uch;
int Tin, Tout, Wdir, Bar;
long Rain, tmp;

int com_port = 1;                      // Default of com 2.

char AMPM [5];
int hour, min,sec, month, day;
int start_row = 8, start_col = 30;
int tinaccum, toutaccum, wspaccum, wndbin [16];

unsigned a, b, c, d, crc_error=0, soh_error=0;
unsigned long accum=0;

main (int argc, char *argv [])
{
   unsigned i;
   unsigned n;
   int ch, error;
   unsigned char *bp, *ap, *cp, *ucptr;
   unsigned int *pchar;
   float aper;
   time_t ltime;
   struct tm *today;

   textmode (BW80);

   bp = (unsigned char *) &b;
   ap = (unsigned char *) &a;
   cp = (unsigned char *) &c;

   if (get_buffer () == -1)
      exit (0);

   if ( strcmp (argv [1], "1200") == 0 )
      initialize_serial_port (0x83);
   else
/*
  2400 baud, 8 data bits, no parity, one stop bit...
*/
      initialize_serial_port (0xa3);


   if ( argc == 3 )
      com_port = atoi (argv [2]) - 1;

//    put_serial_charnw (0x44);
    
//    printf ("'%c'\n", get_serial_charnw ());


    printf ("CTRL-BRK to exit.\n");
    printf ("%% ");
    while (1)
       {
       if (kbhit () == 0)
          continue;
       
       gets (line);
       if ( strncmp (line, "fill", 2) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("FILL");
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }
       else if ( strncmp (line, "loop", 4) == 0 )
          {
          put_serial_string ("LOOP");
          a =  5;                           // Default 5 times.
          sscanf (line, "%s %u", command, &a);
          send_unsigned ((unsigned) (65536 - a));
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());

          clrscr ();
          crc_error = soh_error = 0;
          _setcursortype (_NOCURSOR);
          for ( i = 0; i < a; i++)
             {
             if (kbhit () != 0)
                {
                fflush (stdin);
                break;
                }
             error = loop_fill (17);
             if ( error == -2 )              // crc error...
                crc_error++;
             else if (error == -3)           // soh error..
                soh_error++;

//             window (start_row-2, start_col, start_row+ 8, start_col + 30);
             gotoxy (start_col, start_row-1);
             cprintf ("Sensor image....%d", i+1);

             gotoxy (start_col, start_row);
             cprintf ("Tin = %5.2f\n",  *((int *)(receive_buffer+0))/10.0 );
             gotoxy (start_col, start_row+1);
             cprintf ("Tout = %5.2f\n",  *((int *)(receive_buffer+2))/10.0 );
             
             gotoxy (start_col, start_row+2);
             cprintf ("Wsp = %3u\n",  *((unsigned char *)(receive_buffer+4)) );
             if ( (i % 2) == 0)
                {
                gotoxy (start_col, start_row+3);
                cprintf ("WDir  = %5u\n",  *((int *)(receive_buffer+5)) );
                }
             gotoxy (start_col, start_row+4);
             cprintf ("Barometer = %5.2f\n",  *((int *)(receive_buffer+7))/1000.0 );
             gotoxy (start_col, start_row+5);
             cprintf ("Hin = %3u\n",  *((unsigned char *)(receive_buffer+9)) );
             gotoxy (start_col, start_row+6);
             cprintf ("Hout = %3u\n",  *((unsigned char *)(receive_buffer+10)) );
             gotoxy (start_col, start_row+7);
             cprintf ("Rain = %ld\n",  *((long *)(receive_buffer+11)) );

             if ( crc_error > 0)
                {
                gotoxy (start_col, start_row+8);
                cprintf ("%u %u\n", crc_error, soh_error);
                }
             }

          _setcursortype (_NORMALCURSOR);
          clrscr ();
          }
       else if ( strncmp (line, "sper", 4) == 0 )
          {
          printf ("Sample period? (sec) "); 
          scanf ("%u", &a);
          put_serial_string ("SSP");
          put_serial_char ((unsigned char) (256 - a));              
          put_serial_char (0xd);              
          printf ("%x\n", get_serial_char ());
          fflush (stdin);
          }
       else if ( strncmp (line, "arcper", 6) == 0 )
          {
          printf ("Archive period? (min) "); 
          scanf ("%d", &a);
          put_serial_string ("SAP");
          put_serial_char ((unsigned char) a);              
          put_serial_char (0xd);              
          printf ("%x\n", get_serial_char ());
          fflush (stdin);
          }
       else if ( strncmp (line, "periods", 3) == 0 )

          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (1);              
          put_serial_char (0x3a);              
          put_serial_char (3);              // Get 4 nibbles.
          put_serial_char (0x0d);
          fill_buffer (3);
             if ( receive_buffer [0] != 6 )
            printf ("Ack was not received. Got %x\n", receive_buffer [0]);

          printf ("Sample Per = %u (sec)\nArchive Per = %u (min)\n",
                          (256 - *((receive_buffer+1))),
                         *(receive_buffer+2) );

          }
       else if ( strncmp (line, "timers", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (0);                      // Read bank 0.           
          put_serial_char (0x08);                   // Address.                 
          put_serial_char (1);                      // # nibbles - 1.
          put_serial_char (0x0d);
          fill_buffer (2);
          if ( receive_buffer [0] != 6 )
            printf ("Ack was not received. Got %x\n", receive_buffer [0]);

          printf ("Sample timer = %6.2f (min) or %d (sec)\n",
                                 (256 -  *((receive_buffer+1)))/60.0, 
                                            256 - *((receive_buffer+1)) );

          put_serial_string ("RRD");
          put_serial_char (1);              
          put_serial_char (0x48);              
          put_serial_char (3);              
          put_serial_char (0x0d);
          fill_buffer (3);
          if ( receive_buffer [0] != 6 )
            printf ("Ack was not received. Got %x\n", receive_buffer [0]);

          printf ("Last Archive %2d:%2d\n", *((unsigned *)(receive_buffer+1))/60,
                                *((unsigned *)(receive_buffer+1))%60 );
          }
       else if ( strncmp (line, "ramchk", 6) == 0 )
          {
          put_serial_string ("RRD");
          put_serial_char (0);              
          put_serial_char (0xe);              
          put_serial_char (0);               // Read one nibble.
          put_serial_char (0x0d);

          if ( (ch = get_serial_char ()) != 6)
             {
             printf ("ACK not received.  Got %x\n", ch);
             }
          else
             {
             fill_buffer (1);
             printf ("Flag %x\n", *(receive_buffer) );
             }
          }
       else if ( strncmp (line, "ebt", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("EBT");
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }
       else if ( strncmp (line, "dbt", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("DBT");
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }
       else if ( strncmp (line, "read", 4) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("START");
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }
       else if ( strncmp (line, "stop", 4) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("STOP");
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }
       else if ( strncmp (line, "sbd", 3) == 0 )
          {
          clear_receive_buffer ();
          sscanf (line, "%s %u", command, &a);
          if ( a == 300 )
             {
             put_serial_string ("SBD");
             put_serial_char (0x6c);           // Send value for TM0.
             put_serial_char (73);             // Send value for TMOD0.
             put_serial_char (0x0d);
             printf ("%x\n", get_serial_char ());
             initialize_serial_port (0x43);
             }          
          else if (a == 1200)
             {
             put_serial_string ("SBD");
             put_serial_char (0x7c);           // Send value for TM0.
             put_serial_char (73);             // Send value for TMOD0.
             put_serial_char (0x0d);
             printf ("%x\n", get_serial_char ());
             initialize_serial_port (0x83);
             }          
          else if (a == 2400)
             {
             put_serial_string ("SBD");
             put_serial_char (0x7c);           // Send value for TM0.
             put_serial_char (36);             // Send value for TMOD0.
             put_serial_char (0x0d);
             printf ("%x\n", get_serial_char ());
             initialize_serial_port (0xa3);
             }          
          else if (a == 4800)
             {
             put_serial_string ("SBD");
             put_serial_char (0x7c);           // Send value for TM0.
             put_serial_char (18);             // Send value for TMOD0.
             put_serial_char (0x0d);
             printf ("%x\n", get_serial_char ());
             initialize_serial_port (0xc3);
             }          
          else 
             printf ("Baud rate %d not available.\n", a);

          }
       else if ( strncmp (line, "sensor", 6) == 0 )
          {
          clear_receive_buffer ();
          if ( strlen (line) == 6)
             a = 1;
          else
             sscanf (line, "%s %u", command, &a);

          for (i = 0; i < a; i++)
             {
             if (kbhit () > 0)
                {
                fflush (stdin);
                break;
                }
             printf ("Sending IMG command...%u\n", i);
             put_serial_string ("IMG");
             put_serial_char (0x0d);
             
             if ( (c = get_serial_char ()) != 6 )
                printf ("Ack was not received on cmd %u. Got %x\n", i+1,c);
             }
          }
       else if ( strncmp (line, "ptrs", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (1);
          put_serial_char (0);
          put_serial_char (7);
          put_serial_char (0x0d);
          fill_buffer (5);
          printf ("Entrys = %d\n", *((int *)(receive_buffer+1))/21);
          printf ("New Pointer = %x (%d)\n", *((int *)(receive_buffer+1)),
                                               *((int *)(receive_buffer+1)) );
          printf ("Old Pointer = %x (%d)\n", *((int *)(receive_buffer+3)),
                                               *((int *)(receive_buffer+3)) );
          }
       else if ( strncmp (line, "peek", 4) == 0 )
          {
          clear_receive_buffer ();
          a = 0;
          sscanf (line, "%s %u", command, &a);
          put_serial_string ("SRD");
          send_unsigned (a);
          send_unsigned (21-1);
          put_serial_char (0x0d);
          crc_fill_buffer (24);
          show_arc_entry ();
          }
       else if ( strncmp (line, "arcrd", 5) == 0 )
          {
          a = 0;                               
          clear_receive_buffer ();
          sscanf (line, "%s %d", command, &a);
          a = (a > 0) ? (a) : (1);
          put_serial_string ("SRD");
          send_unsigned (21 * (a-1));
          send_unsigned (21-1);
          put_serial_char (0x0d);
          crc_fill_buffer (24);
          show_arc_entry ();
          }
       else if ( strncmp (line, "samp", 4) == 0 )
          {
          clear_receive_buffer ();
          if ( strlen (line) == 4)
             a = 1;
          else
             sscanf (line, "%s %u", command, &a);

          for (i = 0; i < a; i++)
             {
             if (kbhit () > 0)
                {
                fflush (stdin);
                break;
                }
             printf ("Sampling...%u\n", i+1);
             put_serial_string ("SAMP");
             put_serial_char (0x0d);
             
             if ( (c = get_serial_char ()) != 6 )
                printf ("Ack was not received on cmd %u. Got %x\n", i+1,c);
             }
          }
       else if ( strncmp (line, "calc", 4) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("CALC");
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }
       else if ( strncmp (line, "arc", 3) == 0 )
          {
          clear_receive_buffer ();

          if ( strlen (line) == 3)
             a = 1;
          else
             sscanf (line, "%s %u", command, &a);

          for (i = 0; i < a; i++)
             {
             if (kbhit () > 0)
                {
                fflush (stdin);
                break;
                }
             printf ("Archiving %u......\n", i+1);
             put_serial_string ("ARC");
             put_serial_char (0x0d);
             if ( get_serial_char () != 6 )
                printf ("Ack was not received on cmd %u.\n", i+1);
             }
          }
       else if ( strncmp (line, "sramd", 5) == 0 )
          {
          clear_receive_buffer ();
          sscanf (line, "%s %x %x", command, &a, &b);
          put_serial_string ("SRD");
          send_unsigned (a);
          send_unsigned (b-1);
          put_serial_char (0x0d);

//  Ask for N-1 to get N on Ram read commands.
          crc_fill_buffer (b+1+2);              // Get 6, Data..., CRC code.
          ucptr = (unsigned char *) &i;
          n = 0;
          for (i = 1; i < bptr-2; i++)
             {
             if ( (unsigned char) ((*ucptr-1)+ *ap) != receive_buffer [i] )
                n++;
             printf ("%x ? %x\n", 
                        (unsigned char) (*ucptr-1 + *ap), receive_buffer [i]);
             }
          printf ("Errors %u\n", n);
          }
       else if ( strncmp (line, "sram", 4) == 0 )
          {
          clear_receive_buffer ();
          sscanf (line, "%s %x %x", command, &a, &b);
          put_serial_string ("SRD");
          send_unsigned (a);
          send_unsigned (b-1);
          printf ("Checking ram from %x to %x...\n", a, a+b-1);
          put_serial_char (0x0d);

//  Ask for N-1 to get N on Ram read commands.

          crc_fill_buffer (b+1+2);
          ucptr = (unsigned char *) &i;
          n = 0;
          for (i = 1; i < bptr-2; i++)
             {
             if ( (unsigned char) ((*ucptr-1)+ *ap) != receive_buffer [i] )
                n++;
             }
          printf ("Errors %u\n", n);
          }
       else if ( strncmp (line, "ai", 2) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (0x1);              
          put_serial_char (136);              
          put_serial_char (41);              
          put_serial_char (0x0d);
          fill_buffer (22);
// Archive image.....
          printf ("Barometer = %7.3f\n",  *((int *)(receive_buffer+1))/1000.0 );
          printf ("Hin = %u\n",  *((unsigned char *)(receive_buffer+3)) );
          printf ("Hout = %u\n",  *((unsigned char *)(receive_buffer+4)) );
          printf ("Rain = %u\n",  *((unsigned *)(receive_buffer+5)) );
          printf ("TinAvg = %5.2f\n",  *((int *)(receive_buffer+7))/10.0 );
          printf ("ToutAvg = %5.2f\n",  *((int *)(receive_buffer+9))/10.0 );
          printf ("Wsp Avg = %u\n",  *((unsigned char *)(receive_buffer+11)) );
          printf ("WDir  = %u\n",  *((unsigned char *)(receive_buffer+12)) );
          printf ("THiOut = %5.2f\n",  *((int *)(receive_buffer+13))/10.0 );
          printf ("Gust  = %u\n",  *((unsigned char *)(receive_buffer+15)) );
          strcpy (AMPM, "am");
          sprintf (line, "%x", *((receive_buffer+16))  );
          hour = atoi (line);                 // Get hour..
          if ( hour > 12 )                    // Adjust 24 hr format.     
             {
             strcpy (AMPM, "pm");
             hour = hour - 12;
             }
          sprintf (line, "%x", *((receive_buffer+17))  );
          min = atoi (line);                 // Get minutes...
          
          sprintf (line, "%x", *((receive_buffer+18))  );
          day = atoi (line);                 // Get day...

          month =  *((receive_buffer+19)) & 0xf;

          printf ("Time %d/%d %d:%d %s\n", month, day, hour, min, AMPM);
          printf ("TLowOut = %5.2f\n",  *((int *)(receive_buffer+20))/10.0 );
          }
       else if ( strncmp (line, "siwr", 4) == 0 )
          {
          clear_receive_buffer ();
// Write to sensor image.....
          printf ("Tin? "); 
          scanf ("%d", &Tin);
          printf ("Tout? "); 
          scanf ("%d", &Tout);
          printf ("Wsp? "); 
          scanf ("%d", &Wsp);
          printf ("Wdir? "); 
          scanf ("%d", &Wdir);
          printf ("Barometer? "); 
          scanf ("%d", &Bar);
          printf ("Hin? "); 
          scanf ("%d", &Hin);
          printf ("Hout? "); 
          scanf ("%d", &Hout);
          printf ("Rain? "); 
          scanf ("%ld", &Rain);

// Send the 15 bytes using 2 rwr commands.
          put_serial_string ("RWR");
          put_serial_char (0x1d);
          put_serial_char (0x1c);
          send_unsigned ((unsigned) Tin);
          send_unsigned ((unsigned) Tout);
          put_serial_char (Wsp);
          send_unsigned ((unsigned) Wdir );
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());

          put_serial_string ("RWR");
          put_serial_char (0x1f);
          put_serial_char (42);
          send_unsigned ((unsigned) Bar );
          put_serial_char (Hin);
          put_serial_char (Hout);

          pchar = (unsigned int *) &Rain;
          send_unsigned ((unsigned) (*pchar) );
          send_unsigned ((unsigned) (*(pchar+1)) );
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          fflush (stdin);
          }
       else if ( strncmp (line, "si", 2) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (0x1);              
          put_serial_char (0x1c);              
          put_serial_char (29);              
          put_serial_char (0x0d);
          if ( (ch = get_serial_char ()) != 6)
             {
             printf ("ACK not received.  Got %x\n", ch);
             }
          else
             {
             fill_buffer ( 15);
// Sensor image.....
             printf ("Tin = %5.2f\n",  *((int *)(receive_buffer))/10.0 );
             printf ("Tout = %5.2f\n",  *((int *)(receive_buffer+2))/10.0 );
             printf ("Wsp = %u\n",  *((unsigned char *)(receive_buffer+4)) );
             printf ("WDir  = %u\n",  *((int *)(receive_buffer+5)) );
             printf ("Barometer = %7.3f\n",  *((int *)(receive_buffer+7))/1000.0 );
             printf ("Hin = %u\n",  *((unsigned char *)(receive_buffer+9)) );
             printf ("Hout = %u\n",  *((unsigned char *)(receive_buffer+10)) );
             printf ("Rain = %ld\n",  *((long *)(receive_buffer+11)) );
             }
          }
       else if ( strncmp (line, "accum", 5) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (0x1);              
          put_serial_char (80);              
          put_serial_char (47);              
          put_serial_char (0x0d);
          fill_buffer ( 25);
// Accumulator image.....
         tmp = 
         (*((long *)(receive_buffer+1)) & 0x000fffff);
         if ((tmp & 0x00080000) > 0)                   // Sign extend....
            {
            tmp = tmp | 0xfff80000;
            }

          printf ("TinAccum = %5.2f\n", tmp / 10.0);

          uch = *(receive_buffer + 3) >> 4;
          accum = *((unsigned *) (receive_buffer + 4));
          accum = (accum << 4);
          tmp = accum = uch | accum;          // Set lower 4 bits of accum.
          if ((tmp & 0x00080000) > 0)                   // Sign extend....
             {
             tmp = tmp | 0xfff80000;
             }
          
          printf ("ToutAccum = %5.2f\n", tmp / 10.0 );
          printf ("WspAccum = %u\n",  *((unsigned *)(receive_buffer+6)) );
          for (i = 0; i < 16; i++)
             printf ("Wind Bin %d = %d\n",
                                i, *((unsigned char *)(receive_buffer+8+i)) );
          printf ("Samples = %u\n",  *((unsigned char *)(receive_buffer+8+i)) );
          }
       else if ( strncmp (line, "tin", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x44);              // Send the command.
          put_serial_char (0x30);              // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 3);
          printf ("Tin = %5.2f\n",  *((int *)(receive_buffer+1))/10.0 );
          }
       else if ( strncmp (line, "tout", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x44);              // Send the command.
          put_serial_char (0x56);              // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 3);
          printf ("Tout = %5.2f\n",  *((int *)(receive_buffer+1))/10.0 );
          }
       else if ( strncmp (line, "hin", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x24);              // Send the command.
          put_serial_char (0x80);              // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 2);
          printf ("Hin = %u\n",  *(receive_buffer+1) );
          }
       else if ( strncmp (line, "hout", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x24);              // Send the command.
          put_serial_char (0x98);              // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 2);
          printf ("Hout = %u\n",  *(receive_buffer+1) );
          }
       else if ( strncmp (line, "bar", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x44);              // Send the command.
          put_serial_char (0x0);               // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 3);
          if ( receive_buffer [0] != 6 )
             printf ("Sorry ugh....%x\n", receive_buffer [0]);
          else
             printf ("Barometer = %7.3f\n",  *((int *)(receive_buffer+1))/1000.0 );
          }
       else if ( strncmp (line, "dew", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x42);               // Send the command.
          put_serial_char (0x8a);               // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 3);
          if ( receive_buffer [0] != 6 )
             printf ("Sorry ugh....%x\n", receive_buffer [0]);
          else
             printf ("Dew Pt. = %5.2f\n",  *((int *)(receive_buffer+1))/10.0 );
          }
       else if ( strncmp (line, "wsp", 3) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x22);              // Send the command.
          put_serial_char (0x5e);               // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 2);
          printf ("Wind Speed = %d\n",  *((char *)(receive_buffer+1)) );
          }
       else if ( strncmp (line, "chill", 4) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x42);               // Send the command.
          put_serial_char (0xac);               // Send the address.
          put_serial_char (0x0d);
          fill_buffer (3);
          if ( receive_buffer [0] != 6 )
             printf ("No ACK received..%x\n", receive_buffer [0]);
          else
             printf ("Wind Chill = %5.2f\n",  *((int *)(receive_buffer+1))/10.0 );
          }
       else if ( strncmp (line, "wdir", 4) == 0 )
          {
          clear_receive_buffer ();
          put_serial_string ("WRD");
          put_serial_char (0x44);              // Send the command.
          put_serial_char (0xb4);              // Send the address.
          put_serial_char (0x0d);
          fill_buffer ( 3);
          printf ("Wind Dir = %d\n",  *((int *)(receive_buffer+1)) );
          }
       else if ( strncmp (line, "rrd", 3) == 0 )
          {
          clear_receive_buffer ();
          sscanf (line, "%s %x %x %d", command, &a, &b, &c);
          put_serial_string ("RRD");
          put_serial_char (*(ap));
          put_serial_char (*(bp));
          put_serial_char ((unsigned char) (*(cp)-1));
          put_serial_char (0x0d);

          n = ceil (c/2.0);
          fill_buffer (n + 1);

          if ( receive_buffer [0] != 6 )
            printf ("Ack was not received. Got %x\n", receive_buffer [0]);

          for (i = 1; i < bptr; i++)
             {
             a = receive_buffer [i];
             printf ("%x ", *ap & 0x0f);
             printf ("%x ", *ap >> 4); 
             if ( (i) % 8 == 0 )
                printf ("\n");
             }
          }
       else if ( strncmp (line, "rwr", 3) == 0 )
          {
          clear_receive_buffer ();
          sscanf (line, "%s %x %x", command, &a, &b);
          put_serial_string ("RWR");
          put_serial_char (*(ap));
          put_serial_char (*(bp));
          c = ((*ap) & 0x0f) + 1;
          n = (c + 1) / 2;                         // Number of bytes to get.
          printf ("Enter hex bytes to send?\n");
          fflush (stdin);
          for ( i = 0; i < n; i++)
             {
             printf ("Byte %d ? ", i);
             gets (line);
             sscanf (line, "%x", &c);
             if ( c == 0x13 )
                break;
             receive_buffer [i] = (unsigned char) c;
             }
          for ( i = 0; i < n; i++)
             put_serial_char (receive_buffer [i]);
//             printf ("Sending %x\n", receive_buffer [i]);

          put_serial_char (0x0d);
          
        
          printf ("%x\n", get_serial_char ());

          }
       else if ( strncmp (line, "srd", 3) == 0 )
          {
          clear_receive_buffer ();
          sscanf (line, "%s %x %x", command, &b, &a);
          put_serial_string ("SRD");
          send_unsigned (b);
          send_unsigned (a-1);  
          put_serial_char (0x0d);
/*
  Ask for N-1 to get N on Ram read commands.
*/
          crc_fill_buffer ( (a+1) + 2); 

          for (i = 0; i < bptr-2; i++)
             printf ("%x\n", receive_buffer [i]);
          }
       else if ( strncmp (line, "swr", 3) == 0 )
          {
          sscanf (line, "%s %x %x", command, &b, &a);
          put_serial_string ("SWR");
          send_unsigned (b);
          put_serial_char (*(ap));
          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }

       else if ( strncmp (line, "wrd", 3) == 0 )
          {
          sscanf (line, "%s %x %x", command, &b, &a);
          put_serial_string ("WRD");
          put_serial_char (*(bp));
          put_serial_char (*(ap));
          put_serial_char (0x0d);
          n = (((*bp) >> 4) + 1) / 2;
          fill_buffer ( (n + 1));
          for (i = 0; i < bptr; i++)
             printf ("%x\n", receive_buffer [i]);
          }
       else if ( strncmp (line, "wwr", 3) == 0 )
          {
          sscanf (line, "%s %x %x", command, &b, &a);
          put_serial_string ("WWR");
          put_serial_char (*(bp));
          put_serial_char (*(ap));

          c = ((*bp) >> 4)  + 1;
          n = (c) / 2;                         // Number of bytes to get.
          printf ("Enter hex bytes to send?\n");
          fflush (stdin);
          for ( i = 0; i < n; i++)
             {
             printf ("Byte %d ? ", i);
             gets (line);
             sscanf (line, "%x", &c);
             if ( c == 0x13 )
                break;
             receive_buffer [i] = (unsigned char) c;
             }
          for ( i = 0; i < n; i++)
             put_serial_char (receive_buffer [i]);
//             printf ("Sending %x\n", receive_buffer [i]);


          put_serial_char (0x0d);
          printf ("%x\n", get_serial_char ());
          }

       else if (line [0] == '?')
          {
          show_cmds ();
          }
      else if (strncmp(line,"dmp",3)==0)
          {
          clear_receive_buffer ();
          put_serial_string ("RRD");
          put_serial_char (0);              
          put_serial_char (0x42);           // Read nibble with wrap flag.   
          put_serial_char (0);              // Get 1 nibble.
          put_serial_char (0x0d);
          fill_buffer (2);
          if ( receive_buffer [0] != 6 )
            printf ("Wrap flag read failed.  Sorry no dump.....\n");
          else
            {
            if ( (receive_buffer [1] & 0x1) > 0 )    // Is wrap flag set?
               {
               printf ("Buffer has wrapped.  Dumping all entrys...\n");
               a = 32767;
               }
            else
               {
/*
  Find out how many entrys to read...
*/
               clear_receive_buffer ();
               put_serial_string ("RRD");
               put_serial_char (1);
               put_serial_char (0);
               put_serial_char (7);
               put_serial_char (0x0d);
               fill_buffer (5);

               printf ("Dumping %d entrys...\n",
                                             *((int *)(receive_buffer+1))/21);

               a= *((int *)(receive_buffer+1)); /* end address newptr */
               }

            clear_receive_buffer ();
     	      put_serial_string ("SRD");
     	      send_unsigned (0);
     	      send_unsigned (a-1);
     	      put_serial_char (0x0d);
/*
  Ask for N-1 to get N on Ram read commands.
*/
            crc_fill_buffer ( (a+1) + 2); 
            outfile=fopen("DMPFILE.DMP","wb");
            for (i = 1; i < bptr-2; i++)
                {
                fprintf (outfile,"%c", receive_buffer [i]);
                }
            fflush(outfile);
            fclose(outfile);
            }  // End if wrap flag read command worked...
         }
       else if (strcmp(line,"reset",5) == 0)
               {
                 clear_receive_buffer();
                 put_serial_string("RWR");
                 put_serial_char(0x17);  /* bank 1 , nibbles 8 */
                 put_serial_char(00);  /* start address 00 */
                 for(i = 0; i < 4;++i)
                     put_serial_char(00);
                 put_serial_char(0x0d);
                 printf("%x\n",get_serial_char());
/*
   Do read command to set data lines in input state.
*/
                 put_serial_string ("SRD");
                 send_unsigned (0);          // Arbitrarily read address 0.
                 send_unsigned (0);          // Go for 1 byte.
                 put_serial_char (0x0d);
                 crc_fill_buffer (1+1+2);    // Get ACK, Data, CRC code.

                 clear_receive_buffer();
                 put_serial_string("RWR");
                 put_serial_char(0x00);
                 put_serial_char(0x42);
/*
   Set the WrapFlag, P67InOutFlag, and TimeOutFlag to 0.
*/
                 put_serial_char(0x0);    
                 put_serial_char(0x0d);
                 printf("%x\n",get_serial_char());


               }
       else  if(strncmp (line, "settime",7) == 0)
            {

             printf ("Setting your weather buddy time to your PC time...\n");
             time(&ltime);
             today = localtime(&ltime);
             hour = (today->tm_hour);
             min = (today->tm_min);
             sec = (today->tm_sec);
             receive_buffer[0] = (unsigned char)(((hour/10)*16)+(hour%10));
             receive_buffer[1] = (unsigned char)(((min/10)*16)+(min%10));
             receive_buffer[2] = (unsigned char)(((sec/10)*16)+(sec%10));
             put_serial_string("WWR");
             put_serial_char(0x63);  /* six nibbles, write to bank 1 */
             put_serial_char(0xbe);
             put_serial_char(receive_buffer[0]);
             put_serial_char(receive_buffer[1]);
             put_serial_char(receive_buffer[2]);
             put_serial_char(0x0d);
             printf("%x\n",get_serial_char());
             month = (today->tm_mon);

             day   = (today->tm_mday);
             put_serial_string("WWR");
             put_serial_char(0x43);
             put_serial_char(0xc8);
             receive_buffer[1] = (unsigned char)(month+1);
             receive_buffer[0] = (unsigned char)(((day/10)*16)+(day%10));
             put_serial_char(receive_buffer[0]);
             put_serial_char(receive_buffer[1]);
            put_serial_char(0x0d);
            printf("%x\n",get_serial_char());
            }
       else
          {
          printf ("Command is not here.....\n");
          }

       printf ("%% ");

       }  // End processing commands.

          
}

void show_arc_entry ()
{

// Archive Entry....
          printf ("Barometer = %7.3f\n",  *((int *)(receive_buffer+1))/1000.0 );
          printf ("Hin = %u\n",  *((unsigned char *)(receive_buffer+3)) );
          printf ("Hout = %u\n",  *((unsigned char *)(receive_buffer+4)) );
          printf ("Rain = %u\n",  *((unsigned *)(receive_buffer+5)) );
          printf ("TinAvg = %5.2f\n",  *((int *)(receive_buffer+7))/10.0 );
          printf ("ToutAvg = %5.2f\n",  *((int *)(receive_buffer+9))/10.0 );
          printf ("Wsp Avg = %u\n",  *((unsigned char *)(receive_buffer+11)) );
          printf ("WDir  = %u\n",  *((unsigned char *)(receive_buffer+12)) );
          printf ("THiOut = %5.2f\n",  *((int *)(receive_buffer+13))/10.0 );
          printf ("Gust  = %u\n",  *((unsigned char *)(receive_buffer+15)) );
          strcpy (AMPM, "am");
          sprintf (line, "%x", *((receive_buffer+16))  );
          hour = atoi (line);                 // Get hour..
          if ( hour > 12 )                    // Adjust 24 hr format.     
             {
             strcpy (AMPM, "pm");
             hour = hour - 12;
             }
          sprintf (line, "%x", *((receive_buffer+17))  );
          min = atoi (line);                 // Get minutes...
          
          sprintf (line, "%x", *((receive_buffer+18))  );
          day = atoi (line);                 // Get day...

          month =  *((receive_buffer+19)) & 0xf;

          printf ("Time %d/%d %d:%d %s\n", month, day, hour, min, AMPM);
          printf ("TLowOut = %5.2f\n",  *((int *)(receive_buffer+20))/10.0 );
}











