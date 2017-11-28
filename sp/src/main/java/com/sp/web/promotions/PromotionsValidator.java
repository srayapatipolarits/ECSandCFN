package com.sp.web.promotions;

import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;

/**
 * @author daxabraham
 * 
 *         This is the validator interface for all the Promotions validations.
 */
public interface PromotionsValidator {

  /**
   * Validate the promotion.
   * 
   * @param promotion
   *          - the promotion to validate
   * @param product
   *          - the product to apply it to
   * @return true on successful validation
   * 
   * @throws PromotionsValidationException
   *           - Error while validating the promotion
   */
  boolean validate(Promotion promotion, Product product) throws PromotionsValidationException;
}
