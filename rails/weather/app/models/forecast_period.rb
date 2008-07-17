class ForecastPeriod < ActiveRecord::Base
  belongs_to  :NoaaForecast
end
