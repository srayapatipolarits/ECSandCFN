package com.sp.web.service.sse;


/**
 * EventGatway interface sends events from the server.
 * 
 * @author pradeepruhil
 *
 */
public interface EventGateway {
  
  /**
   * sendEvent is the gateway for the all the events from server.
   * 
   * @param messageEventRequest message event request.
   */
  public void sendEvent(MessageEventRequest messageEventRequest);
}
