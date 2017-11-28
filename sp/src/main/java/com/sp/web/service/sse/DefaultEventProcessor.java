package com.sp.web.service.sse;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Default event processor is the default implemenation for event action processor which will send
 * all the data present in {@link MessageEventRequest#getMessagePayLoad()} in SPResponse.
 * 
 * @author pradeepruhil
 *
 */
@Component
@Qualifier("defaultEventProcessor")
public class DefaultEventProcessor implements EventActionProcessor {
  
  @Autowired
  private ApplicationEventListener applicationEventListener;
  
  /**
   * @see com.sp.web.service.sse.EventActionProcessor#handleRequest(com.sp.web.service.sse.
   *      MessageEventRequest)
   */
  @Override
  public void handleRequest(MessageEventRequest eventRequest) {
    
    SPResponse response = new SPResponse();
    if (process(eventRequest, response)) {
      if (!eventRequest.isAllMembers() && !CollectionUtils.isEmpty(eventRequest.getUserIds())) {
        eventRequest
            .getUserIds()
            .stream()
            .forEach(
                uid -> applicationEventListener.sendEvents(response, uid,
                    eventRequest));
      } else {
        applicationEventListener.sendEvents(response, eventRequest);
      }
    }
  }
  
  /**
   * @see com.sp.web.service.sse.EventActionProcessor#process(com.sp.web.service.sse.MessageEventRequest,
   *      com.sp.web.mvc.SPResponse)
   */
  @Override
  public boolean process(MessageEventRequest eventRequest, SPResponse response) {
    response.add(eventRequest.getMessagePayLoad());
    return true;
  }
  
}
