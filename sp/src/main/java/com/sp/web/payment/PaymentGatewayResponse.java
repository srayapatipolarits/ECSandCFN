package com.sp.web.payment;

/**
 * @author Dax Abraham
 *
 *         The bean class to store the payment response from the payment gateway.
 */
public class PaymentGatewayResponse {

  private String txnId;
  private boolean isSuccess;
  private String errorMessage;
  private String authorizedNetProfileId;
  private String cardType;

  public String getTxnId() {
    return txnId;
  }

  public void setTxnId(String txnId) {
    this.txnId = txnId;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public void setAuthorizedNetProfileId(String authorizedNetProfileId) {
    this.authorizedNetProfileId = authorizedNetProfileId;
  }

  public String getAuthorizedNetProfileId() {
    return authorizedNetProfileId;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public String getCardType() {
    return cardType;
  }

}
