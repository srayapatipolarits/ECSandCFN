package com.sp.web.model.payment;

/**
 * <code>PaymentReasonType</code> enum contains the reason which will be used in the payment invoice and payment
 * history.
 * 
 * @author pradeepruhil
 *
 */
public enum PaymentReasonType {

  ADD_MEMBER("addMemberReason.stg"),

  ADD_CANDIDATE("hriringReason.stg"),
  
  NEW_ACCOUNT("newAccountReason.stg"),
  
  UPDATE_ACCOUNT("updateAccountReason.stg"),
  
  NEW_PLAN("newPlan.stg"),

  RECHARGE_ACCOUNT("rechargeReason.stg"),
  
  HISTORY_RECIEPT("historyReceipt.stg");
  ;
  
  

  String templateName;

  private static final String TEMPLATE_FOLDER = "templates/email/payment/";

  PaymentReasonType(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return TEMPLATE_FOLDER + templateName;
  }

}
