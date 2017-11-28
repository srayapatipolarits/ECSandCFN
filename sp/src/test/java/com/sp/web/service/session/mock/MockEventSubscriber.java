/**
 * 
 */
package com.sp.web.service.session.mock;

import com.sp.web.service.sse.EventActionProcessor;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.utils.ApplicationContextUtils;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author pradeepruhil
 *
 */
@Component
@Profile({ "Test" })
public class MockEventSubscriber {
  
  public void receiveEvents(MessageEventRequest messageEventRequest) {
    String eventProcessor = messageEventRequest.getEventActionType().getEventProcessor();
    
    EventActionProcessor eventActionProcessor = (EventActionProcessor) ApplicationContextUtils
        .getBean(eventProcessor);
    
    /* call the action processor. */
    eventActionProcessor.handleRequest(messageEventRequest);
  }
}
