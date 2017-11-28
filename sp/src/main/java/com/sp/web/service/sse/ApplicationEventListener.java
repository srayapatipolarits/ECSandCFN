package com.sp.web.service.sse;

import com.sp.web.mvc.SPResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * ApplicationEventListener class is the event publishing class.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class ApplicationEventListener {
  
  private static final Logger LOG = Logger.getLogger(ApplicationEventListener.class);
  
  @Autowired
  private SseEventFactory sseEventFactory;
  
  /**
   * publicChannelEventHandler.
   * 
   * @param spResponse
   *          response to be sent to the client.
   */
  public void sendEvents(SPResponse spResponse, String userId, MessageEventRequest eventRequest) {
    
    Map<String, SseEmitter> sseEmitter = sseEventFactory.getSseEmitter(eventRequest, userId);
    if (sseEmitter != null) {
      sendEvent(spResponse, sseEmitter, userId, eventRequest.getCompanyId());
    }
    
  }
  
  /**
   * sendEvents method will send the sseEmitter to the all the logged in users of the company.
   * 
   * @param response
   *          is the response to be sent to all the empoliyess of the company.
   * @param eventRequest
   *          is the companyId.
   */
  public void sendEvents(SPResponse response, MessageEventRequest eventRequest) {
    Map<String, Map<String, SseEmitter>> sseEmitters = sseEventFactory
        .getCompanySseEmitters(eventRequest);
    if (sseEmitters != null && !sseEmitters.isEmpty()) {
      sseEmitters.forEach((uid, emitterMap) -> sendEvent(response, emitterMap, uid,
          eventRequest.getCompanyId()));
    }
    
  }
  
  /**
   * sendEvents method will send the sseEmitter to the all the logged in users of the company.
   * 
   * @param response
   *          is the response to be sent to all the empoliyess of the company.
   * @param companyId
   *          is the companyId.
   */
  public void sendRemoveEvents(SPResponse response, String companyId, String userId,
      String sessionId) {
    SseEmitter sseEmitter = sseEventFactory.getRemovedSseEmitter(companyId, userId, sessionId);
    try {
      if (sseEmitter != null) {
        sseEmitter.send(response, MediaType.APPLICATION_JSON);
        sseEmitter.complete();
      }
    } catch (Exception e) {
      LOG.error("Error occurede while sending event to front", e);
    }
    sseEmitter = null;
  }
  
  /**
   * send event method will send the events to client.
   * 
   * @param spResponseS
   *          response to be sent to client.
   * @param sseEmitter
   *          instance.
   */
  private void sendEvent(SPResponse spResponse, Map<String, SseEmitter> sseEmitterMap,
      String userId, String companyId) {
    if (sseEmitterMap == null || sseEmitterMap.isEmpty()) {
      return;
    }
    
    if (LOG.isDebugEnabled()) {
      for (String key : sseEmitterMap.keySet()) {
        LOG.debug(key + ":" + sseEmitterMap.get(key));
      }
    }
    
    SseEmitter sseMitter = null;
    for (String key : sseEmitterMap.keySet()) {
      try {
        sseMitter = sseEmitterMap.get(key);
        sseMitter.send(spResponse, MediaType.APPLICATION_JSON);
      } catch (Exception e) {
        LOG.error("Error occurede while sending event to front, SessionId: " + key + ", User "
            + userId, e);
        /* exception occurred while sending the sse, removing the sse */
        sseEventFactory.removeSseEmitter(companyId, userId, key);
      }
    }
  }
  
}