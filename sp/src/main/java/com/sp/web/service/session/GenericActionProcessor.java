package com.sp.web.service.session;

import com.sp.web.model.User;

import org.apache.log4j.Logger;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vikram
 *
 *         The class to process Generic action
 */
@Component("GenericActionProcessor")
public class GenericActionProcessor implements UpdateSessionActionProcessor {

  private static final Logger LOG = Logger.getLogger(GenericActionProcessor.class);
  
  /**
   * Method populates params to updatesMap
   * 
   */
  @Override
  public void doUpdate(User user, UserUpdateAction action, Map<String, Object> params,
      Map<String, Map<UserUpdateAction, Object>> updatesMap, SessionInformation session) {

    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering GenericActionProcessor doUpdate");
    }
      
    Map<UserUpdateAction, Object> actionMap = updatesMap.get(session.getSessionId()); 

    //First request, new map will be created. For subsequent requests for same user, reuse the map
    if(actionMap == null)
      actionMap = new HashMap<UserUpdateAction, Object>();
    
    actionMap.put(action, params);
    
    updatesMap.put(session.getSessionId(),actionMap);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Successfully updated actionMap and updatesMap for user:" + user.getId()
        + " and action:" + action);
    }
  }  
}
