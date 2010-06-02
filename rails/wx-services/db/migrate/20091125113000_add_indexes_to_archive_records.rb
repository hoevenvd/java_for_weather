class AddIndexesToArchiveRecords < ActiveRecord::Migration
  def self.up
    add_index :archive_records, [:location, :date, :high_outside_temp]
    add_index :archive_records, [:location, :date, :low_outside_temp]
    add_index :archive_records, [:location, :date, :high_wind_speed]
    add_index :archive_records, [:location, :date, :rainfall]
  end

  def self.down
    remove_index :archive_records, [:location, :date, :high_outside_temp]
    remove_index :archive_records, [:location, :date, :low_outside_temp]
    remove_index :archive_records, [:location, :date, :high_wind_speed]
    remove_index :archive_records, [:location, :date, :rainfall]
  end
end
