package com.sp.web.service.sse;

import com.sp.web.mvc.SPResponse;

/**
 * <code>EventActionProcessor</code> class is the event processor class for the processing the
 * events and return the response.
 * 
 * @author pradeepruhil
 *
 */
public interface EventActionProcessor {
  
  /**
   * <code>handleRequest</code> method will process the event request and send the response to the
   * event listener to send the events to the client.
   * 
   * @param eventRequest
   *          Message event request.
   */
  void handleRequest(MessageEventRequest eventRequest);
  
  /**
   * process method will take the event request and creates the spresponse to be send to client in
   * case it returns true.
   * 
   * @param eventRequest
   *          Message event request.
   * @param response
   *          SPResponse to be sent to client.
   * @return true if response is to be sent to client else false.
   */
  boolean process(MessageEventRequest eventRequest, SPResponse response);
}
