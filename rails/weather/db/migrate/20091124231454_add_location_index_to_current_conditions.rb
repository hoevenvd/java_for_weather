class AddLocationIndexToCurrentConditions < ActiveRecord::Migration
  def self.up
    add_index :current_conditions, [:location]
  end

  def self.down
    remove_index :current_conditions, [:location]
  end
end
