class AddStormRainToCurrentConditions < ActiveRecord::Migration
  def self.up
    add_column :current_conditions, :storm_rain, :float
  end

  def self.down
    remove_column :current_conditions, :storm_rain
  end
end
