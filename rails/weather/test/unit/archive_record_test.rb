require File.dirname(__FILE__) + '/../test_helper'

class ArchiveRecordTest < ActiveSupport::TestCase #Test::Unit::TestCase
  fixtures :archive_records

  def test_minimal_validation
    a = ArchiveRecord.new
    assert !a.valid?
    a = get_minimal_record
    assert a.valid?
  end
  
  def test_prevailing_wind_direction
    a = get_minimal_record
    assert a.valid?
    a[:prevailing_wind_direction] = 360
    assert a.valid?
    a[:prevailing_wind_direction] = 361
    assert !a.valid?
    a[:prevailing_wind_direction] = -1
    assert !a.valid?
    a[:prevailing_wind_direction] = 180
    assert a.valid?
  end

  def test_outside_humidity
    a = get_minimal_record
    assert a.valid?
    a[:outside_humidity] = 101
    assert !a.valid?
    a[:outside_humidity] = -1
    assert !a.valid?
    a[:outside_humidity] = 50
    assert a.valid?
    a[:outside_humidity] = 0
    assert a.valid?
    a[:outside_humidity] = 100
    assert a.valid?
  end

  def test_inside_humidity
    a = get_minimal_record
    assert a.valid?
    a[:inside_humidity] = 101
    assert !a.valid?
    a[:inside_humidity] = -1
    assert !a.valid?
    a[:inside_humidity] = 50
    assert a.valid?
    a[:inside_humidity] = 0
    assert a.valid?
    a[:inside_humidity] = 100
    assert a.valid?
  end

  def test_direction_of_high_wind_speed
    a = get_minimal_record
    assert a.valid?
    a[:direction_of_high_wind_speed] = 360
    assert a.valid?
    a[:direction_of_high_wind_speed] = 361
    assert !a.valid?
    a[:direction_of_high_wind_speed] = -1
    assert !a.valid?
    a[:direction_of_high_wind_speed] = 180
    assert a.valid?
    a[:direction_of_high_wind_speed] = 90
    assert a.valid?
    a[:direction_of_high_wind_speed] = 359
    assert a.valid?
    a[:direction_of_high_wind_speed] = 0
    assert a.valid?
  end
  
  def test_pressure
    a = get_minimal_record
    assert a.valid?
    a[:pressure] = 0
    assert !a.valid?
    a[:pressure] = -1
    assert !a.valid?
    a[:pressure] = 24.999
    assert !a.valid?
    a[:pressure] = 35.01
    assert !a.valid?
    a[:pressure] = 30.001
    assert a.valid?
  end
  
  def get_minimal_record
    ArchiveRecord.new(:date => Time.now, :location => "here")
  end

  def test_location_length
    a = get_minimal_record
    a[:location] = "123456789012345678901234567890" # 30 chars - the max
    assert a.valid?

    a = get_minimal_record
    a[:location] = "1234567890123456789012345678901" # 31 chars - one more than the max
    assert !a.valid?

    # long_location:
    # too_long_location:
    # assert false
  end

  def test_outside_temp_m
    a = get_minimal_record
    a[:outside_temp] = 212.0
    a.save
    a.reload
    assert_in_delta a[:outside_temp_m], 100.0, 0.1
  end

  def test_low_outside_temp_m
    a = get_minimal_record
    a[:low_outside_temp] = 212.0
    a.save
    a.reload
    assert_in_delta a[:low_outside_temp_m], 100.0, 0.1
  end

  def test_high_outside_temp_m
    a = get_minimal_record
    a[:high_outside_temp] = 212.0
    a.save
    a.reload
    assert_in_delta a[:high_outside_temp_m], 100.0, 0.1
  end

  def test_inside_temp_m
    a = get_minimal_record
    a[:inside_temp] = 212.0
    a.save
    a.reload
    assert_in_delta a[:inside_temp_m], 100.0, 0.1
  end

  # 29.82 = 1009.8
  def test_pressure_m
    a = get_minimal_record
    a[:pressure] = 29.82
    a.save
    a.reload
    assert_in_delta a[:pressure_m], 1009.8, 0.1
  end

  def test_average_dewpoint_m
    a = get_minimal_record
    a[:outside_temp] = 50.0
    a[:outside_humidity] = 50
    a.save
    a.reload
    assert_in_delta a[:average_dewpoint], 32.1, 0.5
    assert_in_delta a[:average_dewpoint_m], 0.0, 0.5
  end

  def test_average_apparent_temp_m
    a = get_minimal_record
    a[:outside_temp] = 32.0
    a[:outside_humidity] = 50
    a[:average_wind_speed] = 5
    a.save
    a.reload
    assert_in_delta a[:average_apparent_temp], 27.0, 0.5
    assert_in_delta a[:average_apparent_temp_m], a.to_c(27), 0.5
  end

  def test_average_wind_speed_m
    a = get_minimal_record
    a[:average_wind_speed] = 5
    a.save
    a.reload
    assert_in_delta a[:average_wind_speed_m], 2.24, 0.25
  end

  def test_high_wind_speed_m
    a = get_minimal_record
    a[:high_wind_speed] = 5
    a.save
    a.reload
    assert_in_delta a[:high_wind_speed_m], 2.24, 0.25
  end

  def test_high_rain_rate_m
    a = get_minimal_record
    a[:high_rain_rate] = 0.5
    a.save
    a.reload
    assert_in_delta a[:high_rain_rate_m], 12.7, 0.25
  end

  def test_rainfall_m
    a = get_minimal_record
    a[:rainfall] = 0.5
    assert a.valid?
    assert a.save!
    assert a.reload
    assert !a.nil?
    assert_in_delta a[:rainfall_m], 12.7, 0.1
  end
end
