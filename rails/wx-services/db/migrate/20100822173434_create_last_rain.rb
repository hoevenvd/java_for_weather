class CreateLastRain < ActiveRecord::Migration
  def self.up
    create_table :last_rains do |t|
      t.string      :location, :null => false, :limit => 30
      t.datetime    :last_rain
      t.timestamps
    end
    add_index :last_rains, :location
  end

  def self.down
    drop_table :last_rains
  end
end
