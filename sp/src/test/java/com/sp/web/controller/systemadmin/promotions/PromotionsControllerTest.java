package com.sp.web.controller.systemadmin.promotions;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.model.Account;
import com.sp.web.model.Promotion;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.promotions.PromotionStatus;
import com.sp.web.utils.MessagesHelper;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

public class PromotionsControllerTest extends SPTestLoggedInBase {
  
  @Test
  public void testGetAllPromotions() {
    try {
      dbSetup.removeAllPromotions();
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.promotion", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createsPromotions();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getAll")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.promotion", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testGetPromotionDetails() {
    try {
      dbSetup.removeAllPromotions();
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getDetails")
              .param("promotionId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Promotion not found for id:1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createsPromotions();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getDetails").param("promotionId", "1")
                  .contentType(MediaType.TEXT_PLAIN).session(session))
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
  public void testUpdatePromotionStatus() {
    try {
      dbSetup.removeAllPromotions();
      String promotionId = "1";
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/updateStatus")
              .param("promotionId", promotionId)
              .param("promotionStatus", "Active")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Promotion not found for id:1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createsPromotions();
      
      Promotion promo = dbSetup.getPromotion(promotionId);
      assertThat(promo.getStatus(), is(PromotionStatus.Active));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/updateStatus")
              .param("promotionId", promotionId)
              .param("promotionStatus", "InActive")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
            
      promo = dbSetup.getPromotion(promotionId);
      assertThat(promo.getStatus(), is(PromotionStatus.InActive));

      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/updateStatus")
              .param("promotionId", promotionId)
              .param("promotionStatus", "Suspended")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      promo = dbSetup.getPromotion(promotionId);
      assertThat(promo.getStatus(), is(PromotionStatus.Suspended));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testNextPromotionCode() {
    try {
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getCode")
              .contentType(MediaType.TEXT_PLAIN).session(session))
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
  public void testCreatePromotion() {
    try {
      dbSetup.removeAllPromotions();
      
      LocalDate dateTime = LocalDate.now();
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/create")
              .param("startDate", MessagesHelper.formatDate(dateTime, "MM/dd/yyyy"))
              .param("endDate", MessagesHelper.formatDate(dateTime.plusMonths(5), "MM/dd/yyyy"))
              .param("message", "Testing 123")
              .param("promotionType", "TIME_BASED")
              .param("status", "Active")
              .param("code", "1")
              .param("unitPrice", "10")
              .param("productList", "")
              .param("tags", "tag1, tag2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("At least one product required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/create")
              .param("startDate", MessagesHelper.formatDate(dateTime, "MM/dd/yyyy"))
              .param("endDate", MessagesHelper.formatDate(dateTime.plusMonths(5), "MM/dd/yyyy"))
              .param("message", "Testing 123")
              .param("promotionType", "TIME_BASED")
              .param("status", "Active")
              .param("code", "1")
              .param("unitPrice", "10")
              .param("productList", "999")
              .param("tags", "tag1, tag2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Invalid product list."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      

      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/create")
              .param("startDate", MessagesHelper.formatDate(dateTime, "MM/dd/yyyy"))
              .param("endDate", MessagesHelper.formatDate(dateTime.plusMonths(5), "MM/dd/yyyy"))
              .param("message", "Testing 123")
              .param("promotionType", "TIME_BASED")
              .param("status", "Active")
              .param("code", "1")
              .param("unitPrice", "10")
              .param("productList", "1")
              .param("tags", "tag1, tag2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());      
      
      List<Promotion> all = dbSetup.getAll(Promotion.class);
      assertThat(all.get(0).getCode(), is("1"));
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/create")
              .param("startDate", MessagesHelper.formatDate(dateTime, "MM/dd/yyyy"))
              .param("endDate", MessagesHelper.formatDate(dateTime.plusMonths(5), "MM/dd/yyyy"))
              .param("message", "Testing 123")
              .param("promotionType", "TIME_BASED")
              .param("status", "Active")
              .param("code", "1")
              .param("unitPrice", "10")
              .param("productList", "1")
              .param("productList", "2")
              .param("tags", "tag1, tag2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.DuplicateKeyException").value("Could not create promotion."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  @Test
  public void testUpdatePromotion() {
    try {
      dbSetup.removeAllPromotions();
      
      LocalDate dateTime = LocalDate.now();
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/create")
              .param("startDate", MessagesHelper.formatDate(dateTime, "MM/dd/yyyy"))
              .param("endDate", MessagesHelper.formatDate(dateTime.plusMonths(5), "MM/dd/yyyy"))
              .param("message", "Testing 123")
              .param("promotionType", "TIME_BASED")
              .param("status", "Active")
              .param("code", "1")
              .param("unitPrice", "10")
              .param("productList", "1, 2")
              .param("tags", "tag1, tag2")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<Promotion> all = dbSetup.getAll(Promotion.class);
      Promotion promotion = all.get(0);

      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/update")
              .param("promotionId", promotion.getId())
              .param("startDate", MessagesHelper.formatDate(dateTime, "MM/dd/yyyy"))
              .param("endDate", MessagesHelper.formatDate(dateTime.plusMonths(5), "MM/dd/yyyy"))
              .param("message", "Testing 321")
              .param("promotionType", "TIME_BASED")
              .param("status", "Active")
              .param("code", "1")
              .param("unitPrice", "10")
              .param("productList", "1, 3")
              .param("tags", "tag1, tag2, tag3")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      all = dbSetup.getAll(Promotion.class);
      promotion = all.get(0);
      assertThat(promotion.getCode(), is("1"));
      assertThat(promotion.getMessage(), is("Testing 321"));
      assertThat(promotion.getTags(), hasSize(3));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
      
  @Test
  public void testGetAccountList() {
    try {
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllPromotions();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/accounts")
              .param("promotionId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Promotion not found for id:1"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createAccounts();
      dbSetup.createsPromotions();
      dbSetup.createCompanies();
      
      User user = dbSetup.getUser("individual@surepeople.com");
      user.setAccountId("2");
      dbSetup.addUpdate(user);
      final Promotion promotion = dbSetup.getPromotion("1");
      List<Account> accountList = dbSetup.getAll(Account.class);
      accountList.forEach(a -> {
          a.addPromotion(promotion);
          dbSetup.addUpdate(a);
        });
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/accounts")
              .param("promotionId", "1")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.account", hasSize(6)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  
  @Test
  public void testGetAllProducts() {
    try {
      dbSetup.removeAllCompanies();
      dbSetup.removeAllAccounts();
      dbSetup.removeAllPromotions();
      dbSetup.removeAllProducts();
      
      MvcResult result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getProducts")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      dbSetup.createAccounts();
      dbSetup.createsPromotions();
      dbSetup.createCompanies();
      dbSetup.createProducts();
      
      result = this.mockMvc
          .perform(
              post("/sysAdmin/promotions/getProducts")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.product", hasSize(7)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }    
}
