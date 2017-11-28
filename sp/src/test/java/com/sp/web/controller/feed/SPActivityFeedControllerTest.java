package com.sp.web.controller.feed;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.UserMarkerForm;
import com.sp.web.form.feed.SPActivityFeedForm;
import com.sp.web.model.SPFeature;
import com.sp.web.model.feed.SPActivityFeed;
import com.sp.web.model.feed.SPDashboardPostData;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.feed.SPDashboardPostDataRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

public class SPActivityFeedControllerTest extends SPTestLoggedInBase {

  @Autowired
  SPDashboardPostDataRepository activityFeedsRepository;
  
  @Test
  public void testGetActivityFeeds() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding activity feed
      final SPDashboardPostData activityFeeds = createDashboardPostData();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      List<SPActivityFeed> activityFeedList = new ArrayList<SPActivityFeed>();
      activityFeedList.add(new SPActivityFeed(SPFeature.Prism, "Some text to do with prism."));
      activityFeeds.setActivityFeeds(activityFeedList);
      activityFeedsRepository.save(activityFeeds);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private SPDashboardPostData createDashboardPostData() {
    final SPDashboardPostData activityFeeds = SPDashboardPostData.defaultInstance();
    activityFeedsRepository.save(activityFeeds);
    return activityFeeds;
  }

  @Test
  public void testAddActivityFeed() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      
      SPActivityFeedForm form = new SPActivityFeedForm();
      
      ObjectMapper om = new ObjectMapper();
      String content = om.writeValueAsString(form);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Activity text is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setText("A newly added activity.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Activity feature is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setFeature(SPFeature.Blueprint);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setPosition(100);
      form.setText("Adding at the end.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setPosition(0);
      form.setText("Adding at the Begining.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setPosition(1);
      form.setText("Adding after first.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateActivityFeed() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      
      SPActivityFeedForm form = new SPActivityFeedForm();
      
      ObjectMapper om = new ObjectMapper();
      String content = om.writeValueAsString(form);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Activity text is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setText("A newly added activity.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Activity feature is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setFeature(SPFeature.Blueprint);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setId("abc");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Dashboard post data not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPDashboardPostData sPDashboardPostData = createDashboardPostData();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Position is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setPosition(-1);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Position is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPActivityFeed> activityFeeds = sPDashboardPostData.getActivityFeeds();
      final SPActivityFeed activityFeed = new SPActivityFeed(SPFeature.Competency, "Something about competency.");
      activityFeeds.add(activityFeed);
      activityFeedsRepository.save(sPDashboardPostData);
      
      form.setId(activityFeed.getId());
      form.setPosition(0);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final SPActivityFeed activityFeed2 = new SPActivityFeed(SPFeature.Prism, "Something about prism.");
      sPDashboardPostData = activityFeedsRepository.findById(Constants.DEFAULT_COMPANY_ID);
      activityFeeds = sPDashboardPostData.getActivityFeeds();
      activityFeeds.add(activityFeed2);
      activityFeedsRepository.save(sPDashboardPostData);
      
      form.setId(activityFeed2.getId());
      form.setText("I went to the top.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDeleteActivityFeed() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/delete")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/delete")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Dashboard post data not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPDashboardPostData sPDashboardPostData = createDashboardPostData();

      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/delete")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Feed not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<SPActivityFeed> activityFeeds = sPDashboardPostData.getActivityFeeds();
      final SPActivityFeed activityFeed = new SPActivityFeed(SPFeature.Competency, "Something about competency.");
      activityFeeds.add(activityFeed);
      activityFeedsRepository.save(sPDashboardPostData);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/activityFeeds/delete")
              .param("id", activityFeed.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      sPDashboardPostData = activityFeedsRepository.findById(Constants.DEFAULT_COMPANY_ID);
      assertThat(sPDashboardPostData.getActivityFeeds(), is(empty()));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetAllPostAuthors() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.author").value(nullValue()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPDashboardPostData dashboardPostData = createDashboardPostData();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.author", hasSize(0)))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      addAuthor(dashboardPostData, "SurePeople Inc.", "Learning & Development", "http://someurl");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.author", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private UserMarkerDTO addAuthor(SPDashboardPostData dashboardPostData, String name, String title, String url) {
    final List<UserMarkerDTO> authorList = dashboardPostData.getAuthorList();
    final UserMarkerDTO author = UserMarkerDTO.newInstance();
    author.setFirstName(name);
    author.setSmallProfileImage(url);
    author.setTitle(title);
    authorList.add(author);
    activityFeedsRepository.save(dashboardPostData);
    return author;
  }
  
  @Test
  public void testAddPostAuthors() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      UserMarkerForm form = new UserMarkerForm();
      
      ObjectMapper om = new ObjectMapper();
          
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is4xxClientError())
          .andDo(print())
          .andReturn();
      
      form.setName("SurePeople");
      form.setTitle("Learning & Development");
      form.setSmallProfileImage("http://someurl");
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/add")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andDo(print())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdatePostAuthors() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      UserMarkerForm form = new UserMarkerForm();
      
      ObjectMapper om = new ObjectMapper();
          
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is4xxClientError())
          .andDo(print())
          .andReturn();
      
      form.setName("SurePeople");
      form.setTitle("Learning & Development");
      form.setSmallProfileImage("http://someurl");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id is required."))
          .andReturn();

      form.setId("abc");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Dashboard post data not found."))
          .andReturn();
      
      SPDashboardPostData dashboardPostData = createDashboardPostData();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Author not found."))
          .andReturn();
      
      final List<UserMarkerDTO> authorList = dashboardPostData.getAuthorList();
      UserMarkerDTO newInstance = UserMarkerDTO.newInstance();
      authorList.add(newInstance);
      activityFeedsRepository.save(dashboardPostData);
      
      form.setId(newInstance.getId());
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDeletePostAuthors() {
    try {
      
      dbSetup.removeAll("sPDashboardPostData");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/delete")
              .param("authorId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Author id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/delete")
              .param("authorId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Dashboard post data not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPDashboardPostData dashboardPostData = createDashboardPostData();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/delete")
              .param("authorId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Author not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      UserMarkerDTO author = addAuthor(dashboardPostData, "SurePeople Inc.", "Learning & Development", "http://someurl");
      result = this.mockMvc
          .perform(
              post("/sysAdmin/posts/author/delete")
              .param("authorId", author.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      SPDashboardPostData findById = activityFeedsRepository.findById(Constants.DEFAULT_COMPANY_ID);
      assertThat(findById.getAuthorList(), is(empty()));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
}
