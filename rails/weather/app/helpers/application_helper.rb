require 'memcache'

module ApplicationHelper

  def ApplicationHelper.init_cache
    if (@cache == nil)
      @cache = MemCache::new '192.168.1.10:11211', :debug => false
    end
  end
  
  def ApplicationHelper.cache
    if @cache != nil && @cache.active?
      @cache 
    else 
      init_cache
    end
  end
 
  def ApplicationHelper.observed_conditions
    cache["01915-obs"]["weather"]
  end

  def ApplicationHelper.observed_conditions_date
    cache["01915-obs"]["observation_time"].to_s
  end

  def ApplicationHelper.observed_visibility
    cache["01915-obs"]["visibility_mi"]
  end

#  def ApplicationHelper.minutes_to_hhmm(start_tm, end_tm)
#    interval = ((end_tm - start_tm) / 60).to_i # get minutes
#    tmp = interval.divmod(60)
#    hours = tmp[0]
#    minutes = tmp[1]
#    return sprintf("%d:%02d", hours, minutes)
#  end
#  
end
