package com.sp.web.service.session;

import com.sp.web.model.User;

import org.apache.log4j.Logger;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author vikram
 *
 *         The class to process Server Logout action
 */
@Component("ServerLogoutActionProcessor")
public class ServerLogoutActionProcessor implements UpdateSessionActionProcessor {
  
  private static final Logger LOG = Logger.getLogger(ServerLogoutActionProcessor.class);

  /**
   * Expire logged in user session
   * 
   */
  @Override
  public void doUpdate(User user, UserUpdateAction action, Map<String, Object> params,
      Map<String, Map<UserUpdateAction, Object>> updatesMap, SessionInformation session) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering ServerLogoutActionProcessor doUpdate");
    }
    
    session.expireNow();

    if (LOG.isDebugEnabled()) {
      LOG.debug("Session Expired for User:" + user.getId()
        + "and action:" + action);
    }
  }
  
}
