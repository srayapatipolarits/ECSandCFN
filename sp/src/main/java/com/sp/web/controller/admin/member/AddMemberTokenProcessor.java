package com.sp.web.controller.admin.member;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.exception.SPException;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.Company;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.token.TokenProcessor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham This is the token processor for managing add member
 *         tokens.
 */
@Component("addMemberTokenProcessor")
public class AddMemberTokenProcessor implements TokenProcessor {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  TokenRepository tokenRepository;

  /*
   * (non-Javadoc)
   * @see
   * com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token
   * )
   */
  @Override
  public void processToken(Token token) {

    // get the user id from the params
    String userEmail = (String) token.getParam(Constants.PARAM_USER_EMAIL);

    // check if user exists and get the user
    User user;
    try {
      user = userRepository.findByEmailValidated(userEmail);

    } catch (SPException e) {
      token.invalidate("User not found :" + userEmail);
      tokenRepository.persistToken(token);
      throw new InvalidTokenException(e, token);
    }
    token.addParam(Constants.PARAM_LOCALE, user.getUserLocale());

    // check for user status
    if (user.getUserStatus() == UserStatus.PROFILE_INCOMPLETE) {

      /**
       * Perisisting the redirect to view as it will be required while
       * displaying the view when session timesout, as token is stored in the
       * session, in case session timestou, the belowinformation set is lost.
       */
      token.setRedirectToView(Constants.VIEW_ADD_MEMBER);
      tokenRepository.persistToken(token);

      /* Fetch the company details, to be shown on the view */
      /*
       * Add this check, as using the same in case of alternate billing, an individual user is
       * getting registered, then individual user can set this password from the email link
       */
      if (StringUtils.isNotBlank(user.getCompanyId())) {
        Company company = accountRepository.findCompanyById(user.getCompanyId());
        token.addParam("companyName", company.getName());  
      }
      UserProfileForm profileForm = new UserProfileForm(user);
      token.addParam(Constants.PARAM_USER_PROFILE_FORM, profileForm);
      token.addParam("tokenId", token.getTokenId());
      token.addParam("address", user.getAddress());

    } else {
      token.setRedirectToView(Constants.VIEW_INCOMPLETE_ASSESSMENT_HOME);
//    [DAX] 8 Sep 2015 Removing the token invalidation as the user has completed 
//    the profile and we are taking them to the login page.
//      token.invalidate("User status not in profile incomplete state !!!");
//      tokenRepository.persistToken(token);
//      throw new InvalidTokenException(token);
    }
  }
}
