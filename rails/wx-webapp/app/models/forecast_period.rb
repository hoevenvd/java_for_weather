class ForecastPeriod < ActiveRecord::Base
  #FIXME - has_many?
  belongs_to  :NoaaForecast
end
