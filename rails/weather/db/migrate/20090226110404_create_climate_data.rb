class CreateClimateData < ActiveRecord::Migration
  def self.up
    create_table :climates do |t|
      t.string  :location, :null => false
      t.integer :month, :null => false
      t.integer :day, :null => false
      t.integer :avg_high_temp
      t.integer :avg_low_temp
      t.integer :mean_temp
      t.integer :record_high_temp
      t.integer :record_high_temp_year
      t.integer :record_low_temp
      t.integer :record_low_temp_year
      t.timestamps
    end
    add_index :climates, :location
    add_index :climates, :month
    add_index :climates, :day
  end

  def self.down
    drop_table :climates
  end
end

# Day  	Sunrise  	Sunset  	Avg.High 	Avg.Low 	Mean 	RecordHigh 	RecordLow
# 1  	  7:14 AM  	4:22 PM  	    38  	   24  	   31  	70 (1876)  	-3 (1918)
# day rise rise_ampm sunset sunset_ampm avghi avglow mean rec_high rec_high_yr rec_low rec_low_yr

