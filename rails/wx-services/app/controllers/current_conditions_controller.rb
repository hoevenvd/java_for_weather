class CurrentConditionsController < ApplicationController
  # GET /current_conditions
  # GET /current_conditions.xml
  def index
    @current_conditions = CurrentCondition.all

    respond_to do |format|
      format.html # index.html.erb
      format.xml  { render :xml => @current_conditions }
    end
  end

  # GET /current_conditions/1
  # GET /current_conditions/1.xml
  def show
    @current_condition = CurrentCondition.find_by_location(params[:id])
    respond_to do |format|
      format.html
      format.xml { render :xml => @current_condition.to_xml }
      format.yaml { render :text => @current_condition.to_yaml }
      format.json { render :text => @current_condition.to_json }
#      format.html # show.html.erb
#      format.xml  { render :xml => @current_condition }
    end
  end

  # GET /current_conditions/new
  # GET /current_conditions/new.xml
  def new
    @current_condition = CurrentCondition.new

    respond_to do |format|
      format.html # new.html.erb
      format.xml  { render :xml => @current_condition }
    end
  end

  # GET /current_conditions/1/edit
#  def edit
#    @current_condition = CurrentCondition.find_by_location(params[:id])
#  end

  # POST /current_conditions
  # POST /current_conditions.xml
  def create
    @current_condition = CurrentCondition.new(params[:current_condition])

    respond_to do |format|
      if @current_condition.save
        format.html { redirect_to(@current_condition, :notice => 'CurrentCondition was successfully created.') }
        format.xml  { render :xml => @current_condition, :status => :created, :location => @current_condition }
      else
        format.html { render :action => "new" }
        format.xml  { render :xml => @current_condition.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /current_conditions/1
  # PUT /current_conditions/1.xml
  def update
    @current_condition = CurrentCondition.find_by_location(params[:id])

    respond_to do |format|
      if @current_condition.update_attributes(params[:current_condition])
        format.html { redirect_to(@current_condition, :notice => 'CurrentCondition was successfully updated.') }
        format.xml  { head :ok }
      else
        format.html { render :action => "edit" }
        format.xml  { render :xml => @current_condition.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /current_conditions/1
  # DELETE /current_conditions/1.xml
 # def destroy
 #   @current_condition = CurrentCondition.find(params[:id])
 #   @current_condition.destroy

 #   respond_to do |format|
 #     format.html { redirect_to(current_conditions_url) }
 #     format.xml  { head :ok }
 #   end
#  end
end
