/**
 * 
 */
package com.sp.web.controller.feedback;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.model.UserType;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.token.TokenProcessor;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <code>FeedbackInviteTokenProcessor</code> method will process the token for the feedback request to be given by
 * external member.
 * 
 * @author pradeep
 *
 */
@Component("feedbackInviteTokenProcessor")
public class FeedbackInviteTokenProcessor implements TokenProcessor {

  private static final Logger LOG = Logger.getLogger(FeedbackInviteTokenProcessor.class);

  @Autowired
  private FeedbackRepository feedbackRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private LoginHelper loginHelper;

  @Autowired
  private SPGoalFactory spGoalFactory;

  /**
   * 
   * @see com.sp.web.service.token.TokenProcessor#processToken(com.sp.web.model.Token)
   */
  @Override
  public void processToken(Token token) {

    Map<String, Object> paramsMap = token.getParamsMap();

    String feedbackUserId = (String) paramsMap.get(Constants.PARAM_FEEDBACK_USERID);

    /* fetch the growthRequest and check if user has verified his personal information or not */
    FeedbackRequest feedbackRequest = feedbackRepository.findFeedbackRequestByFeedbackUserId(feedbackUserId);
    /* find the user and log inthe user */

    /** Thankyou message is same for growth nd prism 360 */
    if (feedbackRequest == null) {
      token.setRedirectToView("externalFeedbackExpiredThankYou");
      return;
    }
    /* check growth request */

    FeedbackUser feedbackUser = feedbackRepository.findByIdValidated(feedbackUserId);

//    if (!isFeedbackUserIsSameLoggedIn(token.getTokenId(), feedbackUser)) {
//      token.setRedirectToView("feedbackLoggedInError");
//      return;
//    }
    HttpSession httpSession = request.getSession();
    
    if (!GenericUtils.isSameAsLoggedInUser(feedbackUser)) {
      loginHelper.authenticateUserAndSetSession(feedbackUser);
    }
    
    /* if external user has verifed his external details, then show the growth request, other wise */
    httpSession.setAttribute(Constants.PARAM_FEEDBACK_REQUEST_ID, feedbackRequest.getId());
    httpSession.setAttribute(Constants.PARAM_FEEDBACK_USERID, feedbackUser.getId());
    httpSession.setAttribute(Constants.PARAM_FEEDBACK_FOR_ID, feedbackRequest.getRequestedById());

    switch (feedbackUser.getUserStatus()) {
    case INVITATION_SENT:
      token.addParam(Constants.PARAM_FIRSTNAME, feedbackUser.getFirstName());
      token.addParam(Constants.PARAM_LASTNAME, feedbackUser.getLastName());
      token.addParam(Constants.PARAM_EMAIL, feedbackUser.getEmail());
      token.addParam(Constants.PARAM_FEEDBACK_REQUEST, "feedbackRequest");
      if (feedbackUser.getLinkedInUrl() == null) {
        token.addParam(Constants.PARAM_LINKEDIN_URL, "");
      } else {
        token.addParam(Constants.PARAM_LINKEDIN_URL, feedbackUser.getLinkedInUrl());
      }

      token.setRedirectToView("hiringReferenceCompleteProfile");
      break;
    case ASSESSMENT_PENDING:
      token.setRedirectToView("externalWelcome");
      token.addParam(Constants.PARAM_FEEDBACK_USERID, feedbackRequest.getFeedbackUserId());
      token.addParam(Constants.PARAM_TOKEN, token.getTokenId());
      break;
    case ASSESSMENT_PROGRESS:
      token.setRedirectToView("redirect:/assessment360/" + feedbackRequest.getFeedbackUserId());
      break;

    case VALID:
      // take them to the thank you/error page
      if (feedbackUser.getType() == UserType.External) {
        token.setRedirectToView("externalFeedbackThankYou");
      } else {
        token.setRedirectToView("memberFeedbackThankYou");
      }
      break;
    default:
      break;
    }

    LOG.info("Feedack token prcessed, token is " + token);
  }

//  private boolean isFeedbackUserIsSameLoggedIn(String tokenId, FeedbackUser fbUser) {
//
//    HttpSession session = request.getSession();
//
//    /* check if user is loggedin or not */
//    if (SecurityContextHolder.getContext().getAuthentication() != null
//        && !((SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AnonymousAuthenticationToken) || SecurityContextHolder
//            .getContext().getAuthentication().getPrincipal() instanceof String)) {
//
//      /* get the logged in user detail */
//      User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//      LOG.info("Feedback user " + fbUser.getEmail() + ", User for feedback " + user.getEmail());
//
//      if (user.getEmail().equalsIgnoreCase(fbUser.getEmail())) {
//        return true;
//      }
//    } else {
//      loginHelper.authenticateUserAndSetSession(fbUser);
//      return true;
//    }
//
//    session.setAttribute(fbUser.getId(), fbUser);
//    return false;
//  }
}
