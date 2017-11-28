package com.sp.web.mvc.test.setup;

import static org.junit.Assert.assertTrue;

import com.sp.web.model.FeedbackUser;
import com.sp.web.test.setup.AuthenticationHelper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SPTestFeedbackUserLoggedInBaseTest extends SPTestBase {

  @Autowired
  protected AuthenticationHelper authenticationHelper;

  boolean isAuthenticationDone = false;

  static{
    System.setProperty("appPropsFile", "./properties/test/");
  }
  @Before
  public void setUp() throws Exception {
    try {
      if (!isAuthenticationDone) {
        dbSetup.removeAllUsers();
        dbSetup.removeAllFeedbackUsers();
        dbSetup.removeAllCompanies();
        dbSetup.createUsers();
        dbSetup.createCompanies();
        dbSetup.createFeedbackUsers();
        FeedbackUser fbUser = dbSetup.getFeedbackUserById("1");
        authenticationHelper.doAuthenticateWithoutPassword(session, fbUser);
        isAuthenticationDone = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testDoNothing() {
    assertTrue(true);
  }
}
