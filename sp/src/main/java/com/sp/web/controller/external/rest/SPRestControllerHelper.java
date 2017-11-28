package com.sp.web.controller.external.rest;

import com.sp.web.form.external.rest.UserForm;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * SPRestContorllerHelper class will controller helper class for the rest user.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SPRestControllerHelper {
  
  @Autowired
  private SPExternalServiceFactory factory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * createUser method will create the external user in SurePeople plateform.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          is the object params.
   * @return the response.
   */
  public SPResponse createUser(User user, Object[] params) {
    
    UserForm userForm = (UserForm) params[0];
    userForm.validate();
    SPResponse response = new SPResponse();
    factory.createUser(userForm, user);
    
    return response.isSuccess();
  }
  
  public SPResponse deleteUser(User user, Object[] params) {
    String email = (String) params[0];
    Assert.hasText(email, "Error 1002: Invalid Request");
    
    factory.deleteUser(email, user);
    return SPResponse.success();
  }
}
