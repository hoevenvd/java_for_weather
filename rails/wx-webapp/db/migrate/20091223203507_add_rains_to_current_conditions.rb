class AddRainsToCurrentConditions < ActiveRecord::Migration
  def self.up
    add_column :current_conditions, :monthly_rain, :float
    add_column :current_conditions, :yearly_rain, :float
  end

  def self.down
    remove_column :current_conditions, :yearly_rain
    remove_column :current_conditions, :monthly_rain
  end
end
