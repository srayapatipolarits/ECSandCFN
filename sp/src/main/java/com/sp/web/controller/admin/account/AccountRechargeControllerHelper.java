package com.sp.web.controller.admin.account;

import com.sp.web.Constants;
import com.sp.web.account.AccountRechargeRepository;
import com.sp.web.account.AccountRepository;
import com.sp.web.dto.PaymentRecordDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.PaymentInstrumentForm;
import com.sp.web.form.SPPlanForm;
import com.sp.web.model.Account;
import com.sp.web.model.Address;
import com.sp.web.model.Company;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.PaymentType;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.payment.PaymentReasonType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.AccountFactory;
import com.sp.web.promotions.PromotionsFactory;
import com.sp.web.service.pdf.PDFCreatorService;
import com.sp.web.service.stringtemplate.StringTemplateFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Dax Abraham
 *
 *         The helper class for the account recharge controller class.
 */
@Component
public class AccountRechargeControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(AccountRechargeControllerHelper.class);
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  PromotionsFactory promotionsFactory;
  
  @Autowired
  AccountFactory accountFactory;
  
  @Autowired
  AccountRechargeRepository accountRechargeRepository;
  
  @Autowired
  @Qualifier("itextPdfService")
  private PDFCreatorService pdfCreatorService;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private StringTemplateFactory stringTemplateFactory;
  
  /**
   * <code>processAddMemberREquest</code> wil add the member to the account.
   * 
   * @param address
   *          logged in user address
   * @param productId
   *          of the product
   * @param promotionId
   *          promotion id
   * @param unitsToAdd
   *          to be added
   * @param paymentForm
   *          paymentform
   * @param account
   *          account
   * @return the payment record.
   */
  public PaymentRecord processAddMemberRequest(SPPlanForm planForm, SPPlanType planType,
      Account account) {
    // process the add request
    PaymentRecord record = accountFactory.processCandidateAdd(account, planForm, planType);
    return record;
  }
  
  /**
   * Updates the payment instrument for the account.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return response to the update request
   */
  public SPResponse updatePaymentInstrument(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the payment instrument form
    PaymentInstrumentForm piForm = (PaymentInstrumentForm) params[0];
    
    Account account = accountFactory.getAccount(user);
    
    SPPlan spPlan = account.getSpPlanMap().get(piForm.getPlanType());
    
    Assert.notNull(spPlan, "Invalid Plan type specified");
    /* in case of error, excpetion will be thrown. */
    PaymentInstrument pi = accountRepository.findPaymentInstrumentById(spPlan
        .getPreviousPaymentInstrumentId());
    if (pi != null) {
      piForm.update(pi);
      accountFactory.processUpdatePaymentInstrument(account, spPlan, pi, user.getAddress());
      resp.isSuccess();
    } else {
      resp.addError("error", "account not associated with credit card.");
    }
    
    // send success
    
    return resp;
  }
  
  /**
   * Get the payment history for the account.
   * 
   * @param user
   *          - logged in user
   * @return the payment history response
   */
  public SPResponse getPaymentHistory(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    SPPlanType spPlan = (SPPlanType) params[0];
    // get the account and also the payment history
    
    Account account = accountFactory.getAccount(user);
    
    /* check if valid account request */
    if (!account.getSpPlanMap().containsKey(spPlan)) {
      throw new SPException("Plan not subscribed by the account");
    }
    List<PaymentRecord> paymentRecordList = accountFactory.getPaymentHistory(account, spPlan);
    List<PaymentRecordDTO> paymentRespList = paymentRecordList.stream().map(PaymentRecordDTO::new)
        .collect(Collectors.toList());
    resp.add(Constants.PARAM_PAYMENT_HISTORY, paymentRespList);
    return resp;
  }
  
  /**
   * Return the pdf for the payment history.s
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the parameters.
   * @return the SPResponse.
   */
  public SPResponse getPaymentHistoryInvoicePdf(User user, Object[] params) {
    
    String paymentRecordId = (String) params[0];
    HttpServletResponse response = (HttpServletResponse) params[1];
    
    if (StringUtils.isBlank(paymentRecordId)) {
      throw new InvalidRequestException("Payment record id missing ");
    }
    
    PaymentRecord paymentRecord = accountRechargeRepository.findPaymentRecordById(paymentRecordId);
    
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    /* check the user type, in case of buisness user fetch the company */
    if (StringUtils.isNotBlank(user.getCompanyId())) {
      Company company = accountRepository.findCompanyById(user.getCompanyId());
      String companyName = company.getName();
      Address address = company.getAddress();
      paramsMap.put("name", companyName);
      paramsMap.put("address", address);
      paramsMap.put("isBuisness", true);
    } else {
      paramsMap.put("name",
          MessagesHelper.getMessage("user.name", user.getFirstName(), user.getLastName()));
      paramsMap.put("isBuisness", false);
    }
    
    paramsMap.put(Constants.PAYMENT_RECORD, paymentRecord);
    LOG.debug("Amout is " + paymentRecord.getAmount());
    paramsMap.put("formattedAmount", String.format("%.2f", paymentRecord.getAmount()));
    paramsMap.put(Constants.FEDRAL_TAX_ID, enviornment.getProperty("sp.fedral.taxId"));
    
    PaymentType paymentType = paymentRecord.getPaymentInstrument().getPaymentType();
    String paymentTypeFormatted = null;
    if (paymentType == null) {
      paymentTypeFormatted = MessagesHelper.getMessage("admin.account.CREDIT_CARD");
    } else {
      paymentTypeFormatted = MessagesHelper.getMessage("admin.account." + paymentType);
    }
    paramsMap.put(Constants.PARAM_PAYMENT_TYPE, paymentTypeFormatted);
    
    ByteArrayOutputStream baos = pdfCreatorService.createPDF(
        "/templates/email/payment/historyReceipt.stg", paramsMap, user.getCompanyId(),
        PaymentReasonType.HISTORY_RECIEPT.toString());
    response.setContentLength(baos.size());
    response.setContentType("application/pdf");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    response.setHeader("Pragma", "public");
    // write ByteArrayOutputStream to the ServletOutputStream
    
    try {
      OutputStream os = response.getOutputStream();
      baos.writeTo(os);
      os.flush();
      os.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
    
  }
}
