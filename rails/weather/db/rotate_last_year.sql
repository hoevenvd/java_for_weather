#run once a year to keep the regular database smaller
# keep this and years data 

insert into old_archive_records 
  select * from archive_records where date < '2008-01-01';
delete from archive_records where date < '2008-01-01';

