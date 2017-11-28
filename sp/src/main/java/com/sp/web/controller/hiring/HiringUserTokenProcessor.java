package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.service.token.TokenProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The token processor for the hiring candidates.
 */
@Component("hiringCandidate")
public class HiringUserTokenProcessor implements TokenProcessor {
  
  @Autowired
  HiringRepository hiringRepository;
  
  @Autowired
  LoginHelper loginHelper;
  
  /**
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {
    String hiringUserId = token.getParamAsString(Constants.PARAM_HIRING_USER_ID);
    HiringUser user = hiringRepository.findByIdValidated(hiringUserId);
    
    token.addParam(Constants.PARAM_LOCALE, user.getUserLocale());
    //Authenticate the user and create a session for the user
    loginHelper.authenticateUserAndSetSession(user);
    token.addParam(Constants.PARAM_LOCALE, user.getUserLocale());
    
    switch (user.getUserStatus()) {
    case INVITATION_SENT:
      // take the user to complete profile and
      // take the assessment
      token.setRedirectToView("hiringCompleteProfile");
      break;
    case ADD_REFERENCES:
      token.setRedirectToView("candidateReference");
      break;
    case ASSESSMENT_PENDING:
      // send them to the welcome page
      token.setRedirectToView("candidateWelcome");
      break;
    case ASSESSMENT_PROGRESS:
      // take the candidates to the assessment screens
      token.setRedirectToView("candidateAssessment");
      break;
    default:
      // take them to the thank you/error page
      token.setRedirectToView("candidateThankYou");
    }
  }
  
}
