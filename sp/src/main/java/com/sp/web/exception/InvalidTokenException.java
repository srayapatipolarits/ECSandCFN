package com.sp.web.exception;

import com.sp.web.Constants;
import com.sp.web.model.Token;

/**
 * @author Dax Abraham
 *
 *         The invalid token exception class.
 */
public class InvalidTokenException extends SPException {
  
  private static final long serialVersionUID = -8846724078449577611L;
  
  private Token token;
  
  public InvalidTokenException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace, Token token) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.token = token;
  }
  
  public InvalidTokenException(String message, Token token) {
    super(message);
    this.token = token;
  }
  
  public InvalidTokenException(Throwable cause, Token token) {
    super(cause);
    this.token = token;
  }
  
  public InvalidTokenException(String message, SPException exp, Token token) {
    super(message, exp);
    this.token = token;
  }
  
  public InvalidTokenException(Token token) {
    super(token.getInvalidationCause());
    this.token = token;
  }
  
  public Token getToken() {
    return token;
  }
  
  public void setToken(Token token) {
    this.token = token;
  }

  /**
   * Helper method to get the view to redirect to.
   * 
   * @return
   *     the view to redirect to
   */
  public String getRedirectView() {
    String redirectView = null;
    String invalidationCause = null;
    if (token != null) {
      redirectView = token.getRedirectToView();
      invalidationCause = token.getInvalidationCause();
    }
    if (redirectView == null) {
      redirectView = Constants.VIEW_TOKEN_ERROR;
    }
    if (invalidationCause != null) {
      redirectView += "&cause=" + token.getInvalidationCause();
    }
    return redirectView;
  }
  
}
