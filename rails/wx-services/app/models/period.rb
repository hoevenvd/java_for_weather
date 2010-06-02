require 'pp'
require "rubygems"
require "active_record"
	
class Period
  
  attr_reader :start_time, :end_time
  attr_reader :start_time_sql, :end_time_sql
  attr_reader :period_name
  
  def initialize(pd_start, pd_end, period_name = nil)
    @period_name = period_name
    @start_time = pd_start
    @start_time_sql = @start_time.getutc.to_s(:db)
    @end_time = pd_end
    @end_time_sql = @end_time.getutc.to_s(:db)
  end
  
  def Period.rolling_hour
    end_tm = Time.now.in_time_zone
    start_tm = 1.hour.ago.in_time_zone
    return Period.new(start_tm, end_tm, "ROLLLING_HOUR")
  end
  
  def Period.this_hour
    end_tm = Time.now.in_time_zone
    start_tm = end_tm.change(:min => 0)
    return Period.new(start_tm, end_tm, "ROLLLING_HOUR")
  end
  		
  def Period.last_hour
    tm = Time.now.in_time_zone
    end_tm = tm.change(:min => 0)
    start_tm = end_tm.ago(1.hour)
    return Period.new(start_tm, end_tm, "LAST_HOUR")
  end
  		
  def Period.today
    now = Time.now.in_time_zone
    start_tm = now.at_midnight # midnight today
    end_tm = start_tm.tomorrow
    return Period.new(start_tm, end_tm, "TODAY")
  end

  def Period.yesterday
    end_tm = Time.now.in_time_zone.at_midnight # midnight today
    start_tm = end_tm.ago(1.day)
    return Period.new(start_tm, end_tm, "YESTERDAY")
  end
  
  def Period.this_week
    end_tm = Time.now.in_time_zone
    start_tm = end_tm.at_beginning_of_week
    return Period.new(start_tm, end_tm, "THIS_WEEK")
  end

  def Period.this_month
    end_tm = Time.now.in_time_zone
    start_tm = end_tm.at_beginning_of_month
    return Period.new(start_tm, end_tm, "THIS_MONTH")
  end

  def Period.this_year
    end_tm = Time.now.in_time_zone
    start_tm = end_tm.at_beginning_of_year
    return Period.new(start_tm, end_tm, "THIS_YEAR")
  end

  def Period.last_week
    end_tm = Time.now.in_time_zone.at_beginning_of_week
    start_tm = end_tm.ago(1.week)
    return Period.new(start_tm, end_tm, "LAST_WEEK")
  end

  def Period.last_month
    end_tm = Time.now.in_time_zone.at_beginning_of_month
    start_tm = end_tm.ago(1.month)
    return Period.new(start_tm, end_tm, "LAST_MONTH")
  end
  
end

#pp Period.last_hour
