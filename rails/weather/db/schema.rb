# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20091124231454) do

  create_table "archive_records", :force => true do |t|
    t.datetime "date",                                                                                     :null => false
    t.string   "location",                     :limit => 10,                               :default => "", :null => false
    t.decimal  "outside_temp",                               :precision => 4, :scale => 1
    t.decimal  "high_outside_temp",                          :precision => 4, :scale => 1
    t.decimal  "low_outside_temp",                           :precision => 4, :scale => 1
    t.float    "pressure"
    t.integer  "outside_humidity"
    t.float    "rainfall"
    t.float    "high_rain_rate"
    t.integer  "average_wind_speed"
    t.integer  "high_wind_speed"
    t.integer  "direction_of_high_wind_speed"
    t.integer  "prevailing_wind_direction"
    t.decimal  "inside_temp",                                :precision => 4, :scale => 1
    t.decimal  "average_dewpoint",                           :precision => 4, :scale => 1
    t.integer  "average_apparent_temp"
    t.integer  "inside_humidity"
    t.integer  "average_solar_radiation",      :limit => 8
    t.integer  "average_uv_index",             :limit => 8
    t.integer  "et",                           :limit => 8
    t.integer  "high_solar_radiation",         :limit => 8
    t.integer  "high_uv_index",                :limit => 8
    t.integer  "forecastRule",                 :limit => 8
    t.integer  "leaf_temp_1",                  :limit => 8
    t.integer  "leaf_temp_2",                  :limit => 8
    t.integer  "leaf_wetness1",                :limit => 8
    t.integer  "leaf_wetness2",                :limit => 8
    t.integer  "soil_temp1",                   :limit => 8
    t.integer  "soil_temp2",                   :limit => 8
    t.integer  "soil_temp3",                   :limit => 8
    t.integer  "soil_temp4",                   :limit => 8
    t.integer  "extra_humidity1",              :limit => 8
    t.integer  "extra_humidity2",              :limit => 8
    t.integer  "extra_temp1",                  :limit => 8
    t.integer  "extra_temp2",                  :limit => 8
    t.integer  "extra_temp3",                  :limit => 8
    t.integer  "soil_moisture1",               :limit => 8
    t.integer  "soil_moisture2",               :limit => 8
    t.integer  "soil_moisture3",               :limit => 8
    t.integer  "soil_moisture4",               :limit => 8
    t.integer  "number_of_wind_samples",       :limit => 8
    t.integer  "download_record_type",         :limit => 8
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "archive_records", ["date", "location"], :name => "date_loc", :unique => true
  add_index "archive_records", ["date"], :name => "dates"
  add_index "archive_records", ["location"], :name => "locations"

  create_table "climates", :force => true do |t|
    t.string   "location",                           :null => false
    t.integer  "month",                 :limit => 8, :null => false
    t.integer  "day",                   :limit => 8, :null => false
    t.integer  "avg_high_temp",         :limit => 8
    t.integer  "avg_low_temp",          :limit => 8
    t.integer  "mean_temp",             :limit => 8
    t.integer  "record_high_temp",      :limit => 8
    t.integer  "record_high_temp_year", :limit => 8
    t.integer  "record_low_temp",       :limit => 8
    t.integer  "record_low_temp_year",  :limit => 8
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "climates", ["day"], :name => "index_climates_on_day"
  add_index "climates", ["location"], :name => "index_climates_on_location"
  add_index "climates", ["month"], :name => "index_climates_on_month"

  create_table "current_conditions", :force => true do |t|
    t.string   "location",            :limit => 10,                               :default => "", :null => false
    t.datetime "sample_date"
    t.decimal  "outside_temperature",               :precision => 4, :scale => 1
    t.integer  "outside_humidity"
    t.decimal  "dewpoint",                          :precision => 4, :scale => 1
    t.integer  "apparent_temp"
    t.float    "pressure"
    t.string   "bar_status",          :limit => 25
    t.integer  "windspeed"
    t.integer  "wind_direction"
    t.boolean  "is_raining"
    t.float    "rain_rate"
    t.integer  "ten_min_avg_wind"
    t.integer  "uv"
    t.integer  "solar_radiation"
    t.float    "daily_rain"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "current_conditions", ["location"], :name => "index_current_conditions_on_location"

  create_table "forecast_periods", :force => true do |t|
    t.integer  "noaa_forecast_id", :limit => 8,  :default => 0,  :null => false
    t.string   "name",             :limit => 20, :default => "", :null => false
    t.text     "text"
    t.datetime "created_at"
    t.string   "icon_location",                  :default => "", :null => false
    t.datetime "updated_at"
    t.integer  "temp",             :limit => 8
    t.text     "weather"
    t.integer  "pop",              :limit => 8
  end

  add_index "forecast_periods", ["noaa_forecast_id"], :name => "noaa_forecast_id"

  create_table "noaa_conditions", :force => true do |t|
    t.datetime "created_at"
    t.string   "location",       :limit => 20, :default => "", :null => false
    t.datetime "updated_at"
    t.text     "conditions",                                   :null => false
    t.datetime "as_of"
    t.integer  "visibility",     :limit => 8
    t.text     "conditions_xml"
  end

  add_index "noaa_conditions", ["created_at"], :name => "created_at"
  add_index "noaa_conditions", ["location"], :name => "location"

  create_table "noaa_forecasts", :force => true do |t|
    t.text     "forecast_xml"
    t.datetime "created_at"
    t.string   "location",       :limit => 20, :default => "", :null => false
    t.datetime "updated_at"
    t.datetime "creation_time"
    t.datetime "last_retrieved"
  end

  add_index "noaa_forecasts", ["created_at"], :name => "created_at"
  add_index "noaa_forecasts", ["location"], :name => "location"

  create_table "old_archive_records", :force => true do |t|
    t.datetime "date",                                                                                     :null => false
    t.string   "location",                     :limit => 10,                               :default => "", :null => false
    t.decimal  "outside_temp",                               :precision => 4, :scale => 1
    t.decimal  "high_outside_temp",                          :precision => 4, :scale => 1
    t.decimal  "low_outside_temp",                           :precision => 4, :scale => 1
    t.float    "pressure"
    t.integer  "outside_humidity"
    t.float    "rainfall"
    t.float    "high_rain_rate"
    t.integer  "average_wind_speed"
    t.integer  "high_wind_speed"
    t.integer  "direction_of_high_wind_speed"
    t.integer  "prevailing_wind_direction"
    t.decimal  "inside_temp",                                :precision => 4, :scale => 1
    t.decimal  "average_dewpoint",                           :precision => 4, :scale => 1
    t.integer  "average_apparent_temp"
    t.integer  "inside_humidity"
    t.integer  "average_solar_radiation"
    t.integer  "average_uv_index"
    t.integer  "et"
    t.integer  "high_solar_radiation"
    t.integer  "high_uv_index"
    t.integer  "forecastRule"
    t.integer  "leaf_temp_1"
    t.integer  "leaf_temp_2"
    t.integer  "leaf_wetness1"
    t.integer  "leaf_wetness2"
    t.integer  "soil_temp1"
    t.integer  "soil_temp2"
    t.integer  "soil_temp3"
    t.integer  "soil_temp4"
    t.integer  "extra_humidity1"
    t.integer  "extra_humidity2"
    t.integer  "extra_temp1"
    t.integer  "extra_temp2"
    t.integer  "extra_temp3"
    t.integer  "soil_moisture1"
    t.integer  "soil_moisture2"
    t.integer  "soil_moisture3"
    t.integer  "soil_moisture4"
    t.integer  "number_of_wind_samples"
    t.integer  "download_record_type"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "old_archive_records", ["date", "location"], :name => "date_loc", :unique => true
  add_index "old_archive_records", ["date"], :name => "dates"
  add_index "old_archive_records", ["location"], :name => "locations"

  create_table "past_summaries", :force => true do |t|
    t.string   "period"
    t.decimal  "avgDewpoint",                      :precision => 6, :scale => 1
    t.integer  "avgHumidity",        :limit => 8
    t.decimal  "avgPressure",                      :precision => 6, :scale => 2
    t.decimal  "avgTemp",                          :precision => 6, :scale => 1
    t.integer  "avgWindspeed",       :limit => 8
    t.integer  "avgWindchill",       :limit => 8
    t.integer  "hiDewpoint",         :limit => 8
    t.integer  "hiWindspeed",        :limit => 8
    t.integer  "hiHumidity",         :limit => 8
    t.decimal  "hiPressure",                       :precision => 6, :scale => 1
    t.decimal  "hiTemp",                           :precision => 6, :scale => 1
    t.integer  "hiWindchill",        :limit => 8
    t.integer  "lowDewpoint",        :limit => 8
    t.integer  "lowOutsideHumidity", :limit => 8
    t.decimal  "lowPressure",                      :precision => 6, :scale => 1
    t.decimal  "lowTemp",                          :precision => 6, :scale => 1
    t.integer  "lowWindchill",       :limit => 8
    t.decimal  "rain",                             :precision => 6, :scale => 2
    t.datetime "hiTempDate"
    t.datetime "lowTempDate"
    t.datetime "gustDate"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.datetime "startdate"
    t.datetime "enddate"
    t.integer  "degreeDays",         :limit => 8
    t.string   "location",           :limit => 30,                               :null => false
  end

  add_index "past_summaries", ["location", "period"], :name => "index_past_summaries_on_location_and_period", :unique => true
  add_index "past_summaries", ["period"], :name => "index_past_summaries_on_period", :unique => true

  create_table "risesets", :force => true do |t|
    t.string  "location",              :null => false
    t.integer "month",    :limit => 8, :null => false
    t.integer "day",      :limit => 8, :null => false
    t.time    "rise"
    t.time    "set"
  end

  add_index "risesets", ["day"], :name => "index_risesets_on_day"
  add_index "risesets", ["location"], :name => "index_risesets_on_location"
  add_index "risesets", ["month"], :name => "index_risesets_on_month"

end
