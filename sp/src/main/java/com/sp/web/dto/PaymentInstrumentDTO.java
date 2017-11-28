package com.sp.web.dto;

import com.sp.web.model.Account;
import com.sp.web.model.CreditNotePaymentInstrument;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentType;
import com.sp.web.model.account.BillingCycle;
import com.sp.web.model.account.SPPlan;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The DTO bean for the payment instrument.
 */
public class PaymentInstrumentDTO {
  
  /* The type of card */
  private String cardType;
  /* the masked card number */
  private String maskedCardNumber;
  /* the last four digits of the card number */
  private String cardNumberLastFour;
  
  private double creditBalance;
  
  private PaymentType paymentType;
  
  private boolean creditCard;
  
  private String comment;
  
  private BillingCycle billingCycle;
  
  /**
   * Default Constructor.
   */
  public PaymentInstrumentDTO() {
  }
  
  /**
   * Constructor to copy the payment instrument information from the model bean.
   * 
   * @param paymentInstrument
   *          - payment instrument
   */
  public PaymentInstrumentDTO(PaymentInstrument paymentInstrument, Account account) {
    this(paymentInstrument);
    this.billingCycle = account.getBillingCycle();
  }
  
  /**
   * Constructor to copy the payment instrument information from the model bean.
   * 
   * @param paymentInstrument
   *          - payment instrument
   */
  public PaymentInstrumentDTO(PaymentInstrument paymentInstrument, SPPlan plan) {
    this(paymentInstrument);
    this.billingCycle = plan.getBillingCycle();
  }
  
  
  /**
   * Constructor to copy the payment instrument information from the model bean.
   * 
   * @param paymentInstrument
   *          - payment instrument
   */
  public PaymentInstrumentDTO(PaymentInstrument paymentInstrument) {
    
    BeanUtils.copyProperties(paymentInstrument, this);
    this.cardNumberLastFour = paymentInstrument.getCardNumberLastFour();
    if (this.cardNumberLastFour != null) {
      this.maskedCardNumber = MessagesHelper.getMessage("credit.card.masked.format",
          paymentInstrument.getCardNumberLastFour());
    }
    if (paymentInstrument instanceof CreditNotePaymentInstrument) {
      this.creditBalance = ((CreditNotePaymentInstrument) paymentInstrument).getCreditBalance();
    }
    this.paymentType = paymentInstrument.getPaymentType();
    this.comment = paymentInstrument.getComment();
    
  }
  
  public String getCardType() {
    return cardType;
  }
  
  public void setCardType(String cardType) {
    this.cardType = cardType;
  }
  
  public String getMaskedCardNumber() {
    return maskedCardNumber;
  }
  
  public void setMaskedCardNumber(String maskedCardNumber) {
    this.maskedCardNumber = maskedCardNumber;
  }
  
  public String getCardNumberLastFour() {
    return cardNumberLastFour;
  }
  
  public void setCardNumberLastFour(String cardNumberLastFour) {
    this.cardNumberLastFour = cardNumberLastFour;
  }
  
  public void setCreditBalance(double creditBalance) {
    this.creditBalance = creditBalance;
  }
  
  public double getCreditBalance() {
    return creditBalance;
  }
  
  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  public PaymentType getPaymentType() {
    if (paymentType == null && maskedCardNumber != null) {
      paymentType = PaymentType.CREDIT_CARD;
    }
    return paymentType;
  }
  
  /**
   * <code>isCreditCard</code> method return whether paymentType is credit card or not.
   * 
   * @return
   */
  public boolean getCreditCard() {
    if (getPaymentType() == PaymentType.CREDIT_CARD) {
      this.creditCard = true;
    }
    return creditCard;
  }
  
  public void setCreditCard(boolean isCreditCard) {
    if (getPaymentType() == PaymentType.CREDIT_CARD) {
      this.creditCard = true;
    }
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setBillingCycle(BillingCycle billingCycle) {
    this.billingCycle = billingCycle;
  }
  
  public BillingCycle getBillingCycle() {
    return billingCycle;
  }
}
