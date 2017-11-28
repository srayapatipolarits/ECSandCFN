/**
 * 
 */
package com.sp.web.model;


import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <code>CreditNote</code>
 * @author pradeepruhil
 *
 */
public class CreditNote {
  
  private String id;
  
  private PaymentType paymentType;
  
  private BigDecimal totalCredit;
  
  private BigDecimal creditBalance;
  
  private LocalDate createdTime;
  
  private String referenceNo;
  
  private String accountId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public BigDecimal getTotalCredit() {
    return totalCredit;
  }

  public void setTotalCredit(BigDecimal totalCredit) {
    this.totalCredit = totalCredit;
  }

  public BigDecimal getCreditBalance() {
    return creditBalance;
  }

  public void setCreditBalance(BigDecimal creditBalance) {
    this.creditBalance = creditBalance;
  }

  public LocalDate getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(LocalDate createdTime) {
    this.createdTime = createdTime;
  }

  public String getReferenceNo() {
    return referenceNo;
  }

  public void setReferenceNo(String referenceNo) {
    this.referenceNo = referenceNo;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
  
}
