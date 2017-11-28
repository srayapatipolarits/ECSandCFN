package com.sp.web.service.marketing;

import com.sp.web.model.marketing.SPMarketing;
import com.sp.web.repository.marketing.SPMarketingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Prasanna Venkatesh
 *
 *         The factory cache class for the SPMarketing factory.
 */

@Component
public class SPMarketingFactoryCache {
  
  @Autowired
  SPMarketingRepository marketingRepository;
  
  /**
   * Get the Marketing A/B value for a User.
   * 
   *@param userId
   *          for which marketing value to be returned.
   *          
   * @return the SPMarketing.
   */
  public SPMarketing get(String userId) {
    SPMarketing spMarketing = marketingRepository.findByUser(userId);
    return spMarketing;
  }
  
  /**
   * Save the Marketing A/B value for a User.
   * 
   * @param spMarketing
   *          - SPMarketing
   */
  public void save(SPMarketing spMarketing) {
    marketingRepository.save(spMarketing);
  }
  
}
