package com.sp.web.controller.competency;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.account.AccountRepository;
import com.sp.web.dao.CompanyDao;
import com.sp.web.form.FeedbackUserForm;
import com.sp.web.form.competency.CompetencyRequestForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPRating;
import com.sp.web.model.SPRatingScore;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.competency.UserEvaluationRequest;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.library.ArticlesFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author vikram
 *
 *         The test cases for admin services of blueprint.
 */
public class CompetencyControllerTest extends SPTestLoggedInBase {
  
  private static final Random rand = new Random();
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  CompetencyFactory competencyFactory;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Test
  public void testInitiateEvaluation() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userCompetency");
      
      User user = addCompetencyRole();
      
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      ObjectMapper om = new ObjectMapper();
      CompetencyRequestForm form = new CompetencyRequestForm();
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      
      createCompetencyEvaluation(companyId);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUserForm manager = new FeedbackUserForm();
      manager.setExistingUserEmail("dax@surepeople.com");
      
      // adding manager and peers
      form.setManager(manager);
      List<FeedbackUserForm> peerList = new ArrayList<FeedbackUserForm>();
      peerList.add(new FeedbackUserForm("Aisha", "Abraham", "aisha@yopmail.com"));
      peerList.add(new FeedbackUserForm("pradeep1@surepeople.com"));
      form.setPeerList(peerList);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testInitiateEvaluationAdmin() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userCompetency");

      User user = dbSetup.getUser();
      user.addRole(RoleType.CompetencyAdmin);
      dbSetup.addUpdate(user);
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      ObjectMapper om = new ObjectMapper();
      CompetencyRequestForm form = new CompetencyRequestForm();
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      createCompetencyEvaluation(companyId);
      
      form.setUserForEmail("dax@surepeople.com");
      content = om.writeValueAsString(form);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency profile not set for user."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      user2.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user2);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not part of competency evaluation."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUserForm manager = new FeedbackUserForm();
      manager.setExistingUserEmail("dax@surepeople.com");
      
      // adding manager and peers
      form.setUserForEmail("admin@admin.com");
      form.setManager(manager);
      List<FeedbackUserForm> peerList = new ArrayList<FeedbackUserForm>();
      peerList.add(new FeedbackUserForm("Aisha", "Abraham", "aisha@yopmail.com"));
      peerList.add(new FeedbackUserForm("pradeep1@surepeople.com"));
      form.setPeerList(peerList);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/initiate")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testGet() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userCompetency");

      User user = addCompetencyRole();
      
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/get")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding competency profile to the user
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      result = this.mockMvc
          .perform(
              post("/competency/get")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation").value(false))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
      userCompetencyEvaluation.setShowSelfEvaluations(true);
      competencyFactory.update(userCompetencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/get")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation").value(true))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testStartEvaluation() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      addCompetencyRole();
      
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      final CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Evalution type not supported."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
      requiredEvaluationList.add(EvaluationType.Manager);
      competencyEvaluation.setRequiredEvaluationList(requiredEvaluationList);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Evalution type not supported."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      requiredEvaluationList.add(EvaluationType.Self);
      competencyFactory.update(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency profile not set."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
            
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .param("memberId", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser fbUser = new FeedbackUser();
      fbUser.setEmail("dax@yopmail.com");
      fbUser.setFeatureType(FeatureType.Competency);
      fbUser.setType(UserType.External);
      fbUser.addRole(RoleType.FeedbackUser);
      fbUser.setCompanyId(companyId);
      dbSetup.addUpdate(fbUser);
      
      CompetencyEvaluationRequest competencyRequest = new CompetencyEvaluationRequest();
      competencyRequest.setCompanyId(companyId);
      final String fbUserId = fbUser.getId();
      competencyRequest.setFeedbackUserId(fbUserId);
      competencyFactory.update(competencyRequest);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, fbUser);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .param("memberId", fbUserId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No requests found for user."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<UserEvaluationRequest> requestsList = new ArrayList<UserEvaluationRequest>();
      UserEvaluationRequest evaluationRequest = new UserEvaluationRequest(
          competencyProfile.getId(), user.getId(), EvaluationType.Manager);
      requestsList.add(evaluationRequest);
      // adding to the request list
      competencyRequest.setRequestsList(requestsList);
      competencyFactory.update(competencyRequest);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .param("memberId", fbUserId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No requests found for user."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/start")
              .param("memberId", user.getId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andExpect(jsonPath("$.success.user").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  private User addCompetencyRole() {
    User user = dbSetup.getUser();
    user.addRole(RoleType.Competency);
    dbSetup.addUpdate(user);
    authenticationHelper.doAuthenticateWithoutPassword(session, user);
    return user;
  }  
  
  @Test
  public void testSaveEvaluationSelf() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      addCompetencyRole();

      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      final CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not part of competency evaluation."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      
      competencyFactory.addUserToCompetencyEvaluation(user, competencyEvaluation);
      competencyFactory.update(competencyEvaluation);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation type not supported."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
      requiredEvaluationList.add(EvaluationType.Manager);
      competencyEvaluation.setRequiredEvaluationList(requiredEvaluationList);
      competencyFactory.update(competencyEvaluation);
      requiredEvaluationList.add(EvaluationType.Self);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("scoreArray", "3.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Score mismatch from competency profile."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      startMail();

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  @Test
  public void testSaveEvaluationManager() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      final User user = addCompetencyRole();
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", "abc")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      final String competencyEvaluationId = competencyEvaluation.getId();

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", "abc")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser fbUser = new FeedbackUser();
      fbUser.setFirstName("DaxYop");
      fbUser.setLastName("Super");
      fbUser.setEmail("dax@yopmail.com");
      fbUser.setFeatureType(FeatureType.Competency);
      fbUser.setType(UserType.External);
      fbUser.addRole(RoleType.FeedbackUser);
      fbUser.setCompanyId(companyId);
      dbSetup.addUpdate(fbUser);
      
      
      CompetencyEvaluationRequest competencyRequest = new CompetencyEvaluationRequest(fbUser);
      competencyFactory.update(competencyRequest);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, fbUser);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", "abc")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No requests found for user."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String userId = user.getId();
      final String competencyProfileId = competencyProfile.getId();
      competencyRequest.add(competencyProfileId, userId, EvaluationType.Manager);
      competencyFactory.update(competencyRequest);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not part of competency evaluation."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.addUserToCompetencyEvaluation(user, competencyEvaluation);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation type not supported."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
      requiredEvaluationList.add(EvaluationType.Self);
      competencyEvaluation = competencyFactory.getCompetencyEvaluation(competencyEvaluationId);
      competencyEvaluation.setRequiredEvaluationList(requiredEvaluationList);
      competencyFactory.update(competencyEvaluation);
      requiredEvaluationList.add(EvaluationType.Manager);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Evaluation not initiated."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = competencyFactory.getCompetencyEvaluation(competencyEvaluationId);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Score mismatch from competency profile."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser wrongReviewer = new FeedbackUser();
      dbSetup.addUpdate(wrongReviewer);
      
      UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
      UserCompetencyEvaluation evaluation = userCompetencyEvaluation.getEvaluation(competencyEvaluationId);
      evaluation.setManager(new UserCompetencyEvaluationScore(wrongReviewer));
      competencyFactory.update(userCompetencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Reviewer not authorized."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      evaluation.setManager(new UserCompetencyEvaluationScore(fbUser));
      competencyFactory.update(userCompetencyEvaluation);
      
      startMail();

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testSaveEvaluationPeer() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      final User user = addCompetencyRole();
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", "abc")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      final String competencyEvaluationId = competencyEvaluation.getId();

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", "abc")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser fbUser = new FeedbackUser();
      fbUser.setFirstName("DaxYop");
      fbUser.setLastName("Super");
      fbUser.setEmail("dax@yopmail.com");
      fbUser.setFeatureType(FeatureType.Competency);
      fbUser.setType(UserType.External);
      fbUser.addRole(RoleType.FeedbackUser);
      fbUser.setCompanyId(companyId);
      dbSetup.addUpdate(fbUser);
      
      
      CompetencyEvaluationRequest competencyRequest = new CompetencyEvaluationRequest(fbUser);
      competencyFactory.update(competencyRequest);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, fbUser);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", "abc")
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No requests found for user."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String userId = user.getId();
      final String competencyProfileId = competencyProfile.getId();
      competencyRequest.add(competencyProfileId, userId, EvaluationType.Peer);
      competencyFactory.update(competencyRequest);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not part of competency evaluation."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.addUserToCompetencyEvaluation(user, competencyEvaluation);
      competencyFactory.update(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation type not supported."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
      requiredEvaluationList.add(EvaluationType.Self);
      requiredEvaluationList.add(EvaluationType.Manager);
      competencyEvaluation = competencyFactory.getCompetencyEvaluation(competencyEvaluationId);
      competencyEvaluation.setRequiredEvaluationList(requiredEvaluationList);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation type not supported."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      requiredEvaluationList.add(EvaluationType.Peer);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Evaluation not initiated."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Score mismatch from competency profile."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser wrongReviewer = new FeedbackUser();
      wrongReviewer.setEmail("test@test.com");
      dbSetup.addUpdate(wrongReviewer);
      
      UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
      UserCompetencyEvaluation evaluation = userCompetencyEvaluation.getEvaluation(competencyEvaluationId);
      evaluation.addPeer(new UserCompetencyEvaluationScore(wrongReviewer));
      competencyFactory.update(userCompetencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Peer not found in request list."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      evaluation.addPeer(new UserCompetencyEvaluationScore(fbUser));
      competencyFactory.update(userCompetencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/save")
              .param("memberId", userId)
              .param("scoreArray", "3.0, 4.0")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
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
  public void testGetScoreDetails() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      User user = addCompetencyRole();
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Competency evaluation details not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      double[] scoreArray = {3.0d, 4.0d};
      UserCompetencyEvaluationDetails evaluationDetails = competencyFactory.createCompetencyEvaluationDetails(
          scoreArray, user.getId(), "abc", "abc", "abc",
          EvaluationType.Self);
      
      final String evaluationId = evaluationDetails.getId();
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Competency evalaution not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      final String competencyEvaluationId = competencyEvaluation.getId();
      evaluationDetails.setEvaluationId(competencyEvaluationId);
      competencyFactory.update(evaluationDetails);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "User competency evaluation not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.addUserToCompetencyEvaluation(user, competencyEvaluation);
      competencyFactory.update(competencyEvaluation);
      
      final String competencyProfileId = competencyProfile.getId();
      evaluationDetails.setCompetencyId(competencyProfileId);
      competencyFactory.update(evaluationDetails);
      
      competencyEvaluation = competencyFactory.getCompetencyEvaluation(competencyEvaluationId);
//      final CompetencyEvaluationByProfile competencyEvaluationByProfile = competencyEvaluation
//          .findOrCreateCompetencyEvaluationByProfile(competencyProfileId, competencyFactory);
      competencyFactory.update(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user.addRole(RoleType.CompetencyAdmin);
      dbSetup.addUpdate(user);
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
//      competencyEvaluationByProfile.addUserCompetencyResult(user);
      competencyFactory.update(competencyEvaluation);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      evaluationDetails.setEvaluationType(EvaluationType.Manager);
      competencyFactory.update(evaluationDetails);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Manager not set."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      FeedbackUser manager = new FeedbackUser();
      manager.setEmail("manager@yopmail.com");
      manager.setFirstName("Manager yahoo");
      dbSetup.addUpdate(manager);
      
      UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
      UserCompetencyEvaluation evalaution = userCompetencyEvaluation.getEvaluation(competencyEvaluationId);
      evalaution.setManager(new UserCompetencyEvaluationScore(manager));
      competencyFactory.update(userCompetencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

//      evaluationDetails.setEvaluationType(EvaluationType.Peer);
//      competencyFactory.update(evaluationDetails);
//
//      result = this.mockMvc
//          .perform(
//              post("/competency/evaluation/feedback/getEvaluationDetails")
//              .param("scoreDetailsId", evaluationId)
//              .contentType(MediaType.TEXT_PLAIN)
//              .session(session))
//              .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(
//              jsonPath("$.error.IllegalArgumentException").value(
//                  "Peer review not found."))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//      
//      FeedbackUser peerFBUser = new FeedbackUser();
//      peerFBUser.setEmail("peer@yopmail.com");
//      peerFBUser.setFirstName("Peer yahoo");
//      dbSetup.addUpdate(peerFBUser);
////      userEvaluationResult.addPeer(new UserCompetencyEvaluationScore(peerFBUser));
//      
//      competencyFactory.update(competencyEvaluation);
//      
//      result = this.mockMvc
//          .perform(
//              post("/competency/evaluation/feedback/getEvaluationDetails")
//              .param("scoreDetailsId", evaluationId)
//              .contentType(MediaType.TEXT_PLAIN)
//              .session(session))
//              .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(
//              jsonPath("$.error.IllegalArgumentException").value(
//                  "Peer review not found."))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//      
//      evaluationDetails.setReviewerId(peerFBUser.getId());
//      competencyFactory.update(evaluationDetails);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/getEvaluationDetails")
              .param("scoreDetailsId", evaluationId)
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
  public void testGetRequests() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();

      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      
      final User user = addCompetencyRole();

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/requests")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(status().is2xxSuccessful())
              .andExpect(content().contentType("application/json;charset=UTF-8"))
              .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Unable to proceed. This Entry is no longer available"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      final CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/requests")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      FeedbackUser fbUser = new FeedbackUser();
      fbUser.setEmail("dax@yopmail.com");
      fbUser.setFeatureType(FeatureType.Competency);
      fbUser.setType(UserType.External);
      fbUser.addRole(RoleType.FeedbackUser);
      fbUser.setCompanyId(companyId);
      dbSetup.addUpdate(fbUser);
      
      CompetencyEvaluationRequest competencyRequest = new CompetencyEvaluationRequest();
      competencyRequest.setCompanyId(companyId);
      final String fbUserId = fbUser.getId();
      competencyRequest.setFeedbackUserId(fbUserId);
      competencyFactory.update(competencyRequest);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, fbUser);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/requests")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      final String userId = user.getId();
      
      List<UserEvaluationRequest> requestsList = new ArrayList<UserEvaluationRequest>();
      final String competencyProfileId = competencyProfile.getId();
      UserEvaluationRequest evalautionRequest = new UserEvaluationRequest(
          competencyProfileId, userId, EvaluationType.Manager);
      requestsList.add(evalautionRequest);
      competencyRequest.setRequestsList(requestsList);
      competencyFactory.update(competencyRequest);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/requests")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User evaluation result not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      competencyFactory.addUserToCompetencyEvaluation(user, competencyEvaluation);
      competencyFactory.update(competencyEvaluation);
      
      FeedbackUser fbUser2 = new FeedbackUser();
      fbUser2.setFirstName("Aisha");
      fbUser2.setLastName("Abraham");
      fbUser2.setEmail("aisha@yopmail.com");
      dbSetup.addUpdate(fbUser2);
      UserCompetencyEvaluationScore evaluationScore = new UserCompetencyEvaluationScore(fbUser2);
      evaluationScore.setScore(4.0d);
      competencyFactory.update(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/requests")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      evalautionRequest.setType(EvaluationType.Peer);
      competencyFactory.update(competencyRequest);

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/feedback/requests")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testGetPreviousEvaluations() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userCompetency");

      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      
      final User user = addCompetencyRole();

      MvcResult result = this.mockMvc
          .perform(
              post("/competency/getPreviousEvaluations")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      final List<EvaluationType> requiredEvaluationList = competencyEvaluation.getRequiredEvaluationList();
      requiredEvaluationList.clear();
      requiredEvaluationList.add(EvaluationType.Manager);
      competencyFactory.update(competencyEvaluation);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/getPreviousEvaluations")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/getPreviousEvaluations")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      addScores(user, competencyProfile, competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/getPreviousEvaluations")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/getPreviousEvaluations")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  private void addScores(User user, CompetencyProfile competencyProfile,
      CompetencyEvaluation competencyEvaluation) {
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
    UserCompetencyEvaluation evaluation = userCompetencyEvaluation.getEvaluation(competencyEvaluation.getId());
    evaluation
        .setSelf(addRandomEvaluation(new BaseCompetencyEvaluationScore(), competencyProfile));
    competencyFactory.update(userCompetencyEvaluation);
  }

  private BaseCompetencyEvaluationScore addRandomEvaluation(
      BaseCompetencyEvaluationScore score,
      CompetencyProfile competencyProfile) {
    List<String> competencyIdList = competencyProfile.getCompetencyIdList();
    double[] scoreArray = new double[competencyIdList.size()];
    double totalScore = 0d;
    final int competencySize = scoreArray.length;
    for (int i = 0; i < competencySize; i++) {
      scoreArray[i] = rand.nextInt(5);
      totalScore += scoreArray[i];
    }
    
    UserCompetencyEvaluationDetails evaluationDetails = new UserCompetencyEvaluationDetails();
    evaluationDetails.setScoreArray(scoreArray);
    evaluationDetails.setTotalScore(totalScore / competencySize);
    competencyFactory.update(evaluationDetails);
    score.setCompetencyEvaluationScoreDetailsId(evaluationDetails.getId());
    score.setScore(evaluationDetails.getTotalScore());
    return score;
  }
  
  private CompetencyEvaluation createCompetencyEvaluation(final String companyId) {
    List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
    requiredEvaluationList.add(EvaluationType.Self);
    CompetencyEvaluation competencyEvaluation = CompetencyEvaluation.newInstance(companyId,
        requiredEvaluationList);
    competencyEvaluation.setEndDate(LocalDateTime.now().plusDays(2));
    dbSetup.addUpdate(competencyEvaluation);
    // adding the competency profiles
    competencyFactory.getCompanyCompetencyProfiles(companyId).stream()
        .map(c -> competencyFactory.getCompetencyProfile(c.getId()))
        .forEach(competencyEvaluation::addCompetencyProfile);

    // iterate over all the users and add them to the competency profiles user map
    List<User> memberList = accountRepository.getAllMembersForCompany(companyId);
    memberList.stream().filter(m -> m.getCompetencyProfileId() != null)
        .forEach(m -> competencyFactory.addUserToCompetencyEvaluation(m, competencyEvaluation));
    
    competencyFactory.update(competencyEvaluation);
    
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setCompetencyEvaluationId(competencyEvaluation.getId());
    company.setEvaluationInProgress(true);
    companyFactory.updateCompanyDao(company);
    return competencyEvaluation;
  }  
  
  private CompetencyProfile createCompetencyProfile(String companyId) {
    CompetencyProfile competencyProfile = new CompetencyProfile();
    competencyProfile.setActive(true);
    competencyProfile.setName("Some competency name");
    competencyProfile.setDescription("Some competency description.");
    competencyProfile.setCompanyId(companyId);
    List<String> competencyIdList = new ArrayList<String>();
    competencyIdList.add(addCompetency("1").getId());
    competencyIdList.add(addCompetency("2").getId());
    competencyProfile.setCompetencyIdList(competencyIdList);
    dbSetup.addUpdate(competencyProfile);
    return competencyProfile;
  }

  private SPGoal addCompetency(String id) {
    SPGoal competency = new SPGoal();
    competency.setName("Some competency name." + id);
    competency.setStatus(GoalStatus.ACTIVE);
    SPRating rating = new SPRating();
    competency.setRating(rating);
    final List<SPRatingScore> ratingList = new ArrayList<SPRatingScore>();
    SPRatingScore ratingScore = new SPRatingScore();
    ratingScore.setTitle("Terrible");
    ratingScore.setScore(1);
    ratingScore.setDescription("How terrible.");
    ratingList.add(ratingScore);
    SPRatingScore ratingScore2 = new SPRatingScore();
    ratingScore2.setTitle("Intermediate Terrible");
    ratingScore2.setScore(3);
    ratingScore2.setDescription("How intermediate terrible.");
    ratingList.add(ratingScore2);
    SPRatingScore ratingScore3 = new SPRatingScore();
    ratingScore3.setTitle("Super Terrible");
    ratingScore3.setScore(10);
    ratingScore3.setDescription("How super terrible.");
    ratingList.add(ratingScore3);
    rating.setRatingList(ratingList);
    List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory actionCategory = new DSActionCategory();
    devStrategyActionCategoryList.add(actionCategory);
    competency.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    actionCategory.setTitle("action category name");
    List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action =  new DSAction();
    actionList.add(action);
    actionCategory.setActionList(actionList);
    action.setTitle("Strategy name" + id);
    List<String> mandatoryArticles = new ArrayList<String>();
    mandatoryArticles.add("1");
    competency.setMandatoryArticles(mandatoryArticles);
    dbSetup.addUpdate(competency);
    return competency;
  }
  
  private void resetCompanyEvaluationStatus(String companyId) {
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setEvaluationInProgress(false);
    company.setCompetencyEvaluationId(null);
    companyFactory.updateCompanyDao(company);
  }
  
}