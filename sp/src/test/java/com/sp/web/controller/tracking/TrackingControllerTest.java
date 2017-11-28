package com.sp.web.controller.tracking;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TrackingControllerTest extends SPTestLoggedInBase {
  
  private static final Logger LOG = Logger.getLogger(TrackingControllerTest.class);
  
  @Test
  public void trackInformation() throws Exception {
    dbSetup.removeArticles();
    dbSetup.createArticles();
    
    User user = new User();
    user.setEmail("pradeep1@surepeople.com");
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    MvcResult external = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "2").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "5").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "1").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "10").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "14").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "8").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/tracking/trackArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                .param("articleId", "7").param("trackingType", "ARTICLES"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
    
  }
  
  @Test
  public void testRecentlyVisitedArticle() throws Exception {
    
    User user = new User();
    user.setEmail("pradeep1@surepeople.com");
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    MvcResult external = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/tracking/recentVistedArticles")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("MVC Result is :" + external.getResponse().getContentAsString());
  }
  
  @Test
  public void testBookMarkedArticles() throws Exception {
    try {
      User user = new User();
      user.setEmail("pradeep1@surepeople.com");
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session2, user);
      dbSetup.removeAll("bookMarkTracking");
      
      dbSetup.removeArticles();
      dbSetup.createArticles();
      
      MvcResult external;
      
      for (int i = 1; i < 15; i++) {
        external = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/tracking/bookMarkArticle")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2)
                    .param("articleId", "" + i).param("trackingType", "ARTICLES"))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
        LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
      }
      
      external = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/tracking/bookMarkArticle").param("articleId", "6")
                  .param("trackingType", "ARTICLES").param("isBookMarked", "false")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      LOG.info("Get trackArticles team" + external.getResponse().getContentAsString());
      
      List<BookMarkTracking> bookmarkList = dbSetup.getAll(BookMarkTracking.class);
      Optional<BookMarkTracking> bookMarkTracking = bookmarkList.stream()
          .filter(b -> b.getArticleId().equals("6")).findFirst();
      
      assertTrue("Tracking Bean", !bookMarkTracking.isPresent());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetBookMarked() throws Exception {
    
    dbSetup.removeAll("bookMarkTracking");
    
    testBookMarkedArticles();
    
    List<BookMarkTracking> bookmarkList = dbSetup.getAll(BookMarkTracking.class);
    
    BookMarkTracking bookMarkTracking = bookmarkList.stream()
        .filter(b -> b.getArticleId().equals("2")).findFirst().get();
    bookMarkTracking.setAccessTime(LocalDateTime.now());
    dbSetup.addUpdate(bookMarkTracking);
    
    User user = new User();
    user.setEmail("pradeep1@surepeople.com");
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    MvcResult external = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/tracking/getBookMarkedArticles")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.articles", hasSize(10)))
        // for checking article type
        .andExpect(jsonPath("$.success.articles[0].articleType").value("TEXT"))
        .andExpect(jsonPath("$.success.articles[0].articleLinkLabel").value("Team Lead is an art"))
        .andReturn();
    log.debug("The MVC response is :" + external.getResponse().getContentAsString());
    
    bookMarkTracking.setAccessTime(LocalDateTime.now().minusMonths(2));
    dbSetup.addUpdate(bookMarkTracking);
    
    external = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/tracking/getBookMarkedArticles")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.articles", hasSize(10)))
        .andExpect(jsonPath("$.success.articles[0].articleLinkLabel").value("Education is best 13"))
        .andReturn();
    log.debug("The MVC response is :" + external.getResponse().getContentAsString());
  }
  
  @Test
  public void testGetAllBookMarkArticles() throws Exception {
    dbSetup.removeSpGoals();
    dbSetup.createGoals();
    User user = new User();
    user.setEmail("pradeep1@surepeople.com");
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    MvcResult allArticles = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/tracking/getAllBookMarkedArticles")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get All book mark team" + allArticles.getResponse().getContentAsString());
  }
  
}
