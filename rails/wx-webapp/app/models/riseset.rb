class Riseset < ActiveRecord::Base
	# thinks it is dark when it is not
  def self.riseset(location, date)
    r = Riseset.find_by_location_and_month_and_day(location,
            date.localtime.month, date.localtime.day)
    t = { :rise => Time.gm(date.year, date.month, date.day, r.rise.hour, r.rise.min),
          :set => Time.gm(date.year, date.month, date.day, r.set.hour, r.set.min) }
    t[:set] = t[:set] + 1.day if t[:set].hour < 12
    return t
  end

  # a very conservative measurement of light and dark. when in doubt, it's light.
  def self.dark?(location, date)
    date = date.localtime
    r = Riseset.riseset(location, date)
    #find_by_location_and_month_and_day(AppConfig.climate_location, date.month, date.day)
    return ((date < r[:rise] - 1.hour) or (date >= r[:set] + 1.hour))
  end
end
#t = Time.gm(Time.now.year, Time.now.month, Time.now.day, r[:set].hour, r[:set].min).localtime