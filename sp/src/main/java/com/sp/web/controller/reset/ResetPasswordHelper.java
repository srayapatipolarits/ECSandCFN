package com.sp.web.controller.reset;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.exception.InvalidParameterException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.exception.SPException;
import com.sp.web.exception.UserNotFoundException;
import com.sp.web.model.MasterPassword;
import com.sp.web.model.Password;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.password.PasswordManagerService;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.theme.ThemeCacheableFactory;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

/**
 * ResetPasswordHelper class provides helper methods for rest password flow.
 * 
 * @author pruhil
 * 
 */
@Component
@Qualifier("restPasswordHelper")
public class ResetPasswordHelper {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(ResetPasswordHelper.class);
  
  /** Token factory to get the token. */
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  /** Token Repository to perform operation on the repository. */
  @Autowired
  private TokenRepository tokenRepository;
  
  /** Account repository to fetch the user account. */
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private HttpSession httpSession;
  
  @Inject
  private Environment environment;
  
  @Autowired
  private ThemeCacheableFactory themeCacheableFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private PasswordManagerService passwordManagerService;
  
  /** reset email subject. */
  private static final String RESET_TOKEN_EMAIL_SUBJECT = "notification.subject.ResetPassword";
  
  /** reset password template. */
  private static final String RESET_PASSWORD_STG = "templates/email/resetPassword.stg";
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  /**
   * User not found during the reset.
   */
  private static final String USER_NOT_FOUND_RESET = "userNotFoundReset";
  
  /**
   * <code>changePassword</code> method will reset the user password.
   * 
   * @param passwordForms
   *          password form to be changed
   * @return the true/sucess
   */
  public SPResponse setNewPassword(Object[] passwordForms) {
    
    /* check if the validation steps is already done for the token */
    String password = (String) passwordForms[0];
    
    HttpSession httpSession = (HttpSession) passwordForms[1];
    String email = (String) httpSession.getAttribute("email");
    String isTokenValid = (String) httpSession.getAttribute(email + Constants.PARAM_TOKEN);
    
    Token token = tokenRepository.findTokenById(isTokenValid);
    /* check whether the code is still valid to reset the password */
    Optional.ofNullable(token).orElseThrow(
        () -> new InvalidRequestException("Token is Already used or is not present"));
    
    /* As token is processed so it will be invalid. */
    if (token.isValid()
        && ((String) token.getParam(Constants.PARAM_USER_EMAIL)).equalsIgnoreCase(email)) {
      LOG.debug("All well, reseting the password ");
      User user = userRepository.findByEmail(email);
      
      passwordManagerService.setNewPassword(password, user);
      userRepository.updateGenericUser(user);
      token.invalidate("Token is processed for one time use");
      tokenRepository.persistToken(token);
      /**
       * Commenting the automated login. loginHelper.authenticateUserAndSetSession(email,
       * resetPasswordForm.getPassword());
       */
      
      sendNotification(user);
      SPResponse spResponse = new SPResponse();
      spResponse.isSuccess();
      return spResponse;
    } else {
      throw new InvalidTokenException("Token is invalid", token);
    }
    
  }
  
  /**
   * <code>sendReseteamil</code> method will send the reset email.
   * 
   * @param resetPaswordForms
   *          to which reset email to be sent
   * @return the succes or failture error
   */
  public SPResponse sendResetEmail(Object[] resetPaswordForms) {
    final SPResponse spResponse = new SPResponse();
    
    LOG.info("Enter sendResteEmail(), email : "
        + (resetPaswordForms != null ? resetPaswordForms.length : null));
    int expiresTime = 0;
    String email = (String) ((resetPaswordForms != null) ? resetPaswordForms[0] : null);
    
    /* check for null/empty array */
    if (StringUtils.isBlank(email)) {
      throw new SPException("Cannot send reset email, Resetpassword form is null");
    }
    
    String expiresTimeStr = environment.getProperty("reset.token.expires");
    if (StringUtils.isNotEmpty(expiresTimeStr)) {
      expiresTime = Integer.valueOf(expiresTimeStr);
    }
    /* check if user is present or not in the system for the requested email */
    
    User user = userRepository.findByEmail(email.toLowerCase());
    
    if (user == null) {
      throw new UserNotFoundException(MessagesHelper.getMessage(USER_NOT_FOUND_RESET, user));
    }
    
    /*
     * user exist in the system, create a token and send the reset email to the uesr
     */
    Map<String, Object> params = new HashMap<>();
    params.put(Constants.PARAM_USER_EMAIL, email.toLowerCase());
    params.put(Constants.PARAM_EXPIRES_TIME, expiresTime);
    params.put(Constants.PARAM_TIME_UNIT, TimeUnit.HOURS);
    params.put(Constants.PARAM_TIME_UNIT, TimeUnit.HOURS);
    Token token = spTokenFactory.getToken(TokenType.TIME_BASED, params,
        TokenProcessorType.RESET_PASSWORD);
    
    LOG.debug("Token is created to process ");
    sendResetEmail(email, token, expiresTime, user);
    spResponse.add("email", email);
    spResponse.isSuccess();
    /* emial is sent scucesfully */
    return spResponse;
  }
  
  /**
   * <code>sendResetEmail</code> method will send the reset email to the user
   * 
   * @param email
   *          to which reset password to be sent
   * @param token
   *          to fetch the url from the password
   * @param expiresTime
   *          in which the expires time.
   */
  private void sendResetEmail(String email, Token token, int expiresTime, User user) {
    String tokenUrl = token.getTokenUrl();
    /* Prepare the eemail message params */
    EmailParams emailParams = new EmailParams();
    emailParams.setTemplateName(RESET_PASSWORD_STG);
    emailParams.getValueMap().put(Constants.PARAM_TOKEN, tokenUrl);
    emailParams.getValueMap().put(Constants.PARAM_EXPIRES_TIME, expiresTime);
    emailParams.addParam(Constants.PARAM_FOR_USER, user);
    
    List<String> tos = new ArrayList<>();
    tos.add(email);
    emailParams.setTos(tos);
    emailParams.setLocale(user.getProfileSettings().getLocale().toString());
    emailParams.setSubject(MessagesHelper.getMessage(RESET_TOKEN_EMAIL_SUBJECT, user.getLocale()));
    
    notificationsProcessor.process(emailParams, user, NotificationType.ResetPassword, false);
  }
  
  /**
   * <code>changePassword</code> method will change the password of the user.
   * 
   * @param user
   *          current logged in user.
   * @param param
   *          contains the old password and new password.
   * @return the new password.
   */
  public SPResponse changePassword(User user, Object[] param) {
    
    String oldPassword = (String) param[0];
    String newPassword = (String) param[1];
    
    if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
      throw new InvalidParameterException("Password cannot be blank or empty");
    }
    
    if (!passwordEncoder.matches(oldPassword, user.getPasswords().getPassword())) {
      LOG.info("Old password didn't match");
      throw new SPException(MessagesHelper.getMessage("changePassword.error.oldmismatch"));
    }
    
    passwordManagerService.setNewPassword(newPassword, user);
    loginHelper.updateUser(user);
    
    sendNotification(user);
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
    
  }
  
  /**
   * <code>changePassword</code> method will change the password of the user.
   * 
   * @param user
   *          current logged in user.
   * @param param
   *          contains the old password and new password.
   * @return the new password.
   */
  public SPResponse changePasswordExpired(Object[] param) {
    
    String oldPassword = (String) param[0];
    String newPassword = (String) param[1];
    String email = (String) param[2];
    if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)
        || StringUtils.isBlank(email)) {
      throw new InvalidParameterException(MessagesHelper.getMessage("pulse.error.message.generic",
          LocaleHelper.locale()));
    }
    
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new InvalidParameterException(MessagesHelper.getMessage("service.growl.message8",
          LocaleHelper.locale()));
    }
    Password passwords = user.getPasswords();
    if (passwords == null) {
      passwords = Password.newPassword(user.getPassword(), user.getPassword());
      userRepository.updateUser(user);
    }
    
    if (!passwordEncoder.matches(oldPassword, passwords.getPassword())) {
      LOG.info("Old password didn't match");
      throw new SPException(MessagesHelper.getMessage("changePassword.error.oldmismatch"));  
    }
    
    passwordManagerService.setNewPassword(newPassword, user);
    sendNotification(user);
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
    
  }
  
  @Async
  private void sendNotification(User user) {
    notificationsProcessor.process(NotificationType.ChangePassword, user, user, null, false);
  }
  
  /**
   * <code>changePassword</code> method will change the password of the user.
   * 
   * @param user
   *          is the Super Administrator user.
   * 
   * @param param
   *          contains the old password and new password.
   * @return the new password.
   */
  public SPResponse changeSuperPassword(User user, Object[] param) {
    
    String oldPassword = (String) param[0];
    String newPassword = (String) param[1];
    
    if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
      throw new InvalidParameterException("Password cannot be blank or empty");
    }
    
    MasterPassword masterPassword = userRepository.getMasterPassword();
    newPassword = passwordEncoder.encode(newPassword);
    if (masterPassword == null) {
      LOG.debug("Setting the master password for the first time");
      masterPassword = new MasterPassword();
      masterPassword.setOldPassword(newPassword);
      masterPassword.setPassword(newPassword);
    } else {
      if (!passwordEncoder.matches(oldPassword, masterPassword.getPassword())) {
        LOG.info("Old password didn't match");
        throw new SPException(MessagesHelper.getMessage("changePassword.error.oldmismatch"));
      }
      
      if (passwordEncoder.matches(newPassword, masterPassword.getOldPassword())) {
        LOG.info("New Password cannot be same as old password.");
        throw new SPException(MessagesHelper.getMessage("changePassword.error.newsamemismatch"));
      }
      masterPassword.setOldPassword(masterPassword.getPassword());
      masterPassword.setPassword(newPassword);
    }
    
    companyFactory.updateMasterPassword(masterPassword);
    
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
    
  }
}
