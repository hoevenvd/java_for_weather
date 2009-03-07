class Riseset < ActiveRecord::Base
  def self.today_rise
    date = Time.now.utc
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    return Time.gm(date.year, date.month, date.day, r.rise.hour, r.rise.min).localtime
  end

  def self.today_set
    date = Time.now.utc
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    return Time.gm(date.year, date.month, date.day, r.set.hour, r.set.min).localtime
  end
end
