drop table if exists archive_records;
CREATE TABLE archive_records (
 `id` int not null auto_increment,
 `date` datetime NOT NULL,
 `location` char(10) not null,
 `outside_temp` decimal(4,1),
 `high_outside_temp` decimal(4,1),
 `low_outside_temp` decimal(4,1),
 `pressure` float(5,3),
 `outside_humidity` smallint,
 `rainfall` float(5,2),
 `high_rain_rate` float(6,3),
 `average_wind_speed` smallint,
 `high_wind_speed` smallint,
 `direction_of_high_wind_speed` smallint,
 `prevailing_wind_direction` smallint,
 `inside_temp` decimal(4,1),
 `average_dewpoint` decimal(4,1),
 `average_apparent_temp` smallint,
 `inside_humidity` smallint,
 `solar_radiation` int,
 `average_uv_index` int,
 `et` int,
 `high_solar_radation` int,
 `high_uv_index` int,
 `forecastRule` int,
 `leaf_temp_1` int,
 `leaf_temp_2` int,
 `leaf_wetness1` int,
 `leaf_wetness2` int,
 `soil_temp1` int,
 `soil_temp2` int,
 `soil_temp3` int,
 `soil_temp4` int,
 `extra_humidity1` int,
 `extra_humidity2` int,
 `extra_temp1` int,
 `extra_temp2` int,
 `extra_temp3` int,
 `soil_moisture1` int,
 `soil_moisture2` int,
 `soil_moisture3` int,
 `soil_moisture4` int,
 `number_of_wind_samples` int,
 `download_record_type` int,
 `created_at` datetime,
 `updated_at` datetime,
  PRIMARY KEY (id)
) engine = innoDB;
create index dates on archive_records(date desc);
create index locations on archive_records(location);
create unique index date_loc on archive_records(date,location);

drop table if exists current_conditions;
CREATE TABLE current_conditions (
 `id` int not null auto_increment,
  location char(10) not null,
  sample_date datetime,
  outside_temperature decimal(4,1),
  outside_humidity smallint,
  dewpoint decimal(4,1),
  apparent_temp smallint,
  pressure float (5,3),
  bar_status varchar(25),
  windspeed smallint,
  wind_direction smallint,
  is_raining tinyint(1),
  rain_rate float (5,3),
  ten_min_avg_wind smallint,
 `created_at` datetime,
 `updated_at` datetime,
  primary key (id)
 ) engine = Memory ;
 create unique index locations on current_conditions(location);
 
 