require 'noaa_forecast'

class WxController < ApplicationController
  include REXML

  def index
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
    get_current_conditions
    @forecast = NoaaForecast.find_by_location(AppConfig.noaa_location)
  end

  def get_noaa_conditions
    noaa_conditions = NoaaConditions.find_all_by_location(AppConfig.noaa_location, :limit => 1, :order => "as_of desc")
    noaa_conditions = noaa_conditions[0]
    if noaa_conditions !=  nil
      @conditions = noaa_conditions.conditions
      @conditions_date = noaa_conditions.as_of.localtime
      @visibility = noaa_conditions.visibility
    end
  end

  def last_rain
    s = ArchiveRecord.find(:first, :conditions => "rainfall > 0 and location = #{AppConfig.location}", :limit => 1, :order => "date desc")
    if !s.nil?
      s[:date]
    else
      nil
    end
  end
  
  def get_current_conditions
    get_noaa_conditions
    @current = CurrentCondition.find_by_location(AppConfig.location)
    # kludge for time sync problems btw station time and web server
    @current.sample_date = Time.now if @current.sample_date > Time.now
    @today = WxPeriod.today_summary[0]
    if (@current.outside_temperature.to_f >= @today.hiTemp.to_f) 
      @highlo = "<br>(daily high)</br>"
    else
      if (@current.outside_temperature.to_f <= @today.lowTemp.to_f) 
        @highlo = "<br>(daily low)</br>"
      end
    end 
  end

  def current_conditions
    get_current_conditions
    render(:template => "wx/_current_conditions",
           :layout => false)
  end
  
end
