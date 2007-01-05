class CreateCurrentConditions < ActiveRecord::Migration
  def self.up
    create_table :current_conditions do |t|
    end
  end

  def self.down
    drop_table :current_conditions
  end
end
