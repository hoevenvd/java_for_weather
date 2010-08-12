class AddGustDirToPastSummaries < ActiveRecord::Migration
  def self.up
    add_column :past_summaries, :gustDir, :integer
  end

  def self.down
    remove_column :past_summaries, :gustDir
  end
end
