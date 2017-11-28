package com.sp.web.payment;

import com.sp.web.model.Address;


/**
 * @author Dax Abraham
 * 
 *         The interface for processing all the payments in the system.
 */
public interface PaymentGateway {

  /**
   * Process the payment for the given payment request.
   * 
   * @param request
   *          - the request for payment
   * @return the response from the payment gateway
   */
  public PaymentGatewayResponse process(PaymentGatewayRequest request);

  /**
   * <code>updatePaymentInstrumnet</code> method will update the payment instrumnet of the customer
   * 
   * @param request
   *          payment gateway request.
   * @return the response for the payment gatway response.
   */
  public PaymentGatewayResponse updatePaymentInstrument(PaymentGatewayRequest request, Address address);
}
