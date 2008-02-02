require 'noaa_forecast'

class WxController < ApplicationController
  include REXML

  def index
    @current = CurrentCondition.find_by_location(AppConfig.location)
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
    @forecast = ApplicationHelper.forecast
    puts @forecast
  end
  
  def last_rain
    s = ArchiveRecord.find(:first, :conditions => "rainfall > 0 and location = #{AppConfig.location}", :limit => 1, :order => "date desc")
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
    @current = CurrentCondition.find_by_location(AppConfig.location)
    render(:template => "wx/_current_conditions",
           :layout => false)
  end
  
end
