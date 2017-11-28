package com.sp.web.controller.hiring.lens;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.service.hiring.lens.HiringLensFactoryCache;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.token.TokenProcessor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 * 
 *         The token processor for hiring lens.
 */
@Component("hiringLensTokenProcessor")
public class HiringLensTokenProcessor implements TokenProcessor {
  
  private static final String EXTERNAL_FEEDBACK_EXPIRED_THANK_YOU = "externalFeedbackExpiredThankYou";
  
  private static final Logger LOG = Logger.getLogger(HiringLensTokenProcessor.class);
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private HiringLensFactoryCache hiringLensFactoryCache;
  
  @Autowired
  private HttpServletRequest request;
  
  @Autowired
  private LoginHelper loginHelper;
  
  /**
   * 
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {
    
    String feedbackUserId = token.getParamAsString(Constants.PARAM_FEEDBACK_USERID);
    FeedbackUser feedbackUser = hiringLensFactoryCache.getById(feedbackUserId);
    
    if (feedbackUser == null || feedbackUser.getUserStatus() == UserStatus.VALID) {
      expired(token);
      return;
    }
    
    HiringUser hiringUser = hiringUserFactory.getUser(feedbackUser.getFeedbackFor());
    if (hiringUser == null) {
      expired(token);
      return;
    }
    
    loginHelper.authenticateUserAndSetSession(feedbackUser);
    
    /* if external user has verifed his external details, then show the growth request, other wise */
    HttpSession httpSession = request.getSession();
    httpSession.setAttribute(Constants.PARAM_FEEDBACK_USERID, feedbackUserId);
    httpSession.setAttribute(Constants.PARAM_FEEDBACK_FOR_ID, hiringUser.getId());
    
    switch (feedbackUser.getUserStatus()) {
    case INVITATION_SENT:
      token.addParam(Constants.PARAM_FIRSTNAME, feedbackUser.getFirstName());
      token.addParam(Constants.PARAM_LASTNAME, feedbackUser.getLastName());
      token.addParam(Constants.PARAM_EMAIL, feedbackUser.getEmail());
      token.setRedirectToView("hiringLensCompleteProfile");
      break;
    case ASSESSMENT_PENDING:
      token.setRedirectToView("hiringLensWelcome");
      token.addParam(Constants.PARAM_FEEDBACK_USERID, feedbackUserId);
      token.addParam(Constants.PARAM_TOKEN, token.getTokenId());
      break;
    case ASSESSMENT_PROGRESS:
      token.setRedirectToView("redirect:/assessment360/" + feedbackUserId);
      break;
    default:
      break;
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Hiring Lens token prcessed, token is " + token);
    }
  }

  /**
   * The token request has expired.
   * 
   * @param token
   *        - token expired
   */
  private void expired(Token token) {
    token.setRedirectToView(EXTERNAL_FEEDBACK_EXPIRED_THANK_YOU);
    token.setTokenStatus(TokenStatus.INVALID);
    return;
  }
  
  // private boolean isFeedbackUserIsSameLoggedIn(String tokenId, FeedbackUser fbUser) {
  //
  // HttpSession session = request.getSession();
  //
  // /* check if user is loggedin or not */
  // if (SecurityContextHolder.getContext().getAuthentication() != null
  // && !((SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof
  // AnonymousAuthenticationToken) || SecurityContextHolder
  // .getContext().getAuthentication().getPrincipal() instanceof String)) {
  //
  // /* get the logged in user detail */
  // User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  //
  // LOG.info("Feedback user " + fbUser.getEmail() + ", User for feedback " + user.getEmail());
  //
  // if (user.getEmail().equalsIgnoreCase(fbUser.getEmail())) {
  // return true;
  // }
  // } else {
  // loginHelper.authenticateUserAndSetSession(fbUser);
  // return true;
  // }
  //
  // session.setAttribute(fbUser.getId(), fbUser);
  // return false;
  // }
}
