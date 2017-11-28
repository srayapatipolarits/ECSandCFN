package com.sp.web.promotions;

import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.model.Product;
import com.sp.web.model.ProductStatus;
import com.sp.web.model.Promotion;
import com.sp.web.utils.MessagesHelper;

import java.time.LocalDate;

/**
 * @author Dax Abraham
 * 
 *         Time based promotions validator.
 */
public class BasePromotionsValidator implements PromotionsValidator {

  @Override
  public boolean validate(Promotion promotion, Product product) throws PromotionsValidationException {
    // check if the time is valid
    if (promotion.getEndDate().isBefore(LocalDate.now())) {
      // date is in the past
      throw new PromotionsValidationException(MessagesHelper.getMessage("signup.business.step2.notValidCode", ""));
    }

    // check if the product is a valid product
    if (product == null || product.getStatus() != ProductStatus.Active) {
      throw new PromotionsValidationException("The product :" + product + " inactive or suspended.");
    }
    
    if (promotion.getStatus() != PromotionStatus.Active) {
      throw new PromotionsValidationException("Promotion :" + promotion.getCode()
          + ": is no longer active.");
    }

    // check if the product id is present in the list of applicable product ids
    if (!promotion.getProductIdList().contains(product.getId())) {
      throw new PromotionsValidationException("The promotion not applicable for product :" + product.getId() + ".");
    }

    return true;
  }

}