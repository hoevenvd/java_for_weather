class AddNoaaForecastAsOf < ActiveRecord::Migration
  def self.up 
    add_column :noaa_forecasts, :creation_time, :datetime 
  end 

  def self.down 
    remove_column :noaa_forecasts, :creation_time 
  end
end
