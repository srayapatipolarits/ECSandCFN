package com.sp.web.controller.goal;

import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.goal.MeetingInviteForm;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <code>Organization GoalController</code> class will fetch the organization goals associate with
 * the user.
 * 
 * @author Dax Abraham
 *
 */
@Controller
public class ActionPlanController {
  
  @Autowired
  private ActionPlanControllerHelper helper;
  
  /**
   * <code>getOrgGoals</code> method gets the users organization action plans and goals.
   * 
   * @param userEmail
   *          - user email to get the action plans for
   * @param token
   *          Logged in user profile
   * @return the response to the get organization goals request
   */
  @RequestMapping(value = "/actionPlan/getActionPlan", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getUserOrgGoals(@RequestParam(required = false) String userEmail,
      @RequestParam(required = false) String actionPlanId,
      Authentication token) {
    return ControllerHelper.process(helper::getUserOrgGoals, token, userEmail, actionPlanId);
  }
  
  /**
   * Controller method to get the available action plans for user.
   * 
   * @param token
   *          - logged in user
   * @return the list of action plans
   */
  @RequestMapping(value = "/actionPlan/getActionPlans", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getApplicableAcitonPlans(Authentication token) {
    return ControllerHelper.process(helper::getUserAcitonPlans, token);
  }
  
  /**
   * Controller method to select the given plan for the user.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param token
   *          - logged in user
   * @return the response to the select request
   */
  @RequestMapping(value = "/actionPlan/selectPlan", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse selectPlan(@RequestParam String actionPlanId, Authentication token) {
    return ControllerHelper.process(helper::selectPlan, token, actionPlanId);
  }
  
  /**
   * Controller method to check a particular action, the action can be checked or checked off by
   * sending true or false for isChecked.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param uid
   *          - the unique id
   * @param stepId
   *          - the step id
   * @param completed
   *          - is checked
   * @param token
   *          - the logged in user
   * @return the response to the complete action request
   */
  @RequestMapping(value = "/actionPlan/completeAction", method = RequestMethod.GET)
  @ResponseBody
  @Audit(actionType = LogActionType.ActionPlanCompleteAction, type = ServiceType.LEARNING_PLAN)
  public SPResponse completeAction(@RequestParam String actionPlanId, @RequestParam String stepId,
      @RequestParam String uid, @RequestParam(defaultValue = "true") boolean completed,
      Authentication token) {
    return ControllerHelper.process(helper::completeAction, token, actionPlanId, stepId, uid,
        completed);
  }
  
  /**
   * Controller method to do a calendar invite for a list of uers.
   * 
   * @param actionPlanId
   *          - action plan id
   * @param inviteForm
   *          - invite form
   * @param token
   *          - logged in user
   * @return the response to the invite
   */
  @RequestMapping(value = "/actionPlan/sendCalInvite", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse sendCalInvite(@RequestParam String actionPlanId,
      @RequestParam(required = false) String stepId, @RequestParam(required = false) String uid,
      MeetingInviteForm inviteForm, Authentication token) {
    return ControllerHelper.process(helper::sendCalInvite, token, actionPlanId, inviteForm, stepId,
        uid);
  }
  
}
