package com.sp.web.controller.feed;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.CommentForm;
import com.sp.web.form.UserMarkerForm;
import com.sp.web.form.feed.SPMessagePostForm;
import com.sp.web.model.Comment;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.feed.SPMessagePostRepository;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.feed.SPMessagePostFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessagePostControllerTest extends SPTestLoggedInBase {

  @Autowired
  SPMessagePostRepository messagePostRepository;
  
  @Autowired
  NewsFeedFactory newsFeedFactory;
  
  @Autowired
  SPMessagePostFactory messagePostFactory;
  
  @Test
  public void testGetALL() {
    try {
      
      dbSetup.removeAll("sPMessagePost");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePostListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      createMessagePost(user, "This is a test message post.");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePostListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      SPMessagePost messagePost2 = createMessagePost(user, "This is a published message.");
      messagePost2.setPublished(true);
      List<String> companyIds = new ArrayList<String>();
      companyIds.add(user.getCompanyId());
      messagePost2.setCompanyIds(companyIds);
      messagePostRepository.save(messagePost2);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePostListing", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private SPMessagePost createMessagePost(User user, String text) {
    SPMessagePost messagePost = SPMessagePost.newInstance();
    messagePost.setMessage(Comment.newCommment(user, text));
    messagePost.setUpdatedOn(LocalDateTime.now());
    messagePost.setUpdatedBy(new UserMarkerDTO(user));
    messagePostRepository.save(messagePost);
    return messagePost;
  }

  @Test
  public void testGet() {
    try {
      
      dbSetup.removeAll("sPMessagePost");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/get")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/get")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message post not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      SPMessagePost createMessagePost = createMessagePost(user, "This is a test message post.");

      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/get")
              .param("id", createMessagePost.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePost").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testCreate() {
    try {
      
      dbSetup.removeAll("sPMessagePost");
      
      final SPMessagePostForm form = new SPMessagePostForm();
      
      ObjectMapper om = new ObjectMapper();
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      final UserMarkerForm user = new UserMarkerForm();
      form.setOnBehalfUser(user);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user.setName("SurePeople");
      user.setTitle("Learning and Development");
      user.setSmallProfileImage("http://someurl");

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm commentForm = new CommentForm();
      commentForm.setComment("A new message post created.");
      form.setMessage(commentForm);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePost").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testUpdate() {
    try {
      
      dbSetup.removeAll("sPMessagePost");
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      final SPMessagePostForm form = new SPMessagePostForm();
      
      ObjectMapper om = new ObjectMapper();
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
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
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final UserMarkerForm user = new UserMarkerForm();
      form.setOnBehalfUser(user);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user.setName("SurePeople");
      user.setTitle("Learning and Development");
      user.setSmallProfileImage("http://someurl");
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm commentForm = new CommentForm();
      commentForm.setComment("The message got updated.");
      form.setMessage(commentForm);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message post not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User commentUser = dbSetup.getUser("admin@admin.com");
      SPMessagePost messagePost = createMessagePost(commentUser, "Some comment.");
      final String messageId = messagePost.getId();
      form.setId(messageId);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      messagePost = messagePostRepository.findById(messageId);
      messagePost.setPublished(true);
      List<String> companyIds = new ArrayList<String>();
      final String companyId = commentUser.getCompanyId();
      companyIds.add(companyId);
      messagePost.setCompanyIds(companyIds);
      messagePost.setAllMember(true);
      messagePostRepository.save(messagePost);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage dashboardMessage = DashboardMessage.newMessage(companyId, messagePost);
      newsFeedFactory.createDashbaordMessage(dashboardMessage);
      
      NewsFeedHelper companyNewsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      companyNewsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, dashboardMessage);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDelete() {
    try {
      
      dbSetup.removeAll("sPMessagePost");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/deletePost")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/deletePost")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message post not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      SPMessagePost createMessagePost = createMessagePost(user, "This is a test message post.");

      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/deletePost")
              .param("id", createMessagePost.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<SPMessagePost> findAll = messagePostRepository.findAll();
      assertThat(findAll, is(empty()));
      
      createMessagePost = createMessagePost(user, "This is a test message post.");
      createMessagePost.setPublished(true);
      createMessagePost.setAllMember(true);
      List<String> companyIds = new ArrayList<String>();
      companyIds.add(user.getCompanyId());
      createMessagePost.setCompanyIds(companyIds);
      messagePostRepository.save(createMessagePost);
      messagePostFactory.publish(createMessagePost);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/deletePost")
              .param("id", createMessagePost.getId())
              .param("newsFeedOnly", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      findAll = messagePostRepository.findAll();
      assertThat(findAll, is(not(empty())));
      SPMessagePost spMessagePost = findAll.get(0);
      assertThat(spMessagePost.isPublished(), is(false));

      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/deletePost")
              .param("id", createMessagePost.getId())
              .param("newsFeedOnly", "false")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      findAll = messagePostRepository.findAll();
      assertThat(findAll, is(empty()));
      
      createMessagePost = createMessagePost(user, "This is a test message post.");
      createMessagePost.setPublished(true);
      createMessagePost.setAllMember(true);
      createMessagePost.setCompanyIds(companyIds);
      messagePostRepository.save(createMessagePost);
      messagePostFactory.publish(createMessagePost);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/deletePost")
              .param("id", createMessagePost.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      findAll = messagePostRepository.findAll();
      assertThat(findAll, is(empty()));

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testPublish() {
    try {
      
      dbSetup.removeAll("sPMessagePost");
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      final SPMessagePostForm form = new SPMessagePostForm();
      
      ObjectMapper om = new ObjectMapper();
      String content = om.writeValueAsString(form);
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
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
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company ids required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> companyIds = new ArrayList<String>();
      companyIds.add("abc");
      form.setCompanyIds(companyIds);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User roles required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<RoleType> userRoles = new ArrayList<RoleType>();
      userRoles.add(RoleType.Demo);
      form.setUserRoles(userRoles);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message post not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      SPMessagePost messagePost = createMessagePost(user, "Message post for publish.");
      messagePost.setPublished(true);
      messagePostRepository.save(messagePost);
      form.setId(messagePost.getId());

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message post already published."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      messagePost.setPublished(false);
      messagePostRepository.save(messagePost);

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      companyIds.clear();
      companyIds.add(user.getCompanyId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "No valid company found to publish message to."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      userRoles.clear();
      userRoles.add(RoleType.AccountAdministrator);
      userRoles.add(RoleType.Hiring);
      userRoles.add(RoleType.GroupLead);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePost").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      messagePost = createMessagePost(user, "All company message post.");
      form.setAllCompany(true);
      form.setId(messagePost.getId());

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/publish")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messagePost").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/message/post/postActivityDetails")
              .param("id", form.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          //.andExpect(jsonPath("$.success.messagePost").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
}
