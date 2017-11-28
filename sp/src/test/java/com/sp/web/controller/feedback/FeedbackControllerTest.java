package com.sp.web.controller.feedback;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.RequestType;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FeedbackControllerTest extends SPTestLoggedInBase {

  private static final String REQUEST_USER_EMAIL = "pradeep1@surepeople.com";
  private static final Logger LOG = Logger.getLogger(FeedbackControllerTest.class);
  private CountDownLatch lock = new CountDownLatch(1);

  /**
   * 
   * @see com.sp.web.mvc.test.setup.SPTestLoggedInBase#setUp()
   */
  @Before
  public void setUp() throws Exception {
    testSmtp.start();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeSpGoals();
    dbSetup.removeAll("personalityPracticeArea");
    dbSetup.createCompanies();
    
    dbSetup.createGoals();
    dbSetup.createUsers();
    dbSetup.createPersonalityPracticeAreas();
    User user = dbSetup.getUser(REQUEST_USER_EMAIL);
    user.addRole(RoleType.PrismLens);
    dbSetup.addUpdate(user);
    goalsFactory.addGoalsForUser(user);
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
  }
  

  @After
  public void after() throws Exception {
    testSmtp.stop();
  }

  /**
   * Test method for
   * {@link com.sp.web.controller.feedback.FeedbackController#getFeedbackTeam(org.springframework.security.authentication.Authentication)}
   * .
   */
  @Test
  public void testGetFeedbackTeam() throws Exception {
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();

//    User user = new User();
//    user.setEmail("pradeep1@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);

    // No feedback team present.
    MvcResult fee = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getFeedbackTeam").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(
            jsonPath("$.success.feedbackNoTeamHeader").value(
                MessagesHelper.getMessage(Constants.FB_MESSAGE_NO_TEAM_HEADING))).andReturn();
    LOG.info("Get Feedback team" + fee.getResponse().getContentAsString());

    dbSetup.createFeedbackUsers();
    // dbSetup.createFeedbackRequest();

    // No feedback team present.
    MvcResult external = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getFeedbackTeam").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.feedbackTeam").isArray())
        .andExpect(jsonPath("$.success.feedbackTeam", Matchers.hasSize(5))).andReturn();
    LOG.info("Get Feedback team" + external.getResponse().getContentAsString());
    List<FeedbackUser> all = dbSetup.getAll(FeedbackUser.class);
    all.forEach(this::updateAnalysis);
    external = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getFeedbackTeam")
              .param("accountDashboardRequest", "true")
                .contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.feedbackTeam").isArray())
        .andExpect(jsonPath("$.success.feedbackTeam", Matchers.hasSize(5))).andReturn();
    LOG.info("Get Feedback team" + external.getResponse().getContentAsString());    
  }

  @Test
  public void testGetFeedbackStatus() throws Exception {
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();

    User user = dbSetup.getUser();
    final String userId = user.getId();
    
    // No feedback team present.
    MvcResult fee = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedback/getFeedbackStatus")
              .param("userId", userId)
                .contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(status().is2xxSuccessful())        
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(
            jsonPath("$.success.message").value(
                "<a href=\"/sp/feedback\">Request</a> a PRISM Lens from Pradeep"))
        .andReturn();
    LOG.info("Get Feedback team" + fee.getResponse().getContentAsString());
    
    FeedbackRequest fbRequest = new FeedbackRequest();
    fbRequest.setStartDate(LocalDate.now());
    fbRequest.setEndDate(LocalDate.now().plusDays(5));
    User reqUser = dbSetup.getUser(REQUEST_USER_EMAIL);
    fbRequest.setRequestedById(reqUser.getId());
    fbRequest.setRequestStatus(RequestStatus.ACTIVE);
    fbRequest.setRequestType(RequestType.INTERNAL);
    FeedbackUser fbUser = new FeedbackUser();
    fbUser.updateFrom(user);
    fbUser.setFeatureType(FeatureType.PrismLens);
    fbUser.setFeedbackFor(reqUser.getId());
    dbSetup.addUpdate(fbUser);
    fbRequest.setFeedbackUserId(fbUser.getId());
    dbSetup.addUpdate(fbRequest);
    
    fee = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedback/getFeedbackStatus")
              .param("userId", userId)
                .contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(status().is2xxSuccessful())        
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.message").exists())
        .andExpect(jsonPath("$.success.feedbackUser").exists())
        .andReturn();
    LOG.info("Get Feedback team" + fee.getResponse().getContentAsString());
    
    fbUser.setUserStatus(UserStatus.VALID);
    updateAnalysis(fbUser);
    fbRequest.setRequestStatus(RequestStatus.COMPLETED);
    dbSetup.addUpdate(fbRequest);
   
    fee = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedback/getFeedbackStatus")
              .param("userId", userId)
                .contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(status().is2xxSuccessful())        
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.message").exists())
        .andExpect(jsonPath("$.success.feedbackUser").exists())
        .andReturn();
    LOG.info("Get Feedback team" + fee.getResponse().getContentAsString());
    
  }
  
  private void updateAnalysis(FeedbackUser fbUser) {
    Random rand = new Random();
    if (fbUser.getUserStatus() == UserStatus.VALID) {
      AnalysisBean analysis = new AnalysisBean();
      analysis.setCreatedOn(LocalDateTime.now().minusDays(rand.nextInt(100)));
      fbUser.setAnalysis(analysis);
      dbSetup.addUpdate(fbUser);
    }
  }

  @Test
  public void testInviteMemberList() throws Exception {
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.createFeedbackUsers();

//    User user = new User();
//    user.setEmail("pradeep1@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);

    // No feedback team present.
    MvcResult external = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/getAllFeedbackMembers").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.totalMembers").value(1))
        .andExpect(jsonPath("$.success.feedbackUsers").isArray())
        .andExpect(jsonPath("$.success.feedbackUsers", Matchers.hasSize(1))).andReturn();
    LOG.info("Get Feedback team" + external.getResponse().getContentAsString());

  }

  @Test
  public void testInviteUserForFeedback() throws Exception {

    dbSetup.removeAll("notificationLogMessage");
    dbSetup.removeAll("activityLogMessage");
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    // dbSetup.createFeedbackUsers();

//    User user = new User();
//    user.setEmail("pradeep1@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);
    User user = new User();
    user.setEmail(REQUEST_USER_EMAIL);
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);
    
    // Feedback request to internal user
    MvcResult feedbackUserInternal = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/submitFeedbackRequest").param("firstName", "Dax")
                .param("requestType", "INTERNAL").param("lastName", "Abhraham")
                .param("email", "dax@surepeople.com").param("endDate", "07/13/2016")
                .param("referenceType", "Colleague").param("comment", "This is the comment")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get Feedback team" + feedbackUserInternal.getResponse().getContentAsString());

    try {
      lock.await(5000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) { // TODO Auto-generated catch block
      e.printStackTrace();
    }

    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    // dbSetup.createFeedbackUsers();
 // Feedback request to External user
    MvcResult feedbackUserExternal = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/submitFeedbackRequest").param("firstName", "Dax")
                .param("requestType", "EXTERNAL").param("lastName", "Abhraham")
                .param("email", "dax@surepeople.com").param("endDate", "07/13/2016")
                .param("referenceType", "Colleague").contentType(MediaType.TEXT_PLAIN)
                .session(session2).param("comment", "Hi this is teh comment"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidParameterException").value("Invalid entry. Email address belongs to member.")).andReturn();

    // Feedback request to External user
    feedbackUserExternal = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/submitFeedbackRequest").param("firstName", "Dax")
                .param("requestType", "EXTERNAL").param("lastName", "Abhraham")
                .param("email", "external1@surepeople.com").param("endDate", "07/13/2016")
                .param("referenceType", "Colleague").contentType(MediaType.TEXT_PLAIN)
                .session(session2).param("comment", "Hi this is teh comment"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

    try {
      lock.await(5000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) { // TODO Auto-generated catch block
      e.printStackTrace();
    }
    LOG.info("Get Feedback team" + feedbackUserExternal.getResponse().getContentAsString());
  }

  @Test
  public void testGetFeedbackDetails() throws Exception {
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.createFeedbackUsers();
    dbSetup.createFeedbackRequest();
    dbSetup.removeSpGoals();
    dbSetup.createGoals();

    // Feedback request to internal user
    MvcResult feedbackNotGivenDetail = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/getFeedbackDetails").param("feadbackUserId", "4")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.feedbackNotGiven").value("has yet to complete the PRISM Lens assessment."))
        .andReturn();
    LOG.info("Get Feedback team" + feedbackNotGivenDetail.getResponse().getContentAsString());

    // Feedback request to internal user
    MvcResult feedbackGivenDetail = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/getFeedbackDetails").param("feadbackUserId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.swotanalysis.*", Matchers.hasSize(4)))
        .andReturn();
    LOG.info("Get Feedback team" + feedbackGivenDetail.getResponse().getContentAsString());
  }

  @Test
  public void testGetFeedbackArchiveDetails() throws Exception {
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.createFeedbackUsers();
    dbSetup.createFeedbackRequest();
    dbSetup.removeSpGoals();
    dbSetup.createGoals();
    dbSetup.removeAll("feedbackArchiveRequest");
    dbSetup.removeAll("feedbackUserArchive");

    // Feedback request to internal user
    MvcResult feedbackGivenDetail = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedbackArchive/getRequestDetails")
                .param("feedbackUserArchiveId", "1").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.IllegalArgumentException").value("Archive user:1: not found !!!"))        
        .andReturn();
    LOG.info("Get Feedback team" + feedbackGivenDetail.getResponse().getContentAsString());
    
    MvcResult archiveFeedback = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/archiveFeedback").param("feedbackUserId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists()).andReturn();
    LOG.info("Get Archvie Feebdack team" + archiveFeedback.getResponse().getContentAsString());
    
    feedbackGivenDetail = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedbackArchive/getRequestDetails")
                .param("feedbackUserArchiveId", "1").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.swotanalysis.*", Matchers.hasSize(4))).andReturn();
    LOG.info("Get Feedback team" + feedbackGivenDetail.getResponse().getContentAsString());    
  }

//  @Test
//  public void testGetThemeView() throws Exception {
//    dbSetup.removeAllFeedbackUsers();
//    dbSetup.removeAllFeedbackRequest();
//    dbSetup.createFeedbackUsers();
//    dbSetup.createFeedbackRequest();
//    dbSetup.removeSpGoals();
//    dbSetup.createGoals();
//
//    MvcResult themeView = mockMvc
//        .perform(
//            MockMvcRequestBuilders.get("/getThemeView").contentType(MediaType.TEXT_PLAIN)
//                .session(session2))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(jsonPath("$.success").exists())
//        .andExpect(jsonPath("$.success.Success").value("true"))
//        .andExpect(jsonPath("$.success.feedbackGoals.*", Matchers.hasSize(4)))
//        .andReturn();
//    LOG.info("Get Feedback team" + themeView.getResponse().getContentAsString());
//  }

//  @Test
//  public void testGetFeedbackRequestList() throws Exception {
//    dbSetup.removeSpGoals();
//    dbSetup.removeAllFeedbackUsers();
//    dbSetup.removeAllFeedbackRequest();
//    dbSetup.createFeedbackUsers();
//
//    User user = new User();
//    user.setEmail("pradeep2@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);
//
//    MvcResult noFeedbackRequest = mockMvc
//        .perform(
//            MockMvcRequestBuilders.get("/getAllUserFeedbackRequests")
//                .contentType(MediaType.TEXT_PLAIN).session(session2))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(jsonPath("$.error").exists())
//        .andExpect(jsonPath("$.error.InvalidRequestException").value("Not used anymore !!!"))
//        .andReturn();
//    LOG.info("Get noFeedbackRequest team" + noFeedbackRequest.getResponse().getContentAsString());
//
////    dbSetup.createFeedbackRequest();
////    dbSetup.createGoals();
////
////    MvcResult themeView = mockMvc
////        .perform(
////            MockMvcRequestBuilders.get("/getAllUserFeedbackRequests")
////                .contentType(MediaType.TEXT_PLAIN).session(session2))
////        .andExpect(content().contentType("application/json;charset=UTF-8"))
////        .andExpect(jsonPath("$.error").exists())
////        .andExpect(jsonPath("$.error.InvalidRequestException").value("Not used anymore !!!"))
////        .andReturn();
////    LOG.info("Get Feedback team" + themeView.getResponse().getContentAsString());
//  }

  @Test
  public void testGetFeedbackRequestArchivedList() throws Exception {
    dbSetup.removeSpGoals();
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.createFeedbackUsers();

//    User user = new User();
//    user.setEmail("pradeep1@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);

    MvcResult noFeedbackRequest = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getAllArchiveFeedbacks").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get noFeedbackRequest team" + noFeedbackRequest.getResponse().getContentAsString());

    dbSetup.createFeedbackRequest();
    dbSetup.createGoals();

    // archive the feedback request
    noFeedbackRequest = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/archiveFeedback").param("feedbackUserId", "2")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    LOG.info("Get noFeedbackRequest team" + noFeedbackRequest.getResponse().getContentAsString());

    MvcResult themeView = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getAllArchiveFeedbacks").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.feedbackRequest").isArray())
        .andExpect(jsonPath("$.success.feedbackRequest", Matchers.hasSize(2))).andReturn();
    LOG.info("Get Feedback team" + themeView.getResponse().getContentAsString());
  }

//  @Test
//  public void testGetUserFeedbackRequesDetail() throws Exception {
//    dbSetup.removeSpGoals();
//    dbSetup.removeAllFeedbackUsers();
//    dbSetup.removeAllFeedbackRequest();
//    dbSetup.createFeedbackUsers();
//
//    User user = new User();
//    user.setEmail("pradeep2@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);
//
//    MvcResult noFeedbackRequest = mockMvc
//        .perform(
//            MockMvcRequestBuilders.post("/getUserFeedbackRequestDetail")
//                .param("feedbackRequestId", "1").contentType(MediaType.TEXT_PLAIN)
//                .session(session2))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(jsonPath("$.error").exists())
//        .andExpect(
//            jsonPath("$.error.NoFeedbackRequestException").value("Invalid Request parameters."))
//        .andReturn();
//    LOG.info("Get noFeedbackRequest team" + noFeedbackRequest.getResponse().getContentAsString());
//
//    dbSetup.createFeedbackRequest();
//    dbSetup.createGoals();
//
//    MvcResult themeView = mockMvc
//        .perform(
//            MockMvcRequestBuilders.post("/getUserFeedbackRequestDetail")
//                .param("feedbackRequestId", "1").contentType(MediaType.TEXT_PLAIN)
//                .session(session2))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(jsonPath("$.success").exists())
//        .andExpect(jsonPath("$.success.Success").value("true"))
//        .andExpect(jsonPath("$.success.feedbackGoals").isArray())
//        .andExpect(jsonPath("$.success.feedbackGoals", Matchers.hasSize(2))).andReturn();
//    LOG.info("Get Feedback team" + themeView.getResponse().getContentAsString());
//  }

  @Test
  public void testArchiveFeedback() throws Exception {

    dbSetup.removeSpGoals();
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.createFeedbackUsers();
    dbSetup.createFeedbackRequest();

//    User user = new User();
//    user.setEmail("pradeep1@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);

    // No feedback team present.
    MvcResult external = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getFeedbackTeam").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.feedbackTeam").isArray())
        .andExpect(jsonPath("$.success.feedbackTeam", Matchers.hasSize(5))).andReturn();
    LOG.info("Get Feedback team" + external.getResponse().getContentAsString());

    MvcResult noFeedbackRequest = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/archiveFeedback").param("feedbackUserId", "2")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists()).andReturn();
    LOG.info("Get Archvie Feebdack team" + noFeedbackRequest.getResponse().getContentAsString());

    /* check if feedback team is reduced by 1 */
    // No feedback team present.
    MvcResult feedbackTeam = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getFeedbackTeam").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.feedbackTeam").isArray())
        .andExpect(jsonPath("$.success.feedbackTeam", Matchers.hasSize(4))).andReturn();
    LOG.info("Get Feedback team" + feedbackTeam.getResponse().getContentAsString());

  }

  @Test
  public void testAllArchiveFeedback() throws Exception {

    dbSetup.removeSpGoals();
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.createFeedbackUsers();
    dbSetup.createFeedbackRequest();
    dbSetup.removeArchiveFeedbackRequests();

//    User user = new User();
//    user.setEmail("pradeep1@surepeople.com");
//    user.setPassword("password");
//    authenticationHelper.doAuthenticate(session2, user);

    MvcResult archiveFeedback = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/archiveFeedback").param("feedbackUserId", "2")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists()).andReturn();
    LOG.info("Get Archvie Feebdack team" + archiveFeedback.getResponse().getContentAsString());

    MvcResult getAllArchiveFeedbackDetails = mockMvc
        .perform(
            MockMvcRequestBuilders.get("/getAllArchiveFeedbacks").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.feedbackRequest").isArray())
        .andExpect(jsonPath("$.success.feedbackRequest", Matchers.hasSize(1))).andReturn();
    LOG.info("Get getAllArchiveFeedbackDetails Feebdack team"
        + getAllArchiveFeedbackDetails.getResponse().getContentAsString());
  }

  @Test
  public void testGetArchiveRequestDetails() throws Exception {

    dbSetup.removeSpGoals();
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.removeAllArchiveFeedbackUser();
    dbSetup.createGoals();
    dbSetup.createFeedbackUsers();
    dbSetup.createFeedbackRequest();
    dbSetup.removeArchiveFeedbackRequests();
    

    MvcResult archiveFeedback = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/archiveFeedback").param("feedbackUserId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists()).andReturn();
    LOG.info("Get Archvie Feebdack team" + archiveFeedback.getResponse().getContentAsString());

    MvcResult archiveFeedbackDetail = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedbackArchive/getRequestDetails")
                .param("feedbackUserArchiveId", "1").contentType(MediaType.TEXT_PLAIN)
                .session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.swotanalysis").exists())
        .andReturn();
    LOG.info("Get Archvie Feebdack team" + archiveFeedbackDetail.getResponse().getContentAsString());

  }
  
  @Test
  public void testDeleteFeedbackUser() throws Exception {
   
    dbSetup.removeAllFeedbackUsers();
    dbSetup.removeAllFeedbackRequest();
    dbSetup.removeAllCompanies();
    dbSetup.createCompanies();
    User user = new User();
    user.setEmail(REQUEST_USER_EMAIL);
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session2, user);

    MvcResult deleteResponse = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/feedback/delete").param("feedbackUserId", "1")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists()).andReturn();
    LOG.info("Get deleted Feebdack team" + deleteResponse.getResponse().getContentAsString());

    dbSetup.createFeedbackUsers();
    dbSetup.createFeedbackRequest();

    deleteResponse = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/archiveFeedback").param("feedbackUserId", "1")
                .contentType(MediaType.APPLICATION_JSON).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists()).andReturn();
    LOG.info("Get Archvie Feebdack team" + deleteResponse.getResponse().getContentAsString());


    
  }
  
}
