class AllowNonUniquePeriodOnPastSummaries < ActiveRecord::Migration
  def self.up
    remove_index :past_summaries, [:location, :period]
    remove_index :past_summaries, "period"
    add_index :past_summaries, [:location, :period], :name => "index_past_summaries_on_location_and_period", :unique => false
    add_index "past_summaries", :period, :name => "index_past_summaries_on_period", :unique => false
  end

  def self.down
    remove_index :past_summaries, [:location, :period]
    remove_index :past_summaries, :period
    add_index :past_summaries, [:location, :period], :name => "index_past_summaries_on_location_and_period", :unique => true
    add_index "past_summaries", "period", :name => "index_past_summaries_on_period", :unique => true
  end
end
