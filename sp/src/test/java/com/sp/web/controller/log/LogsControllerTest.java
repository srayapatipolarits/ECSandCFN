package com.sp.web.controller.log;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.log.NotificationLogMessage;
import com.sp.web.model.log.UserNotificationsSummary;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.repository.log.LogsRepository;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

public class LogsControllerTest extends SPTestLoggedInBase {

  @Autowired
  @Qualifier("activityLog")
  LogGateway activityLog;

  @Autowired
  @Qualifier("notificationLog")
  LogGateway notificationLog;
  
  @Autowired
  LogsRepository logsRepository;
  
  @Test
  public void testGetNotificationLogs() {
    try {
      // remove any previously created users
      dbSetup.removeAll("notificationLogMessage");
      dbSetup.removeAll("userNotificationsSummary");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/logs/getDashboardNotificationLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notifyCount").value(0))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<UserNotificationsSummary> all = dbSetup.getAll(UserNotificationsSummary.class);
      UserNotificationsSummary userNotificationsSummary = all.get(0);
      userNotificationsSummary.setCount(10);
      dbSetup.addUpdate(userNotificationsSummary);
      
      result = this.mockMvc
          .perform(
              post("/logs/getDashboardNotificationLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notifyCount").value(10))
          .andReturn();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testResetNotificationLogs() {
    try {
      // remove any previously created users
      dbSetup.removeAll("notificationLogMessage");
      dbSetup.removeAll("userNotificationsSummary");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/logs/resetNotificationLogsCount")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<UserNotificationsSummary> all = dbSetup.getAll(UserNotificationsSummary.class);
      UserNotificationsSummary userNotificationsSummary = all.get(0);
      userNotificationsSummary.setCount(10);
      dbSetup.addUpdate(userNotificationsSummary);
      
      result = this.mockMvc
          .perform(
              post("/logs/resetNotificationLogsCount")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      
      all = dbSetup.getAll(UserNotificationsSummary.class);
      userNotificationsSummary = all.get(0);
      assertTrue(userNotificationsSummary.getCount() == 0);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testMarkAsRead() {
    try {
      // remove any previously created users
      dbSetup.removeAll("notificationLogMessage");
      dbSetup.removeAll("userNotificationsSummary");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/logs/markAsRead")
              .param("notificationId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Notification id required."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/logs/markAsRead")
              .param("notificationId", "abc")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("Log not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getDefaultUser();
      User user2 = dbSetup.getUser("dax@surepeople.com");
      LogRequest logRequest = new LogRequest(LogActionType.FeedbackCompleted, user2, user);
      notificationLog.logNotification(logRequest);
      Thread.sleep(1000);
      List<NotificationLogMessage> all = dbSetup.getAll(NotificationLogMessage.class);
      NotificationLogMessage notificationLogMessage = all.get(0);

      result = this.mockMvc
          .perform(
              post("/logs/markAsRead")
              .param("notificationId", notificationLogMessage.getId())
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String notificationId = notificationLogMessage.getId();
      notificationLogMessage = logsRepository.notificationsLogFindById(notificationId);
      assertTrue(notificationLogMessage.isRead());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
  
  @Test
  public void testGetAllNotificationLogs() {
    try {
      // remove any previously created users
      dbSetup.removeAll("notificationLogMessage");
      dbSetup.removeAll("userNotificationsSummary");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/logs/getAllNotificationLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notifications", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      User user2 = dbSetup.getUser("dax@surepeople.com");
      LogRequest logRequest = new LogRequest(LogActionType.FeedbackCompleted, user2, user);
      notificationLog.logNotification(logRequest);
      Thread.sleep(1000);
      
      result = this.mockMvc
          .perform(
              post("/logs/getAllNotificationLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.notifications", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testGetActivityLogs() {
    try {
      // remove any previously created users
      dbSetup.removeAll("activityLogMessage");
      
      // invalid request no company
      MvcResult result = this.mockMvc
          .perform(
              post("/logs/getDashboardActivityLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.activityMessages", hasSize(0)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      User user = dbSetup.getUser("admin@admin.com");
      final User user2 = dbSetup.getUser("dax@surepeople.com");
      Thread.sleep(1000);
      notificationLog.logActivity(new LogRequest(LogActionType.SignupWelcome, user));
      Thread.sleep(1000);
      notificationLog.logActivity(new LogRequest(LogActionType.SignupWelcome, user2));
      Thread.sleep(1000);
      
      result = this.mockMvc
          .perform(
              post("/logs/getDashboardActivityLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.activityMessages", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      for (int i = 0; i < Constants.DASHBOARD_LOG_MESSAGE_LIMIT + 1; i++) {
        notificationLog.logActivity(new LogRequest(LogActionType.SignupWelcome, user));
      }
      Thread.sleep(3000);
      result = this.mockMvc
          .perform(
              post("/logs/getDashboardActivityLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.activityMessages", hasSize(Constants.DASHBOARD_LOG_MESSAGE_LIMIT)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      result = this.mockMvc
          .perform(
              post("/logs/getAllActivityLogs")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.activityMessages", hasSize(32)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      result = this.mockMvc
          .perform(
              post("/logs/getUserActivityLogs")
              .param("userId", " ")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("User not found."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      final String userId = user2.getId();
      
      result = this.mockMvc
          .perform(
              post("/logs/getUserActivityLogs")
              .param("userId", userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.activityMessages", hasSize(1)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
}
