package com.sp.web.controller.alternateBilling;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.sp.web.Constants;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.PaymentType;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;

import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author pradeepruhil
 *
 */
public class AlternateBillingControllerTest extends SPTestLoggedInBase {
  private static final Logger log = Logger.getLogger(AlternateBillingControllerTest.class);
  
  /**
   * Test method for
   * {@link com.sp.web.controller.admin.alternatebilling.AlternateBillingController#getAllAccounts(com.sp.web.model.AccountType, org.springframework.security.authentication.Authentication)}
   * .
   */
  @Test
  public void testGetAllAccounts() throws Exception {
    
    // remove any previously created teams
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    
    dbSetup.createCompanies();
    dbSetup.createAccounts();
    dbSetup.removeAllLogs();
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/getAllAccounts").session(session)
                .param("accountType", "Business").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success.accountDetails").isArray())
        .andExpect(jsonPath("$.success.accountDetails", Matchers.hasSize(7))).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    MvcResult resultIndividual = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/getAllAccounts").session(session)
                .param("accountType", "Individual").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success.accountDetails").isArray())
        .andExpect(jsonPath("$.success.accountDetails", Matchers.hasSize(0))).andReturn();
    log.debug("The MVC Response : " + resultIndividual.getResponse().getContentAsString());
  }
  
  /**
   * Test method for
   * {@link com.sp.web.controller.admin.alternatebilling.AlternateBillingController#signupStep1(com.sp.web.form.SignupForm, com.sp.web.form.AddressForm, org.springframework.validation.BindingResult)}
   * .
   * 
   * @throws IOException
   * @throws JsonMappingException
   * @throws Exception
   */
  @Test
  public void testSignupStep() throws Exception {
    testSmtp.start();
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();
    
    dbSetup.createProducts();
    dbSetup.createsPromotions();
    
    dbSetup.removeAllLogs();
    dbSetup.createCompanies();
    // signup form page 1 - user and company details
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business").session(session)
                .param("company", "SurePeople").param("industry", "HR").param("numEmp", "10")
                .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                .param("state", "Delhi").param("zip", "110048").param("phone", "9818399147")
                .param("referSource", "Test source").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    // signup form page 2 - account, promotions and payment details
    // Create SPPlan bIlling cycle monthlyl
    
    /* test negavtive scenarios */
    
    // try to create the account again
    /* credit ammount is less then the charge account */
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business/account").session(session)
                .param("firstName", "Alternate").param("lastName", "Billing")
                .param("phone", "9818399147").param("title", "Admin")
                .param("email", "alternate@surepeople.com")
                .param("planForms[0].billingCycleType", "Monthly")
                .param("planForms[0].comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].chargeAmount", "2000")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].name", "My Company Plan").param("planForms[0].numAdmin", "2")
                .param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "20")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.PaymentProcessingException").value(
                "insufficient credit amount. -400.0")).andReturn();
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business/account").session(session)
                .param("firstName", "Alternate").param("lastName", "Billing")
                .param("phone", "9818399147").param("title", "Admin")
                .param("email", "alternate@surepeople.com")
                .param("planForms[0].comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].name", "My Company Plan").param("planForms[0].numAdmin", "2")
                .param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "20")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].billingCycleType", "Custom")
                .param("planForms[0].chargeAmount", "16800")
                .param("planForms[0].paymentType", "WIRE").param("planForms[0].noOfMonths", "7")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<Account> all = dbSetup.getAll(Account.class);
    Account account = all.get(0);
    assertThat(account.getSpPlanMap().get(SPPlanType.Primary).getNumMember(), equalTo(19));
    assertThat(account.getSpPlanMap().get(SPPlanType.Primary).getCreditBalance(), equalTo(0.0));
    // try to create the account again
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business/account").session(session)
                .param("firstName", "Alternate").param("lastName", "Billing")
                .param("phone", "9818399147").param("title", "Admin")
                .param("email", "alternate@surepeople.com")
                .param("planForms[0].billingCycleType", "Monthly")
                .param("planForms[0].comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].chargeAmount", "5000")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].name", "My Company Plan").param("planForms[0].numAdmin", "2")
                .param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "20")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false")
                .param("planForms[1].name", "Intelligent hiring")
                .param("planForms[1].numAdmin", "2").param("planForms[1].aggreementTerm", "1")
                .param("planForms[1].spPlanLicensePrice", "300")
                .param("planForms[1].unitAdminPrice", "50")
                .param("planForms[1].overrideAdminPrice", "0")
                .param("planForms[1].isOverrideAdminAmount", "false")
                .param("planForms[1].numCandidates", "20")
                .param("planForms[1].pricePerCandidate", "100")
                .param("planForms[1].overrideCandidatePrice", "0")
                .param("planForms[1].isOverrideAmount", "false")
                .param("planForms[1].overridePlanAmount", "0")
                .param("planForms[1].planType", "IntelligentHiring")
                .param("planForms[1].features", "Hiring").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.InvalidRequestException1").value("Email address already exists."))
        .andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    // try to create the account again
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business/account").session(session)
                .param("planForms[0].billingCycleType", "Monthly").param("comment", "My Comment")
                .param("phone", "9818399147").param("firstName", "Alternate")
                .param("lastName", "Billing").param("title", "Admin")
                .param("email", "alternateHiring@surepeople.com").param("numEmp", "20")
                .param("planForms[0].chargeAmount", "5000")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].name", "Intelligent hiring")
                .param("planForms[0].numAdmin", "2").param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "20")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].planType", "IntelligentHiring")
                .param("planForms[0].features", "PrismLens").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    all = dbSetup.getAll(Account.class);
    account = all.get(1);
    assertThat(account.getSpPlanMap().get(SPPlanType.IntelligentHiring).getNumMember(), equalTo(20));
    testSmtp.stop();
  }
  
  @Test
  public void testGetPlanSFeatures() throws Exception {
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/getPlansFeature").session(session).contentType(
                MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
  
  @Test
  public void testGetBuisnessAccountDetail() {
    try {
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAll("paymentRecord");
      dbSetup.removeAll("paymentInstrument");
      
      // Invalid request no account present
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/account/businessAccountDetails")
                  .param("accountId", "1").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Account not found for account id:1")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request account for company present
      dbSetup.createCompanies();
      dbSetup.createAccounts();
      dbSetup.createUsers();
      dbSetup.createPaymentRecord();
      dbSetup.createPaymentInstruments();
      // adding a business product to the account
      updateProductsAndPaymentInstrument("1", new String[] { "1" });
      
      updatePaymentRecord("1", SPPlanType.Primary);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/account/businessAccountDetails")
                  .param("accountId", "1").contentType(MediaType.TEXT_PLAIN).session(session))
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
  public void testGetPaymentHistory() {
    try {
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAll("paymentInstrument");
      dbSetup.removeAll("paymentRecord");
      dbSetup.createCompanies();
      dbSetup.createAccounts();
      dbSetup.createPaymentInstruments();
      dbSetup.createPaymentRecord();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/account/paymentHistory").param("accountId", "1")
                  .param("spPlanType", SPPlanType.Primary.toString())
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/account/paymentHistory/invoice/1/1").contentType(
                  MediaType.TEXT_PLAIN).session(session)).andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/pdf"))
          .andExpect(content().string("Transaction record not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      PaymentRecord updatePaymentRecord = updatePaymentRecord("1", SPPlanType.Primary);
      
      result = this.mockMvc
          .perform(
              post(
                  "/sysAdmin/alternateBilling/account/paymentHistory/invoice/"
                      + updatePaymentRecord.getId() + "/abcd").contentType(MediaType.TEXT_PLAIN)
                  .session(session)).andExpect(status().is2xxSuccessful())
          .andExpect(content().contentType("application/pdf"))
          .andExpect(content().string("Account not found.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post(
                  "/sysAdmin/alternateBilling/account/paymentHistory/invoice/"
                      + updatePaymentRecord.getId() + "/1").contentType(MediaType.TEXT_PLAIN)
                  .session(session)).andExpect(content().contentType("application/pdf"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddAdminMember() {
    try {
      // remove any previously created teams
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAll("paymentInstrument");
      dbSetup.removeAll("paymentRecord");
      dbSetup.createCompanies();
      dbSetup.createProducts();
      dbSetup.createAccounts();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/addAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Member :shivankspnew@yopmail.com: not found in company.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      dbSetup.createUsers();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/addAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "All Admin Slots are filled!!. Please buy more license for admin slots "))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Account account = dbSetup.getAccount("7");
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      spPlan.setNumAdmin(spPlan.getNumAdmin() + 1);
      dbSetup.addUpdate(account);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/addAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testRemoveAdminMember() {
    try {
      // remove any previously created teams
      // remove any previously created teams
      dbSetup.removeAllUsers();
      dbSetup.removeAllGroups();
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllProducts();
      dbSetup.removeAll("paymentInstrument");
      dbSetup.removeAll("paymentRecord");
      dbSetup.createCompanies();
      dbSetup.createProducts();
      dbSetup.createAccounts();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/removeAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Member :shivankspnew@yopmail.com: not found in company.")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      dbSetup.createUsers();
      Account account = dbSetup.getAccount("7");
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      spPlan.setNumAdmin(0);
      dbSetup.addUpdate(account);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/removeAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Admin Plan is not subscribed for the company")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      account = dbSetup.getAccount("7");
      spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      spPlan.setNumAdmin(1);
      dbSetup.addUpdate(account);
      User user = dbSetup.getUser("pradeep4@surepeople.com");
      dbSetup.removeUser(user);
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/removeAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "At least one admin should be available for the account")).andReturn();
      
      account = dbSetup.getAccount("7");
      spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      spPlan.setNumAdmin(2);
      dbSetup.addUpdate(account);
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/removeAdminMember")
                  .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                  .param("planType", "Primary").session(session).contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddMemberAccounts() throws Exception {
    
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    dbSetup.createCompanies();
    dbSetup.createProducts();
    dbSetup.createAccounts();
    dbSetup.createPaymentInstruments();
    Account account = dbSetup.getAccount("1");
    double nextChargeAmount = account.getSpPlanMap().get(SPPlanType.Primary).getNextChargeAmount();
    log.info("Charge Amount" + nextChargeAmount);
    
    /*
     * Try to add member using unit price, only override stuf is allowed *.
     */
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/account/addMemberAccounts").param("numAdmin", "5")
            
            .param("unitAdminPrice", "10").param("overrideAdminPrice", "50")
                .param("isOverrideAdminAmount", "true")
                
                .param("numCandidates", "10").param("pricePerCandidate", "100")
                .param("overrideCandidatePrice", "0").param("isOverrideAmount", "false")
                
                .param("planType", "Primary").param("isBundle", "false").param("accountId", "1")
                .session(session).contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.InvalidRequestException")
                .value(
                    "Cannot change the unit price during the add member. Please use override amount feature "))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    /* Add 5 admin and 10 members at unit price and admin as override price */
    account = dbSetup.getAccount("1");
    /* set the next payment date */
    SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    spPlan.setNextPaymentDate(DateTime.now().plusDays(Constants.DAYS_OF_MONTHLY_BILLING).toDate());
    dbSetup.addUpdate(account);
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/account/addMemberAccounts").param("numAdmin", "5")
            
            .param("unitAdminPrice", "50").param("overrideAdminPrice", "50")
                .param("isOverrideAdminAmount", "true")
                
                .param("numCandidates", "10").param("pricePerCandidate", "100")
                .param("overrideCandidatePrice", "1000").param("isOverrideAmount", "true")
                
                .param("planType", "Primary").param("isBundle", "false").param("accountId", "1")
                .session(session).contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    account = dbSetup.getAccount("1");
    nextChargeAmount = account.getSpPlanMap().get(SPPlanType.Primary).getNextChargeAmount();
    assertThat(account.getSpPlanMap().get(SPPlanType.Primary).getNumAdmin(), equalTo(7));
    
    /* Add more 10 member at override price */
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/account/addMemberAccounts").param("numAdmin", "0")
                .param("spPlanLicensePrice", "200").param("unitAdminPrice", "50")
                .param("overrideAdminPrice", "0").param("isOverrideAdminAmount", "false")
                .param("numCandidates", "10").param("pricePerCandidate", "100")
                .param("overrideCandidatePrice", "500").param("isOverrideAmount", "true")
                .param("overridePlanAmount", "0").param("planType", "Primary")
                .param("isBundle", "false").param("accountId", "1").session(session)
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    account = dbSetup.getAccount("1");
    double newNextChargeAmount = nextChargeAmount + 500;
    Assert.assertEquals(newNextChargeAmount, account.getSpPlanMap().get(SPPlanType.Primary)
        .getNextChargeAmount(), 0.0);
    
    double hirngNextChargeAccount = account.getSpPlanMap().get(SPPlanType.IntelligentHiring)
        .getNextChargeAmount();
    /* Add 5 hiring candidate at override price */
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/account/addMemberAccounts").param("numAdmin", "0")
                .param("unitAdminPrice", "50").param("overrideAdminPrice", "0")
                .param("isOverrideAdminAmount", "false")
                
                .param("numCandidates", "5").param("pricePerCandidate", "100")
                .param("overrideCandidatePrice", "50").param("isOverrideAmount", "true")
                
                .param("planType", "IntelligentHiring").param("isBundle", "true")
                .param("accountId", "1").session(session).contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    account = dbSetup.getAccount("1");
    
    Assert.assertEquals(hirngNextChargeAccount,
        account.getSpPlanMap().get(SPPlanType.IntelligentHiring).getNextChargeAmount(), 0.0);
    
    hirngNextChargeAccount = account.getSpPlanMap().get(SPPlanType.IntelligentHiring)
        .getNextChargeAmount();
    /* Add 5 Admin candidate at override price */
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/account/addMemberAccounts").param("numAdmin", "5")
                .param("unitAdminPrice", "0").param("overrideAdminPrice", "0")
                .param("isOverrideAdminAmount", "true")
                
                .param("numCandidates", "0").param("pricePerCandidate", "100")
                .param("overrideCandidatePrice", "50").param("isOverrideAmount", "false")
                
                .param("planType", "IntelligentHiring").param("isBundle", "true")
                .param("accountId", "1").session(session).contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    account = dbSetup.getAccount("1");
    Assert.assertEquals(hirngNextChargeAccount,
        account.getSpPlanMap().get(SPPlanType.IntelligentHiring).getNextChargeAmount(), 0.0);
  }
  
  @Test
  public void testGetMembersForCompany() throws Exception {
    
    dbSetup.removeAllUsers();
    dbSetup.removeAllGroups();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    dbSetup.createCompanies();
    dbSetup.createProducts();
    dbSetup.createAccounts();
    dbSetup.createPaymentInstruments();
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/members").param("companyId", "7")
                .param("planType", "IntelligentHiring").session(session)
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success.memberList", Matchers.hasSize(0))).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    dbSetup.createUsers();
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/removeAdminMember")
                .param("userEmail", "shivankspnew@yopmail.com").param("companyId", "7")
                .param("planType", "IntelligentHiring").session(session)
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/members").param("companyId", "7")
                .param("planType", "IntelligentHiring").session(session)
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success.memberList", Matchers.hasSize(1))).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
  
  @Test
  public void testUpdateAccount() throws Exception {
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();
    
    dbSetup.removeAllLogs();
    dbSetup.createDefaultCompany();
    // signup form page 1 - user and company details
    
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business").session(session)
                .param("company", "SurePeople").param("industry", "HR").param("numEmp", "10")
                .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                .param("state", "Delhi").param("zip", "110048").param("phone", "9818399147")
                .param("referSource", "Test source").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    
    // signup form page 2 - account, promotions and payment details
    // Create SPPlan bIlling cycle monthlyl
    
    
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business/account").session(session)
                .param("firstName", "Alternate").param("lastName", "Billing")
                .param("phone", "9818399147").param("title", "Admin")
                .param("email", "alternate@surepeople.com")
                .param("planForms[0].comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].name", "My Company Plan").param("planForms[0].numAdmin", "2")
                .param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "20")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].billingCycleType", "Custom")
                .param("planForms[0].creditAmount", "20000")
                .param("planForms[0].chargeAmount", "16800")
                .param("planForms[0].paymentType", "WIRE").param("planForms[0].noOfMonths", "7")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    /* change the billing cycle before the last month. */
    User user = dbSetup.getUser("alternate@surepeople.com");
    Set<RoleType> userRoles = user.getRoles();
    String companyId = user.getCompanyId();
    Company company = dbSetup.getCompany(companyId);
    Account account = dbSetup.getAccount(company.getAccountId());
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/updateAccount").session(session)
                .param("accountId", company.getAccountId())
                .param("comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].name", "My Company Plan")
                .param("planForms[0].numAdmin", "2").param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].isActive", "true")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].chargeAmount", "5000")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "19")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].billingCycleType", "Monthly")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false")
                .param("planForms[1].name", "Intelligent hiring")
                .param("planForms[1].numAdmin", "2").param("planForms[1].aggreementTerm", "1")
                .param("planForms[1].spPlanLicensePrice", "300")
                .param("planForms[1].unitAdminPrice", "50").param("planForms[1].isActive", "true")
                .param("planForms[1].overrideAdminPrice", "0")
                .param("planForms[1].isOverrideAdminAmount", "false")
                .param("planForms[1].numCandidates", "20")
                .param("planForms[1].pricePerCandidate", "100")
                .param("planForms[1].chargeAmount", "5000")
                .param("planForms[1].billingCycleType", "Monthly")
                .param("planForms[1].paymentType", "WIRE")
                .param("planForms[1].overrideCandidatePrice", "0")
                .param("planForms[1].isOverrideAmount", "false")
                .param("planForms[1].overridePlanAmount", "0")
                .param("planForms[1].planType", "IntelligentHiring")
                .param("planForms[1].features", "Hiring").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidRequestException1").value("Cannot change the billing cycle now. Billing Cycle can be changed during last month of billing payment date ")).andReturn();
    
    // change the last month of payment date.
    Account updateAccount = dbSetup.getAccount(company.getAccountId());
    SPPlan spPlan = updateAccount.getSpPlanMap().get(SPPlanType.Primary);
    spPlan.setNextPaymentDate(DateTime.now().plusDays(10).toDate());
    dbSetup.addUpdate(updateAccount);
    
    User userWithOutPA = dbSetup.getUser("alternate@surepeople.com");
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/updateAccount").session(session)
                .param("accountId", company.getAccountId())
                .param("firstName", "Alternate").param("lastName", "Billing")
                .param("phone", "9818399147").param("title", "Admin")
                .param("email", "alternate@surepeople.com")
                .param("comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].name", "My Company Plan")
                .param("planForms[0].numAdmin", "2").param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].isActive", "true")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].chargeAmount", "5000")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "19")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].billingCycleType", "Monthly")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false")
                .param("planForms[1].name", "Intelligent hiring")
                .param("planForms[1].numAdmin", "2").param("planForms[1].aggreementTerm", "1")
                .param("planForms[1].spPlanLicensePrice", "300")
                .param("planForms[1].unitAdminPrice", "50").param("planForms[1].isActive", "true")
                .param("planForms[1].overrideAdminPrice", "0")
                .param("planForms[1].isOverrideAdminAmount", "false")
                .param("planForms[1].numCandidates", "20")
                .param("planForms[1].pricePerCandidate", "100")
                .param("planForms[1].chargeAmount", "5000")
                .param("planForms[1].billingCycleType", "Monthly")
                .param("planForms[1].paymentType", "WIRE")
                .param("planForms[1].overrideCandidatePrice", "0")
                .param("planForms[1].newPlan", "true")
                .param("planForms[1].isOverrideAmount", "false")
                .param("planForms[1].overridePlanAmount", "0")
                .param("planForms[1].planType", "IntelligentHiring")
                .param("planForms[1].features", "Hiring").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    User userWithPA = dbSetup.getUser("alternate@surepeople.com");
    Assert.assertTrue("Contains role for hiring", userWithPA.getRoles().contains(RoleType.Hiring));
    
    updateAccount = dbSetup.getAccount(company.getAccountId());
    Assert.assertEquals(2600.0,
        updateAccount.getSpPlanMap().get(SPPlanType.IntelligentHiring).getCreditBalance(), 0.0);
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/account/businessAccountDetails")
                .param("accountId", company.getAccountId()).contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    /* get account again */
    updateAccount = dbSetup.getAccount(company.getAccountId());
    double nextChargeAmountPriary = updateAccount.getSpPlanMap().get(SPPlanType.Primary).getNextChargeAmount();
    double nextChargeAmountIntelligent = updateAccount.getSpPlanMap().get(SPPlanType.IntelligentHiring).getNextChargeAmount();
    Assert.assertEquals(2300.0,
        nextChargeAmountPriary, 0.0);
    Assert.assertEquals(account.getAccountUpdateDetailHistory().get(SPPlanType.Primary).size(),
        account.getAccountUpdateDetailHistory().get(SPPlanType.Primary).size());
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/updateAccount").session(session)
                .param("accountId", company.getAccountId())
                .param("comment", "My Comment").param("numEmp", "20")
                .param("planForms[0].name", "My Company Plan")
                .param("planForms[0].numAdmin", "2").param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].isActive", "true")
                .param("planForms[0].spPlanLicensePrice", "300")
                .param("planForms[0].unitAdminPrice", "50")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].chargeAmount", "5000")
                .param("planForms[0].billingCycleType", "Monthly")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "30")
                .param("planForms[0].pricePerCandidate", "100")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Pulse")
                .param("planForms[0].isBundle", "false")
                .param("planForms[1].name", "Intelligent hiring")
                .param("planForms[1].numAdmin", "2").param("planForms[1].aggreementTerm", "1")
                .param("planForms[1].spPlanLicensePrice", "300")
                .param("planForms[1].unitAdminPrice", "50").param("planForms[1].isActive", "true")
                .param("planForms[1].overrideAdminPrice", "0")
                .param("planForms[1].isOverrideAdminAmount", "false")
                .param("planForms[1].numCandidates", "20")
                .param("planForms[1].pricePerCandidate", "100")
                .param("planForms[1].paymentType", "WIRE")
                .param("planForms[1].chargeAmount", "5000")
                .param("planForms[1].billingCycleType", "Monthly")
                .param("planForms[1].overrideCandidatePrice", "0")
                .param("planForms[1].isOverrideAmount", "false")
                .param("planForms[1].overridePlanAmount", "0")
                .param("planForms[1].planType", "IntelligentHiring")
                .param("planForms[1].features", "Hiring").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
    updateAccount = dbSetup.getAccount(company.getAccountId());
    nextChargeAmountPriary = nextChargeAmountPriary + 1100;
    Assert.assertEquals(nextChargeAmountPriary, updateAccount.getSpPlanMap()
        .get(SPPlanType.Primary).getNextChargeAmount(), 0.0);
    
    user = dbSetup.getUser("alternate@surepeople.com");
    
    
//    Assert.assertSame(user.getRoles().toString(), userRoles, user.getRoles());
  }
  
  @Test
  public void testGetAllCompanyMembers() {
    try {
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/allCompanyMembers").session(session)
                  .param("companyId", "1").contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk()).andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAddCredit() {
    try {
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAll("paymentInstrument");
      dbSetup.createPaymentInstruments();
      Account account = dbSetup.getAccount("1");
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/account/addCredit").session(session)
                  .param("accountId", "1").param("spPlanType", SPPlanType.Primary.toString())
                  .param("chargeAmount", "1000").param("paymentType", PaymentType.WIRE.toString())
                  .param("comment", "Add Credit to Erti plan").contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk()).andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      account = dbSetup.getAccount("1");
      
      Assert.assertThat("Credit increase by 1000 dollars ",
          account.getSpPlanMap().get(SPPlanType.Primary).getCreditBalance(),
          Matchers.is(spPlan.getCreditBalance() + 1000));
      
      // plan don't exist
      result = this.mockMvc
          .perform(
              post("/sysAdmin/alternateBilling/account/addCredit").session(session)
                  .param("accountId", "2")
                  .param("spPlanType", SPPlanType.IntelligentHiring.toString())
                  .param("chargeAmount", "1000").param("paymentType", PaymentType.WIRE.toString())
                  .param("comment", "Add Credit to Erti plan").contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(
              jsonPath("$.error.IllegalArgumentException").value(
                  "Plan is not subscribed by the user, cannot add credit")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
