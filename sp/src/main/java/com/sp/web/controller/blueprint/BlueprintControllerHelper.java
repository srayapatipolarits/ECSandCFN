package com.sp.web.controller.blueprint;

import com.sp.web.Constants;
import com.sp.web.comments.CommentsFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.blueprint.BlueprintDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.ExternalUserForm;
import com.sp.web.form.blueprint.BlueprintForm;
import com.sp.web.form.blueprint.BlueprintResponseForm;
import com.sp.web.form.blueprint.BlueprintShareForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.blueprint.BlueprintBackup;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.service.blueprint.BlueprintFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.token.TokenRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The helper class for the blueprint controller.
 */
@Component
public class BlueprintControllerHelper {
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  private CommentsFactory commentsFactory;
  
  @Autowired
  private BlueprintFactory blueprintFactory;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  private SPNoteFeedbackRepository spNoteFeedbackRepository;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  @Autowired
  private TodoFactory todoFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  private LogGateway logGateway;
  
  /**
   * Helper method to get the blueprint settings.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the params for the request
   * @return the blueprint settings
   */
  public SPResponse getSettings(User user, Object[] params) {
    final SPResponse spResponse = new SPResponse();
    String feedbackId = (String) params[0];
    
    // added code to get the settings from company
    // in case of feedback from different companies
    String companyId = null;
    if (StringUtils.isEmpty(feedbackId)) {
      companyId = user.getCompanyId();
    } else {
      PracticeFeedback feedback = spNoteFeedbackRepository.findFeedbackById(feedbackId);
      Assert.notNull(feedback, "Feedback not found.");
      companyId = feedback.getCompanyId();
    }
    spResponse.add(Constants.PARAM_COMPANY, companyId);
    return spResponse.add(Constants.PARAM_BLUEPRINT_SETTINGS,
        blueprintFactory.getBlueprintSettings(companyId));
  }
  
  /**
   * The helper method to get the blueprint for the logged in user or the member's blueprint.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get blueprint request
   */
  public SPResponse getBlueprint(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String memberEmail = (String) params[0];
    User blueprintUser = userFactory.getUserToProcess(memberEmail, user);
    
    Blueprint blueprint = goalsFactory.getBlueprint(blueprintUser);
    
    if (blueprint != null) {
      if (blueprint.getStatus() == GoalStatus.PUBLISHED) {
        resp.add(Constants.PARAM_BLUEPRINT, new BlueprintDTO(blueprint));
      } else {
        resp.add(Constants.PARAM_BLUEPRINT, addBlueprintDTOWithValidation(user, blueprint));
      }
    } else {
      resp.add(Constants.PARAM_BLUEPRINT_SETTINGS,
          blueprintFactory.getBlueprintSettings(user.getCompanyId()));
    }
    
    return resp;
  }
  
  /**
   * Get the blueprint DTO along with validation.
   * 
   * @param user
   *          - logged in user
   * @param blueprint
   *          - logged in user
   * @return the blueprint DTO
   */
  private BlueprintDTO addBlueprintDTOWithValidation(User user, Blueprint blueprint) {
    BlueprintDTO blueprintDTO = new BlueprintDTO(blueprint, commentsFactory, userFactory);
    blueprintDTO.setValid(blueprintFactory.validateBlueprint(blueprint,
        blueprintFactory.getBlueprintSettings(user.getCompanyId())));
    return blueprintDTO;
  }
  
  /**
   * Helper method to create or update the blueprint for the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - blueprint data
   * @return the result for the create or update request
   */
  public SPResponse createOrUpdate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    BlueprintForm blueprintForm = (BlueprintForm) params[0];
    
    // check if the user already has blueprint set
    Blueprint blueprint = goalsFactory.getBlueprint(user);
    
    // creating the blueprint to get the blueprint id
    // if one doesn't exists for the user
    if (blueprint == null) {
      blueprint = new Blueprint();
      blueprint.setCategory(GoalCategory.Blueprint);
      blueprint.setCreatedOn(LocalDateTime.now());
      blueprint.setStatus(GoalStatus.EDIT);
      goalsFactory.updateBlueprint(blueprint);
      user.setBlueprintId(blueprint.getId());
      userFactory.updateUser(user);
    }
    
    // updating the blueprint from the data in the form
    blueprintForm.updateBlueprint(blueprint);
    // saving the blueprint to the database
    goalsFactory.updateBlueprint(blueprint);
    
    // returning the updated blueprint for the uid's
    return resp.add(Constants.PARAM_BLUEPRINT, addBlueprintDTOWithValidation(user, blueprint));
  }
  
  /**
   * The blueprint share request.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - share params
   * @return the response to the share request
   */
  public SPResponse share(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    BlueprintShareForm shareForm = (BlueprintShareForm) params[0];
    
    // validate the form
    shareForm.validate();
    
    // get the users blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // check if the blueprint is in a sharable state
    Assert.isTrue(blueprint.getStatus() == GoalStatus.EDIT,
        "Cannot share blueprint under approval.");
    
    List<FeedbackUser> feedbackUserList = getFeedbackUser(user, shareForm);
    
    feedbackUserList.forEach(feedbackUser -> doEditShare(user, shareForm, blueprint, feedbackUser));
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to share the blueprint after publish.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - the share form
   * @return the response to the share request
   */
  public SPResponse publishShare(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    BlueprintShareForm shareForm = (BlueprintShareForm) params[0];
    
    // do basic validation of the form
    shareForm.validate(true);
    
    // get the users blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // check if the blueprint is in a sharable state
    Assert.isTrue(blueprint.getStatus() == GoalStatus.PUBLISHED, "Blueprint not published.");
    
    // get the list of feedback users
    List<FeedbackUser> feedbackUserList = getFeedbackUser(user, shareForm);
    final String comment = shareForm.getComment();
    final boolean showProgress = shareForm.isShowProgress();
    
    // send the share request to all the users
    feedbackUserList.forEach(feedbackUser -> doPublishShare(user, comment, showProgress, blueprint,
        feedbackUser));
    
    return resp.isSuccess();
  }
  
  /**
   * Method to do a share request for the feedback user.
   * 
   * @param user
   *          - logged in user
   * @param comment
   *          - comment
   * @param showProgress
   *          - flag to show progress
   * @param blueprint
   *          - blueprint
   * @param feedbackUser
   *          - feedback user
   */
  private void doPublishShare(User user, String comment, boolean showProgress, Blueprint blueprint,
      FeedbackUser feedbackUser) {
    // creating a token request
    TokenRequest tokenRequest = new TokenRequest(TokenType.PERPETUAL);
    tokenRequest.addParam(Constants.PARAM_FEEDBACK_USERID, feedbackUser.getId());
    tokenRequest.addParam(Constants.PARAM_BLUEPRINT_SHOW_PROGRESS, showProgress);
    Token token = tokenFactory.getToken(tokenRequest, TokenProcessorType.BLUEPRINT_PUBLISH_SHARE);
    feedbackUser.saveTokenInformation(token);
    userFactory.updateUser(feedbackUser);
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_TOKEN, token);
    paramsMap.put(Constants.PARAM_COMMENT, comment);
    Map<String, String> genderTextMap = MessagesHelper.getGenderText(user);
    paramsMap.putAll(genderTextMap);
    
    // sending the notification
    notificationProcessor.process(NotificationType.BlueprintPublishShare, user, feedbackUser,
        paramsMap, false);
  }
  
  private void doEditShare(User user, BlueprintShareForm shareForm, Blueprint blueprint,
      FeedbackUser feedbackUser) {
    // creating a token request
    TokenRequest tokenRequest = new TokenRequest(TokenType.PERPETUAL);
    tokenRequest.addParam(Constants.PARAM_FEEDBACK_USERID, feedbackUser.getId());
    Token token = tokenFactory.getToken(tokenRequest, TokenProcessorType.BLUEPRINT_SHARE);
    feedbackUser.saveTokenInformation(token);
    userFactory.updateUser(feedbackUser);
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_TOKEN, token);
    paramsMap.put(Constants.PARAM_COMMENT, shareForm.getComment());
    paramsMap.putAll(MessagesHelper.getGenderText(user));
    if (shareForm.isApprovalRequest()) {
      if (!blueprintFactory.validateBlueprint(blueprint,
          blueprintFactory.getBlueprintSettings(user.getCompanyId()))) {
        tokenFactory.removeToken(token);
        throw new InvalidRequestException("Blueprint not valid.");
      }
      // doing the approval request
      token.addParam(Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST, true);
      tokenFactory.persistToken(token);
      blueprint.setStatus(GoalStatus.UNDER_APPROVAL);
      blueprint.addApprover(feedbackUser);
      notificationProcessor.process(NotificationType.BlueprintApproval, user, feedbackUser,
          paramsMap, false);
      goalsFactory.updateBlueprint(blueprint);
      if (feedbackUser.getType() == UserType.Member) {
        todoFactory.addTodo(feedbackUser, TodoRequest.newBlueprintApprovalRequest(user, feedbackUser));
      }
    } else {
      // adding flag if it is an approval request
      token.addParam(Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST, false);
      tokenFactory.persistToken(token);
      // doing the share request
      notificationProcessor.process(NotificationType.BlueprintShare, user, feedbackUser, paramsMap,
          false);
      if (feedbackUser.getType() == UserType.Member) {
        todoFactory.addTodo(feedbackUser,
            TodoRequest.newBlueprintFeedbackRequest(user, feedbackUser));
      }
    }
  }
  
  /**
   * Helper method to get the details of the blueprint for the share view.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - share details params
   * @return the response to the share blueprint details request
   */
  public SPResponse shareBlueprintDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String feedbackUserId = (String) params[0];

    Assert.hasText(feedbackUserId, "Feedback user required.");
    
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(feedbackUserId);
    User member = userFactory.getUser(feedbackUser.getFeedbackFor());
    Blueprint blueprint = goalsFactory.getValidBlueprint(member);
    
    Token token = tokenFactory.findTokenById(feedbackUser.getTokenId());
    
    // add the member
    resp.add(Constants.PARAM_MEMBER, new BaseUserDTO(member));
    
    // adding the feedback user
    resp.add(Constants.PARAM_FEEDBACK_USERID, user.getId());
    
    // check if it is an approval
    boolean isApprovalRequest = (boolean) token.getParam(
        Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST, false);
    resp.add(Constants.PARAM_BLUEPRINT_INTRO, MessagesHelper.genderNormalizeFromKey(
        (isApprovalRequest) ? Constants.BLUEPRINT_APPROVAL_INTRO_KEY
            : Constants.BLUEPRINT_SHARE_INTRO_KEY, member));
    
    // add status if this is an approval request
    resp.add(Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST, isApprovalRequest);
    
    // removing the comments that are not the current users comments
    resp.add(Constants.PARAM_BLUEPRINT,
        new BlueprintDTO(blueprint, commentsFactory).filterComments(user));
    
    // adding the settings
    resp.add(Constants.PARAM_BLUEPRINT_SETTINGS,
        blueprintFactory.getBlueprintSettings(member.getCompanyId()));
    
    return resp;
  }
  
  /**
   * Get the details for the blueprint when it has been published.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get details.
   */
  public SPResponse publishShareGetDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    String feedbackUserId = (String) params[0];

    Assert.hasText(feedbackUserId, "Feedback user required.");
    
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(feedbackUserId);
    User member = userFactory.getUser(feedbackUser.getFeedbackFor());
    Blueprint blueprint = goalsFactory.getValidBlueprint(member);
    
    Token token = tokenFactory.findTokenById(feedbackUser.getTokenId());
    
    // check the status of the blueprint
    if (blueprint.getStatus() != GoalStatus.PUBLISHED) {
      blueprint = blueprintFactory.getBlueprintBackupFromBlueprintId(blueprint.getId());
      Assert.notNull(blueprint,
          "Blueprint not published and no previously published blueprint found.");
    }
    
    // add the member
    resp.add(Constants.PARAM_MEMBER, new BaseUserDTO(member));
    
    // add the flag to show progress or not
    resp.add(Constants.PARAM_BLUEPRINT_SHOW_PROGRESS,
        token.getParam(Constants.PARAM_BLUEPRINT_SHOW_PROGRESS));
    
    // removing the comments that are not the current users comments
    resp.add(Constants.PARAM_BLUEPRINT, new BlueprintDTO(blueprint));
    
    // adding the settings
    resp.add(Constants.PARAM_BLUEPRINT_SETTINGS,
        blueprintFactory.getBlueprintSettings(member.getCompanyId()));
    
    return resp;
  }
  
  /**
   * Helper method to process the response from the share request.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - response parameters
   * @return the response to the process request
   */
  public SPResponse shareBlueprintResponse(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the response form and validate
    BlueprintResponseForm responseForm = (BlueprintResponseForm) params[0];
    responseForm.validate();
    
    // getting the feedback user from the response
    // to account for user opening multiple requests from multiple
    // tabs
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(responseForm.getFeedbackUserId());
    Assert.notNull(feedbackUser, "User not found.");
    
    Token token = tokenFactory.findTokenById(feedbackUser.getTokenId());
    // validate the token
    Assert.isTrue(token.isValid(), "Request has already been submitted.");
    
    // get the blueprint
    User member = userFactory.getUser(feedbackUser.getFeedbackFor());
    Blueprint blueprint = goalsFactory.getValidBlueprint(member);
    
    // validate if the blueprint in session is the same as the one in the response
    Assert.isTrue(blueprint.getId().equalsIgnoreCase(responseForm.getId()),
        "Blueprint id mismatch.");
    
    // params for notification
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    final String comment = responseForm.getComment();
    paramsMap.put(Constants.PARAM_COMMENT, comment);
    
    // check if it is an approval
    boolean isApprovalRequest = (boolean) token
        .getParam(Constants.PARAM_BLUEPRINT_APPROVAL_REQUEST);
    if (responseForm.isApproved()) {
      Assert.isTrue(isApprovalRequest, "Cannot approve a non approval request.");
      
      // processing the approval request
      blueprint.approve();
      
      // send the notification
      notificationProcessor.process(NotificationType.BlueprintApproved, feedbackUser, member,
          paramsMap, false);
      
      // invalidating the token
      token.invalidate("Blueprint approved.", Constants.VIEW_BLUEPRINT_ALREADY_PROCESSED);
      
      userFactory.removeBlueprintRequests(user.getCompanyId(), feedbackUser.getFeedbackFor());
      
      if (feedbackUser.getType() == UserType.Member) {
        LogRequest logRequest = new LogRequest(LogActionType.BlueprintApproved, feedbackUser,
            member);
        logGateway.logActivity(logRequest);
      }
    } else {
      // update the comments from the user if any
      responseForm.updateResponse(blueprint, feedbackUser);
      if (isApprovalRequest) {
        // update the blueprint status back to edit
        blueprint.setStatus(GoalStatus.EDIT);
        // sending the notification for approval revision
        notificationProcessor.process(NotificationType.BlueprintApprovalRevise, feedbackUser,
            member, paramsMap, false);
        if (feedbackUser.getType() == UserType.Member) {
          LogRequest logRequest = new LogRequest(LogActionType.BlueprintApprovalRevise,
              feedbackUser, member);
          logGateway.logActivity(logRequest);
        }
      } else {
        // sending the notification
        notificationProcessor.process(NotificationType.BlueprintFeedback, feedbackUser, member,
            paramsMap, false);
      }
      // invalidating the token
      token.invalidate("Blueprint feedback provided.", Constants.VIEW_BLUEPRINT_ALREADY_PROCESSED);

      todoFactory.remove(feedbackUser);
    }
    
    blueprint.addToFeedbackList(feedbackUser, comment);
    
    // update the token
    tokenFactory.persistToken(token);
    
    // updating the blueprint
    goalsFactory.updateBlueprint(blueprint);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to delete the user comments.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params for delete
   * @return the response to the delete request
   */
  public SPResponse deleteComment(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String uid = (String) params[0];
    String by = (String) params[1];
    
    // validate the parameters
    Assert.isTrue(!StringUtils.isBlank(uid), "UID is required.");
    Assert.isTrue(!StringUtils.isBlank(by), "By is required.");
    
    // get the blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // removing the comment
    return resp.add(Constants.PARAM_COMMENT_REMOVED, blueprint.removeComment(uid, by));
  }
  
  /**
   * Helper method to send the reminder to the blueprint approver.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the send reminder request
   */
  public SPResponse sendReminder(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String comment = (String) params[0];
    
    // get the blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // check if the blueprint is under approval
    Assert.isTrue((blueprint.getStatus() == GoalStatus.UNDER_APPROVAL),
        MessagesHelper.getMessage("service.growl.message7"));
    
    // get the feedback user
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(blueprint.getApprover().getId());
    Assert.notNull(feedbackUser, "Feedback user not found.");
    
    // send the notification
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_COMMENT, comment);
    paramsMap.putAll(MessagesHelper.getGenderText(user));
    notificationProcessor.process(NotificationType.BlueprintApprovalReminder, user, feedbackUser,
        paramsMap, false);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to cancel the approval request.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the cancel request
   */
  public SPResponse cancelApprovalRequest(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String comment = (String) params[0];
    
    // get the blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // check if the blueprint is under approval
    Assert.isTrue(blueprint.getStatus() == GoalStatus.UNDER_APPROVAL,
        MessagesHelper.getMessage("service.growl.message7"));
    
    cancelApprovalUser(user, comment, blueprint, true);
    
    return resp.isSuccess();
  }
  
  /**
   * Delete the approval user and send notification.
   * 
   * @param user
   *          - logged in user
   * @param comment
   *          - comment
   * @param blueprint
   *          - blueprint
   * @param doSendNotification
   *          - send notification
   */
  private void cancelApprovalUser(User user, final String comment, Blueprint blueprint,
      boolean doSendNotification) {
    // get the feedback user
    FeedbackUser feedbackUser = userFactory.getFeedbackUser(blueprint.getApprover().getId());
    Assert.notNull(feedbackUser, "Feedback user not found.");
    
    // get the token for the feedback user
    Token token = tokenFactory.findTokenById(feedbackUser.getTokenId());
    Assert.notNull(token, "Token not found to invalidate.");
    token.invalidate("Approval request cancelled.", Constants.VIEW_BLUEPRINT_REQUEST_CANCELLED);
    tokenFactory.persistToken(token);
    
    // removing the feedback user
    userFactory.removeUser(feedbackUser);
    
    // updating the blueprint
    blueprint.setStatus(GoalStatus.EDIT);
    blueprint.setApprover(null);
    goalsFactory.updateBlueprint(blueprint);
    
    todoFactory.remove(feedbackUser, feedbackUser.getId());
    
    if (doSendNotification) {
      // send a notification to the approver
      Map<String, Object> paramsMap = new HashMap<String, Object>();
      paramsMap.put(Constants.PARAM_COMMENT, comment);
      paramsMap.putAll(MessagesHelper.getGenderText(user));
      notificationProcessor.process(NotificationType.BlueprintApprovalCancel, user, feedbackUser,
          paramsMap, false);
    }
  }
  
  /**
   * Helper method to cancel the approval for the blueprint.
   * 
   * @param user
   *          - logged in user
   * @return the response to the approval request
   */
  public SPResponse cancelApproval(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // check if the blueprint is under approval
    Assert.isTrue(blueprint.getStatus() == GoalStatus.APPROVED, "Blueprint not approved.");
    
    cancelApprovalUser(user, null, blueprint, false);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to delete the feedback received messages.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the delete request
   */
  public SPResponse deleteFeedbackReceived(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String uid = (String) params[0];
    Assert.hasText(uid, "UID is required.");
    
    // get the blueprint
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // remove the feedback received and update the blueprint
    final boolean isRemoved = blueprint.removeFeedbackReceived(uid);
    if (isRemoved) {
      goalsFactory.updateBlueprint(blueprint);
    }
    
    return resp.add(Constants.PARAM_BLUEPRINT_UPDATE, isRemoved);
  }
  
  /**
   * Helper method to mark the feedback messages as viewed.
   * 
   * @param user
   *          - logged in user
   * @return the response to the viewed feedback messages
   */
  public SPResponse viewedFeedbackMessages(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the blueprint and update the flag
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    blueprint.setNewFeedbackReceivedCount(0);
    goalsFactory.updateBlueprint(blueprint);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to publish the blueprint.
   * 
   * @param user
   *          - logged in user
   * @return - the response for the publish request
   */
  public SPResponse publish(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the blueprint and update the flag
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // validate if the blueprint is in approved state
    Assert.isTrue(blueprint.getStatus() == GoalStatus.APPROVED, "Blueprint not approved.");
    
    // update the blueprint status
    blueprint.publish();
    goalsFactory.updateBlueprint(blueprint);
    
    // also remove any tokens and feedback users
    userFactory.removeAllFeedbackUsers(user, blueprint, "Blueprint no longer availalbe for share.");
    
    // remove any blueprint backups
    blueprintFactory.removeBlueprintBackupForBlueprintId(blueprint.getId());
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to mark the success measure complete.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the complete request
   */
  public SPResponse completeSuccessMeasure(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    final String uid = (String) params[0];
    final boolean isComplete = (boolean) params[1];
    
    // validate values
    Assert.hasText(uid, "UID is required.");
    
    // get the blueprint and update the flag
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // validate if the blueprint is in approved state
    Assert.isTrue(blueprint.getStatus() == GoalStatus.PUBLISHED, "Blueprint not published.");
    
    // validate UID
    Assert.isTrue(blueprint.validateUID(uid), "UID not found in blueprint.");
    
    // add or remove from the completed actions
    if (isComplete) {
      blueprint.addCompletedAction(uid);
    } else {
      blueprint.removeCompletedAction(uid);
    }
    goalsFactory.updateBlueprint(blueprint);
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * The helper method to cancel a published blueprint for edit.
   * 
   * @param user
   *          - logged in user
   * @return the response to the cancel request
   */
  public SPResponse cancelPublish(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the blueprint and update the flag
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // validate if the blueprint is in approved state
    Assert.isTrue(blueprint.getStatus() == GoalStatus.PUBLISHED, "Blueprint not published.");
    
    // take a backup of the existing blueprint
    BlueprintBackup blueprintBackup = new BlueprintBackup(blueprint);
    blueprintFactory.updateBlueprintBackup(blueprintBackup);
    
    // changing the status, approver and saving the blueprint
    blueprint.setPrevApprover(blueprint.getApprover());
    blueprint.setApprover(null);
    blueprint.setStatus(GoalStatus.EDIT);
    goalsFactory.updateBlueprint(blueprint);
    
    // sending the blueprint in edit mode
    return resp.add(Constants.PARAM_BLUEPRINT, addBlueprintDTOWithValidation(user, blueprint));
  }
  
  /**
   * Helper method to cancel the blueprint edit and revert back to the previous blueprint from
   * backup.
   * 
   * @param user
   *          - logged in user
   * @return the response to the cancel request
   */
  public SPResponse cancelEdit(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the blueprint and update the flag
    Blueprint blueprint = goalsFactory.getValidBlueprint(user);
    
    // validate if the blueprint is in approved state
    Assert.isTrue(blueprint.getStatus() != GoalStatus.PUBLISHED, "Blueprint already published.");
    
    // get the blueprint backup
    BlueprintBackup blueprintBackup = blueprintFactory.getBlueprintBackupFromBlueprintId(blueprint
        .getId());
    Assert.notNull(blueprintBackup, "Blueprint backup not found.");
    
    blueprintBackup.updateBlueprint(blueprint);
    blueprintFactory.removeBlueprintBackup(blueprintBackup);
    
    goalsFactory.updateBlueprint(blueprint);
    
    // sending the blueprint in edit mode
    return resp.add(Constants.PARAM_BLUEPRINT, addBlueprintDTOWithValidation(user, blueprint));
  }
  
  /**
   * Get the feedback user for the share form.
   * 
   * @param user
   *          - user
   * @param shareForm
   *          - share form
   * @return the feedback user
   */
  private List<FeedbackUser> getFeedbackUser(User user, BlueprintShareForm shareForm) {
    final List<FeedbackUser> feedbackUserList = new ArrayList<FeedbackUser>();
    
    // add the feedback users from members
    final List<String> userIdList = shareForm.getUserIdList();
    if (!CollectionUtils.isEmpty(userIdList)) {
      // adding all the user
      userIdList.forEach(userId -> addFeedbackUser(user, feedbackUserList, userId));
    }
    
    // add any external feedback user
    final List<ExternalUserForm> externalUserList = shareForm.getExternalUserList();
    if (!CollectionUtils.isEmpty(externalUserList)) {
      externalUserList.forEach(externalUser -> feedbackUserList.add(externalUser.getFeedbackUser(
          user, FeatureType.Blueprint)));
    }
    
    // writing it to the database
    feedbackUserList.forEach(userFactory::updateUser);
    return feedbackUserList;
  }

  private void addFeedbackUser(User user, final List<FeedbackUser> feedbackUserList, String userId) {
    Assert.isTrue(!user.getId().equals(userId), "Cannot send request to yourself.");
    User member = userFactory.getUser(userId);
    Assert.notNull(member, "User not found.");
    FeedbackUser fbUser = new FeedbackUser();
    fbUser.setFeedbackFor(user.getId());
    fbUser.setFeatureType(FeatureType.Blueprint);
    fbUser.updateFrom(member);
    feedbackUserList.add(fbUser);
  }
}
