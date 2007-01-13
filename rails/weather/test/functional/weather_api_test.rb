require File.dirname(__FILE__) + '/../test_helper'
require 'weather_controller'

#  class WeatherController
#    def rescue_action(e)
#      raise e
#    end
#  end


class WeatherController; def rescue_action(e) raise e end; end

class WeatherControllerApiTest < Test::Unit::TestCase

  PASSWORD = "wx"
  
  def setup
    @controller = WeatherController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_minimal_current_conditions
    my_struct = create_a_minimal_struct
    invoke :put_current_conditions, PASSWORD, "01915-test", my_struct 
    res = invoke :get_current_conditions, "01915-test"
    assert_not_equal nil, res
  end
  
  def test_get_current_conditions_vp
    my_struct = create_a_good_struct
    invoke :put_current_conditions, PASSWORD, "01915-test", my_struct 
    res = invoke :get_current_conditions, "01915-test"

    assert_not_equal nil, res
    assert_equal res[:temp], 32.2
    assert_equal res[:windspeed], 2
    assert_equal res[:humidity], 45
    assert_equal res[:pressure], 30.001
    assert_equal res[:bar_status], "rising"
    assert_equal res[:rain_rate], 1.01
    assert_equal res[:windspeed], 2
    assert_equal res[:wind_direction], 270
  end
  
  def test_get_current_conditions_wm2
    my_struct = create_a_good_struct_wm2
    invoke :put_current_conditions, PASSWORD, "01915-test", my_struct 
    res = invoke :get_current_conditions, "01915-test"

    assert_not_equal nil, res
    assert_equal res[:temp], 32.2
    assert_equal res[:windspeed], 2
    assert_equal res[:humidity], 45
    assert_equal res[:pressure], 30.001
    assert_equal res[:bar_status], "rising"
    assert_equal res[:rain_rate], 1.01
    assert_equal res[:windspeed], 2
    assert_equal res[:wind_direction], 270
  end
  
  def test_bad_input_data
    begin
      my_struct = create_a_bad_struct
      invoke :put_current_conditions, "wrong", "01915-test", my_struct 
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
      invoke :put_current_conditions, "wrong", "01915-test", my_struct
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
    password = PASSWORD
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
    password = PASSWORD
    invoke :put_archive_entry, password, location, a
    entry = ArchiveRecord.find_by_location_and_date(location, a[:date])
    assert_not_nil entry
    last_entry = invoke :get_last_archive, location
    assert_not_nil last_entry
    assert_equal entry[:date].getutc.to_s, a[:date].getutc.to_s
  end  

  def test_get_rise_set
    password = PASSWORD
    latitude = 42.5
    longitude = -72.5
#    date = Time.now
    date = nil
    struct = invoke :get_rise_set, password, date, latitude, longitude
    assert_not_nil struct
    latitude = 52.5
    longitude = 5.5
    struct = invoke :get_rise_set, password, date, latitude, longitude
    assert_not_nil struct
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
       :wind_direction => 270)
  end
  
  def create_a_minimal_struct
    my_struct = InputSampleStruct.new( 
       :sample_date => Time.now)
  end
  
  def create_a_good_struct_wm2
    my_struct = InputSampleStruct.new( 
       :sample_date => Time.now, 
       :temp => 32.2, 
       :windspeed => 2, 
       :humidity => 45, 
       :pressure => 30.001,
       :bar_status => "rising",
       :rain_rate => 1.01,
       :wind_direction => 270)
  end
  
  def create_a_bad_struct
    my_struct = InputSampleStruct.new( 
       :sample_date => Time.now, 
       :temp => 32.2, 
       :windspeed => 2, 
       :humidity => 145, 
       :pressure => 32.001,
       :bar_status => "rising",
       :ten_min_avg_wind => 2,
       :rain_rate => 1.01,
       :wind_direction => 1270)
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
      :number_of_wind_samples => 20)
  end

  def create_minimal_archive_struct
    a = ArchiveStruct.new(:date => Time.now.getutc)
  end
end
