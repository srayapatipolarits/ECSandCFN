package com.sp.web.controller.profile;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.model.Account;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserProfileSettings;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.test.setup.AuthenticationHelper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public class ProfileControllerTest extends SPTestLoggedInBase {

  @Autowired
  private AuthenticationHelper authenticationHelper;
  
  @Autowired
  private UserRepository userRepository;

  @Test
  public void testSendUserFullAnalysis() throws Exception {
    try {
      // SEt the db.
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      dbSetup.removeAllCompanies();
      dbSetup.createCompanies();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();

      User user = dbSetup.getUser("pradeep1@surepeople.com");
      Locale locale = new Locale("en", "US");
      user.getProfileSettings().setLocale(locale);
      AnalysisBean analysis = user.getAnalysis();
      Map<TraitType, BigDecimal> processing = analysis.getProcessing();
      processing.put(TraitType.External, new BigDecimal(30));
      processing.put(TraitType.Internal, new BigDecimal(70));
      processing.put(TraitType.Concrete, new BigDecimal(65));
      processing.put(TraitType.Intuitive, new BigDecimal(35));
      processing.put(TraitType.Cognitive, new BigDecimal(50));
      processing.put(TraitType.Affective, new BigDecimal(50));
      processing.put(TraitType.Orderly, new BigDecimal(60));
      processing.put(TraitType.Spontaneous, new BigDecimal(40));
      dbSetup.addUpdate(user);
      
      user.setPassword("password");
      authenticationHelper.doAuthenticate(session, user);

      // get analysis where analysis does not exist
      MvcResult result = this.mockMvc
          .perform(
              post("/signedIn/getAnalysisFull").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      MvcResult resultOtherUser = this.mockMvc
          .perform(
              post("/signedIn/getAnalysisFull").contentType(MediaType.TEXT_PLAIN).session(session)
                  .param("email", "pradeep2@surepeople.com"))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + resultOtherUser.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  @Test
  public void testGetProfilePublic() throws Exception {
    // SEt the db.
    dbSetup.removeAllHiringUsers();
    
    try {
      // get analysis where analysis does not exist
      MvcResult result = this.mockMvc
          .perform(post("/profilePublic/abcd").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Invalid url."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User user = dbSetup.getUser();
      HiringUser hiringUser = new HiringUser(user);
      UserProfileSettings profileSettings = hiringUser.getProfileSettings();
      dbSetup.addUpdate(hiringUser);
      
      result = this.mockMvc
          .perform(
              post("/profilePublic/" + profileSettings.getToken())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.user").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetCertificateProfile() throws Exception {
    // SEt the db.

    try {
      // get analysis where analysis does not exist
      MvcResult result = this.mockMvc
          .perform(post("/spCertificate/abcd").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Invalid url."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      User user = dbSetup.getUser("individual@surepeople.com");
      UserProfileSettings profileSettings = user.getProfileSettings();
      profileSettings.setCertificateProfilePublicView(false);
      user.setCreatedOn(LocalDate.now().minusMonths(6));
      Account account = new Account();
      account.setExpiresTime(DateTime.now().plusMonths(4).toDate());
      dbSetup.addUpdate(account);
      user.setAccountId(account.getId());
      dbSetup.addUpdate(user);
      
      userRepository.updateUserCertificate(user);

      result = this.mockMvc
          .perform(
              post("/spCertificate/" + user.getCertificateNumber())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.user").exists())
          .andExpect(jsonPath("$.success.Analysis").doesNotExist())
          .andExpect(
              jsonPath("$.success.user.certificateNumber")
                  .value("SP" + user.getCertificateNumber()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      user = dbSetup.getUser("individual@surepeople.com");
      profileSettings = user.getProfileSettings();
      profileSettings.setCertificateProfilePublicView(true);
      dbSetup.addUpdate(user);
      
      result = this.mockMvc
          .perform(
              post("/spCertificate/" + user.getCertificateNumber())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.user").exists())
          .andExpect(jsonPath("$.success.Analysis").exists())
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetPersonalityAnalysis() throws Exception {
    dbSetup.removeAllUsers();
    dbSetup.createUsers();
    dbSetup.removeAllCompanies();
    dbSetup.createCompanies();

    User user = dbSetup.getUser("pradeep1@surepeople.com");
    AnalysisBean analysis = user.getAnalysis();
    Map<TraitType, BigDecimal> processing = analysis.getProcessing();
    processing.put(TraitType.External, new BigDecimal(30));
    processing.put(TraitType.Internal, new BigDecimal(70));
    processing.put(TraitType.Concrete, new BigDecimal(65));
    processing.put(TraitType.Intuitive, new BigDecimal(35));
    processing.put(TraitType.Cognitive, new BigDecimal(50));
    processing.put(TraitType.Affective, new BigDecimal(50));
    processing.put(TraitType.Orderly, new BigDecimal(60));
    processing.put(TraitType.Spontaneous, new BigDecimal(40));
    dbSetup.addUpdate(user);
    
    user.setPassword("password");
    authenticationHelper.doAuthenticate(session, user);

    // get analysis where analysis does not exist
    MvcResult result = this.mockMvc
        .perform(
            post("/profile/getPersonalityAnalysis").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    MvcResult resultOtherUser = this.mockMvc
        .perform(
            post("/profile/getPersonalityAnalysis").contentType(MediaType.TEXT_PLAIN).session(session)
                .param("email", "pradeep2@surepeople.com"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + resultOtherUser.getResponse().getContentAsString());
    
  }
}
