package com.sp.web.controller.blueprint;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sp.web.Constants;
import com.sp.web.form.ExternalUserForm;
import com.sp.web.form.blueprint.BlueprintForm;
import com.sp.web.form.blueprint.BlueprintMissionStatementForm;
import com.sp.web.form.blueprint.BlueprintResponseForm;
import com.sp.web.form.blueprint.BlueprintShareForm;
import com.sp.web.model.Comments;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintApprover;
import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.blueprint.BlueprintFeedback;
import com.sp.web.model.blueprint.BlueprintMissionStatement;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.token.TokenRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

public class BlueprintControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  SPTokenFactory tokenFactory;
  
  @Autowired
  BlueprintControllerHelper helper;

  @Before
  public void doInit() {
    testSmtp.start();
  }
  
  @After
  public void doTearDown() {
    testSmtp.stop();
  }
  
  @Test
  public void testGetBlueprintSettings() {
    try {
      dbSetup.removeAll("blueprintSettings");
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/getSettings").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetBlueprint() {
    try {

      dbSetup.removeSpGoals();
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/get").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").doesNotExist())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // adding a temporary blueprint
      User user = dbSetup.getUser("admin@admin.com");
      addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/get").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private Blueprint addBlueprint(User user) {
    final Map<DSActionConfig, Boolean> permissions = new HashMap<DSActionConfig, Boolean>();
    permissions.put(DSActionConfig.Completion, Boolean.TRUE);
    Blueprint blueprint = new Blueprint();
    blueprint.setCreatedOn(LocalDateTime.now());
    blueprint.setStatus(GoalStatus.EDIT);
    BlueprintMissionStatement missionStatement = new BlueprintMissionStatement();
    missionStatement.setUid("1");
    missionStatement.setText("ha ha mission statement.");
    blueprint.setMissionStatement(missionStatement);
    final List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory actionCategory = new DSActionCategory();
    actionCategory.setUid("2");
    actionCategory.setTitle("Something objective to set.");
    actionCategory.setStatus(GoalStatus.ACTIVE);
    final List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action = new DSAction();
    action.setUid("3");
    action.setActive(true);
    action.setDescription("Some initiatives I have to set.");
    final List<DSActionData> actionData = new ArrayList<DSActionData>();
    DSActionData actionData1 = new DSActionData();
    actionData1.setUid("4");
    actionData1.setDescription("1. Something to do for success measures.");
    actionData1.setPermissions(permissions);
    actionData.add(actionData1);
    DSActionData actionData2 = new DSActionData();
    actionData2.setUid("5");
    actionData2.setDescription("2. Something to do for success measures.");
    actionData2.setPermissions(permissions);
    actionData.add(actionData2);
    action.setActionData(actionData);
    actionList.add(action);
    actionCategory.setActionList(actionList);
    devStrategyActionCategoryList.add(actionCategory);
    blueprint.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    dbSetup.addUpdate(blueprint);
    
    user.setBlueprintId(blueprint.getId());
    dbSetup.addUpdate(user);

    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    
    return blueprint;
  }

  @Test
  public void testCreateUpdateBlueprint() {
    try {

      final ObjectMapper om = new ObjectMapper();
      dbSetup.removeSpGoals();
      User user = dbSetup.getUser("admin@admin.com");
      
      assertThat(user.getBlueprintId(), is(nullValue()));
      
      // creating the blueprint with the mission statement 
      final BlueprintForm form = new BlueprintForm();
      BlueprintMissionStatementForm missionStatement = new BlueprintMissionStatementForm();
      missionStatement.setText("Some misison statement.");
      form.setMissionStatement(missionStatement);
      
      String content = om.writeValueAsString(form);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/createOrUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getBlueprintId(), is(not(nullValue())));
      Blueprint blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getMissionStatement(), is(not(nullValue())));
      
      // adding an objective
      List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
      DSActionCategory objectiveForm = new DSActionCategory();
      objectiveForm.setTitle("Some objective to work on");
      devStrategyActionCategoryList.add(objectiveForm);
      form.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
      
      content = om.writeValueAsString(form);
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/createOrUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      blueprint = goalsFactory.getBlueprint(user);
      List<DSActionCategory> objectiveList = blueprint.getDevStrategyActionCategoryList();
      assertThat(objectiveList, is(not(nullValue())));
      assertThat(objectiveList.size(), is(greaterThan(0)));
      DSActionCategory objective = objectiveList.get(0);
      objectiveForm.setUid(objective.getUid());
      
      // adding initiative
      List<DSAction> actionList = new ArrayList<DSAction>();
      DSAction initiativeForm = new DSAction();
      initiativeForm.setDescription("Some key initiative to work on.");
      actionList.add(initiativeForm);
      objectiveForm.setActionList(actionList);
      
      content = om.writeValueAsString(form);
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/createOrUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      blueprint = goalsFactory.getBlueprint(user);
      objectiveList = blueprint.getDevStrategyActionCategoryList();
      assertThat(objectiveList, is(not(nullValue())));
      assertThat(objectiveList.size(), is(greaterThan(0)));
      objective = objectiveList.get(0);
      List<DSAction> initiativeList = objective.getActionList();
      assertThat(initiativeList, is(not(nullValue())));
      assertThat(initiativeList.size(), is(greaterThan(0)));
      DSAction initiative = initiativeList.get(0);
      initiativeForm.setUid(initiative.getUid());
      
      // adding CSM
      List<DSActionData> csmFormList = new ArrayList<DSActionData>();
      DSActionData csm1 = new DSActionData();
      csm1.setTitle("1. Some success measure.");
      csmFormList.add(csm1);
      DSActionData csm2 = new DSActionData();
      csm1.setTitle("2. Some success measure.");
      csmFormList.add(csm2);
      initiativeForm.setActionData(csmFormList);
      
      content = om.writeValueAsString(form);
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/createOrUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      blueprint = goalsFactory.getBlueprint(user);
      objectiveList = blueprint.getDevStrategyActionCategoryList();
      assertThat(objectiveList, is(not(nullValue())));
      assertThat(objectiveList.size(), is(greaterThan(0)));
      objective = objectiveList.get(0);
      initiativeList = objective.getActionList();
      assertThat(initiativeList, is(not(nullValue())));
      assertThat(initiativeList.size(), is(greaterThan(0)));
      initiative = initiativeList.get(0);
      List<DSActionData> csmList = initiative.getActionData();
      assertThat(csmList, is(not(nullValue())));
      assertThat(csmList.size(), is(equalTo(2)));
      
      // doing delete test
      form.setMissionStatement(null);
      form.setDevStrategyActionCategoryList(null);
      List<String> deleteList = new ArrayList<String>();
      deleteList.add(objective.getUid());
      form.setDeleteList(deleteList);
      content = om.writeValueAsString(form);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/createOrUpdate")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getDevStrategyActionCategoryList(), is(nullValue()));
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testBlueprintShare() {
    try {

      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();
      
      final ObjectMapper om = new ObjectMapper();
      
      final BlueprintShareForm shareForm = new BlueprintShareForm();
      String content = om.writeValueAsString(shareForm);
      // no parameters
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Atleast one user is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      List<String> userIdList = new ArrayList<String>();
      userIdList.add("abc");
      shareForm.setUserIdList(userIdList);
      
      content = om.writeValueAsString(shareForm);
      
      // blueprint not found for user
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);

      // blueprint not found for user
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      userIdList.clear();
      userIdList.add(user.getId());
      content = om.writeValueAsString(shareForm);

      // blueprint request for self
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Cannot send request to yourself."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
 
      blueprint.setStatus(GoalStatus.UNDER_APPROVAL);
      goalsFactory.updateBlueprint(blueprint);

      // blueprint not edit status
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Cannot share blueprint under approval."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      blueprint.setStatus(GoalStatus.EDIT);
      goalsFactory.updateBlueprint(blueprint);

      User user2 = dbSetup.getUser("dax@surepeople.com");
      
      userIdList.clear();
      userIdList.add(user2.getId());
      content = om.writeValueAsString(shareForm);
      
      // blueprint not edit status
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, is(greaterThan(0)));
      MimeMessage mimeMessage = receivedMessages[0];
      assertThat(mimeMessage.getSubject(), is("Request for Blueprint Feedback"));
      testSmtp.reset();
      
      userIdList.clear();
      List<ExternalUserForm> externalUserList = new ArrayList<ExternalUserForm>();
      ExternalUserForm externalUser = new ExternalUserForm();
      externalUser.setEmail("somebodyExternal@yopmail.com");
      externalUserList.add(externalUser);
      shareForm.setExternalUserList(externalUserList);
      
      content = om.writeValueAsString(shareForm);
      
      // going for external request, first name missing
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("First name required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      externalUser.setFirstName("Somebody");
      externalUser.setLastName("External");
      content = om.writeValueAsString(shareForm);
      
      // going for external request, all data
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, is(greaterThan(0)));
      mimeMessage = receivedMessages[0];
      assertThat(mimeMessage.getSubject(), is("Request for Blueprint Feedback"));
      testSmtp.reset();

      externalUserList.clear();
      userIdList.add(user2.getId());
      shareForm.setApprovalRequest(true);
      content = om.writeValueAsString(shareForm);
      
      // going for internal approval request
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      Thread.sleep(1000);
      
      receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, is(greaterThan(0)));
      mimeMessage = receivedMessages[0];
      assertThat(mimeMessage.getSubject(), is("Request for Blueprint Approval"));
      testSmtp.reset();

      blueprint = goalsFactory.getBlueprint(user);
      blueprint.setStatus(GoalStatus.EDIT);
      goalsFactory.updateBlueprint(blueprint);
      
      userIdList.clear();
      externalUserList.add(externalUser);
      shareForm.setApprovalRequest(true);
      content = om.writeValueAsString(shareForm);
      
      // going for external approval request
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      Thread.sleep(1000);
      
      receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, is(greaterThan(0)));
      mimeMessage = receivedMessages[0];
      assertThat(mimeMessage.getSubject(), is("Request for Blueprint Approval"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testBlueprintPublishShareGetDetails() {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();
      dbSetup.removeAll("blueprintBackup");

      User user = dbSetup.getUser("admin@admin.com");
      final User user2 = dbSetup.getUser("dax@surepeople.com");
      
      Blueprint blueprint = addBlueprint(user);
      blueprint = goalsFactory.getBlueprint(user);
      BlueprintApprover approver = new BlueprintApprover();
      approver.setFirstName("Some");
      approver.setLastName("Approver");
      approver.setEmail("someApprover@yopmail.com");
      approver.setCreatedOn(LocalDateTime.now());
      approver.setApprovedOn(LocalDateTime.now());
      blueprint.setApprover(approver);
      blueprint.publish();
      goalsFactory.updateBlueprint(blueprint);

      final ObjectMapper om = new ObjectMapper();
      
      final BlueprintShareForm shareForm = new BlueprintShareForm();
      shareForm.addUser(user2.getId());
      String content = om.writeValueAsString(shareForm);
      
      // no parameters
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      
      List<Token> all = dbSetup.getAll(Token.class);
      assertThat(all.size(), is(greaterThan(0)));
      Token token = all.get(0);
 
      // calling process token
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/processToken/" + token.getTokenId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
            .andExpect(view().name(Constants.VIEW_BLUEPRINT_PUBLISH_SHARE))  
          .andReturn();
      log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
      
      // calling the get details
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/getDetails")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.showProgress").value(false))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      shareForm.setShowProgress(true);
      shareForm.addExternalUser(new ExternalUserForm("Some", "One", "someone@yopmail.com"));
      content = om.writeValueAsString(shareForm);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      // going to cancel publish
      helper.cancelPublish(user);
      
      all = dbSetup.getAll(Token.class);
      token = all.get(0);
      
      // calling process token
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/processToken/" + token.getTokenId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
            .andExpect(view().name(Constants.VIEW_BLUEPRINT_PUBLISH_SHARE))  
          .andReturn();
      log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/getDetails")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.showProgress").value(true))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testBlueprintGetShareDetailsApproval() {
    try {

      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      User user = dbSetup.getUser("admin@admin.com");
      User user2 = dbSetup.getUser("dax@surepeople.com");
      
      final Blueprint blueprint = addBlueprint(user);

      final ObjectMapper om = new ObjectMapper();
      
      final BlueprintShareForm shareForm = new BlueprintShareForm();
      shareForm.addUser(user2.getId());
      shareForm.setApprovalRequest(true);
      String content = om.writeValueAsString(shareForm);
      
      // no parameters
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      
      List<Token> all = dbSetup.getAll(Token.class);
      assertThat(all.size(), is(greaterThan(0)));
      Token token = all.get(0);
 
      // calling process token
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/processToken/" + token.getTokenId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
            .andExpect(view().name("blueprintShare"))  
          .andReturn();
      log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
      
      // calling the get details
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/details")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprintApprovalRequest").value(true))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      BlueprintResponseForm responseForm = new BlueprintResponseForm();
      
      content = om.writeValueAsString(responseForm);
      
      // now testing revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      responseForm.setId(blueprint.getId() + 1);
      content = om.writeValueAsString(responseForm);

      // feedback id is required
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Feedback User id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      FeedbackUser feedbackUser = dbSetup.getFeedbackUserById(token
          .getParamAsString(Constants.PARAM_FEEDBACK_USERID));
      responseForm.setFeedbackUserId(feedbackUser.getId());
      content = om.writeValueAsString(responseForm);

      // now testing revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint id mismatch."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      responseForm.setId(blueprint.getId());
      
      content = om.writeValueAsString(responseForm);

      // now testing revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);

      // trying to submit request again
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException")
              .value("Request has already been submitted."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      validateToken(token);
      
      responseForm.setComment("Some comments to work on.");
      Map<String, String> commentsMap = new HashMap<String, String>();
      commentsMap.put(blueprint.getMissionStatement().getUid(), "Comment for mission statement.");
      final DSActionCategory objective = blueprint.getDevStrategyActionCategoryList().get(0);
      commentsMap.put(objective.getUid(), "Comment for objective");
      final DSAction initiative = objective.getActionList().get(0);
      commentsMap.put(initiative.getUid(), "Comments for key initiatives.");
      responseForm.setCommentsMap(commentsMap);
      
      content = om.writeValueAsString(responseForm);
      
      // trying to revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/get").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // calling the get details
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/details")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprintApprovalRequest").value(true))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      // re-validate token
      validateToken(token);

      responseForm.setApproved(true);
      
      content = om.writeValueAsString(responseForm);
      
      // trying to revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testBlueprintGetShareDetailsFeedback() {
    try {

      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      User user = dbSetup.getUser("admin@admin.com");
      User user2 = dbSetup.getUser("dax@surepeople.com");
      
      final Blueprint blueprint = addBlueprint(user);

      final ObjectMapper om = new ObjectMapper();
      
      final BlueprintShareForm shareForm = new BlueprintShareForm();
      shareForm.addUser(user2.getId());
      String content = om.writeValueAsString(shareForm);
      
      // no parameters
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      
      List<Token> all = dbSetup.getAll(Token.class);
      assertThat(all.size(), is(greaterThan(0)));
      Token token = all.get(0);
 
      // calling process token
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/processToken/" + token.getTokenId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
            .andExpect(view().name("blueprintShare"))  
          .andReturn();
      log.debug("The MVC Response : " + result.getModelAndView().getModelMap());
      
      // calling the get details
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/details")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprintApprovalRequest").value(false))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      BlueprintResponseForm responseForm = new BlueprintResponseForm();
      
      content = om.writeValueAsString(responseForm);
      
      // now testing revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      responseForm.setId(blueprint.getId() + 1);
      
      content = om.writeValueAsString(responseForm);

      // feedback id is required
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Feedback User id is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      FeedbackUser feedbackUser = dbSetup.getFeedbackUserById(token
          .getParamAsString(Constants.PARAM_FEEDBACK_USERID));
      responseForm.setFeedbackUserId(feedbackUser.getId());
      content = om.writeValueAsString(responseForm);
      
      // now testing feedback without comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint id mismatch."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      responseForm.setId(blueprint.getId());
      
      content = om.writeValueAsString(responseForm);

      // now testing revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);

      // trying to submit request again
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException")
              .value("Request has already been submitted."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      validateToken(token);
      
      responseForm.setComment("Some comments to work on.");
      Map<String, String> commentsMap = new HashMap<String, String>();
      commentsMap.put(blueprint.getMissionStatement().getUid(), "Comment for mission statement.");
      final DSActionCategory objective = blueprint.getDevStrategyActionCategoryList().get(0);
      commentsMap.put(objective.getUid(), "Comment for objective");
      final DSAction initiative = objective.getActionList().get(0);
      commentsMap.put(initiative.getUid(), "Comments for key initiatives.");
      responseForm.setCommentsMap(commentsMap);
      
      content = om.writeValueAsString(responseForm);
      
      // trying to submit feedback with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/get").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // calling the get details
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/details")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprintApprovalRequest").value(false))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      // re-validate token
      validateToken(token);

      responseForm.setApproved(true);
      
      content = om.writeValueAsString(responseForm);
      
      // trying to revise with comments
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException")
              .value("Cannot approve a non approval request."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      // updating and removing some comments
      commentsMap.put("1", "Updated mission statement.");
      commentsMap.put("2", " ");
      
      validateToken(token);
      
      responseForm.setApproved(false);
      
      content = om.writeValueAsString(responseForm);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/share/response")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testDeleteComment() {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // blank parameters
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteComment")
              .param("uid", " ")
              .param("by", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("UID is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // blank by parameter
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteComment")
              .param("uid", "1")
              .param("by", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("By is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      // no blueprint
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteComment")
              .param("uid", "1")
              .param("by", "someby")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      // invalid uid and by
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteComment")
              .param("uid", "1")
              .param("by", "someby")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.commentRemoved").value(false))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      FeedbackUser feedbackUser = addFeedbackUser(user);
      
      Map<String, List<Comments>> commentsMap = new HashMap<String, List<Comments>>();
      List<Comments> commentList = new ArrayList<Comments>();
      Comments comment = new Comments(feedbackUser, "Some comment to delete");
      commentList.add(comment);
      commentsMap.put("1", commentList);
      blueprint.setCommentsMap(commentsMap);
      goalsFactory.updateBlueprint(blueprint);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteComment")
              .param("uid", "1")
              .param("by", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.commentRemoved").value(true))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      
      assertThat(blueprint.getCommentsMap().isEmpty(), is(true));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  private FeedbackUser addFeedbackUser(User user) {
    User user2 = dbSetup.getUser("dax@surepeople.com");
    FeedbackUser feedbackUser = new FeedbackUser();
    feedbackUser.setFeedbackFor(user.getId());
    feedbackUser.updateFrom(user2);
    feedbackUser.setFeatureType(FeatureType.Blueprint);
    TokenRequest tokenRequest = new TokenRequest(TokenType.PERPETUAL);
    Token token = tokenFactory.getToken(tokenRequest, TokenProcessorType.BLUEPRINT_SHARE);
    feedbackUser.saveTokenInformation(token);
    dbSetup.addUpdate(feedbackUser);
    return feedbackUser;
  }  
  
  @Test
  public void testApprovalReminder() {
    try {

      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/sendReminder")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/sendReminder")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not under approval."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.setApprover(new BlueprintApprover(feedbackUser));
      blueprint.setStatus(GoalStatus.UNDER_APPROVAL);
      goalsFactory.updateBlueprint(blueprint);

      // success
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/sendReminder")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCancelApprovalRequest() {
    try {

      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelApprovalRequest")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
  
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelApprovalRequest")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not under approval."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.setApprover(new BlueprintApprover(feedbackUser));
      blueprint.setStatus(GoalStatus.UNDER_APPROVAL);
      goalsFactory.updateBlueprint(blueprint);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelApprovalRequest")
              .param("comment", "I don't like you enough.")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCancelApproval() {
    try {

      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelApproval")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
  
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelApproval")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not approved."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.setApprover(new BlueprintApprover(feedbackUser));
      blueprint.setStatus(GoalStatus.APPROVED);
      goalsFactory.updateBlueprint(blueprint);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelApproval")
              .param("comment", "I don't like you enough.")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getStatus(), is(GoalStatus.EDIT));
      assertThat(blueprint.getApprover(), is(nullValue()));
      
      Thread.sleep(1000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testDeleteFeedbackReceived() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteFeedbackReceived")
              .param("uid", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("UID is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteFeedbackReceived")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteFeedbackReceived")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprintUpdate").value(false))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.addToFeedbackList(feedbackUser, "Some comment.");
      goalsFactory.updateBlueprint(blueprint);
      
      BlueprintFeedback blueprintFeedback = blueprint.getFeedbackReceivedList().get(0);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/deleteFeedbackReceived")
              .param("uid", blueprintFeedback.getUid())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprintUpdate").value(true))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testViewedFeedbackReceived() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/viewedFeedbackMessages")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/viewedFeedbackMessages")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      blueprint.setNewFeedbackReceivedCount(1);
      goalsFactory.updateBlueprint(blueprint);
      assertThat(blueprint.getNewFeedbackReceivedCount(), is(1));
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/viewedFeedbackMessages")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getNewFeedbackReceivedCount(), is(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testPublish() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publish")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publish")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not approved."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.addToFeedbackList(feedbackUser, "Some comment.");
      blueprint.addApprover(feedbackUser);
      blueprint.setStatus(GoalStatus.APPROVED);
      goalsFactory.updateBlueprint(blueprint);
      
      // adding another for testing the share functionality
      addFeedbackUser(user);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publish")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getStatus(), is(GoalStatus.PUBLISHED));
      assertThat(blueprint.getCommentsMap(), is(nullValue()));
      
      List<FeedbackUser> all = dbSetup.getAll(FeedbackUser.class);
      assertThat(all.size(), is(0));
      
      List<Token> allToken = dbSetup.getAll(Token.class);
      
      allToken.forEach(t -> assertThat(t.getTokenStatus(), is(TokenStatus.INVALID)));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
 
  @Test
  public void testMarkSuccessMeasure() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/completeSuccessMeasure")
              .param("uid", " ")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("UID is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/completeSuccessMeasure")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/completeSuccessMeasure")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not published."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.addToFeedbackList(feedbackUser, "Some comment.");
      blueprint.addApprover(feedbackUser);
      blueprint.setStatus(GoalStatus.PUBLISHED);
      goalsFactory.updateBlueprint(blueprint);
      
      // adding another for testing the share functionality
      addFeedbackUser(user);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/completeSuccessMeasure")
              .param("uid", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("UID not found in blueprint."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      List<DSActionCategory> devStrategyActionCategoryList = blueprint.getDevStrategyActionCategoryList();
      DSActionCategory objective = devStrategyActionCategoryList.get(0);
      DSAction initiative = objective.getActionList().get(0);
      DSActionData dsActionData = initiative.getActionData().get(0);
      
      final String uid = dsActionData.getUid();
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/completeSuccessMeasure")
              .param("uid", uid)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getStatus(), is(GoalStatus.PUBLISHED));
      assertThat(blueprint.getCommentsMap(), is(nullValue()));
      
      assertThat(blueprint.getCompletedActions().size(), is(1));
      assertTrue(blueprint.getCompletedActions().contains(uid));
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/completeSuccessMeasure")
              .param("uid", uid)
              .param("isComplete", "false")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getStatus(), is(GoalStatus.PUBLISHED));
      
      assertThat(blueprint.getCompletedActions(), is(nullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  @Test
  public void testCancelPublish() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();
      dbSetup.removeAll("blueprintBackup");

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelPublish")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelPublish")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not published."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.setApprover(new BlueprintApprover(feedbackUser));
      blueprint.approve();
      blueprint.setStatus(GoalStatus.PUBLISHED);
      goalsFactory.updateBlueprint(blueprint);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelPublish")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getStatus(), is(GoalStatus.EDIT));
      assertThat(blueprint.getPrevApprover(), is(not(nullValue())));
      
      List<BlueprintBackup> all = dbSetup.getAll(BlueprintBackup.class);
      assertThat(all.size(), is(1));
      BlueprintBackup blueprintBackup = all.get(0);
      assertThat(blueprintBackup.getBlueprintId(), is(blueprint.getId()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCancelPostPublishEdit() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();
      dbSetup.removeAll("blueprintBackup");

      // no blueprint
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelEdit")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);
      blueprint.setStatus(GoalStatus.PUBLISHED);
      goalsFactory.updateBlueprint(blueprint);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelEdit")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint already published."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      FeedbackUser feedbackUser = addFeedbackUser(user);
      blueprint.setApprover(new BlueprintApprover(feedbackUser));
      blueprint.approve();
      blueprint.setStatus(GoalStatus.PUBLISHED);
      goalsFactory.updateBlueprint(blueprint);

      // going into edit mode
      helper.cancelPublish(user);

      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/cancelEdit")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.blueprint").exists())
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint = goalsFactory.getBlueprint(user);
      assertThat(blueprint.getStatus(), is(GoalStatus.PUBLISHED));
      assertThat(blueprint.getPrevApprover(), is(nullValue()));
      assertThat(blueprint.getApprover(), is(not(nullValue())));
      
      List<BlueprintBackup> all = dbSetup.getAll(BlueprintBackup.class);
      assertThat(all.size(), is(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  @Test
  public void testPublishShare() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllLogs();
      dbSetup.removeAll("blueprintBackup");

      final ObjectMapper om = new ObjectMapper();
      
      final BlueprintShareForm shareForm = new BlueprintShareForm();
      String content = om.writeValueAsString(shareForm);
      // no parameters
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Atleast one user is required."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      List<String> userIdList = new ArrayList<String>();
      userIdList.add("abc");
      shareForm.setUserIdList(userIdList);
      
      content = om.writeValueAsString(shareForm);
      
      // blueprint not found for user
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      Blueprint blueprint = addBlueprint(user);

      // Blueprint not published
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Blueprint not published."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      blueprint.setStatus(GoalStatus.PUBLISHED);
      goalsFactory.updateBlueprint(blueprint);
      
      // user not found 
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());

      userIdList.clear();
      userIdList.add(user.getId());
      content = om.writeValueAsString(shareForm);

      // blueprint request for self
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Cannot send request to yourself."))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
 

      User user2 = dbSetup.getUser("dax@surepeople.com");
      
      userIdList.clear();
      userIdList.add(user2.getId());
      content = om.writeValueAsString(shareForm);
      
      // blueprint not edit status
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, is(greaterThan(0)));
      MimeMessage mimeMessage = receivedMessages[0];
      assertThat(mimeMessage.getSubject(), is("Blueprint Share"));
      testSmtp.reset();
      
      List<ExternalUserForm> externalUserList = new ArrayList<ExternalUserForm>();
      ExternalUserForm externalUser = new ExternalUserForm();
      externalUser.setEmail("somebodyExternal@yopmail.com");
      externalUserList.add(externalUser);
      shareForm.setExternalUserList(externalUserList);
      
      content = om.writeValueAsString(shareForm);
      
      // going for external request, first name missing
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      externalUser.setFirstName("Somebody");
      externalUser.setLastName("External");
      content = om.writeValueAsString(shareForm);
      
      // going for external request, all data
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/blueprint/publishShare")
                  .content(content)
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, is(greaterThan(0)));
      mimeMessage = receivedMessages[0];
      assertThat(mimeMessage.getSubject(), is("Blueprint Share"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  private void validateToken(Token token) {
    token.setTokenStatus(TokenStatus.ENABLED);
    dbSetup.addUpdate(token);
    
    Token tokenInSession = (Token) session2.getAttribute(Constants.PARAM_TOKEN);
    tokenInSession.setTokenStatus(TokenStatus.ENABLED);
  }

}
