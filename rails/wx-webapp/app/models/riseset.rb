class Riseset < ActiveRecord::Base
	#TODO - bug where if the hour gmt is past midnight (say, 0, 1, or 2) then the date comparison fails and dark?
	# thinks it is dark when it is not
  def self.riseset(location, date)
    r = Riseset.find_by_location_and_month_and_day(location,
            date.month, date.day)
    { :rise => Time.gm(date.year, date.month, date.day, r.rise.hour, r.rise.min),
      :set => Time.gm(date.year, date.month, r.set.hour < r.rise.hour ? date.day + 1: date.day, r.set.hour, r.set.min) }
  end

  # a very conservative measurement of light and dark. when in doubt, it's light.
  def self.dark?(location, date)
    date = date.utc
    r = Riseset.riseset(location, date)
    #find_by_location_and_month_and_day(AppConfig.climate_location, date.month, date.day)
    return ((date < r[:rise] - 1.hour) or (date >= r[:set] + 1.hour))
  end
end
#t = Time.gm(Time.now.year, Time.now.month, Time.now.day, r[:set].hour, r[:set].min).localtime