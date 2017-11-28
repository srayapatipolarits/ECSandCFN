package com.sp.web.mvc;
 
import com.sp.web.exception.SPException;

import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
 
/**
* This is the response class for all the AJAX MVC calls.
*
 * @author Dax Abraham
*/
public class SPResponse {
 
  private static final String SUCCESS = "Success";
  @SuppressWarnings("rawtypes")
  Map success;
  Map<String, Object> error;
  boolean doRedirect = false;
  private int errorCount = 0;
 
  @SuppressWarnings("rawtypes")
  public Map getSuccess() {
    return success;
  }
 
  public Map<String, Object> getError() {
    return error;
  }
 
  /**
   * Add an error.
   *
   * @param errorKey
   *          - the error key
   * @param message
   *          - the message
   */
  public SPResponse addError(String errorKey, Object message) {
    if (error == null) {
      error = new HashMap<String, Object>();
    }
    if (errorCount > 0) {
      errorKey += errorCount;
    }
    error.put(errorKey, message);
    errorCount++;
    return this;
  }
 
  /**
   * Add a field error.
   *
   * @param err
   *          - the error to add
   */
  public void addError(FieldError err) {
    addError(err.getField(), err.getDefaultMessage());
  }
 
  /**
   * Add an error.
   *
   * @param exp
   *          - adds the given exception cause to the error response
   */
  public void addError(SPException exp) {
    if (exp.getCause() != null) {
      addError(exp.getCause().getClass().getSimpleName(), exp.getMessage());
    } else {
      addError(exp.getClass().getSimpleName(), exp.getMessage());
    }
  }
 
  /**
   * Called if the request is successful.
   */
  @SuppressWarnings("unchecked")
  public SPResponse isSuccess() {
    if (success == null) {
      success = new HashMap<String, Object>();
    }
    success.put(SUCCESS, "true");
    return this;
  }
 
  /**
   * Sets the value of the success field in the response map.
   *
   * @param status
   *          - status to set
   */
  public void isSuccess(String status) {
    add(SUCCESS, status);
  }
 
  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SPResponse:\nSuccess:" + success + "\nError:" + error;
  }
 
  /**
   * Add a success parameter.
   *
   * @param key
   *          - key for success params
   * @param obj
   *          - value for success params
  */
  @SuppressWarnings("unchecked")
  public SPResponse add(String key, Object obj) {
    if (success == null) {
      isSuccess();
    }
    success.put(key, obj);
    return this;
  }
 
  /**
   * <code>add</code> method will add the whole map in the reponse.
   *
   * @param map
   *          for the method.
   * @return the SPREpsosne
   */
  @SuppressWarnings("unchecked")
  public SPResponse add(Map<String, ? extends Object> map) {
    if (success == null) {
      isSuccess();
    }
    success.putAll(map);
    return this;
  }
 
 
  /**
   * Gets the parameter from the success map.
   *
   * @param key
   *          - the key in success map
   * @return - the value if present
   */
  protected Object get(String key) {
    return (null == success) ? null : success.get(key);
  }
 
 
  /**
   * @return true if there are errors in response.
   */
  public boolean hasErrors() {
    return (error != null && error.size() > 0);
  }
 
  public boolean isDoRedirect() {
    return doRedirect;
  }
 
  public void setDoRedirect(boolean doRedirect) {
    this.doRedirect = doRedirect;
  }
 
  /**
   * Helper method to set the redirect flag.
   */
  public void doRedirect() {
    setDoRedirect(true);
  }
 
  public static SPResponse success() {
    return new SPResponse().isSuccess();
  }
 
  /**
   * set Success method will set the map to the sucess.
   * @param successMap sucess map.
   */
  @SuppressWarnings("unchecked")
  public void setSuccess(Map<String, String> successMap) {
    this.success = successMap;
    if (success != null) {
      success.put(SUCCESS, "true");
    }
  }

  public Map<String, Object> createError() {
    return error = new HashMap<String, Object>();
  }
}
 