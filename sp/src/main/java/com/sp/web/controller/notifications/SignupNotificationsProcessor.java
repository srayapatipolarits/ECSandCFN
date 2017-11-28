  package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.model.Account;
import com.sp.web.model.Address;
import com.sp.web.model.Company;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.payment.PaymentReasonType;
import com.sp.web.service.email.DataSourceAttahcment;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.pdf.ITextPDFCreatorService;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The signup notifications processor.
 */
@Component("signupNotificationsProcessor")
public class SignupNotificationsProcessor extends NotificationsProcessor {
  
  @Autowired
  ITextPDFCreatorService pdfCreatorService;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  private Environment enviornment;
  
  /**
   * The process helper.
   * 
   * @param type
   *          - notification type
   * @param user
   *          - user
   * @param paymentRecord
   *          - payment record
   */
  @Async
  public void process(NotificationType type, User user, Account account, PaymentRecord paymentRecord) {
    EmailParams emailParams = new EmailParams();
    emailParams.setTemplateName(type.getTemplateName());
    emailParams.setTos(user.getEmail());
    emailParams.addParam(Constants.PARAM_MEMBER, user);
    emailParams.addParam(Constants.PARAM_FOR_USER, user);
    emailParams.addParam(Constants.PARAM_ACCOUNT, account);
    Map<String, Object> params = new HashMap<String, Object>();
    
    if (StringUtils.isNotBlank(user.getCompanyId())) {
      Company company = accountRepository.findCompanyById(user.getCompanyId());
      String companyName = company.getName();
      Address address = company.getAddress();
      params.put("name", companyName);
      params.put("address", address);
      params.put("isBuisness", true);
      emailParams.addParam("organizationPlan", company.getFeatureList().contains(SPFeature.OrganizationPlan));
      emailParams.addParam("competency", company.getFeatureList().contains(SPFeature.Competency));
    } else {
      params.put("name", MessagesHelper.getMessage("user.name", user.getFirstName(), user.getLastName()));
      params.put("isBuisness", false);
    }
    params.put(Constants.PAYMENT_RECORD, paymentRecord);
    params.put(Constants.FEDRAL_TAX_ID, enviornment.getProperty("sp.fedral.taxId"));
    params.put("formattedAmount", String.format( "%.2f", paymentRecord.getAmount() ) );
    ByteArrayOutputStream bos = pdfCreatorService.createPDF(Constants.PAYMENT_RECEIPT_TEMPLATE,
        params, "default", PaymentReasonType.HISTORY_RECIEPT.toString());
    DataSourceAttahcment dataSourceAttahcment = new DataSourceAttahcment("SurePeople - Invoice - "
        + paymentRecord.getTxnId() + ".pdf", bos, Constants.CONTENT_TYPE_PDF);
    emailParams.addDataSourceAttachment(dataSourceAttahcment);
    
    process(emailParams, user, type, false);
  }
  
  protected LogRequest getActivityLogRequest(EmailParams emailParams, User user, User userFor,
      NotificationType type) {
    return null;
  }
  
}
