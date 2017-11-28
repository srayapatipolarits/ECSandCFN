package com.sp.web.controller.hiring.spectrum;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class HiringSpectrumControllerTest extends SPTestLoggedInBase {
  
  private static final Logger log = Logger.getLogger(HiringSpectrumControllerTest.class);
  
  @Before
  public void setUp() throws Exception {
    dbSetup.removeAllUsers();
    dbSetup.createUsers();
    User user = dbSetup.getUser("pradeep1@surepeople.com");
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
  }
  @Test
  public void testGetProfileBalance() {
    try {
      dbSetup.removeAllHiringUsers();
      dbSetup.createHiringUsers();
      MvcResult hiringResults = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/hiring/spectrum/personalityBalance")
                 .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + hiringResults.getResponse().getContentAsString()); 
      
      
    } catch (Exception e) {
      log.error("error for hring test",e);
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testGetErtiInsights() {
    try {
      dbSetup.removeAllHiringUsers();
      dbSetup.createHiringUsers();
      MvcResult hiringResults = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/hiring/spectrum/ertiInsights")
                 .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + hiringResults.getResponse().getContentAsString()); 
      
      
    } catch (Exception e) {
      log.error("error for ertiInsights test",e);
      fail(e.getMessage());
    }
    
  }
  
  @Test
  public void testGetErtiAnalytics() {
    try {
      dbSetup.removeAllHiringUsers();
      dbSetup.createHiringUsers();
      MvcResult hiringResults = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/hiring/spectrum/ertiAnalytics")
                 .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + hiringResults.getResponse().getContentAsString()); 
      
      
    } catch (Exception e) {
      log.error("error for ertiInsights test",e);
      fail(e.getMessage());
    }
  }
  
}
