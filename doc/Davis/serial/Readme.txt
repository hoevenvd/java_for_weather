Sample Code and Programming Reference

Updated: January, 22, 1999

Documentation is provided free of charge and "as is." Davis Instruments will provide support
 via e-mail at support@davisnet.com. We will not answer questions over the phone. If you 
have a question, please consult the files contained in this package, especially the 
frequently asked questions in the file "faq.txt". If you cannot find answers to your 
question in "faq.txt," check the web site for an updated version. If you still cannot 
find answer(s), you may submit your questions by e-mail. Please explain in as much 
detail as you can what you are having difficulty with and if possible include a copy
 of the source code section that is in question. We will do our best to answer your 
questions within a week. If the questions you have are lengthy or complicated, Davis
 Instruments is available for PAID support in one hour segments at $100.00 per hour. 

This documentation provides the information and materials needed to write custom 
applications for Davis weather stations and the WeatherLink. The current version 
of this documentation supports all of the weather stations manufactured by Davis as of 
1/98 (these are: the Monitor II, Wizard II & III, Perception II, GroWeather, Energy, and
 Health stations). 

Documentation is provided in both MS-Word 6.0 (.doc) and ASCII (.txt) file formats. The
 information contained in one format of a particular file is identical to the information 
in the other format. Please read the Technical Reference ("techref.txt" or "techref.doc")
 file before doing any programming. The Technical Reference describes each command the 
WeatherLink data logger understands, which are the same commands our WeatherLink Software 
uses. It also contains many tips and examples of how to use the commands effectively. 
Included are a series of "C" and Basic source code examples using the WeatherLink commands.
 We've included source code we have used to exercise the interface. With careful study it
 should answer questions the technical reference does not. The included source code makes
 calls to the commercial package "C ASYNCH MANAGER 5.0", and can not be used to make an 
.exe file without this library. 

If you are programming in BASIC, look at the Visual Basic code (16 bit) in "comm.bas" 
which illustrates how to use the "LOOP" command. We suggest if you are after real-time
 weather data that you use the "LOOP" command.

The commands outlined are a combination of ASCII and binary data, can be sent out any
 RS232 serial port and used to extract from the weather station any data that the station
 displays (current conditions, highs, lows, etc.) as well as archive data from the
 WeatherLink.

NOTE: Davis Instruments is not responsible for any damages resulting from use or misuse 
of the information on this disk. 


--------------------------------------------------------------------------------

What will this disk do for me? 

This library contains the necessary information to enable a programmer to write custom
 programs using Davis weather stations and the WeatherLink. This consists of libraries
 documenting commands, source code example.


--------------------------------------------------------------------------------

What does this disk contain? 

readme.htm - This file contains an introduction and explanation of the contents and organization of the Serial Communications Reference. The contents are the same as the contents of this page. 

techref.txt - This file contains a technical description of the RS232 interface. It describes the primitive commands upon which all higher level functionality must be built. It also contains may examples of "C" code fragments to illustrate how the commands are used. In addition, this file contains tables of the station and link memory addresses. techref.doc - MS Word version of techref.txt 

appendix.txt - This file contains descriptions of coded numerical and bit-mapped values. appendix.doc - MS Word version of appendix.txt 

faq.txt - This file contains short answers to may commonly asked questions. faq.doc - MS Word version of faq.txt 

database.txt - This file contains a description of the database file formats created by the PC Link, GroWeather WinLink, Energy WinLink, and Health WinLink software sold by Davis Instruments. database.doc - MS Word version of database.txt 

commands.c - This source was included to give you a source code reference of the commands in action. 

serial.c - Example "C" interface to the chip. 

serial.h - Header file for serial.c functions. 

ascii.c - Converts a binary weather data file to ascii form. 

ccitt.h - Tables used for the CRC checksum calculation. 

thitable.h - A table for calculating Temperature- Humidity Index from temperature and humidity data. 

For Visual Basic Programmer's look at the source code in COMM.BAS which illustrates how to use the LOOP command and decode the data packet sent back from the weather station. 

COMM.BAS - BASIC source code using loop command. Visual Basic programmers should look at the source code in COMM.BAS which illustrates how to use the LOOP command and how to decode the data packet sent back from the weather station.

CRC.DAT - Data file with tables for CRC calculation. 
FORM1.FRM - Project file. 
FORM1.FRX - Project file. 
VB_LINK.MAK - Make file for VB_LINK.EXE. 
VB_LINK.EXE - Compiled version of COMM.BAS. 