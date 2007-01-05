class WeatherApi < ActionWebService::API::Base
  api_method :get_current_conditions,
             :expects => [{:location=>:string}],
             :returns => [SampleStruct]
             
  api_method :get_last_archive,
             :expects => [{:location=>:string}],
             :returns => [ArchiveStruct]
             
  api_method :put_current_conditions,
             :expects => [{:password=>:string}, 
                          {:location=>:string}, 
                          {:sample=>InputSampleStruct}]
             
  api_method :put_archive_entry,
             :expects => [{:password=>:string}, 
                          {:location=>:string},
                          {:entry=>ArchiveStruct}]

  api_method :get_period,
             :expects => [{:password=>:string}, 
                          {:location=>:string},
                          {:period=>:string}],
             :returns => [PeriodStruct]
end
