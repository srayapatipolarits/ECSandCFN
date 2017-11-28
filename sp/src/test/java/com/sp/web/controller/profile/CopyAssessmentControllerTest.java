package com.sp.web.controller.profile;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

public class CopyAssessmentControllerTest extends SPTestLoggedInBase {

  @Before
  public void before() {
    testSmtp.start();
  }
  
  @After
  public void after() {
    testSmtp.stop();
  }
  
  @Test
  public void testCreateCopyAssessmentRequestMember() {
    try {
      dbSetup.removeAllUsers();
      dbSetup.removeAllTokens();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "pradeep1@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      
      User userToAuth = new User();
      userToAuth.setEmail("admin@admin.com");
      userToAuth.setPassword("admin");
      authenticationHelper.doAuthenticate(session, userToAuth);
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setAnalysis(null);
      dbSetup.addUpdate(user);
      assertThat(user.getAnalysis(), is(nullValue()));
      
      result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "pradeep1@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getAnalysis(), is(notNullValue()));
      assertThat(user.getUserStatus(), is(UserStatus.VALID));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testCreateCopyAssessmentRequestMemberErrorConditions() {
    try {
      dbSetup.removeAllUsers();
      dbSetup.removeAllTokens();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "pradeep1@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      
      User userToAuth = new User();
      userToAuth.setEmail("admin@admin.com");
      userToAuth.setPassword("admin");
      authenticationHelper.doAuthenticate(session, userToAuth);
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setAnalysis(null);
      dbSetup.addUpdate(user);
      assertThat(user.getAnalysis(), is(nullValue()));
      
      result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "pradeep1@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      user = dbSetup.getUser("pradeep1@surepeople.com");
      dbSetup.remove(user);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User not found for email :pradeep1@surepeople.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.removeAllUsers();
      
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("redirect:http://www.surepeople.com/?status=404"))
          .andReturn();
      
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

//      result = this.mockMvc
//          .perform(
//              post("/profileCopy/authorizeCopyProfile")
//              .contentType(MediaType.TEXT_PLAIN).session(session2))
//          .andExpect(content().contentType("application/json;charset=UTF-8"))
//          .andExpect(jsonPath("$.success").exists())
//          .andExpect(jsonPath("$.success.Success").value("true"))
//          .andReturn();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//      
//      user = dbSetup.getUser("admin@admin.com");
//      assertThat(user.getAnalysis(), is(notNullValue()));
//      assertThat(user.getUserStatus(), is(UserStatus.VALID));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testCreateCopyAssessmentRequestIndividual() {
    try {
      dbSetup.removeAllUsers();
      dbSetup.removeAllTokens();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "individual@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      
      User userToAuth = new User();
      userToAuth.setEmail("admin@admin.com");
      userToAuth.setPassword("admin");
      authenticationHelper.doAuthenticate(session, userToAuth);
      
      User user = dbSetup.getUser("admin@admin.com");
      user.setAnalysis(null);
      dbSetup.addUpdate(user);
      assertThat(user.getAnalysis(), is(nullValue()));
      
      result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "individual@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getAnalysis(), is(notNullValue()));
      assertThat(user.getUserStatus(), is(UserStatus.VALID));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testCreateCopyAssessmentRequestHiring() {
    try {
      dbSetup.removeAllUsers();
      dbSetup.removeAllTokens();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "aisha@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      
      User userToAuth = new User();
      userToAuth.setEmail("admin@admin.com");
      userToAuth.setPassword("admin");
      authenticationHelper.doAuthenticate(session, userToAuth);
        
      User user = dbSetup.getUser("admin@admin.com");
      user.setAnalysis(null);
      dbSetup.addUpdate(user);
      assertThat(user.getAnalysis(), is(nullValue()));
      
      HiringUser hiringUser = addHiringCandidate();
      User profileCompletedUser = dbSetup.getUser("individual@surepeople.com");
      hiringUser.setAnalysis(profileCompletedUser.getAnalysis());
      hiringUser.setCreatedOn(LocalDate.now().minusMonths(2));
      PersonalityBeanResponse personalityBeanResponse = hiringUser.getAnalysis().getPersonality()
          .get(RangeType.Primary);
      personalityBeanResponse.setPersonalityType(PersonalityType.Actuary);
      dbSetup.addUpdate(hiringUser);
      
      HiringUser hiringUser2 = new HiringUser();
      BeanUtils.copyProperties(hiringUser, hiringUser2);
      hiringUser.setId(null);
      hiringUser.setCompanyId("234");
      hiringUser.setCreatedOn(LocalDate.now());
      personalityBeanResponse = hiringUser.getAnalysis().getPersonality().get(RangeType.Primary);
      personalityBeanResponse.setPersonalityType(PersonalityType.Designer);
      dbSetup.addUpdate(hiringUser);
      

      result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "dax@einstix.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("admin@admin.com");
      assertThat(user.getAnalysis(), is(notNullValue()));
      assertThat(user.getUserStatus(), is(UserStatus.VALID));
      assertThat(user.getAnalysis().getPersonality().get(RangeType.Primary).getPersonalityType(),
          is(PersonalityType.Designer));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testCreateCopyAssessmentRequestHiringToHiring() {
    try {
      dbSetup.removeAllTokens();
      dbSetup.removeAllHiringUsers();
                        
      HiringUser hiringUser = addHiringCandidate();
      User profileCompletedUser = dbSetup.getUser("individual@surepeople.com");
      hiringUser.setAnalysis(profileCompletedUser.getAnalysis());
      hiringUser.setCreatedOn(LocalDate.now().minusMonths(2));
      PersonalityBeanResponse personalityBeanResponse = hiringUser.getAnalysis().getPersonality()
          .get(RangeType.Primary);
      personalityBeanResponse.setPersonalityType(PersonalityType.Actuary);
      dbSetup.addUpdate(hiringUser);
      
      HiringUser hiringUser2 = new HiringUser();
      BeanUtils.copyProperties(hiringUser, hiringUser2);
      hiringUser2.setId(null);
      hiringUser2.setEmail("charu@surepeople.com");
      hiringUser2.setCompanyId("234");
      hiringUser2.setCreatedOn(LocalDate.now());
      hiringUser2.setAnalysis(null);
      dbSetup.addUpdate(hiringUser2);

      authenticationHelper.doAuthenticateWithoutPassword(session, hiringUser2);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "dax@einstix.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      hiringUser2 = dbSetup.getHiringCandidate(hiringUser2.getEmail(), hiringUser2.getCompanyId());
      assertThat(hiringUser2.getAnalysis(), is(notNullValue()));
      assertThat(hiringUser2.getUserStatus(), is(UserStatus.VALID));
      assertThat(hiringUser2.getAnalysis().getPersonality().get(RangeType.Primary).getPersonalityType(),
          is(PersonalityType.Actuary));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testCreateCopyAssessmentRequestHiringToIndividual() {
    try {
      dbSetup.removeAllTokens();
      dbSetup.removeAllHiringUsers();
                        
      HiringUser hiringUser = addHiringCandidate();
      User profileCompletedUser = dbSetup.getUser("individual@surepeople.com");
      hiringUser.setAnalysis(profileCompletedUser.getAnalysis());
      hiringUser.setCreatedOn(LocalDate.now().minusMonths(2));
      PersonalityBeanResponse personalityBeanResponse = hiringUser.getAnalysis().getPersonality()
          .get(RangeType.Primary);
      personalityBeanResponse.setPersonalityType(PersonalityType.Actuary);
      dbSetup.addUpdate(hiringUser);

      profileCompletedUser.setAnalysis(null);
      dbSetup.addUpdate(profileCompletedUser);
      
      User authUser = new User();
      authUser.setEmail("individual@surepeople.com");
      authUser.setPassword("password");

      authenticationHelper.doAuthenticate(session, authUser);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "dax@einstix.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("individual@surepeople.com");
      assertThat(user.getAnalysis(), is(notNullValue()));
      assertThat(user.getUserStatus(), is(UserStatus.VALID));
      assertThat(user.getAnalysis().getPersonality().get(RangeType.Primary).getPersonalityType(),
          is(PersonalityType.Actuary));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testCreateCopyAssessmentRequestHiringArchiveToIndividual() {
    try {
      dbSetup.removeAllTokens();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();
                        
      HiringUser hiringUser = addHiringCandidate();
      User profileCompletedUser = dbSetup.getUser("individual@surepeople.com");
      hiringUser.setAnalysis(profileCompletedUser.getAnalysis());
      hiringUser.setCreatedOn(LocalDate.now().minusMonths(2));
      PersonalityBeanResponse personalityBeanResponse = hiringUser.getAnalysis().getPersonality()
          .get(RangeType.Primary);
      personalityBeanResponse.setPersonalityType(PersonalityType.Actuary);
      HiringUserArchive hiringUserArchive = new HiringUserArchive(hiringUser);
      dbSetup.addUpdate(hiringUserArchive);
      dbSetup.remove(hiringUser);

      profileCompletedUser.setAnalysis(null);
      dbSetup.addUpdate(profileCompletedUser);
      
      User authUser = new User();
      authUser.setEmail("individual@surepeople.com");
      authUser.setPassword("password");

      authenticationHelper.doAuthenticate(session, authUser);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/profileCopy/copyAssessment")
              .param("copyFromEmail", "dax@einstix.com")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Token> tokenList = dbSetup.getAll(Token.class);
      assertThat(tokenList, hasSize(1));
      
      Thread.sleep(1000);
      
      Token token = tokenList.get(0);
      result = this.mockMvc
          .perform(
              get("/processToken/" + token.getTokenId())
          .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(view().name("copyProfileAuthorize"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/profileCopy/authorizeCopyProfile")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("individual@surepeople.com");
      assertThat(user.getAnalysis(), is(notNullValue()));
      assertThat(user.getUserStatus(), is(UserStatus.VALID));
      assertThat(user.getAnalysis().getPersonality().get(RangeType.Primary).getPersonalityType(),
          is(PersonalityType.Actuary));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
}
