require 'test_helper'

# FIXME - make some tests
class RisesetTest < ActiveSupport::TestCase
  # Replace this with your real tests.
  def test_truth
    assert true
  end
# id	location	month	day	rise	      set
# 733	  01915	    1	   1	12:14:00	21:22:00
  def test_dark?
    d = Time.local(2011, 1, 3, 17, 00)
    # 17:00 EST is 22:00 UTC, right?
    assert(Riseset.dark?("01915", d))
    d = Time.local(2011, 1, 3, 5, 00)

    assert(Riseset.dark?("01915", d))
    assert(Riseset.dark?("01915", d.utc))
  end
end
