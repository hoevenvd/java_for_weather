# caching strategy

# since the data structures are the same as past_summaries, look into sti to allow for simple method overrides for
#  stuff like asking a record if it has expired, encapsulating updating extremes, etc
# provide reasonable defaults for cache ttls by current period and allow override via config
# reading: make sure a record is not too stale

# archive record posting:
#   if no record, create it
#   check to see if the ttl has expired and refresh if necessary
#   update extremes for all cached current periods - stuff like highs/lows, etc

# current conditions posting:
#   if no record, create it
#   check to see if a record has expired and create it if necessary
#   update extremes for all periods

class WeatherController < ApplicationController
  include Cache
  wsdl_service_name 'Weather'
  web_service_scaffold :invoke
  before_invocation :authenticate, :except => [:get_current_conditions,
                                               :get_last_archive]

  def reset_cache(password, location)
    PastSummary.delete_all("location = #{location}")
    update_current_cache(location)
  end

  def update_last_rain(location, date)
    l = LastRain.find_or_create_by_location(location)
    l.last_rain = date
    l.save!
  end
  
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
        :inside_temperature => sample[:inside_temperature],
        :inside_humidity => sample[:inside_humidity],
        :sunrise => sample[:sunrise],
        :sunset => sample[:sunset],
        :daily_rain => sample[:daily_rain],
        :monthly_rain => sample[:monthly_rain],
        :yearly_rain => sample[:yearly_rain],
        :storm_rain => sample[:storm_rain],
        :rain_rate => sample[:rain_rate])
  end
  
  def put_current_conditions(password, location, sample)
    cond = CurrentCondition.find_or_create_by_location(location)
    cond[:sample_date] = Time.now.utc
    cond[:outside_temperature] = sample[:temp] == -9999.0 ? nil : sample[:temp]
    cond[:outside_humidity] = sample[:humidity] == -9999 ? nil : sample[:humidity]
    cond[:pressure] = sample[:pressure] == -9999.0 ? nil : sample[:pressure]
    cond[:bar_status] = sample[:bar_status]

    if sample[:windspeed] == -9999 or sample[:wind_direction] == -9999
      cond[:windspeed] = nil
      cond[:wind_direction] = nil
    else
      cond[:windspeed] = sample[:windspeed]
      cond[:wind_direction] = sample[:wind_direction]
    end
    
    cond[:rain_rate] = sample[:rain_rate] == -9999.0 ? nil : sample[:rain_rate]

    if (!cond[:rain_rate].nil? and cond[:rain_rate] > 0) or cond[:is_raining]
      update_last_rain(location, cond[:sample_date])
    end

    cond[:ten_min_avg_wind] = sample[:ten_min_avg_wind] == -9999 ? nil : sample[:ten_min_avg_wind]
    cond[:inside_temperature] = sample[:inside_temperature] == -9999.0 ? nil : sample[:inside_temperature]
    cond[:inside_humidity] = sample[:inside_humidity] == -9999 ? nil : sample[:inside_humidity]
    cond[:sunrise] = sample[:sunrise] == DateTime.new(1970,1,31,0,0,0) ? nil : sample[:sunrise]
    cond[:sunset] = sample[:sunset] == DateTime.new(1970,1,31,0,0,0) ? nil : sample[:sunset]
    cond[:daily_rain] = sample[:daily_rain] == -9999.0 ? nil : sample[:daily_rain]
    cond[:monthly_rain] = sample[:monthly_rain] == -9999.0 ? nil : sample[:monthly_rain]
    cond[:yearly_rain] = sample[:yearly_rain] == -9999.0 ? nil : sample[:yearly_rain]
    cond[:storm_rain] = sample[:storm_rain] == -9999.0 ? nil : sample[:storm_rain]
    cond[:uv] = sample[:uv] == -9999 ? nil : sample[:uv]
    cond[:solar_radiation] = sample[:solar_radiation] == -9999 ? nil : sample[:solar_radiation]

    if !cond.save
      raise cond.errors.full_messages.to_s
    end

    if SVC_CONFIG != nil && SVC_CONFIG["wunderground"] != nil
      SVC_CONFIG["wunderground"].each do |l|
        if (location == l["location"])
          begin
            WeatherHelper.post_to_wunderground(l["location"], l["id"], l["password"])
          rescue Exception => ex
            logger.error(ex.message + " - conditions were saved before this error\n")
            logger.error(ex.backtrace)
          end
        end
      end
    end
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

  def put_archive_entry(password, location, entry)
    date = entry[:date].getutc    
    rec = ArchiveRecord.find_or_create_by_location_and_date(location, date)
    rec[:location] = location
    rec[:date] = date
    rec[:outside_temp] = entry[:outside_temp] == -9999.0 ? nil : entry[:outside_temp]
    rec[:high_outside_temp] = entry[:high_outside_temp] == -9999.0 ? nil : entry[:high_outside_temp]
    rec[:low_outside_temp] = entry[:low_outside_temp] == -9999.0 ? nil : entry[:low_outside_temp]
    rec[:pressure] = entry[:pressure] == -9999.0 ? nil : entry[:pressure]
    rec[:outside_humidity] = entry[:outside_humidity] == -9999 ? nil : entry[:outside_humidity]
    rec[:rainfall] = entry[:rainfall] == -9999.0 ? nil : entry[:rainfall]

    if !rec[:rainfall].nil? and rec[:rainfall] > 0.0
      update_last_rain(location, date)
    end

    rec[:high_rain_rate] = entry[:high_rain_rate] == -9999.0 ? nil : entry[:high_rain_rate]

    if entry[:average_wind_speed] == -9999 or entry[:prevailing_wind_direction] == -9999
      rec[:average_wind_speed] = nil
      rec[:prevailing_wind_direction] = nil
    else
      rec[:average_wind_speed] = entry[:average_wind_speed]
      rec[:prevailing_wind_direction] = entry[:prevailing_wind_direction]
    end

    if entry[:high_wind_speed] == -9999 or entry[:direction_of_high_wind_speed] == -9999
      rec[:high_wind_speed] = nil
      rec[:direction_of_high_wind_speed] = nil
    else
      rec[:high_wind_speed] = entry[:high_wind_speed]
      rec[:direction_of_high_wind_speed] = entry[:direction_of_high_wind_speed]
    end

    rec[:inside_temp] = entry[:inside_temp] == -9999 ? nil : entry[:inside_temp]
    rec[:inside_humidity] = entry[:inside_humidity] == -9999 ? nil : entry[:inside_humidity]
    rec[:number_of_wind_samples] = entry[:number_of_wind_samples] == -9999 ? nil : entry[:number_of_wind_samples]
    rec[:average_uv_index] = entry[:average_uv_index] == -9999 ? nil : entry[:average_uv_index]
    rec[:high_uv_index] = entry[:high_uv_index] == -9999 ? nil : entry[:high_uv_index]
    rec[:average_solar_radiation] = entry[:average_solar_radiation] == -9999 ? nil : entry[:average_solar_radiation]
    rec[:high_solar_radiation] = entry[:high_solar_radiation] == -9999 ? nil : entry[:high_solar_radiation]

    if !rec.save
      raise rec.errors.full_messages.to_s
    end

    update_current_cache(location)

    if AppConfig.rrd_enabled and !AppConfig.rrd_graphs[location].nil?
      begin
        WeatherHelper.update_rrd(location, rec)
      rescue Exception => ex
        logger.error(ex.message + " - archive_entry was saved before this error\n")
        logger.error(ex.backtrace)
      end
    end

  end

  def get_rise_set(password, date, location)
    Riseset.find_by_location_and_month_and_day(location, date.month, date.day)
  end
  
  def authenticate(name, params)
    raise "not authenticated" unless params[0] == AppConfig.service_password
  end  
end
