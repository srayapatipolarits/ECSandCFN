package com.sp.web.controller.response;

import com.sp.web.exception.SPException;
import com.sp.web.mvc.SPResponse;

/**
 * @author Dax Abraham
 * 
 *         The user response envelop that holds all the user responses for the user controller.
 */
public class UserResponse extends SPResponse {
  
  public UserResponse() { }
  
  public UserResponse(SPException exp) {
    addError(exp);
  }
  
}
