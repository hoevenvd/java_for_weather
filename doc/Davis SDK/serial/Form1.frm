VERSION 2.00
Begin Form Form1 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "Loop Command Demo"
   ClientHeight    =   4620
   ClientLeft      =   2190
   ClientTop       =   2160
   ClientWidth     =   4380
   ControlBox      =   0   'False
   Height          =   5025
   Icon            =   FORM1.FRX:0000
   Left            =   2130
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   ScaleHeight     =   4620
   ScaleWidth      =   4380
   Top             =   1815
   Width           =   4500
   Begin Timer Timer1 
      Enabled         =   0   'False
      Interval        =   100
      Left            =   300
      Top             =   3975
   End
   Begin Frame Frame2 
      Caption         =   "Errors"
      Height          =   1065
      Index           =   1
      Left            =   2220
      TabIndex        =   28
      Top             =   2700
      Width           =   1935
      Begin Label Label4 
         Alignment       =   1  'Right Justify
         BorderStyle     =   1  'Fixed Single
         Height          =   240
         Index           =   4
         Left            =   960
         TabIndex        =   24
         Top             =   750
         Width           =   855
      End
      Begin Label Label4 
         Alignment       =   1  'Right Justify
         BorderStyle     =   1  'Fixed Single
         Height          =   240
         Index           =   3
         Left            =   960
         TabIndex        =   7
         Top             =   525
         Width           =   855
      End
      Begin Label Label4 
         Alignment       =   1  'Right Justify
         BorderStyle     =   1  'Fixed Single
         Height          =   240
         Index           =   2
         Left            =   960
         TabIndex        =   34
         Top             =   300
         Width           =   855
      End
      Begin Label Label3 
         Alignment       =   1  'Right Justify
         Caption         =   "Timeout:"
         Height          =   210
         Index           =   12
         Left            =   120
         TabIndex        =   31
         Top             =   750
         Width           =   735
      End
      Begin Label Label3 
         Alignment       =   1  'Right Justify
         Caption         =   "CRC:"
         Height          =   210
         Index           =   11
         Left            =   180
         TabIndex        =   30
         Top             =   300
         Width           =   675
      End
      Begin Label Label3 
         Alignment       =   1  'Right Justify
         Caption         =   "Comm:"
         Height          =   210
         Index           =   10
         Left            =   120
         TabIndex        =   29
         Top             =   525
         Width           =   735
      End
   End
   Begin Frame Frame2 
      Caption         =   "Status"
      Height          =   1065
      Index           =   0
      Left            =   180
      TabIndex        =   25
      Top             =   2700
      Width           =   1755
      Begin Label Label4 
         Alignment       =   1  'Right Justify
         BorderStyle     =   1  'Fixed Single
         Height          =   240
         Index           =   1
         Left            =   780
         TabIndex        =   33
         Top             =   600
         Width           =   855
      End
      Begin Label Label4 
         Alignment       =   1  'Right Justify
         BorderStyle     =   1  'Fixed Single
         Height          =   240
         Index           =   0
         Left            =   780
         TabIndex        =   32
         Top             =   300
         Width           =   855
      End
      Begin Label Label3 
         Alignment       =   1  'Right Justify
         Caption         =   "Hits:"
         Height          =   210
         Index           =   9
         Left            =   120
         TabIndex        =   27
         Top             =   675
         Width           =   495
      End
      Begin Label Label3 
         Alignment       =   1  'Right Justify
         Caption         =   "Tries:"
         Height          =   210
         Index           =   8
         Left            =   120
         TabIndex        =   26
         Top             =   300
         Width           =   495
      End
   End
   Begin CommandButton btn_exit 
      Caption         =   "Exit"
      Height          =   540
      Left            =   2880
      TabIndex        =   6
      Top             =   3900
      Width           =   1275
   End
   Begin CommandButton btn_connect 
      Caption         =   "Connect"
      Height          =   540
      Left            =   1200
      TabIndex        =   5
      Top             =   3900
      Width           =   1275
   End
   Begin Frame Frame1 
      Caption         =   "Comm Port"
      Height          =   2415
      Left            =   180
      TabIndex        =   0
      Top             =   150
      Width           =   1155
      Begin OptionButton Option1 
         Caption         =   "Com4"
         Height          =   465
         Index           =   4
         Left            =   240
         TabIndex        =   4
         Top             =   1725
         Width           =   855
      End
      Begin OptionButton Option1 
         Caption         =   "Com3"
         Height          =   465
         Index           =   3
         Left            =   240
         TabIndex        =   3
         Top             =   1275
         Width           =   855
      End
      Begin OptionButton Option1 
         Caption         =   "Com2"
         Height          =   465
         Index           =   2
         Left            =   240
         TabIndex        =   2
         Top             =   825
         Width           =   855
      End
      Begin OptionButton Option1 
         Caption         =   "Com1"
         Height          =   465
         Index           =   1
         Left            =   240
         TabIndex        =   1
         Top             =   375
         Width           =   795
      End
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   7
      Left            =   3360
      TabIndex        =   23
      Top             =   2325
      Width           =   795
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Wind Direction"
      Height          =   210
      Index           =   7
      Left            =   1740
      TabIndex        =   22
      Top             =   2325
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Windspeed"
      Height          =   210
      Index           =   6
      Left            =   1740
      TabIndex        =   21
      Top             =   2025
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Rain Total"
      Height          =   210
      Index           =   5
      Left            =   1740
      TabIndex        =   20
      Top             =   1725
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Barometer"
      Height          =   210
      Index           =   4
      Left            =   1740
      TabIndex        =   19
      Top             =   1425
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Outside Humidity"
      Height          =   210
      Index           =   3
      Left            =   1740
      TabIndex        =   18
      Top             =   1125
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Inside Humidity"
      Height          =   210
      Index           =   2
      Left            =   1740
      TabIndex        =   17
      Top             =   825
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Outside Temp."
      Height          =   210
      Index           =   1
      Left            =   1740
      TabIndex        =   16
      Top             =   525
      Width           =   1515
   End
   Begin Label Label3 
      Alignment       =   1  'Right Justify
      Caption         =   "Inside Temp."
      Height          =   210
      Index           =   0
      Left            =   1740
      TabIndex        =   15
      Top             =   225
      Width           =   1515
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   6
      Left            =   3360
      TabIndex        =   14
      Top             =   2025
      Width           =   795
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   5
      Left            =   3360
      TabIndex        =   13
      Top             =   1725
      Width           =   795
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   4
      Left            =   3360
      TabIndex        =   12
      Top             =   1425
      Width           =   795
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   3
      Left            =   3360
      TabIndex        =   11
      Top             =   1125
      Width           =   795
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   2
      Left            =   3360
      TabIndex        =   10
      Top             =   825
      Width           =   795
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   1
      Left            =   3360
      TabIndex        =   9
      Top             =   525
      Width           =   795
   End
   Begin Label Label1 
      BorderStyle     =   1  'Fixed Single
      Height          =   240
      Index           =   0
      Left            =   3360
      TabIndex        =   8
      Top             =   225
      Width           =   795
   End
End

Sub btn_connect_Click ()
Start_Connection

End Sub

Sub btn_exit_Click ()
Comm_Close
End
End Sub

Sub Timer1_Timer ()
get_loopdata
End Sub

