class AddHilowIndexesToArchiveRecords < ActiveRecord::Migration
  def self.up
    add_index :past_summaries, :hiPressureDate
    add_index :past_summaries, :lowPressureDate
    add_index :past_summaries, :hiDewpointDate
    add_index :past_summaries, :lowDewpointDate
    add_index :past_summaries, :hiWindchillDate
    add_index :past_summaries, :lowWindchillDate
    add_index :past_summaries, :hiOutsideHumidityDate
    add_index :past_summaries, :lowOutsideHumidityDate
    add_index :archive_records, :pressure
    add_index :archive_records, :average_apparent_temp
    add_index :archive_records, :average_dewpoint
    add_index :archive_records, :outside_humidity
  end
  
  def self.down
    remove_index :past_summaries, :lowOutsideHumidityDate
    remove_index :past_summaries, :hiOutsideHumidityDate
    remove_index :past_summaries, :lowWindchillDate
    remove_index :past_summaries, :hiWindchillDate
    remove_index :past_summaries, :lowDewpointDate
    remove_index :past_summaries, :hiDewpointDate
    remove_index :past_summaries, :lowPressureDate
    remove_index :past_summaries, :hiPressureDate
    remove_index :archive_records, :pressure
    remove_index :archive_records, :average_apparent_temp
    remove_index :archive_records, :average_dewpoint
    remove_index :archive_records, :outside_humidity
  end
end

