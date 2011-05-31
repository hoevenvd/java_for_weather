import logging
import unittest
from wx import convert
from wx import temps

class ConversionTest(unittest.TestCase):

  def setUp(self):
    logging.info('In setUp()')

  def tearDown(self):
    logging.info('In tearDown()')

  def test_00_c_to_f(self):
    self.assertEqual(32.0, convert.to_f(0))
    self.assertEqual(212, convert.to_f(100))

  def test_01_f_to_c(self):
    self.assertEqual(100, convert.to_c(212))
    self.assertEqual(0, convert.to_c(32))

  def test_02_mph_to_mps(self):
    self.assertEqual(0.45, convert.mph_to_mps(1))
    self.assertEqual(2.24, convert.mph_to_mps(5))
    self.assertEqual(3.58, convert.mph_to_mps(8))

  #  29.79 in / 1008.8 hPa
  def test_03_inches_of_hg_to_mb(self):
    self.assertEqual(1008.8, convert.inches_of_hg_to_mb(29.79))

  # rain conversion
  # 1 inches = 25.4 millimeters
  def test_04_inches_to_mm(self):
    self.assertEqual(25.4, convert.inches_to_mm(1.0))

class ApparentTest(unittest.TestCase):
  def test_00_calc_apparent_temp (self):
    self.assertEqual(27.0, temps.calc_apparent_temp(32.0, 50, 5))
    self.assertEqual(25.0, temps.calc_apparent_temp(35.0, 50, 15))
    self.assertEqual(-19.0, temps.calc_apparent_temp(0.0, 50, 15))
    self.assertEqual(-53.0, temps.calc_apparent_temp(-15.0, 50, 55))
    self.assertRaises(ValueError, temps.calc_apparent_temp, None, 0, 0)
    self.assertEqual(50, temps.calc_apparent_temp(50, None, None))

  def test_01_test_noop_windchill(self):
    self.assertEqual(80, temps.calc_windchill(80, 50))
    
  def test_02_test_noop_heat_index(self):
    self.assertEqual(40, temps.calc_heat_index(40, 50))
