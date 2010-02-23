class Riseset < ActiveRecord::Base
  def self.riseset date
    r = Riseset.find_by_location_and_month_and_day(AppConfig.climate_location,
            date.month, date.day)
    { :rise => r.rise, :set => r.set }
  end
end
