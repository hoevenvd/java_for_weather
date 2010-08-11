class RenameHiHumidityToHighOutsideHumidity < ActiveRecord::Migration
  def self.up
    rename_column :past_summaries, :hiHumidity, :hiOutsideHumidity 
  end

  def self.down
    rename_column :past_summaries, :hiOutsideHumidity, :hiHumidity
  end
end
