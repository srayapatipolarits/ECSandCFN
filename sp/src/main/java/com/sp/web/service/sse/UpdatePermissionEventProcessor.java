package com.sp.web.service.sse;

import com.sp.web.Constants;
import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.navigation.NavigationDTO;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.navigation.NavigationFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.user.UserFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * UpdatePermissionEventProcessor event processor will update the permission of the users in session
 * and will send the updated navigation to the user in case user has updated navigation.
 * 
 * @author pradeepruhil
 *
 */
@Component("updatePermissionEvent")
public class UpdatePermissionEventProcessor extends DefaultEventProcessor {
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private NavigationFactory navigationFactory;
  
  @Autowired
  private SseEventFactory eventFactory;
  
  @Autowired
  private SessionFactory sessionFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * @see com.sp.web.service.sse.DefaultEventProcessor#process(com.sp.web.service.sse.MessageEventRequest,
   *      com.sp.web.mvc.SPResponse)
   */
  @Override
  public boolean process(MessageEventRequest eventRequest, SPResponse response) {
    
    List<String> userIds = eventRequest.getUserIds();
    
    if (CollectionUtils.isEmpty(userIds)) {
      return false;
    }
    
    String userId = userIds.get(0);
    if (!eventFactory.isUserRegisteredForSse(eventRequest.getCompanyId(), userId)) {
      return false;
    }
    /* Get all the session associated with the user */
    Set<String> sessionIds = eventFactory.getAllSessionIdForUser(eventRequest.getCompanyId(),
        userId);
    
    if (!CollectionUtils.isEmpty(sessionIds)) {
      CompanyDao company = companyFactory.getCompany(eventRequest.getCompanyId());
      User updatedUser = userIds.stream().map(userFactory::getUser).findFirst().get();
      sessionIds.stream().forEach(sessId -> {
          User user = sessionFactory.getUserForSession(sessId);
          if (user != null) {
            BeanUtils.copyProperties(updatedUser, user);
            Set<RoleType> roles = company.getRoleList();
            if (roles != null) {
              user.getRoles().addAll(roles);
            }
            user.setUserUpdatedInSession(true);
          }
        });
      
      /* Process the response for the navigation to be sent to the user  */
      NavigationDTO navigation = navigationFactory.getNavigation(updatedUser);
      response.add(Constants.PARAM_NAVIGATION, navigation);
    }
    return super.process(eventRequest, response);
  }
}
