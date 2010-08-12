class AddMetricsToArchiveRecords < ActiveRecord::Migration
  def self.up
    add_column :archive_records, :outside_temp_m, :float
    add_column :archive_records, :low_outside_temp_m, :float
    add_column :archive_records, :high_outside_temp_m, :float
    add_column :archive_records, :inside_temp_m, :float
    add_column :archive_records, :pressure_m, :float
    add_column :archive_records, :rainfall_m, :float
    add_column :archive_records, :high_rain_rate_m, :float
    add_column :archive_records, :average_wind_speed_m, :float
    add_column :archive_records, :high_wind_speed_m, :float
    add_column :archive_records, :average_dewpoint_m, :float
    add_column :archive_records, :average_apparent_temp_m, :float
  end

  def self.down
    remove_column :archive_records, :outside_temp_m
    remove_column :archive_records, :low_outside_temp_m
    remove_column :archive_records, :high_outside_temp_m
    remove_column :archive_records, :inside_temp_m
    remove_column :archive_records, :pressure_m
    remove_column :archive_records, :rainfall_m
    remove_column :archive_records, :high_rain_rate_m
    remove_column :archive_records, :average_wind_speed_m
    remove_column :archive_records, :high_wind_speed_m
    remove_column :archive_records, :average_dewpoint_m
    remove_column :archive_records, :average_apparent_temp_m
  end
end
