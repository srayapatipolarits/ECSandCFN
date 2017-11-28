package com.sp.web.controller.goal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.goal.DevelopmentStrategy;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.scheduler.NewsCredSchedular;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.goals.SPGoalFactoryHelper;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

public class GoalsControllerTest extends SPTestLoggedInBase {
  
  private static final Logger LOG = Logger.getLogger(GoalsControllerTest.class);
  
  @Autowired
  private NewsCredSchedular newsCredSchedular;
  
  @Autowired
  private SPGoalFactoryHelper goalsFactory;
  
  @Autowired
  private SPGoalFactory spGoalsFactory;
  
  @Autowired
  private ArticlesFactory articlesFactory;
  
  @Test
  public void testGetUserGoals() {
    try {
      //
      dbSetup.removeArticles();
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.removeSpGoals();
      dbSetup.createPersonalityPracticeAreas();
      dbSetup.createGoals();
      dbSetup.createArticles();
      
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
      
      List<SPGoal> allGoals = dbSetup.getAll(SPGoal.class);
      List<DevelopmentStrategy> all = dbSetup.getAll(DevelopmentStrategy.class);
      allGoals.stream().forEach(goal -> {
        if (CollectionUtils.isEmpty(goal.getDevelopmentStrategyList())) {
          goal.getDevelopmentStrategyList().addAll(all);
        }
        dbSetup.addUpdate(goal);
      });
      
      user = new User();
      user.setEmail(email);
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);
      
      /* Test for the hightes weight goal coming as first */
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userGoal", hasSize(6)))
          .andExpect(jsonPath("$.success.userGoal[0].goal.name").value("Flexibility")).andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // * Test from cache */
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userGoal", hasSize(6)))
          .andExpect(jsonPath("$.success.userGoal[0].goal.name").value("Flexibility")).andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(email);
      UserGoalDao userGoals = addUserGoals(user);
      
      articlesFactory.resetAllCache();
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getUserGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.userGoal", hasSize(userGoals.getSelectedGoalsProgressList()
                  .size()))).andReturn();
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
              jsonPath("$.success.userGoal", hasSize(userGoals.getSelectedGoalsProgressList()
                  .size())))
          .andExpect(jsonPath("$.success.userGoal[0].goal.name").value("Flexibility"))
          .andExpect(
              jsonPath("$.success.userGoal[1].userArticlesProgress", hasSize(userGoals
                  .getSelectedGoalsProgressList().get(1).getArticleList().size()))).andReturn();
      LOG.info("Get Growth team" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetAllGoals() {
    try {
      
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeArticles();
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.removeSpGoals();
      dbSetup.createPersonalityPracticeAreas();
      dbSetup.createGoals();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();
      
      User user = new User();
      final String email = "pradeep1@surepeople.com";
      user.setEmail(email);
      user.setPassword("password");
      
      User defaultUser = dbSetup.getDefaultUser();
      defaultUser.setAnalysis(null);
      dbSetup.addUpdate(defaultUser);
      authenticationHelper.updateUser(session, defaultUser);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getAllGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User analysis not found."))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      authenticationHelper.doAuthenticate(session2, user);
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getAllGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userAllGoal", hasSize(7)))
          .andExpect(jsonPath("$.success.userSelectedGoal", hasSize(6))).andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      /* create feedback users */
      dbSetup.removeAllFeedbackUsers();
      dbSetup.createFeedbackUsers();
      user = dbSetup.getUser(email);
      addFeedbackGoalsToUser("1", user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/goals/getAllGoals").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userSelectedGoal", hasSize(6))).andReturn();
      LOG.info("Get Growth team" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  // @Test
  public void testRemoveGoals() throws Exception {
    
    dbSetup.removeAllUserGoals();
    
    User user = new User();
    final String email = "pradeep1@surepeople.com";
    user.setEmail(email);
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    MvcResult removeGoals = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/removeUserGoals").param("goalId", "abcd")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
        .andReturn();
    LOG.info("Get Growth team" + removeGoals.getResponse().getContentAsString());
    
    user = dbSetup.getUser(email);
    UserGoalDao addUserGoals = addUserGoals(user);
    
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    
    removeGoals = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/removeUserGoals").param("goalId", "abcd")
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
            MockMvcRequestBuilders.post("/goals/removeUserGoals").param("goalId", "5")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get Growth team" + removeGoals.getResponse().getContentAsString());
    userGoal = goalsFactory.getUserGoal(user);
    assertThat(userGoal.getUserGoalProgress("5"), is(nullValue()));
  }
  
  @Test
  public void testUpdateUserGoalsSelection() throws Exception {
    try {
      
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeArticles();
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.removeSpGoals();
      dbSetup.createPersonalityPracticeAreas();
      dbSetup.createGoals();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();
      
      User user = new User();
      final String email = "pradeep1@surepeople.com";
      user.setEmail(email);
      user.setPassword("password");
      user.addRole(RoleType.User);
      user.addRole(RoleType.Erti);
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                  .param("goalId", "abcd").param("goalId", "def").param("goalId", "mno")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
          .andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(email);
      final UserGoalDao addUserGoals = addUserGoals(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                  .param("goalId", "abcd").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Goal not found :abcd")).andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                  .param("goalId", "2, 1, 3,10,4,9").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      UserGoalDao userGoal = goalsFactory.getUserGoal(user);
      assertThat(userGoal.getSelectedGoalsProgressList().size(), is(6));
      assertThat(userGoal.getSelectedGoalsProgressList().get(0).getGoal().getName(),
          is("Constructive Criticism"));
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/updateUserGoalsSelection")
                  .param("goalId", "4, 10, 3,1,2,9").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
      userGoal = goalsFactory.getUserGoal(user);
      assertThat(userGoal.getSelectedGoalsProgressList().size(), is(6));
      assertThat(userGoal.getSelectedGoalsProgressList().get(0).getGoal().getName(),
          is("Teambuilding"));
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/dashboard/goalsInProgress")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      LOG.info("The mvc results :" + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetDashboardGoals() {
    try {
      
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
              MockMvcRequestBuilders.get("/dashboard/getUserGoals")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.userGoal", hasSize(0))).andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser(email);
      UserGoalDao userGoals = addUserGoals(user);
      /* create feedback users */
      dbSetup.removeAllFeedbackUsers();
      dbSetup.createFeedbackUsers();
      addFeedbackGoalsToUser("1", user);
      userGoals = goalsFactory.getUserGoal(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.get("/dashboard/getUserGoals")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.userGoal", hasSize(userGoals.getSelectedGoalsProgressList()
                  .size())))
          .andExpect(
              jsonPath("$.success.userGoal[0].userArticlesProgress", hasSize(userGoals
                  .getSelectedGoalsProgressList().get(0).getArticleList().size())))
          .andExpect(jsonPath("$.success.userGoal[1].userArticlesProgress", hasSize(0)))
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
              MockMvcRequestBuilders.post("/goals/addToUserGoals").param("goalId", "abcd")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
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
              MockMvcRequestBuilders.post("/goals/addToUserGoals").param("goalId", "abcd")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Goal not found :abcd"))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addToUserGoals").param("goalId", "5")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
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
  public void testAddArticleToUserGoals() {
    try {
      
      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      dbSetup.removeAllUserGoals();
      
      User user = new User();
      final String email = "pradeep1@surepeople.com";
      user.setEmail(email);
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);
      
      List<Article> all = dbSetup.getAll(Article.class);
      Article article = all.get(0);
      
      MvcResult result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addArticleToUserGoals")
                  .param("articleId", article.getId()).contentType(MediaType.TEXT_PLAIN)
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
                  .param("articleId", article.getId() + "123").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Article :15123 not found."))
          .andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/goals/addArticleToUserGoals")
                  .param("articleId", article.getId()).contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      LOG.info("The MVC Response:" + result.getResponse().getContentAsString());
      
      // UserGoalDao userGoal = goalsFactory.getUserGoal(user);
      // UserGoalProgressDao userGoalProgress = userGoal.getUserGoalProgress("5");
      // assertThat(userGoalProgress, is(notNullValue()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetManageDevelopmentStrategy() throws Exception {
    dbSetup.removeArticles();
    dbSetup.removeAll("personalityPracticeArea");
    dbSetup.removeSpGoals();
    dbSetup.createPersonalityPracticeAreas();
    dbSetup.createGoals();
    dbSetup.createArticles();
    User user = new User();
    final String email = "pradeep1@surepeople.com";
    user.setEmail(email);
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/getMds").param("goalId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
        .andReturn();
    LOG.info("The mvc results :" + result.getResponse().getContentAsString());
    
    user = dbSetup.getUser(email);
    final UserGoalDao addUserGoals = addUserGoals(user);
    
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/getMds").param("goalId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.mds", hasSize(2)))
        .andExpect(
            jsonPath("$.success.mds[0].dsDescription").value("Some thing you need to improve in."))
        .andReturn();
    LOG.info("The mvc results :" + result.getResponse().getContentAsString());
    
    /* deactivate the mds */
    SPGoal goal = spGoalsFactory.getGoal("1");
    DevelopmentStrategy developmentStrategy = goal.getDevelopmentStrategyList().get(1);
    developmentStrategy.setActive(false);
    spGoalsFactory.updateGoal(goal);
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/getMds").param("goalId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.mds", hasSize(2)))
        .andExpect(
            jsonPath("$.success.mds[0].dsDescription").value("Some thing you need to improve in."))
        .andReturn();
    LOG.info("The mvc results :" + result.getResponse().getContentAsString());
    
    user = dbSetup.getUser(email);
    user.setUserGoalId(null);
    ;
    final UserGoalDao addUserGoalsa = addUserGoals(user);
    
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/getMds").param("goalId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.mds", hasSize(1)))
        .andExpect(
            jsonPath("$.success.mds[0].dsDescription").value("Some thing you need to improve in."))
        .andReturn();
    LOG.info("The mvc results :" + result.getResponse().getContentAsString());
    
  }
  
  @Test
  public void testUpdateManageDevelopmentStrategy() throws Exception {
    dbSetup.removeArticles();
    dbSetup.removeAll("personalityPracticeArea");
    dbSetup.removeSpGoals();
    dbSetup.createPersonalityPracticeAreas();
    dbSetup.createGoals();
    dbSetup.createArticles();
    User user = new User();
    final String email = "pradeep1@surepeople.com";
    user.setEmail(email);
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/getMds").param("goalId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("User goals not set."))
        .andReturn();
    LOG.info("The mvc results :" + result.getResponse().getContentAsString());
    
    user = dbSetup.getUser(email);
    final UserGoalDao addUserGoals = addUserGoals(user);
    
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/goals/getMds").param("goalId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.mds", hasSize(1)))
        .andExpect(
            jsonPath("$.success.mds[0].dsDescription").value("Some thing you need to improve in."))
        .andReturn();
    LOG.info("The mvc results :" + result.getResponse().getContentAsString());
  }
  
}
