package com.sp.web.scheduler;

import static org.junit.Assert.fail;

import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The test class for the internal marketing test cases.
 */
public class InternalMarketingTest extends SPTestBase {
  
  @Autowired
  InternalMarketingScheduler scheduler; 
  
  @Before
  public void setup1() throws Exception {
    // login the user
    testSmtp.start();
  }

  @After
  public void after() {
    testSmtp.stop();
  }
  
  @Test
  public void testProcess() {
    try {
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      List<User> all = dbSetup.getAll(User.class);
      for (User user : all) {
        if (user.getAnalysis() == null) {
          user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
          dbSetup.addUpdate(user);
        }
      }
      scheduler.process();
      Thread.sleep(5000);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
