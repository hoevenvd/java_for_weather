drop table if exists archive_records;
CREATE TABLE archive_records (
 `id` int not null auto_increment,
 `date` datetime NOT NULL,
 `location` char(10) not null,
 `outside_temp` decimal(4,1) default null,
 `high_outside_temp` decimal(4,1) default null,
 `low_outside_temp` decimal(4,1) default null,
 `pressure` float(5,3) default null,
 `outside_humidity` smallint default null,
 `rainfall` float(5,2) default null,
 `high_rain_rate` float(6,3) default null,
 `average_wind_speed` smallint default null,
 `high_wind_speed` smallint default null,
 `direction_of_high_wind_speed` smallint default null,
 `prevailing_wind_direction` smallint default null,
 `inside_temp` decimal(4,1) default null,
 `average_dewpoint` decimal(4,1) default null,
 `average_apparent_temp` smallint default null,
 `inside_humidity` smallint default null,
 `solar_radiation` int default null,
 `average_uv_index` int default null,
 `et` int default null,
 `high_solar_radation` int, // FIXME! --> radiation, not radation
 `high_uv_index` int default null,
 `forecastRule` int default null,
 `leaf_temp_1` int default null,
 `leaf_temp_2` int default null,
 `leaf_wetness1` int default null,
 `leaf_wetness2` int default null,
 `soil_temp1` int default null,
 `soil_temp2` int default null,
 `soil_temp3` int default null,
 `soil_temp4` int default null,
 `extra_humidity1` int default null,
 `extra_humidity2` int default null,
 `extra_temp1` int default null,
 `extra_temp2` int default null,
 `extra_temp3` int default null,
 `soil_moisture1` int default null,
 `soil_moisture2` int default null,
 `soil_moisture3` int default null,
 `soil_moisture4` int default null,
 `number_of_wind_samples` int default null,
 `download_record_type` int default null,
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
  outside_temperature decimal(4,1) default null,
  outside_humidity smallint default null,
  dewpoint decimal(4,1) default null,
  apparent_temp smallint default null,
  pressure float (5,3) default null,
  bar_status varchar(25) default null,
  windspeed smallint default null,
  wind_direction smallint default null,
  is_raining tinyint(1) default null,
  rain_rate float (5,3) default null,
  ten_min_avg_wind smallint default null,
  uv smallint default null,
  solar_radiation smallint default null,
  daily_rain float (5,2) default null,
 `created_at` datetime,
 `updated_at` datetime,
  primary key (id)
 ) engine = Memory ;
 create unique index locations on current_conditions(location);
 