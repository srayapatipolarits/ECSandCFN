package com.sp.web.service.message;

import org.springframework.messaging.Message;

/**
 * MessageHandlerProcessor interface defines all the message handler which will proces the messages
 * sent from the publisher.
 * 
 * @author pradeepruhil
 *
 */
public interface MessageHandlerProcessor {
  
  /**
   * process message will process the message.
   * 
   * @param message
   *          which is received.
   */
  public void process(Message<SPMessageEnvelop> message);
}
