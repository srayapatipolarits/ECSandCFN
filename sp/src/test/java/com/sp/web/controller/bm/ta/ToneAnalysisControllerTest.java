package com.sp.web.controller.bm.ta;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.User;
import com.sp.web.model.bm.ta.ToneRequestType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.bm.ToneAnalyserFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class ToneAnalysisControllerTest extends SPTestLoggedInBase {

  @Autowired
  ToneAnalyserFactory toneAnalyserFactory;
  
  @Test
  public void testGetAll() {
    try {
      
      dbSetup.removeAll("toneAnalysisRecord");
      dbSetup.removeAll("userToneAnalysis");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAll")
              .param("companyId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAll")
              .param("companyId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.toneAnalysisAgg", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      toneAnalyserFactory.process(ToneRequestType.DashboardMessageCreate,
          "Wow I love this new feature for posting messages.", user);
      Thread.sleep(5000);

      result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAll")
              .param("companyId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.toneAnalysisAgg", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      toneAnalyserFactory.process(ToneRequestType.DashboardMessageComment,
          "Oh man this is terrible this never works.", user);
      Thread.sleep(5000);

      result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAll")
              .param("companyId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.toneAnalysisAgg", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testGetAllUserToneAnalysis() {
    try {
      
      dbSetup.removeAll("toneAnalysisRecord");
      dbSetup.removeAll("userToneAnalysis");
      
      // company not required
      MvcResult result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAllUserToneAnalysis")
              .param("userId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.toneAnalysisDetails", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      toneAnalyserFactory.process(ToneRequestType.DashboardMessageCreate,
          "Wow I love this new feature for posting messages.", user);
      Thread.sleep(5000);
      
      result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAllUserToneAnalysis")
              .param("userId", user.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.toneAnalysisDetails", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      toneAnalyserFactory.process(ToneRequestType.DashboardMessageComment,
          "Oh man this is terrible this never works.", user);
      Thread.sleep(5000);
      
      result = this.mockMvc
          .perform(
              post("/toneAnalysis/getAllUserToneAnalysis")
              .param("userId", user.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.toneAnalysisDetails", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
            
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
      
}
