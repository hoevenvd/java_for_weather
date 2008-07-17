DROP TABLE IF EXISTS `archive_records`;
CREATE TABLE  `archive_records` (
  `id` int(11) NOT NULL auto_increment,
  `date` datetime NOT NULL default '0000-00-00 00:00:00',
  `location` char(10) NOT NULL default '',
  `outside_temp` decimal(4,1) default NULL,
  `high_outside_temp` decimal(4,1) default NULL,
  `low_outside_temp` decimal(4,1) default NULL,
  `pressure` float(5,3) default NULL,
  `outside_humidity` smallint(6) default NULL,
  `rainfall` float(5,2) default NULL,
  `high_rain_rate` float(6,3) default NULL,
  `average_wind_speed` smallint(6) default NULL,
  `high_wind_speed` smallint(6) default NULL,
  `direction_of_high_wind_speed` smallint(6) default NULL,
  `prevailing_wind_direction` smallint(6) default NULL,
  `inside_temp` decimal(4,1) default NULL,
  `average_dewpoint` decimal(4,1) default NULL,
  `average_apparent_temp` smallint(6) default NULL,
  `inside_humidity` smallint(6) default NULL,
  `solar_radiation` int(11) default NULL,
  `average_uv_index` int(11) default NULL,
  `et` int(11) default NULL,
  `high_solar_radation` int(11) default NULL,
  `high_uv_index` int(11) default NULL,
  `forecastRule` int(11) default NULL,
  `leaf_temp_1` int(11) default NULL,
  `leaf_temp_2` int(11) default NULL,
  `leaf_wetness1` int(11) default NULL,
  `leaf_wetness2` int(11) default NULL,
  `soil_temp1` int(11) default NULL,
  `soil_temp2` int(11) default NULL,
  `soil_temp3` int(11) default NULL,
  `soil_temp4` int(11) default NULL,
  `extra_humidity1` int(11) default NULL,
  `extra_humidity2` int(11) default NULL,
  `extra_temp1` int(11) default NULL,
  `extra_temp2` int(11) default NULL,
  `extra_temp3` int(11) default NULL,
  `soil_moisture1` int(11) default NULL,
  `soil_moisture2` int(11) default NULL,
  `soil_moisture3` int(11) default NULL,
  `soil_moisture4` int(11) default NULL,
  `number_of_wind_samples` int(11) default NULL,
  `download_record_type` int(11) default NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `date_loc` (`date`,`location`),
  KEY `dates` (`date`),
  KEY `locations` (`location`)
) TYPE=myIsam;

DROP TABLE IF EXISTS `current_conditions`;
CREATE TABLE  `current_conditions` (
  `id` int(11) NOT NULL auto_increment,
  `location` varchar(10) NOT NULL default '',
  `sample_date` datetime default NULL,
  `outside_temperature` decimal(4,1) default NULL,
  `outside_humidity` smallint(6) default NULL,
  `dewpoint` decimal(4,1) default NULL,
  `apparent_temp` smallint(6) default NULL,
  `pressure` float(5,3) default NULL,
  `bar_status` varchar(25) default NULL,
  `windspeed` smallint(6) default NULL,
  `wind_direction` smallint(6) default NULL,
  `is_raining` tinyint(1) default NULL,
  `rain_rate` float(5,3) default NULL,
  `ten_min_avg_wind` smallint(6) default NULL,
  `uv` smallint(6) default NULL,
  `solar_radiation` smallint(6) default NULL,
  `daily_rain` float(5,2) default NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`)
) TYPE=MyISAM;;

DROP TABLE IF EXISTS `noaa_conditions`;
CREATE TABLE `noaa_conditions` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `location` varchar(20) NOT NULL default '',
  `updated_at` datetime default NULL,
  `conditions` text NOT NULL,
  `as_of` datetime default NULL,
  `visibility` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `created_at` (`created_at`),
  KEY `location` (`location`)
) ENGINE=MyISAM;

DROP TABLE IF EXISTS `noaa_forecasts`;
CREATE TABLE `noaa_forecasts` (
  `id` int(11) NOT NULL auto_increment,
  `forecast_xml` text NOT NULL,
  `created_at` datetime default NULL,
  `location` varchar(20) NOT NULL default '',
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`id`),
  KEY `created_at` (`created_at`),
  KEY `location` (`location`)
) ENGINE=MyISAM;

DROP TABLE IF EXISTS `forecast_periods`;
CREATE TABLE `forecast_periods` (
  `id` int(11) NOT NULL auto_increment,
  `noaa_forecast_id` int(11) NOT NULL default '0',
  `name` varchar(20) NOT NULL default '',
  `text` text,
  `created_at` datetime default NULL,
  `icon_location` varchar(255) NOT NULL default '',
  `updated_at` datetime default NULL,
  `temp` int(11) default NULL,
  `weather` text,
  `pop` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `noaa_forecast_id` (`noaa_forecast_id`)
) ENGINE=MyISAM;
