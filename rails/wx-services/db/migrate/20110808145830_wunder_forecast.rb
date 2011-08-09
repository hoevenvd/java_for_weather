class WunderForecast < ActiveRecord::Migration
  def self.up
    create_table :wunder_conditions do |t|
      t.string  :location, {:limit => 30, :null => false}
      t.text  :conditions
      t.text  :conditions_xml
      t.int :visibility
      t.datetime :as_of
      t.timestamps
    end
    create_table :wunder_forecasts do |t|
      t.string  :location, {:limit => 30, :null => false}
      t.text  :forecast_xml
      t.string  :as_of, :limit => 30
      t.timestamps
    end
    create_table :wunder_forecast_periods do |t|
      t.string :name, :null => false
      t.text  :forecast, :null => false
      t.string :icon_url
      t.references :wunder_forecast
      t.timestamps
    end
    add_index(:wunder_forecasts, :location, :unique => :true)
    add_index(:wunder_conditions, :location, :unique => :true)
  end

  def self.down
    drop_table "wunder_conditions"
    drop_table "wunder_forecasts"
    drop_table "wunder_forecast_periods"
  end
end
