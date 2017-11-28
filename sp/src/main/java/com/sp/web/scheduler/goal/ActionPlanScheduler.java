package com.sp.web.scheduler.goal;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.form.Operation;
import com.sp.web.model.User;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The scheduler class for company activity feeds.
 */
@Component
public class ActionPlanScheduler {
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * The method to add activity feeds to all the companies.
   */
  @Scheduled(cron = "${actionPlan.scheduler}")
  public void process() {
    // run only on job server
    if (!GenericUtils.isJobServerNode(enviornment)) {
      return;
    }
    
    // getting all the plans that are of the type
    // time and time with completion
    List<ActionPlan> actionPlans = actionPlanFactory.getActionPlanByStepType(StepType.TimeBased,
        StepType.TimeBasedWithCompletion);
    
    // iterating over all the action plans and process the
    // time validation
    actionPlans.stream().filter(ap -> ap.isActive()).forEach(this::process);
  }
  
  /**
   * Process update for the given action plan.
   * 
   * @param actionPlan
   *          - action plan
   */
  private void process(ActionPlan actionPlan) {
    
    // getting the action plan dao
    ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
    
    // get the list of companies for the action plan
    final List<String> companyIds = actionPlan.getCompanyIds();
    if (!CollectionUtils.isEmpty(companyIds)) {
      companyIds.forEach(cid -> process(cid, actionPlanDao));
    }
  }
  
  /**
   * Process all the users for the given company and action plan.
   * 
   * @param companyId
   *          - company id
   * @param actionPlan
   *          - actoin plan id
   */
  private void process(String companyId, ActionPlanDao actionPlan) {
    // getting the company action plan settings
    CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
        actionPlan.getId(), companyId);
    
    // check if the plan is not on hold for the given company
    if (!capSettings.isOnHold()) {
      // processing all the members for the company
      capSettings.getMemberIds().forEach(uid -> updateUser(uid, actionPlan));
    }
  }
  
  /**
   * Update the user.
   * 
   * @param uid
   *          - user id
   * @param actionPlan
   *          - action plan
   */
  private void updateUser(String uid, ActionPlanDao actionPlan) {
    // getting the user
    User user = userFactory.getUser(uid);
    // process update if user found
    if (user != null) {
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      actionPlanFactory.updateActionPlanProgress(user, userActionPlan, actionPlan, false, Operation.UPDATE );
      actionPlanFactory.updateUserActionPlan(userActionPlan);
    }
  }
  
}
