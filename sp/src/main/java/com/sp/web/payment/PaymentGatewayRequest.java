package com.sp.web.payment;

import com.sp.web.model.PaymentInstrument;

/**
 * @author Dax Abraham
 * 
 *         The request bean for the payment gateway.
 */
public class PaymentGatewayRequest {
  
  private double amount;
  private String reason;
  private PaymentInstrument instrument;
  @Deprecated
  private String productId;
  @Deprecated
  private String promotionId;
  private String invoiceNumber;
  private String refId;
  private String customerId;
  private String authorizedNetProfileId;
  private double creditBalance;
  
  /**
   * Constructor.
   * 
   * @param reason
   *          - reason for the payment
   * @param amount
   *          - payment amount
   * @param instrument
   *          - payment instrument
   */
  @Deprecated
  public PaymentGatewayRequest(String reason, double amount, PaymentInstrument instrument,
      String productId, String promotionId) {
    this.reason = reason;
    this.amount = amount;
    this.instrument = instrument;
    this.productId = productId;
    this.promotionId = promotionId;
  }
  
  /**
   * Constructor for the payment gateway request.
   * 
   * @param reasonDescription
   *          for the payment gateway instrument.
   * @param chargeAmount
   *          amount to be charged.
   * @param pi
   *          payment instrument.
   * @param creditBalance
   *          credit balance of the account.
   */
  public PaymentGatewayRequest(String reasonDescription, double chargeAmount, PaymentInstrument pi, double creditBalance) {
    this.reason = reasonDescription;
    this.amount = chargeAmount;
    this.instrument = pi;
    this.creditBalance = creditBalance;
  }
  
  public double getAmount() {
    return amount;
  }
  
  public void setAmount(double amount) {
    this.amount = amount;
  }
  
  public String getReason() {
    return reason;
  }
  
  public void setReason(String reason) {
    this.reason = reason;
  }
  
  public PaymentInstrument getInstrument() {
    return instrument;
  }
  
  public void setInstrument(PaymentInstrument instrument) {
    this.instrument = instrument;
  }
  
  @Deprecated
  public String getProductId() {
    return productId;
  }
  
  @Deprecated
  public void setProductId(String productId) {
    this.productId = productId;
  }
  
  @Deprecated
  public String getPromotionId() {
    return promotionId;
  }
  
  @Deprecated
  public void setPromotionId(String promotionId) {
    this.promotionId = promotionId;
  }
  
  public String getInvoiceNumber() {
    return this.invoiceNumber;
  }
  
  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }
  
  public void setRefId(String refId) {
    this.refId = refId;
  }
  
  public String getRefId() {
    return refId;
  }
  
  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }
  
  public String getCustomerId() {
    return customerId;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "PaymentGatewayRequest [amount=" + amount + ", reason=" + reason + ", instrument="
        + instrument + ", productId=" + productId + ", promotionId=" + promotionId
        + ", invoiceNumber=" + invoiceNumber + ", refId=" + refId + ", customerId=" + customerId
        + "]";
  }
  
  public String getAuthorizedNetProfileId() {
    return authorizedNetProfileId;
  }
  
  public void setAuthorizedNetProfileId(String authorizedNetProfileId) {
    this.authorizedNetProfileId = authorizedNetProfileId;
  }
  
  /**
   * @param creditBalance the creditBalance to set
   */
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  /**
   * @return the creditBalance
   */
  public double getCreditBalance() {
    return creditBalance;
  }
}
