package com.sp.web.product;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Account;
import com.sp.web.model.Product;
import com.sp.web.model.ProductValidityType;
import com.sp.web.model.Promotion;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         This class implements the common product helper functionality.
 */
public class ProductHelperCommon {
  
  private static final Logger log = Logger.getLogger(ProductHelperCommon.class);

  /**
   * The expiry time for the product.
   * 
   * @param product
   *          - product
   * @param account
   *          - account
   * @return
   *      the expiry date
   */
  public DateTime getExpiresTime(Product product, Account account) {
    DateTime start = new DateTime(account.getBillingCycleStartDate());
    return start.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12);
  }

  /**
   * Calculates the amount to charge for the given product, promotion and purchase units.
   * 
   * @param account
   *          - account
   * @param product
   *          - product
   * @param promotion
   *          - promotion
   * @param purchaseUnits
   *          - purchase units
   * @return
   *          amount to charge
   */
  public Double getChargeAmount(Account account, Product product, Optional<Promotion> promotion,
      long purchaseUnits) {
    
    double unitPrice = promotion.map(Promotion::getUnitPrice).orElse(product.getUnitPrice());
    
    final ProductValidityType validityType = Optional.ofNullable(product.getValidityType()).orElseThrow(
        () -> new InvalidRequestException("Validity type not set for product :" + product.getId()));
    
    double multiplier = 0;
    // get the next recharge date
    DateTime nextRechargeDate = Optional.ofNullable(new DateTime(account.getNextPaymentDate()))
        .orElse(DateTime.now());
    DateTime today = DateTime.now();
    
    Duration duration = new Duration(today, nextRechargeDate);

    if (log.isDebugEnabled()) {
      log.debug("Unit price :" + unitPrice + ": Duration :" + duration.getStandardDays());
    }
    
    switch (validityType) {
    case MONTHLY:
      if (duration.getStandardDays() <= 0) {
        multiplier = 1;
      } else {
        // prorate the charge
        multiplier = (double)(duration.getStandardDays() + 1) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    case YEARLY:
      if (duration.getStandardDays() <= 0) {
        multiplier = 12;
      } else {
        // prorate the charge
        multiplier = (double)(duration.getStandardDays() + 1) / Constants.DAYS_OF_MONTHLY_BILLING;
      }
      break;
    default:
      throw new InvalidRequestException("Do not know how to process :" + validityType);
    }
    BigDecimal totalPrice = BigDecimal.valueOf(
        unitPrice * purchaseUnits * multiplier).setScale(
        Constants.PRICE_PRECISION, Constants.ROUNDING_MODE);
    return totalPrice.doubleValue();
  }
  
  public Double getUnitPrice(Account account, Product product, Optional<Promotion> promotion) {
    return promotion.map(Promotion::getUnitPrice).orElse(product.getUnitPrice());
  }
  
  /**
   * Updates the next payment date and next payment amount.
   * 
   * @param account
   *          - account to update
   * @param product
   *          - product
   * @param promotion
   *          - promotion
   * @param numMembers
   *          - number of members
   */
  protected void updateNextPayment(Account account, Product product, Promotion promotion,
      long numMembers) {
    double unitPrice = getUnitPrice(account, product, Optional.ofNullable(promotion));
    
    int multiplier = 0;
    
    // calculating the next recharge amount
    switch (product.getValidityType()) {
    case MONTHLY:
      if (account.getNextPaymentDate() == null) {
        // setting the next payment date
        DateTime dateTime = DateTime.now();
        account.setNextPaymentDate(dateTime.plusDays(Constants.DAYS_OF_MONTHLY_BILLING).toDate());
        account.setBillingCycleStartDate(dateTime.toDate());
        account.setExpiresTime(getExpiresTime(product, account).toDate());
      } else {
        Duration duration = new Duration(DateTime.now(), new DateTime(account.getNextPaymentDate()));
        if (duration.getStandardDays() <= 0) {
          DateTime lastPaymentDate = new DateTime(account.getNextPaymentDate());
          account.setNextPaymentDate(lastPaymentDate.plusDays(Constants.DAYS_OF_MONTHLY_BILLING).toDate());
        }
      }
      multiplier = 1;
      break;
    case YEARLY:
      if (account.getNextPaymentDate() == null) {
        // setting the next payment date
        DateTime dateTime = DateTime.now();
        account.setNextPaymentDate(dateTime.plusDays(Constants.DAYS_OF_MONTHLY_BILLING * 12)
            .toDate());
        account.setBillingCycleStartDate(dateTime.toDate());
        account.setExpiresTime(getExpiresTime(product, account).toDate());
      }
      multiplier = 12;
      break;
    default:
      throw new InvalidRequestException("Don't know how to process :" + product.getValidityType());
    }
    // set the next charge amount
    BigDecimal totalPrice = BigDecimal.valueOf(
        unitPrice * multiplier * numMembers).setScale(
        Constants.PRICE_PRECISION, Constants.ROUNDING_MODE);
    account.setNextChargeAmount(totalPrice.doubleValue());
  }  
}
