class RemovePastSummaryId < ActiveRecord::Migration
  def self.up
    remove_column :past_summaries, :past_summary_id
  end

  def self.down
    add_column :past_summaries, :past_summary_id
  end
end
