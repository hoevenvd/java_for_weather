<head>
<html>
<META NAME="keywords" CONTENT="Boston Beverly tide weather mass massachusetts ma live java temperature dry hot humid cold new england northeast current humid rain wind windy current">
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<meta HTTP-EQUIV="Refresh" CONTENT="600">
<meta HTTP-EQUIV="ContentType" CONTENT="text/html">
<title>Very Current Beverly, MA Weather</title>
</head>

<%@ include file="_loadData.jsp" %>


<body BGCOLOR="#FFFFFF" background="http://www.tom.org:8081/weather/images/sky-bkgd1.jpg">
<center>
<table border="0" width="100%" cellspacing="0" cellpadding="3">
  <tr>
    <td width="100%" bgcolor="#000080"><font face="Arial Black" color="#FFFFFF"><big>
    <strong>Very Current Weather in Beverly, MA</strong></big></font></td>
  </tr>
</table>

<p>
<applet CODE = "Ticker.class" archive="java/ticker.jar" WIDTH = 500 HEIGHT = 25>
<PARAM NAME = TextCount VALUE =1>
<PARAM NAME = text1 VALUE =" <%= new Date() %> - Temp: <%= snap.getOutsideTemp() %> / Rel. Humidity: <%= snap.getOutsideHumidity() %>% / Dewpoint: <%= snap.getDewpoint() %> / Wind: <%= snap.getWindDirection().toLongString() %> at <%= snap.getWindspeed() %> mph / Barometer: <%= snap.getPressure() %> - <%= snap.getBarStatus() %> / Sunrise: <%= daylightInfo.getSunrise() %> / Sunset: <%= daylightInfo.getSunset() %> / Hours of daylight today: <%= daylightInfo.getDaylight() %> / Today's High Temp: <%= today.getHighTemp().getValue() %> at <%= DateUtils.getTime(today.getHighTemp().getOccurrenceDate()) %> / Today's Low Temp: <%= today.getLowTemp().getValue() %> at <%= DateUtils.getTime(today.getLowTemp().getOccurrenceDate()) %> Today's Rain: <%= today.getRain() %> inches ">
<PARAM NAME = AppBGImage VALUE =pattern.gif>
<PARAM NAME = AppTile VALUE =true>
<PARAM NAME = DelayBetweenChars VALUE =60>
<PARAM NAME = DelayBetweenRuns VALUE =0>
<PARAM NAME = VertCenter VALUE =true>
<PARAM NAME = Font VALUE =Helvetica>
<PARAM NAME = Style VALUE =BoldItalic>
<PARAM NAME = Pointsize VALUE =18>
<h2><b>You do not see the Java Ticker Applet - You might want to consider a browser upgrade!</b></h2>

</applet>


<p><font face="Arial">This page
features up to the second weather from about 20 miles Northeast of downtown Boston,
Mass.&nbsp; With the exception of the
radar image thanks to <a href="http://www.wunderground.com">Weather Underground</a>, all of this information
comes from sensors mounted on my house.  Numbers are updated constantly
and the page reloads itself
every 10 minutes.  The graphs are built every 30 minutes.</font><p>
<p>
<A HREF="mailto:tom@tom.org">Contact: Tom Mitchell</A>
<p>
Current Date: <b><%= new Date() %></b>
<br>
Data last collected from sensors:<b> <%= snap.getDate() %></b>
<br>
Database current through: <b><%= thisHour.getAsOfDate() %></b>
<br>
<p>

<applet CODE = "SimpleSunApplet.class" archive="java/earth24.jar" WIDTH = 360 HEIGHT = 168 NAME = "SimpleSun">
<PARAM NAME = bgcolor VALUE ="#FFFFFF" >
  The SimpleSun Applet is displayed here if your browser supports Java.
</applet>

<TABLE BORDER WIDTH="303" BGCOLOR="#C0C0C0" >

<TR>
<TD VALIGN=TOP COLSPAN="2">
<TABLE BORDER=0 WIDTH="100%" >
<TR>
<TD ALIGN=CENTER COLSPAN="2" WIDTH="100%"><B><FONT FACE="Arial">Temperature</FONT></B>&nbsp;
<BR><b><%= snap.getOutsideTemp() %></b></TD>
</TR>
<TR>
<TD ALIGN=CENTER  VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">Today's Low:</FONT>
<%= today.getLowTemp().getValue() %> at <%= DateUtils.getTime(today.getLowTemp().getOccurrenceDate()) %></TD>
</TR>
<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">Today's High: </FONT>
<%= today.getHighTemp().getValue() %> at <%= DateUtils.getTime(today.getHighTemp().getOccurrenceDate()) %>
</TD>
<tr>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">Today's Low Wind Chill: </FONT>
<%= today.getLowWindChill().getValue() %> at <%= DateUtils.getTime(today.getLowWindChill().getOccurrenceDate()) %>
</TD>
<tr>

<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%">
Yesterday's Low: <%= yesterday.getLowTemp().getValue() %> at <%= DateUtils.getTime(yesterday.getLowTemp().getOccurrenceDate()) %>
</td>
</tr>

<tr>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%">Yesterday's High:
<%= yesterday.getHighTemp().getValue() %> at <%= DateUtils.getTime(yesterday.getHighTemp().getOccurrenceDate()) %>
</td>
</tr>

<tr>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%">
This Week Avg Temp: <%= thisWeek.getAvgTemp() %>
</td>
</tr>

<tr>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%">
Last Week Avg Temp: <%= lastWeek.getAvgTemp() %>
</td>
</tr>

<tr>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%">
This Month Avg Temp: <%= thisMonth.getAvgTemp() %>
</td>
</tr>

<tr>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%">
Last Month Avg Temp: <%= lastMonth.getAvgTemp() %>
</td>
</tr>

</TABLE>
</TD>
</TR>
<TR>
<TD COLSPAN="2" >
<!--
<img src="http://www.weather.com/custom-images/anim/central_rad_300x500.gif"  width="300" height="500" alt="MidWest Conditions - Courtesy The WeatherChannel">
-->

<img src="http://www.wunderground.com/data/640x480/ne_rd_anim.gif"   width="320" height="240" alt="National Radar - Courtesy WCCO">

</TD>
</TR>

<TR>
<TD COLSPAN="2" >
<!--
<img src="http://www.weather.com/custom-images/anim/central_rad_300x500.gif"  width="300" height="500" alt="MidWest Conditions - Courtesy The WeatherChannel">
-->

<img src="http://www.visi.com/~tom/webcam.jpg"   width="320" height="240" alt="Current Webcam">

</TD>
</TR>

<TR>

<TD ALIGN=CENTER VALIGN=TOP COLSPAN=2 WIDTH="100%"><B>
<FONT FACE="Arial">Data collected from sensors:</FONT></B>
<BR><%= snap.getDate() %></TD>
</tr>

<TD ALIGN=CENTER VALIGN=TOP WIDTH="50%"><B><FONT FACE="Arial">Temperature</FONT></B>&nbsp;
<BR><%= snap.getOutsideTemp() %></TD>


<TD ALIGN=CENTER VALIGN=TOP WIDTH="50%"><B><FONT FACE="Arial">Humidity</FONT></B>&nbsp;
<BR><%= snap.getOutsideHumidity() %></TD>

</tr>
<tr>

<TD ALIGN=CENTER VALIGN=TOP WIDTH="50%"><B><FONT FACE="Arial">Dewpoint</FONT></B>&nbsp;
<BR><%= snap.getDewpoint() %></TD>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="50%"><B><FONT FACE="Arial">Wind Chill</FONT></B>
&nbsp;<BR><%= Rounding.toString(Converter.getWindChill((int)snap.getWindspeed(), snap.getOutsideTemp()),0) %></TD>
</TR>


<TR>
<TD COLSPAN="2" WIDTH="292">
<CENTER><B><FONT FACE="Arial"><FONT SIZE=-1>Wind</FONT></FONT></B></CENTER>
</TD>
</TR>
<TR>
<TD ALIGN=CENTER VALIGN=CENTER COLSPAN="2" WIDTH="100%">
<TABLE BORDER=0 WIDTH="100%" >
<TR>
<TD ALIGN=CENTER VALIGN=CENTER>
<%= snap.getWindDirection().toLongString() %> at <%= Rounding.toString(snap.getWindspeed(),0) %> mph</TD>
</TR>
<TR>
<TD ALIGN=CENTER VALIGN=CENTER >

<FONT FACE="Arial">Today's High Gust: </FONT>
<%= today.getHighGust().getValue() %> mph<br><%= DateUtils.getTime(today.getHighGust().getOccurrenceDate()) %>
<br>This Hour Avg. WindSpeed: <%= thisHour.getAvgWind() %> mph
<br>Last Hour Avg. WindSpeed: <%= lastHour.getAvgWind() %> mph
<br>Today's Avg. WindSpeed: <%= today.getAvgWind() %> mph

</TD>
</TR>
</TABLE>
&nbsp;</TD>
</TR>

<TR>
<TD COLSPAN="2" WIDTH="292">
<CENTER><B><FONT FACE="Arial"><FONT SIZE=-1>Barometer</FONT></FONT></B></CENTER>
<CENTER><%= snap.getPressure() %></CENTER>
<CENTER><%= snap.getBarStatus() %></CENTER>
<CENTER>Today's High: <%= today.getHighPressure().getValue() %> - <%= DateUtils.getTime(today.getHighPressure().getOccurrenceDate()) %></CENTER>
<CENTER>Today's Low: <%= today.getLowPressure().getValue() %> - <%= DateUtils.getTime(today.getLowPressure().getOccurrenceDate()) %></CENTER>
</TD>
</TR>
<TR>
<TD COLSPAN="2" WIDTH="292">
<CENTER><B><FONT FACE="Arial"><FONT SIZE=-1>Precipitation</FONT></FONT></B></CENTER>
<CENTER>Today: <%= today.getRain() %></CENTER>
<CENTER>Yesterday: <%= yesterday.getRain() %> inches</CENTER>
<CENTER>This week: <%= thisWeek.getRain() %> inches</CENTER>
<CENTER>Last week: <%= lastWeek.getRain() %> inches (sun - sat)</CENTER>
<!-- <CENTER>Last rain: 2000-12-30 21:26:00.0</CENTER> -->
</TD>
</TR>
<TR>
<TD COLSPAN="2" WIDTH="292">
<CENTER><B><FONT FACE="Arial"><FONT SIZE=-1>Daylight</FONT></FONT></B></CENTER>
<CENTER>Today's Sunrise: <%= daylightInfo.getSunrise() %><br>
<CENTER>Today's Sunset: <%= daylightInfo.getSunset() %><br>
<CENTER>Hours of daylight today: <%= daylightInfo.getDaylight() %><p>

<% cal = Calendar.getInstance();
   cal.add(Calendar.DATE, 1);
   daylightInfo = daylightCalc.getDaylightInfo(cal.getTime());
%>

<CENTER>Tomorrow's Sunrise: <%= daylightInfo.getSunrise() %><br>
<CENTER>Tomorrow's Sunset: <%= daylightInfo.getSunset() %><br>
<CENTER>Hours of daylight tomorrow: <%= daylightInfo.getDaylight() %><br>
<% cal = Calendar.getInstance();
   cal.add(Calendar.DATE, 7);
   daylightInfo = daylightCalc.getDaylightInfo(cal.getTime());
%>

<br>

<CENTER>Hours of daylight a week from today: <%= daylightInfo.getDaylight() %><br>

<% cal = Calendar.getInstance();
   cal.add(Calendar.MONTH, 1);
   daylightInfo = daylightCalc.getDaylightInfo(cal.getTime());
%>
<CENTER>Hours of daylight a month from today: <%= daylightInfo.getDaylight() %><br>

<% cal = Calendar.getInstance();
   cal.add(Calendar.MONTH, 6);
   daylightInfo = daylightCalc.getDaylightInfo(cal.getTime());
%>
<CENTER>Hours of daylight six months from today: <%= daylightInfo.getDaylight() %><p>
<CENTER>N 42.56210709, W 70.84961700<p>
</TD>
</TR>
</TABLE>
</center>
<p>
<p>
<center><b><h2>3 Day Tide Predictions</h2></b></center>

<p align="center">
    <font face="Arial">
        <img src="http://www.tom.org:8081/weather/images/tides.png?date=<%= new Date().getTime() / 60000L%>" alt="tides.png">
    </font>
</p>

<center><b><h2>36 Hour Temperatures</h2></b></center>

<p align="center">
    <font face="Arial">
        <img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/36_temp.jpg?date=<%= new Date().getTime() / 60000L%>" alt="temp24.gif"></font></p>

<center><b><h2>36 Hour Wind Gust</h2></b></center>
<p align="center"><font face="Arial"><img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/36_wgst.jpg?date=<%= new Date().getTime() / 60000L %>" alt="windspeed24.gif"></font></p>

<center><b><h2>36 Hour Wind Direction</h2></b></center>
<p align="center"><font face="Arial"><img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/36_wdir.jpg?date=<%= new Date().getTime() / 60000L %>" alt="winddir24.gif"></font></p>

<center><b><h2>15 Day Temperatures</h2></b></center>
<p align="center"><font face="Arial"><img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/360_temp.jpg?date=<%= new Date().getTime() / 60000L %>" alt="tempweek.gif"></font></p>

<center><b><h2>15 Day Dewpoints</h2></b></center>
<p align="center"><font face="Arial"><img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/360_dew.jpg?date=<%= new Date().getTime() / 60000L %>" alt="dewweek.gif"></font></p>

<center><b><h2>15 Day Pressure</h2></b></center>
<p align="center"><font face="Arial"><img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/360_pres.jpg?date=<%= new Date().getTime() / 60000L %>" alt="pressure24.gif"></font></p>

<center><b><h2>15 Day Rainfall</h2></b></center>
<p align="center"><font face="Arial"><img width=500 height=185 border=1 src="http://www.tom.org:8081/weather/images/360_rain.jpg?date=<%= new Date().getTime() / 60000L %>"alt="rainweek.gif"></font></p>

<br>
<TABLE BORDER=1 ALIGN="CENTER" WIDTH="50%" >

<TR>
<TD ALIGN=CENTER COLSPAN="2" WIDTH="100%"><B><FONT FACE="Arial">Long-term average Temperatures</FONT></b>
</TD>
</TR>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">This Week: </FONT>
<%= thisWeek.getAvgTemp() %>
</TD>
</TR>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">Last Week: </FONT>
<%= lastWeek.getAvgTemp() %>
</TD>
</TR>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">This Month: </FONT>
<%= thisMonth.getAvgTemp() %>
</TD>
</TR>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">Last Month: </FONT>
<%= lastMonth.getAvgTemp() %>
</TD>
</TR>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">This Season: </FONT>
<%= thisSeason.getAvgTemp() %>
</TD>
</TR>

<TR>
<TD ALIGN=CENTER VALIGN=TOP WIDTH="100%"><FONT FACE="Arial">Last Season: </FONT>
<%= lastSeason.getAvgTemp() %>
</TD>
</TR>
</TABLE>

<img src="http://www.visi.com/cgi-bin/Count.cgi?sh=f&df=tom.dat">
<center>
<img src="http://www.tom.org:8081/weather/images/java-logo.gif" alt="100% Java (tm)!">
<img src="http://www.tom.org:8081/weather/images/nowindows.gif" alt="100% Windows (tm) Free!">
</center>
<br>
<center>
<a href="http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=KMASTONE1">
<img border=0 src="http://banners.wunderground.com/cgi-bin/banner/ban/wxBanner?bannertype=WeatherStationCount&weatherstationcount=KMASTONE1" width=160 height=163>
</center>


<p>

<!-- End Weather and Climate Ring Code -->

<script language="javascript" src="http://ss.webring.com/navbar?f=j;y=mitct02;u=99831932710451447">
</script><noscript><center>
<table bgcolor=gray cellspacing=0 border=2 bordercolor=red><tr>
<td><table cellpadding=2 cellspacing=0 border=0><tr><td align=center>
<font face=arial size=-1>This site is a member of WebRing. 
<br>To browse visit <a href="http://ss.webring.com/navbar?f=l;y=mitct02;u=99831932710451447">
Here</a>.</font></td></tr></table></td></tr></table>
</center></noscript>

<p>
<center>
<table border="1" width="170" cellspacing="0" background="http://one-barton-family.net/Science/Meteorology/cloud.jpg" height="10" cellpadding="0">
  <tr>
    <td width="180">
      <table border="0" width="180" cellspacing="0" cellpadding="0">
        <tr>
          <td width="25" valign="middle" align="center"><font face="arial,helvetica" size=2>
            <a target="_top" style="text-decoration: none;" href="http://www.ringsurf.com/netring?ring=NEWeather;action=home"><img src="http://one-barton-family.net/Science/Meteorology/new_sm2.png" border=1 width="30" height="47" alt="Welcome to the New England Weather web ring - please join!"></a></font>
          </td>
          <td valign="top" align="center" width="155">
            <table border="0" width="100%" cellspacing="0" cellpadding="0" height="10">
              <tr>
                <td width="100%" valign="top" align="center"><a href="http://one-barton-family.net/Science/Meteorology/new.html"><img border="1" src="http://one-barton-family.net/Science/Meteorology/newr.jpg" width="146" height="15" alt="Welcome to the New England Weather Web Ring!"></a></td>
              </tr>
              <tr>
                <td width="100%" valign="top" align="center">
                  <map name="FPMap3">
                    <area href="http://www.ringsurf.com/netring?ring=NEWeather;id=5;action=prev" shape="rect" coords="3, 1, 45, 14">
                    <area href="http://www.ringsurf.com/netring?ring=NEWeather;id=5;action=list" shape="rect" coords="49, 1, 72, 14">
                    <area href="http://www.ringsurf.com/netring?ring=NEWeather;id=5;action=rand" shape="rect" coords="77, 1, 117, 14">
                    <area href="http://www.ringsurf.com/netring?ring=NEWeather;id=5;action=next" shape="rect" coords="123, 1, 147, 14">
                  </map>
                  <img border="0" src="http://one-barton-family.net/Science/Meteorology/options.jpg" width="149" height="15" usemap="#FPMap3">
                </td>
              </tr>
              <tr>
                <td width="100%" valign="top" align="center">
                  <map name="FPMap4">
                    <area href="http://www.ringsurf.com" shape="rect" coords="53, 1, 90, 12">
                  </map><img border="0" src="http://one-barton-family.net/Science/Meteorology/rs.gif" usemap="#FPMap4" width="94" height="13">
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</center>
</body>
</html>
