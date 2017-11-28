package com.sp.web.controller.notifications;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.icegreen.greenmail.util.GreenMailUtil;
import com.sp.web.Constants;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.MessagesHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;

public class NotificationsControllerTest extends SPTestLoggedInBase {

  @Autowired
  NotificationsControllerHelper helper;
  
  boolean isMockSendMail = false;
  
  private CountDownLatch lock = new CountDownLatch(1);
  
  /**
   * Setup mock for mail sending.
   */
  @Before
  public void setUpNotificationController() {
    //helper.gateway = mock(CommunicationGateway.class);
    testSmtp.start();
  }
  
  @After
  public void cleanup() {
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    testSmtp.stop();
  }

  @Test
  public void testSendNotification() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      
      // invalid notification type
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotification")
                  .param("type", "abc")
                  .param("memberEmail", "dax@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
            .andExpect(status().is(400))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());
      
      // invalid member email
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmail", "dax")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User :dax: not found !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // create a user from a different company
      createUserFromAnotherCompany();
      
      // invalid member from another company
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmail", "dax@fromanothercompany.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.InvalidRequestException").value("Unauthroized request !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // valid member from company
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmail", "dax@surepeople.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // valid member from company
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmail", "dax@surepeople.com")
                  .param("emailBody", "This is a test email.")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))          
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      if (lock.await(2, TimeUnit.SECONDS)) {
        Message[] messages = testSmtp.getReceivedMessages();
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("\r", "").replaceAll("\n", "");
        log.debug("Email body :" + body);
      }
      
      testSmtp.reset();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  /**
   * Create a new user from another company.
   */
  private void createUserFromAnotherCompany() {
    User userFromAnotherCompany = new User();
    userFromAnotherCompany.setEmail("dax@fromanothercompany.com");
    userFromAnotherCompany.setCompanyId("AnotherCompany");
    dbSetup.addUpdate(userFromAnotherCompany);
  }

  @Test
  public void testSendGroupNotification() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      testSmtp.reset();
      
      // invalid notification type
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendGroupNotification")
                  .param("type", "abc")
                  .param("memberEmailList", "dax, admin")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
            .andExpect(status().is(400))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());

      // invalid user email's notification type
      result = this.mockMvc
          .perform(
              post("/notifications/sendGroupNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmailList", "dax, admin")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.memberList", hasSize(2)))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      
      // invalid user from another company
      createUserFromAnotherCompany();
      
      result = this.mockMvc
          .perform(
              post("/notifications/sendGroupNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmailList", "dax@surepeople.com, dax@fromanothercompany.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.memberList", hasSize(1)))
          .andExpect(jsonPath("$.error.memberList[0]").value("dax@fromanothercompany.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // valid users
      result = this.mockMvc
          .perform(
              post("/notifications/sendGroupNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmailList", "dax@surepeople.com, admin@admin.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid users
      result = this.mockMvc
          .perform(
              post("/notifications/sendGroupNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmailList", "dax@surepeople.com, admin@admin.com")
                  .param("isGroupMail", "false")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // valid users
      result = this.mockMvc
          .perform(
              post("/notifications/sendGroupNotification")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("memberEmailList", "dax@surepeople.com, admin@admin.com")
                  .param("isGroupMail", "false")
                  .param("emailBody", "This is a test email.")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      
      try {
        lock.await(2, TimeUnit.SECONDS);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      Message[] messages = testSmtp.getReceivedMessages();
      int twosCounter = 0;
      int onesCounter = 0;
      for (Message msg : messages) {
        int recipientsLength = msg.getAllRecipients().length;
        if (recipientsLength == 2) {
          twosCounter++;
        } else if (recipientsLength == 1) {
          onesCounter++;
        }
      }
      
      assertThat("Email message received is 2 !!!", messages.length, is(6));
      assertThat("Email recipients size is 2 !!!", twosCounter, is(2));
      assertThat("Email recipients size is 2 !!!", onesCounter, is(4));      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testSendNotificationParamsHelper() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      dbSetup.removeAll("notificationLogMessage");
      testSmtp.reset();
      
      // invalid notification type
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", "abc")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
            .andExpect(status().is(400))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getStatus());

      // invalid params helper
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "abc")
                  .param("memberEmailList", "dax, admin")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.NoSuchBeanDefinitionException").value("No notifications processor found :abc"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // invalid request no member list
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "defaultNotificationProcessor")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "No recipients found to send the notification to !!!"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
 
      // invalid request no member list
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "defaultNotificationProcessor")
                  .param(Constants.PARAM_MEMBER_LIST, "dax, admin")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Following members not found in company :dax,admin"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // invalid request one member incorrect
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "defaultNotificationProcessor")
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com, admin")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "Following members not found in company :admin"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // create a user from a different company
      createUserFromAnotherCompany();

      // invalid request one member incorrect
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "defaultNotificationProcessor")
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com, dax@fromanothercompany.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(
              jsonPath("$.error.InvalidRequestException").value(
                  "User not validated :dax@fromanothercompany.com"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      // valid request
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "defaultNotificationProcessor")
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com, admin@admin.com")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());

      // valid request with email body and subject
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.RegistrationWelcome.toString())
                  .param("paramsHelper", "defaultNotificationProcessor")
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com, admin@admin.com")
                  .param(Constants.PARAM_SUBJECT, "This is a test subject.")
                  .param("emailBody", "This is a test email body.")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      try {
        lock.await(2, TimeUnit.SECONDS);
      } catch (Exception e) {
        e.printStackTrace();
      }

      // valid request with email body and subject
      result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.GenericMessage.toString())
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com, admin@admin.com")
                  .param(Constants.PARAM_SUBJECT, "This is a test subject.")
                  .param(Constants.PARAM_MESSAGE, "Something great is being built here.")
                  .param(Constants.PARAM_LOG_TYPE, "Growth")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      Thread.sleep(2000);
      
      Message[] messages = testSmtp.getReceivedMessages();      
      assertThat("Email message received is 4 !!!", messages.length, is(6));
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testProfileReminderCompleteProfile() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      dbSetup.removeAll("notificationLogMessage");
      
      User user = dbSetup.getUser("dax@surepeople.com");
      user.setTokenUrl("http://uat.surepeople.com/sp/tokenUrl");
      dbSetup.addUpdate(user);
      
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.ProfileIncompleteReminder.toString())
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com")
                  .param(Constants.PARAM_SUBJECT, "This is a test subject.")
                  .param(Constants.PARAM_LOG_TYPE, "Profile")
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testAssessmentCompleteNotification() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.createUsers();
      dbSetup.removeAll("notificationLogMessage");
      
      User user = dbSetup.getUser("dax@surepeople.com");

      AssessmentCompletedNotificationsProcessor notificationProcessor = 
          (AssessmentCompletedNotificationsProcessor) ApplicationContextUtils
                            .getBean("assessmentsCompletedNotificationsProcessor");

      notificationProcessor.process(user);
      
      User indUser = dbSetup.getUser("individual@surepeople.com");
      indUser.setEmail("dax@einstix.com");

      notificationProcessor.process(indUser);
 
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testFeedbackCompleteNotification() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.createUsers();
      dbSetup.createFeedbackUsers();
      dbSetup.removeAll("notificationLogMessage");
      
      User user = dbSetup.getUser("dax@surepeople.com");

      NotificationsProcessor notificationProcessor = 
          (NotificationsProcessor) ApplicationContextUtils
          .getBean("feedbackNotificationsProcessor");

      User user360 = dbSetup.getFeedbackUserById("1");
      
      notificationProcessor.process(NotificationType.FeedbackCompleted, user360, user);
 
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }

  @Test
  public void testFeedbackInviteNotification() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.createUsers();
      dbSetup.createFeedbackUsers();
      dbSetup.removeAll("notificationLogMessage");
      
      User user = dbSetup.getUser("dax@surepeople.com");

      NotificationsProcessor notificationProcessor = 
          (NotificationsProcessor) ApplicationContextUtils
          .getBean("feedbackNotificationsProcessor");

      User user360 = dbSetup.getFeedbackUserById("1");
      
      Map<String, Object> emailParamsMap = new HashMap<String, Object>();
      emailParamsMap.put(Constants.PARAM_TOKEN, "http://some.token.url");
      
      notificationProcessor.process(NotificationType.FeedbackInviteMember, user360, user,
          emailParamsMap);

      emailParamsMap.put(Constants.PARAM_COMMENT, "Some comment added.");
      
      notificationProcessor.process(NotificationType.FeedbackInviteMember, user360, user,
          emailParamsMap);

      emailParamsMap.put(Constants.PARAM_END_DATE,
          MessagesHelper.formatDate(LocalDate.now().plusDays(5)));
      notificationProcessor.process(NotificationType.FeedbackInviteExternal, user360, user,
          emailParamsMap);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }
  
  @Test
  public void testFeedbackReminderNotification() {
    try {
      // remove any previously created users
      dbSetup.removeAllUsers();
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAllFeedbackRequest();
      dbSetup.createUsers();
      dbSetup.createFeedbackUsers();
      dbSetup.createFeedbackRequest();
      dbSetup.removeAll("notificationLogMessage");
      
      User user360 = dbSetup.getFeedbackUserById("1");
      
      MvcResult result = this.mockMvc
          .perform(
              post("/notifications/sendNotificationWithParams")
                  .param("type", NotificationType.FeedbackReminder.toString())
                  .param(Constants.PARAM_MEMBER_LIST, "dax@surepeople.com")
                  .param(Constants.PARAM_SUBJECT, "Reminder: Please provide your feedback")
                  .param(Constants.PARAM_COMMENT, "Some comment.")
                  .param(Constants.PARAM_FEEDBACK_USERID, user360.getId())
                  .contentType(MediaType.TEXT_PLAIN)
                  .session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error !!!");
    }
  }  
}
