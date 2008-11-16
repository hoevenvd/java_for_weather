class AddLastRetrieved < ActiveRecord::Migration
  def self.up
    add_column :noaa_forecasts, :last_retrieved, :datetime 
  end

  def self.down
    remove_column :noaa_forecasts, :last_retrieved 
  end
end
