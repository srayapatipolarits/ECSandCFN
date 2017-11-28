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

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * <code>UpdateNavigationEventProcessor</code> will send the updated navigation to all the users.
 * 
 * @author pradeepruhil
 *
 */
@Component("updateNavigationEventProcessor")
public class UpdateNavigationEventProcessor implements EventActionProcessor {
  
  private static final Logger log = Logger.getLogger(UpdateNavigationEventProcessor.class);
  
  @Autowired
  private ApplicationEventListener applicationEventListener;
  
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
   * @see com.sp.web.service.sse.EventActionProcessor#handleRequest(com.sp.web.service.sse.
   *      MessageEventRequest)
   */
  @Override
  public void handleRequest(MessageEventRequest eventRequest) {
    SPResponse response = new SPResponse();
    if (process(eventRequest, response)) {
      if (!eventRequest.isAllMembers() && !CollectionUtils.isEmpty(eventRequest.getUserIds())) {
        CompanyDao company = companyFactory.getCompany(eventRequest.getCompanyId());
        eventRequest
            .getUserIds()
            .stream()
            .forEach(
                uid -> {
                Set<String> sessionIds = eventFactory.getAllSessionIdForUser(
                    eventRequest.getCompanyId(), uid);
                
                if (!CollectionUtils.isEmpty(sessionIds)) {
                  User updatedUser = userFactory.getUser(uid);
                  sessionIds.stream().forEach(sessId -> {
                      User user = sessionFactory.getUserForSession(sessId);
                      if (user != null) {
                        BeanUtils.copyProperties(updatedUser, user);
                        Set<RoleType> roles = company.getRoleList();
                        if (roles != null) {
                          user.getRoles().addAll(roles);
                        }
                        /* Process the response for the navigation to be sent to the user */
                        user.setUserUpdatedInSession(true); 
                        NavigationDTO navigation = navigationFactory.getNavigation(updatedUser);
                        response.add(Constants.PARAM_NAVIGATION, navigation);
                      }
                    });
                  applicationEventListener.sendEvents(response, uid, eventRequest);
                  log.debug("User " + uid + ", navigation is " + response);
                }
              });

      } else {
        
        Set<String> sessionIds = eventFactory
            .getAllSessionIdForCompany(eventRequest.getCompanyId());
        if (!CollectionUtils.isEmpty(sessionIds)) {
          CompanyDao company = companyFactory.getCompany(eventRequest.getCompanyId());
          sessionIds.stream().forEach(sessId -> {
            
            User userForSession = sessionFactory.getUserForSession(sessId);
            if (userForSession != null) {
              /* update the features to the user */
              userForSession.getRoles().clear();
              User userRepository = userFactory.getUser(userForSession.getId());
              BeanUtils.copyProperties(userRepository, userForSession);
              Set<RoleType> roles = company.getRoleList();
              if (roles != null) {
                userForSession.getRoles().addAll(roles);
              }
              userForSession.setUserUpdatedInSession(true);
              /* Process the response for the navigation to be sent to the user */
              NavigationDTO navigation = navigationFactory.getNavigation(userForSession);
              response.add(Constants.PARAM_NAVIGATION, navigation);
              applicationEventListener.sendEvents(response, userForSession.getId(),
                  eventRequest);
              log.debug("User " + userForSession + ", navigation is " + response);
            }
          });
        }
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
