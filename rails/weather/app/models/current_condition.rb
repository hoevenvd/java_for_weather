class CurrentCondition < ActiveRecord::Base
  validates_uniqueness_of :location
  validates_presence_of   :location
  validates_length_of     :location, :maximum => 30
  validates_inclusion_of  :outside_humidity, :in => 1..100,
                          :allow_nil => true, :message => "invalid outside humidity"
  validates_inclusion_of  :inside_humidity, :in => 1..100,
                          :allow_nil => true, :message => "invalid inside humidity"
  validates_inclusion_of  :pressure, :in => 25..35,
                          :allow_nil => true, :message => "invalid pressure"
  validates_inclusion_of  :wind_direction, :in => 0..360,
                          :allow_nil => true, :message => "invalid wind direction"

  def wind_str
    return "unavailable" if self[:windspeed].nil?
    return "calm" if (self[:windspeed].eql?(0))
    return Direction.to_s(self[:wind_direction]) + " at " + windspeed.to_s + " mph"
  end
  
  def wind
  end

#  class Event
#    read_attr :date, :value
#    def initialize(date, value)
#      @date = date
#      @value = value
#    end
#  end

  def gust
    start_tm = 10.minutes.ago.utc
    value = ArchiveRecord.maximum(:high_wind_speed, :conditions => "date > \'#{start_tm.to_s(:db)}\' and location = \'#{location}\'")
    value #todo build an Event object here
  end

  def gust_time
    a = ArchiveRecord.find(:first, :conditions => {:high_wind_speed => :gust, :location => location}, :order => "date desc")
    a.date
  end

  def twentyfour_hour_rain
    start_tm = 24.hours.ago.utc
    ArchiveRecord.sum(:rainfall, :conditions => "date >= \'#{start_tm.to_s(:db)}\' and location = \'#{location}\'")
  end

  def hourly_rain
    start_tm = 1.hour.ago.utc
    ArchiveRecord.sum(:rainfall, :conditions => "date >= \'#{start_tm.to_s(:db)}\' and location = \'#{location}\'")
  end

  def temp_trend
    if trend_record == nil or outside_temperature == trend_record.outside_temp then
      return nil
    else 
      outside_temperature > trend_record.outside_temp ? "Rising" : "Falling"
    end
  end
          
  def dewpoint_trend
    if trend_record == nil or dewpoint == trend_record.average_dewpoint then
      return nil
    else 
      dewpoint > trend_record.average_dewpoint ? "Rising" : "Falling"
    end
  end
          
  protected
    
  def trend_record
    bt = Time.now - 2.hours
    et = Time.now - 1.hour
    @trend_record = ArchiveRecord.find(:first,:conditions => "date > \'#{bt.utc.to_s(:db)} and date < #{et.utc.to_s(:db)}\' and location = \'#{location}\'", :order => 'date') unless @trend_record != nil
    @trend_record
  end

  def before_save
    # calculate metric and english dewpoints
    if  !outside_temperature.nil? and !outside_humidity.nil?
      dp = Round.round_f(WxHelper.dewpoint(outside_temperature,
        outside_humidity), 1)
      self.dewpoint = dp
      self.dewpoint_m = Round.round_f(WxHelper.to_c(dp), 1)
    else
      self.dewpoint = self.dewpoint_m = nil
    end
                                  
    if  !outside_temperature.nil? and !outside_humidity.nil?
      at = Round.round_f(WxHelper.apparent_temp(outside_temperature,
                                outside_humidity, windspeed), 1)
      self.apparent_temp = at
      self.apparent_temp_m = Round.round_f(WxHelper.to_c(at), 1)
    else
      self.apparent_temp = self.apparent_temp_m = nil
    end

    if rain_rate.nil?
      self.is_raining = nil
    elsif rain_rate > 0.0
      self.is_raining = true
    else
      self.is_raining = false
    end

    self.windspeed_m = Round.round_f(WxHelper.mph_to_mps(self.windspeed), 1) unless self.windspeed.nil?
    self.ten_min_avg_wind_m = Round.round_f(WxHelper.mph_to_mps(self.ten_min_avg_wind),1) unless self.ten_min_avg_wind.nil?
    self.rain_rate_m = Round.round_f(WxHelper.inches_to_mm(self.rain_rate), 2) unless self.rain_rate.nil?
    self.daily_rain_m = Round.round_f(WxHelper.inches_to_mm(self.daily_rain), 2) unless self.daily_rain.nil?
    self.monthly_rain_m = Round.round_f(WxHelper.inches_to_mm(self.monthly_rain), 2) unless self.monthly_rain.nil?
    self.yearly_rain_m = Round.round_f(WxHelper.inches_to_mm(self.yearly_rain), 2) unless self.yearly_rain.nil?
    self.storm_rain_m = Round.round_f(WxHelper.inches_to_mm(self.storm_rain), 2) unless self.storm_rain.nil?
    self.pressure_m = Round.round_f(WxHelper.inches_of_hg_to_mb(self.pressure), 1) unless self.pressure.nil?
    self.outside_temperature_m = Round.round_f(WxHelper.to_c(self.outside_temperature), 1) unless self.outside_temperature.nil?
    self.inside_temperature_m = Round.round_f(WxHelper.to_c(self.inside_temperature), 1) unless self.inside_temperature.nil?
    
    return true
  end
end
