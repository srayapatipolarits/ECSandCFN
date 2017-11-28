package com.sp.web.exception;


/**
 * @author daxabraham
 * 
 *         Exception to indicate a promotion validation exception.
 */
public class PromotionsValidationException extends SPException {

  private static final long serialVersionUID = -3264472530186855181L;

  public PromotionsValidationException(String message) {
    super(message);
  }

  public PromotionsValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public PromotionsValidationException(Throwable cause) {
    super(cause);
  }

}
