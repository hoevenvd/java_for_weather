<%@ taglib uri="/WEB-INF/weather.tld" prefix="wx"  %>

<table width="400" align=center bgcolor="#cccccc" cellspacing=1 cellpadding=1>
  <tr>
    <td bgcolor="#ffffff"><wx:getCurrentConditions data="date" units="x" /></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff" >Current</font></b></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff" >High</font></b></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff">Low</font></b></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff">Avg</font></b></td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Temperature</b></td>
    <td><b><wx:getCurrentConditions data="temp" units="f" /></b>&nbsp;&#176;F</td>
    <td><b><wx:getPeriodData period="2" data="highTemp" units="f" /></b>&nbsp;&#176;F</td>
    <td><b><wx:getPeriodData period="2" data="lowTemp" units="f" /></b>&nbsp;&#176;F</td>
    <td><b><wx:getPeriodData period="2" data="avgTemp" units="f" /></b>&nbsp;&#176;F</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Dew Point</b></td>
    <td><b><wx:getCurrentConditions data="dewpoint" units="f" /></b>&nbsp;&#176;F</td>
    <td>-</td>
    <td>-</td>
    <td><b><wx:getPeriodData period="2" data="avgDewpoint" units="f" /></b>&nbsp;&#176;F</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Humidity</b></td>
    <td><b><wx:getCurrentConditions data="humidity" units="f" /></b>%</td>
    <td><b><wx:getPeriodData period="2" data="highHumidity" units="f" /></b>%</td>
    <td><b><wx:getPeriodData period="2" data="lowHumidity" units="f" /></b>%</td>
    <td><b><wx:getPeriodData period="2" data="avgHumidity" units="f" /></b>%</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Wind Speed</b></td>
    <td><b><wx:getCurrentConditions data="windspeed" units="mph" /></b>
      &nbsp;<wx:getCurrentConditions data="winddirection" units="dir-short" /></td>
    <td><b><wx:getPeriodData period="2" data="highGust" units="f" /></b> mph</td>
    <td>-</td>
    <td><b><wx:getPeriodData period="2" data="avgWind" units="f" /></b> mph</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Pressure</b></td>
    <td><b><wx:getCurrentConditions  data="pressure" units="f" /></b> in</td>
    <td><b><wx:getPeriodData period="2" data="highPressure" units="f" /></b> in</td>
    <td><b><wx:getPeriodData period="2" data="lowPressure" units="f" /></b> in</td>
    <td><b><wx:getPeriodData period="2" data="avgPressure" units="f" /></b> in</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Rain</b></td>
    <td><b><wx:getPeriodData period="2" data="rain" units="i" /></b></td>
    <td>-</td>
    <td>-</td>
    <td>-</td>
  </tr>
</table>
