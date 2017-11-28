package com.sp.web.service.email;

import org.springframework.integration.annotation.Gateway;

public interface CommunicationGateway {

  /**
   * Send the email message denoted in the message params.
   * 
   * @param emailParams
   */
  @Gateway
  void sendMessage(MessageParams emailParams);
}
