package com.sp.web.model;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;

/**
 * This is the model class that holds the payment details.
 * 
 * @author Dax Abraham
 */
@Document(collection = "paymentInstrument")
public class PaymentInstrument {

  private String id;
  private String nameOnCard;
  private String cardNumber;
  private String month;
  private String year;
  private String cvv;
  private String country;
  private String zip;
  private String cardType;
  private String cardNumberLastFour;

  private String authroziedNetPaymentProfileId;
  
  private PaymentType paymentType;
  
  private String comment;

  public String getNameOnCard() {
    return nameOnCard;
  }

  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  /**
   * Set the card number also set the card number last four digit.
   * 
   * @param cardNumber
   *          - card number
   */
  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    Optional.ofNullable(cardNumber).filter(cn -> cn.length() > 4).ifPresent(cn -> {
      cardNumberLastFour = cn.substring(cardNumber.length() - 4);
    });
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public String getCardNumberLastFour() {
    return cardNumberLastFour;
  }

  /**
   * @param authroziedNetPaymentProfileId
   *          the authroziedNetPaymentProfileId to set.
   */
  public void setAuthroziedNetPaymentProfileId(String authroziedNetPaymentProfileId) {
    this.authroziedNetPaymentProfileId = authroziedNetPaymentProfileId;
  }

  public String getAuthroziedNetPaymentProfileId() {
    return authroziedNetPaymentProfileId;
  }
  
  public PaymentType getPaymentType() {
    return paymentType;
  }
  
  /**
   * @param paymentType the paymentType to set the payment type.
   */
  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void updatePaymentInstrument(PaymentInstrument instrument){
    BeanUtils.copyProperties(instrument, this);
  }
}
