class ArchiveRecord < ActiveRecord::Base
  validates_presence_of   :date
  validates_presence_of   :location
  validates_inclusion_of  :prevailing_wind_direction, :in => 0..359, 
                          :allow_nil => true, :message => "invalid"
  validates_inclusion_of  :direction_of_high_wind_speed, :in => 0..359, 
                          :allow_nil => true, :message => "invalid"
  validates_inclusion_of  :outside_humidity, :in => 1..100, 
                          :allow_nil => true, :message => "invalid"

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
                            
end
