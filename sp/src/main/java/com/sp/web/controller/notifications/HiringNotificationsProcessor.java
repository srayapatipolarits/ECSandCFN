package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.hiring.user.HiringUserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The notifications processor for hiring tool.
 */
@Component("hiringReminderNotificationsProcessor")
public class HiringNotificationsProcessor extends NotificationsProcessor {
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Override
  protected List<User> getUserList(EmailParams emailParams, User user) {
    final ArrayList<User> memberList = new ArrayList<User>();
    final List<String> tos = emailParams.getTos();
    String hiringUserId = tos.get(0);
    HiringUser hiringUser = hiringUserFactory.getUser(hiringUserId);
    Assert.notNull(hiringUser, "User not found.");
    
    // replacing the user id with the actual email 
    tos.clear();
    tos.add(hiringUser.getEmail());
    memberList.add(hiringUser);
    
    emailParams.addParam(Constants.PARAM_FOR_USER, hiringUser);
    emailParams.addParam(Constants.PARAM_MEMBER, user);
    
    // setting the reminder date
    hiringUser.setRemindedOn(LocalDateTime.now());
    hiringUserFactory.updateUser(hiringUser);
    
    return memberList;
  }
}
