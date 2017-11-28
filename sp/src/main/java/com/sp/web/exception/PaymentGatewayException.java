package com.sp.web.exception;


/**
 * <code>PaymentGatewayException</code> for the payment.
 * 
 * @author pradeepruhil
 *
 */
public class PaymentGatewayException extends SPException {

  /**
   * Constructor with gateway.
   * 
   * @param message
   *          exception message.
   */
  public PaymentGatewayException(String message) {
    super(message);
  }

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 6754554341623091965L;

}
