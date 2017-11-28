package com.sp.web.controller.hiring;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.controller.hiring.match.AdminHiringPortriatDetailsDTO;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.form.hiring.match.AdminHiringPortraitMatchForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.hiring.match.AdminHiringPortraitMatchFactory;
import com.sp.web.service.hiring.role.HiringRoleFactoryCache;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HiringSharePortraitControllerTest extends SPTestLoggedInBase {

  @Autowired
  HiringRoleFactoryCache roleFactory;
  
  @Autowired
  AdminHiringPortraitMatchFactory adminFactory;
  
  @Test
  public void testGetSharePortraitList() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      
      // valid request with candidate
      HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitList")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User assessment not completed."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitList")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackUser", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      FeedbackUser feedbackUser = new FeedbackUser();
      feedbackUser.setFirstName("First");
      feedbackUser.setLastName("Last");
      feedbackUser.setEmail("someemail@yopmail.com");
      feedbackUser.setFeatureType(FeatureType.PortraitShare);
      feedbackUser.setFeedbackFor(hiringUser.getId());
      feedbackUser.setUserStatus(UserStatus.INVITATION_NOT_SENT);
      dbSetup.addUpdate(feedbackUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitList")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackUser", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  } 
  
  @Test
  public void testGetSharePortraitDetails() {
    try {
      testSmtp.start();
      
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllFeedbackUsers();
      
      // valid request with candidate
      final HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitDetails")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User for required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitDetails")
              .param("userFor", "abc")
              .param("firstName", "First")
              .param("lastName", "Last")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Email required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitDetails")
              .param("userFor", "abc")
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("email", "someone@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitDetails")
              .param("userFor", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("email", "someone@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User assessment not completed."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/getSharePortraitDetails")
              .param("userFor", hiringUser.getId())
              .param("firstName", "First")
              .param("lastName", "Last")
              .param("email", "someone@yopmail.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
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
  public void testDeleteSharePortrait() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllFeedbackUsers();
      
      // valid request with candidate
      final HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/deleteSharePortrait")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setUserStatus(UserStatus.VALID);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/deleteSharePortrait")
              .param("userId", hiringUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      FeedbackUser feedbackUser = new FeedbackUser();
      feedbackUser.setFirstName("First");
      feedbackUser.setLastName("Last");
      feedbackUser.setEmail("someemail@yopmail.com");
      feedbackUser.setFeatureType(FeatureType.PortraitShare);
      feedbackUser.setFeedbackFor(hiringUser.getId());
      feedbackUser.setUserStatus(UserStatus.INVITATION_NOT_SENT);
      dbSetup.addUpdate(feedbackUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/deleteSharePortrait")
              .param("userId", feedbackUser.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      List<FeedbackUser> all = dbSetup.getAll(FeedbackUser.class);
      assertThat(all, hasSize(0));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetUserSharePortraitDetails() {
    try {
      // remove any previously created users
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("hiringRole");
      dbSetup.removeAll("hiringPortrait");
      
      // valid request with candidate
      final HiringUser hiringUser = addHiringCandidate();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is3xxRedirection())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser.setUserStatus(UserStatus.VALID);
      User user = dbSetup.getUser();
      hiringUser.setAnalysis(user.getAnalysis());
      dbSetup.addUpdate(hiringUser);
      
      FeedbackUser feedbackUser = addFeedbackUser();

      authenticationHelper.doAuthenticateWithoutPassword(session2, feedbackUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      feedbackUser.setFeedbackFor(hiringUser.getId());
      dbSetup.addUpdate(feedbackUser);

      result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Not candidate."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      hiringUser.setType(UserType.HiringCandidate);
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackFlow", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      FeedbackUser lensUser = addFeedbackUser();
      lensUser.setFeedbackFor(hiringUser.getId());
      lensUser.setFeatureType(FeatureType.PrismLensHiring);
      lensUser.setUserStatus(UserStatus.INVITATION_NOT_SENT);
      dbSetup.addUpdate(lensUser);
      
      result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackFlow", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      HiringRole addRole = addRole(user, "Test Role");
      hiringUser.addHiringRole(addRole);
      dbSetup.addUpdate(hiringUser);

      result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackFlow", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      AdminHiringPortriatDetailsDTO addPortrait = addPortrait(user, "Test Portrait");
      addRole.setPortraitId(addPortrait.getId());
      dbSetup.addUpdate(addRole);

      result = this.mockMvc
          .perform(
              post("/hiring/ext/portrait/get")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.feedbackFlow", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  private AdminHiringPortriatDetailsDTO addPortrait(User user, String name) {
    AdminHiringPortraitMatchForm form = new AdminHiringPortraitMatchForm();
    form.setName(name);
    form.setDescription("Some description.");
    Set<String> tags = new HashSet<String>(Arrays.asList("Tag1", "Tag2"));
    form.setTags(tags);
    final Map<CategoryType, Map<String, PortraitDataMatch>> categoryDataMap = 
        new HashMap<CategoryType, Map<String, PortraitDataMatch>>();
    final Map<String, PortraitDataMatch> categoryMatchData = new HashMap<String, PortraitDataMatch>();
    PortraitDataMatch matchData = new PortraitDataMatch();
    matchData.setEnabled(true);
    List<MatchCriteria> matchCriterias = new ArrayList<MatchCriteria>();
    MatchCriteria criteria = new MatchCriteria();
    criteria.setKey(PersonalityType.Refiner + "");
    criteria.setMatchPercent(new BigDecimal(100));
    matchCriterias.add(criteria);
    matchData.setMatchCriterias(matchCriterias);
    categoryMatchData.put(RangeType.Primary + "", matchData);
    categoryDataMap.put(CategoryType.Personality, categoryMatchData);
    form.setCategoryDataMap(categoryDataMap);
    Map<CategoryPriority, BigDecimal> categoryPriorityWeightMap = new HashMap<CategoryPriority, BigDecimal>();
    categoryPriorityWeightMap.put(CategoryPriority.Essential, BigDecimal.valueOf(1));
    form.setCategoryPriorityWeightMap(categoryPriorityWeightMap);
    form.validate();
    final AdminHiringPortriatDetailsDTO create = adminFactory.create(user, form);
    return create;
  }
  
  private HiringRole addRole(User user, String name) {
    HiringRole role = new HiringRole();
    role.setName(name);
    role.setCompanyId(user.getCompanyId());
    roleFactory.save(role);
    return role;
  }

  private FeedbackUser addFeedbackUser() {
    FeedbackUser feedbackUser = new FeedbackUser();
    feedbackUser.setFirstName("First");
    feedbackUser.setLastName("Last");
    feedbackUser.setEmail("someemail@yopmail.com");
    feedbackUser.setFeatureType(FeatureType.PortraitShare);
    feedbackUser.setFeedbackFor("abc");
    feedbackUser.setUserStatus(UserStatus.INVITATION_NOT_SENT);
    feedbackUser.addRole(RoleType.HiringPortraitShare);
    dbSetup.addUpdate(feedbackUser);
    return feedbackUser;
  }  
  
  @Test
  public void testSharePortrait() {
    try {
      // remove any previously created users
      dbSetup.removeAllHiringUsers();

      testSmtp.start();
      
      HiringUser hiringUser = addHiringCandidate();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringSharePortrait.toString())
              .param(Constants.PARAM_MEMBER_LIST, "abc")
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
              .param("type", NotificationType.HiringSharePortrait.toString())
              .param(Constants.PARAM_MEMBER_LIST, hiringUser.getId())
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
}
