package com.sp.web.service.session;

import static org.junit.Assert.assertNotNull;

import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.model.SessionUpdateActionRequest;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
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

public class SessionManagementServiceTest extends SPTestLoggedInBase {
  
  @Autowired
  @Qualifier("sessionManagementService")
  SessionManagementService sessionService;

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  SessionFactory sessionFactory;

  
  @Test
  public void testUpdateSession() {
    assertNotNull(sessionService);
    
    Map<String,Object> params = new HashMap<String, Object>();
    
    User user = dbSetup.getUser("pradeep1@surepeople.com");
    User adminUser = dbSetup.getUser("admin@admin.com");
    user.setPassword("password");
    adminUser.setPassword("admin");
    
    authenticationHelper.doAuthenticate(session, user);
    authenticationHelper.doAuthenticate(session, adminUser);
    
    sessionService.updateSession(user.getId(), UserUpdateAction.UpdatePermission, params);
    
  }

  @Test
  public void testSessionFactoryDoUpdate() {
      assertNotNull(sessionService);

      MockHttpSession session3 = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());
      MockHttpSession session1 = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());
            
      User user1 = dbSetup.getUser("pradeep1@surepeople.com");
      User user3 = dbSetup.getUser("pradeep3@surepeople.com");
      
      User adminUser = dbSetup.getUser("admin@admin.com");
      user1.setPassword("password");
      adminUser.setPassword("admin");
      
      authenticationHelper.doAuthenticate(session, user1);
      authenticationHelper.doAuthenticate(session2, adminUser);
      
      SessionRegistry sessionRegistry =  ApplicationContextUtils.getApplicationContext().getBean(SessionRegistry.class);
   
      sessionRegistry.registerNewSession(session.getId(), user1);
      sessionRegistry.registerNewSession(session2.getId(), adminUser);
      sessionRegistry.registerNewSession(session3.getId(), user3);
      
      Map<String,Object> params = new HashMap<String, Object>();

      SessionUpdateActionRequest request1 = new SessionUpdateActionRequest();
      request1.setUserId(user1.getId());
      request1.setAction(UserUpdateAction.UpdatePermission);
      request1.setParams(params);


      SessionUpdateActionRequest request3 = new SessionUpdateActionRequest();
      request3.setUserId(user3.getId());
      request3.setAction(UserUpdateAction.SendMessage);
      request3.setParams(params);

      //test multiple action for same user
      sessionFactory.doUpdate(request1);
      request1.setAction(UserUpdateAction.SendMessage);
      sessionFactory.doUpdate(request1);
      sessionFactory.updatePendingActions(user1, new SPResponse());
      request1.setAction(UserUpdateAction.ServerLogout);
      sessionFactory.doUpdate(request1);
      sessionFactory.updatePendingActions(user1, new SPResponse());

      //test multiple action for different user
      request1.setAction(UserUpdateAction.UpdatePermission);
      sessionFactory.doUpdate(request1);
      request3.setAction(UserUpdateAction.SendMessage);      
      sessionFactory.doUpdate(request3);

      //test same user from different sessions for different actions
      sessionFactory.doUpdate(request1);
      sessionRegistry.registerNewSession(session1.getId(), user1);
      request1.setAction(UserUpdateAction.SendMessage);
      sessionFactory.doUpdate(request1);
    
  }
  
}
