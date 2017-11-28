package com.sp.web.product;

import com.sp.web.model.Account;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;

import org.joda.time.DateTime;

import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         This is the helper interface for the various product related functionality like price
 *         calculations etc.
 */
public interface ProductHelper {

  /** 
   * Gets the expiry time for the given product.
   * 
   * @param product
   *          - the product 
   * @return
   *      the expiry time for the given product
   */
  DateTime getExpiresTime(Product product, Account account);
    
  /**
   * Get the amount to charge for the given account.
   * 
   * @param account
   *          - account
   * @param product
   *          - product
   * @param promotion
   *          - promotion         
   * @param purchaseUnits
   *          - the factor          
   * @return
   *        the amount to charge
   */
  Double getChargeAmount(Account account, Product product, Optional<Promotion> promotion, long purchaseUnits);

  /**
   * Gets the unit price for the given account product and promotion.
   * 
   * @param account
   *          - account
   * @param product
   *          - product
   * @param promotion
   *          - optional promotion
   * @return
   *        the unit price
   */
  Double getUnitPrice(Account account, Product product, Optional<Promotion> promotion);
  
  /**
   * Updates the given account with the purchase units.
   * 
   * @param account
   *           - account
   * @param product
   *           - product
   * @param promotion
   *           - promotion
   * @param purchaseUnits
   *           - purchase unit
   * @param response 
   *           - the payment charged
   * @param request 
   *           - the payment request
   */
  void updateAccount(Account account, Product product, Promotion promotion, long purchaseUnits);
}
