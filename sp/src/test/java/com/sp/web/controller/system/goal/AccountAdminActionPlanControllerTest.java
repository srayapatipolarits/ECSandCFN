package com.sp.web.controller.system.goal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.form.goal.ActionPlanForm;
import com.sp.web.form.goal.ManageActionPlanForm;
import com.sp.web.form.goal.PracticeAreaActionForm;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;
import com.sp.web.model.goal.DSActionType;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.GroupPermissions;
import com.sp.web.model.goal.KeyOutcomes;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.SPNoteFeedbackType;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.utils.MessagesHelper;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The test cases for administration services of action plan.
 */
public class AccountAdminActionPlanControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  SPGoalFactory spGoalFactory;
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Test
  public void testGetAllCompanyActionPlans() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans").contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans")
                  .param("accountDashboardRequest", "true").param("includeUserId", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User adminUser = dbSetup.getUser("admin@admin.com");
      // adding an action plan
      createActionPlanWithUser("Test Action Plan", adminUser);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans").contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans")
                  .param("accountDashboardRequest", "true").param("includeUserId", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(1)))
          .andExpect(jsonPath("$.success.actionPlan").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final ActionPlan actionPlan = createActionPlanWithUser("Test Action Plan 2", adminUser);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans").contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans")
                  .param("accountDashboardRequest", "true").param("includeUserId", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(2)))
          .andExpect(jsonPath("$.success.actionPlan").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setEditAllowed(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans")
                  .param("accountDashboardRequest", "true").param("includeUserId", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(2)))
          .andExpect(jsonPath("$.success.actionPlan").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan3 = createActionPlan(ActionPlanType.SurePeople);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans")
                  .param("accountDashboardRequest", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(2)))
          .andExpect(jsonPath("$.success.actionPlan").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan3.setAllCompanies(true);
      actionPlanFactory.updateActionPlan(actionPlan3);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getCompanyActionPlans")
                  .param("accountDashboardRequest", "true").param("includeUserId", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanListing", hasSize(3)))
          .andExpect(jsonPath("$.success.actionPlan").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private ActionPlan createActionPlanWithUser(String name, User user) {
    return createActionPlanWithUser(name, user, ActionPlanType.Company);
  }
  
  private ActionPlan createActionPlanWithUser(String name, User user, ActionPlanType type) {
    // creating another
    ActionPlan actionPlan2 = new ActionPlan();
    actionPlan2.setId(null);
    actionPlan2.setActive(true);
    actionPlan2.setName(name);
    actionPlan2.setType(type);
    actionPlan2.setStepType(StepType.All);
    actionPlan2.setEditAllowed(true);
    actionPlanFactory.createActionPlan(actionPlan2);
    CompanyActionPlanSettings capSettings = actionPlan2.addCompany("1", actionPlanFactory);
    actionPlanFactory.updateActionPlan(actionPlan2);
    List<String> userIdList = new ArrayList<String>();
    userIdList.add(user.getId());
    capSettings.setMemberIds(userIdList);
    actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
    return actionPlan2;
  }
  
  private ActionPlan createActionPlan() {
    return createActionPlan(ActionPlanType.Company);
  }
  
  private ActionPlan createActionPlan(ActionPlanType type) {
    return createActionPlan(type, null);
  }
  
  private ActionPlan createActionPlan(ActionPlanType type, String companyId) {
    ActionPlan actionPlan = new ActionPlan();
    actionPlan.setName("Test Action Plan");
    actionPlan.setDescription("Test Action Plan descirption");
    actionPlan.setActive(true);
    actionPlan.setEditAllowed(true);
    actionPlan.setType(type);
    actionPlan.setStepType(StepType.All);
    actionPlanFactory.updateActionPlan(actionPlan);
    if (companyId != null) {
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
    }
    
    return actionPlan;
  }
  
  @Test
  public void testGetActionPlanDetails() throws Exception {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeSpGoals();
      
      final User user = dbSetup.getUser("admin@admin.com");
      user.removeRole(RoleType.SuperAdministrator);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails").param("actionPlanId", "-1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Action plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating a new action plan incorrect practice area
      final String companyId = user.getCompanyId();
      ActionPlan actionPlan = createActionPlan(ActionPlanType.Company, companyId);
      List<String> practiceAreaList = new ArrayList<String>();
      practiceAreaList.add("abc");
      actionPlan.setPracticeAreaIdList(practiceAreaList);
      dbSetup.addUpdate(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails")
                  .param("actionPlanId", actionPlan.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPGoal addGoal = addGoal();
      practiceAreaList = new ArrayList<String>();
      practiceAreaList.add(addGoal.getId());
      actionPlan.setPracticeAreaIdList(practiceAreaList);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails")
                  .param("actionPlanId", actionPlan.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addGroupAction(addGoal);
      actionPlanFactory.updateActionPlan(actionPlan);
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
      actionPlanDao.updateActionCount();
      actionPlanDao.addCompany(companyId, actionPlanFactory);
      CompanyActionPlanSettings capSettings = actionPlanFactory.createCompanyActionPlanSettings(
          companyId, actionPlanDao);
      List<String> userIdList = capSettings.getMemberIds();
      userIdList.add(user.getId());
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      final String actionPlanId = actionPlanDao.getId();
      ActionPlanProgress actionPlanProgress = userActionPlan
          .getOrCreateActionPlanProgress(actionPlanId);
      actionPlanProgress.addPracticeArea(addGoal);
      userActionPlan.addCompletion(actionPlanId, addGoal.getId(), "abc", true);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails")
                  .param("actionPlanId", actionPlan.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails")
                  .param("actionPlanId", actionPlan.getId()).param("accountDashboard", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList").doesNotExist()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanDao.setEditAllowed(false);
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails").param("actionPlanId", actionPlanId)
                  .param("accountDashboard", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList").doesNotExist()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails").param("actionPlanId", actionPlanId)
                  .param("accountDashboard", "false").param("isEdit", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCreateActionPlan() throws Exception {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeSpGoals();
      dbSetup.removeAll("companyActionPlanSettings");
      
      User user = removeSuperAdminAccess();
      
      final ActionPlanForm actionPlanForm = new ActionPlanForm();
      // actionPlanForm.setName("Test Action Plan");
      // actionPlanForm.setCompanyId("abc");
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(actionPlanForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      company.setActionPlanAdminEnabled(true);
      companyFactory.updateCompany(company);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setName("Test Action Plan");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Description not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setDescription("Test Action Plan Description.");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException")
                  .value("Need at least one practice area.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<PracticeAreaActionForm> practiceAreaList = new ArrayList<PracticeAreaActionForm>();
      PracticeAreaActionForm practiceArea = new PracticeAreaActionForm();
      practiceAreaList.add(practiceArea);
      actionPlanForm.setPracticeAreaList(practiceAreaList);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Plan type required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setType(ActionPlanType.Company);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Practice area name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String actionPlanName = "Test Practice Area";
      practiceArea.setName(actionPlanName);
      practiceArea.setDescription("Overview for the test practice area.");
      practiceArea.setStatus(GoalStatus.ACTIVE);
      Map<String, String> introVideo = new HashMap<String, String>();
      introVideo.put(Constants.PARAM_TITLE, "Video title");
      introVideo.put(Constants.PARAM_DESCRIPTION, "Video Description");
      introVideo.put(Constants.PARAM_URL, "Video URL");
      introVideo.put(Constants.PARAM_THUMBNAIL_URL, "Video Thumbnail URL");
      introVideo.put(Constants.PARAM_STATUS, GoalStatus.ACTIVE.toString());
      practiceArea.setIntroVideo(introVideo);
      KeyOutcomes keyOutcomes = new KeyOutcomes();
      keyOutcomes.setDescription("By the end of Month 1, each director will have:");
      List<String> outcomesList = new ArrayList<String>();
      outcomesList.add("A baseline understanding of Comcast, including organizational strategy,"
          + " leadership, values, and customers;");
      outcomesList.add("An understanding of the media and technology space;");
      outcomesList.add("An understanding of his/her PRISM Profile.");
      keyOutcomes.setOutcomesList(outcomesList);
      practiceArea.setKeyOutcomes(keyOutcomes);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Practice Area at least one category required.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
      DSActionCategory devStrategyActionCategory = new DSActionCategory();
      devStrategyActionCategory.setTitle("");
      devStrategyActionCategoryList.add(devStrategyActionCategory);
      practiceArea.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Category status required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      devStrategyActionCategory.setTitle("Do some action");
      devStrategyActionCategory.setStatus(GoalStatus.ACTIVE);
      final List<DSAction> actionList = new ArrayList<DSAction>();
      DSAction action1 = new DSAction();
      action1.setTitle("Our Values");
      action1.setTimeInMins(83);
      action1.setActive(true);
      action1.setType(DSActionType.Group);
      action1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
          + "Mauris condimentum lacus nisl, eget place");
      devStrategyActionCategory.setActionList(actionList);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "At least one category action required.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<DSActionData> actionData = new ArrayList<DSActionData>();
      DSActionData actionData1 = new DSActionData();
      actionData1.setTitle("Comcast Announces Fifth Back-To-School Kickoff");
      actionData1.setType(DSActionDataType.Video);
      actionData1.setLinkText("Play Video");
      actionData1.setUrl("sprepo.surepeople.com/cdn/");
      actionData1.setImageLink("sprepo.surepeople.com/cdn/");
      actionData1.setAltText("Reimagining");
      actionData.add(actionData1);
      action1.setActionData(actionData);
      actionList.add(action1);
      request = om.writeValueAsString(actionPlanForm);
      
      // result = this.mockMvc
      // .perform(
      // post("/admin/actionPlan/create")
      // .content(request)
      // .contentType(MediaType.APPLICATION_JSON)
      // .session(session))
      // .andExpect(content().contentType("application/json;charset=UTF-8"))
      // .andExpect(jsonPath("$.error").exists())
      // .andExpect(
      // jsonPath("$.error.IllegalArgumentException").value(
      // "Completion required for group type of action data."))
      // .andReturn();
      // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Map<DSActionConfig, Boolean> actionDataPermissions = new HashMap<DSActionConfig, Boolean>();
      actionDataPermissions.put(DSActionConfig.Completion, Boolean.TRUE);
      actionData1.setPermissions(actionDataPermissions);
      
      DSActionData actionData2 = new DSActionData();
      actionData2.setTitle("View our Corporate Social Responsibility Report");
      actionData2.setType(DSActionDataType.Video);
      actionData2.setLinkText("Play Video");
      actionData2.setUrl("sprepo.surepeople.com/cdn/");
      actionData2.setImageLink("sprepo.surepeople.com/cdn/");
      Map<DSActionConfig, Boolean> actionDataPermissions2 = new HashMap<DSActionConfig, Boolean>();
      actionDataPermissions2.put(DSActionConfig.Completion, Boolean.TRUE);
      actionDataPermissions2.put(DSActionConfig.Note, Boolean.TRUE);
      actionData2.setPermissions(actionDataPermissions2);
      actionData.add(actionData2);
      
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Step type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setStepType(StepType.All);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // duplicate action plan
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Action plan with the same name already exists.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setName(actionPlanForm.getName().toUpperCase());
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Action plan with the same name already exists.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testUpdateActionPlan() throws Exception {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeSpGoals();
      dbSetup.removeAll("companyActionPlanSettings");
      
      User user = removeSuperAdminAccess();
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      company.setActionPlanAdminEnabled(false);
      companyFactory.updateCompany(company);
      
      final ActionPlanForm actionPlanForm = new ActionPlanForm();
      // actionPlanForm.setName("Test Action Plan");
      // actionPlanForm.setCompanyId("abc");
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(actionPlanForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      company = companyFactory.getCompany(user.getCompanyId());
      company.setActionPlanAdminEnabled(true);
      companyFactory.updateCompany(company);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Action plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setId("abc");
      actionPlanForm.setName("Test Action Plan");
      actionPlanForm.setCreatedByCompanyId("abc");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Action plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      actionPlanForm.setId(actionPlan.getId());
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = user.getCompanyId();
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlan.setEditAllowed(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setEditAllowed(true);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Description not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setDescription("Some description for the plan.");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException")
                  .value("Need at least one practice area.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<PracticeAreaActionForm> practiceAreaList = new ArrayList<PracticeAreaActionForm>();
      PracticeAreaActionForm practiceArea = new PracticeAreaActionForm();
      practiceArea.setName("Test Practice Area");
      practiceArea.setDescription("Overview for the test practice area.");
      practiceArea.setStatus(GoalStatus.ACTIVE);
      practiceAreaList.add(practiceArea);
      actionPlanForm.setPracticeAreaList(practiceAreaList);
      List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
      DSActionCategory devStrategyActionCategory = new DSActionCategory();
      devStrategyActionCategoryList.add(devStrategyActionCategory);
      practiceArea.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
      devStrategyActionCategory.setTitle("Do some action");
      devStrategyActionCategory.setStatus(GoalStatus.ACTIVE);
      final List<DSAction> actionList = new ArrayList<DSAction>();
      DSAction action1 = new DSAction();
      action1.setTitle("Our Values");
      action1.setTimeInMins(83);
      action1.setType(DSActionType.Group);
      action1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
          + "Mauris condimentum lacus nisl, eget place");
      devStrategyActionCategory.setActionList(actionList);
      final List<DSActionData> actionData = new ArrayList<DSActionData>();
      DSActionData actionData1 = new DSActionData();
      actionData1.setTitle("Comcast Announces Fifth Back-To-School Kickoff");
      actionData1.setType(DSActionDataType.Video);
      actionData1.setLinkText("Play Video");
      actionData1.setUrl("sprepo.surepeople.com/cdn/");
      actionData1.setImageLink("sprepo.surepeople.com/cdn/");
      actionData1.setAltText("Reimagining");
      actionData.add(actionData1);
      action1.setActionData(actionData);
      actionList.add(action1);
      Map<DSActionConfig, Boolean> actionDataPermissions = new HashMap<DSActionConfig, Boolean>();
      actionDataPermissions.put(DSActionConfig.Completion, Boolean.TRUE);
      actionData1.setPermissions(actionDataPermissions);
      DSActionData actionData2 = new DSActionData();
      actionData2.setTitle("View our Corporate Social Responsibility Report");
      actionData2.setType(DSActionDataType.Video);
      actionData2.setLinkText("Play Video");
      actionData2.setUrl("sprepo.surepeople.com/cdn/");
      actionData2.setImageLink("sprepo.surepeople.com/cdn/");
      Map<DSActionConfig, Boolean> actionDataPermissions2 = new HashMap<DSActionConfig, Boolean>();
      actionDataPermissions2.put(DSActionConfig.Completion, Boolean.TRUE);
      actionDataPermissions2.put(DSActionConfig.Note, Boolean.TRUE);
      actionData2.setPermissions(actionDataPermissions2);
      actionData.add(actionData2);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Plan type required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setType(ActionPlanType.SurePeople);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Step type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setStepType(StepType.All);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Cannot modify a global plan."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan = actionPlanFactory.getActionPlan(actionPlan.getId());
      actionPlan.setType(ActionPlanType.Company);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      actionPlanForm.setType(ActionPlanType.Company);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Step Name already exists in action plan.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setName(actionPlanForm.getName().toUpperCase());
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Step Name already exists in action plan.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private User removeSuperAdminAccess() {
    User user = dbSetup.getUser("admin@admin.com");
    user.removeRole(RoleType.SuperAdministrator);
    dbSetup.addUpdate(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    return user;
  }
  
  @Test
  public void testPublish() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      User user = removeSuperAdminAccess();
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      company.setActionPlanAdminEnabled(false);
      companyFactory.updateCompany(company);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", "134")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = user.getCompanyId();
      company = companyFactory.getCompany(companyId);
      company.setActionPlanAdminEnabled(true);
      companyFactory.updateCompany(company);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", "134")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Plan already published."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setActive(false);
      actionPlan.setCreatedByCompanyId("1");
      actionPlan.getCompanyIds().clear();
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlan.setActive(false);
      actionPlan.setCreatedByCompanyId("1");
      actionPlan.getCompanyIds().clear();
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Cannot modify a global plan."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.Company);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/publish").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private void addGroupAction(SPGoal goal) {
    final List<DSActionCategory> devStrategyActionCategoryList = goal
        .getDevStrategyActionCategoryList();
    DSActionCategory categoryAction = new DSActionCategory();
    categoryAction.setTitle("Videos & Resources");
    categoryAction.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action1 = new DSAction();
    action1.setTitle("Our Values");
    action1.setTimeInMins(83);
    action1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
        + "Mauris condimentum lacus nisl, eget place");
    action1.setActive(true);
    Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
    permissions.put(DSActionConfig.Note, Boolean.TRUE);
    permissions.put(DSActionConfig.Feedback, Boolean.TRUE);
    permissions.put(DSActionConfig.Completion, Boolean.FALSE);
    action1.setPermissions(permissions);
    final List<DSActionData> actionData = new ArrayList<DSActionData>();
    DSActionData actionData1 = new DSActionData();
    actionData1.setTitle("Comcast Announces Fifth Back-To-School Kickoff");
    actionData1.setType(DSActionDataType.Video);
    actionData1.setLinkText("Play Video");
    actionData1.setUrl("sprepo.surepeople.com/cdn/");
    actionData1.setImageLink("sprepo.surepeople.com/cdn/");
    actionData1.setAltText("Reimagining");
    Map<DSActionConfig, Boolean> actionDataPermissions = new HashMap<DSActionConfig, Boolean>();
    actionDataPermissions.put(DSActionConfig.Completion, Boolean.TRUE);
    actionData1.setPermissions(actionDataPermissions);
    actionData.add(actionData1);
    action1.setActionData(actionData);
    actionList.add(action1);
    DSActionData actionData2 = new DSActionData();
    actionData2.setTitle("View our Corporate Social Responsibility Report");
    actionData2.setType(DSActionDataType.Video);
    actionData2.setLinkText("Play Video");
    actionData2.setUrl("sprepo.surepeople.com/cdn/");
    actionData2.setImageLink("sprepo.surepeople.com/cdn/");
    Map<DSActionConfig, Boolean> actionDataPermissions2 = new HashMap<DSActionConfig, Boolean>();
    actionDataPermissions2.put(DSActionConfig.Completion, Boolean.TRUE);
    actionDataPermissions2.put(DSActionConfig.Note, Boolean.TRUE);
    actionData2.setPermissions(actionDataPermissions2);
    actionData.add(actionData2);
    categoryAction.setActionList(actionList);
    devStrategyActionCategoryList.add(categoryAction);
    spGoalFactory.updateGoal(goal);
  }
  
  private SPGoal addGoal() {
    SPGoal goal = new SPGoal();
    goal.setCategory(GoalCategory.ActionPlan);
    goal.setStatus(GoalStatus.ACTIVE);
    goal.setName("Month 1: Getting to Know Comcast");
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
    action1.setActive(true);
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
    actionList.add(action1);
    devCategory1.setActionList(actionList);
    devStrategyActionCategoryList.add(devCategory1);
    goal.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    dbSetup.addUpdate(goal);
    spGoalFactory.updateGoal(goal);
    return goal;
  }
  
  @Test
  public void testDeletedActionPlan() throws Exception {
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeSpGoals();
      dbSetup.removeAll("sPNote");
      dbSetup.removeAll("companyActionPlanSettings");
      
      final User user = removeSuperAdminAccess();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/getDetails").param("actionPlanId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Action plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan(ActionPlanType.SurePeople);
      
      String actionPlanId = actionPlan.getId();
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/delete").param("actionPlanId", actionPlanId)
                  .param("practiceAreaId", "11").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is(200)).andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = user.getCompanyId();
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/delete").param("actionPlanId", actionPlanId)
                  .param("practiceAreaId", "11").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is(200))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Cannot modify a global plan."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.Company);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/delete").param("actionPlanId", actionPlanId)
                  .param("practiceAreaId", "11").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is(200)).andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      assertThat(actionPlan, is(notNullValue()));
      
      SPGoal addGoal = addGoal();
      actionPlanDao.createOrGetPracticeAreaIdList().add(addGoal.getId());
      actionPlanFactory.updateActionPlan(actionPlanDao);
      String goalId = addGoal.getId();
      /* create some notes */
      PracticeFeedback feedback = new PracticeFeedback();
      feedback.setGoalId(goalId);
      feedback.setType(SPNoteFeedbackType.FEEDBACK);
      feedback.setCompanyId(companyId);
      dbSetup.addUpdate(feedback);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/delete").param("actionPlanId", actionPlanId)
                  .contentType(MediaType.TEXT_PLAIN).session(session)).andExpect(status().is(200))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      List<PracticeFeedback> allPractice = dbSetup.getAll(PracticeFeedback.class);
      Assert.assertEquals(0, allPractice.size());
      
      actionPlan = createActionPlanWithUser("Some action plan created", user);
      actionPlanId = actionPlan.getId();
      
      addGoal = addGoal();
      actionPlan.createOrGetPracticeAreaIdList().add(addGoal.getId());
      actionPlanFactory.updateActionPlan(actionPlan);
      DSActionCategory dsActionCategory = addGoal.getDevStrategyActionCategoryList().get(0);
      DSAction dsAction = dsActionCategory.getActionList().get(0);
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanProgress actionPlanProgress = userActionPlan
          .getOrCreateActionPlanProgress(actionPlanId);
      actionPlanProgress.addPracticeArea(addGoal);
      userActionPlan.addCompletion(actionPlanId, addGoal.getId(), dsAction.getUid(), true);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/delete").param("actionPlanId", actionPlanId)
                  .param("practiceAreaId", addGoal.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session)).andExpect(status().is(200))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      Map<String, Set<String>> completeActionsMap = userActionPlan.getActionPlanProgress(
          actionPlan.getId()).getCompletedActionsForStep();
      assertThat(completeActionsMap.get(actionPlan.getId()), is(nullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAssignActionPlan() {
    
    try {
      testSmtp.start();
      
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      dbSetup.removeAllNotificationLogs();
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      
      final User user = removeSuperAdminAccess();
      
      final ManageActionPlanForm actionPlanForm = new ManageActionPlanForm();
      
      final ObjectMapper om = new ObjectMapper();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "User or group list not found in request.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> userIdList = new ArrayList<String>();
      userIdList.add("abc");
      actionPlanForm.setUserIdList(userIdList);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Organization plan not found in request.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> actionPlanIdList = new ArrayList<String>();
      actionPlanIdList.add("abc");
      actionPlanForm.setActionPlanIdList(actionPlanIdList);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Learning plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userIdList.add(user.getId());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Learning plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      actionPlanIdList.clear();
      final String actionPlanId = actionPlan.getId();
      actionPlanIdList.add(actionPlanId);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("admin@admin.com");
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user2);
      assertTrue(userActionPlan.getActionPlanProgressMap().containsKey(actionPlanId));
      assertThat(userActionPlan.getSelectedActionPlan(), equalTo(actionPlanId));
      
      Thread.sleep(1000);
      
      List<GroupPermissions> groupPermissionsList = new ArrayList<GroupPermissions>();
      GroupPermissions gp = new GroupPermissions("abc", false);
      groupPermissionsList.add(gp);
      actionPlanForm.setGroupPermissionsList(groupPermissionsList);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      gp.setGroupId("2");
      
      UserGroup ug = dbSetup.getUserGroup("AnotherGroup");
      ug.setGroupLead("admin@admin.com");
      dbSetup.addUpdate(ug);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = user2.getCompanyId();
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);
      assertThat(capSettings.getMemberIds().size(), equalTo(3));
      
      Thread.sleep(1000);
      
      testSmtp.reset();
      
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      actionPlanDao.setActive(false);
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      userIdList.add("6");
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Plan not active."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat(testSmtp.getReceivedMessages().length, equalTo(0));
      User user3 = dbSetup.getUser("pradeep3@surepeople.com");
      UserActionPlan user3ActionPlan = actionPlanFactory.getUserActionPlan(user3);
      assertThat(user3ActionPlan.getActionPlanProgress(actionPlanId), is(nullValue()));
      
      actionPlanDao.setActive(true);
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      capSettings.setOnHold(true);
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/assignActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  MessagesHelper.getMessage("orgplan.error.planhold"))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testRemoveFromActionPlan() {
    
    try {
      testSmtp.start();
      
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      dbSetup.removeAllNotificationLogs();
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      
      final User user = removeSuperAdminAccess();
      final ManageActionPlanForm actionPlanForm = new ManageActionPlanForm();
      
      final ObjectMapper om = new ObjectMapper();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "User or group list not found in request.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> userIdList = new ArrayList<String>();
      userIdList.add("abc");
      actionPlanForm.setUserIdList(userIdList);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Organization plan not found in request.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> actionPlanIdList = new ArrayList<String>();
      actionPlanIdList.add("abc");
      actionPlanForm.setActionPlanIdList(actionPlanIdList);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userIdList.add(user.getId());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      actionPlanIdList.clear();
      final String actionPlanId = actionPlan.getId();
      actionPlanIdList.add(actionPlanId);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("admin@admin.com");
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user2);
      assertTrue(!userActionPlan.getActionPlanProgressMap().containsKey(actionPlanId));
      assertThat(userActionPlan.getSelectedActionPlan(), is(nullValue()));
      
      Thread.sleep(1000);
      
      // adding the action plan to the user
      userActionPlan = actionPlanFactory.getUserActionPlan(user2);
      ActionPlanProgress actionPlanProgress = userActionPlan
          .getOrCreateActionPlanProgress(actionPlanId);
      Map<String, Set<String>> stepActionCompleted = actionPlanProgress
          .getCompletedActionsForStep();
      Set<String> completedActions = new HashSet<String>();
      completedActions.add("abc");
      stepActionCompleted.put("abc", completedActions);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      CompanyActionPlanSettings capSettings = actionPlanFactory.createCompanyActionPlanSettings(
          "1", actionPlan);
      capSettings.addUser(user2.getId());
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userActionPlan = actionPlanFactory.getUserActionPlan(user2);
      assertTrue(!userActionPlan.getActionPlanProgressMap().containsKey(actionPlanId));
      assertThat(userActionPlan.getSelectedActionPlan(), is(nullValue()));
      assertTrue(userActionPlan.getRemovedActionPlanProgressMap().containsKey(actionPlanId));
      String companyId = user.getCompanyId();
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      assertThat(capSettings.getMemberIds(), is(empty()));
      
      Thread.sleep(1000);
      
      List<GroupPermissions> groupPermissionsList = new ArrayList<GroupPermissions>();
      GroupPermissions gp = new GroupPermissions("abc", false);
      groupPermissionsList.add(gp);
      actionPlanForm.setGroupPermissionsList(groupPermissionsList);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      gp.setGroupId("2");
      
      UserGroup ug = dbSetup.getUserGroup("AnotherGroup");
      ug.setGroupLead("admin@admin.com");
      dbSetup.addUpdate(ug);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      assertThat(capSettings.getMemberIds().size(), equalTo(0));
      
      Thread.sleep(1000);
      
      testSmtp.reset();
      
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      actionPlanDao.setActive(false);
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      // adding the action plan to the user
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      capSettings.addUser(user.getId());
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userActionPlan = actionPlanFactory.getUserActionPlan(user2);
      assertTrue(!userActionPlan.getActionPlanProgressMap().containsKey(actionPlanId));
      assertThat(userActionPlan.getSelectedActionPlan(), is(nullValue()));
      assertTrue(userActionPlan.getRemovedActionPlanProgressMap().containsKey(actionPlanId));
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      assertThat(capSettings.getMemberIds(), is(not(empty())));
      
      assertThat(testSmtp.getReceivedMessages().length, equalTo(0));
      
      actionPlanDao.setActive(true);
      actionPlanFactory.updateActionPlan(actionPlanDao);
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      capSettings.setOnHold(true);
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/removeActionPlan")
                  .content(om.writeValueAsString(actionPlanForm))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userActionPlan = actionPlanFactory.getUserActionPlan(user2);
      assertTrue(!userActionPlan.getActionPlanProgressMap().containsKey(actionPlanId));
      assertThat(userActionPlan.getSelectedActionPlan(), is(nullValue()));
      assertTrue(userActionPlan.getRemovedActionPlanProgressMap().containsKey(actionPlanId));
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      assertThat(capSettings.getMemberIds(), is(not(empty())));
      
      assertThat(testSmtp.getReceivedMessages().length, equalTo(0));
      
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testActivateDeactivateActionPlan() {
    
    try {
      testSmtp.start();
      
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      dbSetup.removeAllNotificationLogs();
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("companyActionPlanSettings");
      
      final User user = removeSuperAdminAccess();
      actionPlanFactory.getUserActionPlan(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/activateDeactivate").param("actionPlanId", "abc")
                  .param("onHold", "false").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = user.getCompanyId();
      ActionPlan actionPlan = createActionPlan();
      
      final String actionPlanId = actionPlan.getId();
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/activateDeactivate")
                  .param("actionPlanId", actionPlanId).param("onHold", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      assertTrue(actionPlan.isActive());
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/activateDeactivate")
                  .param("actionPlanId", actionPlanId).param("onHold", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      assertTrue(actionPlanDao.isActive());
      
      // adding users to the action plan
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);
      capSettings.addUser(user.getId());
      capSettings.addUser("6");
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      // adding action plan to the user
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanProgress actionPlanProgress = userActionPlan
          .getOrCreateActionPlanProgress(actionPlanId);
      Map<String, Set<String>> stepActionCompleted = actionPlanProgress
          .getCompletedActionsForStep();
      Set<String> completedActions = new HashSet<String>();
      completedActions.add("abc");
      stepActionCompleted.put("abc", completedActions);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/activateDeactivate")
                  .param("actionPlanId", actionPlanId).param("onHold", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      assertTrue(!capSettings.isOnHold());
      assertThat(capSettings.getMemberIds().size(), equalTo(2));
      assertThat(testSmtp.getReceivedMessages().length, equalTo(2));
      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      assertThat(userActionPlan.getActionPlanProgressMap().size(), equalTo(1));
      assertThat(userActionPlan.getRemovedActionPlanProgressMap(), is(nullValue()));
      assertThat(userActionPlan.getSelectedActionPlan(), is(actionPlanId));
      User user3 = dbSetup.getUser("pradeep3@surepeople.com");
      userActionPlan = actionPlanFactory.getUserActionPlan(user3);
      assertThat(userActionPlan.getActionPlanProgressMap().size(), equalTo(1));
      assertThat(userActionPlan.getSelectedActionPlan(), is(actionPlanId));
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/activateDeactivate")
                  .param("actionPlanId", actionPlanId).param("onHold", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      assertTrue(capSettings.isOnHold());
      assertThat(capSettings.getMemberIds().size(), equalTo(2));
      assertThat(testSmtp.getReceivedMessages().length, equalTo(4));
      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      assertThat(userActionPlan.getActionPlanProgressMap().size(), equalTo(0));
      assertThat(userActionPlan.getRemovedActionPlanProgressMap().size(), equalTo(1));
      assertThat(userActionPlan.getSelectedActionPlan(), is(nullValue()));
      
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testModifyAllMembers() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      final User user = removeSuperAdminAccess();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers").param("actionPlanId", "134")
                  .param("enable", "false").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      
      final String actionPlanId = actionPlan.getId();
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = user.getCompanyId();
      actionPlan.setCreatedByCompanyId(companyId);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      actionPlan.setAllMembers(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(false));
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "true")
                  .param("companyId", companyId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(true));
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "false")
                  .param("companyId", companyId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(false));
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "true")
                  .param("companyId", companyId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(true));
      
      actionPlan.setAllMembers(true);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "true")
                  .param("companyId", companyId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      capSettings.setAllMembers(false);
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
      
      actionPlan.setAllMembers(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(true));
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(false));
      
      ActionPlan actionPlan2 = createActionPlan(ActionPlanType.SurePeople);
      actionPlan2.setAllCompanies(false);
      actionPlanFactory.updateActionPlan(actionPlan2);
      
      final String actionPlanId2 = actionPlan2.getId();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId2).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan2.setAllCompanies(true);
      actionPlan2.setAllMembers(true);
      actionPlanFactory.updateActionPlan(actionPlan2);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId2).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan2.setAllMembers(false);
      actionPlanFactory.updateActionPlan(actionPlan2);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId2).param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}