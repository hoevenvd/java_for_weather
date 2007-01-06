use newWeather;

drop table if exists dmprecords;

CREATE TABLE dmprecords (
 id int not null auto_increment,
 `date` datetime NOT NULL default '0000-00-00 00:00:00',
 `outsideTemperature` float(4,1) NOT NULL default '0',
 `barometer` float(5,3) NOT NULL default '0',
 `outsideHumidity` int(11) NOT NULL default '0',
 `rainfall` float(5,2) NOT NULL default '0',
 dewpoint float(4,1) not null,
 windchill float(4,1) not null,
 `highRainRate` float(6,3) NOT NULL default '0',
 `averageWindSpeed` int(11) NOT NULL default '0',
 `highWindSpeed` int(11) NOT NULL default '0',
 `directionOfHiWindSpeed` int(11) NOT NULL default '0',
 `prevailingWindDirection` int(11) NOT NULL default '0',
 `numberOfWindSamples` int(11) NOT NULL default '0',
 `insideTemperature` float(4,1) NOT NULL default '0',
 `insideHumidity` int(11) NOT NULL default '0',
 `highOutTemperature` float(4,1) NOT NULL default '0',
 `lowOutTemperature` float(4,1) NOT NULL default '0',
 `solarRadiation` int(11) NOT NULL default '0',
 `averageUVIndex` int(11) NOT NULL default '0',
 `ET` int(11) NOT NULL default '0',
 `highSolarRadiation` int(11) NOT NULL default '0',
 `highUVIndex` int(11) NOT NULL default '0',
 `forecastRule` int(11) NOT NULL default '0',
 `leafTemperature1` int(11) NOT NULL default '0',
 `leafTemperature2` int(11) NOT NULL default '0',
 `leafWetness1` int(11) NOT NULL default '0',
 `leafWetness2` int(11) NOT NULL default '0',
 `soilTemperature1` int(11) NOT NULL default '0',
 `soilTemperature2` int(11) NOT NULL default '0',
 `soilTemperature3` int(11) NOT NULL default '0',
 `soilTemperature4` int(11) NOT NULL default '0',
 `downloadRecordType` int(11) NOT NULL default '0',
 `extraHumidity1` int(11) NOT NULL default '0',
 `extraHumidity2` int(11) NOT NULL default '0',
 `extraTemperature1` int(11) NOT NULL default '0',
 `extraTemperature2` int(11) NOT NULL default '0',
 `extraTemperature3` int(11) NOT NULL default '0',
 `soilMoisture1` int(11) NOT NULL default '0',
 `soilMoisture2` int(11) NOT NULL default '0',
 `soilMoisture3` int(11) NOT NULL default '0',
 `soilMoisture4` int(11) NOT NULL default '0',
 location char(10) not null default '01915',
 `recordedDate` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY (id)
);

create unique index dates on newWeather.dmprecords(date desc);
create index locations on newWeather.dmprecords(location);
