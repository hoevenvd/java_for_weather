class Round
  def Round.round_f (num, places)
    sprintf("%.#{places}f", num).to_f
  end
end
