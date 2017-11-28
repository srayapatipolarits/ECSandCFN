package com.sp.web.mvc.signin;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.utils.MessagesHelper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

public class LoginControllerTest extends SPTestLoggedInBase {

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testGetProfile() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();

      // get profile
      MvcResult result = this.mockMvc
          .perform(post("/signedIn/getProfile").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetAnalysis() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      User defaultUser = dbSetup.getUser("admin@admin.com");
      defaultUser.setAnalysis(null);
      dbSetup.addUpdate(defaultUser);
      authenticationHelper.updateUser(session,defaultUser);
      // get analysis where analysis does not exist
      MvcResult result = this.mockMvc
          .perform(post("/signedIn/getAnalysis").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User analysis not completed yet !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // adding the analysis for the user
      defaultUser = dbSetup.getUser("admin@admin.com");
      AnalysisBean analysis = new AnalysisBean();
      analysis.setAccuracy(BigDecimal.valueOf(95.5));
      defaultUser.setAnalysis(analysis);
      dbSetup.addUpdate(defaultUser);

      authenticationHelper.doAuthenticate(session, dbSetup.getDefaultUser());
      // get analysis where analysis does not exist
      result = this.mockMvc
          .perform(post("/signedIn/getAnalysis").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.Analysis").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetFullAnalysis() {
    String message = MessagesHelper.getMessage("profile.cm.avoid.headline");
    log.debug("message" + message);
  }

  @Test
  public void hiringUserLogin() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();

      // adding the analysis for the user
      User defaultUser = dbSetup.getUser("hiring@surepeople.com");

      authenticationHelper.doAuthenticateWithoutPassword(session, defaultUser);
      // get analysis where analysis does not exist
      MvcResult result = this.mockMvc.perform(get("/home").session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
}
