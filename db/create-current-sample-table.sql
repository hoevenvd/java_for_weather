use newWeather;

drop table if exists samples;

CREATE TABLE samples (
  id int not null auto_increment,
  sample_date datetime NOT NULL,
  outside_temperature float(4,1) not null,
  outside_humidity smallint not null,
  dewpoint float(4,1) not null,
  windchill float(4,1) not null,
  pressure float (5,3) not null,
  bar_status varchar(25) not null,
  windspeed smallint not null,
  wind_direction smallint not null,
  is_raining tinyint not null,
  rain_rate float (5,3) not null,
  tenMinAvgWind smallint,
  primary key (id)
 );

