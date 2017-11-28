/**
 * 
 */
package com.sp.web.service.payment;

import com.sp.web.model.PaymentInstrument;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.payment.AuthorizedNetPaymentGateway;
import com.sp.web.payment.PaymentGateway;
import com.sp.web.payment.PaymentGatewayRequest;
import com.sp.web.payment.PaymentGatewayResponse;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author pradeepruhil
 *
 */
public class AuthorizedNetPaymentGatewayTest extends SPTestBase {

  private static final Logger LOG = Logger.getLogger(AuthorizedNetPaymentGateway.class);

  @Autowired
  @Qualifier("authorizedNet")
  private PaymentGateway authorizedNetGateway;

  @Test
  public void testCrediCard() {

    /* test with credit card */
    PaymentInstrument instrument = new PaymentInstrument();
    instrument.setCardNumber("5424000000000015");
    instrument.setCvv("999");
    instrument.setMonth("12");
    instrument.setYear("17");

    PaymentGatewayRequest gatewayRequest = new PaymentGatewayRequest("Bying Test prodocut", 6, instrument,300.00);
    gatewayRequest.setCustomerId("1419278590612");
    gatewayRequest.setInvoiceNumber("INV0001");
    gatewayRequest.setRefId("IN00021");
    PaymentGatewayResponse process = authorizedNetGateway.process(gatewayRequest);

    LOG.debug("Response returend is " + process);
    Assert.assertTrue(process.isSuccess());

  }

  @Test
  public void testUpdatePaymentProfileId() {

    PaymentInstrument instrument = new PaymentInstrument();
    instrument.setAuthroziedNetPaymentProfileId("28306489");
    PaymentGatewayRequest gatewayRequest = new PaymentGatewayRequest("Bying Test product with Payment profile id ", 3,
        instrument,0.00);
    gatewayRequest.setCustomerId("111212abc");
    gatewayRequest.setAuthorizedNetProfileId("31300159");
    gatewayRequest.setInvoiceNumber("INV0001");
    gatewayRequest.setRefId("IN00021");
    PaymentGatewayResponse process = authorizedNetGateway.process(gatewayRequest);

    LOG.debug("Response returend is " + process);
    Assert.assertTrue(process.isSuccess());
  }
}
