class CurrentCondition < ActiveRecord::Base
  validates_uniqueness_of :location
  validates_presence_of   :location
  validates_inclusion_of  :outside_humidity, :in => 1..100, 
                          :allow_nil => true, :message => "invalid"

  def wind_str
    return "calm" if (self[:windspeed].eql?(0))
    return Direction.to_s(self[:wind_direction]) + " at " + windspeed.to_s + " mph"
  end
  
  def wind
  end

  def gust
    start_tm = 10.minutes.ago.utc
    ArchiveRecord.maximum(:high_wind_speed, :conditions => "date > \'#{start_tm.to_s(:db)}\'")
  end

  def hourly_rain
    start_tm = 1.hour.ago.utc
    ArchiveRecord.sum(:rainfall, :conditions => "date > \'#{start_tm.to_s(:db)}\'")
  end

  def temp_trend
    if trend_record == nil or outside_temperature == trend_record.outside_temp then
      return nil
    else 
      outside_temperature > trend_record.outside_temp ? "Rising" : "Falling"
    end
  end
          
  protected
    
  def trend_record
    bt = Time.now - 2.hours
    et = Time.now - 1.hour
    @trend_record = ArchiveRecord.find(:first,:conditions => "date > \'#{bt.to_s(:db)} and date < #{et.to_s(:db)}\'", :order => 'date') unless @trend_record != nil
    @trend_record
  end

  def before_save    
    if  !(outside_temperature.nil? || outside_humidity.nil?)
      self.dewpoint = Round.round_f(WxHelper.dewpoint(outside_temperature, 
                                outside_humidity), 1)
    end
                                  
    if  !(outside_temperature.nil? || outside_humidity.nil? || windspeed.nil?)
      self.apparent_temp = Round.round_f(WxHelper.apparent_temp(outside_temperature, 
                                outside_humidity, windspeed), 1)
    end

    if (!rain_rate.nil? && rain_rate > 0.0)
      self.is_raining = true
    else
      self.is_raining = false
    end
    return true

  end
                            
  def validate
    errors.add(:pressure, "invalid") if !pressure.nil? && 
         (pressure < 27 || pressure > 32)
  end
end
