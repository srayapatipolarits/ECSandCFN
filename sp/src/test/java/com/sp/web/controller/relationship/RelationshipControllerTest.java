package com.sp.web.controller.relationship;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.relationship.RelationshipReportManager;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RelationshipControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  RelationshipReportManager relationshipReportManager;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Test
  public void testGetMembers() {
    try {
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      
      // test user who is not group lead
      MvcResult result = this.mockMvc
          .perform(
              post("/relationship/getMembers").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // test user who is group lead but no members
      dbSetup.createUsers();
      
      result = this.mockMvc
          .perform(
              post("/relationship/getMembers").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList", hasSize(4))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      CompanyDao company = companyFactory.getCompany("1");
      company.setRestrictRelationShipAdvisor(true);
      companyFactory.updateCompanyDao(company);
      
      // creating a new group to test
      String testGroup = "TestGroup";
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", testGroup)
                  .param("groupLead", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(testGroup))
          .andExpect(jsonPath("$.success.groupInfo.groupLead.email").value("admin@admin.com"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      User defaultUser = dbSetup.getDefaultUser();
      authenticationHelper.doAuthenticate(session, defaultUser);
      result = this.mockMvc
          .perform(
              post("/relationship/getMembers").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList", hasSize(4))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding a member to the group
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("name", testGroup)
                  .param("memberEmail", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      authenticationHelper.doAuthenticateWithoutPassword(session2, user2);
      result = this.mockMvc
          .perform(
              post("/relationship/getMembers").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      testGroup = "AnotherTestGroup";
      result = this.mockMvc
          .perform(
              post("/admin/group/create").param("name", testGroup)
                  .param("groupLead", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.groupInfo.name").value(testGroup))
          .andExpect(jsonPath("$.success.groupInfo.groupLead.email").value("admin@admin.com"))
          .andReturn();
      
      // adding a member to the group
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("name", testGroup)
                  .param("memberEmail", "dax@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      User anotherUser = new User();
      anotherUser.setFirstName("Charu");
      anotherUser.setLastName("Gupta");
      anotherUser.setEmail("charu@surepeople.com");
      anotherUser.setCompanyId("1");
      anotherUser.addRole(RoleType.User);
      AnalysisBean analysis = new AnalysisBean();
      analysis.setAccuracy(BigDecimal.valueOf(100));
      anotherUser.setAnalysis(analysis);
      dbSetup.addUpdate(anotherUser);
      
      result = this.mockMvc
          .perform(
              post("/admin/group/addMember").param("name", testGroup)
                  .param("memberEmail", "charu@surepeople.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      
      // re-authenticating to ensure that the relations are getting updated
      authenticationHelper.doAuthenticate(session, dbSetup.getDefaultUser());
      
      result = this.mockMvc
          .perform(
              post("/relationship/getMembers").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.memberList", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetTitle() {
    try {
      // test user who is group lead but no members
      dbSetup.removeAllGroups();
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      
      User user = dbSetup.getUser("pradeep1@surepeople.com");
      String profileStatement = relationshipReportManager.getProfileStatement(user);
      
      log.debug("Profile statement :" + profileStatement);
      
      assertNotNull("Profile Statement.", profileStatement);
      assertThat(
          "Profile Statement is not null",
          profileStatement,
          is("Building friendships in a variety of settings comes naturally to Pradeep,"
              + " as does maintaining an extensive network of contacts. He can be a people "
              + "magnet with a natural ability to attract others. Pradeep leads with enthusiasm "
              + "and desire while building a positive social environment where maintaining contact"
              + " with people is necessary. Because of the broad network he has established, he "
              + "likely has access to people who can be very helpful in many different areas. "
              + "Pradeep possesses the ability to promote ideas and inspire enthusiasm in others. "
              + "He is usually optimistic and can form favorable conclusions without having all "
              + "the details nailed down. This optimism allows him to embrace change and spontaneity."));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetReport() {
    try {
      // test user who is group lead but no members
      dbSetup.removeAllGroups();
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      dbSetup.removeAll("activityTracking");
      
      // test user not present
      MvcResult result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user1", "abc").param("user2", "def")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :abc: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result);
      
      // test user1 is present and user2 is not not present
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user1", "dax@surepeople.com")
                  .param("user2", "def").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :def: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result);
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setAnalysis(null);
      dbSetup.addUpdate(user);
      
      // test both users present, group lead has group association
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :admin@admin.com: assessment not completed !!!")).andReturn();
      log.debug("The MVC Response : " + result);
      
      updateAssessment("admin@admin.com", PersonalityType.Ambassador, PersonalityType.Innovator);
      
      // re-authenticating to ensure that the relations are getting updated
      authenticationHelper.doAuthenticate(session, dbSetup.getDefaultUser());
      
      // test both users present, group lead has group association
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.secondary.effort").value(containsString("Pradeep")))
          .andExpect(jsonPath("$.success.primary.effort").value(containsString("Dax"))).andReturn();
      log.debug("The MVC Response : " + result);
      
      updateAssessment("dax@surepeople.com", PersonalityType.Visionary, PersonalityType.Navigator);
      
      updateAssessment("admin@admin.com", PersonalityType.Motivator, PersonalityType.Encourager);
      // test both users present, group lead has group association
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.secondary.effort").value(containsString("Pradeep")))
          .andExpect(jsonPath("$.success.primary.effort").value(containsString("Dax"))).andReturn();
      log.debug("The MVC Response : " + result);
      
      updateAssessment("dax@surepeople.com", PersonalityType.Navigator, PersonalityType.Designer);
      updateAssessment("admin@admin.com", PersonalityType.Designer, PersonalityType.Pragmatist);
      
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result);
      
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").param("user1PersonalityType", "Primary")
                  .param("user2PersonalityType", "UnderPressure").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result);
      
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").param("user1PersonalityType", "UnderPressure")
                  .param("user2PersonalityType", "UnderPressure").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result);
      
      result = this.mockMvc
          .perform(
              post("/relationship/getReport").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").param("user1PersonalityType", "UnderPressure")
                  .param("user2PersonalityType", "Primary").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result);
      
      JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString())
          .getJSONObject("success");
      String primaryStr = jsonObject.getJSONObject("primary").toString();
      assertThat(primaryStr.substring(1, primaryStr.length() - 1), not(containsString("{")));
      String secondaryStr = jsonObject.getJSONObject("secondary").toString();
      assertThat(secondaryStr.substring(1, secondaryStr.length() - 1), not(containsString("{")));
      
      Thread.sleep(2000);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetMatchReport() {
    try {
      // test user who is group lead but no members
      dbSetup.removeAllGroups();
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      
      // test user not present
      MvcResult result = this.mockMvc
          .perform(
              post("/relationship/getCompare").param("user1", "abc").param("user2", "def")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :abc: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // test user1 is present and user2 is not not present
      result = this.mockMvc
          .perform(
              post("/relationship/getCompare").param("user1", "dax@surepeople.com")
                  .param("user2", "def").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User :def: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // test both users present, group lead has group association
      User user = dbSetup.getUser("admin@admin.com");
      user.setAnalysis(null);
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/relationship/getCompare").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "User analysis not found for first user.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      updateAssessment("admin@admin.com", PersonalityType.Motivator, PersonalityType.Encourager);
      
      // test both users present, group lead has group association
      result = this.mockMvc
          .perform(
              post("/relationship/getCompare").param("user2", "dax@surepeople.com")
                  .param("user1", "admin@admin.com").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          // .andExpect(jsonPath("$.success.secondary.effort").value(containsString("Pradeep")))
          // .andExpect(jsonPath("$.success.primary.effort").value(containsString("Dax")))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  /**
   * Update the assessment for the given user.
   * 
   * @param userEmail
   *          - user email
   * @param personality
   *          - personality to update
   */
  private void updateAssessment(String userEmail, PersonalityType primaryPersonality,
      PersonalityType underPressurePersonality) {
    // adding the assessment data for admin user
    final User user = dbSetup.getUser(userEmail);
    AnalysisBean analBean = new AnalysisBean();
    HashMap<RangeType, PersonalityBeanResponse> personalityType = new HashMap<RangeType, PersonalityBeanResponse>();
    personalityType.put(RangeType.Primary, new PersonalityBeanResponse(primaryPersonality, 1234));
    personalityType.put(RangeType.UnderPressure, new PersonalityBeanResponse(
        underPressurePersonality, 1234));
    analBean.setPersonality(personalityType);
    
    // adding processing traits map
    Map<TraitType, BigDecimal> processingMap = new HashMap<TraitType, BigDecimal>();
    addTraitsScore(TraitType.External, TraitType.Internal, processingMap);
    addTraitsScore(TraitType.Concrete, TraitType.Intuitive, processingMap);
    addTraitsScore(TraitType.Orderly, TraitType.Spontaneous, processingMap);
    addTraitsScore(TraitType.Cognitive, TraitType.Affective, processingMap);
    analBean.setProcessing(processingMap);
    
    // adding conflict management
    Map<TraitType, BigDecimal> conflictManagementMap = new HashMap<TraitType, BigDecimal>();
    addPieScores(conflictManagementMap, TraitType.Accommodate, TraitType.Avoid,
        TraitType.Collaborate, TraitType.Compete, TraitType.Compromise);
    analBean.setConflictManagement(conflictManagementMap);
    
    // adding fundamental needs
    Map<TraitType, BigDecimal> fundamentalNeedsMap = new HashMap<TraitType, BigDecimal>();
    addPieScores(fundamentalNeedsMap, TraitType.Security, TraitType.Control, TraitType.Significance);
    analBean.setFundamentalNeeds(fundamentalNeedsMap);
    
    // adding learning style
    Map<TraitType, BigDecimal> learningStyleMap = new HashMap<TraitType, BigDecimal>();
    addPieScores(learningStyleMap, TraitType.Global, TraitType.Analytical);
    analBean.setLearningStyle(learningStyleMap);
    
    // adding decision making
    Map<TraitType, BigDecimal> decisionMakingMap = new HashMap<TraitType, BigDecimal>();
    addPieScores(decisionMakingMap, TraitType.Inward, TraitType.Outward);
    addPieScores(decisionMakingMap, TraitType.Careful, TraitType.Rapid);
    analBean.setDecisionMaking(decisionMakingMap);
    
    // adding motivation why
    Map<TraitType, BigDecimal> motivationWhyMap = new HashMap<TraitType, BigDecimal>();
    addTraitsScore(TraitType.AttainmentOfGoals, TraitType.RecognitionForEffort, motivationWhyMap);
    addTraitsScore(TraitType.Power, TraitType.Compliance, motivationWhyMap);
    addTraitsScore(TraitType.Activity, TraitType.Affiliation, motivationWhyMap);
    analBean.setMotivationWhy(motivationWhyMap);
    
    // adding motivation how
    Map<TraitType, BigDecimal> motivationHowMap = new HashMap<TraitType, BigDecimal>();
    addTraitsScore(TraitType.SelfAffirmed, TraitType.AffirmedByOthers, motivationHowMap);
    addTraitsScore(TraitType.ExchangeOfIdeas, TraitType.ReceiveDirection, motivationHowMap);
    addTraitsScore(TraitType.Freedom, TraitType.Consistency, motivationHowMap);
    addTraitsScore(TraitType.TaskCompletion, TraitType.PrefersProcess, motivationHowMap);
    analBean.setMotivationHow(motivationHowMap);
    
    // adding motivation what
    Map<TraitType, BigDecimal> motivationWhatMap = new HashMap<TraitType, BigDecimal>();
    addTraitsScore(TraitType.Hygiene, TraitType.Accomplishment, motivationWhatMap);
    analBean.setMotivationWhat(motivationWhatMap);
    
    user.setAnalysis(analBean);
    dbSetup.addUpdate(user);
  }
  
  private void addPieScores(Map<TraitType, BigDecimal> conflictManagementMap,
      TraitType... traitTypes) {
    Random rand = new Random();
    int limit = 100;
    for (TraitType traitType : traitTypes) {
      int val = rand.nextInt(limit);
      conflictManagementMap.put(traitType, new BigDecimal(val));
      limit -= val;
    }
  }
  
  /**
   * Set random trait type scores.
   * 
   * @param traitType1
   *          - trait type 1
   * @param traitType2
   *          - trait type 2
   * @param scoreMap
   *          - score map
   */
  private void addTraitsScore(TraitType traitType1, TraitType traitType2,
      Map<TraitType, BigDecimal> scoreMap) {
    
    Random rand = new Random();
    int nextInt = rand.nextInt(100);
    scoreMap.put(traitType1, new BigDecimal(nextInt));
    scoreMap.put(traitType2, new BigDecimal(100 - nextInt));
  }
  
}
