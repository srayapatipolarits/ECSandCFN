package com.sp.web.controller.reset;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.Token;
import com.sp.web.model.TokenStatus;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.service.token.TokenProcessor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <code>ResetPasswordTokenProcessor</code> class provides processing for reset password functionality.
 * 
 * @author pruhil
 */
@Service("resetPasswordProcessor")
@Scope("prototype")
public class ResetPasswordTokenProcessor implements TokenProcessor {

  /** initializing the logger. */
  private static final Logger LOG = Logger.getLogger(ResetPasswordTokenProcessor.class);

  /** http servlet request object to store attribute. */
  @Autowired
  private HttpServletRequest request;

  @Autowired
  private TokenRepository tokenRepository;

  /**
   * <code>processToken</code> method will get the information which is to be
   * 
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model .Token)
   */
  @Override
  public void processToken(Token token) {

    // validate the token
    if (token.getTokenStatus() == TokenStatus.INVALID) {
      throw new InvalidTokenException("Token already expired", token);
    }

    /* Get the user email, for which this token is generated */
    String emailId = (String) token.getParamsMap().get(Constants.PARAM_USER_EMAIL);

    /*
     * set the user email id in the request attribute to put it as hidden field in set new password form when user set
     * the new password. This is required to know for which user the request is submitted.
     */
    request.setAttribute(Constants.PARAM_USER_EMAIL, emailId);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Token processed for the reset Password, user is " + emailId);
    }

    /** Storing in the token, in the session, to avoid. */
    HttpSession httpSession = request.getSession();
    httpSession.setAttribute(emailId + Constants.PARAM_TOKEN, token.getTokenId());
    httpSession.setAttribute("email", emailId);
    // setting the veiw to redirect to
    token.setRedirectToView(Constants.VIEW_RESET_PASSWORD);
  }

}
