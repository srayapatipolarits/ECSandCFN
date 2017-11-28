package com.sp.web.controller.systemadmin.fallback;

import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.fallback.FallbackFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * FallbackAdminControllerHelper is the generic controllerHelper class for
 * {@link FallbackAdminController}.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class FallbackAdminControllerHelper {
  
  @Autowired
  FallbackFactory fallbackFactory;
  
  /**
   * executeFallback will implement the fallback.
   * 
   * @param user
   *          is the logged in user.
   * @param param
   *          contains the companyid.
   * @return the response.
   */
  public SPResponse executeFallback(User user, Object[] param) {
    
    String companyId = (String) param[0];
    
    fallbackFactory.processFallbacksByCompany(companyId);
    return new SPResponse().isSuccess();
  }
}
