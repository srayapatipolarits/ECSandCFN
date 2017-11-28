/**
 * 
 */
package com.sp.web.service.session.mock;

import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author pradeepruhil
 *
 */
@Component("mockEventGateway")
@Profile({ "Test" })
public class MockEventGatewayImpl implements EventGateway {
  
  private static final Logger LOG = Logger.getLogger(MockEventGatewayImpl.class);
  
  @Autowired
  private MockEventSubscriber mockEventSubscriber;
  
  /**
   * @see com.sp.web.service.sse.EventGateway#sendEvent(com.sp.web.service.sse.MessageEventRequest)
   */
  @Override
  public void sendEvent(MessageEventRequest messageEventRequest) {
    LOG.debug("Event sent successfully. " + messageEventRequest);
    mockEventSubscriber.receiveEvents(messageEventRequest);
  }
  
}
