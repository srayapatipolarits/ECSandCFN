/**
 * 
 */
package com.sp.web.exception;

/**
 * @author Dax Abraham
 *
 */
public class InvalidRequestException extends SPException {

  /**
	 * 
	 */
  private static final long serialVersionUID = -7512694161695733211L;

  /**
   * @param message
   */
  public InvalidRequestException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public InvalidRequestException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public InvalidRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public InvalidRequestException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
