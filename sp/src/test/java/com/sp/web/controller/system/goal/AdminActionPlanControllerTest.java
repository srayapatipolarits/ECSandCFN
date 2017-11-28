package com.sp.web.controller.system.goal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
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
import com.sp.web.form.goal.PracticeAreaActionForm;
import com.sp.web.model.SPFeature;
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
import com.sp.web.model.goal.DSActionType;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The test cases for administration services of action plan.
 */
public class AdminActionPlanControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  SPGoalFactory spGoalFactory;
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Test
  public void testGetAllActionPlans() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
                .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding an action plan
      final ActionPlan actionPlan = createActionPlan();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
                .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.Company);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      final String companyId = "1";
      final CompanyDao company = companyFactory.getCompany(companyId);
      company.addFeature(SPFeature.OrganizationPlan);
      companyFactory.updateCompany(company);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan", hasSize(1)))
          .andExpect(jsonPath("$.success.actionPlan[0].company").exists())
          .andExpect(jsonPath("$.success.actionPlan[0].actionPlanList", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating another
      actionPlan.setId(null);
      actionPlan.setName("Test Action Plan 2");
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlan.setAllCompanies(true);
      actionPlan.setAllMembers(false);
      actionPlan.setCompanyIds(null);
      dbSetup.addUpdate(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan", hasSize(1)))
          .andExpect(jsonPath("$.success.actionPlan[0].company").exists())
          .andExpect(jsonPath("$.success.actionPlan[0].actionPlanList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // providing company access to edit action plan
      company.setActionPlanAdminEnabled(true);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan", hasSize(1)))
          .andExpect(jsonPath("$.success.actionPlan[0].company").exists())
          .andExpect(jsonPath("$.success.actionPlan[0].actionPlanList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding feature to a new company
      CompanyDao company2 = companyFactory.getCompany("2");
      company2.addFeature(SPFeature.OrganizationPlan);
      companyFactory.updateCompanyDao(company2);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan", hasSize(2)))
          .andExpect(jsonPath("$.success.actionPlan[0].company").exists())
          .andExpect(jsonPath("$.success.actionPlan[0].actionPlanList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setEditAllowed(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.Company.toString())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan", hasSize(2)))
          .andExpect(jsonPath("$.success.actionPlan[0].company").exists())
          .andExpect(jsonPath("$.success.actionPlan[0].actionPlanList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getAll")
              .param("type", ActionPlanType.SurePeople.toString())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private ActionPlan createActionPlan() {
    return createActionPlan("Test Action Plan", ActionPlanType.Company, "1");
  }
  
  private ActionPlan createActionPlan(String name, ActionPlanType type, String companyId) {
    ActionPlan actionPlan = new ActionPlan();
    actionPlan.setName(name);
    actionPlan.setActive(true);
    actionPlan.setAllMembers(true);
    actionPlan.setType(type);
    dbSetup.addUpdate(actionPlan);
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
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getDetails").param("actionPlanId", "-1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Action plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating a new action plan incorrect practice area
      ActionPlan actionPlan = createActionPlan();
      List<String> practiceAreaList = new ArrayList<String>();
      practiceAreaList.add("abc");
      actionPlan.setPracticeAreaIdList(practiceAreaList);
      dbSetup.addUpdate(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getDetails").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
              post("/sysAdmin/actionPlan/getDetails").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addGroupAction(addGoal);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getDetails").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setEditAllowed(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getDetails").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompanyActionPlanSettings companyActionPlanSettings = actionPlanFactory
          .getCompanyActionPlanSettings(actionPlan.getId(), "1");
      actionPlanFactory.removeCompanyActionPlanSettings(companyActionPlanSettings);
      
      // creating an action plan which is SP and global
      actionPlan.setId(null);
      actionPlan.setName("SurePeople action plan");
      actionPlan.getCompanyIds().clear();
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlan.setAllCompanies(false);
      actionPlan.setAllMembers(true);
      actionPlanFactory.createActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getDetails")
              .param("actionPlanId", actionPlan.getId())
              .param("isEdit", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan.practiceAreaList", hasSize(1))).andReturn();
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
      
      final ActionPlanForm actionPlanForm = new ActionPlanForm();
      // actionPlanForm.setName("Test Action Plan");
      // actionPlanForm.setCompanyId("abc");
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(actionPlanForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setCreatedByCompanyId("abc");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Plan type required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      actionPlanForm.setType(ActionPlanType.Company);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Step type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setStepType(StepType.AllWithDuration);
      request = om.writeValueAsString(actionPlanForm);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Duration is reuquired."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      practiceArea.setDurationDays(20);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setCreatedByCompanyId("1");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // duplicate action plan
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
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
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Action plan with the same name already exists.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // for a different company
      actionPlanForm.setName(actionPlanName);
      actionPlanForm.setCreatedByCompanyId("2");
      request = om.writeValueAsString(actionPlanForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
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
      dbSetup.removeAll("userActionPlan");
      dbSetup.removeSpGoals();
      
      final ActionPlanForm actionPlanForm = new ActionPlanForm();
      // actionPlanForm.setName("Test Action Plan");
      // actionPlanForm.setCompanyId("abc");
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(actionPlanForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
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
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Action plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      final String actionPlanId = actionPlan.getId();
      actionPlanForm.setId(actionPlanId);
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Description not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setDescription("Some description goes here.");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
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
              post("/sysAdmin/actionPlan/update").content(request)
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
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Step type is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      actionPlanForm.setStepType(StepType.TimeBased);
      request = om.writeValueAsString(actionPlanForm);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Duration is reuquired."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      practiceArea.setDurationDays(20);
      request = om.writeValueAsString(actionPlanForm);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlanForm.setCreatedByCompanyId("1");
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
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
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Step Name already exists in action plan.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // testing the deactivated actions
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      User user = dbSetup.getUser("admin@admin.com");
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanProgress actionPlanProgress = userActionPlan
          .getOrCreateActionPlanProgress(actionPlanId);
      final SPGoal spGoal = actionPlanDao.getPracticeAreaList().get(0);
      actionPlanProgress.addPracticeArea(spGoal);
      String uid = spGoal.getDevStrategyActionCategoryList().get(0).getActionList().get(0)
          .getActionData().get(0).getUid();
      actionPlanProgress.completeAction(spGoal.getId(), uid, true);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      updateUIDs(actionPlanDao, actionPlanForm);
      PracticeAreaActionForm practiceAreaActionForm = actionPlanForm.getPracticeAreaList().get(0);
      practiceAreaActionForm.setId(spGoal.getId());
      practiceAreaActionForm.setStatus(GoalStatus.INACTIVE);
      
      request = om.writeValueAsString(actionPlanForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private void updateUIDs(ActionPlanDao actionPlanDao, ActionPlanForm actionPlanForm) {
    final List<SPGoal> practiceAreaList = actionPlanDao.getPracticeAreaList();
    List<PracticeAreaActionForm> practiceAreaList2 = actionPlanForm.getPracticeAreaList();
    for (int i = 0; i < practiceAreaList.size(); i++) {
      SPGoal spGoal = practiceAreaList.get(i);
      PracticeAreaActionForm practiceAreaActionForm = practiceAreaList2.get(i);
      practiceAreaActionForm.setId(spGoal.getId());
      
      final List<DSActionCategory> devStrategyActionCategoryList = spGoal
          .getDevStrategyActionCategoryList();
      List<DSActionCategory> devStrategyActionCategoryList2 = practiceAreaActionForm
          .getDevStrategyActionCategoryList();
      for (int j = 0; j < devStrategyActionCategoryList.size(); j++) {
        DSActionCategory dsActionCategory = devStrategyActionCategoryList.get(j);
        DSActionCategory dsActionCategory2 = devStrategyActionCategoryList2.get(j);
        dsActionCategory2.setUid(dsActionCategory.getUid());
        final List<DSAction> actionList = dsActionCategory.getActionList();
        List<DSAction> actionList2 = dsActionCategory2.getActionList();
        for (int k = 0; k < actionList.size(); k++) {
          DSAction dsAction = actionList.get(k);
          DSAction dsAction2 = actionList2.get(k);
          dsAction2.setUid(dsAction.getUid());
          
          final List<DSActionData> actionData = dsAction.getActionData();
          List<DSActionData> actionData2 = dsAction2.getActionData();
          for (int l = 0; l < actionData.size(); l++) {
            DSActionData dsActionData = actionData.get(l);
            DSActionData dsActionData2 = actionData2.get(l);
            dsActionData2.setUid(dsActionData.getUid());
          }
        }
      }
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
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/delete").param("actionPlanId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/delete").param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan = createActionPlan();

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/delete")
              .param("actionPlanId", actionPlan.getId())
              .param("practiceAreaId","abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlanDao actionPlan2 = actionPlanFactory.getActionPlan(actionPlan.getId());
      assertThat(actionPlan2, is(notNullValue()));
      
      SPGoal goal = new SPGoal();
      goal.setCategory(GoalCategory.ActionPlan);
      goal.setName("Some name");
      final List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
      DSActionCategory dsac = new DSActionCategory();
      dsac.setTitle("Ha ha");
      dsac.setStatus(GoalStatus.ACTIVE);
      final List<DSAction> actionList = new ArrayList<DSAction>();
      DSAction dsa = new DSAction();
      dsa.setTitle("Some action");
      dsa.setActive(true);
      Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
      permissions.put(DSActionConfig.Completion, true);
      dsa.setPermissions(permissions);
      actionList.add(dsa);
      DSAction dsa2 = new DSAction();
      dsa2.setTitle("DSA title 2");
      dsa2.setActive(true);
      List<DSActionData> actionData = new ArrayList<DSActionData>();
      DSActionData dsad = new DSActionData();
      dsad.setTitle("Action data 1");
      dsad.setPermissions(permissions);
      actionData.add(dsad);
      DSActionData dsad2 = new DSActionData();
      dsad2.setTitle("Action data 2");
      dsad2.setPermissions(permissions);
      actionData.add(dsad2);
      dsa2.setActionData(actionData);
      actionList.add(dsa2);
      dsac.setActionList(actionList);
      devStrategyActionCategoryList.add(dsac);
      goal.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
      devStrategyActionCategoryList.forEach(d -> d.addUID(actionPlan2::getNextUID));
      goal.updateActionIds();
      dbSetup.addUpdate(goal);
      List<String> practiceAreaList = new ArrayList<String>();
      practiceAreaList.add(goal.getId());
      actionPlan2.setPracticeAreaIdList(practiceAreaList);
      actionPlanFactory.updateActionPlan(actionPlan2);
      
      /* create some notes */      
      PracticeFeedback feedback = new PracticeFeedback();
      feedback.setGoalId(goal.getId());
      feedback.setType(SPNoteFeedbackType.FEEDBACK);
      feedback.setCompanyId("1");
      dbSetup.addUpdate(feedback);
      result = this.mockMvc
          .perform(
              post("/admin/account/actionPlan/delete")
              .param("actionPlanId", actionPlan2.getId())
              .param("practiceAreaId", goal.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session)).andExpect(status().is(200))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      List<PracticeFeedback> allPractice = dbSetup.getAll(PracticeFeedback.class);
      Assert.assertEquals(0, allPractice.size());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAdminEnable() {
    
    try {
      dbSetup.removeAll("actionPlan");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/adminEnable").param("companyId", "134")
                  .param("enableAdminAccess", "false").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :134"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String companyId = "1";
      CompanyDao company = companyFactory.getCompany(companyId);
      company.removeFeature(SPFeature.OrganizationPlan);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/adminEnable").param("companyId", companyId)
                  .param("enableAdminAccess", "false").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Company does not have organisation plan.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      company.addFeature(SPFeature.OrganizationPlan);
      companyFactory.updateCompanyDao(company);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/adminEnable").param("companyId", companyId)
                  .param("enableAdminAccess", "false").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      company = companyFactory.getCompany(companyId);
      assertThat(company.isActionPlanAdminEnabled(), equalTo(false));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/adminEnable").param("companyId", companyId)
                  .param("enableAdminAccess", "true").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      company = companyFactory.getCompany(companyId);
      assertThat(company.isActionPlanAdminEnabled(), equalTo(true));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testPublish() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/publish")
                  .param("actionPlanId", "134")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Organization plan not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      ActionPlan actionPlan = createActionPlan();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/publish")
                  .param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Plan already published."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setActive(false);
      actionPlan.setCreatedByCompanyId("1");
      actionPlan.getCompanyIds().clear();
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/publish")
                  .param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlan.setActive(false);
      actionPlan.setCreatedByCompanyId("1");
      actionPlan.getCompanyIds().clear();
      actionPlanFactory.updateActionPlan(actionPlan);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/publish")
                  .param("actionPlanId", actionPlan.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testEditAccess() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/editAccess")
                  .param("actionPlanId", "134")
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
              post("/sysAdmin/actionPlan/modify/editAccess")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
      assertThat(actionPlan.isEditAllowed(), is(false));

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/editAccess")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
      assertThat(actionPlan.isEditAllowed(), is(true));
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/editAccess")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorised access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testModifyAllCompanies() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allCompanies")
                  .param("actionPlanId", "134")
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
              post("/sysAdmin/actionPlan/modify/allCompanies")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Unauthorised access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlan.setActive(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allCompanies")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Plan not published."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setActive(true);
      actionPlanFactory.updateActionPlan(actionPlan);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allCompanies")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
      assertThat(actionPlan.isAllCompanies(), is(false));

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allCompanies")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan = actionPlanFactory.getActionPlan(actionPlanId);
      assertThat(actionPlan.isAllCompanies(), is(true));
      
      
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
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", "134")
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Company id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String companyId = "1";
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setAllMembers(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(false));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(true));
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(false));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);
      
      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(true));
      
      actionPlan.setAllMembers(true);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "true")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);

      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(true));
      assertThat(capSettings.isAllMembers(), is(true));

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/modify/allMembers")
                  .param("actionPlanId", actionPlanId)
                  .param("enable", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      capSettings = actionPlanFactory.getCompanyActionPlanSettings(actionPlanId, companyId);

      assertThat(actionPlanFactory.getActionPlan(actionPlanId).isAllMembers(), is(false));
      assertThat(capSettings.isAllMembers(), is(true));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAddCompany() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/addCompany")
                  .param("actionPlanIds", "134")
                  .param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String companyId = "1";
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/addCompany")
                  .param("actionPlanIds", "abc")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
              post("/sysAdmin/actionPlan/addCompany")
                  .param("actionPlanIds", actionPlanId)
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Not a global plan."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlanFactory.updateActionPlan(actionPlan);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/addCompany")
                  .param("actionPlanIds", actionPlanId)
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Company already added."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      actionPlan.getCompanyIds().clear();
      actionPlanFactory.updateActionPlan(actionPlan);

      dbSetup.removeAll("companyActionPlanSettings");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/addCompany")
                  .param("actionPlanIds", actionPlanId)
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);

      assertThat(actionPlanFactory.getActionPlan(actionPlanId).hasCompany(companyId), is(true));
      assertThat(capSettings, is(notNullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testRemoveCompany() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/removeCompany")
                  .param("actionPlanId", "134")
                  .param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
              post("/sysAdmin/actionPlan/removeCompany")
                  .param("actionPlanId", actionPlanId)
                  .param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String companyId = "1";
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/removeCompany")
                  .param("actionPlanId", actionPlanId)
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Not a global plan."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      actionPlan.setType(ActionPlanType.SurePeople);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/removeCompany")
                  .param("actionPlanId", actionPlanId)
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      CompanyActionPlanSettings capSettings = actionPlanFactory.getCompanyActionPlanSettings(
          actionPlanId, companyId);

      assertThat(actionPlanFactory.getActionPlan(actionPlanId).hasCompany(companyId), is(false));
      assertThat(capSettings, is(nullValue()));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/removeCompany")
                  .param("actionPlanId", actionPlanId)
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company not added."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  @Test
  public void testGetGlobalPrograms() {
    
    try {
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("companyActionPlanSettings");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getGlobalPrograms")
                  .param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      createActionPlan();
      final String companyId = "1";
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getGlobalPrograms")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      createActionPlan("All Company False Action Plan", ActionPlanType.SurePeople, null);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getGlobalPrograms")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan("All Company Action Plan", ActionPlanType.SurePeople, null);
      actionPlan.setAllCompanies(false);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getGlobalPrograms")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      createActionPlan("All Company Action Plan", ActionPlanType.SurePeople, companyId);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/actionPlan/getGlobalPrograms")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }    
}