package com.sp.web.controller.admin.themes;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.goal.GoalForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>ThemeController</code> define the interfaces to manage the themes in the SP.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class AdminGoalsController {
  
  /**
   * AdminGoalsControllerHelper is the controller helper class for the admin.
   */
  @Autowired
  private AdminGoalsControllerHelper adminGoalsControllerHelper;
  
  /**
   * createNewGoal method will create the new goal for individual or buisness.
   * 
   * @param goalForm
   *          goalForm containing the input parameters.
   * @param token
   *          adminstrator loggd in user.
   * @return the goal response.
   */
  @RequestMapping(value = "/sysAdmin/goals/createTheme", method = RequestMethod.GET)
  public String createThemeView() {
    return "createTheme";
  }
  
  @RequestMapping(value = "/sysAdmin/goals/themeListings", method = RequestMethod.GET)
  public String themeListingsView() {
    return "themeListings";
  }
  
  @RequestMapping(value = "/sysAdmin/goals/themeDetails", method = RequestMethod.GET)
  public String themeDetailsView() {
    return "themeDetails";
  }
  
  @RequestMapping(value = "/sysAdmin/goals/createGoal",method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createNewGoal(GoalForm goalForm, Authentication token) {
    return ControllerHelper.process(adminGoalsControllerHelper::createNewGoal, token, goalForm);
  }
  
  /**
   * createNewGoal method will create the new goal for individual or buisness.
   * 
   * @param goalForm
   *          goalForm containing the input parameters.
   * @param token
   *          adminstrator loggd in user.
   * @return the goal response.
   */
  @RequestMapping(value = "/sysAdmin/goals/updateGoal", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateGoal(@RequestParam String goalId, GoalForm goalForm,
      Authentication token) {
    return ControllerHelper
        .process(adminGoalsControllerHelper::updateGoal, token, goalForm, goalId);
  }
  
  @RequestMapping(value = "/sysAdmin/goals/getAllGoals",method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllGoals(Authentication token) {
    return ControllerHelper.process(adminGoalsControllerHelper::getAllGoals, token);
  }
  
  
  /**
   * deactivate method will create the new goal for individual or buisness.
   * 
   * @param goalForm
   *          goalForm containing the input parameters.
   * @param token
   *          adminstrator loggd in user.
   * @return the goal response.
   */
  @RequestMapping(value = "/sysAdmin/goals/deactivate",method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deactivateGoal(@RequestParam String goalId,
      Authentication token) {
    
    return ControllerHelper.process(adminGoalsControllerHelper::deactivateGoal, token, goalId);
  }
  
  /**
   * activate method will create the new goal for individual or buisness.
   * 
   * @param goalForm
   *          goalForm containing the input parameters.
   * @param token
   *          adminstrator loggd in user.
   * @return the goal response.
   */
  @RequestMapping(value = "/sysAdmin/goals/activate",method = RequestMethod.POST)
  @ResponseBody
  public SPResponse activateGoal(@RequestParam String goalId,
      Authentication token) {
    return ControllerHelper.process(adminGoalsControllerHelper::activateGoal, token, goalId);
  }
  
  
  /**
   * activate method will create the new goal for individual or buisness.
   * 
   * @param goalForm
   *          goalForm containing the input parameters.
   * @param token
   *          adminstrator loggd in user.
   * @return the goal response.
   */
  @RequestMapping(value = "/sysAdmin/goals/getGoalDetail",method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGoalDetail(@RequestParam String goalId,
      Authentication token) {
    return ControllerHelper.process(adminGoalsControllerHelper::getGoalDetail, token, goalId);
  }
  
  /**
   * View For Practice Area Screen
   * 
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/practiceAdminArea", method = RequestMethod.GET)
  public String validatePracticeAdminArea(Authentication token) {
    return "practiveAreaList";
  }
  /**
   * View For View Detai Screen
   * 
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/practiceAreaViewDetail", method = RequestMethod.GET)
  public String validatePractiveAreaViewDetail(Authentication token) {
    return "practiceAreaViewDetail";
  }
  /**
   * View For View Detai Screen
   * 
   */
  @RequestMapping(value = "/sysAdmin/practiceArea/createPracticeAreaViewDetail", method = RequestMethod.GET)
  public String validateCreatePractiveAreaViewDetail(Authentication token) {
    return "createPracticeAreaViewDetail";
  }
  
}
