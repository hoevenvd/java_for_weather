class SamplesController < ApplicationController
  def index
    show
    render :action => 'show'
  end

  def show
    @sample = Sample.find(:first)
    @conditions = ApplicationHelper.observed_conditions
    @conditions_date = ApplicationHelper.observed_conditions_date
    @visibility = ApplicationHelper.observed_visibility
  end

end
