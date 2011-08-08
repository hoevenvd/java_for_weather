require 'rexml/document'
include REXML
file = File.new("wunderground-forecast.xml")
doc = Document.new(file)
root = doc.elements["forecast"]
quick_forecast = root.elements["txt_forecast"]
simple_forecast = root.elements["simpleforecast"]
quick_forecast.elements.each do |f|
  puts f
end
