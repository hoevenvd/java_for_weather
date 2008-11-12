class CreateOldArchiveRecords < ActiveRecord::Migration
  def self.up
    create_table :old_archive_records do |t|
      t.datetime "date",                         :null => false
      t.string   "location",                     :limit => 10, :default => "", :null => false
      t.decimal  "outside_temp",                 :precision => 4, :scale => 1
      t.decimal  "high_outside_temp",            :precision => 4, :scale => 1
      t.decimal  "low_outside_temp",             :precision => 4, :scale => 1
      t.float    "pressure"
      t.integer  "outside_humidity",             :limit => 6
      t.float    "rainfall"
      t.float    "high_rain_rate"
      t.integer  "average_wind_speed",           :limit => 6
      t.integer  "high_wind_speed",              :limit => 6
      t.integer  "direction_of_high_wind_speed", :limit => 6
      t.integer  "prevailing_wind_direction",    :limit => 6
      t.decimal  "inside_temp",                  :precision => 4, :scale => 1
      t.decimal  "average_dewpoint",             :precision => 4, :scale => 1
      t.integer  "average_apparent_temp",        :limit => 6
      t.integer  "inside_humidity",              :limit => 6
      t.integer  "average_solar_radiation",      :limit => 6
      t.integer  "average_uv_index",             :limit => 6
      t.integer  "et",                           :limit => 6
      t.integer  "high_solar_radiation",         :limit => 6
      t.integer  "high_uv_index",                :limit => 6
      t.integer  "forecastRule",                 :limit => 6
      t.integer  "leaf_temp_1",                  :limit => 6
      t.integer  "leaf_temp_2",                  :limit => 6
      t.integer  "leaf_wetness1",                :limit => 6
      t.integer  "leaf_wetness2",                :limit => 6
      t.integer  "soil_temp1",                   :limit => 6
      t.integer  "soil_temp2",                   :limit => 6
      t.integer  "soil_temp3",                   :limit => 6
      t.integer  "soil_temp4",                   :limit => 6
      t.integer  "extra_humidity1",              :limit => 6
      t.integer  "extra_humidity2",              :limit => 6
      t.integer  "extra_temp1",                  :limit => 6
      t.integer  "extra_temp2",                  :limit => 6
      t.integer  "extra_temp3",                  :limit => 6
      t.integer  "soil_moisture1",               :limit => 6
      t.integer  "soil_moisture2",               :limit => 6
      t.integer  "soil_moisture3",               :limit => 6
      t.integer  "soil_moisture4",               :limit => 6
      t.integer  "number_of_wind_samples",       :limit => 6
      t.integer  "download_record_type",         :limit => 6
      t.timestamps
    end

    add_index "old_archive_records", ["date", "location"], :name => "date_loc", :unique => true
    add_index "old_archive_records", ["date"], :name => "dates"
    add_index "old_archive_records", ["location"], :name => "locations"

  end

  def self.down
    drop_table :old_archive_records
  end
end
