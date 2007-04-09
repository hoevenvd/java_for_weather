class WundergroundStruct < ActionWebService::Struct
	member 	:dateutc,			:time 
	member	:winddir,		  	:int
	member	:windspeed,			:int
	member	:windgustmph,		:int
	member	:humidity,			:int
	member	:tempf,				:int
	member	:rainin,			:float
	member	:dailyrainin,		:float
	member	:baromin,			:float
	member	:dewptf,			:float
	member  :solarradiation, 	:int
	member	:softwaretype,		:text
end
