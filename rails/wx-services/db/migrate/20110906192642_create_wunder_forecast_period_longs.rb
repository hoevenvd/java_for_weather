class CreateWunderForecastPeriodLongs < ActiveRecord::Migration
  def self.up
    create_table :wunder_forecast_period_longs do |t|
      t.datetime  :date
      t.float :high
      t.float :high_m 
      t.float :low
      t.float :low_m
      t.text :conditions
      t.string :icon_location
      t.timestamps
    end
end

    def self.down
    drop_table :wunder_forecast_period_longs
    end
end

#mysql> describe wunder_forecast_period_longs;
#+---------------+--------------+------+-----+---------+----------------+
#| Field         | Type         | Null | Key | Default | Extra          |
#+---------------+--------------+------+-----+---------+----------------+
#| id            | int(11)      | NO   | PRI | NULL    | auto_increment |
#| date          | datetime     | NO   |     | NULL    |                |
#| high          | float        | YES  |     | NULL    |                |
#| high_m        | float        | YES  |     | NULL    |                |
#| low           | float        | YES  |     | NULL    |                |
#| low_m         | float        | YES  |     | NULL    |                |
#| conditions    | text         | YES  |     | NULL    |                |
#| icon_location | varchar(255) | YES  |     | NULL    |                |
#+---------------+--------------+------+-----+---------+----------------+
