class Riseset < ActiveRecord::Base
  def self.riseset date
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    { :rise => Time.gm(date.year, date.month, date.day, r.rise.hour, r.rise.min),
      :set => Time.gm(date.year, date.month, date.day, r.set.hour, r.set.min) }
  end
end
#t = Time.gm(Time.now.year, Time.now.month, Time.now.day, r[:set].hour, r[:set].min).localtime