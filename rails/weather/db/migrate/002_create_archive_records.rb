class CreateArchiveRecords < ActiveRecord::Migration
  def self.up
    create_table :archive_records do |t|
      # t.column :name, :string
    end
  end

  def self.down
    drop_table :archive_records
  end
end
