package com.sp.web.service.sse;

import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * <code>EventSubscriber</code> class is the subscriber for all the events.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class EventSubscriber {

  private static final Logger log = Logger.getLogger(EventSubscriber.class);

  /**
   * processEventRequest is the event request for the pro processEventRequest
   * 
   * @param messageEventRequest
   *          contains the message event request.
   */
  public void processEventRequest(MessageEventRequest messageEventRequest) {

    String eventProcessor = messageEventRequest.getEventActionType()
        .getEventProcessor();

    EventActionProcessor eventActionProcessor = (EventActionProcessor) ApplicationContextUtils
        .getBean(eventProcessor);

    /* call the action processor. */
    eventActionProcessor.handleRequest(messageEventRequest);
  }
}
