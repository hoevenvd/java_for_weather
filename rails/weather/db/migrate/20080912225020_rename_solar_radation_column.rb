# Correct typo in column name

class RenameSolarRadationColumn < ActiveRecord::Migration
  def self.up
    rename_column "archive_records", "high_solar_radation", "high_solar_radiation"
end

  def self.down
    rename_column "archive_records", "high_solar_radiation", "high_solar_radation"
  end
end
