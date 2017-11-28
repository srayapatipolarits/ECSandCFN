package com.sp.web.controller.goal;

import com.sp.web.Constants;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.Token;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.signin.SPNoteFeedbackLoginHelper;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.token.TokenProcessor;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vikram This is the token processor for managing feedback tokens.
 */
@Component("spNoteFeedbackTokenProcessor")
public class SPNoteFeedbackTokenProcessor implements TokenProcessor {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private SPNoteFeedbackRepository spNoteFeedbackRepository;
  
  @Autowired
  private TokenRepository tokenRepository;
  
  @Autowired
  private SPNoteFeedbackLoginHelper spNoteFeedbackLoginHelper;
  
  @Autowired
  private HttpServletRequest request;
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  private LogGateway notificationGateway;
  
  @Override
  public void processToken(Token token) {
    // get the user id from the params
    String tokenId = token.getTokenId();
    PracticeFeedback feedback = spNoteFeedbackRepository.findFeedbackbyTokenId(tokenId);
    String decline = request.getParameter("decline");
    /* check if valid feedback exist for that token, otherwise throw invalid token exception */
    if (feedback != null) {
      if (StringUtils.isBlank(decline)) {
        token.addParam("feedbackId", feedback.getId());
        
        if (feedback.getFeedbackStatus() == RequestStatus.COMPLETED) {
          token.setTokenStatus(TokenStatus.INVALID);
          token.invalidate("Token already used.");
          tokenRepository.updateToken(token);
          token.setRedirectToView(Constants.VIEW_FEEDBACK_TOKEN_USED);
        } else if (feedback.getFeedbackStatus() == RequestStatus.DECLINED) {
          token.setRedirectToView(Constants.VIEW_FEEDBACK_ALREADY_DECLINE);
        } else if (feedback.getFeedbackStatus() == RequestStatus.NOT_INITIATED) {
          // check if user exists and get the user
          FeedbackUser feedbackUser = userRepository.findFeedbackUser(feedback.getFeedbackUserId());
          SPGoal goal = goalFactory.getGoal(feedback.getGoalId());
          
          // Need feedbackUser to be authenticated and set in session so that he can submit feedback
          // response.
          // Need memberUser name, Title, profileimage to be displayed on submit feedback request
          // page
          if (feedbackUser != null) {
            
            User memberUser = userRepository.findUserById(feedbackUser.getFeedbackFor());
            
            if (memberUser != null) {
              String introMessage = null;
              switch (goal.getCategory()) {
              case ActionPlan:
                introMessage = MessagesHelper.getMessage(
                    "notesAndFeedback.feedback.response.orgPlan.intro", goal.getName());
                break;
              case Blueprint:
                introMessage = MessagesHelper.getMessage(
                    "notesAndFeedback.feedback.response.blueprint.intro");
                break;
              default:
                introMessage = MessagesHelper
                .getMessage("notesAndFeedback.feedback.response.individual.intro", goal.getName());
              }
                  
              introMessage = MessagesHelper.genderNormalize(introMessage, memberUser);
              
              token.addParam("heading", introMessage);
              BaseUserDTO baseUserDTO = new BaseUserDTO(memberUser);
              token.addParam("user", baseUserDTO);
            } else {
              token.invalidate("User doesnot exist or might have deleted ");
              tokenRepository.persistToken(token);
              throw new InvalidTokenException("Invalid Request", token);
            }
          }
          
          spNoteFeedbackLoginHelper.authenticateUserAndSetSession(feedbackUser);
          tokenRepository.persistToken(token);
          token.setRedirectToView("submitRequestFeedback");
        }
      } else {
        if (feedback.getFeedbackStatus() == RequestStatus.DECLINED) {
          token.setRedirectToView(Constants.VIEW_FEEDBACK_ALREADY_DECLINE);
        } else {
          token.setRedirectToView(Constants.VIEW_FEEDBACK_DECLINE);
          feedback.setFeedbackStatus(RequestStatus.DECLINED);
          spNoteFeedbackRepository.updateRequestFeedback(feedback);
          FeedbackUser feedbackUser = userRepository.findFeedbackUser(feedback.getFeedbackUserId());
          spNoteFeedbackLoginHelper.authenticateUserAndSetSession(feedbackUser);
          handleDeclineFlow(feedback, feedbackUser);
        }
        
      }
    } else {
      token.invalidate("Feedback Request got expired or might have deletd by user.");
      tokenRepository.persistToken(token);
      throw new InvalidTokenException("Invalid Request", token);
    }
    
    /*
     * } catch (SPException e) { token.invalidate("User not found :" + userEmail);
     * tokenRepository.persistToken(token); throw new InvalidTokenException(e, token); }
     */
  }
  
  private void handleDeclineFlow(PracticeFeedback feedback, User user) {
    String feedbackForUserId = feedback.getUserId();
    User feedbackFor = userRepository.findUserById(feedbackForUserId);
    UserType userTye = UserType.Member;
    if (user instanceof FeedbackUser) {
      userTye = ((FeedbackUser) user).getType();
    }
    BaseUserDTO userDto = new BaseUserDTO(user);
    String message;
    if (userTye == UserType.External) {
      message = MessagesHelper.getMessage("log.PracticeFeedback.FeedbackDeclined.message",
          userDto.getEmail(), userDto.getEmail(), feedback.getId());
    } else {
      message = MessagesHelper.getMessage("log.PracticeFeedback.FeedbackDeclined.message",
          userDto.getName(), userDto.getEmail(), feedback.getId());
    }
    
    LogRequest logRequest = new LogRequest(LogActionType.FeedbackDeclined, feedbackFor, user);
    logRequest.setDoMessagesOverride(true);
    logRequest.addParam(Constants.PARAM_MESSAGE, message);
    notificationGateway.logNotification(logRequest);
  }
  
}
