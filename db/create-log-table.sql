use newWeather;

drop table if exists log;

CREATE TABLE log (
  id int not null auto_increment,
  Date datetime,
  Logger varchar(255),
  Priority varchar(255),
  Message varchar(255),
  primary key (id)
 );

