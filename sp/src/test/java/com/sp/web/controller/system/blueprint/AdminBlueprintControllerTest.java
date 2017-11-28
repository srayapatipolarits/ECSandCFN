package com.sp.web.controller.system.blueprint;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.form.blueprint.BlueprintSettingsForm;
import com.sp.web.model.Company;
import com.sp.web.model.SPFeature;
import com.sp.web.model.blueprint.BlueprintSettings;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.blueprint.BlueprintFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vikram
 *
 *         The test cases for admin services of blueprint.
 */
public class AdminBlueprintControllerTest extends SPTestLoggedInBase {
  
  @Autowired
  BlueprintFactory blueprintFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Test
  public void testCreateOrUpdateBlueprintSettings() throws Exception {
    
    try {
      
      // form with valid data without company id. This will create default blueprint settings
      // if company id is passed, it will update the existing blueprint settings
      
      BlueprintSettingsForm bpSettingsForm = new BlueprintSettingsForm();
      bpSettingsForm.setCompanyId("4");
      // bpSettingsForm.setId("default");
      Map<String, Object> dataMap = new HashMap<String, Object>();
      final String keyChanged = "blueprintOverviewText";
      dataMap.put(keyChanged, "Blueprint Overview");
      dataMap.put("blueprintLinkType", "PDF");
      dataMap.put("blueprintLinkText", "View Blueprint Example");
      dataMap.put("blueprintLinkUrl", "sprepo.surepeople.com/cdn/");
      dataMap.put("blueprintThumbnailImageUrl", "sprepo.surepeople.com/cdn/");
      dataMap.put("blueprintImageAltText", "Reimagining");
      
      dataMap.put("missionStatementOverviewText", "Mission Statement Overview");
      dataMap.put("missionStatementLinkType", "PDF");
      dataMap.put("missionStatementLinkText", "Tips & Examples");
      dataMap.put("missionStatementLinkUrl", "sprepo.surepeople.com/cdn/");
      
      dataMap.put("objectivesOverviewText", "Objectives Overview");
      dataMap.put("objectivesLinkType", "PDF");
      dataMap.put("objectivesLinkText", "Tips & Examples");
      dataMap.put("objectivesLinkUrl", "sprepo.surepeople.com/cdn/");
      dataMap.put("objectivesMin", 1);
      dataMap.put("objectivesMax", 3);
      
      dataMap.put("keyInitiativeOverviewText", "Key Initiative Overview");
      dataMap.put("keyInitiativeLinkType", "PDF");
      dataMap.put("keyInitiativeLinkText", "Tips & Examples");
      dataMap.put("keyInitiativeLinkUrl", "sprepo.surepeople.com/cdn/");
      dataMap.put("keyInitiativeMin", 2);
      dataMap.put("keyInitiativeMax", 4);
      
      dataMap.put("csmOverviewText", "CSM Overview");
      dataMap.put("csmLinkType", "PDF");
      dataMap.put("csmLinkText", "Tips & Examples");
      dataMap.put("csmLinkUrl", "sprepo.surepeople.com/cdn/");
      dataMap.put("csmMin", 2);
      dataMap.put("csmMax", 5);
      bpSettingsForm.setDataMap(dataMap);
      
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(bpSettingsForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/createOrUpdate").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      bpSettingsForm.setCompanyId("abc");
      request = om.writeValueAsString(bpSettingsForm);
      // bpSettingsForm.setId("1");
      result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/createOrUpdate").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      bpSettingsForm.setCompanyId("1");
      final String changedText = "Blueprint Overview Changed.";
      dataMap.put(keyChanged, changedText);
      
      request = om.writeValueAsString(bpSettingsForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/createOrUpdate").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      BlueprintSettings blueprintSettings = blueprintFactory.getBlueprintSettings(bpSettingsForm
          .getCompanyId());
      final Map<String, Object> dataMap2 = blueprintSettings.getDataMap();
      assertThat(dataMap2, is(not(nullValue())));
      assertThat(dataMap2.get(keyChanged), is(changedText));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testgetAllBlueprintSettings() throws Exception {
    
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/getAll").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetCompanies() throws Exception {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/getCompanies").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companies", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Company> all = dbSetup.getAll(Company.class);
      assertThat(all.size(), is(greaterThan(0)));
      Company company = all.get(0);
      company.addFeature(SPFeature.Blueprint);
      companyFactory.updateCompany(company);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/getCompanies").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.companies", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testgetBlueprintSettingsDetails() throws Exception {
    
    try {
      
      dbSetup.removeAll("blueprintSettings");
      
      // adding the default blueprint settings
      BlueprintSettings blueprintSettings = new BlueprintSettings();
      blueprintSettings.setCompanyId(Constants.BLUEPRINT_DEFAULT_COMPANY_ID);
      Map<String, Object> dataMap = new HashMap<String, Object>();
      dataMap.put(Constants.BLUEPRINT_MIN_OBJECTIVES, 1);
      dataMap.put(Constants.BLUEPRINT_MAX_OBJECTIVES, 10);
      dataMap.put(Constants.BLUEPRINT_MIN_INITIATIVES, 1);
      dataMap.put(Constants.BLUEPRINT_MAX_INITIATIVES, 10);
      dataMap.put(Constants.BLUEPRINT_MIN_SUCCESS_MEASURES, 1);
      dataMap.put(Constants.BLUEPRINT_MAX_SUCCESS_MEASURES, 10);
      blueprintSettings.setDataMap(dataMap);
      dbSetup.addUpdate(blueprintSettings);
      
      // test with default company id
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/getDetails").param("companyId", "default")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // test with invalid company id
      result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/getDetails").param("companyId", "")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // test with valid company id
      result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/getDetails").param("companyId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testDelete() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/delete").param("companyId", "default")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Cannot delete default blueprint settings.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/blueprint/delete").param("companyId", "abc")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
}