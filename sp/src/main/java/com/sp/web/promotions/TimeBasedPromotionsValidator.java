package com.sp.web.promotions;

import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;

import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         Time based promotions validator.
 */
@Component("TIME_BASED")
public class TimeBasedPromotionsValidator extends BasePromotionsValidator {

  @Override
  public boolean validate(Promotion promotion, Product product) throws PromotionsValidationException {
    return super.validate(promotion, product);
  }

}