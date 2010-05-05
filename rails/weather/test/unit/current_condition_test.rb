require File.dirname(__FILE__) + '/../test_helper'

class CurrentConditionTest < ActiveSupport::TestCase #Test::Unit::TestCase
  fixtures :current_conditions

  def test_valid_outside_humidity
    cond = current_conditions(:good)
    cond.outside_humidity = 101
    assert !cond.save
    assert_equal "invalid outside humidity", cond.errors.on(:outside_humidity)
    cond.outside_humidity = 99
    assert cond.save
    cond.outside_humidity = 0
    assert !cond.save
    cond.outside_humidity = 1
    assert cond.save
    cond.outside_humidity = -1
    assert !cond.save
    assert_equal "invalid outside humidity", cond.errors.on(:outside_humidity)
    cond.outside_humidity = nil
    assert cond.save
  end

  def test_valid_inside_humidity
    cond = current_conditions(:good)
    cond.inside_humidity = 101
    assert !cond.save
    assert_equal "invalid inside humidity", cond.errors.on(:inside_humidity)
    cond.inside_humidity = 99
    assert cond.save
    cond.inside_humidity = 0
    assert !cond.save
    cond.inside_humidity = -1
    assert !cond.save
    assert_equal "invalid inside humidity", cond.errors.on(:inside_humidity)
    cond.inside_humidity = nil
    assert cond.save
  end

  def test_valid_wind_direction
    cond = current_conditions(:good)
    assert cond.valid?
    cond.wind_direction = 99
    assert cond.valid?
    cond.wind_direction = 0
    assert cond.valid?
    cond.wind_direction = -1
    assert !cond.valid?
    assert_equal "invalid wind direction", cond.errors.on(:wind_direction)
    cond.wind_direction = nil
    assert cond.valid?
    cond[:wind_direction] = 361
    assert !cond.valid?
    assert_equal "invalid wind direction", cond.errors.on(:wind_direction)
    cond[:wind_direction] = 360
    assert cond.valid?
  end

  def test_valid_pressure
    cond = current_conditions(:good)
    cond.pressure = nil
    assert cond.valid?
    cond.pressure = 20
    assert !cond.valid?
    assert_equal "invalid pressure", cond.errors.on(:pressure)
    cond.pressure = -1
    assert !cond.valid?
    assert_equal "invalid pressure", cond.errors.on(:pressure)
    cond.pressure = 32.1
    assert cond.valid?
    cond.pressure = 30
    assert cond.valid?
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

  def test_location_length
    cond = CurrentCondition.new
    cond.location = "123456789012345678901234567890"
    assert_not_nil cond
    cond[:sample_date] = Time.now
    cond[:outside_temperature] = 32.2
    assert cond.save!
    CurrentCondition.delete(cond[:id])
    cond = CurrentCondition.new
    assert_not_nil cond
    cond[:sample_date] = Time.now
    cond[:outside_temperature] = 32.2
    cond.location = "1234567890123456789012345678901"
    assert !cond.valid?
  end

  def test_dewpoint
    d = Time.now
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => d,
        :outside_humidity => 50, :outside_temperature => 50.0)
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:dewpoint], 32.1, 0.5
  end

# test metrics

  def test_outside_temp_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:outside_temperature] = 212
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:outside_temperature_m], 100.0
  end

  def test_inside_temp_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:inside_temperature] = 212
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:inside_temperature_m], 100.0
  end
  
  def test_dewpoint_m
    d = Time.now
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => d,
        :outside_humidity => 50, :outside_temperature => 50.0)
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:dewpoint], 32.1, 0.5
    assert_in_delta c[:dewpoint_m], 0.0, 0.5
  end

  def test_apparent_temp_m
    # temp, rh, wind
    #>> WxHelper.apparent_temp(32.0, 50, 5)
    #=> 27
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:outside_temperature] = 32.0
    c[:outside_humidity] = 50
    c[:windspeed] = 5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:apparent_temp], 27.0, 0.5
    assert_in_delta c[:apparent_temp_m], WxHelper.to_c(27), 0.5
  end

  # 29.82 = 1009.8
  def test_pressure_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:pressure] = 29.82
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:pressure_m], 1009.8, 0.1
  end

  def test_windspeed_m
    #5 mph is 2.24 m/s
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:windspeed] = 5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:windspeed_m], 2.24, 0.25
  end

#1 mph = 0.45 m/s
#5 mph = 2,24 m/s
#8 mph = 3,58 m/s  # 5 m/s = 11.18468146 mph
  def test_ten_min_avg_wind_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:ten_min_avg_wind] = 5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:ten_min_avg_wind_m], 2.24, 0.25
    c[:ten_min_avg_wind] = 1
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:ten_min_avg_wind_m], 0.45, 0.25
    c[:ten_min_avg_wind] = 8
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_in_delta c[:ten_min_avg_wind_m], 3.58, 0.25
  end

  def test_rain_rate_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:rain_rate] = 0.5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:rain_rate_m], 12.7
  end

  def test_daily_rain_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:daily_rain] = 0.5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:daily_rain_m], 12.7
  end

  def test_monthly_rain_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:monthly_rain] = 0.5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:monthly_rain_m], 12.7
  end

  def test_yearly_rain_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:yearly_rain] = 0.5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:yearly_rain_m], 12.7
  end

  def test_storm_rain_m
    c = CurrentCondition.new(:location => AppConfig.location, :sample_date => Time.now)
    c[:storm_rain] = 0.5
    assert c.valid?
    assert c.save!
    assert c.reload
    assert !c.nil?
    assert_equal c[:storm_rain_m], 12.7
  end
end

#====

#00:00  	 43.2 °F  / 6.2 °C  	 38.7 °F  / 3.7 °C  	 29.82in  / 1009.7hPa  	North  	 1.0mph  / 1.6km/h  	 4.0mph  / 6.4km/h  	84%  	 0.00in  / 0.0mm  	0 watts/m^2
#00:05 	43.4 °F / 6.3 °C 	38.9 °F / 3.8 °C 	29.82in / 1009.7hPa 	Calm 	  	4.0mph / 6.4km/h 	84% 	0.00in / 0.0mm 	0 watts/m^2
#00:10 	43.3 °F / 6.3 °C 	38.5 °F / 3.6 °C 	29.82in / 1009.7hPa 	Calm 	  	3.0mph / 4.8km/h 	83% 	0.00in / 0.0mm 	0 watts/m^2
#00:16 	43.5 °F / 6.4 °C 	38.4 °F / 3.6 °C 	29.82in / 1009.7hPa 	North 	2.0mph / 3.2km/h 	6.0mph / 9.7km/h 	82% 	0.00in / 0.0mm 	0 watts/m^2
#00:21 	43.6 °F / 6.4 °C 	38.5 °F / 3.6 °C 	29.82in / 1009.7hPa 	North 	2.0mph / 3.2km/h 	5.0mph / 8.0km/h 	82% 	0.00in / 0.0mm 	0 watts/m^2
#00:31 	43.6 °F / 6.4 °C 	38.2 °F / 3.4 °C 	29.82in / 1009.7hPa 	North 	1.0mph / 1.6km/h 	5.0mph / 8.0km/h 	81% 	0.00in / 0.0mm 	0 watts/m^2
#00:36 	43.6 °F / 6.4 °C 	38.2 °F / 3.4 °C 	29.82in / 1009.7hPa 	North 	2.0mph / 3.2km/h 	6.0mph / 9.7km/h 	81% 	0.00in / 0.0mm 	0 watts/m^2
#00:41 	43.4 °F / 6.3 °C 	38.0 °F / 3.3 °C 	29.82in / 1009.7hPa 	North 	1.0mph / 1.6km/h 	6.0mph / 9.7km/h 	81% 	0.00in / 0.0mm 	0 watts/m^2
#00:46 	43.4 °F / 6.3 °C 	37.6 °F / 3.1 °C 	29.82in / 1009.7hPa 	NW 	1.0mph / 1.6km/h 	7.0mph / 11.3km/h 	80% 	0.00in / 0.0mm 	0 watts/m^2
#00:51 	43.3 °F / 6.3 °C 	37.5 °F / 3.1 °C 	29.82in / 1009.7hPa 	North 	2.0mph / 3.2km/h 	5.0mph / 8.0km/h 	80% 	0.00in / 0.0mm 	0 watts/m^2
#00:56 	43.3 °F / 6.3 °C 	37.5 °F / 3.1 °C 	29.82in / 1009.7hPa 	North 	2.0mph / 3.2km/h 	6.0mph / 9.7km/h 	80% 	0.00in / 0.0mm 	0 watts/m^2
#01:06 	42.9 °F / 6.1 °C 	36.8 °F / 2.7 °C 	29.81in / 1009.4hPa 	North 	1.0mph / 1.6km/h 	6.0mph / 9.7km/h 	79% 	0.00in / 0.0mm 	0 watts/m^2
#01:11 	42.7 °F / 5.9 °C 	37.0 °F / 2.8 °C 	29.81in / 1009.4hPa 	North 	2.0mph / 3.2km/h 	4.0mph / 6.4km/h 	80% 	0.00in / 0.0mm 	0 watts/m^2
#01:21 	42.2 °F / 5.7 °C 	36.5 °F / 2.5 °C 	29.81in / 1009.4hPa 	NNW 	1.0mph / 1.6km/h 	5.0mph / 8.0km/h 	80% 	0.00in / 0.0mm 	0 watts/m^2
#01:36 	41.6 °F / 5.3 °C 	36.2 °F / 2.3 °C 	29.81in / 1009.4hPa 	Calm 	  	5.0mph / 8.0km/h 	81% 	0.00in / 0.0mm 	0 watts/m^2

