package com.sp.web.controller.marketing;

import com.sp.web.Constants;
import com.sp.web.dto.marketing.SPMarketingDTO;
import com.sp.web.form.marketing.SPMarketingForm;
import com.sp.web.model.User;
import com.sp.web.model.marketing.SPMarketing;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.marketing.SPMarketingFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * @author Prasanna Venkatesh
 * 
 *         The controller helper for SPMarketing controller.
 */
@Component
public class SPMarketingControllerHelper {
  
  @Autowired
  SPMarketingFactory marketingFactory;
  
  /**
   * The helper method for retrieving the marketing A/B version for a user.
   * 
   * @param user
   *          - user
   * @return the response to the get request
   */
  public SPResponse get(User user) {
    final SPResponse resp = new SPResponse();
    SPMarketing spMarketing = marketingFactory.get(user.getId());
    SPMarketingDTO marketingDto = null;
    if (spMarketing != null) {
      marketingDto = new SPMarketingDTO(spMarketing);
    }
    resp.add(Constants.PARAM_USER_MARKETING, marketingDto);
    return resp.isSuccess();
  }
  
  /**
   * The helper method to save the marketing A/B version for a user.
   * 
   * @param params
   *          String version
   * @return the SPResponse
   */
  public SPResponse save(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    SPMarketingForm form = new SPMarketingForm();
    form.setVersion((String) params[0]);
    form.setUserId(user.getId());
    
    SPMarketing spMarketing = new SPMarketing();
    BeanUtils.copyProperties(form, spMarketing);
    
    marketingFactory.save(spMarketing);
    return resp.isSuccess();
  }
  
}
