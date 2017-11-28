/**
 * 
 */
package com.sp.web.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author pradeepruhil
 *
 */
@Document(collection = "paymentInstrument")
public class CreditNotePaymentInstrument extends PaymentInstrument {
  
  private double creditBalance;
  
  private LocalDateTime createTime;
  
  private String referenceNo;
  
  private String accountId;
  
  private String financialReferenceNo;
  
  public CreditNotePaymentInstrument() {
    
  }
  
  /**
   * Constructor.
   * @param notePaymentInstrument paymentInstrumentId 
   */
  public CreditNotePaymentInstrument(CreditNotePaymentInstrument notePaymentInstrument) {
    this.setCreditBalance(notePaymentInstrument.getCreditBalance());
    this.setPaymentType(notePaymentInstrument.getPaymentType());
    this.accountId = notePaymentInstrument.getAccountId();
    this.financialReferenceNo = notePaymentInstrument.getFinancialReferenceNo();
  }
  
  public double getCreditBalance() {
    return creditBalance;
  }
  
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  
  public LocalDateTime getCreateTime() {
    return createTime;
  }
  
  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
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
  
  /**
   * @param financialReferenceNo the financialReferenceNo to set
   */
  public void setFinancialReferenceNo(String financialReferenceNo) {
    this.financialReferenceNo = financialReferenceNo;
  }
  
  /**
   * @return the financialReferenceNo
   */
  public String getFinancialReferenceNo() {
    return financialReferenceNo;
  }
  
}
