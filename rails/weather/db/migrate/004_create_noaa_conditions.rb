class CreateNoaaConditions < ActiveRecord::Migration
  def self.up
    create_table :noaa_conditions do |t|

      t.timestamps
    end
  end

  def self.down
    drop_table :noaa_conditions
  end
end
