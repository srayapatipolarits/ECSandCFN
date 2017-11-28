package com.sp.web.controller.pulse;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.PulseAssessmentForm;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;
import com.sp.web.model.pulse.PulseScore;
import com.sp.web.model.pulse.PulseScoreBean;
import com.sp.web.model.pulse.PulseSelection;
import com.sp.web.model.pulse.QuestionOptions;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.scheduler.WorkspacePulseDailyProcessor;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.MessagesHelper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * @author Dax Abraham
 *
 *         The Test class for workspace pulse tests.
 */
public class WorkspacePulseControllerTest extends SPTestLoggedInBase {
  
  @Autowired 
  WorkspacePulseFactory pulseFactory;
  private Random random = new Random();
  
  private void beforeStarting() {
    testSmtp.start();
  }
  
  private void afterEnding() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    testSmtp.stop();
  }

  @Test
  public void testPulseStart() {
    try {
      beforeStarting();
      
      // remove any previously created users
      dbSetup.removeAllCompanies();
      dbSetup.removeAll("pulseRequest");
      dbSetup.removeAll("userTodoRequests");
      
      LocalDate startDate = LocalDate.now();
      LocalDate endDate = startDate.plusDays(10);

      // invalid request no company
      final String formatStartDate = MessagesHelper.formatDate(startDate, "MM/dd/yyyy");
      String formatEndDate = MessagesHelper.formatDate(endDate, "MM/dd/yyyy");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/start")
              .param("startDate", MessagesHelper.formatDate(LocalDate.now().minusDays(1), "MM/dd/yyyy"))
              .param("endDate", formatEndDate)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Cannot start pulse in the past, invalid pulse start date !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      dbSetup.createCompanies();
      
      // invalid end date same as start date
      result = this.mockMvc
          .perform(
              post("/pulse/start")
              .param("startDate", formatStartDate)
              .param("endDate", formatStartDate)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Invalid start date and end date must be at least one day apart !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // invalid end date before start date
      result = this.mockMvc
          .perform(
              post("/pulse/start")
              .param("startDate", formatStartDate)
              .param("endDate", MessagesHelper.formatDate(LocalDate.now().minusDays(1), "MM/dd/yyyy"))
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Invalid start date and end date must be at least one day apart !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setDeactivated(true);
      dbSetup.addUpdate(user);
      // valid request 
      result = this.mockMvc
          .perform(
              post("/pulse/start")
              .param("startDate", formatStartDate)
              .param("endDate", formatEndDate)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      user = dbSetup.getUser("dax@surepeople.com");
      user.setDeactivated(false);
      dbSetup.addUpdate(user);
      
      // duplicate request
      result = this.mockMvc
          .perform(
              post("/pulse/start")
              .param("startDate", formatStartDate)
              .param("endDate", formatEndDate)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Already executing a pulse for :defaultWorkspaceQuestionSet"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      afterEnding();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testSendReminder() {
    try {
      beforeStarting();
      // remove any previously created users
      dbSetup.removeAll("pulseResults");
      dbSetup.removeAll("pulseRequest");
      dbSetup.removeAll("userTodoRequests");
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/sendReminder")
              .param("pulseRequestId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Pulse request not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      String companyId = "1";
      PulseRequest addPulseRequest = addPulseRequest(companyId);
      
      result = this.mockMvc
          .perform(
              post("/pulse/sendReminder")
              .param("pulseRequestId", addPulseRequest.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      afterEnding();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testCancelPulse() {
    try {
      beforeStarting();

      // remove any previously created users
      dbSetup.removeAll("pulseResults");
      dbSetup.removeAll("pulseRequest");
      dbSetup.removeAll("userTodoRequests");
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/cancelPulse")
              .param("pulseRequestId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Pulse request not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      String companyId = "1";
      PulseRequest addPulseRequest = addPulseRequest(companyId);
      
      result = this.mockMvc
          .perform(
              post("/pulse/cancelPulse")
              .param("pulseRequestId", addPulseRequest.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      afterEnding();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetPulseResults() {
    try {
      // remove any previously created users
      dbSetup.removeAll("pulseResults");
      dbSetup.removeAll("pulseRequest");
      dbSetup.removeAll("userTodoRequests");
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/getRequests")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseResults", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      String companyId = "1";

      addPulseResult(companyId, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), "1");
      
      result = this.mockMvc
          .perform(
              post("/pulse/getRequests")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseResults", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/pulse/getRequests")
              .param("pulseQuestionSetId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseResults", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/pulse/getRequests")
              .param("pulseQuestionSetId", "defaultWorkspaceQuestionSet")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseResults", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // add a request 
      // this will add the currently processed
      // results for the current request
      addPulseRequest(companyId);

      result = this.mockMvc
          .perform(
              post("/pulse/getRequests")
              .param("pulseQuestionSetId", "defaultWorkspaceQuestionSet")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseResults", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetPulseScore() {
    try {
      // remove any previously created users
      dbSetup.removeAll("pulseResults");
      dbSetup.removeAll("pulseRequest");
      dbSetup.removeAll("pulseAssessment");
      dbSetup.removeAllGroups();
      dbSetup.removeAll("userTodoRequests");
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/getScore")
              .param("pulseResultId", "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Pulse result id required !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      result = this.mockMvc
          .perform(
              post("/pulse/getScore")
              .param("pulseResultId", "test")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Invalid request id, no previous result by the given id :test"))
           .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      String companyId = "1";

      // add pulse request
      final PulseRequest request = addPulseRequest(companyId);

      PulseResults pulseResult = addPulseResult(companyId, LocalDate.now().minusMonths(2),
          LocalDate.now().minusMonths(1), request.getId());
      
      // valid request
      
      result = this.mockMvc
          .perform(
              post("/pulse/getScore")
              .param("pulseResultId", pulseResult.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.addGroups();
      
      // get a group
      final String groupName = "Executive";
      UserGroup group = dbSetup.getUserGroup(groupName);
      // adding another member to the group as executive 
      // only has one group member
      group.addMember("dax@surepeople.com");
      dbSetup.addUpdate(group);
      
      // take the assessment for each of the members
      takePulseFor("admin@admin.com", request.getId());
      takePulseFor("dax@surepeople.com", request.getId());
      
      result = this.mockMvc
          .perform(
              post("/pulse/getScore")
              .param("pulseResultId", pulseResult.getId())
              .param("groupName", groupName)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          //.andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetPulseQuestionSets() {
    try {      
      List<PulseQuestionSet> all = dbSetup.getAll(PulseQuestionSet.class);
      if (all.size() > 0) {
        for (int i = 1; i < all.size(); i++) {
          PulseQuestionSet pulseQuestionSet = all.get(i);
          dbSetup.remove(pulseQuestionSet);
        }
      }
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/getQuestionSets")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseQuestion", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(PulseQuestionSet.class);
      PulseQuestionSet pulseQuestionSet = all.get(0);
      pulseQuestionSet.setName("A new Test Pulse");
      pulseQuestionSet.setId(null);
      pulseQuestionSet.setForAll(false);
      ArrayList<String> companyIdList = new ArrayList<String>();
      companyIdList.add("1");
      pulseQuestionSet.setCompanyId(companyIdList);
      dbSetup.addUpdate(pulseQuestionSet);

      pulseQuestionSet.setName("Another pulse test.");
      pulseQuestionSet.setId(null);
      companyIdList.clear();
      companyIdList.add("2");
      dbSetup.addUpdate(pulseQuestionSet);
      
      result = this.mockMvc
          .perform(
              post("/pulse/getQuestionSets")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseQuestion", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.removeAll("pulseQuestionSet");
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetPulseQuestions() {
    try {
      // remove any previously created users
      dbSetup.removeAll("pulseQuestionSet");
      
      // valid request no pulse
      MvcResult result = this.mockMvc
          .perform(
              post("/pulse/getQuestions")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseQuestion").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/pulse/getQuestions")
              .param("pulseQuestionSetId", "defaultWorkspaceQuestionSet")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.pulseQuestion").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/pulse/getQuestions")
              .param("pulseQuestionSetId", "abcd")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Question set not found for :abcd"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
      
  @Test
  public void testSavePulseAssessment() {
    try {
      dbSetup.removeAll("pulseRequest");
      dbSetup.removeAll("pulseAssessment");
      dbSetup.removeAll("userTodoRequests");
      final int[] responses = {1, 2};
      
      List<PulseAssessmentForm> responseList = new ArrayList<PulseAssessmentForm>();
      
      PulseAssessmentForm assessmentForm = new PulseAssessmentForm();
      assessmentForm.setCategoryName("Leadership");
      assessmentForm.setQuestionSelectionIndex(getSelectionIndexes("Leadership"));
      responseList.add(assessmentForm);
      
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(responseList);
      log.debug("Request :" + request);

      // invalid request
      MvcResult result = this.mockMvc
          .perform(
              post("/pulseUser/saveAssessment")
              .param("pulseRequestId", "123")
              .contentType(MediaType.APPLICATION_JSON)
              .content(request)
              .session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(jsonPath("$.error.InvalidRequestException").value("Pulse request no longer availalbe !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // create a pulse request
      String companyId = "1";
//      PulseRequest pulseRequest = addPulseRequest(companyId, "abcd");
//      
//      result = this.mockMvc
//          .perform(
//              post("/pulseUser/saveAssessment")
//              .param("pulseRequestId", pulseRequest.getId())
//              .contentType(MediaType.APPLICATION_JSON)
//              .content(request)
//              .session(session))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.error").exists())
//          .andExpect(
//              jsonPath("$.error.InvalidRequestException").value(
//                  "Pulse question set not found for id :abcd"))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      PulseRequest pulseRequest = addPulseRequest(companyId);

      result = this.mockMvc
          .perform(
              post("/pulseUser/saveAssessment")
              .param("pulseRequestId", pulseRequest.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(request)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Following categories have not been answered :Engagement ,Culture"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      int[] invalidResponses = {1};
      assessmentForm = new PulseAssessmentForm();
      assessmentForm.setCategoryName("Engagement");
      assessmentForm.setQuestionSelectionIndex(invalidResponses);
      responseList.add(assessmentForm);

      request = om.writeValueAsString(responseList);
      log.debug("Request :" + request);

      result = this.mockMvc
          .perform(
              post("/pulseUser/saveAssessment")
                .param("pulseRequestId", pulseRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Question :1 not answered for the category :Engagement")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
      responseList.remove(1);
      
      // invalid category
      assessmentForm = new PulseAssessmentForm();
      assessmentForm.setCategoryName("InvalidCategory");
      assessmentForm.setQuestionSelectionIndex(responses);
      responseList.add(assessmentForm);
      
      request = om.writeValueAsString(responseList);
      log.debug("Request :" + request);
      
      result = this.mockMvc
          .perform(
              post("/pulseUser/saveAssessment")
                .param("pulseRequestId", pulseRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException")
                  .value(
                      "Category :InvalidCategory: not found in question set :defaultWorkspaceQuestionSet"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      responseList.remove(1);
      
      assessmentForm = new PulseAssessmentForm();
      assessmentForm.setCategoryName("Engagement");
      assessmentForm.setQuestionSelectionIndex(getSelectionIndexes("Engagement"));
      responseList.add(assessmentForm);
      
      assessmentForm = new PulseAssessmentForm();
      assessmentForm.setCategoryName("Culture");
      assessmentForm.setQuestionSelectionIndex(getSelectionIndexes("Culture"));
      responseList.add(assessmentForm);

      request = om.writeValueAsString(responseList);
      log.debug("Request :" + request);
      
      result = this.mockMvc
          .perform(
              post("/pulseUser/saveAssessment")
                .param("pulseRequestId", pulseRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      List<PulseAssessment> all = dbSetup.getAll(PulseAssessment.class);
      
      assertThat("At least one pulse assessment is there !!!", all.size(), is(1));
      
      // repeating the test
      result = this.mockMvc
          .perform(
              post("/pulseUser/saveAssessment")
                .param("pulseRequestId", pulseRequest.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.DuplicateKeyException").value(
                  "Unable to store the assessment for the member !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private int[] getSelectionIndexes(String categoryName) {
    PulseQuestionSet defaultPulseQuestionSet = pulseFactory.getDefaultPulseQuestionSet();
    List<PulseQuestionBean> questions = defaultPulseQuestionSet.getQuestions().get(categoryName);
    return questions.stream().mapToInt(this::randomSelection).toArray();
  }

  @Test
  public void testScheduler() {
    
    dbSetup.removeAll("pulseRequest");
    dbSetup.removeAll("pulseResults");
    dbSetup.removeAll("userTodoRequests");
    final WorkspacePulseDailyProcessor dailyProcessor = ApplicationContextUtils
        .getBean(WorkspacePulseDailyProcessor.class);
    
    String companyId = "1";
    LocalDate startDate = LocalDate.now().plusMonths(1);
    PulseRequest addPulseRequest = addPulseRequest(companyId, pulseFactory
        .getDefaultPulseQuestionSet().getId(), startDate, startDate.plusDays(1));
    addPulseRequest.getId();
    List<PulseRequest> all = dbSetup.getAll(PulseRequest.class);
    Optional<PulseRequest> findFirst = all.stream()
        .filter(pr -> pr.getId().equals(addPulseRequest.getId())).findFirst();
    assertThat(findFirst.get(), notNullValue());
    PulseRequest pulseRequest = findFirst.get();
    pulseRequest.setStartDate(LocalDate.now().minusMonths(1));
    dbSetup.addUpdate(pulseRequest);
    
    dailyProcessor.processPulseRequests();
  }

  private PulseRequest addPulseRequest(String companyId) {
    return addPulseRequest(companyId, pulseFactory.getDefaultPulseQuestionSet().getId());
  }

  private PulseRequest addPulseRequest(String companyId, String pulseQuestionSetId) {
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(10);
    return addPulseRequest(companyId, pulseQuestionSetId, startDate, endDate);
  }

  private PulseRequest addPulseRequest(String companyId, String pulseQuestionSetId,
      LocalDate startDate, LocalDate endDate) {
    return pulseFactory.startPulse(startDate, endDate, companyId, pulseQuestionSetId);
  }

  private void takePulseFor(String userEmail, String pulseRequestId) {
    // get the user 
    User user = dbSetup.getUser(userEmail);
    
    // get the question set
    PulseQuestionSet defaultPulseQuestionSet = pulseFactory.getDefaultPulseQuestionSet();
    
    PulseAssessment pulseAssessment = new PulseAssessment();
    pulseAssessment.setMemberId(user.getId());
    pulseAssessment.setPulseRequestId(pulseRequestId);
    Map<String, List<PulseSelection>> assessment = new HashMap<String, List<PulseSelection>>();
    Map<String, List<PulseQuestionBean>> questions = defaultPulseQuestionSet.getQuestions();
    for (String key: questions.keySet()) {
      List<PulseQuestionBean> categoryQuestions = questions.get(key);
      List<PulseSelection> categoryPulseSelection = new ArrayList<PulseSelection>();
      for (PulseQuestionBean questionBean : categoryQuestions) {
        categoryPulseSelection.add(new PulseSelection(questionBean.getNumber(),
            randomSelection(questionBean)));
      }
      assessment.put(key, categoryPulseSelection);
    }
    pulseAssessment.setAssessment(assessment);
    
    // store the database
    dbSetup.addUpdate(pulseAssessment);
  }

  private int randomSelection(PulseQuestionBean questionBean) {
    List<QuestionOptions> optionsList = questionBean.getOptionsList();
    int randomSelection = random.nextInt(optionsList.size());
    return randomSelection;
  }

  /**
   * Add a pulse result.
   * 
   * @param companyId
   *          - company id
   * @param startDate
   *          - start date
   * @param endDate
   *          - end date
   * @param pulseRequestId 
   *          - pulse request 
   */
  private PulseResults addPulseResult(String companyId, LocalDate startDate, LocalDate endDate, String pulseRequestId) {
    // add some pulse results
    PulseResults pulseResults = new PulseResults();
    pulseResults.setStartDate(startDate);
    pulseResults.setEndDate(endDate);
    pulseResults.setNumberOfRespondents(2);
    pulseResults.setNumberOfMembers(2);
    pulseResults.setPulseQuestionSetId("defaultWorkspaceQuestionSet");
    pulseResults.setCompanyId(companyId);
    pulseResults.setPulseRequestId(pulseRequestId);
    final Map<String, PulseScore> pulseScore = new HashMap<String, PulseScore>();
    PulseScore cultureScore = new PulseScore();
    cultureScore.setScore(4.5);
    final List<List<PulseScoreBean>> summationList = new ArrayList<List<PulseScoreBean>>();
    List<PulseScoreBean> summationScore = new ArrayList<PulseScoreBean>();
    PulseScoreBean scoreBean = new PulseScoreBean();
    scoreBean.increment(3);
    summationScore.add(scoreBean);
    scoreBean.increment(2);
    summationScore.add(scoreBean);
    summationList.add(summationScore);
    cultureScore.setSummationList(summationList);
    pulseScore.put("Culture", cultureScore);
    PulseScore loyaltyScore = new PulseScore();
    loyaltyScore.setScore(3.2);
    loyaltyScore.setSummationList(summationList);
    pulseScore.put("Engagement", loyaltyScore);
    PulseScore leadershipScore = new PulseScore();
    leadershipScore.setScore(3.2);
    leadershipScore.setSummationList(summationList);
    pulseScore.put("Leadership", leadershipScore);
    pulseResults.setPulseScore(pulseScore);
    
    dbSetup.addUpdate(pulseResults);
    return pulseResults;
  }  
}
