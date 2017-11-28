package com.sp.web.controller.discussion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.CommentForm;
import com.sp.web.form.discussion.GroupDiscussionForm;
import com.sp.web.model.Comment;
import com.sp.web.model.User;
import com.sp.web.model.discussion.GroupDiscussion;
import com.sp.web.model.discussion.UserGroupDiscussion;
import com.sp.web.model.poll.SPMiniPoll;
import com.sp.web.model.poll.SPMiniPollType;
import com.sp.web.model.poll.SPSelectionType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.discussion.GroupDiscussionFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The test cases for the group discussion services.
 */
public class GroupDiscussionControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  GroupDiscussionFactory gdFactory;
  
  @Test
  public void testGetAll() throws Exception {
    try {
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");

      MvcResult result = this.mockMvc
          .perform(
              post("/gd/getAll")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/gd/getAll")
              .param("isDashboardRequest", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a group discussion
      User user = dbSetup.getUser("admin@admin.com");
      addGroupDiscussion(user);
      
      result = this.mockMvc
          .perform(
              post("/gd/getAll")
              .param("isDashboardRequest", "false")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.gdListing").exists())
          .andExpect(jsonPath("$.success.gdListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/gd/getAll")
              .param("isDashboardRequest", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.gdListing").exists())
          .andExpect(jsonPath("$.success.gdListing", hasSize(1)))
          .andExpect(jsonPath("$.success.gd").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetDetails() throws Exception {
    try {
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");

      MvcResult result = this.mockMvc
          .perform(
              post("/gd/getDetails")
              .param("gdId", "")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/gd/getDetails")
              .param("gdId", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      UserGroupDiscussion userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
      userGroupDiscussion.addGroupDiscussion("abc");
      authenticationHelper.doAuthenticateWithoutPassword(session, user);

      result = this.mockMvc
          .perform(
              post("/gd/getDetails")
              .param("gdId", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      GroupDiscussion addGroupDiscussion = addGroupDiscussion(user);
      final String gdId = addGroupDiscussion.getId();

      result = this.mockMvc
          .perform(
              post("/gd/getDetails")
              .param("gdId", gdId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.gd").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCreate() throws Exception {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");

      ObjectMapper om = new ObjectMapper();
      GroupDiscussionForm gdForm = new GroupDiscussionForm();
      
      String content = om.writeValueAsString(gdForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/gd/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one member required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> memberIds = new ArrayList<String>();
      memberIds.add("abc");
      gdForm.setMemberIds(memberIds);
      
      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      comment.setComment("Some comment.");
      gdForm.setComment(comment);
      
      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.gd").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      memberIds.add(user.getId());

      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(5000);
      
      user = dbSetup.getUser("dax@surepeople.com");
      UserGroupDiscussion userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
      assertTrue(!CollectionUtils.isEmpty(userGroupDiscussion.getGroupDiscussionIds()));
      assertThat(userGroupDiscussion.getUnreadCountMapOrCreate().values().iterator().next(),
          is(equalTo(Integer.valueOf(1))));
      
      
      SPMiniPoll spMiniPoll = new SPMiniPoll();
      LocalDateTime dateTime = LocalDateTime.now();
      dateTime.plusDays(14);
      spMiniPoll.setEndDate(dateTime);
      spMiniPoll.setInstructionStr("Choose 1 color");
      List<String> options = new ArrayList<String>();
      options.add("Red");
      options.add("Blue");
      options.add("Green");
      options.add("Yello");
      spMiniPoll.setOptions(options);
      spMiniPoll.setQuestion("Which is your best color?");
      spMiniPoll.setSelectionType(SPSelectionType.SingleSelect);
      spMiniPoll.setType(SPMiniPollType.MultipleOptions);
      
      
      gdForm.getComment().setMiniPoll(spMiniPoll);
      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/create")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(5000);
      
      
      testSmtp.stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testUpdate() throws Exception {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");

      ObjectMapper om = new ObjectMapper();
      GroupDiscussionForm gdForm = new GroupDiscussionForm();
      
      String content = om.writeValueAsString(gdForm);

      
      /* register the event for admin@admin.com user */
      MvcResult sseResult = mockMvc.perform(get("/sse/push").session(session))
          .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      /* register the event for dax@surepeople.com.com user */
      User user2 = dbSetup.getUser("dax@surepeople.com");
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      
      MvcResult sseResultUser2 = mockMvc.perform(get("/sse/push").session(session2))
          .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
      
      
      MvcResult result = this.mockMvc
          .perform(
              post("/gd/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      gdForm.setGdId("abc");
      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      comment.setComment("A new comment added.");
      gdForm.setComment(comment);
      
      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      GroupDiscussion gd = addGroupDiscussion(user);
      gdForm.setGdId(gd.getId());
      
      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      addUserToGroupDiscussion(gd, user);

      content = om.writeValueAsString(gdForm);
      
      result = this.mockMvc
          .perform(
              post("/gd/update")
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.gd").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      UserGroupDiscussion userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
      assertTrue(!CollectionUtils.isEmpty(userGroupDiscussion.getGroupDiscussionIds()));
      assertThat(userGroupDiscussion.getUnreadCountMapOrCreate().values().iterator().next(),
          is(equalTo(Integer.valueOf(1))));
      
      user = dbSetup.getUser("dax@surepeople.com");
      userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
      assertTrue(!CollectionUtils.isEmpty(userGroupDiscussion.getGroupDiscussionIds()));
      assertThat(userGroupDiscussion.getUnreadCountMapOrCreate().values().iterator().next(),
          is(equalTo(Integer.valueOf(2))));
      
      String contentAsString = sseResult.getResponse().getContentAsString();
      log.debug("The MVC Response : " + contentAsString);
      
      String userEvent2 = sseResultUser2.getResponse().getContentAsString();
      String json = userEvent2.substring(5, userEvent2.length() - 2);
      log.debug("The MVC Response userEvent2: " + json);
      
      Thread.sleep(5000);
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testAddMember() throws Exception {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");

      MvcResult result = this.mockMvc
          .perform(
              post("/gd/addMember")
              .param("gdId", "")
              .param("memberIds", "")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
//      result = this.mockMvc
//          .perform(
//              post("/gd/addMember")
//              .param("gdId", "abc")
//              .param("memberIds", "")
//              .contentType(MediaType.TEXT_PLAIN)
//              .session(session))
//              .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Member not found to add."))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/gd/addMember")
              .param("gdId", "abc")
              .param("memberIds", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      User user = dbSetup.getUser("dax@surepeople.com");
      GroupDiscussion gd = addGroupDiscussion(user);
      final String gdId = gd.getId();
      
      result = this.mockMvc
          .perform(
              post("/gd/addMember")
              .param("gdId", gdId)
              .param("memberIds", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);

      result = this.mockMvc
          .perform(
              post("/gd/addMember")
              .param("gdId", gdId)
              .param("memberIds", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      user = dbSetup.getUser("admin@admin.com");

      result = this.mockMvc
          .perform(
              post("/gd/addMember")
              .param("gdId", gdId)
              .param("memberIds", user.getId())
              .param("newName", "some new name")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      Thread.sleep(2000);
      
      user = dbSetup.getUser("admin@admin.com");
      UserGroupDiscussion userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
      assertTrue(!CollectionUtils.isEmpty(userGroupDiscussion.getGroupDiscussionIds()));
      assertThat(userGroupDiscussion.getUnreadCountMapOrCreate().values().iterator().next(),
          is(equalTo(Integer.valueOf(3))));
      
      user = dbSetup.getUser("dax@surepeople.com");
      userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
      assertTrue(!CollectionUtils.isEmpty(userGroupDiscussion.getGroupDiscussionIds()));
      assertThat(userGroupDiscussion.getUnreadCountMapOrCreate().values().iterator().next(),
          is(equalTo(Integer.valueOf(1))));
      
      result = this.mockMvc
          .perform(
              post("/gd/addMember")
              .param("gdId", gdId)
              .param("nameCleared", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      GroupDiscussion groupDiscussion = gdFactory.getGroupDiscussion(gdId);
      assertTrue(groupDiscussion.isNameOverriden() == false);
      
      Thread.sleep(2000);
      
      testSmtp.stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testLeaveGroupDiscussion() throws Exception {
    try {
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");

      MvcResult result = this.mockMvc
          .perform(
              post("/gd/leaveGroupDiscussion")
              .param("gdId", "")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/gd/leaveGroupDiscussion")
              .param("gdId", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      GroupDiscussion gd = addGroupDiscussion(user);
      gd.setName(null);
      gd.setNameOverriden(false);
      gdFactory.updateGroupDiscussion(gd);
      final String gdId = gd.getId();

      result = this.mockMvc
          .perform(
              post("/gd/leaveGroupDiscussion")
              .param("gdId", gdId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      user = dbSetup.getUser("admin@admin.com");
      addUserToGroupDiscussion(gd, user);

      result = this.mockMvc
          .perform(
              post("/gd/leaveGroupDiscussion")
              .param("gdId", gdId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      gd = gdFactory.getGroupDiscussion(gd.getId());
      assertThat(gd, is(notNullValue()));
      assertThat(gd.getMemberIds(), hasSize(1));
      
      user = dbSetup.getUser("admin@admin.com");
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      result = this.mockMvc
          .perform(
              post("/gd/leaveGroupDiscussion")
              .param("gdId", gdId)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
          
      gd = gdFactory.getGroupDiscussion(gd.getId());
      assertThat(gd, is(nullValue()));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
      
  @Test
  public void testGetUnreadCount() throws Exception {
    try {
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/gd/getUnreadCount")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.unreadCount").value(0))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      GroupDiscussion gd = addGroupDiscussion(user );

      result = this.mockMvc
          .perform(
              post("/gd/getUnreadCount")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.unreadCount").value(1))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      user = dbSetup.getUser("dax@surepeople.com");
      GroupDiscussion gd2 = addGroupDiscussion(user);
      user = dbSetup.getUser("admin@admin.com");
      addUserToGroupDiscussion(gd2, user);

      result = this.mockMvc
          .perform(
              post("/gd/getUnreadCount")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.unreadCount").value(2))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testRenameGroup() throws Exception {
    try {
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/gd/renameGroup")
              .param("gdId", "")
              .param("name", "")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/gd/renameGroup")
              .param("gdId", "abc")
              .param("name", "")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Name is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/gd/renameGroup")
              .param("gdId", "abc")
              .param("name", "abc")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      User user = dbSetup.getUser("admin@admin.com");
      GroupDiscussion gd = addGroupDiscussion(user);
      final String gdId = gd.getId();
      
      final String newName = "abc"; 
          
      result = this.mockMvc
          .perform(
              post("/gd/renameGroup")
              .param("gdId", gdId)
              .param("name", newName)
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      gd = gdFactory.getGroupDiscussion(gdId);
      assertThat(gd.getName(), is(equalTo(newName)));
      assertThat(gd.isNameOverriden(), is(equalTo(true)));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  } 
  
  @Test
  public void testFilterNotification() throws Exception {
    try {
      dbSetup.removeAll("userGroupDiscussion");
      dbSetup.removeAll("groupDiscussion");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/gd/filterNotification")
              .param("gdId", "")
              .param("doFilter", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
            
      result = this.mockMvc
          .perform(
              post("/gd/filterNotification")
              .param("gdId", "abc")
              .param("doFilter", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Group discussion not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      User user = dbSetup.getUser("admin@admin.com");
      GroupDiscussion gd = addGroupDiscussion(user);
      final String gdId = gd.getId();
      
      result = this.mockMvc
          .perform(
              post("/gd/filterNotification")
              .param("gdId", gdId)
              .param("doFilter", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      gd = gdFactory.getGroupDiscussion(gdId);
      assertThat(gd.getFilterNotifications(), hasSize(1));

      // duplicate add to filter check
      result = this.mockMvc
          .perform(
              post("/gd/filterNotification")
              .param("gdId", gdId)
              .param("doFilter", "true")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      gd = gdFactory.getGroupDiscussion(gdId);
      assertThat(gd.getFilterNotifications(), hasSize(1));
      
      result = this.mockMvc
          .perform(
              post("/gd/filterNotification")
              .param("gdId", gdId)
              .param("doFilter", "false")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
              .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      gd = gdFactory.getGroupDiscussion(gdId);
      assertThat(gd.getFilterNotifications(), hasSize(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }   
  
  private void addUserToGroupDiscussion(GroupDiscussion gd, User user) {
    UserGroupDiscussion userGroupDiscussion = gdFactory.getUserGroupDiscussion(user);
    final String gdId = gd.getId();
    userGroupDiscussion.addGroupDiscussion(gdId);
    userGroupDiscussion.updateUnreadCount(gdId);
    gdFactory.updateUserGroupDiscussion(userGroupDiscussion);
    gd.addMember(user.getId());
    gdFactory.updateGroupDiscussion(gd);
  }

  private GroupDiscussion addGroupDiscussion(User user) {
    GroupDiscussion groupDiscussion = new GroupDiscussion();
    groupDiscussion.setName("Some GD");
    groupDiscussion.setCompanyId(user.getCompanyId());
    List<String> memberIds = new ArrayList<String>();
    groupDiscussion.setMemberIds(memberIds);
    groupDiscussion.setNameOverriden(true);
    Comment comment = new Comment();
    comment.setText("Some text");
    groupDiscussion.addComment(comment, user);
    gdFactory.createGroupDiscussion(groupDiscussion);
    addUserToGroupDiscussion(groupDiscussion, user);
    return groupDiscussion;
  }
}