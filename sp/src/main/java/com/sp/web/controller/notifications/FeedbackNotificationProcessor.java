package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.log.LogRequest;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The feedback notification processor.
 */
@Component("feedbackNotificationsProcessor")
public class FeedbackNotificationProcessor extends NotificationsProcessor {

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Override
  protected List<User> getUserList(EmailParams emailParams, User user) {
    final ArrayList<User> memberList = new ArrayList<User>();
    
    User userFor = (User)emailParams.getValue(Constants.PARAM_FOR_USER);
    if (userFor == null) {
      String fbUserId = (String) emailParams.getValue(Constants.PARAM_FEEDBACK_USERID);
      FeedbackUser fbUser = feedbackRepository.findByIdValidated(fbUserId);
      fbUser.setId(fbUser.getMemberId());
      userFor = fbUser;
      FeedbackRequest feedbackRequest = feedbackRepository.findFeedbackRequestByFeedbackUserId(fbUserId);
      emailParams.addParam(Constants.PARAM_TOKEN, feedbackRequest.getTokenUrl());
      emailParams.addParam(Constants.PARAM_FOR_USER, fbUser);
      emailParams.addParam(Constants.PARAM_MEMBER, user);
      emailParams.getValueMap().put(Constants.PARAM_END_DATE,
          MessagesHelper.formatDate(feedbackRequest.getEndDate()));
      
    }
    emailParams.addParam(Constants.PARAM_FOR_USER, userFor);
    emailParams.addParam(Constants.PARAM_MEMBER, user);
    memberList.add(userFor);
    return memberList;
  }

  protected LogRequest getLogRequest(EmailParams emailParams, User user, User userFor, NotificationType type) {
    // if external user then no need for notification
    if (userFor instanceof FeedbackUser) {
      FeedbackUser fbUser = (FeedbackUser) userFor;
      if (fbUser.getType() == UserType.External) {
        return null;
      }
      
      // set the id as the member id for 
      // notification messages
      fbUser.setId(fbUser.getMemberId());
    }
    LogRequest logRequest = super.getLogRequest(emailParams, user, userFor, type);
    logRequest.addParam(Constants.PARAM_NOTIFICATION_URL_PARAM, user.getId());
    return logRequest;
  }

  protected List<User> validateUsers(User user, List<User> memberEmailList, NotificationType type) {
    return new ArrayList<User>(0);
  }
  
//  @Override
//  protected LogRequest getLogRequest(EmailParams emailParams, User user, User userFor, NotificationType type) {
//    LogRequest logRequest = null;
//    if (!(userFor instanceof FeedbackUser)) {
//      if (((FeedbackUser) userFor).getCompanyId() != null) {
//        logRequest = new LogRequest(type.getLogType(), userFor);
//        logRequest.setLogType(LogType.valueOf((String)emailParams.getValue(Constants.PARAM_LOG_TYPE)));
//        
//        if (logRequest.getLogActionType() == LogActionType.Default) {
//          logRequest.setDoMessagesOverride(true);
//          logRequest.addParam(Constants.PARAM_TITLE, emailParams.getSubject());
//          logRequest.addParam(Constants.PARAM_MESSAGE, emailParams.getValue(Constants.PARAM_MESSAGE));
//        }
//      }
//    }
//    return logRequest;
//  }
}
