package com.sp.web.controller.library;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.Constants;
import com.sp.web.controller.tracking.TrackingControllerHelper;
import com.sp.web.dao.article.ArticleDao;
import com.sp.web.dao.goal.UserArticleProgressDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dto.library.TrainingSpotLightDTO;
import com.sp.web.form.TrackingForm;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.User;
import com.sp.web.model.article.Article;
import com.sp.web.model.article.ArticleStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserGoal;
import com.sp.web.model.library.TrainingLibraryHomeArticle;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.library.ArticlesFactory;
import com.sp.web.repository.library.TrainingLibraryArticleRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TrainingLibraryControllerTest extends SPTestLoggedInBase {
  
  @Value("classpath:assessment-initial-sections.json")
  private Resource sampleHtml;
  
  @Autowired
  private TrainingLibraryArticleRepository trainingLibraryArticleRepository;
  
  @Autowired
  ArticlesFactory articlesFactory;
  
  @Autowired
  TrackingControllerHelper trackingHelper;
  
  // @Before
  // public void setup1() throws JsonParseException, JsonMappingException, IOException {
  // dbSetup.removeAllUsers();
  // dbSetup.removeAllCompanies();
  // dbSetup.createUsers();
  // dbSetup.createCompanies();
  //
  // User user = new User();
  // user.setEmail("pradeep1@surepeople.com");
  // user.setPassword("password");
  // authenticationHelper.doAuthenticate(session2, user);
  // }
  
  @Test
  public void getAllTopics() throws Exception {
    
    /* check for no goals present */
    dbSetup.removeSpGoals();
    articlesFactory.load();
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getAllTopics").param("topic", "Theme")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.topics").exists())
        .andExpect(jsonPath("$.success.topics.*", hasSize(0))).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    dbSetup.createGoals();
    articlesFactory.load();
    articlesFactory.resetAllCache();
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getAllTopics").param("topic", "Theme")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.topics").exists())
        .andExpect(jsonPath("$.success.topics.*", hasSize(9))).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getAllTopics").param("topic", "Author")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.topics").exists()).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getAllTopics").param("topic", "Source")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.topics").exists()).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
  }
  
  @Test
  public void getAllArticlesTest() throws Exception {
    dbSetup.removeSpGoals();
    dbSetup.createGoals();
    dbSetup.removeAllUserGoals();
    dbSetup.removeArticles();
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.articles").isArray()).andReturn();
    
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    dbSetup.createArticles();
    articlesFactory.resetAllCache();
    
    final List<Article> articleList = dbSetup.getAll(Article.class);
    
    User authUser = dbSetup.getUser("pradeep1@surepeople.com");
    UserGoalDao userGoal = addUserGoals(authUser);
    userGoal.getArticleProgressMap().values()
        .forEach(ap -> ap.setArticleStatus(ArticleStatus.COMPLETED));
    authenticationHelper.doAuthenticateWithoutPassword(session, authUser);
    
    /* All the articles */
    final int totalArticleCount = articleList.size();
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.totalArticlesCount").value(totalArticleCount))
        .andExpect(jsonPath("$.success.articles", hasSize(totalArticleCount))).andReturn();
    
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    /* All the articles for category theme and value* Effective Management */
    final String goalName = "1";
    int timeManagementArticleCount = (int) articleList.stream()
        .filter(a -> a.getGoals().contains(goalName)).count();
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .param("topicCategory", "Themes").param("topicValue", goalName)
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.totalArticlesCount").value(timeManagementArticleCount))
        .andExpect(jsonPath("$.success.articles").isArray()).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    /* All the articles for category author and value* Eric Ravenscraft */
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .param("topicCategory", "Author").param("topicValue", "Chetan Bhagat")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.totalArticlesCount").value(8))
        .andExpect(jsonPath("$.success.articles").isArray()).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    /* All the articles for category sources and value* Lifehacker */
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .param("topicCategory", "Source").param("topicValue", "Fast Company")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.totalArticlesCount").value(3))
        .andExpect(jsonPath("$.success.articles").isArray()).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    /* No article the articles */
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .param("topicCategory", "Source").param("topicValue", "NoArticle")
                .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.totalArticlesCount").value(0))
        .andExpect(jsonPath("$.success.articles", hasSize(0))).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults").param("topicCategory", "")
                .param("topicValue", " ").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.totalArticlesCount").value(17))
        .andExpect(jsonPath("$.success.articles", hasSize(17))).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
    result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/trainingLibrary/getResults")
                .param("topicValue", "Fast Company").contentType(MediaType.TEXT_PLAIN)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.totalArticlesCount").value(3))
        .andExpect(jsonPath("$.success.articles", hasSize(3))).andReturn();
    log.debug("The MVC Result is :" + result.getResponse().getContentAsString());
    
  }
  
  @Test
  public void getDuplicateArticlesTest() throws Exception {
    /* All the articles */
    List<Article> allArticles = trainingLibraryArticleRepository.getAllArticles();
    allArticles.stream().forEach(ar -> {
      Set<String> goals = ar.getGoals();
      if (goals.size() > 1) {
        System.out.println(goals);
      }
    });
  }
  
  @Test
  public void testArticleRecommendation() {
    try {
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAllUserGoals();
      dbSetup.removeAll("bookMarkTracking");
      
      // incorrect article id
      
      MvcResult result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "123")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Article :123 not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid article id and recommending the article
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.recommendationCount").value(1)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // recommending again
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User has already recommended the article.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/1").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.articleRecommendedByUser").value(true))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("pradeep1@surepeople.com");
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      // valid article id and recommending the article with a different user
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.recommendationCount").value(2)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // in-valid article id and un-recommending the article
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "123")
                  .param("doReccomend", "false").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Article :123 not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/1").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.articleRecommendedByUser").value(true))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid article id and un-recommending the article
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "1")
                  .param("doReccomend", "false").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.recommendationCount").value(1)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid article id and un-recommending the article again
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/recommendArticle").param("articleId", "1")
                  .param("doReccomend", "false").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User has not recommended the article.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/1").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.articleRecommendedByUser").value(false))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetArticleDetails() {
    try {
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAllUserGoals();
      dbSetup.removeAll("bookMarkTracking");
      User user = dbSetup.getUser("pradeep1@surepeople.com");
      
      UserGoalDao userGoal = addUserGoals(user);
      
      UserArticleProgressDao userArticleProgress = userGoal.getArticleProgressMap().values()
          .stream().findFirst().get();
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      final String articleId = userArticleProgress.getArticleId();
      MvcResult result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article").exists())
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("NOT_STARTED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true))
          .andExpect(jsonPath("$.success.article.articleBookmarked").value(false)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // bookmark the article
      BookMarkTracking bookMarkTracking = new BookMarkTracking();
      bookMarkTracking.setAccessTime(LocalDateTime.now());
      bookMarkTracking.setArticleId(articleId);
      bookMarkTracking.setBookMarked(true);
      bookMarkTracking.setUserId(user.getId());
      dbSetup.addUpdate(bookMarkTracking);
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("NOT_STARTED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true))
          .andExpect(jsonPath("$.success.article.articleBookmarked").value(true)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      articlesFactory.addComment(articleId, user, "Test Comment.");
      articlesFactory.addComment(articleId, user, "Test Comment.");
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("NOT_STARTED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true))
          .andExpect(jsonPath("$.success.article.articleBookmarked").value(true))
          .andExpect(jsonPath("$.success.article.comments", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      articlesFactory.removeComment(user, articleId, 1);
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("NOT_STARTED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true))
          .andExpect(jsonPath("$.success.article.articleBookmarked").value(true))
          .andExpect(jsonPath("$.success.article.comments", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      articlesFactory.updateComment(user, articleId, 0, "new comment");
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("NOT_STARTED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true))
          .andExpect(jsonPath("$.success.article.articleBookmarked").value(true))
          .andExpect(jsonPath("$.success.article.comments", hasSize(1)))
          .andExpect(jsonPath("$.success.article.comments[0].comment").value("new comment"))
          .andExpect(jsonPath("$.success.relatedArticles").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // checking for can be added to user goals
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/3").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.relatedArticles").exists())
          .andExpect(jsonPath("$.success.article.canAddToUserGoals").value(true)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testUpdateArticleToProfile() {
    try {
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      dbSetup.removeAllUserGoals();
      dbSetup.removeAll("bookMarkTracking");
      dbSetup.removeAll("activityTracking");
      User user = dbSetup.getUser("pradeep1@surepeople.com");
      
      UserGoalDao userGoal = addUserGoals(user);
      
      UserArticleProgressDao userArticleProgress = userGoal.getArticleProgressMap().entrySet()
          .stream().findFirst().get().getValue();
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      final String articleId = userArticleProgress.getArticleId();
      assertThat(userArticleProgress.getArticleStatus(), is(ArticleStatus.NOT_STARTED));
      
      MvcResult result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article").exists())
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("NOT_STARTED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true))
          .andExpect(jsonPath("$.success.article.articleBookmarked").value(false)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // mark the status
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/udpateArticleToProfile").param("articleId", articleId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              get("/trainingLibrary/getArticleDetail/" + articleId).contentType(
                  MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.article.userArticleStatus").value("COMPLETED"))
          .andExpect(jsonPath("$.success.article.articleForUser").value(true)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // exhausting all the articles for the user goals
      List<UserGoal> all = dbSetup.getAll(UserGoal.class);
      user = dbSetup.getUser("pradeep1@surepeople.com");
      final String userGoalId = user.getUserGoalId();
      Optional<UserGoal> findFirst = all.stream().filter(ug -> ug.getId().equals(userGoalId))
          .findFirst();
      assertTrue(findFirst.isPresent());
      UserGoal userGoal2 = findFirst.get();
      String activeGoalId = userGoal2.getActiveGoalId();
      List<ArticleDao> artilces = articlesFactory.getArtilces("Themes", "1", "en_US");
      artilces.forEach(ap -> {
        try {
          this.mockMvc
              .perform(
                  post("/trainingLibrary/udpateArticleToProfile").param("articleId", ap.getId())
                      .contentType(MediaType.TEXT_PLAIN).session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
              .andExpect(jsonPath("$.success").exists())
              .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
      });
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/udpateArticleToProfile").param("articleId", articleId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/dashboard/goalsInProgress")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.info("The mvc results :" + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetTrendingArticles() {
    try {
      dbSetup.removeAll("articleTrackingBean");
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      
      User user = dbSetup.getUser("pradeep1@surepeople.com");
      UserGoalDao userGoal = addUserGoals(user);
      final TrackingForm tForm = new TrackingForm();
      final Object[] params = new Object[1];
      params[0] = tForm;
      userGoal.getArticleProgressMap().values().forEach(uap -> {
        tForm.setArticleId(uap.getArticleId());
        trackingHelper.trackInformation(user, params);
      });
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/trainingLibrary/trendingAndTopRated").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trendingArticles").exists())
          .andExpect(
              jsonPath("$.success.trendingArticles[0].articleLinkLabel").value(
                  "Education is best 11")).andExpect(jsonPath("$.success.topArticles").exists())
          // .andExpect(jsonPath("$.success.topArticles[0].articleLinkLabel").value("Education is best 2"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      tForm.setArticleId("2");
      trackingHelper.trackInformation(user, params);
      trackingHelper.trackInformation(user, params);
      trackingHelper.trackInformation(user, params);
      
      String email = user.getEmail();
      articlesFactory.reccomendArticle("2", email, true);
      articlesFactory.reccomendArticle("2", "dax@surepeople.com", true);
      articlesFactory.reccomendArticle("2", "admin@admin.com", true);
      articlesFactory.reccomendArticle("2", "pradeep2@surepeople.com", true);
      articlesFactory.reccomendArticle("2", "pradeep3@surepeople.com", true);
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/trendingAndTopRated").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trendingArticles").exists())
          .andExpect(
              jsonPath("$.success.trendingArticles[0].articleLinkLabel").value(
                  "Education is best 11")).andExpect(jsonPath("$.success.topArticles").exists())
          // .andExpect(jsonPath("$.success.topArticles[0].articleLinkLabel").value("Education is best"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // clear the cache and see if the list is reversed
      articlesFactory.resetAllCache();
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/trendingAndTopRated").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trendingArticles").exists())
          .andExpect(
              jsonPath("$.success.trendingArticles[0].articleLinkLabel").value(
                  "Team Lead is an art"))
          .andExpect(jsonPath("$.success.topArticles").exists())
          .andExpect(
              jsonPath("$.success.topArticles[0].articleLinkLabel").value("Team Lead is an art"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetTrainingSpotLight() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/trainingLibrary/trainingSpotlight").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trainingSpotLight").exists()).andReturn();
      
      String prevResult = result.getResponse().getContentAsString();
      log.debug("The MVC Response : " + prevResult);
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/trainingSpotlight").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trainingSpotLight").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertThat(result.getResponse().getContentAsString(), is(not(prevResult)));
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/trainingSpotlight").param("goalId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trainingSpotLight").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Map<String, List<TrainingSpotLightDTO>> trainingSpotLightArticles = articlesFactory
          .getTrainingSpotLightArticles(Constants.DEFAULT_LOCALE);
      String goalId = trainingSpotLightArticles.keySet().stream().findAny().get();
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/trainingSpotlight").param("goalId", goalId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.trainingSpotLight").exists())
          // .andExpect(jsonPath("$.success.trainingSpotLight.goal.id").value(goalId))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetDuplicateArticlesReport() {
    try {
      List<Article> all = dbSetup.getAll(Article.class);
      List<SPGoal> allGoals = dbSetup.getAll(SPGoal.class);
      final Map<String, SPGoal> gaolsMap = allGoals.stream().collect(
          Collectors.toMap(SPGoal::getId, g -> g));
      Map<String, List<Article>> collect = all.stream().collect(
          Collectors.groupingBy(Article::getArticleLinkLabel));
      System.out.println(">>>>>>>>>>>>> Printing ...");
      for (String key : collect.keySet()) {
        List<Article> list = collect.get(key);
        if (list.size() > 1) {
          System.out.print(list.get(0).getArticleLinkLabel() + ":");
          Set<String> goals = new HashSet<String>();
          for (Article a : list) {
            System.out.print(a.getId() + ":");
            goals.addAll(a.getGoals());
          }
          System.out.print(": Goals :"
              + goals.stream().map(gaolsMap::get).map(SPGoal::getName)
                  .collect(Collectors.joining(", ")));
          System.out.println();
        }
      }
      System.out.println(">>>>>>>>>>>>> Printing End...");
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testHomepageArticles() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeArticles();
      dbSetup.createArticles();
      dbSetup.removeAll("trainingLibraryHomeArticle");
      
      List<TrainingLibraryHomeArticle> homeArticles = dbSetup
          .getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(0));
      
      User user = new User();
      user.setEmail("admin@admin.com");
      user.setPassword("admin");
      authenticationHelper.doAuthenticate(session, user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/trainingLibrary/homeArticles").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<Article> articlesList = dbSetup.getAll(Article.class);
      Article article = articlesList.get(0);
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/admin/add/contentArticles")
                  .param("articleId", article.getId()).param("position", "0")
                  .param("companyId", "business").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(1));
      assertThat(homeArticles.get(0).getShortDescription(), isEmptyOrNullString());
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/admin/add/contentArticles")
                  .param("articleId", article.getId()).param("position", "0")
                  .param("companyId", "business")
                  .param("shortDescription", "This is some short description.")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(1));
      assertThat(homeArticles.get(0).getShortDescription(), not(isEmptyOrNullString()));
      
      article = articlesList.get(1);
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/admin/add/contentArticles")
                  .param("articleId", article.getId()).param("position", "1")
                  .param("companyId", "business").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(2));
      
      for (int i = 0; i < 4; i++) {
        article = articlesList.get(6 - i);
        result = this.mockMvc
            .perform(
                post("/trainingLibrary/admin/add/contentArticles")
                    .param("articleId", article.getId()).param("position", i + "")
                    .param("companyId", "business").contentType(MediaType.TEXT_PLAIN)
                    .session(session))
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
        log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      }
      homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(4));
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/homeArticles").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      String companyId = "1";
      for (int i = 0; i < 4; i++) {
        article = articlesList.get(6 - i);
        result = this.mockMvc
            .perform(
                post("/trainingLibrary/admin/add/contentArticles")
                    .param("articleId", article.getId()).param("position", i + "")
                    .param("companyId", companyId).contentType(MediaType.TEXT_PLAIN)
                    .session(session))
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
        log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      }
      homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(8));
      
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/homeArticles").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testShareArticles() {
    try {
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      testSmtp.start();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/trainingLibrary/shareArticle").param("articleId", "1")
                  .param("toList", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
