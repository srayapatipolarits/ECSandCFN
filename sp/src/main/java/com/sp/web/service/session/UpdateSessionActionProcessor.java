package com.sp.web.service.session;

import com.sp.web.model.User;

import org.springframework.security.core.session.SessionInformation;

import java.util.Map;

/**
 * @author vikram
 * 
 *         The interface to process user action and populate Map with respective values
 */
public interface UpdateSessionActionProcessor {
  
  /**
   * Adds UserUpdateAction & its related object for the logged in user in updatesMap
   * 
   * @param user
   *          - the user whose profile, permission, role got updated
   * @param action
   *          - the UserUpdateAction
   * @param params
   *          - the map can be used for passing any generic information/message to logged in user
   * @param updatesMap
   *          - the map holds mapping of sessionid & map of action and Object. <sessionid: Map<action,object>>
   * @param session
   *          - logged in user session which is updated
   */
  public void doUpdate(User user, UserUpdateAction action, Map<String, Object> params,
      Map<String, Map<UserUpdateAction, Object>> updatesMap, SessionInformation session);
  
}
