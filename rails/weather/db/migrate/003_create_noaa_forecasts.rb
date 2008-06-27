class CreateNoaaForecasts < ActiveRecord::Migration
  def self.up
    create_table :noaa_forecasts do |t|

      t.timestamps
    end
  end

  def self.down
    drop_table :noaa_forecasts
  end
end
