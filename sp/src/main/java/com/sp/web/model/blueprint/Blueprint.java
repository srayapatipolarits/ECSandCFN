package com.sp.web.model.blueprint;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Comments;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author vikram
 *
 *         The Blueprint configured by user.
 */
@Document(collection = "sPGoal")
public class Blueprint extends SPGoal {
  
  private static final long serialVersionUID = 1994885461174985875L;
  
  private LocalDateTime createdOn;
  private LocalDateTime publishedOn;
  private BlueprintMissionStatement missionStatement;
  private Map<String, List<Comments>> commentsMap;
  private HashSet<String> completedActions;
  private int uidCount;
  private int newFeedbackReceivedCount;
  private List<BlueprintFeedback> feedbackReceivedList;
  private BlueprintApprover approver;
  private BlueprintApprover prevApprover;
  private int totalActionCount;
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public BlueprintMissionStatement getMissionStatement() {
    return missionStatement;
  }
  
  public void setMissionStatement(BlueprintMissionStatement missionStatement) {
    this.missionStatement = missionStatement;
  }
  
  public HashSet<String> getCompletedActions() {
    return completedActions;
  }
  
  public void setCompletedActions(HashSet<String> completedActions) {
    this.completedActions = completedActions;
  }
  
  /**
   * Validate if the given uid is in the completed actions list.
   * 
   * @param uid
   *          - unique id
   * @return true if completed else false
   */
  public boolean hasCompleted(String uid) {
    return (completedActions != null) ? completedActions.contains(uid) : false;
  }
  
  /**
   * Gets the next UID.
   * 
   * @return the next UID
   */
  public String getNextUID() {
    if (id == null) {
      // action plan must be saved previously to get a ID
      throw new InvalidRequestException("Action plan not initialized.");
    }
    return id + uidCount++;
  }
  
  public Map<String, List<Comments>> getCommentsMap() {
    return commentsMap;
  }
  
  public void setCommentsMap(Map<String, List<Comments>> commentsMap) {
    this.commentsMap = commentsMap;
  }
    
  public List<BlueprintFeedback> getFeedbackReceivedList() {
    return feedbackReceivedList;
  }
  
  public void setFeedbackReceivedList(List<BlueprintFeedback> feedbackReceivedList) {
    this.feedbackReceivedList = feedbackReceivedList;
  }
  
  public BlueprintApprover getApprover() {
    return approver;
  }
  
  public void setApprover(BlueprintApprover approver) {
    this.approver = approver;
  }
  
  public BlueprintApprover getPrevApprover() {
    return prevApprover;
  }

  public void setPrevApprover(BlueprintApprover prevApprover) {
    this.prevApprover = prevApprover;
  }

  public LocalDateTime getPublishedOn() {
    return publishedOn;
  }

  public void setPublishedOn(LocalDateTime publishedOn) {
    this.publishedOn = publishedOn;
  }
  
  /**
   * Adds the user to the feedback list.
   * 
   * @param feedbackUser
   *          - feedback user
   * @param comment 
   *          - comment from the user
   */
  public void addToFeedbackList(User feedbackUser, String comment) {
    if (feedbackReceivedList == null) {
      feedbackReceivedList = new ArrayList<BlueprintFeedback>();
    }
    feedbackReceivedList.add(new BlueprintFeedback(feedbackUser, this::getNextUID, comment));
    newFeedbackReceivedCount++;
  }
  
  /**
   * Remove the comment with the given UID and by.
   * 
   * @param uid
   *          - UID
   * @param by
   *          - by user id
   * @return true if the comment was removed
   */
  public boolean removeComment(String uid, String by) {
    if (!CollectionUtils.isEmpty(commentsMap)) {
      List<Comments> commentList = commentsMap.get(uid);
      if (!CollectionUtils.isEmpty(commentList)) {
        boolean isRemoved = commentList.removeIf(c -> c.getBy().equals(by));
        if (commentList.isEmpty()) {
          commentsMap.remove(uid);
        }
        return isRemoved;
      }
    }
    
    return false;
  }
  
  /**
   * Remove the feedback received message.
   * 
   * @param uid
   *          - unique id
   * @return true if removed else false
   */
  public boolean removeFeedbackReceived(String uid) {
    if (!CollectionUtils.isEmpty(feedbackReceivedList)) {
      Optional<BlueprintFeedback> findFirst = feedbackReceivedList.stream()
          .filter(fr -> fr.getUid().equals(uid)).findFirst();
      if (findFirst.isPresent()) {
        return feedbackReceivedList.remove(findFirst.get());
      }
    }
    return false;
  }
  
  /**
   * Publish the blueprint.
   */
  public void publish() {
    // update the status
    setStatus(GoalStatus.PUBLISHED);
    // update the approver published on
    Assert.notNull(approver, "Approver not found.");
    publishedOn = LocalDateTime.now();
    
    // resetting previous approver
    if (prevApprover != null) {
      prevApprover = null;
    }
    // remove all the comments and feedback message list
    if (!CollectionUtils.isEmpty(commentsMap)) {
      commentsMap.clear();
      commentsMap = null;
    }
    if (!CollectionUtils.isEmpty(feedbackReceivedList)) {
      feedbackReceivedList.clear();
      feedbackReceivedList = null;
    }
    // calculate the total number of actions
    totalActionCount = getDevStrategyActionCategoryList().stream()
        .mapToInt(DSActionCategory::getActionCount).sum();
    newFeedbackReceivedCount++;
  }
  
  /**
   * Helper method to add/set the approver.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  public void addApprover(FeedbackUser feedbackUser) {
    this.approver = new BlueprintApprover(feedbackUser);
  }

  /**
   * Helper method to approve the blueprint.
   */
  public void approve() {
    setStatus(GoalStatus.APPROVED);
    approver.setApprovedOn(LocalDateTime.now());
  }

  /**
   * Helper method to add the completed action.
   * 
   * @param uid
   *          - UID
   */
  public void addCompletedAction(String uid) {
    if (CollectionUtils.isEmpty(completedActions)) {
      completedActions = new HashSet<String>();
    }
    completedActions.add(uid);
  }

  /**
   * Helper method to remove from completed actions.
   * 
   * @param uid
   *          - UID
   */
  public void removeCompletedAction(String uid) {
    if (!CollectionUtils.isEmpty(completedActions)) {
      completedActions.remove(uid);
      if (completedActions.isEmpty()) {
        completedActions = null;
      }
    }
  }

  public int getNewFeedbackReceivedCount() {
    return newFeedbackReceivedCount;
  }

  public void setNewFeedbackReceivedCount(int newFeedbackReceivedCount) {
    this.newFeedbackReceivedCount = newFeedbackReceivedCount;
  }

  public int getTotalActionCount() {
    return totalActionCount;
  }

  public void setTotalActionCount(int totalActionCount) {
    this.totalActionCount = totalActionCount;
  }
  
}
