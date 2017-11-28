package com.sp.web.mvc.signup;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
//import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sp.web.model.Account;
import com.sp.web.model.ProductType;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.test.setup.DBSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;

public class SignupControllerTest extends SPTestBase {

  @Autowired
  MongoTemplate mongoTemplate;

  private DBSetup dbSetup;

  @Before
  public void setup() throws Exception {
    dbSetup = new DBSetup(mongoTemplate);
    testSmtp.start();
  }
  
  @After
  public void after() {
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    testSmtp.stop();
  }

  @Test
  public void testGetRegistrationForm() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/signup").contentType(MediaType.TEXT_PLAIN))
        .andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }

  @Test
  public void testRegistration() throws Exception {
    try {
      MvcResult result = this.mockMvc
          .perform(
              post("/signup/business").param("firstName", "Dax").param("lastName", "Abraham")
                  .param("title", "Director").param("email", "dax@surepeople.com")
                  .param("password", "abcd").param("company", "SurePeople").param("industry", "HR")
                  .param("noEmp", "10").param("country", "India").param("address1", "M-97, GK1")
                  .param("city", "Delhi").param("state", "Delhi").param("zip", "110048")
                  .param("phone", "9818399147").contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk()).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetProducts() throws Exception {
    dbSetup.removeAllProducts();

    MvcResult result = this.mockMvc
        .perform(
            post("/signup/getProducts").param("productType", ProductType.BUSINESS.toString())
                .contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").doesNotExist())
        .andExpect(
            jsonPath("$.error.Product_Not_Found").value("No products found for type :BUSINESS"))
        .andReturn();
    log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");

    // adding the products
    dbSetup.createProducts();
    result = this.mockMvc
        .perform(
            post("/signup/getProducts").param("productType", ProductType.BUSINESS.toString())
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success.products", hasSize(2))).andReturn();
    log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");
    dbSetup.removeAllProducts();
  }

  @Test
  public void testRegistrationInvalidParameter() throws Exception {
    MvcResult result = this.mockMvc.perform(
        post("/signup/business").param("firstName1", "Dax").param("lastName", "Abraham")
            .param("title", "Director").param("email", "dax@surepeople.com")
            .param("password", "abcd").param("company", "SurePeople").param("industry", "HR")
            .param("noEmp", "10").param("country", "India").param("address1", "M-97, GK1")
            .param("city", "Delhi").param("state", "Delhi").param("zip", "110048")
            .param("phone", "9818399147").contentType(MediaType.TEXT_PLAIN)).andReturn();

    BindingResult bindingResult = (BindingResult) result.getResolvedException();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString() + ":"
        + bindingResult.getFieldError().getField());

    assertTrue("Request has errors !!!", bindingResult.hasErrors());
    assertThat("Error messgae count is 1 !!!", bindingResult.getErrorCount(), equalTo(1));
    assertThat("The error is the fristName parameter cannot be null !!!", bindingResult
        .getFieldError().getField(), equalToIgnoringCase("firstName"));
    assertThat("The error is the fristName parameter cannot be null !!!", bindingResult
        .getFieldError().getDefaultMessage(), equalToIgnoringCase("may not be empty"));
  }

  @Test
  public void testGetPromotionsTest() throws Exception {
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();

    try {
      MvcResult result = this.mockMvc
          .perform(
              post("/signup/promotion").param("code", "test").param("productId", "1")
                  .contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").doesNotExist())
          .andExpect(
              jsonPath("$.error.Promotion_Not_Found").value("Invalid Affiliate code."))
          .andReturn();
      log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");

      dbSetup.createProducts();
      dbSetup.createsPromotions();

      result = this.mockMvc
          .perform(
              post("/signup/promotion").param("code", "testCode").param("productId", "1")
                  .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.promotion").exists())
          .andExpect(jsonPath("$.success.promotion.name").value("test"))
          .andExpect(jsonPath("$.success.promotion.status").value("Active")).andReturn();
      log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");

      // invalid test code
      result = this.mockMvc
          .perform(
              post("/signup/promotion").param("code", "testCodeInvalid").param("productId", "1")
                  .contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").doesNotExist())
          .andExpect(
              jsonPath("$.error.Promotion_Not_Found").value("Invalid Affiliate code."))
          .andReturn();
      log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");

      // invalid product id
      result = this.mockMvc
          .perform(
              post("/signup/promotion").param("code", "testCode").param("productId", "2")
                  .contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").doesNotExist())
          .andExpect(
              jsonPath("$.error.Promotion_Not_Found").value("Invalid Affiliate code."))
          .andReturn();
      log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");

      // inactive product
      result = this.mockMvc
          .perform(
              post("/signup/promotion").param("code", "testCode").param("productId", "3")
                  .contentType(MediaType.TEXT_PLAIN))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").doesNotExist())
          .andExpect(
              jsonPath("$.error.Promotion_Not_Found").value("Invalid Affiliate code."))
          .andReturn();
      log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");
    } catch (Exception e) {
      e.printStackTrace();
      fail("Something bad happened !!!");
    }
  }

  @Test
  public void testSignupBusinessAcount() throws Exception {
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();

    dbSetup.createProducts();
    dbSetup.createsPromotions();
    
    dbSetup.removeAllLogs();

    // signup form page 1 - user and company details

    MvcResult result = this.mockMvc
        .perform(
            post("/signup/business").session(session).param("firstName", "Dax")
                .param("lastName", "Abraham").param("title", "Director")
                .param("email", "dax@surepeople.com").param("password", "abcd")
                .param("company", "SurePeople").param("industry", "HR").param("numEmp", "10")
                .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                .param("state", "Delhi").param("zip", "110048").param("phone", "9818399147")
                .param("referSource", "Test source")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // signup form page 2 - account, promotions and payment details

    result = this.mockMvc
        .perform(
            post("/signup/business/account").session(session).param("productId", "1")
                .param("promotionId", "1").param("numEmp", "20").param("chargeAmount", "120")
                .param("nameOnCard", "Dax").param("cardNumber", "370000000000002").param("month", "12")
                .param("year", "18").param("cvv", "900").param("country", "India")
                .param("zip", "110048").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // try to create the account again

    result = this.mockMvc
        .perform(
            post("/signup/business/account").session(session).param("productId", "1")
                .param("promotionId", "1").param("numEmp", "20").param("chargeAmount", "121")
                .param("nameOnCard", "Dax").param("cardNumber", "370000000000002").param("month", "12")
                .param("year", "18").param("cvv", "1234").param("country", "India")
                .param("zip", "110048").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidRequestException").value("User already exists in the system."))
        .andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    
    List<Account> all = dbSetup.getAll(Account.class);
    Account account = all.get(0);
    assertThat(account.getAvailableSubscriptions(), equalTo(19));

  }

  @Test
  public void testGetProductsIndividual() throws Exception {
    dbSetup.removeAllProducts();

    MvcResult result = this.mockMvc
        .perform(
            post("/signup/getProducts").param("productType", ProductType.INDIVIDUAL.toString())
                .contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").doesNotExist())
        .andExpect(
            jsonPath("$.error.Product_Not_Found").value("No products found for type :INDIVIDUAL"))
        .andReturn();
    log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");

    // adding the products
    dbSetup.createProducts();

    result = this.mockMvc
        .perform(
            post("/signup/getProducts").param("productType", ProductType.INDIVIDUAL.toString())
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success.products", hasSize(1))).andReturn();
    log.debug("The products returned are :" + result.getResponse().getContentAsString() + ":");
    dbSetup.removeAllProducts();
  }

  @Test
  public void testSignupIndividualAcount() throws Exception {
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();

    dbSetup.createProducts();
    dbSetup.createsPromotions();

    // signup form page 1 - user and company details

    MvcResult result = this.mockMvc
        .perform(
            post("/signup/individual").session(session).param("firstName", "Dax")
                .param("lastName", "Abraham").param("title", "Director")
                .param("email", "dax@surepeople.com").param("password", "abcd")
                .param("month", "09").param("day", "09")
                .param("year", "1977").param("gender", "M").param("country", "India")
                .param("address1", "M-97, GK1").param("city", "Delhi").param("state", "Delhi")
                .param("zip", "110048").param("phone", "9818399147")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // signup form page 2 - account, promotions and payment details
    // error due to business product trying to apply to individuals

    result = this.mockMvc
        .perform(
            post("/signup/individual/account").session(session).param("productId", "1")
                .param("promotionId", "1").param("numEmp", "1").param("chargeAmount", "19")
                .param("nameOnCard", "Dax").param("cardNumber", "370000000000002").param("month", "12")
                .param("year", "2017").param("cvv", "1234").param("country", "India")
                .param("zip", "110048").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.SignupException").value(
                "Product :1: is not valid for individuals !!!")).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // signup form page 2 - account, promotions and payment details
    // error due to business promotion trying to apply to individuals

    result = this.mockMvc
        .perform(
            post("/signup/individual/account").session(session).param("productId", "6")
                .param("promotionId", "1").param("numEmp", "1").param("chargeAmount", "39")
                .param("nameOnCard", "Dax").param("cardNumber", "370000000000002").param("month", "12")
                .param("year", "2018").param("cvv", "1234").param("country", "India")
                .param("zip", "110048").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(
            jsonPath("$.error.PromotionsValidationException").value(
                "The promotion not applicable for product :6 !!!")).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // signup form page 2 - account, promotions and payment details

    result = this.mockMvc
        .perform(
            post("/signup/individual/account").session(session).param("productId", "5").param("promotionId", "1")
                .param("numEmp", "1").param("chargeAmount", "99").param("nameOnCard", "Dax")
                .param("cardNumber", "370000000000002").param("month", "12").param("year", "2017")
                .param("cvv", "1234").param("country", "India").param("zip", "110048")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // try to create the account again

    result = this.mockMvc
        .perform(
            post("/signup/individual/account").session(session).param("productId", "5")
                .param("numEmp", "1").param("chargeAmount", "99").param("nameOnCard", "Dax")
                .param("cardNumber", "370000000000002").param("month", "12").param("year", "2015")
                .param("cvv", "1234").param("country", "India").param("zip", "110048")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.error.InvalidRequestException").value("User already exists in the system."))
        .andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }

  @Test
  public void testSignupAssistedAcount() throws Exception {
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();
    dbSetup.removeAllAssistedUsers();

    dbSetup.createProducts();
    dbSetup.createsPromotions();

    // signup form page 1 - user and company details

    MvcResult result = this.mockMvc
        .perform(
            post("/signup/business").session(session).param("firstName", "Dax")
                .param("lastName", "Abraham").param("title", "Director")
                .param("email", "dax@surepeople.com").param("password", "abcd")
                .param("company", "SurePeople").param("industry", "HR").param("noEmp", "10")
                .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                .param("state", "Delhi").param("zip", "110048").param("phone", "9818399147")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // signup form page 2 - account, promotions and payment details
    // assisted no user name
    result = this.mockMvc
        .perform(
            post("/signup/business/assisted/account").session(session).contentType(
                MediaType.TEXT_PLAIN)).andExpect(status().is4xxClientError()).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    // signup form page 2 - account, promotions and payment details
    // assisted no user name
    result = this.mockMvc
        .perform(
            post("/signup/business/assisted/account").session(session).param("name", "Dax Abraham")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().is4xxClientError())
        .andReturn();

    // company not present
    result = this.mockMvc
        .perform(
            post("/signup/business/assisted/account").session(session).param("name", "Dax Abraham")
                .param("phone", "9818399147").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().is4xxClientError()).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    result = this.mockMvc
        .perform(
            post("/signup/business/assisted/account").session(session).param("name", "Dax Abraham")
                .param("company", "SurePeople").param("numEmp", "700").param("phone", "9818399147")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());

    dbSetup.removeAllAssistedUsers();
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();
  }
  
  @Test
  public void testSignUpInvalidCardScenario() throws Exception{
    dbSetup.removeAllPromotions();
    dbSetup.removeAllProducts();
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.removeAllInstruments();
    dbSetup.removeAllAssistedUsers();

    dbSetup.createProducts();
    dbSetup.createsPromotions();
    // signup form page 1 - user and company details

    MvcResult result = this.mockMvc
        .perform(
            post("/signup/business").session(session).param("firstName", "Dax")
                .param("lastName", "Abraham").param("title", "Director")
                .param("email", "dax@surepeople.com").param("password", "abcd")
                .param("company", "SurePeople").param("industry", "HR").param("noEmp", "10")
                .param("country", "India").param("address1", "M-97, GK1").param("city", "Delhi")
                .param("state", "Delhi").param("zip", "110048").param("phone", "9818399147")
                .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk()).andReturn();
                
      result = this.mockMvc
        .perform(
            post("/signup/business/account").session(session).param("productId", "1")
                .param("promotionId", "1").param("numEmp", "20").param("chargeAmount", "120")
                .param("nameOnCard", "Dax").param("cardNumber", "4222222222222").param("month", "12")
                .param("year", "18").param("cvv", "901").param("country", "India")
                .param("zip", "110048").contentType(MediaType.TEXT_PLAIN))
        .andExpect(status().isOk()).andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true")).andReturn();

    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
 
  }

  @Test
  public void testGetCountries() {
    try {
      MvcResult result = this.mockMvc
          .perform(
              get("/getCountries")
                  .session(session)
                  .contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
                  .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
 
  }  
}
