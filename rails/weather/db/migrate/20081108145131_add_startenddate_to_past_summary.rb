class AddStartenddateToPastSummary < ActiveRecord::Migration
  def self.up
    add_column :past_summaries, :startdate, :datetime
    add_column :past_summaries, :enddate, :datetime
  end

  def self.down
    remove_column :past_summaries, :enddate
    remove_column :past_summaries, :startdate
  end
end
