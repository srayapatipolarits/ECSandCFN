package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.hiring.lens.HiringLensFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The notifications processor for hiring lens reminder requests.
 */
@Component("hiringLensReminderNotificationProcessor")
public class HiringLensReminderNotificationsProcessor extends NotificationsProcessor {

  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private HiringLensFactory hiringLensFactory;
  
  @Override
  protected List<User> getUserList(EmailParams emailParams, User user) {
    final ArrayList<User> memberList = new ArrayList<User>();
    User userFor = (User)emailParams.getValue(Constants.PARAM_FOR_USER);
    if (userFor == null) {
      final List<String> tos = emailParams.getTos();
      if (!tos.isEmpty()) {
        String userId = tos.get(0);
        FeedbackUser feedbackUser = hiringLensFactory.getFeedbackUser(userId);
        Assert.notNull(feedbackUser, "User not found.");
        memberList.add(feedbackUser);
        emailParams.addParam(Constants.PARAM_FOR_USER, feedbackUser);
        HiringUser hiringUser = hiringUserFactory.getUser(feedbackUser.getFeedbackFor());
        emailParams.addParam(Constants.PARAM_MEMBER, hiringUser);
        if (hiringUser.getType() == UserType.HiringCandidate) {
          emailParams.addParam(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.HiringLensReminderCandidate", hiringUser.getLocale(),
              hiringUser.getFirstName(), hiringUser.getLastName()));
        } else {
          emailParams.addParam(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
              "notification.subject.HiringLensReminder", hiringUser.getLocale(),
              hiringUser.getFirstName(), hiringUser.getLastName()));
        }
        hiringLensFactory.updateRemindedOn(feedbackUser);
      }
    } else {
      memberList.add(userFor);
      emailParams.addParam(Constants.PARAM_MEMBER, user);
    }
    return memberList;
  }
}

