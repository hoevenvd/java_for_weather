<%@ taglib uri="/WEB-INF/noaa-wx.tld" prefix="noaa-wx" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"  %>

<html:html/>

<head></head>
<body>
  <pre>
    <noaa-wx:getForecast
          url="http://weather.noaa.gov/pub/data/forecasts/zone/ma/maz019.txt"
          prefix="MAZ007"
    />
  </pre>
  <hr/>
  <pre>
    <noaa-wx:getForecast
          url="http://weather.noaa.gov/pub/data/forecasts/marine/coastal/an/anz250.txt"
          prefix="ANZ250"
    />
  </pre>
</body>
