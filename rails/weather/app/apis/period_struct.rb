class PeriodStruct < ActionWebService::Struct
	member	:start,      				            :time
	member	:end,       				            :time

	member	:outside_temp,			            :float
	member	:high_outside_temp,	            :float
	member	:low_outside_temp,	            :float

	member	:pressure,					            :float
	member	:high_pressure,			            :float
	member	:low_pressure,			            :float

	member	:outside_humidity,	            :int
	member	:high_outside_humidity,	        :int
	member	:low_outside_humidity,	        :int

	member  :dewpoint,                      :float
	member  :high_dewpoint,                 :float
	member  :low_dewpoint,                  :float

	member	:average_wind_speed,            :int
	member	:high_wind_speed,		            :int

	member  :apparent_temp,                 :int
	member  :high_apparent_temp,            :int
	member  :low_apparent_temp,             :int

	member	:rainfall,					            :float
end
