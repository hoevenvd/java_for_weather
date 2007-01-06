#!/usr/bin/env ruby

# program to copy the contents of the table dmprecords
# (currently hardcoded for the newWeather database) and translate them
# into records suitable for adding into archive_records
#
# note: since this will use ActiveRecord for persistence instead of
# relying on a service, the records inserted will not overwrite exising
# records of the same date
# should be run from ./script/console < db/move_dmprecords.rb

class Converter
  
  def self.adjust_for_local_time_zone_offset(bad_date)
    t = Time.new  # get a local time
    gmt_date = bad_date + t.gmt_offset.abs
  end

  def self.create_archive_record(rec)
    adj_date = adjust_for_local_time_zone_offset(rec[:date])
    dup = ArchiveRecord.find_by_date_and_location(adj_date, rec[:location])
    # look for existing record
    # if not found
    if dup.nil?
      # create a new ArchiveRecord instance
      new_rec = ArchiveRecord.new
      # set fields
      # have to adjust by timezone offset
      new_rec[:date] = adj_date
      new_rec[:location] = rec[:location]
      new_rec[:outside_temp] = rec[:outsideTemperature]
      new_rec[:high_outside_temp] = rec[:highOutTemperature]
      new_rec[:low_outside_temp] = rec[:lowOutTemperature]
      new_rec[:pressure] = rec[:barometer]
      new_rec[:outside_humidity] = rec[:outsideHumidity]
      new_rec[:rainfall] = rec[:rainfall]
      new_rec[:high_rain_rate] = rec[:highRainRate]
      new_rec[:average_wind_speed] = rec[:averageWindSpeed]
      new_rec[:high_wind_speed] = rec[:highWindSpeed]
      new_rec[:direction_of_high_wind_speed] = rec[:directionOfHighWindSpeed]
      new_rec[:prevailing_wind_direction] = rec[:prevailingWindDirection]
      new_rec[:inside_temp] = rec[:insideTemperature]
      new_rec[:inside_humidity] = rec[:insideHumidity]
      new_rec[:number_of_wind_samples] = rec[:numberOfWindSamples]
      # save it
      if new_rec.valid?
        new_rec.save
        puts "record saved: " + new_rec[:date].to_s
      else
        puts "record invalid" + new_rec.errors.to_s
      end
    else
      puts "dup: "
    Converter.print_rec(rec)
    end
  end
  
  def self.print_rec(rec)
    puts rec[:location] + " : " + self.adjust_for_local_time_zone_offset(rec[:date]).to_s
  end

end

page_len = 1000


CONDITIONS = "location = 01915"

pages = (Dmprecord.count(:all, :conditions => CONDITIONS) / page_len) +1
puts pages.to_s + " pages"

cur_page = 0
loop do
  puts "getting page: " + cur_page.to_s
  a = Dmprecord.find(:all, :order => "location, date desc", :conditions => CONDITIONS,
                     :limit => page_len, :offset => cur_page * page_len)
  a.each { | rec | Converter.create_archive_record(rec) }
  cur_page += 1
  break if (cur_page > pages)
end

