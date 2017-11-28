package com.sp.web.controller.goal;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.Token;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.lndfeedback.DevelopmentFeedbackFactory;
import com.sp.web.service.token.TokenProcessor;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vikram This is the token processor for managing feedback tokens.
 */
@Component("developmentFeedbackTokenProcessor")
public class DevelopmentFeedbackTokenProcessor implements TokenProcessor {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  @Qualifier("developmentFeedback")
  private DevelopmentFeedbackFactory developmentFeedbackFactory;
  
  @Autowired
  private TokenRepository tokenRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private HttpServletRequest request;
  
  
  @Override
  public void processToken(Token token) {
    // get the user id from the params
    String tokenId = token.getTokenId();
    DevelopmentFeedback feedback = developmentFeedbackFactory.findDevFeedbackbyTokenId(tokenId);
    
    // String decline = request.getParameter("decline");
    /* check if valid feedback exist for that token, otherwise throw invalid token exception */
    if (feedback != null) {
      // if (StringUtils.isBlank(decline)) {
      // token.addParam("feedbackId", feedback.getId());
      //
      if (feedback.getRequestStatus() == RequestStatus.COMPLETED) {
        token.setTokenStatus(TokenStatus.INVALID);
        token.invalidate("Token already used.");
        tokenRepository.updateToken(token);
        token.setRedirectToView(Constants.VIEW_FEEDBACK_TOKEN_USED);
      } else if (feedback.getRequestStatus() == RequestStatus.DECLINED) {
        token.setRedirectToView(Constants.VIEW_FEEDBACK_ALREADY_DECLINE);
      } else if (feedback.getRequestStatus() == RequestStatus.NOT_INITIATED) {
        // check if user exists and get the user
        request.getSession().setAttribute("id", feedback.getId());
        FeedbackUser feedbackUser = userRepository.findFeedbackUser(feedback.getFeedbackUserId());
        if (!GenericUtils.isSameAsLoggedInUser(feedbackUser)) {
          loginHelper.authenticateUserAndSetSession(feedbackUser);
        }
        token.setRedirectToView("submitRequestFeedback");
      }
      /*
       * } catch (SPException e) { token.invalidate("User not found :" + userEmail);
       * tokenRepository.persistToken(token); throw new InvalidTokenException(e, token); }
       */
    }else{
      token.invalidate("Feedback Request not found");
      tokenRepository.persistToken(token); 
      throw new InvalidTokenException("Development Feedback request no longer exist", token); }
    }
  
  // private void handleDeclineFlow(DevelopmentFeedback feedback, User user) {
  // String feedbackForUserId = feedback.getUserId();
  // User feedbackFor = userRepository.findUserById(feedbackForUserId);
  // UserType userTye = UserType.Member;
  // if (user instanceof FeedbackUser) {
  // userTye = ((FeedbackUser) user).getType();
  // }
  // BaseUserDTO userDto = new BaseUserDTO(user);
  // String message;
  // if (userTye == UserType.External) {
  // message = MessagesHelper.getMessage("log.PracticeFeedback.FeedbackDeclined.message",
  // userDto.getEmail(), userDto.getEmail(), feedback.getId());
  // } else {
  // message = MessagesHelper.getMessage("log.PracticeFeedback.FeedbackDeclined.message",
  // userDto.getName(), userDto.getEmail(), feedback.getId());
  // }
  //
  // LogRequest logRequest = new LogRequest(LogActionType.FeedbackDeclined, feedbackFor, user);
  // logRequest.setDoMessagesOverride(true);
  // logRequest.addParam(Constants.PARAM_MESSAGE, message);
  // notificationGateway.logNotification(logRequest);
  // }
  
}
