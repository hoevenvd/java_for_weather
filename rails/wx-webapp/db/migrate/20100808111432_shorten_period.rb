class ShortenPeriod < ActiveRecord::Migration
  def self.up
    change_column :past_summaries, :period, :string, :limit => 20, :null => false
  end

  def self.down
    change_column :past_summaries, :period, :string, :null => false
  end
end
