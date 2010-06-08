class ChangePressureToDecimal < ActiveRecord::Migration
  def self.up
    change_column(:archive_records, :pressure, :decimal, :precision => 5, :scale => 3)
  end

  def self.down
    change_column(:archive_records, :pressure, :float)
  end
end


#current_conditions

#rain_rate
#inside_temperature
#daily_rain
#monthly_rain
#yearly_rain
#storm_rain
#monthly_rain
##yearly_rain
#ten_min_avg_wind_m
#rain_rate_m
#windspeed_m
#pressure_m
#apparent_temp-m
#dewpoint_m
#inside_temperature_m
#outside_temperature_m
#daily_rain_m
#monthly_rain_m
#yearly_rain_m
#storm_rain_m
