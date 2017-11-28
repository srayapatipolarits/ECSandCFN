package com.sp.web.controller.notifications;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Dax Abraham
 * 
 *         The generic controller that is responsible for sending all the notifications in the
 *         system.
 */
@Controller
public class NotificationsController {

  @Autowired
  NotificationsControllerHelper helper;
  
  /**
   * Sends the notification for the given notification type to the user in the user list.
   * 
   * @param type
   *          - notification type
   * @param memberEmail
   *          - email of members
   * @param token
   *          - logged in user
   * @return
   *        - the response to the send notification request
   */
  @RequestMapping(value = "/notifications/sendNotification", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse sendNotification(@RequestParam NotificationType type,
      @RequestParam String memberEmail,
      @RequestParam(required = false) String subject,
      @RequestParam(required = false) String emailBody,
      Authentication token) {

    // process the send notification method
    return process(helper::sendNotification, token, type, memberEmail, emailBody, subject);
  }


  /**
   * Sends the notification to a group of members.
   * 
   * @param type
   *        - notification type
   * @param memberEmailList
   *        - list of member email
   * @param isGroupMail
   *        - send together or individually
   * @param token
   *        - logged in user
   * @return
   *        the response to the send group request
   */
  @RequestMapping(value = "/notifications/sendGroupNotification", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse sendGroupNotification(@RequestParam NotificationType type,
      @RequestParam List<String> memberEmailList,
      @RequestParam(required = false, defaultValue = "true") boolean isGroupMail,
      @RequestParam(required = false) String emailBody, 
      @RequestParam(required = false) String subject,
      Authentication token) {

    // process the send notification method
    return process(helper::sendGroupNotification, token, type, memberEmailList, isGroupMail, emailBody, subject);
  }

  /**
   * The controller method to send the notification but additionally it provides the 
   * opportunity to process the parameters as well. 
   * 
   * @param type
   *          - the notification type
   * @param paramsHelper
   *          - the parameters helper
   * @param request
   *          - the http request
   * @param token 
   *          - logged in user
   * @return
   *          the response to the send notification request
   */
  @RequestMapping(value = "/notifications/sendNotificationWithParams", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse sendNotificationWithParams(@RequestParam NotificationType type,
      @RequestParam(required = false) String paramsHelper,
      HttpServletRequest request,
      Authentication token) {

    // process the send notification method
    return process(helper::sendNotificationWithParams, token, type, paramsHelper, request);
  }
}
