require 'memcache'

module ApplicationHelper

  def ApplicationHelper.init_cache
    if (@cache == nil)
      @cache = MemCache::new '192.168.1.3:11211', :debug => false
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

  def ApplicationHelper.forecast
    cache["01915-forecast"]
  end

end
