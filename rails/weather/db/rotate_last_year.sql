#run once a month to keep the regular database smaller

insert into old_archive_records 
  select * from archive_records where date < date_sub(CURDATE(), INTERVAL 90 day);
delete from archive_records where date < date_sub(CURDATE(), INTERVAL 90 day);
