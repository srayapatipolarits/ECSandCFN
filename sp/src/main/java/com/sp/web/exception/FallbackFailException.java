package com.sp.web.exception;

public class FallbackFailException extends SPException {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public FallbackFailException(String message) {
    super(message);
  }
  
  public FallbackFailException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
