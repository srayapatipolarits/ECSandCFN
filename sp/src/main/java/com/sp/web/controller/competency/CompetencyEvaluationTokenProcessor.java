package com.sp.web.controller.competency;

import com.sp.web.Constants;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.product.CompanyFactory;
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
@Component("competencyEvaluationProcessor")
public class CompetencyEvaluationTokenProcessor implements TokenProcessor {

  //private static final Logger log = Logger.getLogger(CompetencyEvaluationTokenProcessor.class);
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /* Injecting constants, to initalize static varaible from properties file. */
  @Autowired
  private Constants constants;
  
  @Override
  public void processToken(Token token) {
    // get the feedback user
    String feedbackUserId = token.getParamAsString(Constants.PARAM_FEEDBACK_USERID);
    Assert.notNull(feedbackUserId, "Invalid token, feedback user not found.");
    
    // get the feedback user
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(feedbackUserId);
    Assert.notNull(feedbackUser, "Feedback user not found.");
    
    CompanyDao company = companyFactory.getCompany(feedbackUser.getCompanyId());
    if (!company.isEvaluationInProgress()) {
      token.setRedirectToView(constants.VIEW_COMPETENCY_EVALUATION_ENDED);
    }
    
    if (!GenericUtils.isSameAsLoggedInUser(feedbackUser)) {
      loginHelper.authenticateUserAndSetSession(feedbackUser);
    }

    // redirect to the blueprint share view
    token.setRedirectToView(constants.VIEW_COMPETENCY_EVALUATION);
  }
  
}
