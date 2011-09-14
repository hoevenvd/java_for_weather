class AddVisibilityMAndIcons < ActiveRecord::Migration
  def self.up
      add_column("wunder_conditions", "visibility_m", :integer)
      add_column("wunder_conditions", "icon_url", :string)
  end

  def self.down
    remove_column("wunder_conditions", "visibility_m")
    remove_column("wunder_conditions", "icon_url")
  end
end
