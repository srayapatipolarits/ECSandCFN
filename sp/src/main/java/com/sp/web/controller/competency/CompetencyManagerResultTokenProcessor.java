package com.sp.web.controller.competency;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.service.token.TokenProcessor;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 *
 *         The blueprint token processor.
 */
@Component("competencyManagerResultProcessor")
public class CompetencyManagerResultTokenProcessor implements TokenProcessor {

  //private static final Logger log = Logger.getLogger(CompetencyEvaluationTokenProcessor.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Override
  public void processToken(Token token) {
    // get the feedback user
    String feedbackUserId = token.getParamAsString(Constants.PARAM_FEEDBACK_USERID);
    Assert.notNull(feedbackUserId, "Invalid token, feedback user not found.");
    
    // get the feedback user
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(feedbackUserId);
    Assert.notNull(feedbackUser, "Feedback user not found.");
    
    if (!GenericUtils.isSameAsLoggedInUser(feedbackUser)) {
      loginHelper.authenticateUserAndSetSession(feedbackUser);
    }

    // redirect to the competency manager view
    token.setRedirectToView("competencyManagerResult");
  }
  
}
