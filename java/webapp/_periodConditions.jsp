<%@ taglib uri="/WEB-INF/weather.tld" prefix="wx"  %>

<table width="400" align=center bgcolor="#cccccc" cellspacing=1 cellpadding=1>
  <tr>
    <td bgcolor="#ffffff"></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff" >High</font></b></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff">Low</font></b></td>
    <td bgcolor="#000080" align="center"><b><font color="#ffffff">Avg</font></b></td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Temperature</b></td>
    <td><b><wx:getPeriodData period="" data="highTemp" units="f" /></b>&nbsp;&#176;F</td>
    <td><b><wx:getPeriodData period="" data="lowTemp" units="f" /></b>&nbsp;&#176;F</td>
    <td><b><wx:getPeriodData period="" data="avgTemp" units="f" /></b>&nbsp;&#176;F</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Dew Point</b></td>
    <td>-</td>
    <td>-</td>
    <td><b><wx:getPeriodData period="" data="avgDewpoint" units="f" /></b>&nbsp;&#176;F</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Humidity</b></td>
    <td><b><wx:getPeriodData period="" data="highHumidity" units="f" /></b>%</td>
    <td><b><wx:getPeriodData period="" data="lowHumidity" units="f" /></b>%</td>
    <td><b><wx:getPeriodData period="" data="avgHumidity" units="f" /></b>%</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Wind</b></td>
    <td><b><wx:getPeriodData period="" data="highGust" units="f" /></b> mph</td>
    <td>-</td>
    <td><b><wx:getPeriodData period="" data="avgWind" units="f" /></b> mph</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Pressure</b></td>
    <td><b><wx:getPeriodData period="" data="highPressure" units="f" /></b> in</td>
    <td><b><wx:getPeriodData period="" data="lowPressure" units="f" /></b> in</td>
    <td><b><wx:getPeriodData period="" data="avgPressure" units="f" /></b> in</td>
  </tr>
  <tr bgcolor="#ffffff" class=smalltable>
    <td><b>Rain</b></td>
    <td>-</td>
    <td>-</td>
    <td><b><wx:getPeriodData period="" data="rain" units="f" /></b> in</td>
  </tr>
</table>
