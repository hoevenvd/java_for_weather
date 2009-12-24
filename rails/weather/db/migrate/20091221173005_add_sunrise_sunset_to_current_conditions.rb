class AddSunriseSunsetToCurrentConditions < ActiveRecord::Migration
  def self.up
    add_column :current_conditions, :sunrise, :datetime
    add_column :current_conditions, :sunset, :datetime
  end

  def self.down
    remove_column :current_conditions, :sunset
    remove_column :current_conditions, :sunrise
  end
end
