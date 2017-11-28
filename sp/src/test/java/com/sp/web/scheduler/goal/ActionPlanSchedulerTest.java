package com.sp.web.scheduler.goal;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.model.User;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.KeyOutcomes;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ActionPlanSchedulerTest extends SPTestLoggedInBase {

  @Autowired
  private ActionPlanScheduler scheduler;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private SPGoalFactory spGoalFactory;
  
  @Test
  public void testScheduler() {
    try {
      
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("sPGoal");
      dbSetup.removeAll("companyActionPlanSettings");
      dbSetup.removeAll("userActionPlan");
      
      scheduler.process();
      
      final String companyId = "1";
      createActionPlan("ActionPlan1", ActionPlanType.Company, companyId, StepType.All);
      
      scheduler.process();
      
      ActionPlan actionPlan = createActionPlan("ActionPlan2", ActionPlanType.Company, null,
          StepType.TimeBased);
      
      final String actionPlanId = actionPlan.getId();
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      
      SPGoal addGoal = addGoal("Month 1: Getting to Know Comcast", actionPlan::getNextUID);
      addGoal.setDurationDays(10);
      spGoalFactory.updateGoal(addGoal);
      actionPlanDao.createOrGetPracticeAreaIdList().add(addGoal.getId());
      addGoal = addGoal("Month 2: Getting to Know Comcast", actionPlan::getNextUID);
      addGoal.setDurationDays(10);
      spGoalFactory.updateGoal(addGoal);
      actionPlanDao.createOrGetPracticeAreaIdList().add(addGoal.getId());
      CompanyActionPlanSettings capSettings = actionPlanDao.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      User user = dbSetup.getUser("admin@admin.com");
      capSettings.addUser(user.getId());
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      userActionPlan.getOrCreateActionPlanProgress(actionPlanId);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      scheduler.process();
      
      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanProgress actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlanId);
      assertThat(actionPlanProgress.getCompletedActionsForStep().size(), equalTo(1));
      
      // back dating the user action progress
      actionPlanProgress.setDaysOffset(11);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      scheduler.process();

      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      actionPlanProgress = userActionPlan.getActionPlanProgress(actionPlanId);
      assertThat(actionPlanProgress.getCompletedActionsForStep().size(), equalTo(2));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private ActionPlan createActionPlan(String name, ActionPlanType type, String companyId, StepType stepType) {
    ActionPlan actionPlan = new ActionPlan();
    actionPlan.setName(name);
    actionPlan.setActive(true);
    actionPlan.setAllMembers(true);
    actionPlan.setType(type);
    actionPlan.setStepType(stepType);
    dbSetup.addUpdate(actionPlan);
    if (companyId != null) {
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
    }
    return actionPlan;
  }
  
  private SPGoal addGoal(String name, Supplier<String> uidGenerator) {
    SPGoal goal = new SPGoal();
    goal.setCategory(GoalCategory.ActionPlan);
    goal.setStatus(GoalStatus.ACTIVE);
    goal.setName(name);
    goal.setDescription("Month 1 of the Director on-boarding experience marks the beginning "
        + "LAUNCH— the first 90 days of the process. The focus of Month 1 is on helping new "
        + "directors get to know the business.");
    KeyOutcomes keyOutcomes = new KeyOutcomes();
    keyOutcomes.setDescription("By the end of Month 1, each director will have:");
    List<String> outcomesList = new ArrayList<String>();
    outcomesList.add("A baseline understanding of Comcast, including organizational strategy,"
        + " leadership, values, and customers;");
    outcomesList.add("An understanding of the media and technology space;");
    outcomesList.add("An understanding of his/her PRISM Profile.");
    keyOutcomes.setOutcomesList(outcomesList);
    goal.setKeyOutcomes(keyOutcomes);
    final List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory devCategory1 = new DSActionCategory();
    devCategory1.setTitle("Tasks & Excercises");
    devCategory1.setStatus(GoalStatus.ACTIVE);
    devCategory1.setHelpText("ha ha ha ha.");
    final List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action1 = new DSAction();
    action1.setTitle("Download and review the Comcast Strategy PPT deck");
    Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
    permissions.put(DSActionConfig.Note, Boolean.TRUE);
    permissions.put(DSActionConfig.Feedback, Boolean.TRUE);
    permissions.put(DSActionConfig.Completion, Boolean.TRUE);
    action1.setPermissions(permissions);
    final List<DSActionData> actionData = new ArrayList<DSActionData>();
    DSActionData actionData1 = new DSActionData();
    actionData1.setLinkText("View Presentation");
    actionData1.setType(DSActionDataType.PPT);
    actionData1.setUrl("some url");
    actionData.add(actionData1);
    DSActionData actionData2 = new DSActionData();
    actionData.add(actionData2);
    actionData2.setLinkText("View Instruction");
    actionData2.setType(DSActionDataType.PDF);
    actionData2.setUrl("some url");
    action1.setActionData(actionData);
    action1.setActive(true);
    actionList.add(action1);
    devCategory1.setActionList(actionList);
    devStrategyActionCategoryList.add(devCategory1);
    goal.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    goal.updateUids(uidGenerator);
    goal.updateActionIds();
    dbSetup.addUpdate(goal);
    spGoalFactory.updateGoal(goal);
    return goal;
  }  
}
