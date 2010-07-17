# caching strategy

# since the data structures are the same as past_summaries, look into sti to allow for simple method overrides for
#  stuff like asking a record if it has expired, encapsulating updating extremes, etc
# provide reasonable defaults for cache ttls by current period and allow override via config
# reading: make sure a record is not too stale

# archive record posting:
#   if no record, create it
#   check to see if the ttl has expired and refresh if necessary
#   update extremes for all cached current periods - stuff like highs/lows, etc

# current conditions posting:
#   if no record, create it
#   check to see if a record has expired and create it if necessary
#   update extremes for all periods

module Cache

  HIGH_LOW_FIELDS = [:Temp, :Pressure, :Dewpoint, :Windchill, :OutsideHumidity]

  # look at each attribute
  # if it starts with hi and does not end with date
  #  construct a method name and see if the passed-in record's call result is a new high
  #    if so, set the attribute and construct a method call for that field's date setter and call it
  # same with lows

  def update_current_cache(location, archive_record)
    #check today
    today = PastSummary.find_by_period_and_location(:today, location)
    # update extremes here
    if today.nil? or today.updated_at < 5.minutes.ago # TODO make this a config parameter w/ a default of 10 mins
      pd = WxPeriod.query(Period.today, location)
      WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    end

    # dont forget last rain
    # check extremes here and update if necessary
    #check this_week
    this_week = PastSummary.find_by_period_and_location(:this_week, location)

    if this_week.nil? or this_week.updated_at < 1.hour.ago # TODO make this a config parameter w/ a default of 1 hour
      pd = WxPeriod.query(Period.this_week, location)
      WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    end

    #check this_month
    this_month = PastSummary.find_by_period_and_location(:this_month, location)

    if this_month.nil? or this_month.updated_at < 1.hour.ago # TODO make this a config parameter w/ a default of 1 hour
      pd = WxPeriod.query(Period.this_month, location)
      WxPeriod.add_to_db(pd, location) unless pd.avgTemp == nil
    end

    update_extremes(archive_record)
  end

  def update_extremes(archive_record)
    # check to see if the current record being posted represents new highs, lows, etc.
    # check year, month, week, day in that order
    # if it is the largest period extreme, then all the others too
    cache = PastSummary.current_periods

    if (archive_record.rainfall > 0)

    end
  end
end