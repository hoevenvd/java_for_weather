class Riseset < ActiveRecord::Base
  def self.riseset(date)
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    { :rise => Time.gm(date.year, date.month, date.day, r.rise.hour, r.rise.min),
      :set => Time.gm(date.year, date.month, date.day, r.set.hour, r.set.min) }
  end

  # a very conservative measurement of light and dark. when in doubt, its light.
  def self.dark?(location, date)
    date = date.utc
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    return ((date.hour < r.rise.hour - 1) or (date.hour >= r.set.hour + 1))
  end
end
#t = Time.gm(Time.now.year, Time.now.month, Time.now.day, r[:set].hour, r[:set].min).localtime