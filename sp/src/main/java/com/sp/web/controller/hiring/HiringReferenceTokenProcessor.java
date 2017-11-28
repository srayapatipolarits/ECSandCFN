package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.service.token.TokenProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 *
 *         The token processor for the hiring reference.
 */
@Component("hiringReference")
public class HiringReferenceTokenProcessor implements TokenProcessor {

  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  LoginHelper loginHelper;

  @Autowired
  private HttpServletRequest request;

  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {
    String hiringReferenceId = (String) token.getParam(Constants.PARAM_FEEDBACK_USERID);
    FeedbackUser hiringUser = feedbackRepository.findByIdValidated(hiringReferenceId);

    // /Authenticate the user and create a session for the user
    loginHelper.authenticateUserAndSetSession(hiringUser);

    HttpSession httpSession = request.getSession();
    /* if external user has verifed his external details, then show the growth request, other wise */
    httpSession.setAttribute(Constants.PARAM_FEEDBACK_USERID, hiringUser.getId());

    switch (hiringUser.getUserStatus()) {
    case INVITATION_SENT:
      // take the user to complete profile and
      // take the assessment
      token.addParam(Constants.PARAM_FIRSTNAME, hiringUser.getFirstName());
      token.addParam(Constants.PARAM_LASTNAME, hiringUser.getLastName());
      token.addParam(Constants.PARAM_EMAIL, hiringUser.getEmail());
      token.addParam(Constants.PARAM_HIRING_REQUEST, "hiringRefereces");
      if (hiringUser.getLinkedInUrl() == null) {
        token.addParam(Constants.PARAM_LINKEDIN_URL, "");
      } else {
        token.addParam(Constants.PARAM_LINKEDIN_URL, hiringUser.getLinkedInUrl());
      }

      token.setRedirectToView("hiringReferenceCompleteProfile");
      break;
    case ASSESSMENT_PENDING:
      // send them to the welcome page
      token.setRedirectToView("externalWelcome");
      token.addParam(Constants.PARAM_FEEDBACK_USERID, hiringUser.getId());
      break;
    case ASSESSMENT_PROGRESS:
      // take the candidates to the assessment screens
      token.setRedirectToView("externalRestart");
      break;
    default:
      // take them to the thank you/error page
      token.setRedirectToView("externalThankYou");
    }
  }

}
