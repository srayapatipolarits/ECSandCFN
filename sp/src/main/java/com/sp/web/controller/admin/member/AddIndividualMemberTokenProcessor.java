package com.sp.web.controller.admin.member;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.exception.SPException;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.token.TokenProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham This is the token processor for managing add member
 *         tokens.
 */
@Component("addIndividualMemberTokenProcessor")
public class AddIndividualMemberTokenProcessor implements TokenProcessor {

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

    // check for user status
    if (user.getUserStatus() == UserStatus.PROFILE_INCOMPLETE) {

      /**
       * Perisisting the redirect to view as it will be required while
       * displaying the view when session timesout, as token is stored in the
       * session, in case session timestou, the belowinformation set is lost
       */
      token.setRedirectToView(Constants.VIEW_ADD_INDIVIDUAL_MEMBER);
      tokenRepository.persistToken(token);

      UserProfileForm profileForm = new UserProfileForm(user);
      token.addParam(Constants.PARAM_USER_PROFILE_FORM, profileForm);
      token.addParam("tokenId", token.getTokenId());
      token.addParam("address", user.getAddress());

    } else {
      token.invalidate("User status not in profile incomplete state !!!");
      tokenRepository.persistToken(token);
      throw new InvalidTokenException(token);
    }
  }
}
