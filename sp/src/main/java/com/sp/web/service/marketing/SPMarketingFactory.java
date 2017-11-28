package com.sp.web.service.marketing;

import com.sp.web.model.marketing.SPMarketing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 
 * @author Prasanna Venkatesh
 * 
 *  The Marketing Factory class
 */

@Component
public class SPMarketingFactory {
  
  @Autowired
  SPMarketingFactoryCache marketingFactoryCache;
  
  
  /**
   * Get the Marketing A/B value for a User.
   * 
   *@param user
   *          for which marketing value to be returned.
   *          
   * @return the SPMarketing.
   */
  public SPMarketing get(String userId) {
    return marketingFactoryCache.get(userId);
  }

  /**
   * Save the Marketing A/B value for a User.
   * 
   * @param spMarketing
   *          - SPMarketing
   */
  public void save(SPMarketing spMarketing) {
    marketingFactoryCache.save(spMarketing);
  }
  
}
