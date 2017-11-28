/**
 * 
 */
package com.sp.web.service.sse;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author pradeepruhil
 *
 */
@Component
public class LogoutListener implements ApplicationListener<SessionDestroyedEvent> {
  
  @Autowired
  private ApplicationEventListener eventListener;
  
  /**
   * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context
   *      .ApplicationEvent)
   */
  @Override
  public void onApplicationEvent(SessionDestroyedEvent event) {
    
    List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
    String sessionId = event.getId();
    User user = null;
    for (SecurityContext securityContext : lstSecurityContext) {
      user = GenericUtils.getUserFromAuthentication(securityContext.getAuthentication());
      if (user != null) {
        SPResponse response = new SPResponse();
        response.add(Constants.PARAM_ACTION_TYPE, ActionType.Logout);
        eventListener.sendRemoveEvents(response, user.getCompanyId(), user.getId(), sessionId);
      }
    }
  }
  
}
