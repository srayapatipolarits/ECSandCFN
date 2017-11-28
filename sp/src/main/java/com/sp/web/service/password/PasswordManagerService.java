package com.sp.web.service.password;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.InvalidPasswordException;
import com.sp.web.exception.SPException;
import com.sp.web.exception.SignInFailedException;
import com.sp.web.exception.SignInFailedException.SignInFailReason;
import com.sp.web.model.MasterPassword;
import com.sp.web.model.Password;
import com.sp.web.model.User;
import com.sp.web.product.CompanyFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * PasswordManagerService class will perform the operation for the user regarding password
 * management rules.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PasswordManagerService {
  
  private static final int MAX_FAILED_ATTEMPTS = 4;

  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * ValidatePassword method will validate the credentials for the user. It will check if the
   * company has enabled enhanced password security password.
   * 
   * @param masterPassword
   *          master password to be checked.
   * @param password
   *          user has entered.
   * @param user
   *          for which password to be check
   * @param company
   *          of the user.
   */
  public void validatePassword(String password, User user, CompanyDao company) {
    
    Password userPassword = user.getPasswords();
    //TODO: Why are we doing this? [dax  05-09-17]
    if (userPassword == null) {
      /* create the new password */
      userPassword = Password.newPassword(user.getPassword(), user.getPassword());
      user.setPasswords(userPassword);
      userFactory.updateUser(user);
      return;
    }
    
    if (company.isEnhancePasswordSecurity()) {
      enhancedValidation(password, user, userPassword);
    } else {
      if (!doAuthenticate(password, userPassword.getPassword())) {
        throw new InvalidPasswordException();
      }
    }
    
  }
  
  private boolean doAuthenticate(String password, String userPassword) {
    if (!passwordEncoder.matches(password, userPassword)) {
      MasterPassword masterPassword = companyFactory.getMasterPassword();
      if (masterPassword != null) {
        if (passwordEncoder.matches(password, masterPassword.getPassword())) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  /**
   * <code>enhancedValidation</code> method will validate the enhanced validation rules.
   * 
   * @param masterPassword
   *          . master password for the user.
   * @param password
   *          of the user.
   * @param user
   *          to check for the password.
   * @param userPassword
   *          - user password 
   */
  private void enhancedValidation(String password, User user, Password userPassword) {
    
    boolean updateUser = false;
    
    try {
      if (userPassword.isAccountLocked()) {
        /* check if user trying within 2 hours of locked time */
        if (ChronoUnit.SECONDS.between(userPassword.getLastUpdated(), LocalDateTime.now()) 
            < Constants.PASSWORD_LOCKOUT_DURATION) {
          throw new SignInFailedException(SignInFailReason.UserAccountLocked);
        } else {
          /* reset the settings in case user is logging in after locked out period */
          userPassword.setAccountLocked(false);
          userPassword.setFailedAttempts(0);
          updateUser = true;
        }
      }
      
      if (!doAuthenticate(password, userPassword.getPassword())) {
        updateUser = true;
        userPassword.incrementFailedAttempts();
        if (userPassword.getFailedAttempts() == MAX_FAILED_ATTEMPTS) {
          userPassword.setAccountLocked(true);
          throw new SignInFailedException(SignInFailReason.UserAccountLocked);
        } else {
          userPassword.setLastUpdated(LocalDateTime.now());
          throw new InvalidPasswordException(MessagesHelper.getMessage(
              "login.authentication.failed.11",
              String.valueOf((MAX_FAILED_ATTEMPTS - userPassword.getFailedAttempts()))));
        }
      } else {
        /* check if the user is logging in after lockout time or not */
        if (ChronoUnit.DAYS.between(userPassword.getLastChanged().toLocalDate(), LocalDate.now()) 
            > Constants.PASSWORD_EXPIRE_DURATION) {
          throw new SignInFailedException(SignInFailReason.PasswordExpired);
        }
        updateUser = userPassword.resetSettings();
      }
    } finally {
      if (updateUser) {
        userFactory.updateUser(user);
      }
    }
  }
  
  /**
   * setNewPassword will set the new password to the user profile.
   * 
   * @param newPassword
   *          is the new password of the user.
   * @param user
   *          whose new password is to be set.
   */
  public void setNewPassword(String newPassword, User user) {
    Password passwords = user.getPasswords();
    if (passwords == null) {
      passwords = Password.newPassword(user.getPassword(), user.getPassword());
      user.setPasswords(passwords);
    }
    String encode = passwordEncoder.encode(newPassword);
    /* validate if old password is same as new one */
    
    for (String password : passwords.getPreviousPassword()) {
      
      if (passwordEncoder.matches(newPassword, password)) {
        throw new SPException(MessagesHelper.getMessage("password.used.previous.error",
            LocaleHelper.locale(), passwords.getPreviousPassword().size()));
      }
    }
    
    passwords.updatePassword(encode);
    userFactory.updateUser(user);
  }
  
}
