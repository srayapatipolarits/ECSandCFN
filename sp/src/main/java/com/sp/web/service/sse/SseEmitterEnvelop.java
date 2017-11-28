package com.sp.web.service.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SseEmitterEnvelop class is the holder for sse emtter for each user against the session id by
 * which user is logged in to the system.
 * 
 * @author pradeepruhil
 *
 */
public class SseEmitterEnvelop {
  
  
  /** user id of the person. */
  private String userId;
  
  /**
   * sseEmmitter map holds the sseMietter agains the session id.
   */
  private  Map<String, SseEmitter> sseEmitterMap;
  
  public void setSseEmitterMap(Map<String, SseEmitter> sseEmitterMap) {
    this.sseEmitterMap = sseEmitterMap;
  }
  
  /**
   * returns the ssemitter map.
   * 
   * @return the sseEmitterMap
   */
  public Map<String, SseEmitter> getSseEmitterMap() {
    if (sseEmitterMap == null) {
      sseEmitterMap = new HashMap<String, SseEmitter>();
    }
    return sseEmitterMap;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public String getUserId() {
    return userId;
  }

  @Override
  public String toString() {
    return "SseEmitterEnvelop [userId=" + userId + ", sseEmitterMap=" + sseEmitterMap + "]";
  }
  
  public Set<String> allKeys() {
    return getSseEmitterMap().keySet();
  }
  
}
