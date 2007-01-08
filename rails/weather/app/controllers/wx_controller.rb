

class WxController < ApplicationController
  include REXML

  LON = -1.23660504
  LAT = 0.742841626
  LOC = "01915"
  
  def index
    @current = CurrentCondition.find_by_location(LOC)
    @today = WxPeriod.today_summary[0]
    @yesterday = WxPeriod.yesterday_summary[0]
    @this_hour = WxPeriod.this_hour_summary[0]
    @last_hour = WxPeriod.last_hour_summary[0]
    @this_week = WxPeriod.this_week_summary[0]
    @this_month = WxPeriod.this_month_summary[0]
#    @this_year = WxPeriod.this_year_summary[0]
    @last_week = WxPeriod.last_week_summary[0]
    @last_month = WxPeriod.last_month_summary[0]
    last_rain_date = last_rain
    @last_rain = last_rain_date
    @conditions = ApplicationHelper.observed_conditions
    @conditions_date = ApplicationHelper.observed_conditions_date
    @visibility = ApplicationHelper.observed_visibility
  end
  
  def last_rain
    sql = "select date from archive_records where rainfall > 0 and location = " + "\'" + LOC + "\'"
    s = ArchiveRecord.find(:first, :conditions => "rainfall > 0 and location = #{LOC}", :limit => 1, :order => "date desc")
    if !s.nil?
      s[:date]
    else
      nil
    end
  end
  
  def current_conditions
    @conditions = ApplicationHelper.observed_conditions
    @conditions_date = ApplicationHelper.observed_conditions_date
    @visibility = ApplicationHelper.observed_visibility
    @current = CurrentCondition.find_by_location(LOC)
    render(:template => "wx/_current_conditions",
           :layout => false)
  end
  
  def minutes_to_hhmm(start_tm, end_tm)
    interval = ((end_tm - start_tm) / 60).to_i # get minutes
    tmp = interval.divmod(60)
    hours = tmp[0]
    minutes = tmp[1]
    return sprintf("%d:%02d", hours, minutes)
  end
  
end
