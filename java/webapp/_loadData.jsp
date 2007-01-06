<%@ page import="java.util.*,
                 javax.servlet.*,
                 javax.servlet.http.*,
                 org.tom.weather.daylight.*,
		             org.tom.util.*,
                 org.tom.weather.*,
                 com.tangosol.net.NamedCache,
                 com.tangosol.net.CacheFactory"
%>

<%! DaylightCalculator daylightCalc = new DaylightCalculator("EST", -71.1D, 41.5D); %>
<%! DaylightInfo daylightInfo; %>
<%! Calendar cal; %>
<%! SnapShot snap = null; %>
<%! PeriodData today = null; %>
<%! PeriodData yesterday = null; %>
<%! PeriodData thisHour = null; %>
<%! PeriodData lastHour = null; %>
<%! PeriodData thisWeek = null; %>
<%! PeriodData lastWeek = null; %>
<%! PeriodData thisMonth = null; %>
<%! PeriodData lastMonth = null; %>
<%! PeriodData thisSeason = null; %>
<%! PeriodData lastSeason = null; %>
<%! PeriodData thisYear = null; %>
<%! PeriodData lastYear = null; %>
<%! PeriodData forever = null; %>

<%
  NamedCache cache = CacheFactory.getReplicatedCache("ArchiveCache");
%>

<%
            daylightInfo = daylightCalc.getDaylightInfo(new Date());
            snap = (SnapShot)cache.get(Constants.CURRENT_SNAPSHOT);
            today = (PeriodData)cache.get("Period-" + Period.TODAY);
            yesterday = (PeriodData)cache.get("Period-" + Period.YESTERDAY);
            thisHour = (PeriodData)cache.get("Period-" + Period.THIS_HOUR);
            lastHour = (PeriodData)cache.get("Period-" + Period.LAST_HOUR);
            thisWeek = (PeriodData)cache.get("Period-" + Period.THIS_WEEK);
            lastWeek = (PeriodData)cache.get("Period-" + Period.LAST_WEEK);
            thisMonth = (PeriodData)cache.get("Period-" + Period.THIS_MONTH);
            lastMonth = (PeriodData)cache.get("Period-" + Period.LAST_MONTH);
            thisSeason = (PeriodData)cache.get("Period-" + Period.THIS_SEASON);
            lastSeason = (PeriodData)cache.get("Period-" + Period.LAST_SEASON);
            thisYear = (PeriodData)cache.get("Period-" + Period.THIS_YEAR);
            lastYear = (PeriodData)cache.get("Period-" + Period.LAST_YEAR);
            //forever = (PeriodData)cache.get("Period-" + Period.FOREVER);

%>
