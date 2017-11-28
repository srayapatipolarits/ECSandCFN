package com.sp.web.controller.log;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 * 
 *         The controller for the getting the logs for the members.
 */
@Controller
public class LogsController {
  
  @Autowired
  LogsControllerHelper helper;

  /**
   * The controller method to get the notification logs to display for the dashboard.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the get dashboard request
   */
  @RequestMapping(value = "/logs/getDashboardNotificationLogs", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getDashboardNotificationLogs(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getDashboardNotificationLogs, token);
  }

  /**
   * Controller method to reset the notifications count. This is only required in case 
   * the bell is open and an SSE for count increment is received.
   * 
   * @param token
   *          - logged in user
   * @return
   *    the response to the reset request
   */
  @RequestMapping(value = "/logs/resetNotificationLogsCount", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse resetNotificationLogsCount(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::resetNotificationLogsCount, token);
  }
  
  /**
   * Controller method to get all the notification messages. This call also 
   * resets the notification log count as well.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the get notification messages response
   */
  @RequestMapping(value = "/logs/getAllNotificationLogs", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getAllNotificationLogs(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getAllNotificationLogs, token);
  }

  /**
   * Controller method to mark a particular notification as read.
   * 
   * @param notificationId
   *          - notification id
   * @param token
   *          - logged in user
   * @return
   *    the response to the mark as read request
   */
  @RequestMapping(value = "/logs/markAsRead", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse markAsRead(@RequestParam String notificationId, Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::markAsRead, token, notificationId);
  }
  
  /**
   * Controller method to get the dashboard activity logs.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the response to the log messages
   */
  @RequestMapping(value = "/logs/getDashboardActivityLogs", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getDashboardActivityLogs(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getDashboardActivityLogs, token);
  }
  
  /**
   * Controller method to get all the notification messages.
   * 
   * @param token
   *          - logged in user
   * @return
   *      the get notification messages response
   */
  @RequestMapping(value = "/logs/getAllActivityLogs", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getAllActivityLogs(Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getAllActivityLogs, token);
  }

  /**
   * Controller method to get the user activity logs.
   * 
   * @param userId
   *          - user id
   * @param token
   *          - logged in user
   * @return
   *    the user activity logs response
   */
  @RequestMapping(value = "/logs/getUserActivityLogs", method = RequestMethod.POST)
  @ResponseBody 
  public SPResponse getAllActivityLogs(@RequestParam String userId, Authentication token) {

    // process the get hiring subscriptions request
    return process(helper::getUserActivityLogs, token, userId);
  }
  
}
