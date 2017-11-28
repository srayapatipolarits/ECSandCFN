package com.sp.web.scheduler;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.Constants;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.Company;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.account.PlanStatus;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.payment.MockPaymentGateway;
import com.sp.web.utils.ApplicationContextUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;

public class AccountUpdationSchedulerTest extends SPTestLoggedInBase {
  
  @Autowired
  AccountUpdationScheduler updationScheduler;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Test
  public void testProcessAccounts() {
    try {
      dbSetup.removeAllAccounts();
      dbSetup.createAccounts();
      dbSetup.removeAll("paymentInstrument");
      dbSetup.removeAll("paymentRecord");
      dbSetup.createPaymentInstruments();
      
      String accountId = "1";
      Account account = dbSetup.getAccount(accountId);
      
      SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      spPlan.setNextChargeAmount(1000.0);
      
      DateTime lastPaymentDate = DateTime.now();
      spPlan.setNextPaymentDate(lastPaymentDate.toDate());
      
      dbSetup.addUpdate(account);
      
      dbSetup.removeAllProducts();
      
      // no products in the database
      updationScheduler.processAccounts();
      
      dbSetup.createProducts();
      
      // no payment instrument
      updationScheduler.processAccounts();
      
      // add the payment instrument
      updatePI(account);
      dbSetup.addUpdate(account);
      
      // with payment instrument
      updationScheduler.processAccounts();
      
      DateTime expectedNextPaymentDate = lastPaymentDate
          .plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12);
      account = dbSetup.getAccount(accountId);
      spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
      
      Duration duration = new Duration(new DateTime(spPlan.getNextPaymentDate()),
          expectedNextPaymentDate);
      assertThat("The next payment date is one year later !!!", duration.getStandardDays(), is(0L));
      
      List<PaymentRecord> paymentRecordList = dbSetup.getAll(PaymentRecord.class);
      
      assertThat("Number of payment records !!!", paymentRecordList.size(), is(1));
      assertThat("Amount charged is", paymentRecordList.get(0).getAmount(), is(3360.0));
      
      // with promotion
      updatePromo(account.getId(), "1", 5.0);
      account = dbSetup.getAccount(accountId);
      account.setNextPaymentDate(lastPaymentDate.toDate());
      account.setExpiresTime(lastPaymentDate.toDate());
      dbSetup.addUpdate(account);
      
      // with promotion
      updationScheduler.processAccounts();
      
      MockPaymentGateway paymentGateway = ApplicationContextUtils.getBean(MockPaymentGateway.class);
      paymentGateway.dontProcessAnyMore();
      account = dbSetup.getAccount(accountId);
      account.setNextPaymentDate(lastPaymentDate.toDate());
      dbSetup.addUpdate(account);
      
      // with payment failure
      updationScheduler.processAccounts();
      
      account = dbSetup.getAccount(accountId);
      paymentRecordList = dbSetup.getAll(PaymentRecord.class);
      assertThat("Number of payment records !!!", paymentRecordList.size(), is(2));
      assertThat("Account is in payment failure status", account.getStatus(),
          is(AccountStatus.RENEWAL_PAYMENT_FAILED));
      
      paymentGateway.startProcessingAgain();
      
      // the payment expired flows
      updationScheduler.processAccounts();
      
      account = dbSetup.getAccount(accountId);
      account.setNextPaymentDate(lastPaymentDate.minusDays(
          Constants.DEFAULT_EXPIRY_GRACE_TIME_DAYS + 1).toDate());
      dbSetup.addUpdate(account);
      
      // the payment expired flows
      updationScheduler.processAccounts();
      
      account = dbSetup.getAccount(accountId);
      Company company = dbSetup.getCompany("1");
      assertThat("Account is in payment failure status", account.getStatus(),
          is(AccountStatus.EXPIRED));
      assertThat("All users are blocked", company.isBlockAllMembers(), is(true));
      
      // test archive account
      account = dbSetup.getAccount(accountId);
      account.setExpiresTime(DateTime.now().minusDays(Constants.DEFAULT_ACCOUNT_EXPIRY_DAYS + 1)
          .toDate());
      dbSetup.addUpdate(account);
      
      // the payment expired flows
      updationScheduler.processAccounts();
      
      Thread.sleep(1 * 1000);
      
      account = dbSetup.getAccount(accountId);
      assertThat("Account no longer exists !!!", account, nullValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Something bad happened !!!");
    }
  }
  
  @Test
  public void testProcessRechargeAccounts() throws Exception {
    testSmtp.start();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllCompanies();
    /* create an account */
    createAccount();
    dbSetup.createCompanies();
    
    /* check the first recharge */
    Account account = dbSetup.getAll(Account.class).get(0);
    
    SPPlan spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    DateTime lastPaymentDate = DateTime.now();
    spPlan.setNextPaymentDate(lastPaymentDate.toDate());
    dbSetup.addUpdate(account);
    updationScheduler.processAccounts();
    
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    DateTime expectedNextPaymentDate = lastPaymentDate
        .plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 1);
    
    Duration duration = new Duration(new DateTime(spPlan.getNextPaymentDate()),
        expectedNextPaymentDate);
    assertThat("The next payment date is one year later !!!", duration.getStandardDays(), is(0L));
    assertThat("Credit balance is", spPlan.getCreditBalance(), is(2600.0));
    
    /* invite 1 user */
    
    reserverSubcritpion(account);
    
    lastPaymentDate = DateTime.now();
    spPlan.setNextPaymentDate(lastPaymentDate.toDate());
    dbSetup.addUpdate(account);
    
    /* recharge again with 1 member invite */
    updationScheduler.processAccounts();
    
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    
    expectedNextPaymentDate = lastPaymentDate.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 1);
    
    duration = new Duration(new DateTime(spPlan.getNextPaymentDate()), expectedNextPaymentDate);
    assertThat("The next payment date is one year later !!!", duration.getStandardDays(), is(0L));
    assertThat("Credit balance is", spPlan.getCreditBalance(), is(1400.0));
    
    // Test prerecharge notification
    
    lastPaymentDate = DateTime.now();
    spPlan.setNextPaymentDate(lastPaymentDate.plusDays(9).toDate());
    spPlan.setCreditBalance(1000);
    dbSetup.addUpdate(account);
    
    updationScheduler.processAccounts();
    
    // with payment failure insufficient balance.
    
    lastPaymentDate = DateTime.now();
    spPlan.setNextPaymentDate(lastPaymentDate.toDate());
    spPlan.setCreditBalance(1000);
    dbSetup.addUpdate(account);
    
    updationScheduler.processAccounts();
    
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    
    expectedNextPaymentDate = lastPaymentDate.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 1);
    
    duration = new Duration(new DateTime(spPlan.getNextPaymentDate()), expectedNextPaymentDate);
    assertThat("The next payment date is one month later !!!", duration.getStandardDays(), is(29l));
    assertThat("SPPlan Status is", spPlan.getPlanStatus(), is(PlanStatus.RENEWAL_PAYMENT_FAILED));
    
    // Payment failure insuffice balance, notification test.
    
    lastPaymentDate = DateTime.now();
    spPlan.setNextPaymentDate(lastPaymentDate.minusDays(5).toDate());
    spPlan.setCreditBalance(1000);
    dbSetup.addUpdate(account);
    
    updationScheduler.processAccounts();
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    assertThat("SPPlan Status is", spPlan.getPlanStatus(), is(PlanStatus.RENEWAL_PAYMENT_FAILED));
    
    // Expire account after 20 days grace period
    
    lastPaymentDate = DateTime.now();
    spPlan.setNextPaymentDate(lastPaymentDate.minusDays(21).toDate());
    spPlan.setCreditBalance(1000);
    dbSetup.addUpdate(account);
    updationScheduler.processAccounts();
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    Date expiresTime = spPlan.getExpiresTime();
    assertThat("SPPlan Status is", spPlan.getPlanStatus(), is(PlanStatus.EXPIRED));
    
    /* capture variables to replicate the expire flow */
    Date nextPaymentDateExpired = spPlan.getNextPaymentDate();
    Date expiresTimeExipired = spPlan.getExpiresTime();
    double creditBalanceExpiredAccount = spPlan.getCreditBalance();
    
    // Add the credit and recharge sucessfull
    spPlan.setCreditBalance(1200);
    
    DateTime expiresTimetDate = new DateTime(expiresTime);
    DateTime nextPaymentDays = expiresTimetDate.minusDays(35);
    
    dbSetup.addUpdate(account);
    updationScheduler.processAccounts();
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    assertThat("SPPlan Status is", spPlan.getPlanStatus(), is(PlanStatus.ACTIVE));
    
    expectedNextPaymentDate = nextPaymentDays.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 1);
    
    duration = new Duration(new DateTime(spPlan.getNextPaymentDate()), expectedNextPaymentDate);
    assertThat("The next payment date is one year later !!!", duration.getStandardDays(), is(0L));
    assertThat("Credit balance is", spPlan.getCreditBalance(), is(0.0));
    
    //payment expired flow leading to block state.
    spPlan.setPlanStatus(PlanStatus.EXPIRED);
    spPlan.setExpiresTime(DateTime.now().toDate());
    spPlan.setNextPaymentDate(nextPaymentDateExpired);
    spPlan.setCreditBalance(creditBalanceExpiredAccount);;
    dbSetup.addUpdate(account);
    
    
    updationScheduler.processAccounts();
    account = dbSetup.getAccount(account.getId());
    spPlan = account.getSpPlanMap().get(SPPlanType.Primary);
    assertThat("SPPlan Status is", spPlan.getPlanStatus(), is(PlanStatus.BLOCKED));
    
    
    testSmtp.stop();
  }
  
  private void reserverSubcritpion(Account account) throws Exception {
    
    dbSetup.createSingUser("alternatetestuser@yopmail.com",
        accountRepository.getCompanyForAccount(account.getId()).getId());
    account.reserveSubscritption(SPPlanType.Primary);
    dbSetup.addUpdate(account);
    
  }
  
  private void createAccount() throws Exception {
    MvcResult result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business").session(session)
                .param("company", "SurePeople").param("industry", "HR").param("numEmp", "10")
                .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                .param("state", "Delhi").param("zip", "110048").param("phone", "9818399147")
                .param("referSource", "Test source").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    result = this.mockMvc
        .perform(
            post("/sysAdmin/alternateBilling/signup/business/account").session(session)
                .param("firstName", "Alternate").param("lastName", "Billing")
                .param("phone", "9818399147").param("title", "Admin")
                .param("email", "alternate@surepeople.com")
                .param("planForms[0].comment", "My Comment").param("numEmp", "10")
                .param("planForms[0].name", "My Company Plan").param("planForms[0].numAdmin", "1")
                .param("planForms[0].aggreementTerm", "1")
                .param("planForms[0].spPlanLicensePrice", "1000")
                .param("planForms[0].unitAdminPrice", "100")
                .param("planForms[0].overrideAdminPrice", "0")
                .param("planForms[0].isOverrideAdminAmount", "false")
                .param("planForms[0].numCandidates", "10")
                .param("planForms[0].pricePerCandidate", "10")
                .param("planForms[0].overrideCandidatePrice", "0")
                .param("planForms[0].isOverrideAmount", "false")
                .param("planForms[0].overridePlanAmount", "0")
                .param("planForms[0].billingCycleType", "Monthly")
                .param("planForms[0].chargeAmount", "5000")
                .param("planForms[0].paymentType", "WIRE")
                .param("planForms[0].planType", "Primary")
                .param("planForms[0].features", "PrismLens,Spectrum")
                .param("planForms[0].isBundle", "false").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
    
  }
  
}
