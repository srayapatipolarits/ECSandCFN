package com.sp.web.controller.goal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPNote;
import com.sp.web.model.goal.SPNoteFeedbackType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.scheduler.NewsCredSchedular;
import com.sp.web.service.goals.SPGoalFactoryHelper;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;

public class SPNoteFeedbackControllerTest extends SPTestLoggedInBase {

  private static final Logger LOG = Logger.getLogger(SPNoteFeedbackControllerTest.class);

  @Autowired
  private NewsCredSchedular newsCredSchedular;
  
  @Autowired
  private SPGoalFactoryHelper goalsFactory;
  
  @Autowired
  private SPNoteFeedbackRepository spNoteFeedbackRepository;

  @Test
  public void testGetUserGoals() {
    try {
//      dbSetup.removeSpGoals();
//      dbSetup.createGoals();
//
//      dbSetup.removeArticles();
//      dbSetup.createArticles();

      final String email = "pradeep1@surepeople.com";
      dbSetup.removeAllUserGoals();
      User user = dbSetup.getUser(email);
      AnalysisBean analysis = user.getAnalysis();
      HashMap<RangeType, PersonalityBeanResponse> personality = analysis.getPersonality();
      PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
      personalityBeanResponse.setPersonalityType(PersonalityType.Supporter);
      personalityBeanResponse = personality.get(RangeType.UnderPressure);
      personalityBeanResponse.setPersonalityType(PersonalityType.Supporter);
      dbSetup.addUpdate(user);

      user = new User();
      user.setEmail(email);
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);
      
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userGoal", hasSize(0)))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userGoal", hasSize(0)))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());      

      user = dbSetup.getUser(email);
      UserGoalDao userGoals = addUserGoals(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.userGoal", hasSize(userGoals
                  .getSelectedGoalsProgressList().size())))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());      
      
      /* create feedback users */
      dbSetup.removeAllFeedbackUsers();
      dbSetup.createFeedbackUsers();
      
      addFeedbackGoalsToUser("1", user);
      
      userGoals = goalsFactory.getUserGoal(user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.userGoal", hasSize(userGoals
                  .getSelectedGoalsProgressList().size())))
          .andExpect(
              jsonPath("$.success.userGoal[1].userArticlesProgress", hasSize(userGoals
                  .getSelectedGoalsProgressList().get(1).getArticleList().size())))
          .andReturn();
      LOG.info("Get Growth team" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testRemoveGoals() throws Exception {
    dbSetup.removeSpGoals();
    dbSetup.createGoals();
    dbSetup.removeArticles();
    dbSetup.createArticles();
    
    dbSetup.removeAllUserGoals();

    User user = new User();
    final String email = "pradeep1@surepeople.com";
    user.setEmail(email);
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);

    MvcResult removeGoals = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/removeUserGoals")
              .param("goalId", "abcd")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
        .andReturn();
    LOG.info("Get Growth team" + removeGoals.getResponse().getContentAsString());
    
    user = dbSetup.getUser(email);
    
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    
    removeGoals = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/removeUserGoals")
              .param("goalId", "abcd")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal not found :abcd"))
        .andReturn();
    LOG.info("Get Growth team" + removeGoals.getResponse().getContentAsString());
    
    
    /* create feedback users */
    dbSetup.removeAllFeedbackUsers();
    dbSetup.createFeedbackUsers();
    
    addFeedbackGoalsToUser("1", user);

    UserGoalDao userGoal = goalsFactory.getUserGoal(user);
    assertThat(userGoal.getUserGoalProgress("5"), is(notNullValue()));
    
    removeGoals = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/removeUserGoals")
              .param("goalId", "5")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    LOG.info("Get Growth team" + removeGoals.getResponse().getContentAsString());
    userGoal = goalsFactory.getUserGoal(user);
    assertThat(userGoal.getUserGoalProgress("5"), is(nullValue()));
  }

  @Test
  public void testUpdateUserGoalsSelection() throws Exception {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();

      User user = new User();
      final String email = "pradeep1@surepeople.com";
      user.setEmail(email);
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);

      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                .param("goalId", "abcd, def, mno")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
          .andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                .param("goalId", "abcd")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException")
                .value("Goals selection must be greater than or equal to 3."))
          .andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(email);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                .param("goalId", "abcd, def, mno")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      UserGoalDao userGoal = goalsFactory.getUserGoal(user);
      assertThat(userGoal.getSelectedGoalsProgressList().size(), is(0));
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                .param("goalId", "1, 2, 3")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      userGoal = goalsFactory.getUserGoal(user);
      assertThat(userGoal.getSelectedGoalsProgressList().size(), is(3));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetDashboardGoals() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();

      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();

      User user = new User();
      final String email = "pradeep1@surepeople.com";
      user.setEmail(email);
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/dashboard/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userGoal", hasSize(0)))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());

      user = dbSetup.getUser(email);
      /* create feedback users */
      dbSetup.removeAllFeedbackUsers();
      dbSetup.createFeedbackUsers();
      addFeedbackGoalsToUser("1", user);
      UserGoalDao userGoals = addUserGoals(user);
      userGoals = goalsFactory.getUserGoal(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/dashboard/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.userGoal", hasSize(userGoals
                  .getSelectedGoalsProgressList().size())))
          .andExpect(
              jsonPath("$.success.userGoal[0].userArticlesProgress", hasSize(userGoals
                  .getSelectedGoalsProgressList().get(0).getArticleList().size())))
          .andExpect(
              jsonPath("$.success.userGoal[1].userArticlesProgress", hasSize(0)))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString()); 
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testAddToUserGoals() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();

      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();

      User user = new User();
      final String email = "pradeep1@surepeople.com";
      user.setEmail(email);
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addToUserGoals")
              .param("goalId", "abcd")
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());

      user = dbSetup.getUser(email);
      addUserGoals(user);
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addToUserGoals")
              .param("goalId", "abcd")
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal not found :abcd"))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addToUserGoals")
              .param("goalId", "5")
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString()); 
      UserGoalDao userGoal = goalsFactory.getUserGoal(user);
      UserGoalProgressDao userGoalProgress = userGoal.getUserGoalProgress("5");
      assertThat(userGoalProgress, is(notNullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testAddNote() {
    try {
      /*dbSetup.removeSpGoals();
      dbSetup.createGoals();

      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();

      */
      User user = new User();
      final String email = "test@yopmail.com";
      user.setEmail(email);
      user.setPassword("surepeople");
      authenticationHelper.doAuthenticate(session2, user);
      
      List<Article> all = dbSetup.getAll(Article.class);
      Article article = all.get(0);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addArticleToUserGoals")
              .param("articleId", article.getId())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());

      user = dbSetup.getUser(email);
      addUserGoals(user);
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addArticleToUserGoals")
                  .param("articleId", article.getId() + "123")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Article :15123 not found."))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addArticleToUserGoals")
              .param("articleId", article.getId())
              .contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
//      UserGoalDao userGoal = goalsFactory.getUserGoal(user);
//      UserGoalProgressDao userGoalProgress = userGoal.getUserGoalProgress("5");
//      assertThat(userGoalProgress, is(notNullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
 
  @Test
  public void testCreateFeedback() {
    try {
      
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAll("sPNote");
      dbSetup.removeAll("feedbackUser");
      
      // incorrect goal id
      MvcResult result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "adf")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // correct goal
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPNote> all = dbSetup.getAll(SPNote.class);
      assertThat(all.size(), is(1));
      
      SPNote spNote = all.get(0);
      assertThat(spNote.getType(), is(SPNoteFeedbackType.FEEDBACK));
      assertThat(spNote.getGoalId(), is("1"));
      assertThat(spNote.getDevStrategyId(), nullValue());
      
      // incorrect development strategy
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("devStrategyId", "abcd")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Development strategy not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("devStrategyId", "999")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Development strategy not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // correct development strategy
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("devStrategyId", "0")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(SPNote.class);
      assertThat(all.size(), is(2));
      spNote = all.get(1);
      assertThat(spNote.getType(), is(SPNoteFeedbackType.FEEDBACK));
      assertThat(spNote.getGoalId(), is("1"));
      assertThat(spNote.getDevStrategyId(), is("0"));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateFeedbackStatus() {
    try {
      
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAll("sPNote");
      dbSetup.removeAll("feedbackUser");
      
      // incorrect goal id
      MvcResult result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      List<SPNote> all = spNoteFeedbackRepository.findAllNotesFeedback("1");
      assertThat(all.size(), is(1));
      PracticeFeedback spNote = (PracticeFeedback) all.get(0);
      assertThat(spNote.getFeedbackStatus(), is(RequestStatus.NOT_INITIATED));

      result = this.mockMvc
          .perform(
              post("/goals/feedback/updateStatus")
              .param("feedbackId", spNote.getId() + "1")
              .param("feedbackStatus", RequestStatus.DECLINED.toString())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Feedback not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/goals/feedback/updateStatus")
              .param("feedbackId", spNote.getId())
              .param("feedbackStatus", RequestStatus.DECLINED.toString())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = spNoteFeedbackRepository.findAllNotesFeedback("1");
      assertThat(all.size(), is(1));
      spNote = (PracticeFeedback) all.get(0);
      assertThat(spNote.getFeedbackStatus(), is(RequestStatus.DECLINED));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetAllNotesFeedback() {
    try {
      
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAll("sPNote");
      dbSetup.removeAll("feedbackUser");
      
      // incorrect goal id
      MvcResult result = this.mockMvc
          .perform(
              get("/goals/notesfeedback/all")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notesAndFeedbackList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              get("/goals/notesfeedback/all")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notesAndFeedbackList", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(1000);
      
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("devStrategyId", "0")
              .param("feedbackUserEmail", "dax@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
            
      result = this.mockMvc
          .perform(
              get("/goals/notesfeedback/all")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notesAndFeedbackList", hasSize(1)))
          .andExpect(jsonPath("$.success.notesAndFeedbackList[0].notesFeedbackList", hasSize(2)))
          .andExpect(jsonPath("$.success.notesAndFeedbackList[0].notesFeedbackList[0].devStrategy").exists())
          .andExpect(jsonPath("$.success.notesAndFeedbackList[0].notesFeedbackList[1].devStrategy").doesNotExist())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetAllNotesFeedbackRequests() {
    try {
      
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAll("sPNote");
      dbSetup.removeAll("feedbackUser");
      
      // incorrect goal id
      MvcResult result = this.mockMvc
          .perform(
              get("/goals/feedback/received")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackRequestList", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/goals/feedback/request")
              .param("goalId", "1")
              .param("feedbackUserEmail", "dax@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User user = new User();
      user.setEmail("dax@surepeople.com");
      user.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, user);
      
      result = this.mockMvc
          .perform(
              get("/goals/feedback/received")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackRequestList", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDeleteAllNotesFeedback() {
    try {
  
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAll("sPNote");
      dbSetup.removeAll("feedbackUser");
      
      // incorrect goal id
      MvcResult result = this.mockMvc
          .perform(
              post("/goals/notesFeedback/deleteAll")
              .param("goalId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "No action taken as both delete note and delete feedback flags were false."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/goals/notesFeedback/deleteAll")
              .param("goalId", "abc")
              .param("deleteNote", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Gaol not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // nothing to delete
      result = this.mockMvc
          .perform(
              post("/goals/notesFeedback/deleteAll")
              .param("goalId", "1")
              .param("deleteNote", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      // adding 2 note
      SPNote note = new SPNote();
      note.setId("1");
      note.setGoalId("1");
      note.setContent("Haha");
      note.setType(SPNoteFeedbackType.NOTE);
      dbSetup.addUpdate(note);

      note.setId("2");
      note.setGoalId("1");
      note.setContent("Haha2");
      note.setDevStrategyId("1");
      note.setType(SPNoteFeedbackType.NOTE);
      dbSetup.addUpdate(note);
      
      result = this.mockMvc
          .perform(
              post("/goals/notesFeedback/deleteAll")
              .param("goalId", "1")
              .param("deleteNote", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPNote> all = dbSetup.getAll(SPNote.class);
      assertThat(all.size(), is(0));

      result = this.mockMvc
          .perform(
              post("/goals/notesFeedback/deleteAll")
              .param("goalId", "1")
              .param("deleteFeedback", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding some feedback
      PracticeFeedback feedback = new PracticeFeedback();
      feedback.setId("1");
      feedback.setGoalId("1");
      feedback.setDevStrategyId("1");
      feedback.setType(SPNoteFeedbackType.FEEDBACK);
      dbSetup.addUpdate(feedback);
      
      result = this.mockMvc
          .perform(
              post("/goals/notesFeedback/deleteAll")
              .param("goalId", "1")
              .param("deleteFeedback", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      all = dbSetup.getAll(SPNote.class);
      assertThat(all.size(), is(0));      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
 
}
