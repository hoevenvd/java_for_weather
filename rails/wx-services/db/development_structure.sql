CREATE TABLE `archive_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `location` varchar(30) NOT NULL DEFAULT '',
  `outside_temp` decimal(4,1) DEFAULT NULL,
  `high_outside_temp` decimal(4,1) DEFAULT NULL,
  `low_outside_temp` decimal(4,1) DEFAULT NULL,
  `pressure` decimal(5,3) DEFAULT NULL,
  `outside_humidity` int(11) DEFAULT NULL,
  `rainfall` float DEFAULT NULL,
  `high_rain_rate` float DEFAULT NULL,
  `average_wind_speed` int(11) DEFAULT NULL,
  `high_wind_speed` int(11) DEFAULT NULL,
  `direction_of_high_wind_speed` int(11) DEFAULT NULL,
  `prevailing_wind_direction` int(11) DEFAULT NULL,
  `inside_temp` decimal(4,1) DEFAULT NULL,
  `average_dewpoint` decimal(4,1) DEFAULT NULL,
  `average_apparent_temp` int(11) DEFAULT NULL,
  `inside_humidity` int(11) DEFAULT NULL,
  `average_solar_radiation` bigint(20) DEFAULT NULL,
  `average_uv_index` bigint(20) DEFAULT NULL,
  `et` bigint(20) DEFAULT NULL,
  `high_solar_radiation` bigint(20) DEFAULT NULL,
  `high_uv_index` bigint(20) DEFAULT NULL,
  `forecastRule` bigint(20) DEFAULT NULL,
  `leaf_temp_1` bigint(20) DEFAULT NULL,
  `leaf_temp_2` bigint(20) DEFAULT NULL,
  `leaf_wetness1` bigint(20) DEFAULT NULL,
  `leaf_wetness2` bigint(20) DEFAULT NULL,
  `soil_temp1` bigint(20) DEFAULT NULL,
  `soil_temp2` bigint(20) DEFAULT NULL,
  `soil_temp3` bigint(20) DEFAULT NULL,
  `soil_temp4` bigint(20) DEFAULT NULL,
  `extra_humidity1` bigint(20) DEFAULT NULL,
  `extra_humidity2` bigint(20) DEFAULT NULL,
  `extra_temp1` bigint(20) DEFAULT NULL,
  `extra_temp2` bigint(20) DEFAULT NULL,
  `extra_temp3` bigint(20) DEFAULT NULL,
  `soil_moisture1` bigint(20) DEFAULT NULL,
  `soil_moisture2` bigint(20) DEFAULT NULL,
  `soil_moisture3` bigint(20) DEFAULT NULL,
  `soil_moisture4` bigint(20) DEFAULT NULL,
  `number_of_wind_samples` bigint(20) DEFAULT NULL,
  `download_record_type` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `outside_temp_m` float DEFAULT NULL,
  `low_outside_temp_m` float DEFAULT NULL,
  `high_outside_temp_m` float DEFAULT NULL,
  `inside_temp_m` float DEFAULT NULL,
  `pressure_m` float DEFAULT NULL,
  `rainfall_m` float DEFAULT NULL,
  `high_rain_rate_m` float DEFAULT NULL,
  `average_wind_speed_m` float DEFAULT NULL,
  `high_wind_speed_m` float DEFAULT NULL,
  `average_dewpoint_m` float DEFAULT NULL,
  `average_apparent_temp_m` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_loc` (`date`,`location`),
  KEY `index_archive_records_on_average_apparent_temp` (`average_apparent_temp`),
  KEY `index_archive_records_on_average_dewpoint` (`average_dewpoint`),
  KEY `dates` (`date`),
  KEY `index_archive_records_on_location_and_date_and_high_outside_temp` (`location`,`date`,`high_outside_temp`),
  KEY `index_archive_records_on_location_and_date_and_high_wind_speed` (`location`,`date`,`high_wind_speed`),
  KEY `index_archive_records_on_location_and_date_and_low_outside_temp` (`location`,`date`,`low_outside_temp`),
  KEY `index_archive_records_on_location_and_date_and_rainfall` (`location`,`date`,`rainfall`),
  KEY `locations` (`location`),
  KEY `index_archive_records_on_outside_humidity` (`outside_humidity`),
  KEY `index_archive_records_on_pressure` (`pressure`)
) ENGINE=InnoDB AUTO_INCREMENT=1683 DEFAULT CHARSET=latin1;

CREATE TABLE `climates` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(30) NOT NULL,
  `month` int(11) NOT NULL,
  `day` int(11) NOT NULL,
  `avg_high_temp` int(11) DEFAULT NULL,
  `avg_low_temp` int(11) DEFAULT NULL,
  `mean_temp` int(11) DEFAULT NULL,
  `record_high_temp` int(11) DEFAULT NULL,
  `record_high_temp_year` int(11) DEFAULT NULL,
  `record_low_temp` int(11) DEFAULT NULL,
  `record_low_temp_year` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_climates_on_day` (`day`),
  KEY `index_climates_on_location` (`location`),
  KEY `index_climates_on_month` (`month`)
) ENGINE=InnoDB AUTO_INCREMENT=1099 DEFAULT CHARSET=latin1;

CREATE TABLE `current_conditions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(30) NOT NULL DEFAULT '',
  `sample_date` datetime DEFAULT NULL,
  `outside_temperature` decimal(4,1) DEFAULT NULL,
  `outside_humidity` int(11) DEFAULT NULL,
  `dewpoint` decimal(4,1) DEFAULT NULL,
  `apparent_temp` int(11) DEFAULT NULL,
  `pressure` float DEFAULT NULL,
  `bar_status` varchar(25) DEFAULT NULL,
  `windspeed` int(11) DEFAULT NULL,
  `wind_direction` int(11) DEFAULT NULL,
  `is_raining` tinyint(1) DEFAULT NULL,
  `rain_rate` float DEFAULT NULL,
  `uv` int(11) DEFAULT NULL,
  `solar_radiation` int(11) DEFAULT NULL,
  `daily_rain` float DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `inside_temperature` float DEFAULT NULL,
  `inside_humidity` int(11) DEFAULT NULL,
  `ten_min_avg_wind` int(11) DEFAULT NULL,
  `sunrise` datetime DEFAULT NULL,
  `sunset` datetime DEFAULT NULL,
  `monthly_rain` float DEFAULT NULL,
  `yearly_rain` float DEFAULT NULL,
  `storm_rain` float DEFAULT NULL,
  `outside_temperature_m` float DEFAULT NULL,
  `inside_temperature_m` float DEFAULT NULL,
  `dewpoint_m` float DEFAULT NULL,
  `apparent_temp_m` float DEFAULT NULL,
  `pressure_m` float DEFAULT NULL,
  `windspeed_m` float DEFAULT NULL,
  `rain_rate_m` float DEFAULT NULL,
  `ten_min_avg_wind_m` float DEFAULT NULL,
  `daily_rain_m` float DEFAULT NULL,
  `monthly_rain_m` float DEFAULT NULL,
  `yearly_rain_m` float DEFAULT NULL,
  `storm_rain_m` float DEFAULT NULL,
  `extra_temp1` float DEFAULT NULL,
  `extra_temp1_m` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_current_conditions_on_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `forecast_periods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `noaa_forecast_id` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(20) NOT NULL DEFAULT '',
  `text` text,
  `created_at` datetime DEFAULT NULL,
  `icon_location` varchar(255) NOT NULL DEFAULT '',
  `updated_at` datetime DEFAULT NULL,
  `temp` bigint(20) DEFAULT NULL,
  `weather` text,
  `pop` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `noaa_forecast_id` (`noaa_forecast_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;

CREATE TABLE `last_rains` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(30) NOT NULL,
  `last_rain` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_last_rains_on_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `noaa_conditions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `location` varchar(30) NOT NULL DEFAULT '',
  `updated_at` datetime DEFAULT NULL,
  `conditions` text NOT NULL,
  `as_of` datetime DEFAULT NULL,
  `visibility` bigint(20) DEFAULT NULL,
  `conditions_xml` text,
  PRIMARY KEY (`id`),
  KEY `index_noaa_conditions_on_as_of` (`as_of`),
  KEY `created_at` (`created_at`),
  KEY `location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `noaa_forecasts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `forecast_xml` text,
  `created_at` datetime DEFAULT NULL,
  `location` varchar(30) NOT NULL DEFAULT '',
  `updated_at` datetime DEFAULT NULL,
  `creation_time` datetime DEFAULT NULL,
  `last_retrieved` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_at` (`created_at`),
  KEY `location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

CREATE TABLE `old_archive_records` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime NOT NULL,
  `location` varchar(30) NOT NULL DEFAULT '',
  `outside_temp` decimal(4,1) DEFAULT NULL,
  `high_outside_temp` decimal(4,1) DEFAULT NULL,
  `low_outside_temp` decimal(4,1) DEFAULT NULL,
  `pressure` decimal(5,3) DEFAULT NULL,
  `outside_humidity` int(11) DEFAULT NULL,
  `rainfall` float DEFAULT NULL,
  `high_rain_rate` float DEFAULT NULL,
  `average_wind_speed` int(11) DEFAULT NULL,
  `high_wind_speed` int(11) DEFAULT NULL,
  `direction_of_high_wind_speed` int(11) DEFAULT NULL,
  `prevailing_wind_direction` int(11) DEFAULT NULL,
  `inside_temp` decimal(4,1) DEFAULT NULL,
  `average_dewpoint` decimal(4,1) DEFAULT NULL,
  `average_apparent_temp` int(11) DEFAULT NULL,
  `inside_humidity` int(11) DEFAULT NULL,
  `average_solar_radiation` int(11) DEFAULT NULL,
  `average_uv_index` int(11) DEFAULT NULL,
  `et` int(11) DEFAULT NULL,
  `high_solar_radiation` int(11) DEFAULT NULL,
  `high_uv_index` int(11) DEFAULT NULL,
  `forecastRule` int(11) DEFAULT NULL,
  `leaf_temp_1` int(11) DEFAULT NULL,
  `leaf_temp_2` int(11) DEFAULT NULL,
  `leaf_wetness1` int(11) DEFAULT NULL,
  `leaf_wetness2` int(11) DEFAULT NULL,
  `soil_temp1` int(11) DEFAULT NULL,
  `soil_temp2` int(11) DEFAULT NULL,
  `soil_temp3` int(11) DEFAULT NULL,
  `soil_temp4` int(11) DEFAULT NULL,
  `extra_humidity1` int(11) DEFAULT NULL,
  `extra_humidity2` int(11) DEFAULT NULL,
  `extra_temp1` int(11) DEFAULT NULL,
  `extra_temp2` int(11) DEFAULT NULL,
  `extra_temp3` int(11) DEFAULT NULL,
  `soil_moisture1` int(11) DEFAULT NULL,
  `soil_moisture2` int(11) DEFAULT NULL,
  `soil_moisture3` int(11) DEFAULT NULL,
  `soil_moisture4` int(11) DEFAULT NULL,
  `number_of_wind_samples` int(11) DEFAULT NULL,
  `download_record_type` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `outside_temp_m` float DEFAULT NULL,
  `low_outside_temp_m` float DEFAULT NULL,
  `high_outside_temp_m` float DEFAULT NULL,
  `inside_temp_m` float DEFAULT NULL,
  `pressure_m` float DEFAULT NULL,
  `rainfall_m` float DEFAULT NULL,
  `high_rain_rate_m` float DEFAULT NULL,
  `average_wind_speed_m` float DEFAULT NULL,
  `high_wind_speed_m` float DEFAULT NULL,
  `average_dewpoint_m` float DEFAULT NULL,
  `average_apparent_temp_m` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_loc` (`date`,`location`),
  KEY `index_old_archive_records_on_average_apparent_temp` (`average_apparent_temp`),
  KEY `index_old_archive_records_on_average_dewpoint` (`average_dewpoint`),
  KEY `dates` (`date`),
  KEY `locations` (`location`),
  KEY `index_old_archive_records_on_outside_humidity` (`outside_humidity`),
  KEY `index_old_archive_records_on_pressure` (`pressure`)
) ENGINE=InnoDB AUTO_INCREMENT=952 DEFAULT CHARSET=latin1;

CREATE TABLE `past_summaries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `period` varchar(20) NOT NULL,
  `avgDewpoint` decimal(6,1) DEFAULT NULL,
  `avgHumidity` bigint(20) DEFAULT NULL,
  `avgPressure` decimal(6,2) DEFAULT NULL,
  `avgTemp` decimal(6,1) DEFAULT NULL,
  `avgWindspeed` bigint(20) DEFAULT NULL,
  `avgWindchill` bigint(20) DEFAULT NULL,
  `hiDewpoint` bigint(20) DEFAULT NULL,
  `hiWindspeed` bigint(20) DEFAULT NULL,
  `hiOutsideHumidity` bigint(20) DEFAULT NULL,
  `hiPressure` decimal(6,1) DEFAULT NULL,
  `hiTemp` decimal(6,1) DEFAULT NULL,
  `hiWindchill` bigint(20) DEFAULT NULL,
  `lowDewpoint` bigint(20) DEFAULT NULL,
  `lowOutsideHumidity` bigint(20) DEFAULT NULL,
  `lowPressure` decimal(6,1) DEFAULT NULL,
  `lowTemp` decimal(6,1) DEFAULT NULL,
  `lowWindchill` bigint(20) DEFAULT NULL,
  `rain` decimal(6,2) DEFAULT NULL,
  `hiTempDate` datetime DEFAULT NULL,
  `lowTempDate` datetime DEFAULT NULL,
  `gustDate` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `startdate` datetime DEFAULT NULL,
  `enddate` datetime DEFAULT NULL,
  `degreeDays` int(11) DEFAULT NULL,
  `location` varchar(30) NOT NULL,
  `gustDir` int(11) DEFAULT NULL,
  `hiPressureDate` datetime DEFAULT NULL,
  `lowPressureDate` datetime DEFAULT NULL,
  `hiDewpointDate` datetime DEFAULT NULL,
  `lowDewpointDate` datetime DEFAULT NULL,
  `hiWindchillDate` datetime DEFAULT NULL,
  `lowWindchillDate` datetime DEFAULT NULL,
  `hiOutsideHumidityDate` datetime DEFAULT NULL,
  `lowOutsideHumidityDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_past_summaries_on_hiDewpointDate` (`hiDewpointDate`),
  KEY `index_past_summaries_on_hiOutsideHumidityDate` (`hiOutsideHumidityDate`),
  KEY `index_past_summaries_on_hiPressureDate` (`hiPressureDate`),
  KEY `index_past_summaries_on_hiWindchillDate` (`hiWindchillDate`),
  KEY `index_past_summaries_on_location_and_period` (`location`,`period`),
  KEY `index_past_summaries_on_lowDewpointDate` (`lowDewpointDate`),
  KEY `index_past_summaries_on_lowOutsideHumidityDate` (`lowOutsideHumidityDate`),
  KEY `index_past_summaries_on_lowPressureDate` (`lowPressureDate`),
  KEY `index_past_summaries_on_lowWindchillDate` (`lowWindchillDate`),
  KEY `index_past_summaries_on_period` (`period`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=latin1;

CREATE TABLE `risesets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(30) NOT NULL,
  `month` int(11) NOT NULL,
  `day` int(11) NOT NULL,
  `rise` time DEFAULT NULL,
  `set` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_risesets_on_day` (`day`),
  KEY `index_risesets_on_location` (`location`),
  KEY `index_risesets_on_month` (`month`)
) ENGINE=InnoDB AUTO_INCREMENT=1099 DEFAULT CHARSET=latin1;

CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `wunder_conditions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(30) NOT NULL,
  `conditions` text,
  `conditions_xml` text,
  `as_of` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `visibility` int(11) DEFAULT NULL,
  `visibility_m` int(11) DEFAULT NULL,
  `icon_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_wunder_conditions_on_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

CREATE TABLE `wunder_forecast_period_longs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date` datetime DEFAULT NULL,
  `high` float DEFAULT NULL,
  `high_m` float DEFAULT NULL,
  `low` float DEFAULT NULL,
  `low_m` float DEFAULT NULL,
  `conditions` text,
  `icon_location` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `wunder_forecast_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `wunder_forecast_periods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `text` text NOT NULL,
  `icon_location` varchar(255) DEFAULT NULL,
  `wunder_forecast_id` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=latin1;

CREATE TABLE `wunder_forecasts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(30) NOT NULL,
  `forecast_xml` text,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `last_retrieved` datetime DEFAULT NULL,
  `creation_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_wunder_forecasts_on_location` (`location`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

INSERT INTO schema_migrations (version) VALUES ('20090225011257');

INSERT INTO schema_migrations (version) VALUES ('20090226110404');

INSERT INTO schema_migrations (version) VALUES ('20090226111251');

INSERT INTO schema_migrations (version) VALUES ('20091121191345');

INSERT INTO schema_migrations (version) VALUES ('20091124231454');

INSERT INTO schema_migrations (version) VALUES ('20091125113000');

INSERT INTO schema_migrations (version) VALUES ('20091218124325');

INSERT INTO schema_migrations (version) VALUES ('20091221173005');

INSERT INTO schema_migrations (version) VALUES ('20091223203507');

INSERT INTO schema_migrations (version) VALUES ('20091225011946');

INSERT INTO schema_migrations (version) VALUES ('20100413012834');

INSERT INTO schema_migrations (version) VALUES ('20100419104650');

INSERT INTO schema_migrations (version) VALUES ('20100423124615');

INSERT INTO schema_migrations (version) VALUES ('20100608003026');

INSERT INTO schema_migrations (version) VALUES ('20100608235707');

INSERT INTO schema_migrations (version) VALUES ('20100615120344');

INSERT INTO schema_migrations (version) VALUES ('20100714104650');

INSERT INTO schema_migrations (version) VALUES ('20100803100010');

INSERT INTO schema_migrations (version) VALUES ('20100808111432');

INSERT INTO schema_migrations (version) VALUES ('20100822173434');

INSERT INTO schema_migrations (version) VALUES ('20100914145047');

INSERT INTO schema_migrations (version) VALUES ('20110808145830');

INSERT INTO schema_migrations (version) VALUES ('20110812113938');

INSERT INTO schema_migrations (version) VALUES ('20110813183414');

INSERT INTO schema_migrations (version) VALUES ('20110906192642');

INSERT INTO schema_migrations (version) VALUES ('20110907120524');

INSERT INTO schema_migrations (version) VALUES ('20110907181447');

INSERT INTO schema_migrations (version) VALUES ('20121006203215');