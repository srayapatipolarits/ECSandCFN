package com.sp.web.controller.dashboard;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.CommentForm;
import com.sp.web.form.feed.DashboardMessageForm;
import com.sp.web.model.Comment;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.SPMedia;
import com.sp.web.model.SPMediaType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.feed.ActivityNewsFeed;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.DashboardMessageType;
import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.feed.SPActivityFeed;
import com.sp.web.model.feed.SPMessagePost;
import com.sp.web.model.feed.SPNewsFeed;
import com.sp.web.model.poll.SPMiniPoll;
import com.sp.web.model.poll.SPMiniPollType;
import com.sp.web.model.poll.SPSelectionType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.feed.NewsFeedFactory;
import com.sp.web.service.feed.NewsFeedHelper;
import com.sp.web.service.feed.NewsFeedSupport;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  NewsFeedFactory newsFeedFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  // @Test
  // public void testGetTasks() {
  // try {
  // // remove any previously created users
  // dbSetup.removeAllCompanies();
  //
  // // company not required
  // MvcResult result = this.mockMvc
  // .perform(
  // post("/dashboard/getTasks")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // dbSetup.createCompanies();
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getTasks")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // String companyId = "1";
  // Company company = dbSetup.getCompany(companyId);
  // UserTask userTask = new UserTask(TaskType.WorkspacePulse);
  // userTask.addParam(Constants.PARAM_PULSE_REQUEST_ID, "12345");
  // userTask.addParam(Constants.PARAM_END_DATE, LocalDate.now().minusDays(1).toString());
  // company.addTask(userTask);
  // dbSetup.addUpdate(company);
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getTasks")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // company = dbSetup.getCompany(companyId);
  // userTask = new UserTask(TaskType.WorkspacePulse);
  // userTask.addParam(Constants.PARAM_PULSE_REQUEST_ID, "12345");
  // userTask.addParam(Constants.PARAM_END_DATE, LocalDate.now().plusDays(5).toString());
  //
  // User user = dbSetup.getUser("admin@admin.com");
  // user.getTaskList().add(userTask);
  //
  // dbSetup.addUpdate(user);
  // authenticationHelper.doAuthenticateWithoutPassword(session, user);
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getTasks")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andExpect(jsonPath("$.success.tasks", hasSize(1)))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // // adding an invalid one
  // userTask = new UserTask(TaskType.WorkspacePulse);
  // userTask.addParam(Constants.PARAM_PULSE_REQUEST_ID, "12345");
  // userTask.addParam(Constants.PARAM_END_DATE, LocalDate.now().minusDays(5).toString());
  // user.getTaskList().add(userTask);
  //
  // dbSetup.addUpdate(user);
  // authenticationHelper.doAuthenticateWithoutPassword(session, user);
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getTasks")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andExpect(jsonPath("$.success.tasks", hasSize(1)))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // fail("Error !!!");
  // }
  // }
  //
  // @Test
  // public void testGetNotesAndFeedback() {
  // try {
  //
  // dbSetup.removeAll("personalityPracticeArea");
  // dbSetup.createPersonalityPracticeAreas();
  //
  // dbSetup.removeSpGoals();
  // dbSetup.createGoals();
  //
  // dbSetup.removeAll("sPNote");
  // dbSetup.removeAll("feedbackUser");
  //
  // // company not required
  // MvcResult result = this.mockMvc
  // .perform(
  // post("/dashboard/getNotesAndFeedback")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andExpect(jsonPath("$.success.allNotesFeedback", hasSize(0)))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  //
  // // adding a note
  // String email = "admin@admin.com";
  // User user = dbSetup.getUser(email);
  // UserGoalDao userGoals = addUserGoals(user);
  // assertNotNull(userGoals);
  //
  // SPNote note = new SPNote();
  // note.setCompanyId(user.getCompanyId());
  // note.setContent("ha ha ha ha ha ha.");
  // final LocalDateTime now = LocalDateTime.now();
  // note.setCreatedOn(now.minusDays(5));
  // note.setGoalId("1");
  // note.setDevStrategyId("1");
  // note.setType(SPNoteFeedbackType.NOTE);
  // note.setUserId(user.getId());
  // dbSetup.addUpdate(note);
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getNotesAndFeedback")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andExpect(jsonPath("$.success.allNotesFeedback", hasSize(1)))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // // adding a feedback
  // PracticeFeedback feedback = new PracticeFeedback();
  // feedback.setCompanyId(user.getCompanyId());
  // feedback.setContent("ha ha ha ha ha ha.");
  // feedback.setCreatedOn(now.minusDays(4));
  // feedback.setGoalId("1");
  // feedback.setDevStrategyId("1");
  // feedback.setType(SPNoteFeedbackType.FEEDBACK);
  // feedback.setUserId(user.getId());
  // feedback.setFeedbackUserEmail("dax@surepeople.com");
  // FeedbackUser fbUser = new FeedbackUser();
  // User member = dbSetup.getUser("dax@surepeople.com");
  // fbUser.updateFrom(member);
  // dbSetup.addUpdate(fbUser);
  // feedback.setFeedbackUserId(fbUser.getId());
  // feedback.setFeedbackStatus(RequestStatus.COMPLETED);
  // feedback.setFeedbackResponse("ho ho ho ho ho ho ho");
  // dbSetup.addUpdate(feedback);
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getNotesAndFeedback")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andExpect(jsonPath("$.success.allNotesFeedback", hasSize(2)))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // // adding more notes than the max limit
  // for (int i = 0; i < Constants.DASHBOARD_NOTES_FEEDBACK_LIMIT; i++) {
  // note = new SPNote();
  // note.setCompanyId(user.getCompanyId());
  // note.setContent("ha ha ha ha ha ha. " + i);
  // note.setCreatedOn(now.minusDays(3));
  // note.setGoalId("1");
  // note.setDevStrategyId("1");
  // note.setType(SPNoteFeedbackType.NOTE);
  // note.setUserId(user.getId());
  // dbSetup.addUpdate(note);
  // }
  //
  // note = new SPNote();
  // note.setCompanyId(user.getCompanyId());
  // note.setContent("ha ha ha ha ha ha. " + 123);
  // note.setCreatedOn(now);
  // note.setGoalId("1");
  // note.setDevStrategyId("1");
  // note.setType(SPNoteFeedbackType.NOTE);
  // note.setUserId(user.getId());
  // dbSetup.addUpdate(note);
  //
  // String nowFormatted = MessagesHelper.formatDate(now.toLocalDate(), "MMM dd, yyyy");
  //
  // result = this.mockMvc
  // .perform(
  // post("/dashboard/getNotesAndFeedback")
  // .contentType(MediaType.TEXT_PLAIN).session(session))
  // .andExpect(content().contentType("application/json;charset=UTF-8"))
  // .andExpect(jsonPath("$.success").exists())
  // .andExpect(jsonPath("$.success.Success").value("true"))
  // .andExpect(jsonPath("$.success.allNotesFeedback",
  // hasSize(Constants.DASHBOARD_NOTES_FEEDBACK_LIMIT)))
  // .andExpect(jsonPath("$.success.allNotesFeedback[0].formattedDate").value(nowFormatted))
  // .andReturn();
  // log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  //
  // } catch (Exception e) {
  // e.printStackTrace();
  // fail("Error !!!");
  // }
  // }
  
  @Test
  public void testGetNewsFeed() {
    try {
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      newsFeedFactory.resetCache();
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/getNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the a news feed
      User user = dbSetup.getUser("admin@admin.com");
      SPMedia media = new SPMedia();
      media.setName("some name");
      media.setUrl("some url");
      SPNewsFeed newsFeed = new SPNewsFeed("Some updates from SurePeople.", media);
      final String companyId = user.getCompanyId();
      final NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      newsFeedHelper.addNewsFeed(newsFeed);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActivityNewsFeed newsFeed2 = new ActivityNewsFeed(user, "He did something great.",
          new SPMedia("Do something great too.", "some url", SPMediaType.Web));
      
      newsFeedHelper.addNewsFeed(newsFeed2);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage message = DashboardMessage.newMessage(
          Comment.newCommment(user, "An announcement."), user.getCompanyId());
      message.setType(DashboardMessageType.Announcement);
      message.getMessage().setOnBehalfUser(new UserMarkerDTO(companyFactory.getCompany(companyId)));
      newsFeedFactory.updateDashboardMessage(message);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(3))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUserGetNewsFeed() {
    try {
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      newsFeedFactory.resetCache();
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/getUserNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the a news feed
      User user = dbSetup.getUser("admin@admin.com");
      SPMedia media = new SPMedia();
      media.setName("some name");
      media.setUrl("some url");
      SPNewsFeed newsFeed = new SPNewsFeed("Some updates from SurePeople.", media);
      final String companyId = user.getCompanyId();
      final NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      newsFeedHelper.addNewsFeed(newsFeed);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActivityNewsFeed newsFeed2 = new ActivityNewsFeed(user, "He did something great.",
          new SPMedia("Do something great too.", "some url", SPMediaType.Web));
      
      newsFeedHelper.addNewsFeed(newsFeed2);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage message = DashboardMessage.newMessage(
          Comment.newCommment(user, "An announcement."), user.getCompanyId());
      message.setType(DashboardMessageType.Announcement);
      message.getMessage().setOnBehalfUser(new UserMarkerDTO(companyFactory.getCompany(companyId)));
      newsFeedFactory.updateDashboardMessage(message);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage message2 = DashboardMessage.newMessage(
          Comment.newCommment(user, "Hi everybody."), user.getCompanyId());
      newsFeedFactory.updateDashboardMessage(message2);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message2);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User anotherUser = dbSetup.getUser("dax@surepeople.com");
      DashboardMessage message3 = DashboardMessage.newMessage(
          Comment.newCommment(anotherUser, "Dax is awesome."), anotherUser.getCompanyId());
      message3.addTaggedMember(user);
      newsFeedFactory.updateDashboardMessage(message3);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message3);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserNewsFeed").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUserProfileFeed() {
    try {
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      newsFeedFactory.resetCache();
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/getUserProfileFeed")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User user = dbSetup.getUser("dax@surepeople.com");
      final String userId = user.getId();
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserProfileFeed")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").doesNotExist())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the a news feed 
      SPMedia media = new SPMedia();
      media.setName("some name");
      media.setUrl("some url");
      SPNewsFeed newsFeed = new SPNewsFeed("Some updates from SurePeople.", media);
      final String companyId = user.getCompanyId();
      final NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      newsFeedHelper.addNewsFeed(newsFeed);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserProfileFeed")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.Analysis").exists())
          .andExpect(jsonPath("$.success.newsFeed").doesNotExist())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Comment newCommment = Comment.newCommment(user, "This is super.");
      DashboardMessage message = DashboardMessage.newMessage(newCommment, companyId);
      newsFeedFactory.createDashbaordMessage(message);
      
      newsFeedHelper.addNewsFeed(new NewsFeed(NewsFeedType.DashboardMessage, message));
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getUserProfileFeed")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.Analysis").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User adminUser = dbSetup.getUser();
      newCommment = Comment.newCommment(user, "This is private message.");
      message = DashboardMessage.newMessage(newCommment, companyId);
      message.setAllCompany(false);
      List<String> memberIds = new ArrayList<String>();
      memberIds.add(adminUser.getId());
      message.setMemberIds(memberIds);
      newsFeedFactory.createDashbaordMessage(message);
      newsFeedHelper.addNewsFeed(new NewsFeed(NewsFeedType.DashboardMessage, message));

      result = this.mockMvc
          .perform(
              post("/dashboard/getUserProfileFeed")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.member").exists())
          .andExpect(jsonPath("$.success.Analysis").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }    
  
  @Test
  public void testGetAnnouncements() {
    try {
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      newsFeedFactory.resetCache();
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/getAnnouncements").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding the a news feed
      User user = dbSetup.getUser("admin@admin.com");
      SPMedia media = new SPMedia();
      media.setName("some name");
      media.setUrl("some url");
      SPNewsFeed newsFeed = new SPNewsFeed("Some updates from SurePeople.", media);
      final String companyId = user.getCompanyId();
      final NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      newsFeedHelper.addNewsFeed(newsFeed);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getAnnouncements").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      ActivityNewsFeed newsFeed2 = new ActivityNewsFeed(user, "He did something great.",
          new SPMedia("Do something great too.", "some url", SPMediaType.Web));
      
      newsFeedHelper.addNewsFeed(newsFeed2);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getAnnouncements").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage message = DashboardMessage.newMessage(
          Comment.newCommment(user, "An announcement."), user.getCompanyId());
      message.setType(DashboardMessageType.Announcement);
      message.getMessage().setOnBehalfUser(new UserMarkerDTO(companyFactory.getCompany(companyId)));
      newsFeedFactory.updateDashboardMessage(message);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getAnnouncements").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage message2 = DashboardMessage.newMessage(
          Comment.newCommment(user, "Hi everybody."), user.getCompanyId());
      newsFeedFactory.updateDashboardMessage(message2);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message2);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getAnnouncements").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User anotherUser = dbSetup.getUser("dax@surepeople.com");
      DashboardMessage message3 = DashboardMessage.newMessage(
          Comment.newCommment(anotherUser, "Dax is awesome."), anotherUser.getCompanyId());
      message3.addTaggedMember(user);
      newsFeedFactory.updateDashboardMessage(message3);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message3);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/getAnnouncements").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.newsFeed").exists())
          .andExpect(jsonPath("$.success.newsFeed", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetDashboardMessageDetails() {
    try {
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      DashboardMessage message = createDashboardMessage(user);
      final String messageId = message.getId();
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message.setAllCompany(false);
      message.setMemberIds(new ArrayList<String>());
      newsFeedFactory.updateDashboardMessage(message);
      
      user.removeRole(RoleType.SuperAdministrator);
      dbSetup.addUpdate(user);
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized access."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> memberIds = new ArrayList<String>();
      memberIds.add(user.getId());
      message.setMemberIds(memberIds);
      newsFeedFactory.updateDashboardMessage(message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message.addComment(Comment.newCommment(user, "Comment 1 added."));
      message.addComment(Comment.newCommment(user, "Comment 2 added."));
      newsFeedFactory.updateDashboardMessage(message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", messageId)
                  .param("commentsOnly", "true").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message = newsFeedFactory.getDashboardMessage(messageId);
      Comment comment = message.getComment(0);
      Comment newComment = Comment.newCommment(user, "Comment 1 sub comment.");
      Map<String, UserMarkerDTO> likedByMembers = new HashMap<String, UserMarkerDTO>();
      likedByMembers.put(user.getId(), new UserMarkerDTO(user));
      newComment.setLikedByMembers(likedByMembers);
      comment.addComment(newComment);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/details").param("messageId", messageId)
                  .param("commentsOnly", "true").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private DashboardMessage createDashboardMessage(User user) {
    DashboardMessage message = DashboardMessage.newMessage(
        Comment.newCommment(user, "Teting get details."), user.getCompanyId());
    newsFeedFactory.createDashbaordMessage(message);
    return message;
  }
  
  @Test
  public void testNewDashboardMessage() {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      DashboardMessageForm form = new DashboardMessageForm();
      ObjectMapper om = new ObjectMapper();
      
      String content = om.writeValueAsString(form);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      form.setComment(comment);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Content or content reference not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      comment.setComment("Some comment added.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Member list not found to share message.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setAllMembers(true);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      List<String> taggedMemberIds = new ArrayList<String>();
      taggedMemberIds.add(user.getId());
      form.setTaggedMemberIds(taggedMemberIds);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.removeAllGroups();
      dbSetup.addGroups();
      
      form.setAllMembers(false);
      List<String> groupIds = new ArrayList<String>();
      groupIds.add("1");
      form.setGroupIds(groupIds);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      user.removeRole(RoleType.AccountAdministrator);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      UserGroup userGroup = dbSetup.getUserGroup("Executive");
      userGroup.addMember("dax@surepeople.com");
      dbSetup.addUpdate(userGroup);
      
      form.setSendEmail(true);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      form.setAllMembers(true);
      form.setGroupIds(null);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      testSmtp.stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateDashboardMessage() {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      DashboardMessageForm form = new DashboardMessageForm();
      ObjectMapper om = new ObjectMapper();
      
      String content = om.writeValueAsString(form);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setMessageId(" ");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setMessageId("abc");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      form.setComment(comment);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Content or content reference not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String updatedCommentText = "Updated the comment.";
      comment.setComment(updatedCommentText);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      DashboardMessage message = createDashboardMessage(user);
      final String messageId = message.getId();
      form.setMessageId(messageId);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("News feed not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToCompanyNewsFeed(message, user.getCompanyId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId))
          .andExpect(jsonPath("$.success.message.message.text").value(updatedCommentText))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> deletedTaggedMemberIds = new ArrayList<String>();
      deletedTaggedMemberIds.add("abc");
      form.setDeletedTaggedMemberIds(deletedTaggedMemberIds);
      
      addTaggedMember(message, user);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId))
          .andExpect(jsonPath("$.success.message.message.text").value(updatedCommentText))
          .andExpect(jsonPath("$.success.message.taggedMembers", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addTaggedMember(message, user);
      
      deletedTaggedMemberIds.clear();
      deletedTaggedMemberIds.add(user.getId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId))
          .andExpect(jsonPath("$.success.message.message.text").value(updatedCommentText))
          .andExpect(jsonPath("$.success.message.taggedMembers", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addTaggedMember(message, user);
      user = dbSetup.getUser("dax@surepeople.com");
      List<String> taggedMemberIds = new ArrayList<String>();
      taggedMemberIds.add(user.getId());
      form.setTaggedMemberIds(taggedMemberIds);
      
      addToCompanyNewsFeed(
          DashboardMessage.newMessage(Comment.newCommment(user, "Message 1."), user.getCompanyId()),
          user.getCompanyId());
      addToCompanyNewsFeed(
          DashboardMessage.newMessage(Comment.newCommment(user, "Message 2."), user.getCompanyId()),
          user.getCompanyId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/update").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId))
          .andExpect(jsonPath("$.success.message.message.text").value(updatedCommentText))
          .andExpect(jsonPath("$.success.message.taggedMembers", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(user.getCompanyId());
      NewsFeed newsFeed = newsFeedHelper.getNewsFeeds().get(0);
      assertThat(newsFeed.getFeedRefId(), is(messageId));
      
      Thread.sleep(2000);
      testSmtp.stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddCommentDashboardMessage() {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      DashboardMessageForm form = new DashboardMessageForm();
      ObjectMapper om = new ObjectMapper();
      
      String content = om.writeValueAsString(form);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setMessageId(" ");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setMessageId("abc");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      form.setComment(comment);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Content or content reference not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final String updatedCommentText = "new comment added.";
      comment.setComment(updatedCommentText);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      DashboardMessage message = createDashboardMessage(user);
      final String messageId = message.getId();
      form.setMessageId(messageId);
      form.setCid(-1);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("News feed not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToCompanyNewsFeed(message, user.getCompanyId());
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      String updatedCommentText2 = "From a diferent user.";
      comment.setComment(updatedCommentText2);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a sub comment
      comment.setCid(2);
      comment.setComment("Adding a sub comment.");
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/addComment").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.message").exists())
          .andExpect(jsonPath("$.success.message.id").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(5000);
      testSmtp.stop();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testLikeDashboardMessage() {
    try {
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", " ")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      DashboardMessage message = createDashboardMessage(user);
      final String messageId = message.getId();
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("News feed not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToCompanyNewsFeed(message, user.getCompanyId());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId))
          .andExpect(jsonPath("$.success.likeCount").value(0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId))
          .andExpect(jsonPath("$.success.likeCount").value(1)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message = newsFeedFactory.getDashboardMessage(messageId);
      Comment newCommment = Comment.newCommment(user, "New comment.");
      message.addComment(newCommment);
      final int cid = newCommment.getCid();
      Comment subComment = Comment.newCommment(user, "Sub comment.");
      newCommment.addComment(subComment);
      final int childCid = subComment.getCid();
      newsFeedFactory.updateDashboardMessage(message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId).param("cid", cid + "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId))
          .andExpect(jsonPath("$.success.cid").value(cid))
          .andExpect(jsonPath("$.success.likeCount").value(1)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId).param("cid", cid + "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId))
          .andExpect(jsonPath("$.success.cid").value(cid))
          .andExpect(jsonPath("$.success.likeCount").value(0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId).param("cid", cid + "1")
                  .param("childCid", childCid + "1").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId).param("cid", cid + "")
                  .param("childCid", childCid + "2").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId).param("cid", cid + "")
                  .param("childCid", childCid + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId))
          .andExpect(jsonPath("$.success.cid").value(cid))
          .andExpect(jsonPath("$.success.likeCount").value(1)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/like").param("messageId", messageId).param("cid", cid + "")
                  .param("childCid", childCid + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId))
          .andExpect(jsonPath("$.success.cid").value(cid))
          .andExpect(jsonPath("$.success.likeCount").value(0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDeleteDashboardMessage() {
    try {
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/delete").param("messageId", " ")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/delete").param("messageId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      DashboardMessage message = createDashboardMessage(user);
      final String messageId = message.getId();
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/delete").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("News feed not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      addToCompanyNewsFeed(message, user.getCompanyId());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/delete").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage dashboardMessage = newsFeedFactory.getDashboardMessage(messageId);
      assertThat(dashboardMessage, is(nullValue()));
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/delete").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testDeleteCommentDashboardMessage() {
    try {
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", " ")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Message not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      DashboardMessage message = createDashboardMessage(user);
      final String messageId = message.getId();
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .param("cid", "0").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message = newsFeedFactory.getDashboardMessage(messageId);
      Comment newCommment = Comment.newCommment(user, "New comment.");
      Comment subComment = Comment.newCommment(user, "Sub comment");
      newCommment.addComment(subComment);
      message.addComment(newCommment);
      newsFeedFactory.updateDashboardMessage(message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .param("cid", newCommment.getCid() + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message = newsFeedFactory.getDashboardMessage(messageId);
      assertThat(message.getComments().size(), is(0));
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      
      newCommment = Comment.newCommment(user, "New comment 2.");
      subComment = Comment.newCommment(user, "Sub comment 2");
      Comment subComment2 = Comment.newCommment(user2, "Sub comment 3");
      newCommment.addComment(subComment);
      newCommment.addComment(subComment2);
      message.addComment(newCommment);
      newsFeedFactory.updateDashboardMessage(message);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .param("cid", newCommment.getCid() + "")
                  .param("childCid", subComment.getCid() + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .param("cid", newCommment.getCid() + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Unauthorized request."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .param("cid", newCommment.getCid() + "")
                  .param("childCid", subComment2.getCid() + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message = newsFeedFactory.getDashboardMessage(messageId);
      assertThat(message.getComments().size(), is(1));
      assertThat(message.getComments().get(0).getChildComments().size(), is(1));
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/deleteComment").param("messageId", messageId)
                  .param("cid", newCommment.getCid() + "")
                  .param("childCid", subComment.getCid() + "").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.messageId").value(messageId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      message = newsFeedFactory.getDashboardMessage(messageId);
      assertThat(message.getComments().size(), is(1));
      assertThat(message.getComments().get(0).getChildComments().size(), is(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private void addTaggedMember(DashboardMessage message, User user) {
    message = newsFeedFactory.getDashboardMessage(message.getId());
    List<UserMarkerDTO> taggedMembersToAdd = new ArrayList<UserMarkerDTO>();
    taggedMembersToAdd.add(new UserMarkerDTO(user));
    message.addTaggedMembers(taggedMembersToAdd);
    newsFeedFactory.updateDashboardMessage(message);
  }
  
  private void addToCompanyNewsFeed(DashboardMessage message, String companyId) {
    NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
    newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);
  }
  
  @Test
  public void testNewDashboardMessageMiniPoll() {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      DashboardMessageForm form = new DashboardMessageForm();
      ObjectMapper om = new ObjectMapper();
      
      String content = om.writeValueAsString(form);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      form.setComment(comment);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Content or content reference not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      comment.setComment("Some comment added.");
      
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
      comment.setMiniPoll(spMiniPoll);
      
      form.setAllMembers(true);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      spMiniPoll = new SPMiniPoll();
      dateTime = LocalDateTime.now();
      dateTime.plusDays(14);
      spMiniPoll.setEndDate(dateTime);
      spMiniPoll.setInstructionStr("Choose 1 color");
      options = new ArrayList<String>();
      options.add("1");
      options.add("2");
      options.add("3");
      options.add("4");
      spMiniPoll.setOptions(options);
      spMiniPoll.setQuestion("Which is your best color?");
      spMiniPoll.setSelectionType(SPSelectionType.SingleSelect);
      spMiniPoll.setType(SPMiniPollType.Scale);
      comment.setMiniPoll(spMiniPoll);
      
      form.setAllMembers(true);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      testSmtp.stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAnswerMiniPolls() {
    try {
      testSmtp.start();
      
      dbSetup.removeAll("companyNewsFeed");
      dbSetup.removeAll("dashboardMessage");
      
      DashboardMessageForm form = new DashboardMessageForm();
      ObjectMapper om = new ObjectMapper();
      
      String content = om.writeValueAsString(form);
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Comment is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CommentForm comment = new CommentForm();
      form.setComment(comment);
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Content or content reference not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      comment.setComment("Some comment added.");
      
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
      comment.setMiniPoll(spMiniPoll);
      
      form.setAllMembers(true);
      
      content = om.writeValueAsString(form);
      result = this.mockMvc
          .perform(
              post("/dashboard/message/new").content(content)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      DashboardMessage db = dbSetup.getMongoTemplate().findOne(
          Query.query(Criteria.where("type").is(DashboardMessageType.MiniPolls)),
          DashboardMessage.class);
      
      result = this.mockMvc
          .perform(
              post("/dashboard/message/answerMiniPoll").param("messageId", db.getId())
                  .param("selection", "1").content(content).contentType(MediaType.APPLICATION_JSON)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      testSmtp.stop();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetArticle() {
    try {
      
      User user = dbSetup.getUser();
      goalsFactory.addGoalsForUser(user);
      authenticationHelper.updateUser(session, user);
      
      
      MvcResult result = this.mockMvc
          .perform(
              post("/dashboard/learning/getArticle")
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
  
  @Test
  public void testSystemAdminRemoveAllQuotes() {
    try {
      dbSetup.removeAll("dashboardMessage");
      dbSetup.removeAll("companyNewsFeed");
      
      User user = dbSetup.getUser();
      final String companyId = user.getCompanyId();
      NewsFeedHelper newsFeedHelper = newsFeedFactory.getCompanyNewsFeedHelper(companyId);
      SPActivityFeed activityFeed = new SPActivityFeed(SPFeature.Hiring,
          "Something to do with SP Match.");
      DashboardMessage message = DashboardMessage.newMessage(companyId, activityFeed);
      newsFeedFactory.createDashbaordMessage(message);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message);

      DashboardMessage message2 = DashboardMessage.newMessage(companyId, activityFeed);
      newsFeedFactory.createDashbaordMessage(message2);
      newsFeedHelper.createNewsFeed(NewsFeedType.DashboardMessage, message2);
      
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/removeAllQuotes")
              .param("email", "admin@admin.com")
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
  
}
