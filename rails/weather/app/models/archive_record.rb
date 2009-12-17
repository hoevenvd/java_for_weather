class ArchiveRecord < ActiveRecord::Base
  validates_presence_of   :date
  validates_presence_of   :location
  validates_inclusion_of  :prevailing_wind_direction, :in => 0..359, 
                          :allow_nil => true, :message => "invalid"
  validates_inclusion_of  :direction_of_high_wind_speed, :in => 0..359, 
                          :allow_nil => true, :message => "invalid"
  validates_inclusion_of  :outside_humidity, :in => 1..100, 
                          :allow_nil => true, :message => "invalid"

  named_scope :really_old, lambda {{:conditions => ["date < ? and location = \'#{AppConfig.location}\'", Time.now.utc.at_beginning_of_year - 1.year]}}
  named_scope :last_year, lambda {{:conditions => ["date < ? and date >= ? and location = \'#{AppConfig.location}\'",
                                   Time.now.utc.at_beginning_of_year,
                                   Time.now.utc.at_beginning_of_year - 1.year],
                                   :order => "date desc"}}
  named_scope :last_rain, lambda { |location| {:conditions => ["rainfall > 0 and location = #{AppConfig.location}", location], 
                            :limit => 1, :order => "date desc"} }

  def before_save    
    if  !(outside_temp.nil? || outside_humidity.nil?)
      self.average_dewpoint = Round.round_f(WxHelper.dewpoint(outside_temp, 
                                outside_humidity), 1)
    end
                                  
    if  !(outside_temp.nil? || outside_humidity.nil? || average_wind_speed.nil?)
      self.average_apparent_temp = Round.round_f(WxHelper.apparent_temp(outside_temp, 
                                outside_humidity, average_wind_speed), 1)
    end
  end

  def validate
    errors.add(:pressure, "invalid") if !pressure.nil? && 
         (pressure < 27 || pressure > 32)
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
