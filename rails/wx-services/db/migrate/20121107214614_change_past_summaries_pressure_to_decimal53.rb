class ChangePastSummariesPressureToDecimal53 < ActiveRecord::Migration
  def self.up
    change_column :past_summaries, :hiPressure, :decimal, :precision => 5, :scale => 3
    change_column :past_summaries, :lowPressure, :decimal, :precision => 5, :scale => 3
    change_column :past_summaries, :avgPressure, :decimal, :precision => 5, :scale => 3
  end

  def self.down
    change_column :past_summaries, :hiPressure, :decimal, :precision => 6, :scale => 1
    change_column :past_summaries, :lowPressure, :decimal, :precision => 6, :scale => 1
    change_column :past_summaries, :avgPressure, :decimal, :precision => 6, :scale => 2
  end
end




