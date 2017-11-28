package com.sp.web.controller.systemadmin.promotions;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.PromotionForm;
import com.sp.web.mvc.SPResponse;
import com.sp.web.promotions.PromotionStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.validation.Valid;

/**
 * @author Dax Abraham
 *
 *         This is the promotions controller to maintain and manage promotions data.
 */
@Controller
@PreAuthorize("hasRole('SuperAdministrator')")
public class PromotionsController {
  
  @Autowired
  private PromotionsControllerHelper helper;
  
  /**
   * Controller method to get all the promotions in the system.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the get promotions request
   */
  
  @RequestMapping(value = "/sysAdmin/promotions/createAffiliateCode", method = RequestMethod.GET)
  public String createCodeView() {
    return "createCode";
  }  
  @RequestMapping(value = "/sysAdmin/promotions/affiliateCodesListing", method = RequestMethod.GET)
  public String affiliateCodesView() {
    return "affiliateCodes";
  }    
  @RequestMapping(value = "/sysAdmin/promotions/affiliateCodeDetails", method = RequestMethod.GET)
  public String codeDetailsView() {
    return "codeDetails";
  }      
  
  @RequestMapping(value = "/sysAdmin/promotions/getAll", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllPromotions(Authentication token) {
    return process(helper::getAllPromotions, token);
  }

  /**
   * Controller method to get the promotions details for the given promotion id.
   * 
   * @param promotionId
   *          - promotion id
   * @param token
   *          - logged in user
   * @return
   *      the details of the promotion id
   */
  @RequestMapping(value = "/sysAdmin/promotions/getDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPromotionDetails(@RequestParam String promotionId,
      Authentication token) {
    return process(helper::getPromotionDetails, token, promotionId);
  }
  
  /**
   * Controller method to update the promotion status for the given promotion.
   * 
   * @param promotionId
   *           - the promotion id
   * @param promotionStatus
   *            - promotion status
   * @param token
   *            - logged in user
   * @return
   *      the response to the promotion update request
   */
  @RequestMapping(value = "/sysAdmin/promotions/updateStatus", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updatePromotionStatus(@RequestParam String promotionId,
      @RequestParam PromotionStatus promotionStatus, Authentication token) {
    return process(helper::updatePromotionStatus, token, promotionId, promotionStatus);
  }

  /**
   * Get the next promotion code.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the next promotion code
   */
  @RequestMapping(value = "/sysAdmin/promotions/getCode", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createPromotion(Authentication token) {
    return process(helper::getCode, token);
  }


  /**
   * Controller method to create the promotion.
   * 
   * @param promoForm
   *          - promotion form
   * @param productList
   *          - product list
   * @param token
   *          - logged in user
   * @return
   *      the response to the create request
   */
  @RequestMapping(value = "/sysAdmin/promotions/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createPromotion(@Valid PromotionForm promoForm,
      @RequestParam List<String> productList, Authentication token) {
    return process(helper::createPromotion, token, promoForm, productList);
  }

  /**
   * Controller method to update the promotion.
   * 
   * @param promoForm
   *          - promotion form
   * @param promotionId
   *          - promotion id
   * @param token
   *          - logged in user
   * @return
   *      the response to the update request
   */
  @RequestMapping(value = "/sysAdmin/promotions/update", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updatePromotion(@Valid PromotionForm promoForm,
      @RequestParam String promotionId, Authentication token) {
    return process(helper::updatePromotion, token, promoForm, promotionId);
  }

  /**
   * Controller method to get a list of accounts where the promotion is tagged to.
   * 
   * @param promotionId
   *          - promotion id
   * @param token
   *          - logged in user
   * @return
   *      the response to the get account request
   */
  @RequestMapping(value = "/sysAdmin/promotions/accounts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAccounts(@RequestParam String promotionId,
      Authentication token) {
    return process(helper::getAccounts, token, promotionId);
  }

  /**
   * Controller method to get the list of all the products.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the list of products
   */
  @RequestMapping(value = "/sysAdmin/promotions/getProducts", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllProducts(Authentication token) {
    return process(helper::getAllProducts, token);
  }
  
}
