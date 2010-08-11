class AddAsOfIndexToNoaaConditions < ActiveRecord::Migration
  def self.up
    add_index :noaa_conditions, :as_of
  end

  def self.down
    remove_index :noaa_conditions, :as_of
  end
end
