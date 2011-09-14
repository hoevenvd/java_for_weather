class AddWunderForecastIdToTableLong < ActiveRecord::Migration
  def self.up
    add_column("wunder_forecast_period_longs", "wunder_forecast_id", :integer)
  end

  def self.down
      remove_column("wunder_forecast_period_longs", "wunder_forecast_id")
  end
end
