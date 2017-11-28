package com.sp.web.service.message;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

/**
 * SPMessageGateway is the gateway to send message events to the receiver server. Message will be
 * published and will reach all the subscribed servers for processing.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SPMessageGateway {
  
  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(SPMessageGateway.class);
  /**
   * JMSTempalte which will publish the message to the topic configured in integration-publish.xml
   * Message will be sent asynchronously to the receiver.
   */
  @Autowired
  @Qualifier("jmsTemplatePub")
  private JmsTemplate publisher;
  
  /**
   * sendMessage will send the message to the publishers.
   * 
   * @param message
   *          to be sent.
   */
  public void sendMessage(SPMessageEnvelop spMessage) {
    
    if (log.isInfoEnabled()) {
      log.info("Mesage to be sent " + spMessage);
    }
    Message<SPMessageEnvelop> message = new GenericMessage<SPMessageEnvelop>(spMessage);
    publisher.convertAndSend(message);
  }
}
