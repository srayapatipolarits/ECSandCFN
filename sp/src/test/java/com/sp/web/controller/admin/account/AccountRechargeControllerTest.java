package com.sp.web.controller.admin.account;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.Constants;
import com.sp.web.exception.PaymentProcessingException;
import com.sp.web.model.Account;
import com.sp.web.model.AccountStatus;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.Promotion;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.AccountFactory;
import com.sp.web.promotions.PromotionStatus;
import com.sp.web.service.payment.MockPaymentGateway;
import com.sp.web.utils.ApplicationContextUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class AccountRechargeControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetHiringProduct() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    
    try {
      
      // no hiring product
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/getHiringProduct").contentType(MediaType.TEXT_PLAIN).session(
                  session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.NoSuchElementException").value(
                  "HIRING product not found for account !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      // valid hiring products
      result = this.mockMvc
          .perform(
              post("/admin/account/getHiringProduct").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product.minEmployee").value(25))
          .andExpect(jsonPath("$.success.product").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid hiring products with promotion
      updatePromo("1", "4", 350);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/getHiringProduct").contentType(MediaType.TEXT_PLAIN).session(
                  session)).andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists())
          .andExpect(jsonPath("$.success.promotion").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetBusinessProduct() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    
    try {
      
      // no business product
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentBusinessProductInfo")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.NoSuchElementException").value(
                  "BUSINESS product not found for account !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      // valid business products
      result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentBusinessProductInfo")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid hiring products with promotion
      Promotion promo = updatePromo("1", "1", 5.5);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentBusinessProductInfo")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists())
          .andExpect(jsonPath("$.success.promotion").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      promo.setStatus(PromotionStatus.InActive);
      dbSetup.addUpdate(promo);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentBusinessProductInfo")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertTrue("Promtion is not removed, but will stay with the account", (dbSetup.getAccount("1")
          .getPromotions().size() == 1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetIndividualProduct() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    
    try {
      String productId = "5";
      String accountId = "1";
      updateIndividualUser(productId, accountId);
      
      // no business product
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentIndividualProductInfo").contentType(
                  MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.NoSuchElementException").value(
                  "INDIVIDUAL product not found for account !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      // valid business products
      result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentIndividualProductInfo").contentType(
                  MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid hiring products with promotion
      Promotion promo = updatePromo(accountId, productId, 5.5);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentIndividualProductInfo").contentType(
                  MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists())
          .andExpect(jsonPath("$.success.promotion").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      promo.setStatus(PromotionStatus.InActive);
      dbSetup.addUpdate(promo);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/getCurrentIndividualProductInfo").contentType(
                  MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product").exists()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      assertTrue("promotion inactive will still be avaible with account !!!",
          (dbSetup.getAccount("1").getPromotions().size() == 1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddCandidatesHiringProduct() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    
    try {
      updateProductsAndPaymentInstrument("1", new String[] { "1", "4" });
      
      // no hiring product
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "2").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("No product found for id:4"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      // incorrect num batches
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "0").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Number to add must be greater than zero !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "2").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(998.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request with promotion
      Promotion promo = updatePromo("1", "4", 350);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(700.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      MockPaymentGateway pg = ApplicationContextUtils.getBean(MockPaymentGateway.class);
      pg.dontProcessAnyMore();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.PaymentProcessingException").value("Payment failed !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalidate the promotion
      promo.setStatus(PromotionStatus.Suspended);
      pg.startProcessingAgain();
      dbSetup.addUpdate(promo);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(700.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // trying again for an invalid promotion being tried to apply to an account
      // that has no promotions previously
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidates").param("productId", "4")
                  .param("numBatches", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
            .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(700.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      pg.startProcessingAgain();
      
      promo.setStatus(PromotionStatus.Active);
      dbSetup.addUpdate(promo);
      
      // with a new payment instrument
      result = this.mockMvc
          .perform(
              post("/admin/account/addHiringCandidatesPI").param("productId", "4")
                  .param("numBatches", "2").param("promotionId", "1000").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", "1")
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(700.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testProcessPayment() {
    
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    dbSetup.removeAll("deletedUser");
    
    try {
      String productId = "2";
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { productId, "4" });
      Account account = dbSetup.getAccount(accountId);
      
      // incorrect expiry time
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/processPayment").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", "1")
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.NoSuchElementException").value(
                  "BUSINESS product not found for account !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      int availableSubs = 25;
      int curentEmployeeCount = (int) dbSetup.getMemberCount("1");
      double prodUnitPrice = 16.0;
      double monthlyCharge = prodUnitPrice * (availableSubs + curentEmployeeCount);
      account.setNextChargeAmount(monthlyCharge);
      DateTime lastPaymentDate = DateTime.now().minusDays(1);
      account.setNextPaymentDate(lastPaymentDate.toDate());
      dbSetup.addUpdate(account);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/processPayment").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", "1")
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment.amount").value(monthlyCharge)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      account = dbSetup.getAccount(accountId);
      Duration duration = new Duration(lastPaymentDate, new DateTime(account.getNextPaymentDate()));
      assertThat("There are 30 days between the two days !!!", (int) duration.getStandardDays(),
          is(Constants.DAYS_OF_MONTHLY_BILLING));
      assertThat("Next payment amount !!!", account.getNextChargeAmount(), is(monthlyCharge));
      
      lastPaymentDate = new DateTime(account.getNextPaymentDate());
      account.setStatus(AccountStatus.RENEWAL_PAYMENT_FAILED);
      dbSetup.addUpdate(account);
      updatePromo(accountId, productId, 5);
      
      MockPaymentGateway pg = ApplicationContextUtils.getBean(MockPaymentGateway.class);
      pg.dontProcessAnyMore();
      result = this.mockMvc
          .perform(
              post("/admin/account/processPayment").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", "1")
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.PaymentProcessingException").value("Payment failed !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      pg.startProcessingAgain();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/processPayment").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", "1")
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment.amount").value(monthlyCharge)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      prodUnitPrice = 5;
      monthlyCharge = prodUnitPrice * (availableSubs + curentEmployeeCount);
      
      account = dbSetup.getAccount(accountId);
      duration = new Duration(lastPaymentDate, new DateTime(account.getNextPaymentDate()));
      assertThat("There are 30 days between the two days !!!", (int) duration.getStandardDays(),
          is(0));
      assertThat("Next payment amount !!!", account.getNextChargeAmount(), is(monthlyCharge));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddMemberAccount() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    dbSetup.removeAll("deletedUser");
    
    try {
      updateProductsAndPaymentInstrument("1", new String[] { "1", "4" });
      
      // no product
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "25").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("No product found for id:1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      // incorrect num batches
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "0").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Number to add must be greater than zero !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Account account = dbSetup.getAccount("1");
      int numSubs = account.getAvailableSubscriptions();
      LocalDate billingCycleStartDate = LocalDate
          .fromDateFields(account.getBillingCycleStartDate());
      account.setNextPaymentDate(billingCycleStartDate.plusDays(
          12 * Constants.DAYS_OF_MONTHLY_BILLING).toDate());
      account.setNextChargeAmount(numSubs * 6.0);
      dbSetup.addUpdate(account);
      
      // valid request
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "25").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(3850.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      numSubs += 25;
      account = dbSetup.getAccount("1");
      assertThat("Subscriptions have increased.", account.getAvailableSubscriptions(), is(numSubs));
      
      // valid request with promotion
      Promotion promo = updatePromo("1", "1", 5);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(110.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      numSubs += 2;
      int empCount = (int) dbSetup.getMemberCount("1");
      double prodUnitPrice = 5;
      double chargeAmount = prodUnitPrice * (empCount + numSubs) * 12;
      
      account = dbSetup.getAccount("1");
      assertThat("Subscriptions have increased.", account.getAvailableSubscriptions(), is(numSubs));
      assertThat("Next payment amount is", account.getNextChargeAmount(), is(chargeAmount));
      
      MockPaymentGateway pg = ApplicationContextUtils.getBean(MockPaymentGateway.class);
      pg.dontProcessAnyMore();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.PaymentProcessingException").value("Payment failed !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalidate the promotion
      promo.setStatus(PromotionStatus.Suspended);
      pg.startProcessingAgain();
      dbSetup.addUpdate(promo);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
       .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(110.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // trying again for an invalid promotion being tried to apply to an account
      // that has no promotions previously
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", "1")
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
           .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(110.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      pg.startProcessingAgain();
      
      promo.setStatus(PromotionStatus.Active);
      dbSetup.addUpdate(promo);
      
      // with a new payment instrument
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccountsPI").param("productId", "1")
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .param("nameOnCard", "dax").param("cardNumber", "12345 12345 12344 4567")
                  .param("month", "1").param("year", "11").param("cvv", "123")
                  .param("country", "USA").param("zip", "12345").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(110.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testAddMemberAccountMonthlyPayments() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAllProducts();
    dbSetup.removeAllPromotions();
    dbSetup.removeAll("paymentInstrument");
    dbSetup.removeAll("paymentRecord");
    dbSetup.removeAll("deletedUser");
    
    try {
      String productId = "2";
      String annualProductId = "1";
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { productId, "4" });
      
      // no product
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "25").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No product found for id:" + productId)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createProducts();
      
      // incorrect num batches
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "0").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Number to add must be greater than zero !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Account account = dbSetup.getAccount(accountId);
      int numSubs = account.getAvailableSubscriptions();
      LocalDate billingCycleStartDate = LocalDate.now().minusDays(15);
      account.setStartDate(billingCycleStartDate.toDate());
      account.setBillingCycleStartDate(billingCycleStartDate.toDate());
      account.setNextPaymentDate(billingCycleStartDate.plusDays(
          1 * Constants.DAYS_OF_MONTHLY_BILLING).toDate());
      account.setNextChargeAmount(numSubs * 16.0);
      dbSetup.addUpdate(account);
      
      // valid request
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "25").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(200.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      numSubs += 25;
      account = dbSetup.getAccount(accountId);
      assertThat("Subscriptions have increased.", account.getAvailableSubscriptions(), is(numSubs));
      
      // valid request with promotion
      Promotion promo = updatePromo(accountId, productId, 6.5);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(6.5)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      numSubs += 2;
      
      // get the count of employees
      int empCount = (int) dbSetup.getMemberCount("1");
      double prodUnitPrice = 6.5;
      double chargeAmount = prodUnitPrice * (empCount + numSubs);
      
      account = dbSetup.getAccount(accountId);
      assertThat("Subscriptions have increased.", account.getAvailableSubscriptions(), is(numSubs));
      // as there are two existing members the total cost would be 55 * 6.5
      assertThat("Next payment amount is", account.getNextChargeAmount(), is(chargeAmount));
      
      MockPaymentGateway pg = ApplicationContextUtils.getBean(MockPaymentGateway.class);
      pg.dontProcessAnyMore();
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.PaymentProcessingException").value("Payment failed !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalidate the promotion
      promo.setStatus(PromotionStatus.Suspended);
      pg.startProcessingAgain();
      dbSetup.addUpdate(promo);
      
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(6.5)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // trying again for an invalid promotion being tried to apply to an account
      // that has no promotions previously
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
         .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(6.5))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // invalid product id
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", annualProductId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Invalid product :1: not associated to account !!!")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      promo.setStatus(PromotionStatus.Active);
      dbSetup.addUpdate(promo);
      
      // with a new payment instrument
      result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccountsPI").param("productId", productId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .param("nameOnCard", "dax").param("cardNumber", "12345 12345 12344 4567")
                  .param("month", productId).param("year", "11").param("cvv", "123")
                  .param("country", "USA").param("zip", "12345").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(6.5)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testUpdatePaymentInstrument() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAll("paymentInstrument");
    
    try {
      String productId = "2";
      String annualProductId = "1";
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { productId, "4" });
      
      Account account = dbSetup.getAccount(accountId);
      String prevPaymentInstrumentId = account.getPaymentInstrumentId();
      
      // update for business account
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/updatePaymentInstrument").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", productId)
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      account = dbSetup.getAccount(accountId);
      Assert.assertEquals("The payment instrument for the account !!!",
          account.getPaymentInstrumentId(), prevPaymentInstrumentId);
      // PaymentInstrument pi = dbSetup.getPaymentInstrument(prevPaymentInstrumentId);
      // assertNull("Previous payment instrument is no longer in the db.", pi);
      
      // getting an individual user
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setCompanyId(null);
      user.setAccountId(accountId);
      user.addRole(RoleType.IndividualAccountAdministrator);
      dbSetup.addUpdate(user);
      
      User userForLogin = new User();
      userForLogin.setEmail("dax@surepeople.com");
      userForLogin.setPassword("pwd123");
      
      authenticationHelper.doAuthenticate(session2, userForLogin);
      result = this.mockMvc
          .perform(
              post("/admin/account/updatePaymentInstrument").param("nameOnCard", "dax")
                  .param("cardNumber", "12345 12345 12344 4567").param("month", productId)
                  .param("year", "11").param("cvv", "123").param("country", "USA")
                  .param("zip", "12345").contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      log.debug("The MVC Response : " + result.getResponse().getContentType());
      account = dbSetup.getAccount(accountId);
      Assert.assertEquals("The payment instrument for the account !!!",
          account.getPaymentInstrumentId(), prevPaymentInstrumentId);
      // pi = dbSetup.getPaymentInstrument(prevPaymentInstrumentId);
      // assertNull("Previous payment instrument is no longer in the db.", pi);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testPaymentHistory() {
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAll("paymentInstrument");
    
    try {
      String productId = "2";
      String annualProductId = "1";
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { productId, "4" });
      
      // valid request with promotion
      Promotion promo = updatePromo(accountId, productId, 6.5);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/addMemberAccounts").param("productId", productId)
                  .param("numAccounts", "2").param("promotionId", "1000")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.payment").exists())
          .andExpect(jsonPath("$.success.payment.amount").value(13.0)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/admin/account/paymentHistory").contentType(MediaType.TEXT_PLAIN).session(
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
  public void testGetActiveUsers() {
    dbSetup.removeAllUsers();
    dbSetup.removeAllAccounts();
    dbSetup.createAccounts();
    dbSetup.removeAll("paymentInstrument");
    
    try {
      dbSetup.createUsers();
      
      String productId = "1";
      String accountId = "1";
      updateProductsAndPaymentInstrument(accountId, new String[] { productId, "4" });
      
      int empCount = (int) dbSetup.getMemberCount("1");
      int activeUserCount = 25 + empCount;
      
      MvcResult result = this.mockMvc
          .perform(
              post("/admin/account/getActiveMemberCount").contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.activeMembers").value(activeUserCount)).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
}
