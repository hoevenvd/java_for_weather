class NoaaConditions < ActiveRecord::Base
  named_scope :latest, lambda { |location| {:conditions => ["location = ?", location], :order => "as_of desc", :limit => 1} }
end
