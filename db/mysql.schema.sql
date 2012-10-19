# mysql database schema

CREATE TABLE weather_exists (
  dummy int(11) default NULL
);


CREATE TABLE weathersnapshots (
  snapdate timestamp(14) NOT NULL,
  location char(25) default NULL,
  avgintemp float default NULL,
  avgouttemp float default NULL,
  hiouttemp float default NULL,
  lowouttemp float default NULL,
  avgwindspeed int(4) default NULL,
  winddirection int(4) default NULL,
  windgust int(4) default NULL,
  barometer float default NULL,
  inhumidity int(4) default NULL,
  outhumidity int(4) default NULL,
  rain float default NULL,
  uploaded timestamp(14) NOT NULL,
  dewpoint float default NULL,
  windchill float default NULL,
  KEY snapdates(snapdate),
  KEY locations(location)
);

