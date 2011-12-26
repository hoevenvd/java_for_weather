class WunderConditions < ActiveRecord::Base
  named_scope :private_latest, lambda { |location| {:conditions => ["location = ?", location], :order => "as_of desc", :limit => 1} }
  
  def self.latest(location)
    WunderConditions.private_latest(location)[0]
  end
end
