class RenameSolarRadationColumnToAverage < ActiveRecord::Migration
  def self.up
    rename_column "archive_records", "solar_radation", "average_solar_radiation"
  end

  def self.down
    rename_column "archive_records", "average_solar_radation", "solar_radiation"
  end
end
