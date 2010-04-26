require 'test_helper'

class WxHelperTest < ActiveSupport::TestCase #ActionView::TestCase
  def test_c_to_f
    assert_equal WxHelper.to_f(100), 212
    assert_equal WxHelper.to_f(0), 32
  end

  def test_f_to_c
    assert_equal WxHelper.to_c(212), 100
    assert_equal WxHelper.to_c(32), 0
  end

#1 mph = 0.45 m/s
#5 mph = 2,24 m/s
#8 mph = 3,58 m/s  # 5 m/s = 11.18468146 mph
  def test_mph_to_mps
    ret = WxHelper.mph_to_mps(1)
    assert_in_delta ret, 0.45, 0.01
    ret = WxHelper.mph_to_mps(5)
    assert_in_delta ret, 2.24, 0.01
    ret = WxHelper.mph_to_mps(8)
    assert_in_delta ret, 3.58, 0.01
  end

  # 1 inches = 25.4 millimeters - 1.10in  / 27.9mm
  def test_inches_to_mm
    ret = WxHelper.inches_to_mm(1.1)
    assert_in_delta ret, 27.9, 0.1
  end

  # 1 inch of mercury = 25.4 mm of mercury = 33.86 millibars
  # = 33.86 hectoPascals
  #  29.79 in / 1008.7 hPa
  def test_inches_of_hg_to_mb
    ret = WxHelper.inches_of_hg_to_mb(29.78)
    assert_in_delta ret,  1008.4, 1.0
  end

end
