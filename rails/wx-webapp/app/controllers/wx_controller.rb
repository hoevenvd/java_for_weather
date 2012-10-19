require 'noaa_forecast'

class WxController < ApplicationController
  include REXML

  def index
    periods
    get_current_conditions
    get_noaa_forecast
    get_climate
    get_riseset
  end

  def get_noaa_forecast
    # @forecast != nil && @forecast.last_retrieved > 6.hours.ago
    @forecast = NoaaForecast.latest(AppConfig.noaa_location)
  end

  def get_climate
    c = Climate.find_by_location_and_month_and_day(AppConfig.climate_location,Time.now.localtime.month, Time.now.localtime.day)
    if !c.nil?
      @normal_high = c.avg_high_temp
      @normal_low = c.avg_low_temp
      @climate_available = true
    else
      @climate_available = false
    end
  end

  def get_riseset
    @riseset_today = Riseset.riseset(AppConfig.climate_location, Time.now)
    @riseset_available = !(@riseset_today.nil?)
    @riseset_week = Riseset.riseset(AppConfig.climate_location, Time.now + 1.week)
    @riseset_month = Riseset.riseset(AppConfig.climate_location, Time.now + 1.month)
  end

  def get_airport_conditions
    noaa_conditions = NoaaConditions.latest(AppConfig.noaa_location)
    if noaa_conditions ==  nil || noaa_conditions.as_of.localtime < 2.hours.ago
      noaa_conditions = WunderConditions.latest(AppConfig.noaa_location)
    end
    if (noaa_conditions != nil && noaa_conditions.as_of.localtime > 2.hours.ago)
      @conditions = noaa_conditions.conditions
      @conditions_date = noaa_conditions.as_of.localtime
      @visibility = noaa_conditions.visibility
    end
  end

  def periods
    @today = WxPeriod.today_summary(AppConfig.location)
    @this_hour = WxPeriod.this_hour_summary(AppConfig.location)
    @this_week = WxPeriod.this_week_summary(AppConfig.location)
    @this_month = WxPeriod.this_month_summary(AppConfig.location)

    @yesterday = WxPeriod.yesterday_summary(AppConfig.location)
    @last_hour = WxPeriod.last_hour_summary(AppConfig.location)
    @last_week = WxPeriod.last_week_summary(AppConfig.location)
    @last_month = WxPeriod.last_month_summary(AppConfig.location)
  end

  def last_rain
    l = LastRain.find_by_location(AppConfig.location)
    l.nil? ? nil : l.last_rain
  end
  
  def get_current_conditions
    @airport_conditions = get_airport_conditions # TODO nothing in @airport_conditions
    @current = CurrentCondition.find_by_location(AppConfig.location)
    @dark = Riseset.dark?(AppConfig.location, Time.now.utc)
    # kludge for time sync problems btw station time and web server
    @current.sample_date = Time.now if !@current.nil? and @current.sample_date > Time.now
    @today = WxPeriod.today_summary(AppConfig.location)
    if @today != nil
      if (@current.outside_temperature.to_f >= @today.hiTemp.to_f) 
        @highlo = "<br>(daily high)</br>"
      else
        if (@current.outside_temperature.to_f <= @today.lowTemp.to_f)
          @highlo = "<br>(daily low)</br>"
        end
      end
      @last_rain = last_rain
    end
    get_climate
    get_riseset
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
