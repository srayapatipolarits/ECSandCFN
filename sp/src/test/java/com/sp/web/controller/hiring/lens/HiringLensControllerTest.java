package com.sp.web.controller.hiring.lens;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.lens.HiringLensFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HiringLensControllerTest extends SPTestLoggedInBase {

  @Autowired
  HiringUserFactory hiringUserFactory;

  @Autowired
  HiringLensFactory lensFactory;
  
  @Test
  public void testGetALL() {
    try {
      
      dbSetup.removeAll("hiringUser");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllHiringArchiveUsers();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/lens/getAllRequests")
              .param("feedbackFor", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLensListing", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      final User user = dbSetup.getUser("admin@admin.com");
      HiringUser hiringUser = addHiringCandidate();
      addFeedbackUser(hiringUser, UserStatus.INVITATION_NOT_SENT);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/getAllRequests")
              .param("feedbackFor", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLensListing", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      FeedbackUser fbUser = addFeedbackUser(hiringUser, UserStatus.VALID);
      final AnalysisBean analysis = user.getAnalysis();
      analysis.setCreatedOn(LocalDateTime.now().minusMonths(2));
      fbUser.setAnalysis(analysis);
      dbSetup.addUpdate(fbUser);

      result = this.mockMvc
          .perform(
              post("/hiring/lens/getAllRequests")
              .param("feedbackFor", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLensListing", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUserFactory.archiveUser(user, hiringUser, false);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/getAllRequests")
              .param("feedbackFor", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLensListing", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGet() {
    try {
      
      dbSetup.removeAll("hiringUser");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllHiringArchiveUsers();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/lens/get")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/get")
              .param("id", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      HiringUser hiringUser = addHiringCandidate();
      FeedbackUser feedbackUser = addFeedbackUser(hiringUser, UserStatus.INVITATION_SENT);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/get")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Lens not completed."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      feedbackUser.setAnalysis(user.getAnalysis());
      feedbackUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(feedbackUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/get")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUserFactory.archiveUser(user, hiringUser, false);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/get")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/hiring/lens/get")
              .param("id", feedbackUser.getId())
              .param("archiveUser", "true")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens").exists())
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
      
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      
      testSmtp.start();
      
      ObjectMapper om = new ObjectMapper();
      
      HiringLensForm form = new HiringLensForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Feedback for required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      form.setFeedbackFor("abc");
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("First name required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setFirstName("First");
      form.setLastName("Last");
      form.setEmail("someone@yopmail.com");

      result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      form.setFeedbackFor(hiringUser.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<FeedbackUser> all = dbSetup.getAll(FeedbackUser.class);
      FeedbackUser feedbackUser = all.get(0);
      assertThat(feedbackUser.getUserStatus(), equalTo(UserStatus.INVITATION_NOT_SENT));

      form.setRequestNow(true);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      all = dbSetup.getAll(FeedbackUser.class);
      feedbackUser = all.get(1);
      assertThat(feedbackUser.getUserStatus(), equalTo(UserStatus.ASSESSMENT_PENDING));
      
      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Phone number required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setPhoneNumber("9818399147");
      form.setReferenceType("ReferenceType");
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/create")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(5000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }
  
  @Test
  public void testSendRequest() {
    try {
      
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllTokens();
      
      testSmtp.start();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/lens/sendRequest")
              .param("id", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringUser hiringUser = addHiringCandidate();
      FeedbackUser feedbackUser = addFeedbackUser(hiringUser, UserStatus.INVITATION_SENT);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/sendRequest")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Invitation already sent."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      feedbackUser.setUserStatus(UserStatus.INVITATION_NOT_SENT);
      dbSetup.addUpdate(feedbackUser);

      result = this.mockMvc
          .perform(
              post("/hiring/lens/sendRequest")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      Thread.sleep(5000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }
  
  @Test
  public void testUpdate() {
    try {
      
      dbSetup.removeAll("hiringUser");
      dbSetup.removeAllHiringArchiveUsers();
      
      ObjectMapper om = new ObjectMapper();
      
      HiringLensForm form = new HiringLensForm();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/lens/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  
      form.setId("abc");
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Feedback for required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      form.setFeedbackFor(hiringUser.getId());
      
      final String firstName = "FirstNew";
      form.setFirstName(firstName);
      form.setLastName("Last");
      form.setEmail("someone@yopmail.com");

      result = this.mockMvc
          .perform(
              post("/hiring/lens/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      FeedbackUser feedbackUser = addFeedbackUser(hiringUser, UserStatus.INVITATION_NOT_SENT);
      form.setId(feedbackUser.getId());
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/update")
              .content(om.writeValueAsString(form))
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringLens.firstName").value(firstName))
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
      
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringArchiveUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllFeedbackUsers();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/lens/delete")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/delete")
              .param("id", "abc")
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringUser = addHiringCandidate();
      FeedbackUser feedbackUser = addFeedbackUser(hiringUser, UserStatus.INVITATION_SENT);

      result = this.mockMvc
          .perform(
              post("/hiring/lens/delete")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      List<FeedbackUser> all = dbSetup.getAll(FeedbackUser.class);
      assertThat(all, hasSize(0));
      
      feedbackUser = addFeedbackUser(hiringUser, UserStatus.INVITATION_NOT_SENT);
      
      User user = dbSetup.getUser();
      HiringLensForm form = new HiringLensForm();
      form.setId(feedbackUser.getId());
      lensFactory.sendRequest(user, form);
      
      Thread.sleep(2000);
      
      result = this.mockMvc
          .perform(
              post("/hiring/lens/delete")
              .param("id", feedbackUser.getId())
              .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      all = dbSetup.getAll(FeedbackUser.class);
      assertThat(all, hasSize(0));
      
      List<Token> all2 = dbSetup.getAll(Token.class);
      assertThat(all2, hasSize(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testSendReminder() {
    try {
      // remove any previously created users
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllFeedbackUsers();
      
      testSmtp.start();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringLensReminder.toString())
              .param(Constants.PARAM_MEMBER_LIST, "dax@einstix.com")
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringUser hiringCandidate = addHiringCandidate();
      FeedbackUser feedbackUser = addFeedbackUser(hiringCandidate, UserStatus.INVITATION_SENT);
      feedbackUser.setTokenUrl("http://tokenUrl/doSomething");
      dbSetup.addUpdate(feedbackUser);
      
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringLensReminder.toString())
              .param(Constants.PARAM_MEMBER_LIST, feedbackUser.getId())
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      Thread.sleep(8000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    } finally {
      testSmtp.stop();
    }
  }
  
  private FeedbackUser addFeedbackUser(HiringUser hiringUser, UserStatus status) {
    FeedbackUser fbUser = new FeedbackUser();
    fbUser.setFirstName("First");
    fbUser.setLastName("Last");
    fbUser.setEmail("someone@yopmail.com");
    fbUser.setTitle("Title");
    fbUser.setFeedbackFor(hiringUser.getId());
    fbUser.setUserStatus(status);
    fbUser.setFeatureType(FeatureType.PrismLensHiring);
    fbUser.setCreatedOn(LocalDate.now());
    fbUser.setCompanyId(hiringUser.getCompanyId());
    dbSetup.addUpdate(fbUser);
    return fbUser;
  }
}
