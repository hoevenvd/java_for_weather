require 'pp'
require 'period'
 
class WxPeriod < Period
  def WxPeriod.this_hour_summary
    return WxPeriod.query(this_hour)
  end
  
  def WxPeriod.today_summary
    return WxPeriod.query(today)
  end
  
  def WxPeriod.this_week_summary
    return WxPeriod.query(this_week)
  end
  
  def WxPeriod.this_month_summary
    return WxPeriod.query(this_month)
  end
  
  def WxPeriod.this_year_summary
    return WxPeriod.query(this_year)
  end
  
  def WxPeriod.last_hour_summary
    return WxPeriod.query(last_hour)
  end
  
  def WxPeriod.yesterday_summary
    return WxPeriod.query(yesterday)
  end
  
  def WxPeriod.last_week_summary
    return WxPeriod.query(last_week)
  end
  
  def WxPeriod.last_month_summary
    return WxPeriod.query(last_month)
  end

  def hi_temp_date(pd, temp)
    a = ArchiveRecord.find(:first, :conditions => "date >= '#{@start_time_sql}' and date < '#{@end_time_sql}' and high_outside_temp = '#{temp}'", :order => "date desc")
    if (a != nil) then
      a.date != nil ? a.date : nil
    else
      nil
    end
  end
  
  def low_temp_date(pd, temp)
    a = ArchiveRecord.find(:first, :conditions => "date >= '#{@start_time_sql}' and date < '#{@end_time_sql}' and low_outside_temp = '#{temp}'", :order => "date desc")
    if (a != nil) then
      a.date != nil ? a.date : nil
    else
      nil
    end
  end
  
  def gust_date(pd, gust)
    a = ArchiveRecord.find(:first, :conditions => "date >= '#{@start_time_sql}' and date < '#{@end_time_sql}' and high_wind_speed = '#{gust}'", :order => "date desc")
    if (a != nil) then
      a.date != nil ? a.date : nil
    else
      nil
    end
  end
  
  def WxPeriod.query(pd)
    rs = ArchiveRecord.find_by_sql("select round(avg(average_dewpoint),1) as avgDewpoint, 
                                       round(avg(outside_humidity),0) as avgHumidity, 
                                       round(avg(pressure),2) as avgPressure, round(avg(outside_temp),1) as avgTemp, 
                                       round(avg(average_wind_speed),1) as avgWindspeed, round(avg(average_apparent_temp),0) as avgWindchill, 
                                       max(average_dewpoint) as hiDewpoint, max(high_wind_speed) as hiWindspeed, 
                                       max(outside_humidity) as hiHumidity, max(pressure) as hiPressure, max(high_outside_temp) as hiTemp, 
                                       max(average_apparent_temp) as hiWindchill, min(average_dewpoint) as lowDewpoint,
                                       min(outside_humidity) as lowOutsideHumidity, min(pressure) as lowPressure, 
                                       min(low_outside_temp) as lowTemp, min(average_apparent_temp) as lowWindchill, sum(rainfall) as rain 
                                    from archive_records d 
                                    where d.date > '#{pd.start_time_sql}' and d.date <= '#{pd.end_time_sql}';");
    #FIXME - needs massive refactoring of this class to get rid of the statics
    my_pd = WxPeriod.new(pd.start_time, pd.end_time)
    rs[0]["hiTempDate"] = my_pd.hi_temp_date(my_pd, rs[0]["hiTemp"])
    rs[0]["lowTempDate"] = my_pd.low_temp_date(my_pd, rs[0]["lowTemp"])
    rs[0]["gustDate"] = my_pd.gust_date(my_pd, rs[0]["hiWindspeed"])
    rs[0]
  end
end


