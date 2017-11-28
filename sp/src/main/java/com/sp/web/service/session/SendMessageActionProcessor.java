package com.sp.web.service.session;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vikram
 *
 *         The class to process SendMessage action
 */
@Component("SendMessageActionProcessor")
public class SendMessageActionProcessor implements UpdateSessionActionProcessor {

  private static final Logger LOG = Logger.getLogger(UpdateProfileActionProcessor.class);

  /**
   * Method populates updated Message to updatesMap. This message will be send in response to FE
   * 
   */
  @Override
  public void doUpdate(User user, UserUpdateAction action, Map<String, Object> params,
      Map<String, Map<UserUpdateAction, Object>> updatesMap, SessionInformation session) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering SendMessageActionProcessor doUpdate");
    }
    
    Map<UserUpdateAction, Object> actionMap = updatesMap.get(session.getSessionId()); 

    //First request, new map will be created. For subsequent requests for same user, reuse the map
    if(actionMap == null) {
      actionMap = new HashMap<UserUpdateAction, Object>();
    }
    
    List actionList = new ArrayList<>();    
    actionList.add(MessagesHelper.getMessage(Constants.USER_UPDATE_ACTION +action.toString(),user.getFirstName(),user.getLastName()));
    actionList.add(params);
    actionMap.put(action, actionList);
       
    updatesMap.put(session.getSessionId(), actionMap);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Successfully updated actionMap and updatesMap for user:" + user.getId()
        + " and action:" + action);
    }
  }
  
}
