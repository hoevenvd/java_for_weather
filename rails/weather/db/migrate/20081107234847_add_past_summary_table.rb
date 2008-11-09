class AddPastSummaryTable < ActiveRecord::Migration
  def self.up 
    create_table :past_summaries, :options => "auto_increment = 1" do |t| 
      t.integer    :past_summary_id, :null => false
      t.string     :period
      t.decimal    :avgDewpoint, :precision => 6, :scale => 1
      t.integer    :avgHumidity
      t.decimal    :avgPressure, :precision => 6, :scale => 2
      t.decimal    :avgTemp, :precision => 6, :scale => 1
      t.integer    :avgWindspeed
      t.integer    :avgWindchill
      t.integer    :hiDewpoint
      t.integer    :hiWindspeed
      t.integer    :hiHumidity
      t.decimal    :hiPressure, :precision => 6, :scale => 1
      t.decimal    :hiTemp, :precision => 6, :scale => 1
      t.integer    :hiWindchill
      t.integer    :lowDewpoint
      t.integer    :lowOutsideHumidity
      t.decimal    :lowPressure, :precision => 6, :scale => 1
      t.decimal    :lowTemp, :precision => 6, :scale => 1
      t.integer    :lowWindchill
      t.decimal    :rain, :precision => 6, :scale => 2
      t.datetime   :hiTempDate
      t.datetime   :lowTempDate
      t.datetime   :gustDate
      t.timestamps
    end      
    add_index :past_summaries, :period, :unique => true
  end 

  def self.down
    drop_table :past_summaries 
  end 
end
