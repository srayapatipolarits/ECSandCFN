package com.sp.web.form;

import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.account.SPPlanType;

import org.springframework.beans.BeanUtils;

/**
 * @author Dax Abraham
 * 
 *         The form to input the payment instrument information.
 */
public class PaymentInstrumentForm {
  
  /**
   * Payment details.
   */
  private String nameOnCard;
  private String cardNumber;
  private String month;
  private String year;
  private String cvv;
  private String country;
  private String zip;
  private SPPlanType planType;
  
  public String getNameOnCard() {
    return nameOnCard;
  }
  
  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }
  
  public String getCardNumber() {
    return cardNumber;
  }
  
  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }
  
  public String getMonth() {
    return month;
  }
  
  public void setMonth(String month) {
    this.month = month;
  }
  
  public String getYear() {
    return year;
  }
  
  public void setYear(String year) {
    this.year = year;
  }
  
  public String getCvv() {
    return cvv;
  }
  
  public void setCvv(String cvv) {
    this.cvv = cvv;
  }
  
  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public String getZip() {
    return zip;
  }
  
  public void setZip(String zip) {
    this.zip = zip;
  }
  
  /**
   * Updates the payment information with the given payment form details.
   * 
   * @param pi
   *          - payment instrument to update
   * @return the updated payment instrument
   */
  public PaymentInstrument update(PaymentInstrument pi) {
    BeanUtils.copyProperties(this, pi);
    return pi;
  }
  
  /**
   * Creates a new payment instrument from the information present in the form.
   * 
   * @return - payment instrument
   */
  public PaymentInstrument newPaymentInstrument() {
    return update(new PaymentInstrument());
  }
  
  public void setPlanType(SPPlanType planType) {
    this.planType = planType;
  }
  
  public SPPlanType getPlanType() {
    return planType;
  }
  
}
