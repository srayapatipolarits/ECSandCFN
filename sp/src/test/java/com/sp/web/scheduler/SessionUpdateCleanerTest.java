package com.sp.web.scheduler;

import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.model.SessionUpdateActionRequest;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.service.session.SessionManagementService;
import com.sp.web.service.session.SessionManagementServiceTest;
import com.sp.web.service.session.UserUpdateAction;
import com.sp.web.test.setup.AuthenticationHelper;
import com.sp.web.utils.ApplicationContextUtils;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.session.SessionRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class SessionUpdateCleanerTest extends SPTestLoggedInBase {

  @Autowired
  SessionUpdateCleaner sessionCleaner;

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  SessionFactory sessionFactory;
 
  
  @Test
  public void cleanSessionUpdateRequestsTest() {
    
    User user1 = dbSetup.getUser("pradeep1@surepeople.com");
    User user3 = dbSetup.getUser("pradeep3@surepeople.com");
    
    User adminUser = dbSetup.getUser("admin@admin.com");
    user1.setPassword("password");
    adminUser.setPassword("admin");
    
    authenticationHelper.doAuthenticate(session, user1);
    authenticationHelper.doAuthenticate(session2, adminUser);
    
    SessionRegistry sessionRegistry =  ApplicationContextUtils.getApplicationContext().getBean(SessionRegistry.class);
    MockHttpSession session3 = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());
    MockHttpSession session4 = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());

    sessionRegistry.registerNewSession(session.getId(), user1);
    sessionRegistry.registerNewSession(session2.getId(), adminUser);
    sessionRegistry.registerNewSession(session3.getId(), user3);
    sessionRegistry.registerNewSession(session4.getId(), user3);
    
    Map<String,Object> params = new HashMap<String, Object>();

    SessionUpdateActionRequest request1 = new SessionUpdateActionRequest();
    request1.setUserId(user1.getId());
    request1.setAction(UserUpdateAction.UpdatePermission);
    request1.setParams(params);


    SessionUpdateActionRequest request3 = new SessionUpdateActionRequest();
    request3.setUserId(user3.getId());
    request3.setAction(UserUpdateAction.SendMessage);
    request3.setParams(params);

    //test multiple action for different user
    sessionFactory.doUpdate(request1);
    sessionFactory.doUpdate(request3);
    
    //call clean when user present in session and map both
    sessionCleaner.cleanSessionUpdateRequests();
    
    request3.setAction(UserUpdateAction.ServerLogout);
    sessionFactory.doUpdate(request3);
    
    //call clean when user present in map only/after user logout
    sessionCleaner.cleanSessionUpdateRequests();

  }

}
