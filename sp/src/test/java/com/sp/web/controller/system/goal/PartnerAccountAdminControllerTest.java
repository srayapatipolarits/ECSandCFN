package com.sp.web.controller.system.goal;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.form.PartnerAccountForm;
import com.sp.web.model.partner.account.PartnerAccount;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class PartnerAccountAdminControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetAllPartnerAccounts() {
    
    try {
      dbSetup.removeAll("partnerAccount");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/getAll").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // adding an PartnerAccount
      final PartnerAccount partnerAccount = createPartnerAccount();
      
      result = this.mockMvc.perform(post("/sysAdmin/partnerAccount/getAll")
      
      .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.partnerAccountListing", hasSize(1))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // creating another
      partnerAccount.setId(null);
      partnerAccount.setActive(true);
      partnerAccount.setCompanyId("1");
      partnerAccount.setPartnerId(RandomStringUtils.random(15, true, true));
      dbSetup.addUpdate(partnerAccount);
      
      result = this.mockMvc.perform(post("/sysAdmin/partnerAccount/getAll")
      
      .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.partnerAccountListing", hasSize(2))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  private PartnerAccount createPartnerAccount() {
    return createPartnerAccount(RandomStringUtils.random(15, true, true), true, "1");
  }
  
  private PartnerAccount createPartnerAccount(String partnerId, boolean active, String companyId) {
    PartnerAccount partnerAccount = new PartnerAccount();
    partnerAccount.setPartnerId(partnerId);
    partnerAccount.setActive(true);
    partnerAccount.setCompanyId(companyId);
    
    dbSetup.addUpdate(partnerAccount);
    return partnerAccount;
  }
  
  @Test
  public void testGetpartnerAccountDetails() throws Exception {
    
    try {
      dbSetup.removeAll("partnerAccount");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/get").param("partnerAccountId", "-1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Partner Account not found"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      PartnerAccount createPartnerAccount = createPartnerAccount();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/get").param("id", createPartnerAccount.getId())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(
              jsonPath("$.success.partnerAccount.partnerId",
                  org.hamcrest.Matchers.is(createPartnerAccount.getPartnerId()))).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testCreatePartnerAccount() throws Exception {
    
    try {
      dbSetup.removeAll("partnerAccount");
      dbSetup.removeAll("hiringUser");
      
      PartnerAccountForm partnerAccountForm = new PartnerAccountForm();
      partnerAccountForm.setActive(false);
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(partnerAccountForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Company id not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      partnerAccountForm.setActive(true);
      partnerAccountForm.setCompanyId("1");
      
      request = om.writeValueAsString(partnerAccountForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/create").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
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
  public void testUpdatepartnerAccount() throws Exception {
    
    try {
      dbSetup.removeAll("partnerAccount");
      
      final PartnerAccountForm partnerAccountForm = new PartnerAccountForm();
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(partnerAccountForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Partner id is not present"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      partnerAccountForm.setId("abc");
      partnerAccountForm.setCompanyId("1");
      request = om.writeValueAsString(partnerAccountForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Partner Account not found"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      PartnerAccount partnerAccount = createPartnerAccount();
      final String partnerAccountId = partnerAccount.getId();
      partnerAccountForm.setId(partnerAccountId);
      request = om.writeValueAsString(partnerAccountForm);
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/update").content(request)
                  .contentType(MediaType.APPLICATION_JSON).session(session))
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
  public void testDeletedPartnerAccount() throws Exception {
    try {
      dbSetup.removeAll("partnerAccount");
      final PartnerAccountForm partnerAccountForm = new PartnerAccountForm();
      ObjectMapper om = new ObjectMapper();
      String request = om.writeValueAsString(partnerAccountForm);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/delete").content(request)
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value("Partner Account not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      PartnerAccount partnerAccount = createPartnerAccount();
      partnerAccountForm.setId(partnerAccount.getId());
      request = om.writeValueAsString(partnerAccountForm);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/partnerAccount/delete").content(request)
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
}
