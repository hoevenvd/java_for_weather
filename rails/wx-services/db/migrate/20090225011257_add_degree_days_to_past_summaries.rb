class AddDegreeDaysToPastSummaries < ActiveRecord::Migration
  def self.up
    add_column :past_summaries, :degreeDays, :integer
  end

  def self.down
    remove_column :past_summaries, :degreeDays
  end
end


# Day  	Sunrise  	Sunset  	Avg.High 	Avg.Low 	Mean 	RecordHigh 	RecordLow
# 1  	  7:14 AM  	4:22 PM  	    38  	   24  	   31  	70 (1876)  	-3 (1918)
# day rise rise_ampm sunset sunset_ampm avghi avglow mean rec_high rec_high_yr rec_low rec_low_yr

