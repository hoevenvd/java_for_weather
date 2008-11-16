class NoaaForecast < ActiveRecord::Base
  has_many  :forecast_periods, { :dependent => :delete_all, :order => :id }
  named_scope :latest_priv, lambda { |location| {:conditions => ["location = ?", location]} }
  
  def self.latest(location)
    NoaaForecast.latest_priv(location)[0]
  end
end
