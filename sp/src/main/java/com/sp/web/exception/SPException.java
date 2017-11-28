/**
 * 
 */
package com.sp.web.exception;

/**
 * @author daxabraham
 * 
 *         The enclosing SurePeople exception for future use.
 */
public class SPException extends RuntimeException {

  /**
   * The serial version UID for the class
   */
  private static final long serialVersionUID = -283017357655687681L;

  /**
   * @param message
   */
  public SPException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public SPException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public SPException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public SPException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * @return the error as a JSON String
   */
  public String getErrorAsJson() {
    return "\"error\":" + getMessage() + "\"";
  }

}
