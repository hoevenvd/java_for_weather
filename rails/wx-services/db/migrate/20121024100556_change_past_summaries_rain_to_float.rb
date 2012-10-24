class ChangePastSummariesRainToFloat < ActiveRecord::Migration
  def self.up
    change_column :past_summaries, :rain, :float
  end

  def self.down
    change_column :past_summaries, :rain, :decimal, :precision => 6, :scale => 2
  end
end
