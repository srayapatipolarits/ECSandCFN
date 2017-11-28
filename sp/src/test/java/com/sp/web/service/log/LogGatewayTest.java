package com.sp.web.service.log;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.sp.web.model.User;
import com.sp.web.model.log.ActivityLogMessage;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.NotificationLogMessage;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class LogGatewayTest extends SPTestBase {

  @Autowired
  @Qualifier("activityLog")
  LogGateway activityLog;

  @Autowired
  @Qualifier("notificationLog")
  LogGateway notificationLog;
  
  @Test
  public void testActivityLog() {
    try {
      dbSetup.removeAll("activityLogMessage");
      //LogGateway logGateway = ApplicationContextUtils.getBean(LogGateway.class);
      assertNotNull(activityLog);
      
      User user = dbSetup.getDefaultUser();
      activityLog.logActivity(new LogRequest(LogActionType.ProfileWelcome, user));
      
      Thread.sleep(1000);
      
      List<ActivityLogMessage> activityLogMessages = dbSetup.getAll(ActivityLogMessage.class);
      assertThat(activityLogMessages, hasSize(1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testNotificationLog() {
    try {
      dbSetup.removeAll("notificationLogMessage");
      assertNotNull(notificationLog);
      
      User user = dbSetup.getDefaultUser();
      notificationLog.logNotification(new LogRequest(LogActionType.ProfileWelcome, user));
      
      Thread.sleep(1000);
      
      List<NotificationLogMessage> notificationLogMessages = dbSetup.getAll(NotificationLogMessage.class);
      assertThat(notificationLogMessages, hasSize(1));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
}
