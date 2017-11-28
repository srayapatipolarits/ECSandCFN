package com.sp.web.controller.hiring.dashboard;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.hiring.dashboard.HiringAdminFormSettings;
import com.sp.web.model.SPFeature;
import com.sp.web.model.hiring.dashboard.HiringDashboardSettings;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiringAdminDashboardControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetAll() {
    try {
      
      dbSetup.removeAll("hiringDashboardSettings");
      dbSetup.removeAll("articles");
      dbSetup.createArticles();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/getAll").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringDashboardListing", hasSize(0))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating a tutorial
      createHiringDashboardSettings();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/getAll").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringDashboardListing", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  private HiringDashboardSettings createHiringDashboardSettings() throws Exception {
    
    HiringAdminFormSettings form = new HiringAdminFormSettings();
    List<String> artilces = new ArrayList<String>();
    artilces.add("1");
    artilces.add("2");
    artilces.add("3");
    form.setArtilces(artilces);
    
    Map<SPFeature, String> dashbaordImages = new HashMap<SPFeature, String>();
    dashbaordImages.put(SPFeature.RelationShipAdvisor, "/images/relationshipadivosr.jpg");
    dashbaordImages.put(SPFeature.Spectrum, "/images/spectrum.jpg");
    dashbaordImages.put(SPFeature.Hiring, "/images/hiring.jpg");
    form.setMediaSettings(dashbaordImages);
    
    ObjectMapper om = new ObjectMapper();
    String request = om.writeValueAsString(form);
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/hiring/dashboard/create").content(request)
                .contentType(MediaType.APPLICATION_JSON).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<HiringDashboardSettings> all = dbSetup.getAll(HiringDashboardSettings.class);
    assertTrue(all.size() > 0);
    return all.get(0);
    
  }
  
  @Test
  public void testGet() {
    try {
      
      dbSetup.removeAll("hiringDashboardSettings");
      dbSetup.removeAll("articles");
      dbSetup.createArticles();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/get").param("id", " ")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("ID is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/get").param("id", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Hiring Dashboard Settings not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      HiringDashboardSettings dashbaordSettings = createHiringDashboardSettings();
      
      final String dashboardSettingId = dashbaordSettings.getId();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/get").param("id", dashboardSettingId)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.hiringDashboard.id").value(dashboardSettingId))
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
      dbSetup.removeAll("hiringDashboardSettings");
      dbSetup.removeAll("articles");
      dbSetup.createArticles();
      
      ObjectMapper om = new ObjectMapper();
      
      HiringAdminFormSettings form = new HiringAdminFormSettings();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/create").content(om.writeValueAsString(form))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("At least one article required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> artilces = new ArrayList<String>();
      artilces.add("1");
      artilces.add("2");
      artilces.add("3");
      form.setArtilces(artilces);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/create").content(om.writeValueAsString(form))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
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
  public void testUpdate() {
    try {
      
      dbSetup.removeAll("hiringDashboardSettings");
      dbSetup.removeAll("articles");
      dbSetup.createArticles();
      
      ObjectMapper om = new ObjectMapper();
      
      HiringAdminFormSettings form = new HiringAdminFormSettings();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/update").content(om.writeValueAsString(form))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("ID is required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      form.setId("abc");
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/update").content(om.writeValueAsString(form))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("At least one article required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<String> artilces = new ArrayList<String>();
      artilces.add("1");
      artilces.add("2");
      artilces.add("3");
      form.setArtilces(artilces);
      
            
      HiringDashboardSettings dashboardSettins = createHiringDashboardSettings();
      final String id = dashboardSettins.getId();
      form.getArtilces().add("5");
      
      form.setId(id);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/hiring/dashboard/update").content(om.writeValueAsString(form))
                  .contentType(MediaType.APPLICATION_JSON).session(session))
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
  public void testDelete() {
    fail("Not yet implemented");
  }
  
}
