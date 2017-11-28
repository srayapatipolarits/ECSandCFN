package com.sp.web.controller.admin.account;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.exception.SignInFailedException.SignInFailReason;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class AccountControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetAvailableAccounts() {
    try {
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      
      // Invalid request no company present
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/member/availalbeMemberSubscriptions").contentType(MediaType.TEXT_PLAIN)
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
              post("/admin/member/availalbeMemberSubscriptions").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Account not set for company :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request account for company present
      dbSetup.createAccounts();
      
      result = this.mockMvc
          .perform(
              post("/admin/member/availalbeMemberSubscriptions").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.availalbeMemberSubscriptions").value(25)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetBusinessAccountDetails() {
    try {
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/businessAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session).param("planType", SPPlanType.Primary.toString()))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid request no account for company present
      dbSetup.createCompanies();
      dbSetup.createProducts();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/businessAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session).param("planType", SPPlanType.Primary.toString()))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Account not set for company :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request account for company present
      dbSetup.createAccounts();
      
      // adding a business product to the account
      updateProductsAndPaymentInstrument("1", new String[] { "1", "4" });
      
      updatePaymentRecord("1", SPPlanType.Primary);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/businessAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session).param("planType", SPPlanType.Primary.toString()))
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
  public void testGetIndividualAccountDetails() {
    try {
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/individualAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Account Id must not be null !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createUsers();
      
      // update the default user
      User user = dbSetup.getUser("dax@surepeople.com");
      String encryptedPassword = user.getPassword();
      user.setCompanyId(null);
      dbSetup.createAccounts();
      user.setAccountId("1");
      dbSetup.addUpdate(user);
      
      user.setPassword("pwd123");
      // adding the account for the user
      
      authenticationHelper.doAuthenticate(session2, user);
      
      // unauthorized request for the user
      
      result = this.mockMvc
          .perform(
              post("/admin/account/individualAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session2)).andExpect(status().is4xxClientError()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      // adding the role for the user
      user.addRole(RoleType.IndividualAccountAdministrator);
      user.setPassword(encryptedPassword);
      dbSetup.addUpdate(user);
      user.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, user);
      user.setAccountId(null);
      dbSetup.addUpdate(user);
      result = this.mockMvc
          .perform(
              post("/admin/account/individualAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Payment instrument cannot be null !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      String accountId = "2";
      user.setAccountId(accountId);
      user.setPassword(encryptedPassword);
      dbSetup.addUpdate(user);
      dbSetup.createProducts();
      updateProductsAndPaymentInstrument(accountId, new String[] { "5" });
      updatePaymentRecord(accountId, SPPlanType.IntelligentHiring);
      
      user.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, user);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/individualAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.account.id").value(accountId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetCompanyDetails() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/getCompanyDetails").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid request no account for company present
      dbSetup.createCompanies();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/getCompanyDetails").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testBlockAllMembers() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/blockAllMembers").param("isBlockAllUsers", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // successfully set
      dbSetup.createCompanies();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/blockAllMembers").param("isBlockAllUsers", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // check authentication success
      User user = new User();
      user.setEmail("dax@surepeople.com");
      user.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, user);
      
      // block all the users
      result = this.mockMvc
          .perform(
              post("/admin/account/blockAllMembers").param("isBlockAllUsers", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      try {
        // check authentication success
        authenticationHelper.doAuthenticate(session2, user);
      } catch (Exception e) {
        assertThat("Authentication has failed !!!", SignInFailReason.valueOf(e.getMessage()),
            is(SignInFailReason.CompanyBlocked));
      }
      
      // reset the blocked flag and try authentication again
      result = this.mockMvc
          .perform(
              post("/admin/account/blockAllMembers").param("isBlockAllUsers", "false")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      authenticationHelper.doAuthenticate(session2, user);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateCompany() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/updateCompany").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(status().is4xxClientError()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/updateCompany").param("name", "SurePeople")
                  .param("industry", "Test Industry").param("numberOfEmployees", "10")
                  .param("phoneNumber", "9818399147").param("address1", "address1")
                  .param("address2", "address2").param("country", "country").param("city", "city")
                  .param("state", "state").param("zip", "zip").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // successfully set
      dbSetup.createCompanies();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/updateCompany").param("name", "SurePeople")
                  .param("industry", "Test Industry").param("numberOfEmployees", "10")
                  .param("phoneNumber", "9818399147").param("address1", "address1")
                  .param("address2", "address2").param("country", "country").param("city", "city")
                  .param("state", "state").param("zip", "zip").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
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
  public void testCancelCompany() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(post("/admin/account/cancel").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      // successfully cancel
      dbSetup.createCompanies();
      dbSetup.createAccounts();
      dbSetup.createProducts();
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { accountId, "4" });
      
      updatePaymentRecord(accountId, SPPlanType.Primary);
      
      result = this.mockMvc
          .perform(post("/admin/account/cancel").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/businessAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.account.status").value(AccountStatus.CANCEL.toString()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testCancelIndividual() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAllUsers();
      
      dbSetup.createUsers();
      dbSetup.createAccounts();
      
      // update the default user
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setCompanyId(null);
      user.addRole(RoleType.IndividualAccountAdministrator);
      String accountId = "2";
      user.setAccountId(accountId);
      dbSetup.addUpdate(user);
      
      dbSetup.createProducts();
      updateProductsAndPaymentInstrument(accountId, new String[] { "5" });
      
      updatePaymentRecord(accountId, SPPlanType.IntelligentHiring);
      
      User forAuth = new User();
      forAuth.setEmail("dax@surepeople.com");
      forAuth.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, forAuth);
      
      // successfully cancel
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/cancel").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/individualAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.account.status").value(AccountStatus.CANCEL.toString()))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testReActivateCompany() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      
      // Invalid request no company present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/reActivate").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Company not found :1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      // successfully cancel
      dbSetup.createCompanies();
      dbSetup.createAccounts();
      dbSetup.createProducts();
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { accountId, "4" });
      
      updatePaymentRecord(accountId, SPPlanType.Primary);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/reActivate").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value("Account must be cancelled first."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Account account = dbSetup.getAccount("1");
      assertThat("Account valid.", account.getStatus(), is(AccountStatus.VALID));
      
      account.setStatus(AccountStatus.CANCEL);
      dbSetup.addUpdate(account);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/reActivate").contentType(MediaType.TEXT_PLAIN).session(session))
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
  public void testUpdateUserProfileSettings() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAllUsers();
      
      dbSetup.createUsers();
      dbSetup.createAccounts();
      
      // update the default user
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setCompanyId(null);
      user.addRole(RoleType.IndividualAccountAdministrator);
      String accountId = "2";
      user.setAccountId(accountId);
      dbSetup.addUpdate(user);
      
      dbSetup.createProducts();
      updateProductsAndPaymentInstrument(accountId, new String[] { "5" });
      
      User forAuth = new User();
      forAuth.setEmail("dax@surepeople.com");
      forAuth.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, forAuth);
      
      updatePaymentRecord(accountId, SPPlanType.IntelligentHiring);
      // successfully cancel
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/updateIndividualProfile").param("isHiringAccessAllowed", "true")
                  .param("is360ProfileAllowed", "false").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/individualAccountDetails").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.account.id").value(accountId))
          .andExpect(jsonPath("$.success.profileSettings.is360ProfileAccessAllowed").value(false))
          .andExpect(jsonPath("$.success.profileSettings.hiringAccessAllowed").value(true))
          .andExpect(jsonPath("$.success.profileSettings.token").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdateIndividualProfileSpCertificate() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAllUsers();
      
      dbSetup.createUsers();
      dbSetup.createAccounts();
      
      // update the default user
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setCompanyId(null);
      user.addRole(RoleType.IndividualAccountAdministrator);
      String accountId = "2";
      user.setAccountId(accountId);
      dbSetup.addUpdate(user);
      
      dbSetup.createProducts();
      updateProductsAndPaymentInstrument(accountId, new String[] { "5" });
      
      User forAuth = new User();
      forAuth.setEmail("dax@surepeople.com");
      forAuth.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, forAuth);
      
      updatePaymentRecord(accountId, SPPlanType.IntelligentHiring);
      // successfully cancel
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/updateIndividualProfile/spCertificate")
                  .param("isCertificateProfilePublicViewAllowed", "true")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      user = dbSetup.getUser("dax@surepeople.com");
      
      assertThat(user.getProfileSettings().isCertificateProfilePublicView(), is(true));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGenerateToken() {
    try {
      // remove any previously created teams
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAllUsers();
      
      dbSetup.createUsers();
      dbSetup.createAccounts();
      
      // update the default user
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setCompanyId(null);
      user.addRole(RoleType.IndividualAccountAdministrator);
      user.setAccountId("2");
      dbSetup.addUpdate(user);
      
      dbSetup.createProducts();
      updateProductsAndPaymentInstrument("2", new String[] { "5" });
      
      User forAuth = new User();
      forAuth.setEmail("dax@surepeople.com");
      forAuth.setPassword("pwd123");
      authenticationHelper.doAuthenticate(session2, forAuth);
      
      // successfully cancel
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/individualGenerateToken").contentType(MediaType.TEXT_PLAIN)
                  .session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.token").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
}
