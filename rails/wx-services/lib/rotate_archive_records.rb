# move older than last year from archive_records to archive_records_archive
# move contents of last_year to archive_records_archive
# move records from last year from archive_records to archive_records_lastyear

find_or_initialize_by_date_and_location()

# find_all_by...
# >> Time.now.utc.at_beginning_of_year - 1.year

# "move" means:
#   get one from a result set, 
#   search for it in the destination by date and location
#   delete the original
#   insert the new one

# transactions:
# 
# Account.transaction do 
#   account1.deposit(100) 
#   account2.withdraw(100) 
# end 

# ArchiveRecord.really_old
