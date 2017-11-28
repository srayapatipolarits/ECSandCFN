package com.sp.web.controller.admin.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

/**
 * CompanyThemeControllerTest
 * 
 * @author pradeepruhil
 *
 */
public class CompanyThemeControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testSaveTheme() throws Exception {
    // remove any previously created teams
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.createCompanies();
    
    String sytlesFormJson = "{\"stylesMap\":{\"sp-icon-color\":\"#2c3947\",\"sp-header-accentbar-color\":\"#f63\",\"sp-signin-accentbar-color\":\"#f63\",\"sp-link-color\":\"#ff4000\",\"sp-header-title-color\":\"#f63\",\"sp-footer-background-color\":\"#f63\",\"sp-button-color\":\"ff4000\",\"sp-link-hover-color\":\"#000\",\"sp-main-nav-color\":\"#ffa200\",\"sp-panel-accentbar-color\":\"#ee5a1f\",\"sub-nav-link-color\":\"#000\",\"sp-sub-nav-background-color\":\"#ffa200\",\"sp-footer-navigation-text-color\":\"#ff\",\"sp-footer-navigation-hover-color\":\"#000\"}}";
    
    // invalid request no company
    MvcResult result = this.mockMvc
        .perform(
            post("/admin/company/saveTheme").contentType(MediaType.APPLICATION_JSON)
                .content(sytlesFormJson).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.InvalidRequestException").value("Assessment due date not set !!!"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
  }
  
  @Test
  public void testGetThemeDetail() throws Exception {
    // remove any previously created teams
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    
    // Invalid request no company present
    MvcResult result = this.mockMvc
        .perform(
            post("/admin/company/getThemeDetail").contentType(MediaType.TEXT_PLAIN)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    // invalid request no account for company present
    dbSetup.createCompanies();
    
    result = this.mockMvc
        .perform(
            post("/admin/company/getThemeDetail").contentType(MediaType.TEXT_PLAIN)
                .session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.compThemeDetail").exists()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
  }
}