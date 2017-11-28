package com.sp.web.controller.profile;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.HiringCandidateNotificationsProcessor;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.token.TimeBasedTokenRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 *
 *         The helper for the copy assessment controller.
 */
@Component
public class CopyAssessmentControllerHelper {

  @Autowired
  UserFactory userFactory;
  
  @Autowired
  SPTokenFactory tokenFactory;
  
  @Autowired
  CommunicationGateway gateway;
  
  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  @Qualifier("hiringCandidateNotificationProcessor")
  private HiringCandidateNotificationsProcessor notificationProcessor;
  
  
  /**
   * Helper method to check if the user email provided has a valid assessment associated to it, and then
   * create a token for the same.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return
   *      the response to the copy assessment
   */
  public SPResponse copyAssessment(User user, Object[] params) {
    
    final SPResponse resp = new SPResponse();
    
    String copyFromEmail = (String) params[0];

    User existingUser = userFactory.getCopyProfileFromUser(user, copyFromEmail);
    
    if (existingUser == null) {
      throw new InvalidRequestException(MessagesHelper.getMessage("exception.profileCopy.userNotFound"));
    }
    
    createTokenAndSendEmail(existingUser, user);
    resp.isSuccess();
    return resp;
  }
  
  /**
   * Helper method to process the authorization for the copy profile request. 
   * 
   * @param params
   *          - params
   * @return
   *      the response to the authorize request.
   */
  public SPResponse authorizeCopyProfile(Object[] params) {
    final SPResponse resp = new SPResponse();
    
    Token token = (Token) params[0];
    Assert.notNull(token, "Token not found in session.");
    
    // get the user from the session
    User user = (User) token.getParam(Constants.PARAM_USER);
    Assert.notNull(user, "User not found to process.");
    
    String copyFromEmail = token.getParamAsString(Constants.PARAM_EMAIL);
    Assert.notNull(user, "Email not found to process.");
    
    userFactory.doCopyProfileFromUser(user,
        UserType.valueOf((String) token.getParam(Constants.PARAM_USER_TYPE)), copyFromEmail);
    
    if (user.getUserStatus() == UserStatus.VALID && user instanceof HiringUser
        && user.getType() == UserType.Member) {
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      if (company.isSharePortrait()) {
        notificationProcessor.process(NotificationType.AssessmentCompletedEmployee, user);
      }
    }
      
    resp.isSuccess();
    return resp;
  }

  /**
   * Method to create a new copy profile token and send email.
   * 
   * @param existingUser
   *            - user to copy profile from
   * @param user
   *            - user
   */
  private void createTokenAndSendEmail(User existingUser, User user) {
    
    // create a token
    TimeBasedTokenRequest tokenRequest = new TimeBasedTokenRequest();
    tokenRequest.addParam(Constants.PARAM_USER_ID, user.getId());
    tokenRequest.addParam(Constants.PARAM_USER_TYPE, getUserType(user));
    tokenRequest.addParam(Constants.PARAM_EXISTING_USER_ID, existingUser.getId());
    tokenRequest.addParam(Constants.PARAM_EMAIL, existingUser.getEmail());
    tokenRequest.addParam(Constants.PARAM_EXISTING_USER_TYPE, getUserType(existingUser));
    final Token token = tokenFactory.getToken(tokenRequest, TokenProcessorType.COPY_PROFILE);
    
    // send the email for authorization
    EmailParams emailParams = new EmailParams(Constants.COPY_PROFILE_TEMPLATE,
        existingUser.getEmail(), MessagesHelper.getMessage("email.subject.copy.profile"),
        existingUser.getProfileSettings().getLocale().toString());
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    if (company == null || company.getCompanyTheme() == null) {
      company = companyFactory.getCompany(Constants.COMP_THEME_DEFAULT_COMPANY_ID);
    }
    emailParams.addParam(Constants.PARAM_COMPANY, company);
    emailParams.addParam(Constants.PARAM_MEMBER, existingUser);
    emailParams.addParam(Constants.PARAM_FOR_USER, user);
    emailParams.addParam(Constants.PARAM_TOKEN, token);
    emailParams.addParam(Constants.PARAM_NOTIFICATION_TYPE, NotificationType.COPY_PROFILE_TEMPLATE);
    
    gateway.sendMessage(emailParams);
  }

  /**
   * Get the user type.
   * 
   * @param user
   *          - the user to check
   * @return
   *      the user type
   */
  private UserType getUserType(User user) {
    if (user instanceof HiringUser) {
      return UserType.HiringCandidate;
    } else {
      return UserType.Member;
    }
  }
}
