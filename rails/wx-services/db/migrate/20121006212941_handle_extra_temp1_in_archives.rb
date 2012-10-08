# NOTE: migration assumes no valid data in the extra_temp1 field in the database
class HandleExtraTemp1InArchives < ActiveRecord::Migration
  def self.up
    # change column type from int to float in both archive and old_archive
    change_column :archive_records, :extra_temp1, :decimal, :precision => 4, :scale => 1
    change_column :old_archive_records, :extra_temp1, :decimal, :precision => 4, :scale => 1

    add_column :archive_records, :high_extra_temp1, :decimal, :precision => 4, :scale => 1
    add_column :old_archive_records, :high_extra_temp1, :decimal, :precision => 4, :scale => 1

    add_column :archive_records, :low_extra_temp1, :decimal, :precision => 4, :scale => 1
    add_column :old_archive_records, :low_extra_temp1, :decimal, :precision => 4, :scale => 1
    # add columns for metrics in both archive and old_archive
    add_column :archive_records, :extra_temp1_m, :decimal, :precision => 4, :scale => 1
    add_column :old_archive_records, :extra_temp1_m, :decimal, :precision => 4, :scale => 1
    add_column :archive_records, :high_extra_temp1_m, :decimal, :precision => 4, :scale => 1
    add_column :old_archive_records, :high_extra_temp1_m, :decimal, :precision => 4, :scale => 1

    add_column :archive_records, :low_extra_temp1_m, :decimal, :precision => 4, :scale => 1
    add_column :old_archive_records, :low_extra_temp1_m, :decimal, :precision => 4, :scale => 1
    # add highs, lows, dates to past_summaries
    # hiExtraTemp1, lowExtraTemp1, hiExtraTemp1Date, lowExtraTemp1Date
    add_column :past_summaries, :avgExtraTemp1, :decimal, :precision => 6, :scale => 1
    add_column :past_summaries, :hiExtraTemp1, :decimal, :precision => 6, :scale => 1
    add_column :past_summaries, :lowExtraTemp1, :decimal, :precision => 6, :scale => 1
    add_column :past_summaries, :hiExtraTemp1Date, :datetime
    add_column :past_summaries, :lowExtraTemp1Date, :datetime
    # then, indexes for hiExtraTemp1 and lowExtraTemp1
    # why aren't all of the fields indexed? see schema.rb
    # example syntax: add_index :noaa_conditions, :as_of
  end

  def self.down
    change_column :archive_records, :extra_temp1, :integer
    change_column :old_archive_records, :extra_temp1, :integer
    remove_column :archive_records, :extra_temp1_m
    remove_column :old_archive_records, :extra_temp1_m
    remove_column :archive_records, :high_extra_temp1
    remove_column :old_archive_records, :high_extra_temp1
    remove_column :archive_records, :low_extra_temp1
    remove_column :old_archive_records, :low_extra_temp1
    remove_column :archive_records, :high_extra_temp1_m
    remove_column :old_archive_records, :high_extra_temp1_m
    remove_column :archive_records, :low_extra_temp1_m
    remove_column :old_archive_records, :low_extra_temp1_m
    remove_column :past_summaries, :avgExtraTemp1
    remove_column :past_summaries, :hiExtraTemp1
    remove_column :past_summaries, :lowExtraTemp1
    remove_column :past_summaries, :hiExtraTemp1Date
    remove_column :past_summaries, :lowExtraTemp1Date
  end
end
