class WunderForecast < ActiveRecord::Base
  unloadable # hack to prevent errors about can't dup Nil.class (http://strd6.com/?p=250)
  has_many  :wunder_forecast_periods, { :dependent => :delete_all, :order => :id }
  has_many  :wunder_forecast_period_longs, { :dependent => :delete_all, :order => :id }

  named_scope :latest_priv, lambda { |location| {:conditions => ["location = ?", location]} }



  
  def self.latest(location)
    WunderForecast.latest_priv(location)[0]
  end
end
