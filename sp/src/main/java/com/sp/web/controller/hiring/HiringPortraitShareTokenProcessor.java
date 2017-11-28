package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenStatus;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.token.TokenProcessor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The token processor for hiring lens.
 */
@Component("hiringSharePortriatTokenProcessor")
public class HiringPortraitShareTokenProcessor implements TokenProcessor {
  
  private static final String EXPIRED_THANK_YOU = "redirect:http://www.surepeople.com/?status=404";
  
  private static final Logger LOG = Logger.getLogger(HiringPortraitShareTokenProcessor.class);
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  /**
   * 
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {
    
    String feedbackUserId = token.getParamAsString(Constants.PARAM_FEEDBACK_USERID);
    FeedbackUser feedbackUser = feedbackRepository.findByIdValidated(feedbackUserId);

    if (feedbackUser == null) {
      expired(token);
      return;
    }
    
    HiringUser hiringUser = hiringUserFactory.getUser(feedbackUser.getFeedbackFor());
    if (hiringUser == null) {
      expired(token);
      return;
    }
    
    loginHelper.authenticateUserAndSetSession(feedbackUser);
    token.setRedirectToView("hiringSharePortrait");
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Hiring portrait share token prcessed, token is " + token);
    }
  }

  /**
   * The token request has expired.
   * 
   * @param token
   *        - token expired
   */
  private void expired(Token token) {
    token.setRedirectToView(EXPIRED_THANK_YOU);
    token.setTokenStatus(TokenStatus.INVALID);
    return;
  }

}
