class AddLocationToPastSummary < ActiveRecord::Migration
  def self.up
    add_column :past_summaries, :location, :string, :limit => 30, :null => false
#    remove_index :past_summaries, :period
    add_index :past_summaries, [:location, :period], :name => "index_past_summaries_on_location_and_period", :unique => true
    PastSummary.delete_all
  end

  def self.down
    remove_index :past_summaries, [:location, :period]
    remove_column :past_summaries, :location
#    add_index "past_summaries", ["period"], :name => "index_past_summaries_on_period", :unique => true
  end
end
