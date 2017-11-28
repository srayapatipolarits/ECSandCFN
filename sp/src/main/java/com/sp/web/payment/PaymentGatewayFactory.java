package com.sp.web.payment;

import com.sp.web.exception.PaymentGatewayException;
import com.sp.web.model.PaymentType;
import com.sp.web.utils.ApplicationContextUtils;

import org.springframework.stereotype.Component;

/**
 * PaymentGatewayFactory will return the payment gateway for the type passed to it.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PaymentGatewayFactory {
  
  /**
   * <code>getPaymentGateway</code> method returns the payment gateway for the payment type passed.
   * 
   * @param paymentType
   *          for which payment gateway is to be returned.
   * @return the payment gateway.
   */
  public PaymentGateway getPaymentGateway(PaymentType paymentType) {
    
    /* check for backward compatibility. IN case of null assign credit card as payment gateway. */
    if (paymentType == null) {
      paymentType = PaymentType.CREDIT_CARD;
    }
    PaymentGateway gateway;
    switch (paymentType) {
    case CREDIT_CARD:
      gateway = (PaymentGateway) ApplicationContextUtils.getBean("authorizedNet");
      break;
    case SP_INTERNAL:
    case TRIAL:
    case WIRE:
      gateway = (PaymentGateway) ApplicationContextUtils.getBean("creditNotePayment");
      break;
    default:
      throw new PaymentGatewayException("Invalid Payment Type.");
    }
    return gateway;
  }
  
}
