require File.dirname(__FILE__) + '/../test_helper'

class ArchiveRecordTest < Test::Unit::TestCase
  fixtures :archive_records

  def test_minimal_validation
    a = ArchiveRecord.new
    assert !a.save
    a = get_minimal_record
    assert a.save
  end
  
  def test_prevailing_wind_direction
    a = get_minimal_record
    assert a.valid?
    a[:prevailing_wind_direction] = 360
    assert !a.valid?
    a[:prevailing_wind_direction] = -1
    assert !a.valid?
    a[:prevailing_wind_direction] = 180
    assert a.valid?
  end
  
  def test_direction_of_high_wind_speed
    a = get_minimal_record
    assert a.valid?
    a[:direction_of_high_wind_speed] = 360
    assert !a.valid?
    a[:direction_of_high_wind_speed] = -1
    assert !a.valid?
    a[:direction_of_high_wind_speed] = 180
    assert a.valid?
  end
  
  def test_outside_humidity
    a = get_minimal_record
    assert a.valid?
    a[:outside_humidity] = 0
    assert !a.valid?
    a[:outside_humidity] = -1
    assert !a.valid?
    a[:outside_humidity] = 101
    assert !a.valid?
    a[:outside_humidity] = 100
    assert a.valid?
  end
  
  def test_pressure
    a = get_minimal_record
    assert a.valid?
    a[:pressure] = 0
    assert !a.valid?
    a[:pressure] = -1
    assert !a.valid?
    a[:pressure] = 26.999
    assert !a.valid?
    a[:pressure] = 32.01
    assert !a.valid?
    a[:pressure] = 30.001
    assert a.valid?
  end
  
  def get_minimal_record
    ArchiveRecord.new(:date => Time.now, :location => "here")
  end
end
