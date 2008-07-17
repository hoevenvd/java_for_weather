class NoaaForecast < ActiveRecord::Base
  has_many  :forecast_periods, { :dependent => :delete_all, :order => :id }
end
