class CreateSunrise < ActiveRecord::Migration
  def self.up
    create_table :risesets do |t|
      t.string  :location, :null => false
      t.integer :month, :null => false
      t.integer :day, :null => false
      t.time    :rise
      t.time    :set
    end
    add_index :risesets, :location
    add_index :risesets, :month
    add_index :risesets, :day
  end

  def self.down
    drop_table :risesets
  end
end

# Day  	Sunrise  	Sunset  	Avg.High 	Avg.Low 	Mean 	RecordHigh 	RecordLow
# 1  	  7:14 AM  	4:22 PM  	    38  	   24  	   31  	70 (1876)  	-3 (1918)
# day rise rise_ampm sunset sunset_ampm avghi avglow mean rec_high rec_high_yr rec_low rec_low_yr
