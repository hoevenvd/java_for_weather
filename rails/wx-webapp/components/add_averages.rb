CLIMATE_LOCATION = "01915"

def add_riseset(mon, a)
  rise_hour = a[1].split(":")[0].to_i
  rise_hour += 12 if a[2] == "PM"
  rise_minute = a[1].split(":")[1].to_i
  set_hour = a[3].split(":")[0].to_i
  set_hour += 12 if a[4] == "PM"
  set_minute = a[3].split(":")[1].to_i

  r = Riseset.new
  r.location = CLIMATE_LOCATION
  r.month = mon
  r.day = a[0].to_i
  r.rise = Time.local(Time.now.year, r.month, r.day, rise_hour, rise_minute).utc
  r.set = Time.local(Time.now.year, r.month, r.day, set_hour, set_minute).utc
  r.save!
end

def add_climate(mon, a)  
  c = Climate.new
  c.location = CLIMATE_LOCATION
  c.month = mon
  c.day = a[0].to_i
  c.avg_high_temp = a[5].to_i
  c.avg_low_temp = a[6].to_i
  c.mean_temp = a[7].to_i
  c.record_high_temp = a[8].to_i
  c.record_high_temp_year = a[9].strip.gsub('(', '').to_i
  c.record_low_temp = a[10].to_i
  c.record_low_temp_year = a[11].strip.gsub('(', '').to_i
  c.save!
end

# Day  	Sunrise  	Sunset  	Avg.High 	Avg.Low 	Mean 	RecordHigh 	RecordLow
# 1  	  7:14 AM  	4:22 PM  	    38  	   24  	   31  	70 (1876)  	-3 (1918)
# day rise rise_ampm sunset sunset_ampm avghi avglow mean rec_high rec_high_yr rec_low rec_low_yr

month = nil
Riseset.delete_all
Climate.delete_all
File.open("../../doc/boston-climate.txt").each do |line|
#  puts line
  a = line.split
  if a.length == 0
    next
  elsif a.length == 1
    month = a[0].to_i
    puts "new month: #{month}"
  elsif a.length == 12
    add_riseset(month, a)
    add_climate(month, a)
  else
    puts "error: got unexpected number of elements: #{a.length}"
    puts a
  end
end

