package com.sp.web.mvc.signup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.audit.Audit;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.dto.promotions.PromotionRegistrationDTO;
import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.exception.SPException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.SignupAccountForm;
import com.sp.web.form.SignupAssistedAccountForm;
import com.sp.web.form.SignupForm;
import com.sp.web.form.UserProfileForm;
import com.sp.web.form.password.PasswordForm;
import com.sp.web.model.Countries;
import com.sp.web.model.Product;
import com.sp.web.model.ProductType;
import com.sp.web.model.Promotion;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.ProductRepository;
import com.sp.web.promotions.PromotionsFactory;
import com.sp.web.promotions.PromotionsRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author daxabraham
 * 
 *         The controller for sign up.
 */
@Controller
@Scope("session")
public class SignupController implements Serializable {
  
  private static final long serialVersionUID = 3691578827008345234L;
  
  /*
   * The reference of the Logger class
   */
  private static final Logger LOG = Logger.getLogger(SignupController.class);
  
  private SignupForm signupForm;
  
  private AddressForm addressForm;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private ProductRepository productRepository;
  
  @Autowired
  private PromotionsRepository promotionsRepository;
  
  @Autowired
  private SignupHelper signupHelper;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  private PromotionsFactory promotionsFactory;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private ResourceLoader resourceLoader;
  
  private static final HashMap<String, String> countryListMap = new HashMap<String, String>();
  
  /**
   * Render a signup form to the person as HTML in their web browser.
   * Commented because this page is not used.
   */
  /*@RequestMapping(value = "/signup", method = RequestMethod.GET)
  public SignupForm signupForm(WebRequest request) {
    return new SignupForm();
  }*/
  
  /**
   * Render a signup form to the person as HTML in their web browser.
   * Commented as this is no longer used 
   */
  /*@RequestMapping(value = "/signupIndividual", method = RequestMethod.GET)
  public SignupForm signupFormIndividual(WebRequest request) {
    return new SignupForm();
  }*/
  
  /**
   * Method to sign up the individual user step 1.
   * 
   * @param signupForm
   *          - the sign up form for user and company details
   * @param addressForm
   *          - the company address
   * @param bindingResult
   *          - if there are any validation errors
   * @return the json response for the sign up request
   * Commented as this is no longer used
   */
  /*@RequestMapping(value = "/signup/individual", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupIndividualStep1(@Valid SignupForm signupForm,
      @Valid AddressForm addressForm, BindingResult bindingResult) {
    LOG.debug("The sign up form received :" + signupForm + ":" + addressForm + ":" + bindingResult);
    SPResponse spResponse = new SPResponse();
    
    if (bindingResult.hasErrors()) {
      addBindingErrors(bindingResult, spResponse);
    } else {
      // validate the email
      User user = userRepository.findByEmail(signupForm.getEmail());
      if (user != null) {
        spResponse.addError("Duplicate_Email",
            MessagesHelper.getMessage("exception.duplicateEmail.signup"));
      } else {
        this.signupForm = signupForm;
        this.addressForm = addressForm;
        spResponse.isSuccess();
      }
    }
    LOG.debug("Sending response :" + spResponse);
    return spResponse;
  }*/
  
  /**
   * Method to sign up the business user step 1.
   * 
   * @param signupForm
   *          - the sign up form for user and company details
   * @param addressForm
   *          - the company address
   * @param bindingResult
   *          - if there are any validation errors
   * @return the json response for the sign up request
   */
  @Audit("")
  @RequestMapping(value = "/signup/business", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signup(@Valid SignupForm signupForm, @Valid AddressForm addressForm,
      BindingResult bindingResult) {
    LOG.debug("The sign up form received :" + signupForm + ":" + addressForm + ":" + bindingResult);
    SPResponse spResponse = new SPResponse();
    
    if (bindingResult.hasErrors()) {
      for (FieldError err : bindingResult.getFieldErrors()) {
        spResponse.addError(err);
      }
    } else {
      // validate the email
      User user = userRepository.findByEmail(signupForm.getEmail());
      if (user != null) {
        spResponse.addError("Duplicate_Email",
            MessagesHelper.getMessage("exception.duplicateEmail.signup"));
      } else {
        // storing the sign up form in the session
        this.signupForm = signupForm;
        this.addressForm = addressForm;
        spResponse.isSuccess();
      }
    }
    LOG.debug("Sending response :" + spResponse);
    return spResponse;
  }
  
  /**
   * Controller method to validate the promotions.
   * 
   * @param code
   *          - promotion code
   * @param productId
   *          - product id
   * @return the response to the get promotion request
   */
  @RequestMapping(value = "/signup/promotion", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse validatePromotion(@RequestParam String code, @RequestParam String productId) {
    SPResponse spResponse = new SPResponse();
    try {
      Promotion promotion = promotionsRepository.findByCode(code);
      if (promotion != null) {
        try {
          promotionsFactory.validate(promotion, productId);
          spResponse.add("promotion", new PromotionRegistrationDTO(promotion));
        } catch (PromotionsValidationException e) {
          LOG.warn("Invalid promotions request code:" + code + ": productId :" + productId + "!!!",
              e);
          spResponse.addError("Promotion_Not_Found",
              MessagesHelper.getMessage("promotionCode.invalid"));
        }
      } else {
        spResponse.addError("Promotion_Not_Found",
            MessagesHelper.getMessage("promotionCode.invalid"));
      }
    } catch (Exception e) {
      LOG.warn("Error processing promotions !!!", e);
      spResponse.addError(new SPException(e));
    }
    return spResponse;
  }
  
  /**
   * Controller method for the get products.
   * 
   * @param productType
   *          - the product type
   * @return the response to the get product request
   */
  @RequestMapping(value = "/signup/getProducts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProducts(@RequestParam ProductType productType) {
    SPResponse spResponse = new SPResponse();
    
    String property = enviornment.getProperty("sp.active.products." + productType.toString());
    String[] productsArr = property.split(",");
    List<String> productsList = Arrays.asList(productsArr);
    List<Product> products = productRepository.findAllProductsById(productsList);
    if (products != null && products.size() != 0) {
      spResponse.add("products", products);
    } else {
      spResponse.addError("Product_Not_Found", "No products found for type :" + productType);
    }
    return spResponse;
  }
  
  /**
   * Controller method to validate the given email.
   * 
   * @param email
   *          - email to check
   * @return - sp response success if no email present else error
   */
  @RequestMapping(value = "/signup/validateEmail", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse validateEmail(@RequestParam String email) {
    SPResponse spResponse = new SPResponse();
    if (LOG.isDebugEnabled()) {
      LOG.debug("The email to validate :" + email);
    }
    // validate the email
    User user = userRepository.findByEmail(email); // signupForm.getEmail()
    if (user != null) {
      spResponse.addError("Duplicate_Email",
          MessagesHelper.getMessage("exception.duplicateEmail.signup"));
    } else {
      spResponse.isSuccess();
    }
    return spResponse;
  }
  
  /**
   * The controller method to signup business account.
   * 
   * @param accountForm
   *          - the account details form
   * @return - response for the signup
   */
  @RequestMapping(value = "/signup/business/account", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupBusinessAccount(@Valid SignupAccountForm accountForm,
      HttpServletRequest request) {
    SPResponse resp = new SPResponse();
    try {
      boolean success = false;
      if (this.signupForm != null && StringUtils.isNotEmpty(this.signupForm.getFirstName())) {
        success = signupHelper.signupBusiness(signupForm, addressForm, accountForm, request);
      }
      
      if (!success) {
        resp.addError("SP_SIGNUP_ERROR", "Could not signup account !!!");
      } else {
        resp.isSuccess();
      }
    } catch (SPException e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(e);
    } catch (Exception e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(new SPException(e));
    }
    return resp;
  }
  
  /**
   * This method signs up an individual user.
   * 
   * @param accountForm
   *          - the account details to signup
   * @return - SPResponse indicating success or failure of operation
   */
  @RequestMapping(value = "/signup/individual/account", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupIndividualAccount(@Valid SignupAccountForm accountForm,
      HttpServletRequest request) {
    SPResponse resp = new SPResponse();
    try {
      boolean success = signupHelper
          .signupIndividual(signupForm, addressForm, accountForm, request);
      if (!success) {
        resp.addError("SP_INDIVIDUAL_SIGNUP_ERROR", "Could not signup account !!!");
      } else {
        resp.isSuccess();
      }
    } catch (SPException e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(e);
    } catch (Exception e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(new SPException(e));
    }
    return resp;
  }
  
  /**
   * This method signs up an assisted business account.
   * 
   * @param assistedForm
   *          - the account details to signup
   * @return - SPResponse indicating success or failure of operation
   */
  @RequestMapping(value = "/signup/business/assisted/account", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupAssistedAccount(@Valid SignupAssistedAccountForm assistedForm) {
    SPResponse resp = new SPResponse();
    try {
      boolean success = signupHelper.signupAssisted(signupForm, assistedForm);
      if (!success) {
        resp.addError("SP_ASSISTED_SIGNUP_ERROR", "Could not signup account !!!");
      } else {
        resp.isSuccess();
      }
    } catch (SPException e) {
      LOG.warn("Could not sign up account !!!", e);
      resp.addError(e);
    }
    return resp;
  }
  
  /**
   * This method updates the member profile and then takes them to the next view.
   * 
   * @param userProfileForm
   *          - updated user profile
   * @param addressForm
   *          - update address form
   * @param password
   *          - user password
   * @param bindingResult
   *          - validation errors
   * @param session
   *          - http session
   * @return - the response for the update member
   */
  @RequestMapping(value = "/signup/member", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupMember(@Valid UserProfileForm userProfileForm,
      @Valid AddressForm addressForm, @Valid PasswordForm password,
      @RequestParam(required = false) String token, BindingResult bindingResult,
      HttpSession session, HttpServletResponse httpServletResponse) {
    
    SPResponse resp = new SPResponse();
    
    if (bindingResult.hasErrors()) {
      addBindingErrors(bindingResult, resp);
      return resp;
    }
    
    SPResponse response = ControllerHelper.doProcess(signupHelper::signupMember, userProfileForm,
        addressForm, password.getPassword(), session, token);
    return response;
    
  }
  
  /**
   * This method updates individual user profile when the user is created via alernate profile.
   * 
   * @param userProfileForm
   *          - updated user profile
   * @param addressForm
   *          - update address form
   * @param password
   *          - user password
   * @param bindingResult
   *          - validation errors
   * @param session
   *          - http session
   * @return - the response for the update member
   * Commented as this is no longer used 
   */
  /*@RequestMapping(value = "/signup/individual/member", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse signupIndividualMember(@Valid UserProfileForm userProfileForm,
      @Valid AddressForm addressForm, @Valid PasswordForm password,
      @RequestParam(required = false) String token, BindingResult bindingResult,
      HttpSession session, HttpServletResponse httpServletResponse) {
    
    SPResponse resp = new SPResponse();
    
    if (bindingResult.hasErrors()) {
      addBindingErrors(bindingResult, resp);
      return resp;
    }
    
    SPResponse response = ControllerHelper.doProcess(signupHelper::signupIndividualMember,
        userProfileForm, addressForm, password.getPassword(), session, token);
    return response;
    
  }*/
  
  /**
   * Helper method to add the field errors to the response.
   * 
   * @param bindingResult
   *          - the binding result
   * @param resp
   *          - the response
   * @return the updated response
   */
  private SPResponse addBindingErrors(BindingResult bindingResult, SPResponse resp) {
    bindingResult.getAllErrors().forEach(err -> resp.addError((FieldError) err));
    return resp;
  }
  
  /**
   * This method retuen country list.
   * 
   * @return - json country list
   */
  @RequestMapping(value = "/getCountries", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public SPResponse getCountries(@RequestParam(defaultValue = "en_US") String locale) {
    
    locale = LocaleHelper.isSupported(locale);
    
    String countryList = countryListMap.get(locale);
    
    if (countryList == null) {
      String fileName = null;
      if (Constants.DEFAULT_LOCALE.equalsIgnoreCase(locale)) {
        fileName = "countryList.json";
      } else {
        fileName = "countryList_" + locale.toString() + ".json";
      }
      
      ObjectMapper mapper = new ObjectMapper();
      try {
        Resource resource = resourceLoader.getResource("classpath:" + fileName);
        Countries readValue = mapper.readValue(resource.getFile(), Countries.class);
        countryList = mapper.writeValueAsString(readValue);
      } catch (Exception e) {
        LOG.error("error occurred retreving the country list for the locale " + locale + ": file :"
            + fileName, e);
        Resource resource = resourceLoader.getResource("classpath:countryList.json");
        Countries readValue;
        try {
          readValue = mapper.readValue(resource.getFile(), Countries.class);
          countryList = mapper.writeValueAsString(readValue);
        } catch (IOException e1) {
          LOG.error("Error occurred while getting the country list", e1);
        }
      }
      
      if (countryList == null) {
        countryList = MessagesHelper.getMessage("countries.list");
      } else {
        countryListMap.put(locale, countryList);
      }
    }
    return new SPResponse().add("countries", countryList);
  }
}
