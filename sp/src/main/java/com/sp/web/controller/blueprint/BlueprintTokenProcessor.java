package com.sp.web.controller.blueprint;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintApprover;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.token.TokenProcessor;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 *
 *         The blueprint token processor.
 */
@Component("blueprintTokenProcessor")
public class BlueprintTokenProcessor implements TokenProcessor {
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Override
  public void processToken(Token token) {
    // get the feedback user
    String feedbackUserId = token.getParamAsString(Constants.PARAM_FEEDBACK_USERID);
    Assert.notNull(feedbackUserId, "Feedback user id not found in request.");
    
    // get the feedback user
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(feedbackUserId);
    Assert.notNull(feedbackUser, "Feedback user not found.");
    
    // get the member for whom the feedback is being provided
    User member = userFactory.getUser(feedbackUser.getFeedbackFor());
    Assert.notNull(member, "Member not found.");
    
    // get the blueprint for the member
    Blueprint blueprint = goalsFactory.getBlueprint(member);
    Assert.notNull(blueprint, MessagesHelper.getMessage("service.growl.message4"));
    
    // check the status
    final GoalStatus blueprintStatus = blueprint.getStatus();
    Assert.isTrue(blueprintStatus != GoalStatus.PUBLISHED, "Blueprint already published.");
    
    if (blueprintStatus == GoalStatus.APPROVED) {
      boolean isApprovalRequest = (boolean) token
          .getParam(Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST);
      if (isApprovalRequest) {
        throw new InvalidRequestException("Blueprint already approved.");
      } else {
        // validate if the approver is the correct approver
        final BlueprintApprover approver = blueprint.getApprover();
        if (approver != null && !feedbackUser.equals(approver.getEmail())) {
          throw new InvalidRequestException("User not approver but registered an approval request.");
        }
      }
    } else if (blueprintStatus == GoalStatus.PUBLISHED) {
      throw new InvalidRequestException("Blueprint already published.");
    }

    // logging in the user in case the current session is not the same
    // for the given user
    if (!GenericUtils.isSameAsLoggedInUser(feedbackUser)) {
      loginHelper.authenticateUserAndSetSession(feedbackUser);
    }
    
    // redirect to the blueprint share view
    //token.setRedirectToView(Constants.VIEW_BLUEPRINT_SHARE);
    token.setRedirectToView("redirect:/blueprint/share/" + feedbackUser.getId());
  }
  
}
