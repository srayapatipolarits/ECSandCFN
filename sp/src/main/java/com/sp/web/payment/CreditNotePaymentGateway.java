package com.sp.web.payment;

import com.sp.web.account.AccountRepository;
import com.sp.web.exception.PaymentGatewayException;
import com.sp.web.model.Address;
import com.sp.web.model.CreditNotePaymentInstrument;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CreditNotePaymentGateway provides the functionality for the product using the payment gateway.
 * 
 * @author pradeepruhil
 *
 */
@Service("creditNotePayment")
public class CreditNotePaymentGateway implements PaymentGateway {
  
  private static final Logger LOG = Logger.getLogger(CreditNotePaymentGateway.class);
  
  @Autowired
  private AccountRepository accountRepository;
  
  /**
   * <code>process</code> method will process the payment gateway.
   * 
   * @see com.sp.web.payment.PaymentGateway#process(com.sp.web.payment.PaymentGatewayRequest)
   */
  @Override
  public PaymentGatewayResponse process(PaymentGatewayRequest request) {
    
    /* get the credit note payment instrumnet */
    
    CreditNotePaymentInstrument instrument = (CreditNotePaymentInstrument) request.getInstrument();
    
    double creditBalance = request.getCreditBalance();
    
    double amount = request.getAmount();
    /* check if amount to credit is less than total credit */
    
    if (amount > creditBalance) {
      LOG.error("insufficient credit amount " + (creditBalance - amount));
      throw new PaymentGatewayException("Insufficient credit amount. " + (creditBalance - amount));
    }
    
    instrument.setCreditBalance(creditBalance - amount);
    instrument.setReferenceNo(UUID.randomUUID().toString());
    instrument.setCreateTime(LocalDateTime.now());
    
    PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
    paymentGatewayResponse.setSuccess(true);
    paymentGatewayResponse.setTxnId(instrument.getReferenceNo());
    return paymentGatewayResponse;
  }
  
  /**
   * updatePaymentInstrument mehtod will update the account and the payment instrument for the
   * latest credit note.
   * 
   * @see com.sp.web.payment.PaymentGateway#updatePaymentInstrument(com.sp.web.payment.PaymentGatewayRequest,
   *      com.sp.web.model.Address)
   */
  @Override
  public PaymentGatewayResponse updatePaymentInstrument(PaymentGatewayRequest request,
      Address address) {
    
    CreditNotePaymentInstrument instrument = (CreditNotePaymentInstrument) request.getInstrument();
    instrument.setReferenceNo(UUID.randomUUID().toString());
    instrument.setCreateTime(LocalDateTime.now());
    
    PaymentGatewayResponse paymentGatewayResponse = new PaymentGatewayResponse();
    paymentGatewayResponse.setSuccess(true);
    paymentGatewayResponse.setTxnId(instrument.getReferenceNo());
    return paymentGatewayResponse;
  }
  
}
