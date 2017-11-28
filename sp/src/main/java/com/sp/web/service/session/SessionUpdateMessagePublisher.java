package com.sp.web.service.session;

import com.sp.web.model.SessionUpdateActionRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * Class to publish messages to Topic
 * 
 * @author vikram
 *
 */

@Deprecated
public class SessionUpdateMessagePublisher {

  @Autowired
  private JmsTemplate jmsTemplate;

  /**
   * method will send message to Topic using jmstemplate
   * 
   * @param InvalidateSessionRequest request
   *
   */
  @Deprecated
  public void sendSessionUpdateMessage(SessionUpdateActionRequest request) {
    jmsTemplate.convertAndSend(request);
  }
  
}
