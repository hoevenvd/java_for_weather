class CreateForecastPeriods < ActiveRecord::Migration
  def self.up
    create_table :forecast_periods do |t|

      t.timestamps
    end
  end

  def self.down
    drop_table :forecast_periods
  end
end
