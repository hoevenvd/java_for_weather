class AlignWunderWithNoaa < ActiveRecord::Migration
  def self.up
    add_column("wunder_conditions", "visibility", :integer)
  end

  def self.down
    remove_column("wunder_conditions", "visibility")
  end
end

#wunder

#location varchar(30)
#conditions text
#conditions_xml text
#as_of datetime
#timestamps

#add_column(table_name, column_name, type, options)
#rename_column(table_name, column_name, new_column_name): Renames a column but keeps the type and content.
#change_column(table_name, column_name, type, options): Changes the column to a different type using the same parameters as add_column.
#remove_column(table_name, column_name): Removes the column named column_name from the table called table_name.

#noaa

#location varchar(30)
#created_at datetime
#conditions text
#as_of datetime
#visibility bigint
#conditions_xml text
