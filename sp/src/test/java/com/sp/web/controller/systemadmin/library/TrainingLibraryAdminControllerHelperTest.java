package com.sp.web.controller.systemadmin.library;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.article.Article;
import com.sp.web.model.library.TrainingLibraryHomeArticle;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TrainingLibraryAdminControllerHelperTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetAll() {
    try {
      dbSetup.removeAll("trainingLibraryHomeArticle");
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/library/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.company", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<Article> articlesList = dbSetup.getAll(Article.class);
      addTrainingHomeArticles(articlesList, "business", 4);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/library/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.company", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addTrainingHomeArticles(articlesList, "1", 8);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/library/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.company", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetArticles() {
    try {
      dbSetup.removeAll("trainingLibraryHomeArticle");
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/library/articles")
              .param("companyId", "business")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.Content", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<Article> articlesList = dbSetup.getAll(Article.class);
      addTrainingHomeArticles(articlesList, "business", 4);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/library/articles")
              .param("companyId", "business")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.Content", hasSize(4)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDelete() {
    try {
      dbSetup.removeAll("trainingLibraryHomeArticle");
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/library/delete")
              .param("companyId", "business")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final List<Article> articlesList = dbSetup.getAll(Article.class);
      addTrainingHomeArticles(articlesList, "business", 4);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/library/delete")
              .param("companyId", "business")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<TrainingLibraryHomeArticle> homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
      assertThat(homeArticles, hasSize(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private void addTrainingHomeArticles(final List<Article> articlesList, String companyId, int sizeValidation)
      throws Exception, UnsupportedEncodingException {
    MvcResult result;
    Article article;
    for (int i = 0; i < 4; i++) {
      article = articlesList.get(6 - i);
      result = this.mockMvc
          .perform(
              post("/trainingLibrary/admin/add/contentArticles")
                  .param("articleId", article.getId())
                  .param("position", i + "")
                  .param("companyId", companyId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    }
    List<TrainingLibraryHomeArticle> homeArticles = dbSetup.getAll(TrainingLibraryHomeArticle.class);
    assertThat(homeArticles, hasSize(sizeValidation));
  }
  
}
