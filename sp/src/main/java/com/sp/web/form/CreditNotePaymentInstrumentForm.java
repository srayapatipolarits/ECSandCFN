package com.sp.web.form;

import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentType;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The form to input the payment instrument information.
 */
public class CreditNotePaymentInstrumentForm extends PaymentInstrumentForm {
  
  /**
   * Payment details.
   */
  private double totalCredit;
  
  private PaymentType paymentType;
  
  private double creditBalance;
  
  private String cnr;
  
  public void setTotalCredit(double totalCredit) {
    this.totalCredit = totalCredit;
  }
  
  public double getTotalCredit() {
    return totalCredit;
  }
  
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  
  public double getCreditBalance() {
    return creditBalance;
  }
  
  /**
   * Updates the payment information with the given payment form details.
   * 
   * @param pi
   *          - payment instrument to update
   * @return the updated payment instrument
   */
  public PaymentInstrument update(PaymentInstrument pi) {
    /* get the ealier credit note balance */
    CreditNotePaymentInstrument creditNotePaymentInstrument = (CreditNotePaymentInstrument) pi;
    double previousCreditBalance = creditNotePaymentInstrument.getCreditBalance();
    BeanUtils.copyProperties(this, creditNotePaymentInstrument);
    creditNotePaymentInstrument.setCreditBalance(previousCreditBalance);
    creditNotePaymentInstrument.setFinancialReferenceNo(creditNotePaymentInstrument.getFinancialReferenceNo());
    return pi;
  }
  
  /**
   * Creates a new payment instrument from the information present in the form.
   * 
   * @return - payment instrument
   */
  public PaymentInstrument newPaymentInstrument() {
    return update(new CreditNotePaymentInstrument());
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setCnr(String cnr) {
    this.cnr = cnr;
  }
  
  public String getCnr() {
    return cnr;
  }
}
