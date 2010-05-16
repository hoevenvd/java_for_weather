class ArchiveRecord < ActiveRecord::Base
  include WxUtils

  validates_presence_of   :date
  validates_presence_of   :location
  validates_length_of     :location, :maximum => 30
  validates_inclusion_of  :prevailing_wind_direction, :in => 0..360,
                          :allow_nil => true, :message => "invalid prevailing wind direction"
  validates_inclusion_of  :direction_of_high_wind_speed, :in => 0..360,
                          :allow_nil => true, :message => "invalid high wind speed direction"
  validates_inclusion_of  :outside_humidity, :in => 0..100,
                          :allow_nil => true, :message => "invalid outside humidity"
  validates_inclusion_of  :inside_humidity, :in => 0..100,
                          :allow_nil => true, :message => "invalid inside humidity"
  validates_inclusion_of  :pressure, :in => 25..35,
                          :allow_nil => true, :message => "invalid pressure"

  named_scope :really_old, lambda {{:conditions => ["date < ? and location = \'#{AppConfig.location}\'", Time.now.utc.at_beginning_of_year - 1.year]}}
  named_scope :last_year, lambda {{:conditions => ["date < ? and date >= ? and location = \'#{AppConfig.location}\'",
                                   Time.now.utc.at_beginning_of_year,
                                   Time.now.utc.at_beginning_of_year - 1.year],
                                   :order => "date desc"}}
  named_scope :last_rain, lambda { |location| {:conditions => ["rainfall > 0 and location = \'#{AppConfig.location}\'", location],
                            :limit => 1, :order => "date desc"} }

  def before_save    
    if  !(outside_temp.nil? || outside_humidity.nil?)
      self.average_dewpoint = Round.round_f(calc_dewpoint(outside_temp,
                                outside_humidity), 1)
      self.average_dewpoint_m = Round.round_f(to_c(self.average_dewpoint), 1)
    else
      self.average_dewpoint = self.average_dewpoint_m = nil
    end
                                  
    if  !(outside_temp.nil? || outside_humidity.nil?)
      self.average_apparent_temp = Round.round_f(calc_apparent_temp(outside_temp,
                                outside_humidity, average_wind_speed), 1)
      self.average_apparent_temp_m = Round.round_f(to_c(self.average_apparent_temp), 1)
    else
      self.average_apparent_temp = self.average_apparent_temp_m = nil
    end

    self.outside_temp_m = Round.round_f(to_c(self.outside_temp), 1) unless self.outside_temp.nil?
    self.low_outside_temp_m = Round.round_f(to_c(self.low_outside_temp), 1) unless self.low_outside_temp.nil?
    self.high_outside_temp_m = Round.round_f(to_c(self.high_outside_temp), 1) unless self.high_outside_temp.nil?
    self.inside_temp_m = Round.round_f(to_c(self.inside_temp), 1) unless self.inside_temp.nil?

    self.pressure_m = Round.round_f(inches_of_hg_to_mb(self.pressure), 1) unless self.pressure.nil?
    self.rainfall_m = Round.round_f(inches_to_mm(self.rainfall), 1) unless self.rainfall.nil?
    self.high_rain_rate_m = Round.round_f(inches_to_mm(self.high_rain_rate), 1) unless self.high_rain_rate.nil?
    self.average_wind_speed_m = Round.round_f(mph_to_mps(self.average_wind_speed), 1) unless self.average_wind_speed.nil?
    self.high_wind_speed_m = Round.round_f(mph_to_mps(self.high_wind_speed), 1) unless self.high_wind_speed.nil?
 end

  def self.last_rain_date(location)
    last = ArchiveRecord.last_rain(location)[0]
    last == nil ? nil : last.date
  end
  
  def self.last_rolling_hour_rain
    start_tm = 1.hour.ago
    rain = ArchiveRecord.sum(:rainfall, :conditions => "date > \'#{start_tm.to_s(:db)}\' and location = \'#{location}\'")
  end
                            
end
