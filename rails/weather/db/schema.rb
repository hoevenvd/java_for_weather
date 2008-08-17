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

ActiveRecord::Schema.define(:version => 0) do

  create_table "archive_records", :force => true do |t|
    t.datetime "date",                                                                                     :null => false
    t.string   "location",                     :limit => 10,                               :default => "", :null => false
    t.decimal  "outside_temp",                               :precision => 4, :scale => 1
    t.decimal  "high_outside_temp",                          :precision => 4, :scale => 1
    t.decimal  "low_outside_temp",                           :precision => 4, :scale => 1
    t.float    "pressure",                     :limit => 5
    t.integer  "outside_humidity",             :limit => 6
    t.float    "rainfall",                     :limit => 5
    t.float    "high_rain_rate",               :limit => 6
    t.integer  "average_wind_speed",           :limit => 6
    t.integer  "high_wind_speed",              :limit => 6
    t.integer  "direction_of_high_wind_speed", :limit => 6
    t.integer  "prevailing_wind_direction",    :limit => 6
    t.decimal  "inside_temp",                                :precision => 4, :scale => 1
    t.decimal  "average_dewpoint",                           :precision => 4, :scale => 1
    t.integer  "average_apparent_temp",        :limit => 6
    t.integer  "inside_humidity",              :limit => 6
    t.integer  "solar_radiation",              :limit => 11
    t.integer  "average_uv_index",             :limit => 11
    t.integer  "et",                           :limit => 11
    t.integer  "high_solar_radation",          :limit => 11
    t.integer  "high_uv_index",                :limit => 11
    t.integer  "forecastRule",                 :limit => 11
    t.integer  "leaf_temp_1",                  :limit => 11
    t.integer  "leaf_temp_2",                  :limit => 11
    t.integer  "leaf_wetness1",                :limit => 11
    t.integer  "leaf_wetness2",                :limit => 11
    t.integer  "soil_temp1",                   :limit => 11
    t.integer  "soil_temp2",                   :limit => 11
    t.integer  "soil_temp3",                   :limit => 11
    t.integer  "soil_temp4",                   :limit => 11
    t.integer  "extra_humidity1",              :limit => 11
    t.integer  "extra_humidity2",              :limit => 11
    t.integer  "extra_temp1",                  :limit => 11
    t.integer  "extra_temp2",                  :limit => 11
    t.integer  "extra_temp3",                  :limit => 11
    t.integer  "soil_moisture1",               :limit => 11
    t.integer  "soil_moisture2",               :limit => 11
    t.integer  "soil_moisture3",               :limit => 11
    t.integer  "soil_moisture4",               :limit => 11
    t.integer  "number_of_wind_samples",       :limit => 11
    t.integer  "download_record_type",         :limit => 11
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "archive_records", ["date", "location"], :name => "date_loc", :unique => true
  add_index "archive_records", ["date"], :name => "dates"
  add_index "archive_records", ["location"], :name => "locations"

  create_table "current_conditions", :force => true do |t|
    t.string   "location",            :limit => 10,                               :default => "", :null => false
    t.datetime "sample_date"
    t.decimal  "outside_temperature",               :precision => 4, :scale => 1
    t.integer  "outside_humidity",    :limit => 6
    t.decimal  "dewpoint",                          :precision => 4, :scale => 1
    t.integer  "apparent_temp",       :limit => 6
    t.float    "pressure",            :limit => 5
    t.string   "bar_status",          :limit => 25
    t.integer  "windspeed",           :limit => 6
    t.integer  "wind_direction",      :limit => 6
    t.boolean  "is_raining"
    t.float    "rain_rate",           :limit => 5
    t.integer  "ten_min_avg_wind",    :limit => 6
    t.integer  "uv",                  :limit => 6
    t.integer  "solar_radiation",     :limit => 6
    t.float    "daily_rain",          :limit => 5
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "forecast_periods", :force => true do |t|
    t.integer  "noaa_forecast_id", :limit => 11, :default => 0,  :null => false
    t.string   "name",             :limit => 20, :default => "", :null => false
    t.text     "text"
    t.datetime "created_at"
    t.string   "icon_location",                  :default => "", :null => false
    t.datetime "updated_at"
    t.integer  "temp",             :limit => 11
    t.text     "weather"
    t.integer  "pop",              :limit => 11
  end

  add_index "forecast_periods", ["noaa_forecast_id"], :name => "noaa_forecast_id"

  create_table "noaa_conditions", :force => true do |t|
    t.datetime "created_at"
    t.string   "location",       :limit => 20, :default => "", :null => false
    t.datetime "updated_at"
    t.text     "conditions",                                   :null => false
    t.datetime "as_of"
    t.integer  "visibility",     :limit => 11
    t.text     "conditions_xml"
  end

  add_index "noaa_conditions", ["created_at"], :name => "created_at"
  add_index "noaa_conditions", ["location"], :name => "location"

  create_table "noaa_forecasts", :force => true do |t|
    t.text     "forecast_xml",                               :null => false
    t.datetime "created_at"
    t.string   "location",     :limit => 20, :default => "", :null => false
    t.datetime "updated_at"
  end

  add_index "noaa_forecasts", ["created_at"], :name => "created_at"
  add_index "noaa_forecasts", ["location"], :name => "location"

  create_table "schema_info", :id => false, :force => true do |t|
    t.integer "version", :limit => 11
  end

end
