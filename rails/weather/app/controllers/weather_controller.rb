class WeatherController < ApplicationController
  wsdl_service_name 'Weather'
  web_service_scaffold :invoke
  before_invocation :authenticate, :except => [:get_current_conditions,
                                               :get_last_archive]
  
  def get_current_conditions(location)
    sample = CurrentCondition.find_by_location(location)
    raise ArgumentError if sample == nil
    new_sample = 
      SampleStruct.new(
        :sample_date => sample[:sample_date],
        :temp => sample[:outside_temperature],
        :humidity => sample[:outside_humidity],
        :dewpoint => sample[:dewpoint],
        :pressure => sample[:pressure],
        :bar_status => sample[:bar_status],
        :windspeed => sample[:windspeed],
        :ten_min_avg_wind => sample[:ten_min_avg_wind],
        :is_raining => sample[:is_raining],
        :solar_radiation => sample[:solar_radiation],
        :wind_direction => sample[:wind_direction],
        :apparent_temp =>  sample[:apparent_temp],
        :rain_rate => sample[:rain_rate])
  end
  
  def put_current_conditions(password, location, sample)
    cond = CurrentCondition.find_or_create_by_location(location)
    cond[:location] = location
    cond[:sample_date] = sample[:sample_date].getutc
    cond[:outside_temperature] = sample[:temp]
    cond[:outside_humidity] = sample[:humidity]
    cond[:pressure] = sample[:pressure]
    cond[:bar_status] = sample[:bar_status]
    cond[:windspeed] = sample[:windspeed]
    cond[:wind_direction] = sample[:wind_direction]
    cond[:rain_rate] = sample[:rain_rate]
    cond[:ten_min_avg_wind] = sample[:ten_min_avg_wind]
    cond[:uv] = sample[:uv]
    cond[:solar_radiation] = sample[:solar_radiation]
    cond[:daily_rain] = sample[:daily_rain]
    
    if !cond.save
      raise cond.errors.full_messages.to_s
    end

    if AppConfig.wunderground != nil
      WeatherHelper.post_to_wunderground(location)
    end
  end
  
  def get_last_archive(location)
    entry = ArchiveRecord.find(:first, 
                               :conditions => ["location = ?", location],
                               :order => "date DESC")
    return nil if entry.nil?
    last_entry = 
      ArchiveStruct.new(
        :location => location,
        :date => entry[:date],
        :outside_temp => entry[:outside_temp],
        :high_outside_temp => entry[:high_outside_temp],
        :low_outside_temp => entry[:low_outside_temp],
        :pressure => entry[:pressure],
        :outside_humidity => entry[:outside_humidity],
        :rainfall => entry[:rainfall],
        :high_rain_rate => entry[:high_rain_rate],
        :average_wind_speed => entry[:average_wind_speed],
        :high_wind_speed => entry[:high_wind_speed],
        :direction_of_high_wind_speed => entry[:direction_of_high_wind_speed],
        :prevailing_wind_direction => entry[:prevailing_wind_direction],
        :inside_temp => entry[:inside_temp],
        :inside_humidity => entry[:inside_humidity],
        :number_of_wind_samples => entry[:number_of_wind_samples],
        :average_uv_index => entry[:average_uv_index],
        :high_uv_index => entry[:high_uv_index],
        :average_solar_radiation => entry[:average_solar_radiation],
        :high_solar_radiation => entry[:high_solar_radiation]
      )
  end

  def get_archive_since(password, location, date)
    records = ArchiveRecord.find_all_by_location(location, :conditions => ["date > ?", date.utc], :order => "date", :limit => 60)
    structs = Array.new
    records.each do | entry |
          structs << 
      ArchiveStruct.new(
        :location => location,
        :date => entry[:date],
        :outside_temp => entry[:outside_temp],
        :high_outside_temp => entry[:high_outside_temp],
        :low_outside_temp => entry[:low_outside_temp],
        :pressure => entry[:pressure],
        :outside_humidity => entry[:outside_humidity],
        :rainfall => entry[:rainfall],
        :high_rain_rate => entry[:high_rain_rate],
        :average_wind_speed => entry[:average_wind_speed],
        :high_wind_speed => entry[:high_wind_speed],
        :direction_of_high_wind_speed => entry[:direction_of_high_wind_speed],
        :prevailing_wind_direction => entry[:prevailing_wind_direction],
        :inside_temp => entry[:inside_temp],
        :inside_humidity => entry[:inside_humidity],
        :number_of_wind_samples => entry[:number_of_wind_samples],
        :average_uv_index => entry[:average_uv_index],
        :high_uv_index => entry[:high_uv_index],
        :average_solar_radiation => entry[:average_solar_radiation],
        :high_solar_radiation => entry[:high_solar_radiation]
      )
    end
    return structs
  end
  
  def put_archive_entry(password, location, entry)
    date = entry[:date].getutc    
    rec = ArchiveRecord.find_or_create_by_location_and_date(location, date)
    rec[:location] = location
    rec[:date] = date
    rec[:outside_temp] = entry[:outside_temp]
    rec[:high_outside_temp] = entry[:high_outside_temp]
    rec[:low_outside_temp] = entry[:low_outside_temp]
    rec[:pressure] = entry[:pressure]
    rec[:outside_humidity] = entry[:outside_humidity]
    rec[:rainfall] = entry[:rainfall]
    rec[:high_rain_rate] = entry[:high_rain_rate]
    rec[:average_wind_speed] = entry[:average_wind_speed]
    rec[:high_wind_speed] = entry[:high_wind_speed]
    rec[:direction_of_high_wind_speed] = entry[:direction_of_high_wind_speed]
    rec[:prevailing_wind_direction] = entry[:prevailing_wind_direction]
    rec[:inside_temp] = entry[:inside_temp]
    rec[:inside_humidity] = entry[:inside_humidity]
    rec[:number_of_wind_samples] = entry[:number_of_wind_samples]
    rec[:average_uv_index] = entry[:average_uv_index]
    rec[:high_uv_index] = entry[:high_uv_index]
    rec[:average_solar_radiation] = entry[:average_solar_radiation]
    rec[:high_solar_radiation] = entry[:high_solar_radiation]
    if !rec.save
      raise rec.errors.full_messages.to_s
    end
  end
  
  def authenticate(name, params)
    raise "not authenticated" unless params[0] == AppConfig.service_password
  end  
end
