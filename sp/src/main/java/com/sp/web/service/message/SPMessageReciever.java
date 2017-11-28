package com.sp.web.service.message;

import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.messaging.Message;

public class SPMessageReciever {
  
  private static final Logger log = Logger.getLogger(SPMessageReciever.class);
  
  /**
   * onMessage will receive the messages event and will do the processing.
   * 
   * @param message
   *          to be recieved.
   */
  public void onMessage(Message<SPMessageEnvelop> message) {
    
//    if (log.isInfoEnabled()) {
//      log.info("Message recieved is " + message);
//      ;
//    }
    // String processorName = message.getPayload().getServiceProcessor();
    
    /* get the bean */
//    if (log.isDebugEnabled()) {
//      log.debug("Message handler type: " + message.getPayload().getMessageHandler());
//    }
    MessageHandlerProcessor messageProcessor = (MessageHandlerProcessor) ApplicationContextUtils
        .getBean(message.getPayload().getMessageHandler().getType());
    messageProcessor.process(message);
  }
}
