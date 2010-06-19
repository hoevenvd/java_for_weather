class AddHiPressureDateToPastSummaries < ActiveRecord::Migration
  def self.up
    add_column :past_summaries, :hiPressureDate, :datetime
    add_column :past_summaries, :lowPressureDate, :datetime
    add_column :past_summaries, :hiDewpointDate, :datetime
    add_column :past_summaries, :lowDewpointDate, :datetime
    add_column :past_summaries, :hiWindchillDate, :datetime
    add_column :past_summaries, :lowWindchillDate, :datetime
    add_column :past_summaries, :hiOutsideHumidityDate, :datetime
    add_column :past_summaries, :lowOutsideHumidityDate, :datetime
  end

  def self.down
    remove_column :past_summaries, :lowOutsideHumidityDate
    remove_column :past_summaries, :hiOutsideHumidityDate
    remove_column :past_summaries, :lowWindchillDate
    remove_column :past_summaries, :hiWindchillDate
    remove_column :past_summaries, :lowDewpointDate
    remove_column :past_summaries, :hiDewpointDate
    remove_column :past_summaries, :lowPressureDate
    remove_column :past_summaries, :hiPressureDate
  end
end
