# ping tom.org
require 'net/http'

h = Net::HTTP.new('www.tom.org', 80)
resp, data = h.get('/javascripts/effects.js', nil )
if !resp.code.to_i.eql?(200)
  puts "http not ok"
else
  puts "ok: " + Time.now.to_s
end

