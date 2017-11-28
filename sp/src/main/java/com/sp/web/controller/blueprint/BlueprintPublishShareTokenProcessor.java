package com.sp.web.controller.blueprint;

import com.sp.web.Constants;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.service.blueprint.BlueprintFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.token.TokenProcessor;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 *
 *         The blueprint token processor.
 */
@Component("blueprintPublishShareTokenProcessor")
public class BlueprintPublishShareTokenProcessor implements TokenProcessor {

  @Autowired
  UserFactory userFactory;
  
  @Autowired
  SPGoalFactory goalsFactory;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  BlueprintFactory blueprintFactory;
  
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
    if (blueprintStatus != GoalStatus.PUBLISHED) {
      // check if the backup exists
      BlueprintBackup blueprintBackup = blueprintFactory
          .getBlueprintBackupFromBlueprintId(blueprint.getId());
      Assert.notNull(blueprintBackup, "Blueprint not in published state and no backup found.");
    } 
    
    loginHelper.authenticateUserAndSetSession(feedbackUser);

    // adding data to the session
    token.addParam(Constants.PARAM_MEMBER, member);
    
    // redirect to the blueprint share view
//    token.setRedirectToView(Constants.VIEW_BLUEPRINT_PUBLISH_SHARE);
    token.setRedirectToView("redirect:/blueprint/share/publish/" + feedbackUser.getId());
  }
  
}
