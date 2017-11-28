package com.sp.web.promotions;

import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;
import com.sp.web.product.ProductRepository;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author daxabraham This is the factory class for promotions.
 */
@Component
public class PromotionsFactory {

  private static final Logger LOG = Logger.getLogger(PromotionsFactory.class);

  @Autowired
  ProductRepository productRepository;
  
  /**
   * Validate the given promotion.
   * 
   * @param promotion
   *          - promotion to validate
   * @param product
   *          - the product
   * @return true if validation success else false
   * @throws PromotionsValidationException
   *           - if the validation fails
   */
  public boolean validate(Promotion promotion, Product product)
      throws PromotionsValidationException {
    try {
      ApplicationContext appContext = ApplicationContextUtils.getApplicationContext();
      PromotionsValidator validator = (PromotionsValidator) appContext.getBean(promotion
          .getPromotionType().toString());
      return validator.validate(promotion, product);
    } catch (BeansException e) {
      LOG.fatal("Could not instantiate promotions validator for :" + promotion, e);
      throw new PromotionsValidationException("Could not instantiate promotions validator for :"
          + promotion, e);
    }
  }

  /**
   * Validate the given promotion.
   * 
   * @param promotion
   *          - promotion to validate
   * @param productId
   *          - the productId
   * @return true if validation success else false
   * @throws PromotionsValidationException
   *           - if the validation fails
   */
  public boolean validate(Promotion promotion, String productId)
      throws PromotionsValidationException {
    return validate(promotion, productRepository.findById(productId));
  }
}
