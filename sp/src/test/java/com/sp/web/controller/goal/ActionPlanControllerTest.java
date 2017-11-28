package com.sp.web.controller.goal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.form.Operation;
import com.sp.web.form.goal.ActionPlanForm;
import com.sp.web.form.goal.PracticeAreaActionForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.goal.ActionPlan;
import com.sp.web.model.goal.ActionPlanType;
import com.sp.web.model.goal.CompanyActionPlanSettings;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;
import com.sp.web.model.goal.DSActionType;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.StepType;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.goals.ActionPlanFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionPlanControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Test
  public void testGetUseractionPlan() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllGroups();
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      
      // test no action plans
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // creating one action plan
      ActionPlan actionPlan = createActionPlan();
      actionPlan.setCreatedByCompanyId("abc");
      actionPlanFactory.updateActionPlan(actionPlan);
      
      // testing with action plan for different companies
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      

      actionPlan.setCreatedByCompanyId("1");
      actionPlanFactory.updateActionPlan(actionPlan);

      // testing for action plan from same company but with active false
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      

      actionPlan.setActive(true);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      // testing for action plan from same company but with active true
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      userActionPlan.setSelectedActionPlan("abc");
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      assertThat(userActionPlan.getSelectedActionPlan(), is(nullValue()));
      
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlan.getId());
      actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlanDao, Operation.ADD);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      // all valid should come up as action plan
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      SPGoal spGoal = actionPlanDao.getPracticeAreaList().get(0);
      DSActionData dsAction = spGoal.getDevStrategyActionCategoryList().get(0).getActionList()
          .get(0).getActionData().get(0);
      userActionPlan.addCompletion(actionPlanDao.getId(), spGoal.getId(), dsAction.getUid(), true);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      user.addMessage(SPFeature.OrganizationPlan, "Some message");
      dbSetup.addUpdate(user);
      authenticationHelper.doAuthenticateWithoutPassword(session, user);

      // all valid should come up as action plan check for completed action
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      userActionPlan.getOrCreateActionPlanProgress("abc");
      actionPlanFactory.updateUserActionPlan(userActionPlan);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlan")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlan").exists())
          .andExpect(jsonPath("$.success.showHamburger").value(true))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetApplicableAcitonPlans() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllGroups();
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      
      // test no action plans
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlans")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(0)))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // creating one action plan
      ActionPlan actionPlan = createActionPlan();
      actionPlan.setCreatedByCompanyId("abc");
      actionPlanFactory.updateActionPlan(actionPlan);
      
      // action plan for another company
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlans")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(0)))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      actionPlan.setCreatedByCompanyId("1");
      actionPlanFactory.updateActionPlan(actionPlan);

      // action plan for same company not active
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlans")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(0)))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      actionPlan.setActive(true);
      actionPlanFactory.updateActionPlan(actionPlan);
      
      User user = dbSetup.getUser("admin@admin.com");
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      userActionPlan.getOrCreateActionPlanProgress(actionPlan.getId());
      actionPlanFactory.updateUserActionPlan(userActionPlan);

      // action plan for same company but active
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/getActionPlans")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.actionPlanList", hasSize(1)))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testSelectPlan() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllGroups();
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      
      // test action plan id blank
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/selectPlan")
              .param("actionPlanId", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Organization Plan id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // test action plan id invalid
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/selectPlan")
              .param("actionPlanId", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.DashboardRedirectException").value(
                  "Organizational Plan not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      
      // creating one action plan
      ActionPlan actionPlan = createActionPlan();
      actionPlan.setCreatedByCompanyId("abc");
      actionPlanFactory.updateActionPlan(actionPlan);

      // test action plan id invalid
      final String actionPlanId = actionPlan.getId();
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/selectPlan")
              .param("actionPlanId", actionPlanId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Learning Program is no longer available.."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      actionPlan.setCreatedByCompanyId("1");
      actionPlanFactory.updateActionPlan(actionPlan);

      // test action plan id valid but plan is in active
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/selectPlan")
              .param("actionPlanId", actionPlanId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Learning Program is no longer available.."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      actionPlan.setActive(true);
      actionPlanFactory.updateActionPlan(actionPlan);

      // test action plan id valid but plan is active
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/selectPlan")
              .param("actionPlanId", actionPlanId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Learning Program is no longer available.."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      User user = dbSetup.getUser("admin@admin.com");
      String companyId = "1";
      CompanyActionPlanSettings capSettings = actionPlan.addCompany(companyId, actionPlanFactory);
      actionPlanFactory.updateActionPlan(actionPlan);
      capSettings.addUser(user.getId());
      actionPlanFactory.updateCompanyActionPlanSettings(capSettings);
 
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlanDao, Operation.ADD);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      // test action plan id valid but plan is active but not for all and invalid group
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/selectPlan")
              .param("actionPlanId", actionPlanId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
 
  @Test
  public void testCompleteAction() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllGroups();
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      
      // test action plan id blank
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", " ")
              .param("stepId", " ")
              .param("uid", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Organization Plan id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      // uid not sent
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", "abc")
              .param("stepId", " ")
              .param("uid", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("UID uinique id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      // selected action plan id not set
//      result = mockMvc
//          .perform(
//              MockMvcRequestBuilders.get("/actionPlan/completeAction")
//              .param("actionPlanId", "abc")
//              .param("stepId", "abc")
//              .param("uid", "abc")
//              .contentType(MediaType.TEXT_PLAIN)
//              .session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(
//              jsonPath("$.error.DashboardRedirectException").value(
//                  "Organization Plan not currently selected by user."))
//          .andReturn();
//      log.info("The MVC Response:" + result.getResponse().getContentAsString());      

      User user = dbSetup.getUser("admin@admin.com");
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(user);
      userActionPlan.setSelectedActionPlan("1");
      actionPlanFactory.updateUserActionPlan(userActionPlan);

      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      // action plan id mismatch
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", "abc")
              .param("stepId", "abc")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.DashboardRedirectException").value(
                  "Organizational Plan not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      userActionPlan.setSelectedActionPlan("abc");
      dbSetup.addUpdate(userActionPlan);
      
      // action plan id mismatch
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", "abc")
              .param("stepId", "abc")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.DashboardRedirectException").value(
                  "Organizational Plan not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      // creating one action plan
      ActionPlan actionPlan = createActionPlan();
      actionPlan.setCreatedByCompanyId("abc");
      actionPlanFactory.updateActionPlan(actionPlan);

      final String actionPlanId = actionPlan.getId();
      ActionPlanDao actionPlanDao = actionPlanFactory.getActionPlan(actionPlanId);
      actionPlanFactory.addUpdateActionPlanProgress(user, userActionPlan, actionPlanDao, Operation.ADD);
      actionPlanFactory.updateUserActionPlan(userActionPlan);
      
      // invalid uid
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", actionPlanId)
              .param("stepId", "abc")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.DashboardRedirectException").value(
                  "Unable to proceed. This entry is no longer available."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      SPGoal spGoal = actionPlanDao.getPracticeAreaList().get(0);
      DSActionData dsAction = spGoal.getDevStrategyActionCategoryList().get(0).getActionList()
          .get(0).getActionData().get(0);

      // valid request
      final String stepId = spGoal.getId();
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", actionPlanId)
              .param("stepId", stepId)
              .param("uid", dsAction.getUid())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      

      // valid remove request
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/completeAction")
              .param("actionPlanId", actionPlanId)
              .param("stepId", stepId)
              .param("uid", dsAction.getUid())
              .param("completed", "false")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
        
      userActionPlan = actionPlanFactory.getUserActionPlan(user);
      assertThat(userActionPlan.getActionPlanProgress(actionPlanId).getCompletedActionsForStep()
          .get(stepId).isEmpty(), is(true));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testSendInviteAction() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllGroups();
      dbSetup.removeAll("actionPlan");
      dbSetup.removeAll("userActionPlan");
      
      testSmtp.start();
      
      // test action plan id blank
//      MvcResult result = mockMvc
//          .perform(
//              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
//              .param("actionPlanId", " ")
//              .contentType(MediaType.TEXT_PLAIN)
//              .session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Organization Plan id is required."))
//          .andReturn();
//      log.info("The MVC Response:" + result.getResponse().getContentAsString());
//      
//      result = mockMvc
//          .perform(
//              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
//              .param("actionPlanId", "abc")
//              .contentType(MediaType.TEXT_PLAIN)
//              .session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(
//              jsonPath("$.error.DashboardRedirectException").value(
//                  "Organizational Plan not found."))
//          .andReturn();
//      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      ActionPlan actionPlan = createActionPlan();
      final String actionPlanId = actionPlan.getId();
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
              .param("actionPlanId", actionPlanId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "The recipient list is required and must contain at least one recipient."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
              .param("actionPlanId", actionPlanId)
              .param("to", "dax@surepeople.com, dax@einstix.com")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Subject is required for the invite request."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
              .param("actionPlanId", actionPlanId)
              .param("to", "dax@surepeople.com, dax@einstix.com")
              .param("subject", "Subject is test meeting invite.")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Start date is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      final String startDate = formatter.format(LocalDateTime.now());
      final String endDate =  formatter.format(LocalDateTime.now().plusHours(2));
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
              .param("actionPlanId", actionPlanId)
              .param("to", "dax@surepeople.com, dax@einstix.com")
              .param("subject", "Subject is test meeting invite.")
              .param("startDate", startDate)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("End date is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/actionPlan/sendCalInvite")
              .param("actionPlanId", actionPlanId)
              .param("to", "dax@surepeople.com, dax@einstix.com")
              .param("subject", "Subject is test meeting invite.")
              .param("startDate", startDate)
              .param("endDate", endDate)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(10 * 1000);
      
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  
  private ActionPlan createActionPlan() throws Exception {
    CompanyDao company = companyFactory.getCompany("1");
    company.setActionPlanAdminEnabled(true);
    companyFactory.updateCompany(company);
    
    final ActionPlanForm actionPlanForm = new ActionPlanForm();
    actionPlanForm.setName("Test Action Plan");
    actionPlanForm.setCreatedByCompanyId("1");
    actionPlanForm.setDescription("Description for action plan.");
    actionPlanForm.setType(ActionPlanType.Company);
    actionPlanForm.setStepType(StepType.All);
    
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
    action1.setActive(true);
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

    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(actionPlanForm);
    
    MvcResult result = this.mockMvc
        .perform(
            post("/admin/account/actionPlan/create")
            .content(request)
            .contentType(MediaType.APPLICATION_JSON)
            .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<ActionPlan> all = dbSetup.getAll(ActionPlan.class);
    assertTrue(all.size() > 0);
    return all.get(0);
  }
}
