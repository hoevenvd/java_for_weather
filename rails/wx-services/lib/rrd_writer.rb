$: << AppConfig.rrd_lib_dir unless AppConfig.rrd_lib_dir.nil?

require 'RRD'

module RRDWriter
  def self.write_to_rrd(location, a) # a is an ArchiveRecord model object
    rrd_files_location = AppConfig.rrd_graphs[location]["rrd_files_location"]

    # update outside_temp.rrd with a.date,a.outside_temp,a.outside_temp_m
    if !a.outside_temp.nil?
	RRD.update("#{rrd_files_location}/outside_temp.rrd","#{a.date.to_i}:#{a.outside_temp}:#{a.outside_temp_m}")
    end

    # update apparent_temp.rrd with a.date,a.average_apparent_temp,a.average_apparent_temp_m
    if !a.average_apparent_temp.nil?
	RRD.update("#{rrd_files_location}/apparent_temp.rrd","#{a.date.to_i}:#{a.average_apparent_temp}:#{a.average_apparent_temp_m}")
    end

    # update inside_temp.rrd with a.date,a.inside_temp,a.inside_temp_m
    if !a.inside_temp.nil?
	RRD.update("#{rrd_files_location}/inside_temp.rrd","#{a.date.to_i}:#{a.inside_temp}:#{a.inside_temp_m}")
    end

    # update dewpoint.rrd with a.date,a.average_dewpoint,a.average_dewpoint_m
    if !a.average_dewpoint.nil?
	RRD.update("#{rrd_files_location}/dewpoint.rrd","#{a.date.to_i}:#{a.average_dewpoint}:#{a.average_dewpoint_m}")
    end

    # update outside_hum.rrd with a.date,a.outside_humidity
    if !a.outside_humidity.nil?
	RRD.update("#{rrd_files_location}/outside_hum.rrd","#{a.date.to_i}:#{a.outside_humidity}")
    end

    # update inside_hum.rrd with a.date,a.inside_humidity
    if !a.inside_humidity.nil?
	RRD.update("#{rrd_files_location}/inside_hum.rrd","#{a.date.to_i}:#{a.inside_humidity}")
    end

    # update windspeed.rrd with a.date,a.average_wind_speed,a.average_wind_speed_m
    if !a.average_wind_speed.nil?
	RRD.update("#{rrd_files_location}/windspeed.rrd","#{a.date.to_i}:#{a.average_wind_speed}:#{a.average_wind_speed_m}")
    end

    # update windgust.rrd with a.date,a.high_wind_speed,a.high_wind_speed_m
    if !a.high_wind_speed.nil?
	RRD.update("#{rrd_files_location}/windgust.rrd","#{a.date.to_i}:#{a.high_wind_speed}:#{a.high_wind_speed_m}")
    end

    # update winddirection.rrd with a.date,a.prevailing_wind_direction
    if !a.prevailing_wind_direction.nil?
	RRD.update("#{rrd_files_location}/winddirection.rrd","#{a.date.to_i}:#{a.prevailing_wind_direction}")
    end

    # update rainfall.rrd with a.date,a.rainfall,a.rainfall_m
    if !a.rainfall.nil?
	RRD.update("#{rrd_files_location}/rainfall.rrd","#{a.date.to_i}:#{a.rainfall}:#{a.rainfall_m}")
    end

    # update rainrate.rrd with a.date,a.high_rain_rate,a.high_rain_rate_m
    if !a.high_rain_rate.nil?
	RRD.update("#{rrd_files_location}/rainrate.rrd","#{a.date.to_i}:#{a.high_rain_rate}:#{a.high_rain_rate_m}")
    end

    # update pressure.rrd with a.date,a.pressure,a.pressure_m
    if !a.pressure.nil?
	RRD.update("#{rrd_files_location}/pressure.rrd","#{a.date.to_i}:#{a.pressure}:#{a.pressure_m}")
    end

    # update solarradiation.rrd with a.date,a.average_solar_radiation
    if !a.average_solar_radiation.nil?
	RRD.update("#{rrd_files_location}/solarradiation.rrd","#{a.date.to_i}:#{a.average_solar_radiation}")
    end


  end
  #Code here
end