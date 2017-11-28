package com.sp.web.payment;

import com.sp.web.Constants;
import com.sp.web.exception.PaymentGatewayException;
import com.sp.web.model.Address;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.utils.MessagesHelper;

import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.ArrayOfNumericString;
import net.authorize.api.contract.v1.CreateProfileResponse;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CreditCardType;
import net.authorize.api.contract.v1.CustomerAddressType;
import net.authorize.api.contract.v1.CustomerDataType;
import net.authorize.api.contract.v1.CustomerPaymentProfileExType;
import net.authorize.api.contract.v1.CustomerProfilePaymentType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.MessagesType;
import net.authorize.api.contract.v1.MessagesType.Message;
import net.authorize.api.contract.v1.OrderType;
import net.authorize.api.contract.v1.PaymentProfile;
import net.authorize.api.contract.v1.PaymentType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionResponse.Errors;
import net.authorize.api.contract.v1.TransactionResponse.Errors.Error;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.contract.v1.UpdateCustomerPaymentProfileRequest;
import net.authorize.api.contract.v1.UpdateCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.ValidationModeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.UpdateCustomerPaymentProfileController;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <code>AuthorizedNetPayementGateway</code> will process the payment gateway request.
 * 
 * @author pradeepruhil
 *
 */
@Service("authorizedNet")
@Profile("PROD")
public class AuthorizedNetPaymentGateway implements PaymentGateway {
  
  private static final Logger LOG = Logger.getLogger(AuthorizedNetPaymentGateway.class);
  
  @Autowired
  private Environment enviornment;
  
  /**
   * <code>process</code> method will process the payment gateway.
   * 
   * @see com.sp.web.payment.PaymentGateway#process(com.sp.web.payment.PaymentGatewayRequest)
   */
  @Override
  public PaymentGatewayResponse process(PaymentGatewayRequest gatewayRequest) {
    
    CreateTransactionRequest createTransactionRequest = getTransactionRequest(gatewayRequest);
    
    /* Authorized.net profile controller to submit the request */
    CreateTransactionController createProfileController = new CreateTransactionController(
        createTransactionRequest);
    
    /* set the enviornment to be used */
    createProfileController.execute(getAuthorizedEnviornment());
    
    CreateTransactionResponse transactionResposne = createProfileController.getApiResponse();
    if (transactionResposne == null) {
      /* process the error response from the transaction */
      ANetApiResponse errorResponse = createProfileController.getErrorResponse();
      String errorMessage = handleErrorResponse(errorResponse.getMessages());
      if (StringUtils.isEmpty(errorMessage)) {
        errorMessage = MessagesHelper.getMessage("payments.default.error.message");
      }
      throw new PaymentGatewayException(errorMessage);
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Transaction response returned" + transactionResposne);
    }
    return processTransactionResponse(transactionResposne, gatewayRequest);
    
  }
  
  /**
   * <code>getAuthorizedEnviornment</code> will return the enviornment to be used for connnecting to
   * autorized.net
   * 
   * @return envionrment for authrozied.net
   */
  private net.authorize.Environment getAuthorizedEnviornment() {
    net.authorize.Environment environment = net.authorize.Environment.valueOf(enviornment
        .getProperty(Constants.AUTHROIZED_ENVIORNMENT));
    return environment;
  }
  
  /**
   * <code>processTransactionResponse</code> method will populate the response and the gateway
   * request with customer profile if no customer profile exist for the user.
   * 
   * @param createTransactionResposne
   *          transaction process.
   * @param gatewayRequest
   *          payment gateway Request.
   * @return the payment gatway resposne.
   */
  private PaymentGatewayResponse processTransactionResponse(
      CreateTransactionResponse createTransactionResposne, PaymentGatewayRequest gatewayRequest) {
    PaymentGatewayResponse gatewayResponse = new PaymentGatewayResponse();
    MessagesType messages = createTransactionResposne.getMessages();
    MessageTypeEnum resultCode = messages.getResultCode();
    switch (resultCode) {
    case OK:
      
      gatewayResponse.setSuccess(true);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Transaction is success");
      }
      
      /* get the message and log it */
      TransactionResponse transactionResponse = createTransactionResposne.getTransactionResponse();
      /*
       * Check if transaction is success or not internally *.
       */
      checkTransactionResponse(transactionResponse);
      
      gatewayResponse.setTxnId(transactionResponse.getTransId());
      gatewayResponse.setCardType(transactionResponse.getAccountType());
      
      /* check if customer profile is returned */
      CreateProfileResponse profileResponse = createTransactionResposne.getProfileResponse();
      if (profileResponse != null) {
        processCustomerProfileResponse(gatewayRequest, transactionResponse, profileResponse,
            gatewayResponse);
      }
      break;
    default:
      gatewayResponse.setSuccess(false);
      Errors errorss = createTransactionResposne.getTransactionResponse().getErrors();
      StringBuilder errorMessageBuilder = new StringBuilder();
      if (errorss != null) {
        List<Error> errors = errorss.getError();
        for (Error error : errors) {
          LOG.error("Transaction Error is " + error.getErrorText() + "error:code "
              + error.getErrorCode());
          errorMessageBuilder.append(error.getErrorText());
        }
      }
      
      if (StringUtils.isEmpty(errorMessageBuilder.toString())) {
        errorMessageBuilder.append(MessagesHelper.getMessage("payments.default.error.message"));
      }
      throw new PaymentGatewayException(errorMessageBuilder.toString());
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Return,process transaction response " + gatewayResponse);
    }
    return gatewayResponse;
    
  }
  
  private void checkTransactionResponse(TransactionResponse transactionResponse) {
    
    /* check the cvv Status code */
    String cvvResultCode = transactionResponse.getCvvResultCode();
    
    // M Successful Match
    // N Does NOT Match
    // P Is NOT Processed
    // S Should be on card, but is not indicated
    // U Issuer is not certified or has not provided encryption key
    LOG.info("CVV Result match " + cvvResultCode);
    
    Errors errors = transactionResponse.getErrors();
    StringBuilder errorMessageBuilder = new StringBuilder();
    if (errors != null) {
      List<Error> errorss = errors.getError();
      for (Error error : errorss) {
        errorMessageBuilder.append(error.getErrorText() + "</br>");
        LOG.error("Transaction Error is " + error.getErrorText() + "error:code "
            + error.getErrorCode());
      }
      if (StringUtils.isEmpty(errorMessageBuilder.toString())) {
        errorMessageBuilder.append(MessagesHelper.getMessage("payments.default.error.message"));
      }
      throw new PaymentGatewayException(errorMessageBuilder.toString());
    }
  }
  
  /**
   * <code>updatePaymentInstrument</code> method will update the payment Instrument in
   * Authorized.net
   * 
   * @see com.sp.web.payment.PaymentGateway#updatePaymentInstrument(java.lang.String,
   *      com.sp.web.model.PaymentInstrument)
   */
  @Override
  public PaymentGatewayResponse updatePaymentInstrument(PaymentGatewayRequest gatewayRequest,
      Address address) {
    
    UpdateCustomerPaymentProfileRequest updatePaymentProfileRequest = getUpdatePaymentProfileRequest(
        gatewayRequest, address);
    UpdateCustomerPaymentProfileController customerPaymentProfileController = new UpdateCustomerPaymentProfileController(
        updatePaymentProfileRequest);
    customerPaymentProfileController.execute(getAuthorizedEnviornment());
    UpdateCustomerPaymentProfileResponse apiResponse = customerPaymentProfileController
        .getApiResponse();
    if (apiResponse == null) {
      LOG.error("Error occurred while processing the transaction ");
      throw new PaymentGatewayException(handleErrorResponse(customerPaymentProfileController
          .getErrorResponse().getMessages()));
    }
    MessagesType messages = apiResponse.getMessages();
    MessageTypeEnum resultCode = messages.getResultCode();
    PaymentGatewayResponse gatewayResponse = new PaymentGatewayResponse();
    switch (resultCode) {
    case OK:
      gatewayResponse.setSuccess(true);
      maskGatewayRequest(gatewayRequest.getInstrument());
      gatewayResponse.setTxnId(apiResponse.getRefId());
      break;
    default:
      /* Error condition throw payment gateway exception */
      throw new PaymentGatewayException(handleErrorResponse(messages));
    }
    return gatewayResponse;
    
  }
  
  /**
   * <code>getUpdatePaymentPRofileRequest</code> method will return the payment profile.
   * 
   * @param customerId
   *          returns the customer id
   * @param pi
   *          payment instrument.
   * @return update customer profile request.
   */
  private UpdateCustomerPaymentProfileRequest getUpdatePaymentProfileRequest(
      PaymentGatewayRequest gatewayRequest, Address address) {
    UpdateCustomerPaymentProfileRequest profileRequest = new UpdateCustomerPaymentProfileRequest();
    profileRequest.setMerchantAuthentication(getMerchantAuthenticationType());
    profileRequest.setCustomerProfileId(gatewayRequest.getAuthorizedNetProfileId());
    CustomerPaymentProfileExType paymentProfile = new CustomerPaymentProfileExType();
    paymentProfile.setCustomerPaymentProfileId(gatewayRequest.getInstrument()
        .getAuthroziedNetPaymentProfileId());
    PaymentType paymentType = getPaymentType(gatewayRequest.getInstrument());
    paymentProfile.setPayment(paymentType);
    profileRequest.setPaymentProfile(paymentProfile);
    CustomerAddressType customerAddressType = new CustomerAddressType();
    customerAddressType.setAddress(address.getAddressLine1());
    customerAddressType.setCity(address.getCity());
    customerAddressType.setCountry(address.getCountry());
    customerAddressType.setState(address.getState());
    customerAddressType.setZip(address.getZipCode());
    paymentProfile.setBillTo(customerAddressType);
    profileRequest.setValidationMode(ValidationModeEnum.LIVE_MODE);
    return profileRequest;
  }
  
  /**
   * <code>handleErrorResponse</code> method will handle the error response from the authorized.net
   * service
   * 
   * @param messages
   *          gateway messages response code.
   * @return the error message.
   */
  private String handleErrorResponse(MessagesType messages) {
    /* get the error message */
    List<Message> errorMessages = messages.getMessage();
    String errorMessage = null;
    for (Message message : errorMessages) {
      try {
        errorMessage = MessagesHelper.getMessage(Constants.PAYMENT_GATEWAY + message.getCode());
      } catch (NoSuchMessageException e) {
        LOG.error("NO message configured for error code " + message.getCode());
      }
      if (StringUtils.isEmpty(errorMessage)) {
        errorMessage = message.getText();
        LOG.error("Transaction failed " + errorMessage + message.getCode());
      }
      break;
    }
    if (StringUtils.isEmpty(errorMessage)) {
      errorMessage = MessagesHelper.getMessage("payments.default.error.message");
    }
    return errorMessage;
  }
  
  /**
   * <code>processCustomerProfileResponse</code> method will return the customer profile detail, if
   * not customer profile is present.
   * 
   * @param gatewayRequest
   *          paymentGateway request will send the gateway request.
   * @param transactionResponse
   *          response returned from the response.
   * @param profileResponse
   *          profile reponse
   * @param gatewayResponse
   *          gateway Resposne
   */
  private void processCustomerProfileResponse(PaymentGatewayRequest gatewayRequest,
      TransactionResponse transactionResponse, CreateProfileResponse profileResponse,
      PaymentGatewayResponse gatewayResponse) {
    
    MessagesType messages = profileResponse.getMessages();
    MessageTypeEnum resultCode = messages.getResultCode();
    switch (resultCode) {
    case OK:
      LOG.debug("Profile created successfull for the user ");
      String customerProfileId = profileResponse.getCustomerProfileId();
      ArrayOfNumericString customerPaymentProfileIdList = profileResponse
          .getCustomerPaymentProfileIdList();
      List<String> paymentProfileIds = customerPaymentProfileIdList.getNumericString();
      /* get the payment profile ids */
      if (!CollectionUtils.isEmpty(paymentProfileIds)) {
        String paymentProfileId = paymentProfileIds.get(0);
        gatewayRequest.getInstrument().setAuthroziedNetPaymentProfileId(paymentProfileId);
        gatewayResponse.setAuthorizedNetProfileId(customerProfileId);
      }
      maskGatewayRequest(gatewayRequest.getInstrument());
      break;
    default:
      LOG.error(handleErrorResponse(messages));
      break;
    }
  }
  
  /**
   * <code>getTransactionRequest</code> method will return new transaction request for making a new
   * request.
   * 
   * @param gatewayRequest
   *          contains the information of the credit card or the customer profile id.
   * @return the transaction Request.
   */
  private CreateTransactionRequest getTransactionRequest(PaymentGatewayRequest gatewayRequest) {
    
    /* Create a transaction requet */
    CreateTransactionRequest createTransactionRequest = new CreateTransactionRequest();
    createTransactionRequest.setRefId(gatewayRequest.getRefId());
    
    /* populate merchannt account information */
    MerchantAuthenticationType merchantAuthenticationType = getMerchantAuthenticationType();
    createTransactionRequest.setMerchantAuthentication(merchantAuthenticationType);
    populateTransactionInformation(gatewayRequest, createTransactionRequest);
    return createTransactionRequest;
  }
  
  /**
   * <code>populateTransactionRequestType</code> method will set the credit card or payment profile
   * information for the request.
   * 
   * @param gatewayRequest
   *          payment gateway request
   * @param createTransactionRequest
   *          create transaction Request.
   */
  private void populateTransactionInformation(PaymentGatewayRequest gatewayRequest,
      CreateTransactionRequest createTransactionRequest) {
    /* create trnasaction type */
    TransactionRequestType value = new TransactionRequestType();
    value.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
    
    /* set the amount */
    value.setAmount(BigDecimal.valueOf(gatewayRequest.getAmount()));
    
    CustomerProfilePaymentType customerProfileType = new CustomerProfilePaymentType();
    CustomerDataType customerDataType = new CustomerDataType();
    customerDataType.setId(gatewayRequest.getCustomerId());
    /* Create a customer profile */
    value.setProfile(customerProfileType);
    value.setCustomer(customerDataType);
    
    PaymentInstrument instrument = gatewayRequest.getInstrument();
    
    /* check if customer create profile exist */
    if (StringUtils.isBlank(instrument.getAuthroziedNetPaymentProfileId())
        && StringUtils.isBlank(gatewayRequest.getAuthorizedNetProfileId())) {
      
      customerProfileType.setCreateProfile(true);
      PaymentType paymentType = getPaymentType(instrument);
      value.setPayment(paymentType);
    } else if (StringUtils.isNotBlank(instrument.getAuthroziedNetPaymentProfileId())
        && StringUtils.isNotBlank(gatewayRequest.getAuthorizedNetProfileId())) {
      customerProfileType.setCustomerProfileId(gatewayRequest.getAuthorizedNetProfileId());
      PaymentProfile paymentProfile = new PaymentProfile();
      paymentProfile.setPaymentProfileId(instrument.getAuthroziedNetPaymentProfileId());
      customerProfileType.setPaymentProfile(paymentProfile);
      /* Create a customer profile */
      value.setProfile(customerProfileType);
    } else {
      throw new PaymentGatewayException("Invalid Request Parameters");
    }
    
    // Set the order type
    OrderType orderType = new OrderType();
    orderType.setDescription(gatewayRequest.getReason());
    orderType.setInvoiceNumber(gatewayRequest.getInvoiceNumber());
    value.setOrder(orderType);
    createTransactionRequest.setTransactionRequest(value);
    
    if (Constants.PAYMENTGATEWAY_IS_TAX_EXEMPT) {
      value.setTaxExempt(Boolean.TRUE);
    }
  }
  
  /**
   * <code>getPaymentType</code> method returns the paymentType.
   * 
   * @param instrument
   *          payment instrument.
   * @return the payment type.
   */
  private PaymentType getPaymentType(PaymentInstrument instrument) {
    
    /* create credit type instance for holding the credit card information */
    CreditCardType cardType = new CreditCardType();
    
    /* set the card number */
    cardType.setCardNumber(instrument.getCardNumber());
    
    /* set the credi card cvv number */
    cardType.setCardCode(instrument.getCvv());
    // Expiration date set, in the format of mmyy
    cardType.setExpirationDate(instrument.getMonth().concat(instrument.getYear()));
    PaymentType paymentType = new PaymentType();
    paymentType.setCreditCard(cardType);
    return paymentType;
  }
  
  /**
   * <code>populateMerchantAuthenticationType</code> method will populate the merchange information.
   * 
   * @param createTransactionRequest
   *          transactionRequest to set the merchnat information.
   */
  private MerchantAuthenticationType getMerchantAuthenticationType() {
    MerchantAuthenticationType authenticationType = new MerchantAuthenticationType();
    
    authenticationType.setName(enviornment
        .getProperty(Constants.SUREPEOPLE_AUTHORIZED_NET_ACCOUNT_ID));
    authenticationType.setTransactionKey(enviornment
        .getProperty(Constants.SUREPEOPLE_AUTHORIZED_NET_TRANSACTION_KEY));
    return authenticationType;
  }
  
  /**
   * <code>marksGatwayRequest</code> method will mask the payment information(card no., cvv and
   * expiry date.
   * 
   * @param gatewayRequest
   *          paymenet gatway request.
   */
  private void maskGatewayRequest(PaymentInstrument instrument) {
    instrument.setCardNumber(null);
    instrument.setCvv(null);
    instrument.setCvv(null);
    instrument.setMonth(null);
    instrument.setYear(null);
  }
}