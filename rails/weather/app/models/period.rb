require 'pp'
require "rubygems"
gem "activerecord"
	
class Period
  SQL_FORMAT = "%Y-%m-%d %H:%M:%S"
  
  attr_reader :start_time, :end_time
  attr_reader :start_time_sql, :end_time_sql
  def initialize(pd_start, pd_end)
    @start_time = pd_start
    @start_time_sql = @start_time.getutc.strftime(SQL_FORMAT)
    @end_time = pd_end
    @end_time_sql = @end_time.getutc.strftime(SQL_FORMAT)
  end
  
  def Period.rolling_hour
    end_tm = Time.now
    start_tm = 1.hour.ago
  	return Period.new(start_tm, end_tm)
  end
  
  def Period.this_hour
    end_tm = Time.now
    start_tm = end_tm.change(:min => 0)
  	return Period.new(start_tm, end_tm)
  end
  		
  def Period.last_hour
    tm = Time.now
    end_tm = tm.change(:min => 0)
    start_tm = end_tm.ago(1.hour)
  	return Period.new(start_tm, end_tm)
  end
  		
  def Period.today
  	now = Time.now
  	start_tm = now.at_midnight # midnight today
  	end_tm = start_tm.tomorrow
  	return Period.new(start_tm, end_tm)
  end

  def Period.yesterday
  	end_tm = Time.now.at_midnight # midnight today
  	start_tm = end_tm.ago(1.day)
  	return Period.new(start_tm, end_tm)
  end
  
  def Period.this_week
  	end_tm = Time.now
  	start_tm = end_tm.at_beginning_of_week
  	return Period.new(start_tm, end_tm)
  end

  def Period.this_month
  	end_tm = Time.now
  	start_tm = end_tm.at_beginning_of_month
  	return Period.new(start_tm, end_tm)
  end

  def Period.this_year
  	end_tm = Time.now
  	start_tm = end_tm.at_beginning_of_year
  	return Period.new(start_tm, end_tm)
  end

  def Period.last_week
  	end_tm = Time.now.at_beginning_of_week
  	start_tm = end_tm.ago(1.week)
  	return Period.new(start_tm, end_tm)
  end

  def Period.last_month
  	end_tm = Time.now.at_beginning_of_month
  	start_tm = end_tm.ago(1.month)
  	return Period.new(start_tm, end_tm)
  end
  
end

#pp Period.last_hour
