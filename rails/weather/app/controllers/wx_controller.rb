require 'noaa_forecast'

class WxController < ApplicationController
  include REXML

  def index
    periods
    get_current_conditions
    get_noaa_forecast
  end

  def get_noaa_forecast
    @forecast = NoaaForecast.latest(AppConfig.noaa_location)
  end
  
  def get_noaa_conditions
    noaa_conditions = NoaaConditions.latest(AppConfig.noaa_location)
    if noaa_conditions !=  nil
      @conditions = noaa_conditions.conditions
      @conditions_date = noaa_conditions.as_of.localtime
      @visibility = noaa_conditions.visibility
    end
  end

  def periods
    @today = WxPeriod.today_summary
    @this_hour = WxPeriod.this_hour_summary
    @this_week = WxPeriod.this_week_summary
    @this_month = WxPeriod.this_month_summary

    @yesterday = WxPeriod.yesterday_summary
    @last_hour = WxPeriod.last_hour_summary
    @last_week = WxPeriod.last_week_summary
    @last_month = WxPeriod.last_month_summary
  end

  def last_rain
    ArchiveRecord.last_rain_date(AppConfig.location)
  end
  
  def get_current_conditions
    get_noaa_conditions
    @current = CurrentCondition.find_by_location(AppConfig.location)
    # kludge for time sync problems btw station time and web server
    @current.sample_date = Time.now if @current.sample_date > Time.now
    @today = WxPeriod.today_summary
    if (@current.outside_temperature.to_f >= @today.hiTemp.to_f) 
      @highlo = "<br>(daily high)</br>"
    else
      if (@current.outside_temperature.to_f <= @today.lowTemp.to_f) 
        @highlo = "<br>(daily low)</br>"
      end
    end 
    @last_rain = last_rain
  end

  def current_conditions
    get_current_conditions
    render(:template => "wx/_current_conditions",
           :layout => false)
  end
  
  def period
    periods
    render(:template => "wx/_period",
           :layout => false)
  end
end
