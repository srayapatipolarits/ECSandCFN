package com.sp.web.controller.admin.competency;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.account.AccountRepository;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPRating;
import com.sp.web.model.SPRatingScore;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyEvaluationRequest;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.competency.RatingConfigurationType;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.spectrum.competency.SpectrumCompetencyProfileEvaluationResults;
import com.sp.web.model.task.TaskType;
import com.sp.web.model.todo.CompetencyTodoRequest;
import com.sp.web.model.todo.ParentTodoTaskRequests;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.scheduler.competency.CompetencyScheduler;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.mail.internet.MimeMessage;

/**
 * @author vikram
 *
 *         The test cases for admin services of blueprint.
 */
@Component
public class CompetencyManagerControllerTest extends SPTestLoggedInBase {
  
  private static final Random rand = new Random();
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  CompetencyFactory competencyFactory;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  CompetencyScheduler scheduler;
  
  @Autowired
  CompetencyManagerControllerHelper helper;
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  TodoFactory todoFactory;
  
  @Test
  public void testGet() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("competencyProfile");
      
      User user = dbSetup.getUser();
      user.addRole(RoleType.CompetencyAdmin);
      UserGroup userGroup = dbSetup.getAll(UserGroup.class).get(0);
      final GroupAssociation groupAssociation = new GroupAssociation(userGroup.getName());
      groupAssociation.setGroupId(userGroup.getId());
      user.addGroupAssociation(groupAssociation);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userCount").value(0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating the competency profile
      final String companyId = user.getCompanyId();
      CompetencyProfile competenyProfile = createCompetencyProfile(companyId);
      
      // assigning to the admin
      user.setCompetencyProfileId(competenyProfile.getId());
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userCount").value(1)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation.users", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyEvaluation.completed").value(false)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = competencyFactory
          .getCompetencyEvaluation(competencyEvaluation.getId());
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation.users", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyEvaluation.completed").value(true)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").param("evaluationId", competencyEvaluation.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation.users", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyEvaluation.completed").value(true)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testActivateEvaluation() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", "")
                  .param("requiredEvaluationList", "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate = formatter.format(LocalDateTime.now().minusDays(2));
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException")
                  .value("End date cannot be before today.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      endDate = formatter.format(LocalDateTime.now().plusDays(2));
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "At least one evaluation type is required.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String requiredEvaluationListStr = EvaluationType.Manager + "," + EvaluationType.Peer + ","
          + EvaluationType.Self;
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("At least one user required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr).param("userIds", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No valid user found to initiate competency evaluation.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      
      final String userId = user.getId();
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr)
                  .param("userIds", userId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "An Evaluation is currently in progress.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAddUserToEvaluation() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", "")
                  .param("requiredEvaluationList", "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate = formatter.format(LocalDateTime.now().plusDays(2));
      String requiredEvaluationListStr = EvaluationType.Manager + "," + EvaluationType.Peer + ","
          + EvaluationType.Self;
      String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      final String userId = user.getId();
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr)
                  .param("userIds", userId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/addUserToEvaluation").param("userIds", "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id's requried."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/addUserToEvaluation").param("userIds", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "No valid user found to add to competency evaluation.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/addUserToEvaluation").param("userIds", userId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "No valid user found to add to competency evaluation.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      final String userId2 = user2.getId();
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/addUserToEvaluation").param("userIds", userId2)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "No valid user found to add to competency evaluation.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user2.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user2);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/addUserToEvaluation").param("userIds", userId2)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testRemoveUserFromEvaluation() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", "")
                  .param("requiredEvaluationList", "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate = formatter.format(LocalDateTime.now().plusDays(2));
      String requiredEvaluationListStr = EvaluationType.Manager + "," + EvaluationType.Peer + ","
          + EvaluationType.Self;
      String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      final String userId = user.getId();
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr)
                  .param("userIds", userId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/removeUserFromEvaluation").param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Cannot remove user at least one user required in evaluation.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      final String userId2 = user2.getId();
      user2.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user2);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/addUserToEvaluation").param("userIds", userId2)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/removeUserFromEvaluation").param("userId", "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id requried."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/removeUserFromEvaluation").param("userId", " ")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id requried."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation.users", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/removeUserFromEvaluation").param("userId", userId2)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation.users", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testEndEvaluation() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      
      // reset company evaluation status
      final String companyId = "1";
      resetCompanyEvaluationStatus(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/endEvaluation").contentType(MediaType.TEXT_PLAIN).session(
                  session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Evalaution not in progress."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", "")
                  .param("requiredEvaluationList", "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("End date required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
      String endDate = formatter.format(LocalDateTime.now().plusDays(2));
      String requiredEvaluationListStr = EvaluationType.Manager + "," + EvaluationType.Peer + ","
          + EvaluationType.Self;
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      final String userId = user.getId();
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/activateEvaluation").param("endDate", endDate)
                  .param("requiredEvaluationList", requiredEvaluationListStr)
                  .param("userIds", userId).contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating a feedback user
      FeedbackUser fbUser = new FeedbackUser();
      fbUser.setCompanyId(companyId);
      fbUser.setFeatureType(FeatureType.Competency);
      dbSetup.addUpdate(fbUser);
      
      CompetencyEvaluationRequest request = new CompetencyEvaluationRequest();
      request.setCompanyId(companyId);
      request.setFeedbackUserId(fbUser.getId());
      dbSetup.addUpdate(request);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/endEvaluation").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat(dbSetup.getAll(FeedbackUser.class).size(), is(0));
      assertThat(dbSetup.getAll(CompetencyEvaluationRequest.class).size(), is(0));
      
      List<CompetencyEvaluation> all = dbSetup.getAll(CompetencyEvaluation.class);
      CompetencyEvaluation competencyEvaluation = all.get(0);
      assertThat(competencyEvaluation.isCompleted(), is(true));
      assertThat(competencyEvaluation.getEndedOn(), is(not(nullValue())));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetUsers() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      
      final String companyId = "1";
      createCompetencyEvaluation(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getUsers").contentType(MediaType.TEXT_PLAIN).session(
                  session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.user", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      resetCompanyEvaluationStatus(companyId);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUsers").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.user", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      user.setCompetencyProfileId(competencyProfile.getId());
      UserGroup userGroup = dbSetup.getUserGroup("Executive");
      user.addGroupAssociation(new GroupAssociation(userGroup, true));
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUsers").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.user", hasSize(1)))
          .andExpect(jsonPath("$.success.groups", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyProfile", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetEvaluationList() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      
      final String companyId = "1";
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getEvaluationList").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getEvaluationList").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetViewByCompetencyProfiles() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getViewByCompetencyProfiles").contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getViewByCompetencyProfiles").contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetCompetencyProfileDetails() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyProfileDetails")
                  .param("competencyProfileId", "abc").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Competency profile not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyProfileDetails")
                  .param("competencyProfileId", competencyProfile.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetCompetencyProfileEvaluationUsers() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyProfileEvaluationUsers")
                  .param("competencyProfileId", "abc").param("competencyEvaluationId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Competency profile not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyProfileEvaluationUsers")
                  .param("competencyProfileId", competencyProfileId)
                  .param("competencyEvaluationId", "abc").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException")
                  .value("Competency evaluation not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyProfileEvaluationUsers")
                  .param("competencyProfileId", competencyProfileId)
                  .param("competencyEvaluationId", competencyEvaluation.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      user2.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user2);
      competencyEvaluation = createCompetencyEvaluation(companyId);
      addScores(user, competencyProfile, competencyEvaluation);
      addScores(user2, competencyProfile, competencyEvaluation);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyProfileEvaluationUsers")
                  .param("competencyProfileId", competencyProfileId)
                  .param("competencyEvaluationId", competencyEvaluation.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
      System.exit(0);
    }
  }
  
  @Test
  public void testGetViewByMembers() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      
      final String companyId = "1";
      final CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      user.addRole(RoleType.CompetencyAdmin);
      UserGroup userGroup = dbSetup.getAll(UserGroup.class).get(0);
      final GroupAssociation groupAssociation = new GroupAssociation(userGroup.getName());
      groupAssociation.setGroupId(userGroup.getId());
      user.addGroupAssociation(groupAssociation);
      user.setCompetencyProfileId(competencyProfile.getId());
      dbSetup.addUpdate(user);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getViewByMembers")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getViewByMembers")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          //.andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetUserCompetencies() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencies")
                  .param("userIds", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String userId = user.getId();
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencies")
                  .param("userIds", userId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      User user2 = dbSetup.getUser("dax@surepeople.com");
      String userId2 = user2.getId();
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencies")
                  .param("userIds", userId + "," + userId2)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user2.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user2);
      competencyEvaluation = createCompetencyEvaluation(companyId);
      addScores(user, competencyProfile, competencyEvaluation);
      addScores(user2, competencyProfile, competencyEvaluation);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencies")
                  .param("userIds", userId + "," + userId2)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andExpect(jsonPath("$.success.users", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetUserCompetencyEvaluations() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencyEvaluations")
                  .param("userId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String userId = user.getId();
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencyEvaluations")
                  .param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencyEvaluations")
                  .param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      addScores(user, competencyProfile, competencyEvaluation);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getUserCompetencyEvaluations")
                  .param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
      System.exit(0);
    }
  }
  
  @Test
  public void testGetCompetencyEvaluationPeerRequests() throws Exception {
    try {
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyEvaluationPeerRequests")
                  .param("userId", "abc")
                  .param("competencyEvaluationId", "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyEvaluationPeerRequests")
                  .param("userId", "")
                  .param("competencyEvaluationId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyEvaluationPeerRequests")
                  .param("userId", "abc")
                  .param("competencyEvaluationId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Competency evaluation not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      
      final String userId = user.getId();
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      final String competencyEvaluationId = competencyEvaluation.getId();
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyEvaluationPeerRequests")
                  .param("userId", "abc")
                  .param("competencyEvaluationId", competencyEvaluationId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not part of evaluation."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyEvaluationPeerRequests")
                  .param("userId", userId)
                  .param("competencyEvaluationId", competencyEvaluationId)
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      addScores(user, competencyProfile, competencyEvaluation);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/getCompetencyEvaluationPeerRequests")
                  .param("userId", userId)
                  .param("competencyEvaluationId", competencyEvaluation.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
      System.exit(0);
    }
  }
  
  @Test
  public void testGetManagerResult() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      dbSetup.removeAllTokens();
      
      final String companyId = "1";
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      user.addRole(RoleType.FeedbackUser);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/manager/get/123")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Cannot process request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      List<Token> all = dbSetup.getAll(Token.class);
      assertThat(all, hasSize(0));
      
      final String userId = user.getId();

      competencyEvaluation = createCompetencyEvaluation(companyId);
      UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
      UserCompetencyEvaluation evaluation = userCompetency.getEvaluation(competencyEvaluation
          .getId());
      evaluation.setManager(new UserCompetencyEvaluationScore(getRandomFeedbackUser()));
      competencyFactory.update(userCompetency);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      all = dbSetup.getAll(Token.class);
      assertThat(all, hasSize(0));
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      userCompetency = competencyFactory.getUserCompetency(userId);
      evaluation = userCompetency.getEvaluation(competencyEvaluation
          .getId());
      User user2 = dbSetup.getUser("dax@surepeople.com");
      final FeedbackUser fbUser = new FeedbackUser();
      fbUser.updateFrom(user2);
      evaluation.setManager(new UserCompetencyEvaluationScore(fbUser));
      final FeedbackUser fb1 = getRandomFeedbackUser();
      evaluation.addPeer(new UserCompetencyEvaluationScore(fb1));
      evaluation.updateScore(EvaluationType.Peer, fb1,
          createEvaluationDetails(fb1, competencyProfile));
      final FeedbackUser fb2 = getRandomFeedbackUser();
      evaluation.addPeer(new UserCompetencyEvaluationScore(fb2));
      evaluation.updateScore(EvaluationType.Peer, fb2,
          createEvaluationDetails(fb2, competencyProfile));

      competencyFactory.update(userCompetency);
      Thread.sleep(2000);
      competencyFactory.endEvaluation(competencyEvaluation);

      all = dbSetup.getAll(Token.class);
      assertThat(all, hasSize(1));
      Token token = all.get(0);

      User user3 = dbSetup.getUser("pradeep2@surepeople.com");
      authenticationHelper.doAuthenticateWithoutPassword(session2, user3);
      
      result = mockMvc
          .perform(
              post("/competency/evaluation/manager/get/" + token.getTokenId())
              .contentType(MediaType.TEXT_PLAIN)
              .session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      result = this.mockMvc
          .perform(
              post("/competency/evaluation/manager/get/" + token.getTokenId())
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation").exists())
          .andExpect(jsonPath("$.success.users", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      result = this.mockMvc
          .perform(
              post("/competency/evaluation/manager/peerDetails")
              .param("competencyEvaluationId", competencyEvaluation.getId())
              .param("userId", userId)
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfile").exists())
          .andExpect(jsonPath("$.success.competencyEvaluation", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  } 
  
  @Test
  public void testUserDelete() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      dbSetup.removeAllTokens();
      
      final String companyId = "1";
      CompetencyProfile competencyProfile2 = createCompetencyProfile(companyId, 1);
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfile2.getId());
      user.addRole(RoleType.FeedbackUser);
      dbSetup.addUpdate(user);
      
      // adding the user to delete
      User delUser = new User();
      delUser.setFirstName("FName");
      delUser.setLastName("LName");
      delUser.setEmail("deluser@yopmail.com");
      delUser.setAnalysis(user.getAnalysis());
      delUser.setUserStatus(UserStatus.VALID);
      delUser.setCompanyId(user.getCompanyId());
      delUser.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(delUser);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      Thread.sleep(5000);
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      Thread.sleep(5000);
      
      user.setCompetencyProfileId(null);
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/manager/get")
              .param("tokenId", "123")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Cannot process request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = createCompetencyEvaluation(companyId);      

//      final String userId = delUser.getId();
//      addEvaluationData(competencyProfile, competencyEvaluation, userId);
      
      // adding the evaluation details for the del user
      addEvaluationData(competencyProfile, competencyEvaluation, delUser.getId());
      
      // ending the evaluation
      competencyFactory.endEvaluation(competencyEvaluation);
      
      SpectrumCompetencyProfileEvaluationResults scpr = competencyFactory
          .getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
      assertThat(scpr.getEvaluationResults(), hasSize(3));
      
      result = this.mockMvc
          .perform(
              post("/admin/member/delete")
              .param("memberEmail", delUser.getEmail())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      scpr = competencyFactory
          .getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
      assertThat(scpr.getEvaluationResults(), hasSize(0));
      
      List<CompetencyEvaluation> all = dbSetup.getAll(CompetencyEvaluation.class);
      assertThat(all, hasSize(2));
      
      result = this.mockMvc
          .perform(
              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
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
  public void testProcessCompetnecyEvaluations() throws Exception {
    try {
      startMail();
      
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetency");
      dbSetup.removeAllTokens();
      
      scheduler.processCompetnecyEvaluations();
      
      Thread.sleep(2000);
      
      MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, equalTo(0));
      
      final String companyId = "1";
      CompetencyProfile competencyProfile2 = createCompetencyProfile(companyId, 1);
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      User user = dbSetup.getUser();
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfile2.getId());
      user.addRole(RoleType.FeedbackUser);
      dbSetup.addUpdate(user);
      
      // adding the user to delete
      User delUser = new User();
      delUser.setFirstName("FName");
      delUser.setLastName("LName");
      delUser.setEmail("deluser@yopmail.com");
      delUser.setAnalysis(user.getAnalysis());
      delUser.setUserStatus(UserStatus.VALID);
      delUser.setCompanyId(user.getCompanyId());
      delUser.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(delUser);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      Thread.sleep(5000);
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      competencyFactory.endEvaluation(competencyEvaluation);
      
      Thread.sleep(5000);
      
      user.setCompetencyProfileId(null);
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/competency/evaluation/manager/get")
              .param("tokenId", "123")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Cannot process request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      competencyEvaluation = createCompetencyEvaluation(companyId);
      List<EvaluationType> requiredEvaluationList = competencyEvaluation.getRequiredEvaluationList();
      requiredEvaluationList.add(EvaluationType.Self);
      requiredEvaluationList.add(EvaluationType.Peer);
      competencyFactory.update(competencyEvaluation);

//      final String userId = delUser.getId();
//      addEvaluationData(competencyProfile, competencyEvaluation, userId);
      
      Thread.sleep(2000);
      testSmtp.reset();
      
      scheduler.processCompetnecyEvaluations();
      
      Thread.sleep(2000);
      receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, equalTo(1));
      
      // adding the evaluation details for the del user
      addEvaluationData(competencyProfile, competencyEvaluation, delUser.getId());

      scheduler.processCompetnecyEvaluations();
      
      Thread.sleep(2000);
      receivedMessages = testSmtp.getReceivedMessages();
      assertThat(receivedMessages.length, equalTo(3));
      
//      // ending the evaluation
//      competencyFactory.endEvaluation(competencyEvaluation);
//      
//      SpectrumCompetencyProfileEvaluationResults scpr = competencyFactory
//          .getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
//      assertThat(scpr.getEvaluationResults(), hasSize(3));
//      
//      result = this.mockMvc
//          .perform(
//              post("/admin/member/delete")
//              .param("memberEmail", delUser.getEmail())
//                  .contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(status().is2xxSuccessful())
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//      
//      scpr = competencyFactory
//          .getSpectrumCompetencyProfileEvaluationResult(competencyProfileId);
//      assertThat(scpr.getEvaluationResults(), hasSize(0));
//      
//      List<CompetencyEvaluation> all = dbSetup.getAll(CompetencyEvaluation.class);
//      assertThat(all, hasSize(2));
//      
//      result = this.mockMvc
//          .perform(
//              post("/competency/manager/get").contentType(MediaType.TEXT_PLAIN).session(session))
//          .andExpect(status().is2xxSuccessful())
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      stopMail();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  private User addEvaluationData(CompetencyProfile competencyProfile,
      CompetencyEvaluation competencyEvaluation, final String userId) throws InterruptedException {
    UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
    final String competencyEvaluationId = competencyEvaluation
        .getId();
    final UserCompetencyEvaluation evaluation = userCompetency.getEvaluation(competencyEvaluationId);
    competencyFactory.update(userCompetency);
    User user2 = dbSetup.getUser("dax@surepeople.com");
    final FeedbackUser fbUser = new FeedbackUser();
    fbUser.updateFrom(user2);
    dbSetup.addUpdate(fbUser);
    evaluation.setManager(new UserCompetencyEvaluationScore(fbUser));
    CompetencyEvaluationRequest competencyEvaluationRequest = new CompetencyEvaluationRequest(fbUser);
    competencyEvaluationRequest.add(competencyProfile.getId(), userId, EvaluationType.Manager);
    competencyFactory.update(competencyEvaluationRequest);
    final FeedbackUser fb1 = getRandomFeedbackUser();
    evaluation.addPeer(new UserCompetencyEvaluationScore(fb1));
    evaluation.updateScore(EvaluationType.Peer, fb1,
        createEvaluationDetails(fb1, competencyProfile));
    final FeedbackUser fb2 = getRandomFeedbackUser();
    evaluation.addPeer(new UserCompetencyEvaluationScore(fb2));
    evaluation.updateScore(EvaluationType.Peer, fb2,
        createEvaluationDetails(fb2, competencyProfile));
    competencyFactory.update(userCompetency);
    Thread.sleep(2000);
    User user = userFactory.getUser(userId);
    UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
    ParentTodoTaskRequests parentTodoTaskRequest = userTodoRequests.getParentTodoTaskRequest(competencyEvaluationId);
    if (competencyEvaluation.isSupported(EvaluationType.Self)) {
      CompetencyTodoRequest todoReq = (CompetencyTodoRequest)parentTodoTaskRequest.getRequest(competencyEvaluationId);
      todoReq.setTaskType(TaskType.CompetencyEvaluationSelf);
    } else {
      parentTodoTaskRequest.remove(competencyEvaluationId);
    }
    
    if (parentTodoTaskRequest.isEmpty()) {
      userTodoRequests.removeByParent(competencyEvaluationId);
    }
    todoFactory.updateUserTodoRequests(userTodoRequests);
    
    return user2;
  }   
  
  private void addScores(User user, CompetencyProfile competencyProfile,
      CompetencyEvaluation competencyEvaluation) {
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
    UserCompetencyEvaluation evaluation = userCompetencyEvaluation
        .getEvaluation(competencyEvaluation.getId());
    FeedbackUser fbUser = getRandomFeedbackUser();
    evaluation.setManager((UserCompetencyEvaluationScore) addRandomEvaluation(fbUser,
        new UserCompetencyEvaluationScore(getRandomFeedbackUser()), competencyProfile));
    fbUser = getRandomFeedbackUser();
    evaluation.setSelf(addRandomEvaluation(fbUser, new BaseCompetencyEvaluationScore(),
        competencyProfile));
    final FeedbackUser fb1 = getRandomFeedbackUser();
    evaluation.addPeer(new UserCompetencyEvaluationScore(fb1));
    evaluation.updateScore(EvaluationType.Peer, fb1,
        createEvaluationDetails(fb1, competencyProfile));
    final FeedbackUser fb2 = getRandomFeedbackUser();
    evaluation.addPeer(new UserCompetencyEvaluationScore(fb2));
    evaluation.updateScore(EvaluationType.Peer, fb2,
        createEvaluationDetails(fb2, competencyProfile));
    competencyFactory.update(userCompetencyEvaluation);
  }
  
  private BaseCompetencyEvaluationScore addRandomEvaluation(FeedbackUser fbUser,
      BaseCompetencyEvaluationScore score, CompetencyProfile competencyProfile) {
    UserCompetencyEvaluationDetails evaluationDetails = createEvaluationDetails(fbUser,
        competencyProfile);
    score.setCompetencyEvaluationScoreDetailsId(evaluationDetails.getId());
    score.setScore(evaluationDetails.getTotalScore());
    score.setScoreArray(evaluationDetails.getScoreArray());
    return score;
  }
  
  private UserCompetencyEvaluationDetails createEvaluationDetails(FeedbackUser fbUser,
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
    evaluationDetails.setReviewerId(fbUser.getId());
    return evaluationDetails;
  }
  
  private FeedbackUser getRandomFeedbackUser() {
    FeedbackUser fbUser = new FeedbackUser();
    int nextInt = rand.nextInt();
    fbUser.setFirstName("First" + nextInt);
    fbUser.setLastName("Last" + nextInt);
    fbUser.setEmail("test" + nextInt + "@yopmail.com");
    dbSetup.addUpdate(fbUser);
    return fbUser;
  }
  
  private void resetCompanyEvaluationStatus(String companyId) {
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setEvaluationInProgress(false);
    company.setCompetencyEvaluationId(null);
    companyFactory.updateCompanyDao(company);
  }
  
  /**
   * Create a competency evaluation.
   * 
   * @param companyId
   *          - company id
   * @return the competency evaluation
   */
  private CompetencyEvaluation createCompetencyEvaluation(final String companyId) {
    List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
    requiredEvaluationList.add(EvaluationType.Manager);
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
    final List<User> addedMembers = new ArrayList<User>();
    memberList.stream().filter(m -> m.getCompetencyProfileId() != null)
        .forEach(m -> {
            competencyFactory.addUserToCompetencyEvaluation(m, competencyEvaluation);
            addedMembers.add(m);
          });
    
    competencyFactory.update(competencyEvaluation);
    
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setCompetencyEvaluationId(competencyEvaluation.getId());
    company.setEvaluationInProgress(true);
    companyFactory.updateCompanyDao(company);
    helper.notifyStartEvaluation(company, competencyEvaluation, addedMembers);
    return competencyEvaluation;
  }
  
  private CompetencyProfile createCompetencyProfile(String companyId) {
    return createCompetencyProfile(companyId, 0);
  }
  
  /**
   * Create a competency profile.
   * 
   * @param companyId
   *          - company id
   * @return the competency profile
   */
  private CompetencyProfile createCompetencyProfile(String companyId, int idx) {
    CompetencyProfile competencyProfile = new CompetencyProfile();
    competencyProfile.setActive(true);
    competencyProfile.setName("Some competency name"+idx);
    competencyProfile.setDescription("Some competency description.");
    competencyProfile.setCompanyId(companyId);
    RatingConfiguration ratingConfiguration = new RatingConfiguration(
        RatingConfigurationType.Numeric, 5);
    competencyProfile.setRatingConfiguration(ratingConfiguration);
    SPGoal competency = new SPGoal();
    competency.setName("Some competency name."+idx);
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
    DSAction action = new DSAction();
    actionList.add(action);
    actionCategory.setActionList(actionList);
    action.setTitle("Strategy name");
    List<String> mandatoryArticles = new ArrayList<String>();
    mandatoryArticles.add("1");
    competency.setMandatoryArticles(mandatoryArticles);
    dbSetup.addUpdate(competency);
    List<String> competencyIdList = new ArrayList<String>();
    competencyIdList.add(competency.getId());
    competencyProfile.setCompetencyIdList(competencyIdList);
    dbSetup.addUpdate(competencyProfile);
    return competencyProfile;
  }
  
}