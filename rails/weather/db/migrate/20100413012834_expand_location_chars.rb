class ExpandLocationChars < ActiveRecord::Migration
  def self.up
    change_column :current_conditions, :location, :string, :limit => 30, :null => false
    change_column :archive_records, :location, :string, :limit => 30, :null => false
    change_column :climates, :location, :string, :limit => 30, :null => false
    change_column :noaa_conditions, :location, :string, :limit => 30, :null => false
    change_column :noaa_forecasts, :location, :string, :limit => 30, :null => false
    change_column :old_archive_records, :location, :string, :limit => 30, :null => false
    change_column :risesets, :location, :string, :limit => 30, :null => false
  end

  def self.down
    change_column :current_conditions, :location, :string, :limit => 10, :null => false
    change_column :archive_records, :location, :string, :limit => 10, :null => false
    change_column :climates, :location, :string, :null => false
    change_column :noaa_conditions, :location, :string, :limit => 20, :null => false
    change_column :noaa_forecasts, :location, :string, :limit => 20, :null => false
    change_column :old_archive_records, :location, :string, :limit => 10, :null => false
    change_column :risesets, :location, :string, :null => false
  end
end
