package com.sp.web.dto.goal;

import com.sp.web.dto.CommentsDTO;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The development strategy action category.
 */
public class UserDSActionCategory {
  
  private String uid;
  private String title;
  private String helpText;
  private GoalStatus status;
  private List<UserDSAction> actionList;
  private int totalActions = 0;
  private int completedActions = 0;
  private List<CommentsDTO> commentsList;
  
  /**
   * Constructor.
   * 
   * @param actionCategory
   *          - action category
   * 
   * @param hasCompletedAction
   *          - function to validate if the action has been completed
   */
  public UserDSActionCategory(DSActionCategory actionCategory,
      Function<String, Boolean> hasCompletedAction) {
    this(actionCategory, hasCompletedAction, null);
  }
  
  /**
   * Constructor.
   * 
   * @param actionCategory
   *          - action category
   * @param hasCompletedAction
   *          - function validate if the action has been completed
   * @param commentsDTOMap
   *          - comments map
   */
  public UserDSActionCategory(DSActionCategory actionCategory,
      Function<String, Boolean> hasCompletedAction, Map<String, List<CommentsDTO>> commentsDTOMap) {
    BeanUtils.copyProperties(actionCategory, this);
    final List<DSAction> targetActionList = actionCategory.getActionList();
    if (!CollectionUtils.isEmpty(targetActionList)) {
      actionList = targetActionList.stream().filter(DSAction::isActive).map(act -> {
          UserDSAction udsAction = new UserDSAction(act, hasCompletedAction, commentsDTOMap);
          updateCounts(udsAction);
          return udsAction;
        }).collect(Collectors.toList());
    }
    if (commentsDTOMap != null) {
      commentsList = commentsDTOMap.remove(getUid());
    }
  }
  
  /**
   * Aggregate the total and completed actions count.
   * 
   * @param udsAction
   *          - action
   */
  private void updateCounts(UserDSAction udsAction) {
    totalActions += udsAction.getTotalActions();
    completedActions += udsAction.getCompletedActions();
  }
  
  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getHelpText() {
    return helpText;
  }
  
  public void setHelpText(String helpText) {
    this.helpText = helpText;
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public List<UserDSAction> getActionList() {
    return actionList;
  }
  
  public void setActionList(List<UserDSAction> actionList) {
    this.actionList = actionList;
  }
  
  public int getTotalActions() {
    return totalActions;
  }
  
  public void setTotalActions(int totalActions) {
    this.totalActions = totalActions;
  }
  
  public int getCompletedActions() {
    return completedActions;
  }
  
  public void setCompletedActions(int completedActions) {
    this.completedActions = completedActions;
  }
  
  public List<CommentsDTO> getCommentsList() {
    return commentsList;
  }
  
  public void setCommentsList(List<CommentsDTO> commentsList) {
    this.commentsList = commentsList;
  }

  /**
   * Filter the comments for the given user.
   * 
   * @param email
   *          - email to retain comments from
   */
  public void filterComments(String email) {
    if (commentsList != null) {
      commentsList.removeIf(c -> !c.getBy().getEmail().equals(email));
    }
    
    if (actionList != null) {
      actionList.forEach(a -> a.filterComments(email));
    }
  }
  
}
