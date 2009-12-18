require 'pp'
require 'period'
 
class WxPeriod < Period
  
  def WxPeriod.add_to_db(summary, location)
    s = PastSummary.find_or_initialize_by_period_and_location(summary.period, location)
    s.update_attributes(summary.attributes)
    s
  end

  def WxPeriod.this_hour_summary(location)
    return WxPeriod.query(this_hour, location)
  end
  
  def WxPeriod.today_summary(location)
    return WxPeriod.query(today, location)
  end
  
  def WxPeriod.this_week_summary(location)
    return WxPeriod.query(this_week, location)
  end
  
  def WxPeriod.this_month_summary(location)
    return WxPeriod.query(this_month, location)
  end
  
  def WxPeriod.last_hour_summary(location)
    s = PastSummary.find_by_period_and_location("LAST_HOUR", location)
    if s.nil? or s.startdate.utc != Period.last_hour.start_time.utc
      pd = WxPeriod.query(last_hour, location)
      return WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    else
      return s      
    end
  end
  
  def WxPeriod.yesterday_summary(location)
    s = PastSummary.find_by_period_and_location("YESTERDAY", location)
    if s.nil? or s.startdate.utc != Period.yesterday.start_time.utc
      pd = WxPeriod.query(yesterday, location)
      return WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    else
      return s      
    end
  end
  
  def WxPeriod.last_week_summary(location)
    s = PastSummary.find_by_period_and_location("LAST_WEEK", location)
    if s.nil? or s.startdate.utc != Period.last_week.start_time.utc
      pd = WxPeriod.query(last_week, location)
      return WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    else
      return s      
    end
  end
  
  def WxPeriod.last_month_summary(location)
    s = PastSummary.find_by_period_and_location("LAST_MONTH", location)
    if s.nil? or s.startdate.utc != Period.last_month.start_time.utc
      pd = WxPeriod.query(last_month, location)
      return WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    else
      return s      
    end
  end

  def hi_temp_date(pd, temp, location)
    a = ArchiveRecord.find(:first, :conditions => "location = '#{location}' and date >= '#{pd.start_time_sql}' and date < '#{pd.end_time_sql}' and high_outside_temp = '#{temp}'", :order => "date desc")
    if (a != nil) then
      a.date != nil ? a.date : nil
    else
      nil
    end
  end
  
  def low_temp_date(pd, temp, location)
    a = ArchiveRecord.find(:first, :conditions => "location = '#{location}' and date >= '#{pd.start_time_sql}' and date < '#{pd.end_time_sql}' and low_outside_temp = '#{temp}'", :order => "date desc")
    if (a != nil) then
      a.date != nil ? a.date : nil
    else
      nil
    end
  end
  
  def gust(pd, gust, location)
    a = ArchiveRecord.find(:first, :conditions => "location = '#{location}' and date >= '#{pd.start_time_sql}' and date < '#{pd.end_time_sql}' and high_wind_speed = '#{gust}'", :order => "date desc")
    if (a != nil) then
      { :date => a.date != nil ? a.date : nil,
        :dir => a.direction_of_high_wind_speed != nil ? a.direction_of_high_wind_speed : nil }
    else
      { :date => nil, :dir => nil }
    end
  end
  
  def WxPeriod.query(pd, location)
    rs = ArchiveRecord.find_by_sql("select avg(average_dewpoint) as avgDewpoint, 
                                       avg(outside_humidity) as avgHumidity, 
                                       avg(pressure) as avgPressure, avg(outside_temp) as avgTemp, 
                                       avg(average_wind_speed)as avgWindspeed, avg(average_apparent_temp) as avgWindchill, 
                                       max(average_dewpoint) as hiDewpoint, max(high_wind_speed) as hiWindspeed, 
                                       max(outside_humidity) as hiHumidity, max(pressure) as hiPressure, max(high_outside_temp) as hiTemp, 
                                       max(average_apparent_temp) as hiWindchill, min(average_dewpoint) as lowDewpoint,
                                       min(outside_humidity) as lowOutsideHumidity, min(pressure) as lowPressure, 
                                       min(low_outside_temp) as lowTemp, min(average_apparent_temp) as lowWindchill, sum(rainfall) as rain,
                                       avg(high_outside_temp) - 65.0 as degreeDays
                                    from archive_records d 
                                    where d.location = '#{location}' 
                                       and d.date > '#{pd.start_time_sql}'
                                       and d.date <= '#{pd.end_time_sql}';");
    #FIXME - needs massive refactoring of this class to get rid of the statics
    my_pd = WxPeriod.new(pd.start_time, pd.end_time)
    rs[0]["hiTempDate"] = my_pd.hi_temp_date(my_pd, rs[0]["hiTemp"], location)
    rs[0]["lowTempDate"] = my_pd.low_temp_date(my_pd, rs[0]["lowTemp"], location)
    rs[0]["gustDate"] = my_pd.gust(my_pd, rs[0]["hiWindspeed"], location)[:date]
    rs[0]["gustDir"] = my_pd.gust(my_pd, rs[0]["hiWindspeed"], location)[:dir]
    rs[0]["startdate"] = pd.start_time.utc
    rs[0]["enddate"] = pd.end_time.utc
    rs[0]["period"] = pd.period_name
    rs[0]
  end
end


