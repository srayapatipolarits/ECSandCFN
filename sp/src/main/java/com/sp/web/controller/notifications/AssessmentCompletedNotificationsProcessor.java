package com.sp.web.controller.notifications;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.log.LogRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The notifications processor for assessment completed.
 */
@Component("assessmentsCompletedNotificationsProcessor")
public class AssessmentCompletedNotificationsProcessor extends NotificationsProcessor {
  
  @Autowired
  AccountRepository accountRepository;

  /**
   * Processes the assessment completed notifications.
   * 
   * @param user
   *        - user
   */
  public void process(User user) {
    if (user.getCompanyId() != null) {
      Map<String, Object> params = new HashMap<String, Object>();
      Company company = accountRepository.findCompanyByIdValidated(user.getCompanyId());
      params.put(Constants.PARAM_COMPANY, company);
      // send notification to the member
      process(NotificationType.AssessmentCompletedMember, user, user, params);
    } else {
      // send notification to the individual user
      process(NotificationType.AssessmentCompleted, user, user);
    }
  }
  
  @Override
  protected LogRequest getLogRequest(EmailParams emailParams, User user, User userFor, NotificationType type) {
    return null;
  }
  

}
