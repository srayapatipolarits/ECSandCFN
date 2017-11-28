package com.sp.web.controller.systemadmin;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SystemAdminstratorControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testRemoveDuplicateArticles() {
    try {
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/trainingLibrary/RemoveDuplicateArticles").contentType(
                  MediaType.TEXT_PLAIN).session(session))
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
  public void testGetScriptForCompany() {
    try {
      User user = dbSetup.getUser("pradeep1@surepeople.com");
      LocalDate date = LocalDate.now();
      user.setCreatedOn(date.minusMonths(2));
      user.setDob(date);
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/db/getCompany").param("companyId", "1")
                  .param("products", "55018440a4c8dec313c0e794, 5501844fa4c8dec313c0e797")
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
  public void testGetScriptForUser() {
    try {
      User user = dbSetup.getUser("individual@surepeople.com");
      user.setAccountId("2");
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/db/getUser").param("email", "individual@surepeople.com")
                  .param("products", "550675f63ae8f1b88eda8166").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
  public void testGetAllLoggedInUsers() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/hk/getLoggedInUsers").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetCompanyList() {
    try {
      
      User user = dbSetup.getUser("admin@admin.com");
      Set<RoleType> roles = new HashSet<RoleType>();
      roles.add(RoleType.BillingAdmin);
      user.setRoles(roles);
      
      authenticationHelper.doAuthenticateWithoutPassword(session2, user);
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/hk/getCompanyList").contentType(MediaType.TEXT_PLAIN).session(
                  session2)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testInitializePracticeArea() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/initializePracticeArea").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testAssignDefaultTutorial() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/assignDefaultTutorial")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testExportPrismPortraits() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              get("/sysAdmin/exportPrismPortraits")
              .param("companyId", "1")
              .param("emailIds", "dax@surepeople.com")
              .contentType(MediaType.TEXT_PLAIN)
              .session(session))
          .andExpect(content().contentType("text/csv;charset=UTF-8"))
          .andReturn();
      log.debug("The MVC Response : \n" + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
