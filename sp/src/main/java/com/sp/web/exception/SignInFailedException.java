package com.sp.web.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Dax Abraham
 * 
 *         The exception when the signin has failed.
 */
public class SignInFailedException extends AuthenticationException {
  
  public enum SignInFailReason {
    CompanyBlocked, GeneralError, IndividualBlocked, CompanyMemberBlocked, UserAccountLocked, PasswordExpired;
  }
  
  /**
   * The serial version uid.
   */
  private static final long serialVersionUID = -154824501818201268L;
  
  private SignInFailReason reason;
  
  public SignInFailedException(SignInFailReason reason) {
    super(reason.toString());
    this.reason = reason;
  }
  
  public SignInFailedException(SignInFailReason reason, String message) {
    super(message);
    this.reason = reason;
  }
  
  public SignInFailedException(SignInFailReason reason, Throwable cause) {
    super(reason.toString(), cause);
    this.reason = reason;
  }
  
  public SignInFailedException(SignInFailReason reason, String message, Throwable cause) {
    super(message, cause);
    this.reason = reason;
  }
  
  public SignInFailReason getReason() {
    return reason;
  }
  
}
