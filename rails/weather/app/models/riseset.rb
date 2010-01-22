class Riseset < ActiveRecord::Base
  def self.today_rise
    date = Time.now.utc
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    return Time.gm(date.year, date.month, date.day, r.rise.hour, r.rise.min).in_time_zone
  end

  def self.today_set
    date = Time.now.utc
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    return Time.gm(date.year, date.month, date.day, r.set.hour, r.set.min).in_time_zone
  end

  def self.riseset date
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    { :rise => r.rise, :set => r.set }
  end
end
