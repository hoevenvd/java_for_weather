class AddMetricsToCurrentConditions < ActiveRecord::Migration
  def self.up
    add_column :current_conditions, :outside_temperature_m, :float
    add_column :current_conditions, :inside_temperature_m, :float
    add_column :current_conditions, :dewpoint_m, :float
    add_column :current_conditions, :apparent_temp_m, :float
    add_column :current_conditions, :pressure_m, :float
    add_column :current_conditions, :windspeed_m, :float
    add_column :current_conditions, :rain_rate_m, :float
    add_column :current_conditions, :ten_min_avg_wind_m, :float
    add_column :current_conditions, :daily_rain_m, :float
    add_column :current_conditions, :monthly_rain_m, :float
    add_column :current_conditions, :yearly_rain_m, :float
    add_column :current_conditions, :storm_rain_m, :float
  end

  def self.down
    remove_column :current_conditions, :outside_temperature_m
    remove_column :current_conditions, :inside_temperature_m
    remove_column :current_conditions, :dewpoint_m
    remove_column :current_conditions, :apparent_temp_m
    remove_column :current_conditions, :pressure_m
    remove_column :current_conditions, :windspeed_m
    remove_column :current_conditions, :rain_rate_m
    remove_column :current_conditions, :ten_min_avg_wind_m
    remove_column :current_conditions, :daily_rain_m
    remove_column :current_conditions, :monthly_rain_m
    remove_column :current_conditions, :yearly_rain_m
    remove_column :current_conditions, :storm_rain_m
  end
end
