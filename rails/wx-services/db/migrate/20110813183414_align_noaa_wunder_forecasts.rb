class AlignNoaaWunderForecasts < ActiveRecord::Migration
  def self.up
    add_column("wunder_forecasts", "last_retrieved", :datetime)
    add_column("wunder_forecasts", "creation_time", :datetime)
    remove_column("wunder_forecasts", "as_of")

    rename_column("wunder_forecast_periods", "icon_url", "icon_location")
    rename_column("wunder_forecast_periods", "forecast", "text")
  end

  def self.down
    remove_column("wunder_forecasts", "last_retrieved")
    remove_column("wunder_forecasts", "creation_time")
    add_column("wunder_forecasts", "as_of", :string)

    rename_column("wunder_forecast_periods", "icon_location", "icon_url")
    rename_column("wunder_forecast_periods", "text", "forecast")
  end
end


#mysql> describe wunder_forecast_periods;
#+--------------------+--------------+------+-----+---------+----------------+
#| Field              | Type         | Null | Key | Default | Extra          |
#+--------------------+--------------+------+-----+---------+----------------+
#| id                 | int(11)      | NO   | PRI | NULL    | auto_increment |
#| name               | varchar(255) | NO   |     | NULL    |                |
#| forecast           | text         | NO   |     | NULL    |                |
#| icon_url           | varchar(255) | YES  |     | NULL    |                |
#| wunder_forecast_id | int(11)      | YES  |     | NULL    |                |
#| created_at         | datetime     | YES  |     | NULL    |                |
#| updated_at         | datetime     | YES  |     | NULL    |                |
#+--------------------+--------------+------+-----+---------+----------------+
#7 rows in set (0.01 sec)



#mysql> describe forecast_periods;
#+------------------+--------------+------+-----+---------+----------------+
#| Field            | Type         | Null | Key | Default | Extra          |
#+------------------+--------------+------+-----+---------+----------------+
#| id               | int(11)      | NO   | PRI | NULL    | auto_increment |
#| noaa_forecast_id | bigint(20)   | NO   | MUL | 0       |                |
#| name             | varchar(20)  | NO   |     |         |                |
#| text             | text         | YES  |     | NULL    |                |
#| created_at       | datetime     | YES  |     | NULL    |                |
#| icon_location    | varchar(255) | NO   |     |         |                |
#| updated_at       | datetime     | YES  |     | NULL    |                |
#| temp             | bigint(20)   | YES  |     | NULL    |                |
#| weather          | text         | YES  |     | NULL    |                |
#| pop              | bigint(20)   | YES  |     | NULL    |                |
#+------------------+--------------+------+-----+---------+----------------+
#10 rows in set (0.01 sec)

