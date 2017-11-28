/**
 * 
 */
package com.sp.web.test.setup;

import com.sp.web.model.User;
import com.sp.web.mvc.signin.LoginHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 */
@Component
public class AuthenticationHelper {

  /**
   * The mongo template to user
   */
  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * The password encoder
   */
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private LoginHelper loginHelper;

  public void authenticateUser(MockHttpSession session) throws Exception {
    DBSetup dbSetup = new DBSetup(mongoTemplate, passwordEncoder);
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllAccounts();
    dbSetup.createUsers();
    dbSetup.createCompanies();
    dbSetup.createAccounts();
    User u = dbSetup.getDefaultUser();

    doAuthenticate(session, u);

  }

  /**
   * @param session
   * @param u
   */
  public void doAuthenticate(MockHttpSession session, User u) {
    loginHelper.authenticateUserAndSetSession(u.getEmail(), u.getPassword());
    SecurityContext context = SecurityContextHolder.getContext();
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
  }

  /**
   * @param session
   * @param u
   */
  public void doAuthenticateWithoutPassword(MockHttpSession session, User u) {
    loginHelper.authenticateUserAndSetSession(u);
    SecurityContext context = SecurityContextHolder.getContext();
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
  }

  public void updateUser(MockHttpSession session, User u) {
    loginHelper.updateUser(u);
    SecurityContext context = SecurityContextHolder.getContext();
    session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
  }
}
