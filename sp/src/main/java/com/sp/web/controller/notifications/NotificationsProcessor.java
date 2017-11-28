package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.email.EmailManagement;
import com.sp.web.model.log.LogActionType;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailManagementFactory;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The interface to be implemented for processing the parameters for notifications.
 * 
 *         This provides the ability for the additional processing of the parameters and other
 *         things for sending of the notification.
 * 
 */
public class NotificationsProcessor {
  
  private static final Logger LOG = Logger.getLogger(NotificationsProcessor.class);
  @Autowired
  CommunicationGateway gateway;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  ThemeCacheableFactory themeCacheableFactory;
  
  @Autowired
  EmailManagementFactory emailManagementFactory;
  
  /**
   * Overloaded method to process.
   * 
   * @param type
   *          - notification type
   * @param user
   *          - user
   * @param forUser
   *          - for user
   */
  public void process(NotificationType type, User user, User forUser) {
    process(type, user, forUser, null);
  }
  
  /**
   * Processes the send notification request.
   * 
   * @param type
   *          - notification type
   * @param user
   *          - user
   * @param forUser
   *          - for user
   */
  public void process(NotificationType type, User user, User forUser, Map<String, Object> params) {
    process(type, user, forUser, params, true);
  }
  
  /**
   * Processes the send notification request.
   * 
   * @param type
   *          - notification type
   * @param user
   *          - user
   * @param forUser
   *          - for user
   */
  public void process(NotificationType type, User user, User forUser, Map<String, Object> params,
      boolean doValidated) {
    
    // create the email params to send the notification for
    EmailParams emailParams = new EmailParams();
    emailParams.setTemplateName(type.getTemplateName());
    List<String> tos = new ArrayList<String>();
    tos.add(forUser.getEmail());
    emailParams.setTos(tos);
    if (params != null) {
      emailParams.getValueMap().putAll(params);
    }
    emailParams.addParam(Constants.PARAM_FOR_USER, forUser);
    emailParams.addParam(Constants.PARAM_MEMBER, user);
    process(emailParams, user, type, doValidated);
  }
  
  /**
   * This is the process method to process parameters from the request.
   * 
   * @param emailParams
   *          - the email params to update
   * @param user
   *          - logged in user
   */
  public void process(EmailParams emailParams, User user, NotificationType type, boolean doValidate) {
    
    // check if the recipients are present
    final List<String> tosList = emailParams.getTos();
    if (tosList.size() == 0) {
      throw new InvalidRequestException("No recipients found to send the notification to !!!");
    }
    
    // validate the list of to's with user
    final List<User> userList = getUserList(emailParams, user);
    List<User> errorList = (doValidate) ? validateUsers(user, userList, type)
        : new ArrayList<User>();
    
    // update the email params if required
    updateEmailParams(userList, emailParams, user, errorList);
    
    emailParams.validate();
    
    /* get the company to get the company theme for the emails */
    final CompanyDao company = themeCacheableFactory.getCompanyByIdForTheme(user.getCompanyId());
    
    /** Assigning default constant for Individual users. */
    String companyId = Constants.DEFAULT_COMPANY_ID;
    if (user.getCompanyId() != null) {
      companyId = company.getId();
    }
    
    boolean isEmailManagement = false;
    EmailManagement emailManagement = emailManagementFactory.get(companyId);
    if (emailManagement == null
        || (emailManagement.getContent() != null && emailManagement.getContent().get(type) == null)) {
      emailManagement = emailManagementFactory.get(Constants.DEFAULT_COMPANY_ID);
    }
    /* check if noticiation type exist for the company */
    if (emailManagement != null && emailManagement.getContent() != null
        && emailManagement.getContent().get(type) != null) {
      isEmailManagement = true;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Email management is " + emailManagement);
    }
    // send the group notification
    for (User u : userList) {
      /* setting the subject in the same locale as the user */
      if (emailParams.getSubject() == null) {
        emailParams.setSubject(getSubject(type, u, user, company.getName()));
      }
      EmailParams cloneParam = new EmailParams();
      if (isEmailManagement) {
        Map<String, String> content = emailManagement.getContent().get(type);
        cloneParam.addParam(Constants.PARAM_EMAIL_BODY, content.get(Constants.PARAM_EMAIL_BODY));
        cloneParam.setSubject(content.get(Constants.PARAM_SUBJECT));
        
        if (StringUtils.isNotBlank(content.get(Constants.PARAM_TEMPLATE_NAME))) {
          cloneParam.setTemplateName(content.get(Constants.PARAM_TEMPLATE_NAME));
        } else {
          cloneParam.setTemplateName(Constants.DEFAULT_EMAIL_TEMPLATE);
        }
        cloneParam.addParam(Constants.PARAM_EMAIL_ORANGE_BAND,
            content.get(Constants.PARAM_EMAIL_ORANGE_BAND));
        
        cloneParam.addParam(Constants.PARAM_LINK_TEXT, content.get(Constants.PARAM_LINK_TEXT));
        cloneParam.addParam(Constants.PARAM_LINK_URL, content.get(Constants.PARAM_LINK_URL));
        cloneParam.addParam(Constants.PARAM_ISEMAIL_MANANGEMENT, true);
      } else {
        cloneParam.setEmailBody(emailParams.getEmailBody());
        
        cloneParam.setSubject(emailParams.getSubject());
        cloneParam.setTemplateName(emailParams.getTemplateName());
      }
      if (StringUtils.isNotEmpty(emailParams.getLocale())) {
        cloneParam.setLocale(u.getProfileSettings().getLocale().toString());
      } else {
        cloneParam.setLocale(emailParams.getLocale());
      }
      cloneParam.setFrom(emailParams.getFrom());
      cloneParam.setViaFrom(emailParams.isViaFrom());
      cloneParam.getTos().add(u.getEmail());
      cloneParam.getDataSourceAttachments().clear();
      if (emailParams.getDataSourceAttachments().size() > 0) {
        cloneParam.getDataSourceAttachments().addAll(emailParams.getDataSourceAttachments());
      }
      cloneParam.getValueMap().putAll(emailParams.getValueMap());
      cloneParam.addParam(Constants.PARAM_FOR_USER, u);
      if (cloneParam.getValue("hisHer") == null) {
        cloneParam.getValueMap().putAll(MessagesHelper.getGenderText(user));
      }
      
      cloneParam.addParam(Constants.PARAM_COMPANY, company);
      
      cloneParam.addParam(Constants.PARAM_NOTIFICATION_TYPE, type);
      gateway.sendMessage(cloneParam);
    }
    
    // log the notification
    if (type.getLogType().doLogNotification()) {
      for (User usr : userList) {
        LogRequest logRequest = getLogRequest(emailParams, user, usr, type);
        if (logRequest != null) {
          logGateway.logNotification(logRequest);
        }
      }
    }
    
    // adding the activity for myself
    if (type.getLogType().doLogActivity()) {
      for (User usr : userList) {
        LogRequest logRequest = getActivityLogRequest(emailParams, user, usr, type);
        if (logRequest != null) {
          logGateway.logActivity(logRequest);
        }
      }
    }
  }
  
  /**
   * Method to influence if the user activity should be logged.
   * 
   * @return true if log activity else false
   */
  protected boolean doLogActivity() {
    return true;
  }
  
  protected LogRequest getActivityLogRequest(EmailParams emailParams, User user, User userFor,
      NotificationType type) {
    final LogRequest logRequest = new LogRequest(type.getLogType(), user, userFor);
    String activityMessage = (String) emailParams.getValue(Constants.PARAM_MESSAGE_ACTIVITY);
    if (activityMessage != null) {
      logRequest.setDoMessagesOverride(true);
      logRequest.addParam(Constants.PARAM_MESSAGE_ACTIVITY,
          GenericUtils.replaceSpecialChar(activityMessage));
    }
    return logRequest;
  }
  
  protected LogRequest getLogRequest(EmailParams emailParams, User user, User userFor,
      NotificationType type) {
    
    // returning null for external users as no notification is required
    if (userFor.getType() == UserType.External) {
      return null;
    }
    
    final LogActionType logType = type.getLogType();
    
    if (!logType.doLogNotification()) {
      return null;
    }
    
    LogRequest logRequest = new LogRequest(logType, user, userFor);
    
    String paramMessage = (String) emailParams.getValue(Constants.PARAM_NOTIFICATION_MESSAGE);
    if (!StringUtils.isBlank(paramMessage)) {
      paramMessage = GenericUtils.replaceSpecialChar(paramMessage);
      logRequest.addParam(Constants.PARAM_NOTIFICATION_MESSAGE, paramMessage);
    }
    
    logRequest.addParam(Constants.PARAM_NOTIFICATION_URL_PARAM,
        emailParams.getValue(Constants.PARAM_NOTIFICATION_URL_PARAM));
    
    return logRequest;
  }
  
  /**
   * This method is to update any data that the processor would like to update before the email is
   * sent to the gateway.
   * 
   * @param userList
   *          - final user list after validation
   * @param emailParams
   *          - email's params to update
   * @param user
   *          - requesting user
   * @param errorList
   *          - error list
   */
  protected void updateEmailParams(List<User> userList, EmailParams emailParams, User user,
      List<User> errorList) {
    // filter the email to's list and user list depending on the error in validation
    final List<String> tosList = emailParams.getTos();
    
    errorList.forEach(u -> {
      userList.remove(u);
      tosList.remove(u.getEmail());
    });
    
    // get and add the subject from the request
    // setting the subject
    Optional.ofNullable((String) emailParams.getValue(Constants.PARAM_SUBJECT)).ifPresent(
        emailParams::setSubject);
  }
  
  /**
   * Get the list of users for the given parameters.
   * 
   * @param emailParams
   *          - the email params
   * @param user
   *          - the user
   * @return the list of users
   */
  protected List<User> getUserList(EmailParams emailParams, User user) {
    final ArrayList<User> memberList = new ArrayList<User>();
    User userFor = (User) emailParams.getValue(Constants.PARAM_FOR_USER);
    
    if (userFor == null) {
      final List<String> tos = emailParams.getTos();
      memberList.addAll(userRepository.findByEmail(tos));
      if (memberList.size() != tos.size()) {
        List<String> collectedList = memberList.stream().map(User::getEmail)
            .collect(Collectors.toList());
        throw new InvalidRequestException("Following members not found in company :"
            + tos.stream().filter(u -> !collectedList.contains(u)).collect(Collectors.joining(",")));
      }
    } else {
      memberList.add(userFor);
    }
    return memberList;
  }
  
  /**
   * Validate the list of users if they belong to the same company.
   * 
   * @param user
   *          - logged in user
   * @param memberEmailList
   *          - member email list
   * @param type
   *          adding type to handle validation for specific type of user.
   * @return The list of member email's that are not part of the company of user
   */
  protected List<User> validateUsers(User user, List<User> memberEmailList, NotificationType type) {
    ArrayList<User> errorList = new ArrayList<User>();
    memberEmailList.forEach(member -> {
      try {
        if (!member.equals(user.getEmail())) {
          member.isSameCompany(user);
        }
      } catch (InvalidRequestException e) {
        errorList.add(member);
      }
    });
    if (!errorList.isEmpty()) {
      throw new InvalidRequestException("User not validated :"
          + errorList.stream().map(User::getEmail).collect(Collectors.joining(",")));
    }
    return errorList;
  }
  
  /**
   * Gets the subject for the notification from the messages.properties.
   * 
   * @param type
   *          - notification type
   * @param member
   *          - member to send the notification to
   * @param fromUser
   *          - from user
   * @param companyName
   *          - company name
   * @return the email subject
   */
  private String getSubject(NotificationType type, User member, User fromUser, String companyName) {
    final String lastName = Optional.ofNullable(member.getLastName()).orElse("");
    final String fromUserLastName = Optional.ofNullable(fromUser.getLastName()).orElse("");
    return MessagesHelper.getMessage(Constants.NOTIFICATION_SUBJECT_PREFIX + type,
        member.getLocale(), member.getFirstNameOrEmail(), lastName, fromUser.getFirstName(),
        fromUserLastName, companyName);
  }
}
