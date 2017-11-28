package com.sp.web.promotions;

import com.sp.web.exception.PromotionsValidationException;
import com.sp.web.model.Product;
import com.sp.web.model.Promotion;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dax Abraham
 * 
 *         Count based promotions validator.
 */
@Component("COUNT_BASED")
public class CountBasedPromotionsValidator extends BasePromotionsValidator {

  private static final Logger log = Logger.getLogger(CountBasedPromotionsValidator.class);
  
  @Autowired
  PromotionsRepository promotionsRepository;
  
  private static final ReentrantLock masterLock = new ReentrantLock();

  @Override
  public boolean validate(Promotion promotion, Product product) throws PromotionsValidationException {
    
    boolean countReserved = false;
    
    // base validation
    super.validate(promotion, product);
    
    // check if there are any counts to reduce.
    masterLock.lock();
    
    try {
      Promotion findById = promotionsRepository.findById(promotion.getId());
      int count = findById.getCount();
      if (count > 0) {
        count--;
        countReserved = true;
        findById.setCount(count);
        promotionsRepository.update(findById);
      }
    } catch (Exception e) {
      log.warn("Error updating the count.", e);
    } finally {
      masterLock.unlock();
    }
    
    if (!countReserved) {
      throw new PromotionsValidationException("Promotion no longer active.");
    }
    
    return true;
  }

}