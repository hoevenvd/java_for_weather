require "rubygems"
require_gem "activerecord"

class Dmprecord < ActiveRecord::Base

  Dmprecord.establish_connection(:old_weather)

  def directionOfHighWindspeed
    pp "overriding directionOfHighWindspeed"
    return Direction.to_s(self[:directionOfHighWindSpeed])
  end
  
  def preVailingDirection
    return Direction.to_s(self[:prevailingWindDirection])   
  end
  
  def Dmprecord.last_rain
    result = find_by_sql("select max(date) as rain from dmprecords where rainfall > 0;")
    if (result[0].rain == nil)
      return "unknown";
    else
      dt = ParseDate.parsedate(result[0].rain.to_s)
      return Time.local(*dt)
    end
  end

end
