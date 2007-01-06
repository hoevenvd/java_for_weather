require File.dirname(__FILE__) + '/../test_helper'

class CurrentConditionTest < Test::Unit::TestCase
  fixtures :current_conditions

  def test_valid_humidity
    cond = current_conditions(:good)
    cond.outside_humidity = 101
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:outside_humidity)
    cond.outside_humidity = 99
    assert cond.save
    cond.outside_humidity = 0
    assert !cond.save
    cond.outside_humidity = -1
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:outside_humidity)
    cond.outside_humidity = nil
    assert cond.save
  end
  
  def test_valid_wind_direction
    cond = current_conditions(:good)
    cond.wind_direction = 360
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:wind_direction)
    cond.wind_direction = 99
    assert cond.save
    cond.wind_direction = 0
    assert cond.save
    cond.wind_direction = -1
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:wind_direction)
    cond.wind_direction = nil
    assert cond.save
  end

  def test_valid_pressure
    cond = current_conditions(:good)
    cond.pressure = nil
    assert cond.save
    cond.pressure = 20
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:pressure)
    cond.pressure = -1
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:pressure)
    cond.pressure = 32.1
    assert !cond.save
    assert_equal "invalid", cond.errors.on(:pressure)
    cond.pressure = 30
    assert cond.save
  end
  
  def test_minimal_record
    cond = CurrentCondition.new
    cond[:sample_date] = Time.now
    cond[:outside_temperature] = 32.2
    cond[:location] = "minimal"
    cond.save
    cond = CurrentCondition.find_by_location("minimal")
    assert_not_nil cond
  end

  def test_nil_outside_temp
    cond = current_conditions(:good)
    cond.outside_temperature = nil
    assert !cond.save
    assert_equal ActiveRecord::Errors.default_error_messages[:blank], 
         cond.errors.on(:outside_temperature)
  end
  
  def test_is_raining
    cond = current_conditions(:good)
    cond[:rain_rate] = 0.01
    assert cond.save
    assert cond.reload
    assert cond.is_raining
    cond[:rain_rate] = 0.00
    assert cond.save
    assert cond.reload
    assert !cond.is_raining
  end
  
  def test_crud
    cond = CurrentCondition.new
    assert_not_nil cond
    cond[:sample_date] = Time.now
    cond[:outside_temperature] = 32.2
    cond[:location] = "minimal"
    assert cond.save
    cond[:outside_temperature] = 19.5
    assert cond.save
    loc = cond[:location]
    cond = CurrentCondition.find_by_location(loc)
    assert_not_nil cond
    assert_equal cond[:outside_temperature], 19.5
    assert cond.destroy
    cond = CurrentCondition.find_by_location(loc)
    assert_nil cond
  end
end
