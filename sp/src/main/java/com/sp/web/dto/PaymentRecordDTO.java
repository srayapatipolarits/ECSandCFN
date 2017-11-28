package com.sp.web.dto;

import com.sp.web.model.PaymentRecord;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for the payment record.
 */
public class PaymentRecordDTO {

  /** Adding id, to download pdf in payment history page for this payment record */
  private String id;
  private Date createdOn;
  private PaymentInstrumentDTO paymentInstrument;
  private String accountId;
  private String productId;
  private String promotionId;
  private double amount;
  private String reason;
  private String txnId;

  public PaymentRecordDTO(PaymentRecord record) {
    BeanUtils.copyProperties(record, this);
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public String getCreatedOnFormatted() {
    return MessagesHelper.formatDate(createdOn);
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

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

}
