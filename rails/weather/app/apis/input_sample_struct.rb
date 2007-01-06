class InputSampleStruct < ActionWebService::Struct
	member 	:sample_date,			:time 
	member	:temp,		  			:float
	member	:pressure,				:float
	member	:bar_status,			:string
	member	:humidity,				:int
	member	:windspeed,				:int
	member	:wind_direction,  :int
	member  :rain_rate,       :float
	member  :ten_min_avg_wind,:int
end