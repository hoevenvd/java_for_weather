class PastSummary < ActiveRecord::Base
  include WxUtils
  named_scope :current_periods, lambda {{:conditions => ["enddate > ? and location = \'#{AppConfig.location}\'", Time.now.utc]}}
end
