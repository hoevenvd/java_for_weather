require 'test_helper'

class CurrentConditionsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:current_conditions)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create current_condition" do
    assert_difference('CurrentCondition.count') do
      post :create, :current_condition => { }
    end

    assert_redirected_to current_condition_path(assigns(:current_condition))
  end

  test "should show current_condition" do
    get :show, :id => current_conditions(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => current_conditions(:one).to_param
    assert_response :success
  end

  test "should update current_condition" do
    put :update, :id => current_conditions(:one).to_param, :current_condition => { }
    assert_redirected_to current_condition_path(assigns(:current_condition))
  end

  test "should destroy current_condition" do
    assert_difference('CurrentCondition.count', -1) do
      delete :destroy, :id => current_conditions(:one).to_param
    end

    assert_redirected_to current_conditions_path
  end
end
