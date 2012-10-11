'This program code demonstrates the use of the "Loop" command to retrieve realtime
'temperature, humidity, rain, wind, and barometer data from the weatherLink.
'Full permission is granted to use any or all of this code in any application. This code 
'is supplied as is and was written and tested with VB 3.0 only.

Type CommStateDCB   'data type used with API SetCommState function
    Id          As String * 1   ' Port Id from OpenComm
    BaudRate    As Integer      ' Baud Rate
    ByteSize    As String * 1   ' Data Bit Size (4 to 8)
    Parity      As String * 1   ' Parity
    StopBits    As String * 1   ' Stop Bits
    RlsTimeOut  As Integer      ' Carrier Detect Time "CD"
    CtsTimeOut  As Integer      ' Clear-to-Send Time
    DsrTimeOut  As Integer      ' Data-Set-Ready Time
    bits1       As String * 1
    bits2       As String * 1
    XonChar     As String * 1   ' XON character
    XoffChar    As String * 1   ' XOFF character
    XonLim      As Integer      ' Min characters in buffer before XON is sent
    XoffLim     As Integer      ' Max characters in buffer before XOFF is send
    PeChar      As String * 1   ' Parity Error Character
    EofChar     As String * 1   ' EOF/EOD character
    EvtChar     As String * 1   ' Event character
    TxDelay     As Integer      ' Reserved/Not Used
End Type


Type comstat    'data type used with API GetCommError function
    Bits As String * 1
    cbinque As Integer    'number of bytes in comm receive queue
    cbOutQue As Integer   'number of bytes in comm transmit queue
End Type

Type loop_results  'data type to hold results of loop command
    tin As double
    tout As double
    wspd As Integer
    wdir As Integer
    bar As double
    hin As integer
    hout As integer
    rain As double
End Type

     'API function calls supporting serial port communication
Declare Function GetCommError% Lib "User" (ByVal nCid%, lpStat As comstat)
Declare Function OpenComm Lib "user" (ByVal a As String, ByVal b As Integer, ByVal c As Integer) As Integer
Declare Function CloseComm Lib "user" (ByVal a As Integer) As Integer
Declare Function WriteComm Lib "user" (ByVal a As Integer, ByVal b As String, ByVal c As Integer) As Integer
Declare Function ReadComm Lib "user" (ByVal a As Integer, ByVal b As Any, ByVal c As Integer) As Integer
Declare Function SetCommState Lib "user" (b As CommStateDCB) As Integer
Declare Function FlushComm Lib "user" (ByVal a As Integer, ByVal b As Integer) As Integer


	' API function OpenComm Error Numbers
 Const IE_BADID = -1      ' Invalid or unsupported id
 Const IE_OPEN = -2       ' Device Already Open
 Const IE_NOPEN = -3      ' Device Not Open
 Const IE_MEMORY = -4     ' Unable to allocate queues
 Const IE_DEFAULT = -5    ' Error in default parameters
 Const IE_HARDWARE = -10  ' Hardware Not Present
 Const IE_BYTESIZE = -11  ' Illegal Byte Size
 Const IE_BAUDRATE = -12  ' Unsupported BaudRate
 

'Program Variables
Global Comm_flag As Integer     'set true when comm port is open
Dim commhandle As Integer       'handle for opened comm port
Dim CommState As CommStateDCB   'comm port configuration
Dim apierror As Integer         'returned error value 
Dim crc_data(0 To 255) As Integer   'lookup table for CRC checking
Dim ldata As loop_results       'contains results of loop command
Dim i, j As Integer  'loop counters
Dim file_path$    ' drive and path to CRC.DAT

Sub Comm_Close ()
     'Used to close the active comm port

If Comm_flag Then  'test to see that comm port is open
    apierror = CloseComm(commhandle)
End If

End Sub

Sub Comm_ErrorCheck ()
    'Used to test whether a comm error occurred during the last comm API call 

If apierror < 0 Then
    responce% = MsgBox("Unknown Comm Error Detected -- Closing Current Application", 16, "Comm Port Error")
    Comm_Close
    End
End If

End Sub

Sub Comm_FlushBuf ()
   'Used to flush the contents of the comm buffers    
    
    apierror = FlushComm(commhandle, 0)
    Comm_ErrorCheck
    apierror = FlushComm(commhandle, 1)
    Comm_ErrorCheck
End Sub

Sub Comm_OpenError ()
    'Used to decode comm  open errors and put message on screen

If apierror < 0 Then    'test for API error
    Select Case apierror
    	Case -1
		msgbuf$ = "Invalid or unsupported id"
    	Case -2
		msgbuf$ = "Device Already Open"
    	Case -3
		msgbuf$ = "Device Not Open"
    	Case -4
		msgbuf$ = "Unable to allocate queues"
    	Case -5
		msgbuf$ = "Error in default parameters"
    	Case -10
		msgbuf$ = "Hardware Not Present"
    	Case -11
		msgbuf$ = "Illegal Byte Size"
	Case Is = -12
		msgbuf$ = "Unsupported BaudRate"
	Case Is < -12
		msgbuf$ = "Unkown Communication Error"
    End Select

    responce% = MsgBox(msgbuf$, 16, "Comm Port Error")
    Comm_Close
    End
End If
  
End Sub
   
Sub Comm_Start (port As String)
   'Used to open the comm port    
    
CommRBBuffer% = 64  'set buffer length of comm receive buffer
CommTBBuffer% = 64  'set buffer length of comm transmit buffer
    
apierror = OpenComm(port, CommRBBuffer%, CommTBBuffer%)
Comm_OpenError
Comm_flag = True   ' com port opened
commhandle = apierror   'if no error apierror returns handle of comm port
 
CommState.Id = Chr$(commhandle)
CommState.BaudRate = 2400
CommState.ByteSize = Chr$(8)
CommState.Parity = Chr$(0)
CommState.StopBits = Chr$(0)
CommState.bits1 = Chr$(1)
CommState.bits2 = Chr$(0)
CommState.RlsTimeOut = 0
CommState.CtsTimeOut = 0
CommState.DsrTimeOut = 0
CommState.EvtChar = Chr$(0)
	
apierror = SetCommState(CommState)
Comm_ErrorCheck  
    
End Sub

Function Crc_Calc (Loop_Data$) As Long
  'Used to calculate CRC value of data returned from "Loop" command

 accum& = 0
 
 For L = 1 To Len(Loop_Data$)
    accum_high& = (accum& And 65280) / 256
    COMB_VAL% = CInt(accum_high&) Xor Asc(Mid$(Loop_Data$, (L), 1))
    crc_tbl& = unsigned_int(crc_data(COMB_VAL%))
    accum_low& = (accum& And 255) * 256
    accum& = accum_low& Xor crc_tbl&
 Next L
	
 Crc_Calc = accum&

End Function

Sub Crc_ReadFile ()
   'Used to read the CRC constants from external file    

ffile$ = "crc.dat"
If Dir$(ffile$) = "" Then  'test to see whether data file is in current directory
     	'Request data path from user
	file_path$ = InputBox$("Enter drive and path for CRC.DAT", "CRC.DAT Not Found")
	If file_path$ = "" Then End
	If Right$(file_path$, 1) <> "\" Then file_path$ = file_path$ + "\"
	ffile$ = file_path$ + ffile$
    	If Dir$(ffile$) = "" Then 'test whether new path is valid 
		MsgBox ffile$ + " not found"
		End 'exit program because data file can't be found
    	End If
End If
    
filenum% = FreeFile
Open ffile$ For Binary Access Read Lock Read As #filenum%

For L = 0 To 255
	Get #1, (1 + L * 2), crc_data(L)   ' read data file into array
Next L

Close #filenum%
 
End Sub

Function Decode_LoopData (Value As String) As Integer

    'Decode raw data bytes from "loop" command to numeric value
' NOTE
' Low bit is left char in value
' High bit is right char in value

If Len(Value) = 1 Then
     Decode_LoopData = Val("&H" + Hex$(Asc(Value)))
ElseIf Len(Value) = 2 Then
    temp% = Asc(Left$((Value), 1))
    If temp% < 16 Then
	Decode_LoopData = Val("&H" + Hex$(Asc(Right$(Value, 1))) + "0" + Hex$(temp%))
    Else
	Decode_LoopData = Val("&H" + Hex$(Asc(Right$(Value, 1))) + Hex$(temp%))
    End If
    
End If  

End Function

Sub get_loopdata ()
'procedure for obtaining weather data from LOOP command

form1!Timer1.Enabled = False   'Turn off form1 timer that triggers this subroutine
Dim attempts As Integer  'Number of times  this subroutine attempts to read comm port
Dim cstat As comstat     'used in API GetCommError function call
Dim comm_command$        'Command string to be sent to link

comm_command$ = "LOOP" + Chr$(255) + Chr$(255) + Chr$(13)
Comm_FlushBuf  'Flush any charactors from transmit and receive buffers
apierror = WriteComm(commhandle, comm_command$, Len(comm_command$)) 'send command to port
Comm_ErrorCheck
    
Loop_Data$ = String$(19, "m")  'create fixed length string to recieve loop data
attempts = 8  'try this many times to read comm port before giving up
i = 0
Do
    i = i + 1
    wait .3  'pause for .3 seconds before trying to read port
    di% = GetCommError(commhandle, cstat) 'Find out how many bytes are in receive buffer
    If di% <> 0 Then   'Test for comm error
	'di% is the comm error, if di% = 2 then the recieve queue overflowed, this
	'is the most common error. Refer to Windows SDK for explanation of other errors
	'If a comm error occurs, it is written to the file(link_Err.Log) with time and 
	'date. Can be usefull for troubleshooting.
	filenum% = FreeFile
	file_name$ = file_path$ + "link_Err.Log"
	Open file_name$ For Append As filenum%
	Print #filenum%, Format$(Now, "mm/dd/yy   hh:nn:ss") + "   Comm Error " + Hex$(di%)
	Close #filenum%
	Update_Status 3  'increment comm error count
	Link_Reset    
	Exit Sub
    ElseIf i = attempts Then  'test that allowable number of tries is reached
	    Update_Status 4   'increment timeout counter
	    Link_Reset
	    Exit Sub
    End If

Loop Until cstat.cbinque = 19   'exit loop when 19 bytes have been reached

apierror = ReadComm(commhandle, Loop_Data$, 19)  'read comm port
Comm_ErrorCheck
crc_error& = Crc_Calc(Mid$(Loop_Data$, 3, 17))   'crc test
If crc_error& <> 0 Then        'test for bad crc
    Update_Status 2   ' increment CRC error count
    Link_Reset
    Exit Sub
End If
	
Update_Status 1  ' increment succesful hit count

    
 'Store weather data in variable ldata
ldata.tin = .1 * (Decode_LoopData(Mid$(Loop_Data$, 3, 2)))
ldata.tout = .1 * (Decode_LoopData(Mid$(Loop_Data$, 5, 2)))
ldata.wspd = Decode_LoopData(Mid$(Loop_Data$, 7, 1))
ldata.wdir = Decode_LoopData(Mid$(Loop_Data$, 8, 2))
ldata.bar = .001 * (Decode_LoopData(Mid$(Loop_Data$, 10, 2)))
ldata.hin = Decode_LoopData(Mid$(Loop_Data$, 12, 1))
ldata.hout = Decode_LoopData(Mid$(Loop_Data$, 13, 1))
ldata.rain = .01 * Decode_LoopData(Mid$(Loop_Data$, 14, 2))
    
    
  'display weather data on form1
    
    'INSIDE TEMPERATURE
form1.Label1(0).Caption = Format$(ldata.tin, ".0") + "°"
    
    'OUTSIDE TEMPERATURE
form1.Label1(1).Caption = Format$(ldata.tout, ".0") + "°"

    'Windspeed
form1.Label1(6).Caption = Format$(ldata.wspd, "0") + " mph"

    'Wind Direction
form1.Label1(7).Caption = Format$(ldata.wdir, "0") + "°"
    
    'BAROMETER
form1.Label1(4).Caption = Format$(ldata.bar, ".00") + " in."
    
    'INSIDE HUMIDITY
form1.Label1(2).Caption = Format$(ldata.hin, "0") + "%"

    'OUTSIDE HUMIDITY
form1.Label1(3).Caption = Format$(ldata.hout, "0") + "%"

     'Rain
form1.Label1(5).Caption = Format$(ldata.rain, ".00") + " in."


form1!Timer1.Enabled = True   'turn back on form1 timer to send next "Loop" command

End Sub

Sub Link_Reset ()
   'Used to reset link after a comm, CRC, or timeout error

Comm_FlushBuf  'Flush any charactors from transmit and receive buffers
comm_command$ = Chr$(13)  'carriage return to force a reset of link
apierror = WriteComm(commhandle, comm_command$, Len(comm_command$)) 'send command to port
Comm_ErrorCheck
wait .5  'wait a while for link to respond and allow other proccesses to execute
form1!Timer1.Enabled = True  'turn back on form1 timer to send next "Loop" command
End Sub

Sub Start_Connection ()
  'Used to read comm port selection from form1 and open the port

If Comm_flag Then   'Test to see whether comm port is already open
     Exit Sub
End If

i = 0
For i = 1 To 4           'Read comm port value
    If form1!Option1(i).Value Then
	CommPortName$ = form1!Option1(i).Caption
	Exit For
    End If
Next i
    
Crc_ReadFile     'read in crc data    

Comm_Start CommPortName$   'open the comm port

form1!Timer1.Enabled = True 'turn on form1 timer to send  "Loop" command

End Sub

Function unsigned_int (INTGR As Integer) As Long
'Used to convert a long integer value to
'an unsigned integer data type ; used in the CRC check

If INTGR < 0 Then
    unsigned_int = INTGR + 65536
Else
    unsigned_int = INTGR
End If

End Function

Sub Update_Status (Value%)
  'Used to udate the comm status on form1
  'Value% selects what status to update

Static Tries%  'Total number of times "Loop" command is sent to link
Static hits%   'Total nember of succesfull responces from link
Static CRC%    'Total number of CRC errors
Static comm%   'Total number of comm errors
Static timeout%  'Total number of times that get_loopdata sub times out before getting data

Tries% = Tries% + 1
form1!Label4(0).Caption = Format$(Tries%, "")

Select Case Value%
    Case 1
	hits% = hits% + 1
	form1!Label4(1).Caption = Format$(hits%, "")
    Case 2
	CRC% = CRC% + 1
	form1!Label4(2).Caption = Format$(CRC%, "")
    Case 3
	comm% = comm% + 1
	form1!Label4(3).Caption = Format$(comm%, "")
    Case 4
	timeout% = timeout% + 1
	form1!Label4(4).Caption = Format$(timeout%, "")
End Select
End Sub

Sub wait (delay As Single)
'Allows other programs time to run
' delay is the time in seconds to wait while other processes are executing

Dim Start_Time!
Start_Time! = Timer

Do
    DoEvents
Loop Until Timer - Start_Time! >= delay Or Timer - Start_Time! < 0

End Sub

