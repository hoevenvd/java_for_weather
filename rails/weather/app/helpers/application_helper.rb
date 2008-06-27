# Methods added to this helper will be available to all templates in the application.
require 'memcache'

module ApplicationHelper

  def ApplicationHelper.init_cache
    if (@cache == nil)
      @cache = MemCache::new AppConfig.memcache_url, :debug => false
    end
  end
  
  def ApplicationHelper.cache
    if @cache != nil && @cache.active?
      @cache 
    else 
      init_cache
    end
  end
 
  def ApplicationHelper.forecast
    cache["01915-forecast"]
  end

end
