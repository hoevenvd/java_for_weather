class AddMetricsToOldArchiveRecords < ActiveRecord::Migration
  def self.up
    change_column(:old_archive_records, :pressure, :decimal, :precision => 5, :scale => 3)

    add_column :old_archive_records, :outside_temp_m, :float
    add_column :old_archive_records, :low_outside_temp_m, :float
    add_column :old_archive_records, :high_outside_temp_m, :float
    add_column :old_archive_records, :inside_temp_m, :float
    add_column :old_archive_records, :pressure_m, :float
    add_column :old_archive_records, :rainfall_m, :float
    add_column :old_archive_records, :high_rain_rate_m, :float
    add_column :old_archive_records, :average_wind_speed_m, :float
    add_column :old_archive_records, :high_wind_speed_m, :float
    add_column :old_archive_records, :average_dewpoint_m, :float
    add_column :old_archive_records, :average_apparent_temp_m, :float

    add_index :old_archive_records, :pressure
    add_index :old_archive_records, :average_apparent_temp
    add_index :old_archive_records, :average_dewpoint
    add_index :old_archive_records, :outside_humidity
  end

  def self.down
    change_column(:old_archive_records, :pressure, :float)

    remove_column :old_archive_records, :outside_temp_m
    remove_column :old_archive_records, :low_outside_temp_m
    remove_column :old_archive_records, :high_outside_temp_m
    remove_column :old_archive_records, :inside_temp_m
    remove_column :old_archive_records, :pressure_m
    remove_column :old_archive_records, :rainfall_m
    remove_column :old_archive_records, :high_rain_rate_m
    remove_column :old_archive_records, :average_wind_speed_m
    remove_column :old_archive_records, :high_wind_speed_m
    remove_column :old_archive_records, :average_dewpoint_m
    remove_column :old_archive_records, :average_apparent_temp_m

    remove_index :old_archive_records, :pressure
    remove_index :old_archive_records, :average_apparent_temp
    remove_index :old_archive_records, :average_dewpoint
    remove_index :old_archive_records, :outside_humidity
  end
end
