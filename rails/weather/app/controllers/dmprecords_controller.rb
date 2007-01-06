class DmprecordsController < ApplicationController
  def index
    list
    render :action => 'list'
  end

  def list
    @dmprecord_pages, @dmprecords = paginate :dmprecords, :per_page => 10
  end

end
