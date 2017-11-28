package com.sp.web.controller;

import com.sp.web.controller.response.UserResponse;
import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Dax Abraham
 * 
 *         The helper class that has helper methods for all the controllers.
 */
public class ControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(ControllerHelper.class);
  
  /**
   * This is the wrapping method to retrieve the user information, as well as adding all errors
   * handling etc.
   * 
   * @param process
   *          - the process to invoke
   * @param token
   *          - get the logged in user
   * @param params
   *          - the params to process
   * @return the response for the request
   */
  public static SPResponse process(BiFunction<User, Object[], SPResponse> process,
      Authentication token, Object... params) {
    
    UserResponse errorResponse = null;
    
    try {
      // get the logged in user
      User user = GenericUtils.getUserFromAuthentication(token);
      ControllerHelper.updateUserInAuthentication(token, user);
      SPResponse response = process.apply(user, params);
      // ControllerHelper.processUserActionUpdateRequest(user, response);
      
      return response;
      
    } catch (SPException exp) {
      LOG.warn("Unable to process the request ", exp);
      errorResponse = new UserResponse(exp);
//      errorResponse.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to process the request ", e);
      errorResponse = new UserResponse(new SPException(e.getMessage(), e));
    }
    
    // the response
    return errorResponse;
  }
  
  /**
   * This is the wrapping method to retrieve the user information, as well as adding all errors
   * handling etc.
   * 
   * @param process
   *          - the process to invoke
   * @return the response for the request
   */
  public static SPResponse process(Function<User, SPResponse> process, Authentication token) {
    
    SPResponse errorResponse = null;
    try {
      // get the logged in user
      User user = GenericUtils.getUserFromAuthentication(token);
      ControllerHelper.updateUserInAuthentication(token, user);
      SPResponse response = process.apply(user);
      // ControllerHelper.processUserActionUpdateRequest(user, response);
      
      return response;
      
    } catch (SPException exp) {
      LOG.warn("Unable to process the request ", exp);
      errorResponse = new UserResponse(exp);
    } catch (Exception e) {
      LOG.warn("Unable to process the request ", e);
      errorResponse = new UserResponse(new SPException(e.getMessage(), e));
    }
    
    // the response
    return errorResponse;
  }
  
  /**
   * This is the wrapping method to retrieve the user information, as well as adding all errors
   * handling etc.
   * 
   * @param process
   *          - the process to invoke
   * @param params
   *          - the params to process
   * @return the response for the request
   */
  public static SPResponse process(Function<String, String> process, String params) {
    
    SPResponse spResponse = new SPResponse();
    
    try {
      // get the logged in user
      return spResponse.add("response", process.apply(params));
      
    } catch (SPException exp) {
      LOG.warn("Unable to process the request ", exp);
      spResponse.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to process the request ", e);
      spResponse.addError(new SPException(e.getMessage(), e));
    }
    
    // the response
    return spResponse;
  }
  
  /**
   * Processes the unauthenticated controller requests.
   * 
   * @param process
   *          - function to process
   * @param params
   *          - params
   * @return the response
   */
  public static SPResponse doProcess(Function<Object[], SPResponse> process, Object... params) {
    
    SPResponse errorResponse = null;
    
    try {
      
      return process.apply(params);
      
    } catch (SPException exp) {
      LOG.warn("Unable to process the request ", exp);
      errorResponse = new UserResponse(exp);
    } catch (Exception e) {
      LOG.warn("Unable to process the request ", e);
      errorResponse = new UserResponse(new SPException(e.getMessage(), e));
    }
    
    // the response
    return errorResponse;
  }
  
  /**
   * Processes the unauthenticated controller requests.
   * 
   * @param user
   *          - user
   * @param response
   *          - SPResponse
   * 
   */
  @Deprecated
  public static void processUserActionUpdateRequest(User user, SPResponse response) {
    try {
      SessionFactory.updatePendingActions(user, response);
    } catch (SPException exp) {
      LOG.warn("Unable to process the processUserActionUpdateRequest ", exp);
    } catch (Exception e) {
      LOG.warn("Unable to process the processUserActionUpdateRequest ", e);
    }
  }
  
  public static void updateUserInAuthentication(Authentication authentication, User user) {
    if (user.isUserUpdatedInSession()) {
      LoginHelper loginHelper = ApplicationContextUtils.getBean(LoginHelper.class);
      loginHelper.updateAuthenticationDetail(authentication, user);
      user.setUserUpdatedInSession(false);
    }
  }
  
}
