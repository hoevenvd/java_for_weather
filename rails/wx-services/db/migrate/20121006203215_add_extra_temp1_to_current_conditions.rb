class AddExtraTemp1ToCurrentConditions < ActiveRecord::Migration
  def self.up
    add_column :current_conditions, :extra_temp1, :float
    add_column :current_conditions, :extra_temp1_m, :float
  end

  def self.down
    remove_column :current_conditions, :extra_temp1_m
    remove_column :current_conditions, :extra_temp1
  end
end
