/*
   This program converts weather data stored in it's raw form (as it
is in the SRAM) to ascii.  NOTE: Davis Instruments Weather Link
Software does not store the data in binary format, but a indexed
table.  You can dump the archive memory using Procomm (or your own routines),
and this will give you the data in binary form. 
*/

#include <stdio.h>
#include <stdlib.h>
#include <dos.h>
#include <io.h>
#include <time.h>
#include <math.h>

unsigned char entry [21];

FILE *fp;
char line [80];

main (int argc, char *argv [])
{
  int i, n, j;
  int month, day, hour, min;
  char AMPM [7];
  unsigned char *charp;

  if ( (fp = fopen (argv [1], "rb")) == NULL )
     {
     printf ("Can not find file '%s'\n", argv [1]);
     exit (0);
     }

  n = atoi (argv [2]);


  for (i = 0; i < n; i++)
    {
    if ( fill_entry () < 0 )
       break;

    strcpy (AMPM, "am");
    sprintf (line, "%x", *((entry+15))  );
    hour = atoi (line);                 // Get hour..
    if ( hour > 12 )                    // Adjust 24 hr format.     
       {
       strcpy (AMPM, "pm");
       hour = hour - 12;
       }
    else if (hour == 12)
       strcpy (AMPM, "pm");

    sprintf (line, "%x", *((entry+16))  );
    min = atoi (line);                 // Get minutes...
          
    sprintf (line, "%x", *((entry+17))  );
    day = atoi (line);                 // Get day...

    month =  *((entry+18)) & 0xf;

    printf ("\"%2d/%2d %2d:%2d %s\",", month, day, hour, min, AMPM);
    printf ("%7.1f,",  *((int *)(entry+12))/10.0 );
    printf ("%7.1f,",  *((int *)(entry+19))/10.0 );
    printf ("%7.1f,",  *((int *)(entry+6))/10.0 );
    printf ("%7.1f,",  *((int *)(entry+8))/10.0 );
    printf ("%7.3f,",  *((int *)(entry))/1000.0 );
    printf ("%4u,",  *((unsigned char *)(entry+2)) );
    printf ("%4u,",  *((unsigned char *)(entry+3)) );
    printf ("%4u,",  *((unsigned char *)(entry+4)) );
    printf ("%4u,",  *((unsigned char *)(entry+10)) );
    printf ("%5u,",  *((unsigned char *)(entry+11)) );
    printf ("%4u",  *((unsigned char *)(entry+14)) );

    printf ("\n");
    }

   fclose (fp);
}

int fill_entry ()
{
   int i, c;

   for (i = 0; i < 21; i++)
       if ( (c = getc (fp)) == EOF )
          return -1;
       else
          entry [i] = c;
         
   return 0;
}
