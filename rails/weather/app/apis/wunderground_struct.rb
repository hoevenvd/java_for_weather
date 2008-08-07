class WundergroundStruct < ActionWebService::Struct
	member	:id,		:text
	member	:password,		:text
	member 	:dateutc,			:time 
	member	:winddir,		  	:int
	member	:windspeedmph,			:int
	member	:windgustmph,		:int
	member	:humidity,			:int
	member	:tempf,				:int
	member	:rainin,			:float
	member	:dailyrainin,		:float
	member	:baromin,			:float
	member	:dewptf,			:float
	member  :solarradiation, 	:int
	member  :visibility, 	        :int
	member	:weather,		:text
	member	:softwaretype,		:text
end
