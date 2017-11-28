package com.sp.web.exception;

import com.sp.web.payment.PaymentGatewayResponse;

/**
 * @author Dax Abraham
 * 
 *         The exception thrown for any failure to process the payments.
 */
public class PaymentProcessingException extends SPException {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 2692558541967562473L;
  private PaymentGatewayResponse response;

  public PaymentProcessingException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public PaymentProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  public PaymentProcessingException(String message) {
    super(message);
  }

  public PaymentProcessingException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor to create the exception details from the passed response.
   * 
   * @param response
   *          - payment response
   */
  public PaymentProcessingException(PaymentGatewayResponse response) {
    super(response.getErrorMessage());
    this.response = response;
  }

  public PaymentGatewayResponse getResponse() {
    return response;
  }
}
