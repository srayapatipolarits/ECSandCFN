package com.sp.web.service.sse;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * SseEventFactory class is the factory class for the Server side events.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class SseEventFactory {
  
  /** Map to store the sseEmitterEneveop. */
  private Map<String, Map<String, SseEmitterEnvelop>> sseEmitters = new HashMap<>();
  
  private Long sseTimeout;
  
  @Inject
  public SseEventFactory(Environment enviornment) {
    sseTimeout = Long.valueOf(enviornment.getProperty("sse.timeout"));
  }
  
  /**
   * getSseEmitter returns the emitter for the user.
   * 
   * @param userId
   *          for which emitter is to be retreived.
   * @return the ssEmitter enveolop.
   */
  public SseEmitter createSseEmitter(String userId, String companyId, String sessionId) {
    SseEmitter sseEmitter = null;
    
    Map<String, SseEmitterEnvelop> sseEmitterEnvelopMap = sseEmitters.get(companyId);
    if (sseEmitterEnvelopMap == null) {
      sseEmitterEnvelopMap = new HashMap<>();
      sseEmitters.put(companyId, sseEmitterEnvelopMap);
    }
    SseEmitterEnvelop sseEmitterEnvelop = sseEmitterEnvelopMap.get(userId);
    if (sseEmitterEnvelop == null) {
      sseEmitterEnvelop = new SseEmitterEnvelop();
      sseEmitterEnvelop.setUserId(userId);
      sseEmitterEnvelopMap.put(userId, sseEmitterEnvelop);
    }
    sseEmitter = sseEmitterEnvelop.getSseEmitterMap().get(sessionId);
    /*
     * clearing the old previous sse and creating a new so that sockets got closed for the previous
     * emitter immediately
     */
    if (sseEmitter != null) {
      sseEmitter.complete();
      removeSseEmitter(companyId, userId, sessionId);
    }
    sseEmitter = new SseEmitter(sseTimeout);
    sseEmitter.onTimeout(new Runnable() {
      @Override
      public void run() {
        removeSseEmitter(companyId, userId, sessionId);
      }
    });
    sseEmitterEnvelop.getSseEmitterMap().put(sessionId, sseEmitter);
    return sseEmitter;
  }
  
  /**
   * getSseEmitter returns the emitter for the user.
   * 
   * @param userId
   *          for which emitter is to be retrieved.
   * @param eventRequest
   *          .g for which emitter is to be retrieved.
   * @return the ssEmitter envelop.
   */
  public Map<String, SseEmitter> getSseEmitter(MessageEventRequest eventRequest, String userId) {
    Map<String, SseEmitterEnvelop> sseEmitterEnvelopMap = sseEmitters.get(eventRequest
        .getCompanyId());
    if (sseEmitterEnvelopMap != null) {
      SseEmitterEnvelop sseEmitterEnvelop = sseEmitterEnvelopMap.get(userId);
      if (sseEmitterEnvelop != null) {
        return getFinalEmitters(eventRequest, userId, sseEmitterEnvelop.getSseEmitterMap());
      }
    }
    return null;
  }
  
  /**
   * getFinalEmitters method will return the emitters for the user.
   * 
   * @param eventRequest
   *          containing the session and unfofllow users list.
   * @param userId
   *          for which emitter is to be retreived
   * @param sseEmitterEnvelopMap
   *          map.
   */
  private Map<String, SseEmitter> getFinalEmitters(MessageEventRequest eventRequest, String userId,
      Map<String, SseEmitter> sseEmitterMap) {
    final Map<String, SseEmitter> filteredEmitterMap = new HashMap<>();
    
    /* skip the users which are in the unfollow list */
    List<String> filterUserIds = eventRequest.getFilterUserIds();
    if (filterUserIds != null && filterUserIds.contains(userId)) {
      return filteredEmitterMap;
    }
    if (sseEmitterMap != null) {
      /* fetch all the emitter and ignore the emitter where sessionid is equal to current user */
      if (StringUtils.hasText(eventRequest.getSessionId())) {
        
        sseEmitterMap.forEach((sesId, emitter) -> {
          if (!sesId.equalsIgnoreCase(eventRequest.getSessionId())) {
            filteredEmitterMap.put(sesId, emitter);
          }
        });
      } else {
        filteredEmitterMap.putAll(sseEmitterMap);
      }
    }
    return filteredEmitterMap;
  }
  
  /**
   * getSseEmitter returns the emitter for all the logged in users of the company.
   * 
   * @param eventRequest
   *          for which emitter is to be retreived.
   * @return the ssEmitter enveolop.
   */
  public Map<String, Map<String, SseEmitter>> getCompanySseEmitters(MessageEventRequest eventRequest) {
    Map<String, SseEmitterEnvelop> sseEmitterEnvelop = sseEmitters.get(eventRequest.getCompanyId());
    Map<String, Map<String, SseEmitter>> filteredEmitterEvenlop = new HashMap<>();
    if (sseEmitterEnvelop != null && sseEmitterEnvelop.size() != 0) {
      sseEmitterEnvelop
          .forEach((uid, emitEnvelop) -> {
            Map<String, SseEmitter> sseEmitterMap = emitEnvelop.getSseEmitterMap();
            if (sseEmitterMap != null && sseEmitterMap.size() != 0) {
              Map<String, SseEmitter> finalEmitters = getFinalEmitters(eventRequest, uid,
                  sseEmitterMap);
              if (!finalEmitters.isEmpty()) {
                filteredEmitterEvenlop.put(uid, finalEmitters);
              }
            }
          });
    }
    return filteredEmitterEvenlop;
  }
  
  /**
   * Remove the SseEmitter for the user against the session.
   * 
   * @param userId
   *          User id.
   * @param sessionId
   *          session for session timeout and logout.
   */
  public SseEmitter getRemovedSseEmitter(String companyId, String userId, String sessionId) {
    Map<String, SseEmitterEnvelop> sseEmitterEnvelopMap = sseEmitters.get(companyId);
    
    if (sseEmitterEnvelopMap != null) {
      SseEmitterEnvelop sseEmitterEnvelop = sseEmitterEnvelopMap.get(userId);
      if (sseEmitterEnvelop != null && sseEmitterEnvelop.getSseEmitterMap() != null) {
        SseEmitter sseEmitter = sseEmitterEnvelop.getSseEmitterMap().get(sessionId);
        if (sseEmitter != null) {
          sseEmitterEnvelop.getSseEmitterMap().remove(sessionId);
          return sseEmitter;
        }  
      }
      
    }
    return null;
    
  }
  
  /**
   * <code>isUserRegisteredForSse</code> method will let user know, if user is registered for SSE or
   * not. By default, every logged in users will be registered for sse.
   * 
   * @param userId
   *          to be checked.
   * @param companyId
   *          of the user.
   * @return true or false.
   */
  public boolean isUserRegisteredForSse(String companyId, String userId) {
    
    Map<String, SseEmitterEnvelop> sseEmMap = sseEmitters.get(companyId);
    if (sseEmMap == null || sseEmMap.size() == 0) {
      return false;
    }
    
    SseEmitterEnvelop sseEmitterEnvelop = sseEmMap.get(userId);
    if (sseEmitterEnvelop == null) {
      return false;
    }
    
    return true;
  }
  
  /**
   * <code>getAllSessionIdForUser</code> method will return all the session information for the
   * user.
   * 
   * @param userId
   *          for which session information is to be retreived.
   * @return the set of session id.
   */
  public Set<String> getAllSessionIdForUser(String companyId, String userId) {
    return sseEmitters.get(companyId).get(userId).getSseEmitterMap().keySet();
  }
  
  /**
   * <code>getAllSessionIdForCompany</code> method will return all the session information for the
   * company.
   * 
   * @param companyId
   *          for which session information is to be retreived.
   * @return the set of session id.
   */
  public Set<String> getAllSessionIdForCompany(String companyId) {
    Set<String> sessionIds = new HashSet<>();
    final Map<String, SseEmitterEnvelop> companySseEmittersMap = sseEmitters.get(companyId);
    if (!CollectionUtils.isEmpty(companySseEmittersMap)) {
      companySseEmittersMap.values().stream().map(SseEmitterEnvelop::allKeys)
          .forEach(sessionIds::addAll);
    }
    return sessionIds;
  }
  
  /**
   * <code>removeSseEmitter</code> method will remove the sse emitter for the user.
   * 
   * @param companyId
   *          for the user.
   * @param userId
   *          for whom the sse emitter is to be removed.
   * @param sessId
   *          corresponding to the sse emitter.
   */
  public void removeSseEmitter(String companyId, String userId, String sessId) {
    Map<String, SseEmitterEnvelop> map = sseEmitters.get(companyId);
    if (map != null) {
      SseEmitterEnvelop userEmitterEnveolop = map.get(userId);
      if (userEmitterEnveolop != null) {
        SseEmitter sseEmitter = userEmitterEnveolop.getSseEmitterMap().get(sessId);
        if (sseEmitter != null) {
          sseEmitter.complete();
        }
        userEmitterEnveolop.getSseEmitterMap().remove(sessId);
        sseEmitter = null;
      }
    }
  }
  
}
