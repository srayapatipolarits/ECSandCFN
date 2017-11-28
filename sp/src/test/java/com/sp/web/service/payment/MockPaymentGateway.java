package com.sp.web.service.payment;

import com.sp.web.model.Address;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.payment.PaymentGateway;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * @author Dax Abraham
 * 
 *         The implementation of the mock payment gateway.
 */
@Service("authorizedNet")
@Profile("Test")
public class MockPaymentGateway implements PaymentGateway {

  private static final Logger LOG = Logger.getLogger(MockPaymentGateway.class);

  private boolean doProcess;
  
  public MockPaymentGateway() {
    this.doProcess = true;
  }

  @Override
  public PaymentGatewayResponse process(PaymentGatewayRequest request) {
    LOG.debug("Request received :" + request);
    PaymentGatewayResponse resp = new PaymentGatewayResponse();
    if (doProcess) {
      resp.setSuccess(true);
      resp.setTxnId(System.currentTimeMillis() + "");
    } else {
      resp.setSuccess(false);
      resp.setErrorMessage("Payment failed !!!");
    }
    return resp;
  }

  public void dontProcessAnyMore() {
    this.doProcess = false;
  }

  public void startProcessingAgain() {
    this.doProcess = true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.payment.PaymentGateway#updatePaymentInstrument(java.lang.String,
   * com.sp.web.model.PaymentInstrument)
   */
  @Override
  public PaymentGatewayResponse updatePaymentInstrument(PaymentGatewayRequest gatewayRequest, Address address) {
    PaymentInstrument instrument = gatewayRequest.getInstrument();
    maskGatewayRequest(instrument);
    PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
    paymentGatewayResponse.setSuccess(true);
    paymentGatewayResponse.setTxnId(System.currentTimeMillis() + "");

    return paymentGatewayResponse;
  }

  private void maskGatewayRequest(PaymentInstrument instrument) {
    instrument.setCardNumber(null);
    instrument.setCvv(null);
    instrument.setCvv(null);
    instrument.setMonth(null);
    instrument.setYear(null);
  }

}
