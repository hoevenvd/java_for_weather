class NoaaForecast < ActiveRecord::Base
  has_many  :forecast_periods, { :dependent => :delete_all, :order => :id }
  named_scope :latest, lambda { |location| {:conditions => ["location = ?", location]} }
end
