package com.sp.web.controller.goal;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <code>GoalController</code> class will fetch the goals associate with the user.
 * 
 * @author pradeep
 *
 */
@Controller
public class GoalsController {
  
  @Autowired
  private GoalsControllerHelper goalControllerHelper;
  
  @RequestMapping(value = { "/goals", "/goals/autoTurnOff" }, method = RequestMethod.GET)
  public String getGoalView(Authentication token) {
    return "goals";
  }
  
  @RequestMapping(value = "/managePracticeAreas", method = RequestMethod.GET)
  public String managePracticeAreasView(Authentication token) {
    return "managePracticeAreas";
  }
  
  @RequestMapping(value = "/goals/themeView", method = RequestMethod.GET)
  public String getGoalThemeView(Authentication token) {
    return "reviewGoals";
  }
  
  @RequestMapping(value = "/demoVideo", method = RequestMethod.GET)
  public String demoVideo(Authentication token) {
    return "demoVideo";
  }
  
  /**
   * <code>getUserGoals</code> method will return the user goals as a json response.
   * 
   * @param token
   *          Logged in user profile
   * @return the SpRespones
   */
  @RequestMapping(value = "/goals/getUserGoals", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getUserGoals(Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getUserGoals, token);
  }
  
  /**
   * Controller method to the user goals for a member.
   * 
   * @param memberEmail
   *          - member email
   * @param token
   *          - logged in user
   * @return the response to the get user goals for
   */
  @RequestMapping(value = "/for/goals/getUserGoals", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getUserGoals(@RequestParam String memberEmail, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getUserGoalsFor, token, memberEmail);
  }
  
  /**
   * Controller method to get all the goals for the user, this is the summary view of the goals
   * without the articles.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get all goals request
   */
  @RequestMapping(value = "/goals/getAllGoals", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getAllGoals(Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getAllGoals, token);
  }
  
  /**
   * Controller method to add user goals to the user.
   * 
   * @param token
   *          - logged in user
   * @param goalId
   *          - the goal id
   * @return the response to the add goals request
   */
  @RequestMapping(value = "/goals/addToUserGoals", method = RequestMethod.POST)
  @ResponseBody
  @Deprecated
  public SPResponse addToUserGoals(Authentication token, @RequestParam String goalId) {
    return ControllerHelper.process(goalControllerHelper::addToUserGoals, token, goalId);
  }
  
  /**
   * Controller method to add the given article to the user goals.
   * 
   * @param token
   *          - logged in user
   * @param articleId
   *          - article
   * @return the response to the add article to goals
   */
  @RequestMapping(value = "/goals/addArticleToUserGoals", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addArticleToUserGoals(Authentication token, @RequestParam String articleId) {
    return ControllerHelper.process(goalControllerHelper::addArticleToUserGoals, token, articleId);
  }
  
  /**
   * Remove the given goal from the user's goals list.
   * 
   * @param token
   *          - logged in user
   * @param goalId
   *          - goal id
   * @return the response to the remove goals request
   */
  @RequestMapping(value = "/goals/removeUserGoals", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeUserGoals(Authentication token, String goalId) {
    return ControllerHelper.process(goalControllerHelper::removeUserGoals, token, goalId);
  }
  
  /**
   * Controller method to get all the currently selected user goals along with the articles.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get user goals request
   
  @RequestMapping(value = "/dashboard/getUserGoals", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getDashboardUserGoals(Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getDashboardUserGoals, token);
  }*/
  
  /**
   * Controller method to select the given goal for the user.
   * 
   * @param token
   *          - logged in user
   * @return the response to the select goals request
   */
  @RequestMapping(value = "/goals/updateUserGoalsSelection", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateUserGoalsSelection(@RequestParam List<String> goalId, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::updateUserGoalsSelection, token, goalId);
  }
  
  /**
   * <code>getGoalDetail</code> method will return the goals details with the active learning
   * development for the user.
   * 
   * @param goalId
   *          for which goal detail need to be fetched.
   * @param token
   *          contains the ifnormation for the logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/goals/getGoalDetail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGoalDetail(@RequestParam String goalId,
      @RequestParam(required = false, defaultValue = "") String memberEmail, Authentication token) {
    return ControllerHelper
        .process(goalControllerHelper::getGoalDetail, token, goalId, memberEmail);
  }
  
  /**
   * <code>getGoalInfo</code> method will return the goals details with the active learning
   * development.
   * 
   * @param goalId
   *          for which goal detail need to be fetched.
   * @param token
   *          contains the information for the logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/goals/getGoalInfo", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGoalInfo(@RequestParam String goalId, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getGoalInfo, token, goalId);
  }
  
  /**
   * <code>getGoalArticles</code> method will all the articles for the goals for the user.
   * development for the user.
   * 
   * @param goalId
   *          for which goal detail need to be fetched.
   * @param token
   *          contains the ifnormation for the logged in user.
   * @return the SPResponse.
   */
  @RequestMapping(value = "/goals/getGoalArticles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGoalArticles(@RequestParam String goalId,
      @RequestParam(required = false, defaultValue = "") String memberEmail, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getGoalArticles, token, goalId,
        memberEmail);
  }
  
  /**
   * <code>manageDevelopmentStragey</code> method will manaage the devleopment strategy for the
   * goals.
   * 
   * @param goalId
   *          for which mds are to be updated.
   * @param activeDsIds
   *          active ids
   * @return the resposne.
   */
  @RequestMapping(value = "/goals/updateMds", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse manageDevelopmentStragey(@RequestParam String goalId,
      @RequestParam List<String> activeDsIds, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::manageDevelopmentStragegy, token, goalId,
        activeDsIds);
  }
  
  @RequestMapping(value = "/goals/getMds", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getManageDevelopmentStrategy(@RequestParam String goalId, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getManageDevelopmentStrategy, token,
        goalId);
  }
  
  /* view developement strategy */
  @RequestMapping(value = "/viewDevelopmentStrategy", method = RequestMethod.GET)
  public String validateSendReminder(Authentication token) {
    return "viewDevelopmentStrategy";
  }
  
  /* view Goals Org strategy */
  @RequestMapping(value = "/goalsOrg", method = RequestMethod.GET)
  public String validategoalsOrg(Authentication token) {
    return "goalsOrg";
  }
  
  /* view Goals Org strategy */
  @RequestMapping(value = "/schedulePop", method = RequestMethod.GET)
  public String validateschedulePop(Authentication token) {
    return "schedulePop";
  }
  
  /**
   * Badge progress for the passed refId.
   * 
   * @param refId
   *          is the refernce id, can be a goal, tutorial or leanring porogram id.
   * @param token
   *          is the logged in user.
   * @return the badge progress.
   */
  @RequestMapping(value = "/badge/badgeProgress", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBadgeProgress(@RequestParam String refId, Authentication token) {
    return ControllerHelper.process(goalControllerHelper::getBadgeProgress, token, refId);
  }
}
