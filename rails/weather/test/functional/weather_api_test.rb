require File.dirname(__FILE__) + '/../test_helper'
require 'weather_controller'

#  class WeatherController
#    def rescue_action(e)
#      raise e
#    end
#  end


class WeatherController; def rescue_action(e) raise e end; end

class WeatherControllerApiTest < ActiveSupport::TestCase
  fixtures :risesets
  
  def setup
    @controller = WeatherController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def write_and_read_archive_record(a)
    invoke :put_archive_entry, AppConfig.service_password, AppConfig.location, a
    ArchiveRecord.find_by_date_and_location(a[:date], AppConfig.location)
  end


  def create_a_good_struct
    my_struct = InputSampleStruct.new(
       :sample_date => Time.now,
       :temp => 32.2,
       :windspeed => 2,
       :humidity => 45,
       :pressure => 30.001,
       :bar_status => "rising",
       :rain_rate => 1.01,
       :ten_min_avg_wind => 2,
       :wind_direction => 270,
       :daily_rain => 2.02,
       :uv => 0,
       :solar_radiation => 555)
     return my_struct
  end

  def create_a_minimal_struct
    my_struct = InputSampleStruct.new(
       :sample_date => Time.now)
  end

  def create_a_bad_struct
    my_struct = InputSampleStruct.new(
       :sample_date => Time.now,
       :temp => 32.2,
       :windspeed => 2,
       :humidity => 145, # bad value
       :pressure => 32.001,
       :bar_status => "rising",
       :ten_min_avg_wind => 2,
       :rain_rate => 1.01,
       :wind_direction => 1270) # bad value
  end

  def create_complete_archive_struct
    a = ArchiveStruct.new(
      :date => Time.now.getutc,
      :outside_temp => 43.3,
      :high_outside_temp => 43.6,
      :low_outside_temp => 43.1,
      :pressure => 30.002,
      :outside_humidity => 51,
      :rainfall => 1.15,
      :high_rain_rate => 0.25,
      :average_wind_speed => 12,
      :high_wind_speed => 23,
      :direction_of_high_wind_speed => 235,
      :prevailing_wind_direction => 271,
      :inside_temp => 68,
      :inside_humidity => 45,
      :number_of_wind_samples => 20,
      :average_uv_index => 75,
      :high_uv_index => 99,
      :average_solar_radiation => 450,
      :high_solar_radiation => 500)

  end

  def create_minimal_archive_struct
    a = ArchiveStruct.new(:date => Time.now.getutc)
  end

############## unit tests here ####################

  def test_archive_outside_temp
    a = create_complete_archive_struct
    a.outside_temp = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:outside_temp].nil?
    assert res[:outside_temp_m].nil?
  end

  def test_archive_high_outside_temp
    a = create_complete_archive_struct
    a.high_outside_temp = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:high_outside_temp].nil?
    assert res[:high_outside_temp_m].nil?
  end

  def test_archive_low_outside_temp
    a = create_complete_archive_struct
    a.low_outside_temp = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:low_outside_temp].nil?
    assert res[:low_outside_temp_m].nil?
  end

  def test_archive_outside_humidity
    a = create_complete_archive_struct
    a.outside_humidity = -9999
    res = write_and_read_archive_record(a)
    assert res[:outside_humidity].nil?
  end

  def test_archive_rainfall
    a = create_complete_archive_struct
    a.rainfall = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:rainfall].nil?
    assert res[:rainfall_m].nil?
  end

  def test_archive_high_rain_rate
    a = create_complete_archive_struct
    a.high_rain_rate = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:high_rain_rate].nil?
    assert res[:high_rain_rate_m].nil?
  end

  def test_archive_average_wind_speed
    a = create_complete_archive_struct
    a.average_wind_speed = -9999
    res = write_and_read_archive_record(a)
    assert res[:average_wind_speed].nil?
    assert res[:average_wind_speed_m].nil?
    assert res[:prevailing_wind_direction].nil?
  end

  def test_archive_prevailing_wind_direction
    a = create_complete_archive_struct
    a.prevailing_wind_direction = -9999
    res = write_and_read_archive_record(a)
    assert res[:prevailing_wind_direction].nil?
    assert res[:average_wind_speed].nil?
    assert res[:average_wind_speed_m].nil?
  end

  def test_archive_high_wind_speed
    a = create_complete_archive_struct
    a.high_wind_speed = -9999
    res = write_and_read_archive_record(a)
    assert res[:high_wind_speed].nil?
    assert res[:high_wind_speed_m].nil?
    assert res[:direction_of_high_wind].nil?
  end

  def test_archive_direction_of_high_wind_speed
    a = create_complete_archive_struct
    a.direction_of_high_wind_speed = -9999
    res = write_and_read_archive_record(a)
    assert res[:direction_of_high_wind_speed].nil?
    assert res[:high_wind_speed].nil?
    assert res[:high_wind_speed_m].nil?
  end

  def test_archive_number_of_wind_samples
    a = create_complete_archive_struct
    a.number_of_wind_samples = -9999
    res = write_and_read_archive_record(a)
    assert res[:number_of_wind_samples].nil?
  end

  def test_archive_inside_temp
    a = create_complete_archive_struct
    a.inside_temp = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:inside_temp].nil?
    assert res[:inside_temp_m].nil?
  end

  def test_archive_pressure
    a = create_complete_archive_struct
    a.pressure = -9999.0
    res = write_and_read_archive_record(a)
    assert res[:pressure].nil?
    assert res[:pressure_m].nil?
  end

  def test_archive_inside_humidity
    a = create_complete_archive_struct
    a.inside_humidity = -9999
    res = write_and_read_archive_record(a)
    assert res[:inside_humidity].nil?
  end

  def test_archive_average_uv_index
    a = create_complete_archive_struct
    a.average_uv_index = -9999
    res = write_and_read_archive_record(a)
    assert res[:average_uv_index].nil?
  end

  def test_archive_high_uv_index
    a = create_complete_archive_struct
    a.high_uv_index = -9999
    res = write_and_read_archive_record(a)
    assert res[:high_uv_index].nil?
  end

  def test_archive_average_solar_radiation
    a = create_complete_archive_struct
    a.average_solar_radiation = -9999
    res = write_and_read_archive_record(a)
    assert res[:average_solar_radiation].nil?
  end

  def test_archive_high_solar_radiation
    a = create_complete_archive_struct
    a.high_solar_radiation = -9999
    res = write_and_read_archive_record(a)
    assert res[:high_solar_radiation].nil?
  end

  ################ metric tests ##########################
  # wind 5 mph = 2.24 m/s
  # rain .5 in = 12.7 mm

  def test_archive_rainfall_m
    a = create_complete_archive_struct
    a.rainfall = 0.5
    res = write_and_read_archive_record(a)
    assert_in_delta res[:rainfall_m], 12.7, 0.1
  end

  def test_archive_high_rain_rate_m
    a = create_complete_archive_struct
    a.high_rain_rate = 0.5
    res = write_and_read_archive_record(a)
    assert_in_delta res[:high_rain_rate_m], 12.7, 0.1
  end

  def test_archive_average_wind_speed_m
    a = create_complete_archive_struct
    a.average_wind_speed = 5
    res = write_and_read_archive_record(a)
    assert_in_delta res[:average_wind_speed_m], 2.24, 0.25
  end

  def test_archive_high_wind_speed_m
    a = create_complete_archive_struct
    a.high_wind_speed = 5
    res = write_and_read_archive_record(a)
    assert_in_delta res[:high_wind_speed_m], 2.24, 0.25
  end

  def test_archive_outside_temp_m
    a = create_complete_archive_struct
    a.outside_temp = 212.0
    res = write_and_read_archive_record(a)
    assert_in_delta res[:outside_temp_m], 100.0, 0.1
  end

  def test_archive_high_outside_temp_m
    a = create_complete_archive_struct
    a.high_outside_temp = 212.0
    res = write_and_read_archive_record(a)
    assert_in_delta res[:high_outside_temp_m], 100.0, 0.1
  end

  def test_archive_low_outside_temp_m
    a = create_complete_archive_struct
    a.low_outside_temp = 212.0
    res = write_and_read_archive_record(a)
    assert_in_delta res[:low_outside_temp_m], 100.0, 0.1
  end

  def test_archive_inside_temp_m
    a = create_complete_archive_struct
    a.inside_temp = 212.0
    res = write_and_read_archive_record(a)
    assert_in_delta res[:inside_temp_m], 100.0, 0.1
  end

  ############# end metric tests ########################

  def test_rise_set

    res = invoke :get_rise_set, AppConfig.service_password, Date.new(2000, 1, 1), "01915"
    assert !res.nil?
    assert_equal res.rise.hour, 12
    assert_equal res.rise.min, 14
    assert_equal res.set.hour, 21
    assert_equal res.set.min, 22

    res = invoke :get_rise_set, AppConfig.service_password, Date.new(2000, 12, 31), "01915"
    assert !res.nil?
    assert_equal res.rise.hour, 12
    assert_equal res.rise.min, 14
    assert_equal res.set.hour, 21
    assert_equal res.set.min, 21

    res = invoke :get_rise_set, AppConfig.service_password, Date.new(2000, 06, 19), "01915"
    assert !res.nil?
    assert_equal res.rise.hour, 9
    assert_equal res.rise.min, 7
    assert_equal res.set.hour, 0
    assert_equal res.set.min, 24
  end

  def test_minimal_current_conditions
    my_struct = create_a_minimal_struct
    invoke :put_current_conditions, AppConfig.service_password, AppConfig.location, my_struct 
    res = invoke :get_current_conditions, AppConfig.location
    assert_not_equal nil, res
  end
  
  def test_bad_input_data
    begin
      my_struct = create_a_bad_struct
      invoke :put_current_conditions, "wrong", AppConfig.location, my_struct 
    rescue RuntimeError
      assert true
    end
  end
  
  def test_location_not_found
    begin
      res = invoke :get_current_conditions, "norecord"
    rescue ArgumentError
      assert true
    end
  end
  
  def test_authenticate_put_current_conditions
    begin
      my_struct = create_a_good_struct
      invoke :put_current_conditions, "wrong", AppConfig.location, my_struct
      assert false
    rescue RuntimeError
      assert_equal $!.message, "not authenticated"
    end
  end
  
  def test_authenticate_archive_record
    begin
      a = create_complete_archive_struct
      invoke :put_archive_entry, "wrong", "loc", a
      assert false
    rescue RuntimeError
      assert_equal $!.message, "not authenticated"
    end
  end
  
  def test_basic_archive
    a = create_complete_archive_struct
    location = "basic"
    password = AppConfig.service_password
    invoke :put_archive_entry, password, location, a
    invoke :put_archive_entry, password, location, a
    entry = ArchiveRecord.find_by_location_and_date(location, a[:date])
    assert_not_nil entry
    last_entry = invoke :get_last_archive, location
    assert_not_nil last_entry
    assert_equal entry[:date].getutc.to_s, a[:date].getutc.to_s
  end  

  def test_minimal_archive
    a = create_minimal_archive_struct
    location = "basic"
    password = AppConfig.service_password
    invoke :put_archive_entry, password, location, a
    entry = ArchiveRecord.find_by_location_and_date(location, a[:date])
    assert_not_nil entry
    last_entry = invoke :get_last_archive, location
    assert_not_nil last_entry
    assert_equal entry[:date].getutc.to_s, a[:date].getutc.to_s
  end

  def test_sample_nil_sunrise
     a = create_a_good_struct
     a.sunrise = DateTime.new(1970,1,31,0,0,0)
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:sunrise]
  end

  def test_sample_nil_sunset
     a = create_a_good_struct
     a.sunset = DateTime.new(1970,1,31,0,0,0)
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:sunset]
  end

  def test_sample_nil_dewpoint
    # temp or humidity is null
    a = create_a_good_struct
    a.temp = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:dewpoint]
    a = create_a_good_struct
    a.humidity = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:dewpoint]
    assert_nil a[:dewpoint_m]
  end

  def test_sample_nil_apparent_temp
    # temp or humidity or wind is null
    a = create_a_good_struct
    a.temp = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:apparent_temp]
    a = create_a_good_struct
    a.humidity = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:apparent_temp]
    a = create_a_good_struct
    a.windspeed = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:apparent_temp]
    assert_nil a[:apparent_temp_m]
  end

  def test_sample_nil_is_raining
    a = create_a_good_struct
    a.rain_rate = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:is_raining]
    assert_nil a[:rain_rate]
    assert_nil a[:rain_rate_m]
  end

  def test_sample_nil_inside_humidity
     a = create_a_good_struct
     a.inside_humidity = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:inside_humidity]
  end

  def test_sample_nil_inside_temperature
     a = create_a_good_struct
     a.inside_temperature = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:inside_temperature]
    assert_nil a[:inside_temperature_m]
  end

  def test_sample_nil_solar_radiation
     a = create_a_good_struct
     a.solar_radiation = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:solar_radiation]
  end

  def test_sample_nil_uv
     a = create_a_good_struct
     a.uv = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:uv]
  end

  def test_sample_nil_storm_rain
     a = create_a_good_struct
     a.storm_rain = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:storm_rain]
    assert_nil a[:storm_rain_m]
  end

  def test_sample_nil_yearly_rain
     a = create_a_good_struct
     a.yearly_rain = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:yearly_rain]
    assert_nil a[:yearly_rain_m]
  end

  def test_sample_nil_monthly_rain
     a = create_a_good_struct
     a.monthly_rain = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:monthly_rain]
    assert_nil a[:monthly_rain_m]
  end

  def test_sample_nil_daily_rain
     a = create_a_good_struct
     a.daily_rain = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:daily_rain]
    assert_nil a[:daily_rain_m]
  end

  def test_sample_nil_wind_direction
     a = create_a_good_struct
     a.wind_direction = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:windspeed]
    assert_nil a[:wind_direction]
  end

  def test_sample_nil_rain_rate
     a = create_a_good_struct
     a.rain_rate = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:rain_rate]
    assert_nil a[:rain_rate_m]
  end

  def test_sample_nil_windspeed
     a = create_a_good_struct
     a.windspeed = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:windspeed]
    assert_nil a[:windspeed_m]
    assert_nil a[:wind_direction]
  end

  def test_sample_nil_humidity
     a = create_a_good_struct
     a.humidity = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:humidity]
  end

  def test_sample_nil_temp
     a = create_a_good_struct
     a.temp = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:temp]
    assert_nil a[:temp_m]
  end

  def test_sample_nil_pressure
     a = create_a_good_struct
     a.pressure = -9999.0
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:pressure]
    assert_nil a[:pressure_m]
  end

  def test_sample_nil_ten_min_avg_wind
     a = create_a_good_struct
     a.ten_min_avg_wind = -9999
    invoke :put_current_conditions, AppConfig.service_password, "basic", a
    a = invoke :get_current_conditions, "basic"
    assert_nil a[:ten_min_avg_wind]
    assert_nil a[:ten_min_avg_wind_m]
  end
end


