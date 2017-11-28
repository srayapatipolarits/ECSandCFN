package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Dax Abraham
 *
 *         The notifications controller helper class to perform all the actions of the notifications
 *         controller.
 */
@Component
public class NotificationsControllerHelper {

  @Autowired
  UserRepository userRepository;

  @Autowired
  CommunicationGateway gateway;

  /**
   * Sends a notification to an individual member.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - parameters for the send request
   * @return - the response to the send request
   */
  public SPResponse sendNotification(User user, Object[] params) {
    SPResponse resp = new SPResponse();

    // get the notification type
    NotificationType type = (NotificationType) params[0];

    // get the member to send the notification to
    String memberEmail = (String) params[1];

    // get the email body
    String emailBody = (String) params[2];

    // get the subject for the email
    String subject = (String) params[3];

    sendNotification(user, type, memberEmail, emailBody, subject);

    // set success
    resp.isSuccess();

    return resp;
  }

  /**
   * Sends a notification.
   * 
   * @param user
   *          - logged in user
   * @param type
   *          - notification type
   * @param memberEmail
   *          - member email to send notification to
   * @param emailBody
   *          - the email body
   * @param subject
   *          - the email subject
   */
  private void sendNotification(User user, NotificationType type, String memberEmail,
      String emailBody, String subject) {
    // get the user for given email
    User member = userRepository.findByEmailValidated(memberEmail);

    // validate if the users are from the same company
    // Putting a check for user null, as in case of exteral user trying to send notification, for an
    // event, is not required to work in the same company.
    if (user != null) {
      member.isSameCompany(user);
    }

    // send the notification
    // create the email parameters
    EmailParams emailParams = new EmailParams(type.getTemplateName(), member.getEmail(),
        getSubject(type, member),member.getProfileSettings().getLocale().toString());

    // add the parameters
    emailParams.addParam(Constants.PARAM_MEMBER, member);

    // if subject is present the over ride the subject
    Optional.ofNullable(subject).ifPresent(emailParams::setSubject);

    // set the email body
    emailParams.setEmailBody(emailBody);

    // send the message
    gateway.sendMessage(emailParams);
  }

  /**
   * The send group notification request.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - list of parameters for request
   * @return - the response to the send group request
   */
  public SPResponse sendGroupNotification(User user, Object[] params) {
    SPResponse resp = new SPResponse();

    // get the notification type
    NotificationType type = (NotificationType) params[0];

    // get the member to send the notification to
    @SuppressWarnings("unchecked")
    List<String> memberEmailList = (List<String>) params[1];

    // get the flag if it is to send together or individually
    boolean isGroupMail = (boolean) params[2];

    // get the email body
    String emailBody = (String) params[3];

    // get the subject for the email
    String subject = (String) params[4];

    // if not group mail then send email individually to
    // each member in the list
    if (!isGroupMail) {
      memberEmailList.stream().forEach(
          memberEmail -> sendNotification(user, type, memberEmail, emailBody, subject));
      resp.isSuccess();
    } else {
      // send the email to the whole group
      // validate if the member email are valid
      ArrayList<String> errorList = validateUsers(user, memberEmailList);

      // check if errors exist
      if (errorList.size() > 0) {
        // adding errors to the error list
        resp.addError(Constants.PARAM_MEMBER_LIST, errorList);
      } else {

        // send the group notification
        EmailParams emailParams = new EmailParams(type.getTemplateName(), memberEmailList,
            MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + type), user
                .getProfileSettings().getLocale().toString());
        emailParams.setEmailBody(emailBody);
        Optional.ofNullable(subject).ifPresent(s -> emailParams.setSubject(s));
        gateway.sendMessage(emailParams);

        // set success
        resp.isSuccess();
      }
    }
    return resp;
  }

  /**
   * Validate the list of users if they belong to the same company.
   * 
   * @param user
   *          - logged in user
   * @param memberEmailList
   *          - member email list
   * @return The list of member email's that are not part of the company of user
   */
  private ArrayList<String> validateUsers(User user, List<String> memberEmailList) {
    ArrayList<String> errorList = new ArrayList<String>();
    memberEmailList.stream().forEach(memberEmail -> {
        try {
          User member = userRepository.findByEmailValidated(memberEmail);
          member.isSameCompany(user);
        } catch (InvalidRequestException e) {
          errorList.add(memberEmail);
        }
      });
    return errorList;
  }

  /**
   * The helper method to handle the parameters and send the notification.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - parameters for the request
   * @return the response to the send notification request
   */
  public SPResponse sendNotificationWithParams(User user, Object[] params) {
    final SPResponse resp = new SPResponse();

    // get the notification type
    NotificationType type = (NotificationType) params[0];

    // the name of the parameters helper
    String notificationsProcessorName = (String) params[1];

    // the http request
    HttpServletRequest request = (HttpServletRequest) params[2];

    // create the email params to send the notification for
    EmailParams emailParams = new EmailParams();
    emailParams.setTemplateName(type.getTemplateName());
    
    // get the parameters member list
    String memberListStr = request.getParameter(Constants.PARAM_MEMBER_LIST);

    // set the recipient list
    Optional.ofNullable(memberListStr).ifPresent(
        memberList -> emailParams.setTos(Stream.of(memberList.split(",")).map(String::trim)
            .collect(Collectors.toList())));

    // get and add the subject from the request
    // setting the subject
    Optional.ofNullable(request.getParameter(Constants.PARAM_SUBJECT)).ifPresent(emailParams::setSubject);

    // add the parameters from the request
    emailParams.addAllParams(request.getParameterMap());

    // check if the email body is present in the request
    Optional.ofNullable(request.getParameter(Constants.PARAM_EMAIL_BODY)).ifPresent(emailParams::setEmailBody);    

    // get the parameters helper
    NotificationsProcessor notificationsProcessor;

    try {
      notificationsProcessor = (NotificationsProcessor) ApplicationContextUtils.getBean(Optional.ofNullable(
          notificationsProcessorName).orElse(type.getNotificationProcessor()));
    } catch (Exception e) {
      throw new InvalidRequestException("No notifications processor found :" + notificationsProcessorName, e);
    }

    // call params helper
    notificationsProcessor.process(emailParams, user, type, true);

    // set success
    resp.isSuccess();
    
    return resp;
  }

  /**
   * Gets the subject for the notification from the messages.properties.
   * 
   * @param type
   *          - notification type
   * @param member
   *          - member to send the notification to
   * @return the email subject
   */
  private String getSubject(NotificationType type, User member) {
    return MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + type, member.getFirstName(),
        member.getLastName());
  }
}
