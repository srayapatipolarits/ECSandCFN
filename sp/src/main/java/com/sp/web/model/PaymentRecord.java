package com.sp.web.model;

import com.sp.web.dto.PaymentInstrumentDTO;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;

import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author Dax Abraham
 * 
 *         The model bean to store the payment record.
 */
public class PaymentRecord {

  private String id;
  private Date createdOn;
  private PaymentInstrumentDTO paymentInstrument;
  private String accountId;
  @Deprecated
  private String productId;
  @Deprecated
  private String promotionId;
  private double amount;
  private String reason;
  private String txnId;
  private long orderNumber;
  private String cardType;
  private SPPlanType planType;

  /**
   * Default.
   */
  public PaymentRecord() {
  }

  /**
   * Constructor.
   * 
   * @param account
   * 
   * @param request
   *          - payment request
   */
  public PaymentRecord(Account account, SPPlan plan, PaymentGatewayRequest request, PaymentGatewayResponse response) {
    BeanUtils.copyProperties(request, this);
    if (response != null) {
      BeanUtils.copyProperties(response, this);  
    }
    this.paymentInstrument = new PaymentInstrumentDTO(request.getInstrument());
    createdOn = DateTime.now().toDate();
    this.accountId = account.getId();
    this.planType = plan.getPlanType();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public PaymentInstrumentDTO getPaymentInstrument() {
    return paymentInstrument;
  }

  public void setPaymentInstrument(PaymentInstrumentDTO paymentInstrument) {
    this.paymentInstrument = paymentInstrument;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(String promotionId) {
    this.promotionId = promotionId;
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

  public String getTxnId() {
    return txnId;
  }

  public void setTxnId(String txnId) {
    this.txnId = txnId;
  }

  public long getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(long orderNumber) {
    this.orderNumber = orderNumber;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public String getCardType() {
    return cardType;
  }
  
  public void setPlanType(SPPlanType planType) {
    this.planType = planType;
  }
  
  public SPPlanType getPlanType() {
    return planType;
  }
  
}
