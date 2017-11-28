package com.sp.web.controller.lndfeedback;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.lndfeedback.DevelopmentForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.SPFeature;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintMissionStatement;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.model.lndfeedback.UserDevelopmentFeedbackResponse;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.lndfeedback.DevelopmentFeedbackRepository;
import com.sp.web.repository.user.UserRepository;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author pradeepruhil
 *
 */
public class DevelopmentFeedbackControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  private DevelopmentFeedbackRepository feedbackRepository;
  
  @Autowired
  private UserRepository userRepository;
    
  /**
   * Test method for
   * {@link com.sp.web.controller.generic.GenericController#getAll(org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testGetAll() throws Exception {
    dbSetup.removeSpGoals();
    dbSetup.removeAll("developmentFeedback");
    dbSetup.createGoals();
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/developmentfeedback/getAll").contentType(MediaType.APPLICATION_JSON).session(
                session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    User user = dbSetup.getUser("admin@admin.com");
    addDevelopmentFeedback(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/getAll").contentType(MediaType.APPLICATION_JSON).session(
                session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    
    result = this.mockMvc
        .perform(
            post("/dashboard/getDevelopmentFeedbacks").contentType(MediaType.APPLICATION_JSON).session(
                session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
  }
  
  /**
   * @param user
   */
  private void addDevelopmentFeedback(User user) throws Exception {
    dbSetup.removeAll("actionPlan");
    dbSetup.removeAllFeedbackUsers();
    dbSetup.createActionPlan();
    dbSetup.createFeedbackUsers();
    addBlueprint(user);
    for (int i = 0; i < 5; i++) {
      DevelopmentFeedback developmentFeedback = new DevelopmentFeedback();
      developmentFeedback.setComment("Give me feedback, no " + i);
      developmentFeedback.setCompanyId(user.getCompanyId());
      developmentFeedback.setCreatedOn(LocalDateTime.now());
      FeedbackUser feedbackUserById = dbSetup.getFeedbackUserById("1");
      developmentFeedback.setFeedbackUserId("1");
      developmentFeedback.setUserId(user.getId());
      if (i < 2) {
        developmentFeedback.setDevFeedRefId("1");
        developmentFeedback.setSpFeature(SPFeature.Erti.toString());
        developmentFeedback.setRequestStatus(RequestStatus.COMPLETED);
        developmentFeedback.setResponse("You are good");
        developmentFeedback.setUpdatedOn(LocalDateTime.now().plusDays(1));
        developmentFeedback.setRepliedOn(LocalDateTime.now());
        developmentFeedback.setTitle("Planning");
        
      } else if (i < 3) {
        developmentFeedback.setDevFeedRefId("11");
        developmentFeedback.setFeedParentRefId("1");
        developmentFeedback.setSpFeature(SPFeature.OrganizationPlan.toString());
        developmentFeedback.setUpdatedOn(LocalDateTime.now().minusDays(1));
      } else {
        developmentFeedback.setSpFeature(SPFeature.Blueprint.toString());
        developmentFeedback.setDevFeedRefId(user.getBlueprintId());
        developmentFeedback.setRequestStatus(RequestStatus.ACTIVE);
        developmentFeedback.setUpdatedOn(LocalDateTime.now());
      }
      dbSetup.addUpdate(developmentFeedback);
      
    }
  }
  
  /**
   * Test method for
   * {@link com.sp.web.controller.generic.GenericController#get(java.lang.String, org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testGet() throws Exception{
    dbSetup.removeSpGoals();
    dbSetup.removeAll("developmentFeedback");
    dbSetup.createGoals();
    DevelopmentForm developmentForm = new DevelopmentForm();
    developmentForm.setComment("Give me feedback on practice area Time Management");
    developmentForm.setDevFeedRefId("1");
    developmentForm.setSpFeature(SPFeature.Erti.toString());
    List<String> feedbackUsers = new ArrayList<>();
    feedbackUsers.add("pradeep1@surepeople.com");
    feedbackUsers.add("dax@surepeople.com");
    developmentForm.setFeedbackUsers(feedbackUsers);
    
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(developmentForm);
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    developmentForm.setComment("Second me feedback on practice area Time Management");
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    Thread.sleep(1000);
    User user = dbSetup.getUser("admin@admin.com");
    addBlueprint(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    
    developmentForm.setDevFeedRefId(user.getBlueprintId());
    developmentForm.setSpFeature(SPFeature.Blueprint.toString());
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<DevelopmentFeedback> all = dbSetup.getAll(DevelopmentFeedback.class);
    DevelopmentFeedback developmentFeedback = all.get(all.size() -1);
    
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/get").param("id", developmentFeedback.getId())
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
  }
  
  /**
   * Test method for
   * {@link com.sp.web.controller.generic.GenericController# create(com.sp.web.form.generic.GenericForm, org.springframework.security.core.Authentication)}
   * .
   * 
   * @throws Exception
   *           in case any exception occurred.
   */
  @Test
  public void testCreate() throws Exception {
    testSmtp.start();
    dbSetup.removeSpGoals();
    dbSetup.removeAll("developmentFeedback");
    dbSetup.createGoals();
    DevelopmentForm developmentForm = new DevelopmentForm();
    developmentForm.setComment("Give me feedback on practice area Time Management");
    developmentForm.setDevFeedRefId("1");
    developmentForm.setSpFeature(SPFeature.Erti.toString());
    List<String> feedbackUsers = new ArrayList<>();
    feedbackUsers.add("pradeep1@surepeople.com");
    feedbackUsers.add("dax@surepeople.com");
    developmentForm.setFeedbackUsers(feedbackUsers);
    
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(developmentForm);
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    developmentForm.setComment("Second me feedback on practice area Time Management");
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    Thread.sleep(1000);
    User user = dbSetup.getUser("admin@admin.com");
    addBlueprint(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    
    developmentForm.setDevFeedRefId(user.getBlueprintId());
    developmentForm.setSpFeature(SPFeature.Blueprint.toString());
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    
    developmentForm.setDevFeedRefId("1");
    developmentForm.setSpFeature(SPFeature.Erti.toString());
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/emailFeedbacks").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    Thread.sleep(5000);
    testSmtp.stop();
  }
  
  /**
   * Test method for
   * {@link com.sp.web.controller.generic.GenericController#update(com.sp.web.form.generic.GenericForm, org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testUpdate() throws Exception{
    testSmtp.start();
    dbSetup.removeSpGoals();
    dbSetup.removeAll("developmentFeedback");
    dbSetup.removeAll("token");
    
    dbSetup.createGoals();
    DevelopmentForm developmentForm = new DevelopmentForm();
    developmentForm.setComment("Give me feedback on practice area Time Management");
    developmentForm.setDevFeedRefId("1");
    developmentForm.setSpFeature(SPFeature.Erti.toString());
    List<String> feedbackUsers = new ArrayList<>();
    feedbackUsers.add("pradeep1@surepeople.com");
    feedbackUsers.add("dax@surepeople.com");
    developmentForm.setFeedbackUsers(feedbackUsers);
    
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(developmentForm);
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    developmentForm.setComment("Second me feedback on practice area Time Management");
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    Thread.sleep(1000);
    User user = dbSetup.getUser("admin@admin.com");
    addBlueprint(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    
    developmentForm.setDevFeedRefId(user.getBlueprintId());
    developmentForm.setSpFeature(SPFeature.Blueprint.toString());
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
////    DevelopmentFeedback feedback = feedbackRepository.findDevFeedbackbyTokenId(token.getTokenId());
////    
////    session.setAttribute("id", feedback.getId());
//    FeedbackUser feedbackUser = userRepository.findFeedbackUser(feedback.getFeedbackUserId());

    List<Token> all = dbSetup.getAll(Token.class);
    Token token = all.get(0);
    String tokenId = token.getTokenId();
    result = this.mockMvc
        .perform(
          MockMvcRequestBuilders.get("/processToken/" + tokenId)
                .session(session))
        .andExpect(MockMvcResultMatchers.view().name("submitRequestFeedback")).andReturn();
    
//    authenticationHelper.doAuthenticateWithoutPassword(session, feedbackUser);
    
    result = this.mockMvc
        .perform(
            MockMvcRequestBuilders.get("/developmentfeedback/getAllFeedbackRequest")
                .session(session))
                .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response **** : " + result.getResponse().getContentAsString());

    
    
    
    List<DevelopmentFeedback> dbs = dbSetup.getAll(DevelopmentFeedback.class);
    DevelopmentFeedback developmentFeedback = dbs.get(0);
    developmentForm.setResponse("Feebdack given, This si good");
    developmentForm.setDecline(false);
    developmentForm.setId(developmentFeedback.getId());
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/update").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response ==: " + result.getResponse().getContentAsString());
   
    result = this.mockMvc
        .perform(
          MockMvcRequestBuilders.get("/processToken/" + tokenId)
                .session(session))
        .andExpect(MockMvcResultMatchers.view().name("redirect:http://ec2-52-10-90-53.us-west-2.compute.amazonaws.com/?status=feedbackTokenUsed")).andReturn();
    
    
  }
  
  /**
   * Test method for
   * {@link com.sp.web.controller.generic.GenericController#delete(java.lang.String, org.springframework.security.core.Authentication)}
   * .
   */
  @Test
  public void testDelete() throws Exception {
    testSmtp.start();
    dbSetup.removeSpGoals();
    dbSetup.removeAll("developmentFeedback");
    dbSetup.createGoals();
    DevelopmentForm developmentForm = new DevelopmentForm();
    developmentForm.setComment("Give me feedback on practice area Time Management");
    developmentForm.setDevFeedRefId("1");
    developmentForm.setSpFeature(SPFeature.Erti.toString());
    List<String> feedbackUsers = new ArrayList<>();
    feedbackUsers.add("pradeep1@surepeople.com");
    feedbackUsers.add("dax@surepeople.com");
    developmentForm.setFeedbackUsers(feedbackUsers);
    
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(developmentForm);
    /* invalid request */
    MvcResult result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    developmentForm.setComment("Second me feedback on practice area Time Management");
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    Thread.sleep(1000);
    User user = dbSetup.getUser("admin@admin.com");
    addBlueprint(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    
    developmentForm.setDevFeedRefId(user.getBlueprintId());
    developmentForm.setSpFeature(SPFeature.Blueprint.toString());
    request = om.writeValueAsString(developmentForm);
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    List<DevelopmentFeedback> all = dbSetup.getAll(DevelopmentFeedback.class);
    
    Assert.assertEquals(6, all.size());
    
    List<DevelopmentFeedback> findAllByDevFeedRefId = feedbackRepository.findAllByDevFeedRefId("1", user.getCompanyId(), SPFeature.Erti.toString());
    DevelopmentFeedback developmentFeedback = findAllByDevFeedRefId.get(0);
    String id = developmentFeedback.getId();
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/delete").param("id", id).param("spFeature", SPFeature.Erti.toString())
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    all = dbSetup.getAll(DevelopmentFeedback.class);
    
    Assert.assertEquals(5, all.size());
    
    result = this.mockMvc
        .perform(
            post("/developmentfeedback/delete").param("devFeedRefId", "1").param("spFeature", SPFeature.Erti.toString())
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    all = dbSetup.getAll(DevelopmentFeedback.class);
    
    Assert.assertEquals(2, all.size());
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
  public void testGetAllByDevFeedRefId() throws Exception {
    dbSetup.removeAll("developmentFeedback");
    User user = dbSetup.getUser("admin@admin.com");
    addDevelopmentFeedback(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    MvcResult result = this.mockMvc
        .perform(
            post("/developmentfeedback/getAllByDevFeedRefId").param("devFeedRefId", "1")
                .param("spFeature", SPFeature.Erti.toString())
                .contentType(MediaType.APPLICATION_JSON).session(
                session)).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());    
  }
  
  @Test
  public void testGetAllFeedbackResponses() {
    try {
      dbSetup.removeAll("developmentFeedback");
      dbSetup.removeAll("userDevelopmentFeedbackResponse");
      MvcResult result = this.mockMvc
          .perform(
              post("/developmentfeedback/getAllFeedbackResponses")
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.developmentFeedbackResponseListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      final UserDevelopmentFeedbackResponse developmentFeedbackResponse = feedbackRepository
          .getDevelopmentFeedbackResponse(user.getId());
      User user2 = dbSetup.getUser("dax@surepeople.com");
      DevelopmentFeedback feedback = new DevelopmentFeedback();
      feedback.setComment("Some comment");
      feedback.setCompanyId(user.getCompanyId());
      feedback.setCreatedOn(LocalDateTime.now());
      feedback.setRepliedOn(LocalDateTime.now());
      feedback.setDevFeedRefId("574448ee77c8da57d03c1ae2");
      feedback.setRequestStatus(RequestStatus.COMPLETED);
      feedback.setResponse("Ha ha response.");
      feedback.setSpFeature(SPFeature.Blueprint.toString());
      feedback.setUserId(user2.getId());
      feedbackRepository.save(feedback);
      developmentFeedbackResponse.add(feedback, user2);
      feedbackRepository.update(developmentFeedbackResponse);
      
      result = this.mockMvc
          .perform(
              post("/developmentfeedback/getAllFeedbackResponses")
                  .contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.developmentFeedbackResponseListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }    
  }
  
}
