package com.sp.web.controller.admin.account;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.form.PaymentInstrumentForm;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.AccountFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Dax Abraham
 * 
 *         The controller to help with account recharge.
 */
@Controller
public class AccountRechargeController {
  
  @Autowired
  AccountRechargeControllerHelper helper;
  
  @Autowired
  AccountFactory accountFactory;
  
  /**
   * Controller method to get the details of the hiring product.
   * 
   * @param token
   *          - logged in user
   * @return the response to the get request
   */
  @RequestMapping(value = "/admin/account/renew/individual", method = RequestMethod.GET)
  public String getCurrentIndividualProductInfoView() {
    return "getCurrentIndividualProductInfo";
  }
  
  @RequestMapping(value = "/admin/account/renew", method = RequestMethod.GET)
  public String renewAccountView() {
    return "renewAccount";
  }
  
  @RequestMapping(value = "/admin/account/changeSubscription", method = RequestMethod.GET)
  public String changeSubscriptionView() {
    return "changeSubscription";
  }
  
  @RequestMapping(value = "/admin/account/payment", method = RequestMethod.GET)
  public String processPaymentView() {
    return "processPayment";
  }
  
  @RequestMapping(value = "/admin/account/payment/history", method = RequestMethod.GET)
  public String getPaymentHistoryView() {
    return "getPaymentHistory";
  }
  
  @RequestMapping(value = "/admin/alternateAccount/payment/historyindividual", method = RequestMethod.GET)
  public String getPaymentHistoryalternateView() {
    return "getPaymentHistoryalternate";
  }
  
  @RequestMapping(value = "/admin/alternateAccount/payment/historybusiness", method = RequestMethod.GET)
  public String getPaymenthistorybusinessView() {
    return "getPaymentHistoryalternate";
  }
  
  @RequestMapping(value = "/admin/account/edit/creditcard", method = RequestMethod.GET)
  public String updatePaymentInstrumentView() {
    return "updatePaymentInstrument";
  }
  
  @RequestMapping(value = "/admin/account/add/memberaccounts", method = RequestMethod.GET)
  public String addHiringCandidatesView() {
    return "addHiringCandidates";
  }
  
  @RequestMapping(value = "/admin/account/add/memberalternativeaccounts", method = RequestMethod.GET)
  public String addAlternativeHiringCandidates() {
    return "addAlternativeHiringCandidates";
  }
  
  @RequestMapping(value = "/admin/account/add/candidatealternativeaccounts", method = RequestMethod.GET)
  public String getAlternateHiringProductInfo() {
    return "getAlternateHiringProductInfo";
  }
  
  @RequestMapping(value = "/admin/account/add/addCredit", method = RequestMethod.GET)
  public String addCredit() {
    return "addCredit";
  }
  
  @RequestMapping(value = "/admin/account/add/addCreditIndividual", method = RequestMethod.GET)
  public String addCreditIndividual() {
    return "addCreditIndividual";
  }
  
  @RequestMapping(value = "/admin/account/add/candidateaccounts", method = RequestMethod.GET)
  public String getHiringProductInfoView() {
    return "getHiringProductInfo";
  }
  
  /**
   * Update the payment instrument for the account.
   * 
   * @param paymentForm
   *          - payment form
   * @param token
   *          - logged in user
   * @return the response to the update request
   */
  @RequestMapping(value = "/admin/account/updatePaymentInstrument", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updatePaymentInstrument(@Valid PaymentInstrumentForm paymentForm,
      Authentication token) {
    // process the request
    return process(helper::updatePaymentInstrument, token, paymentForm);
  }
  
  /**
   * Get the payment history for the account.
   * 
   * @param token
   *          - logged in user
   * @return the response to the payment history
   */
  @RequestMapping(value = "/admin/account/paymentHistory", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getPaymentHistory(@RequestParam SPPlanType spPlanType, Authentication token) {
    // process the request
    return process(helper::getPaymentHistory, token, spPlanType);
  }
  
  /**
   * <code>getPaymentHistoryInvoicePdf</code> method wil download the pdf invoice the service.
   * 
   * @param paymentId
   *          for which pdf to be downloaded.
   */
  @RequestMapping(value = "/admin/account/paymentHistory/invoice/{paymentId}")
  public void getPaymentHistoryInvoicePdf(@PathVariable String paymentId, Authentication token,
      HttpServletResponse httpServletResponse) {
    process(helper::getPaymentHistoryInvoicePdf, token, paymentId, httpServletResponse);
  }
  
}
