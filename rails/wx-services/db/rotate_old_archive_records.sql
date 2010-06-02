#run once a month to keep the regular database smaller
start transaction;
insert into old_archive_records 
  select * from archive_records where date < date_sub(CURDATE(), INTERVAL 60 day);
delete from archive_records where date < date_sub(CURDATE(), INTERVAL 60 day);
commit;
