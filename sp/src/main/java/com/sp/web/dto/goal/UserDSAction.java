package com.sp.web.dto.goal;

import com.sp.web.dto.CommentsDTO;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;
import com.sp.web.model.goal.DSActionType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The development strategy action class with user preferences.
 */
public class UserDSAction {
  
  private String uid;
  private String title;
  private String description;
  private DSActionDataType mediaType;
  private String mediaLinkText;
  private String mediaUrl;
  private String mediaImageLink;
  private String mediaAltText;
  private long timeInMins;
  private DSActionType type;
  private List<UserDSActionData> actionData;
  private Map<DSActionConfig, Boolean> permissions;
  private boolean completed;
  private int totalActions;
  private int completedActions;
  private String imageLink;
  private String altText;
  private String url;
  private DSActionDataType actionDataType;
  private List<CommentsDTO> commentsList;
  
  /**
   * Constructor.
   * 
   * @param action
   *          - action
   * @param hasCompletedAction
   *          - function to validate if the action has been completed
   */
  public UserDSAction(DSAction action, Function<String, Boolean> hasCompletedAction) {
    this(action, hasCompletedAction, null);
  }
  
  /**
   * Constructor.
   * 
   * @param action
   *          - action
   * @param hasCompletedAction
   *          - function to validate if the action has been completed
   * @param commentsDTOMap
   *          - comments map
   */
  public UserDSAction(DSAction action, Function<String, Boolean> hasCompletedAction,
      Map<String, List<CommentsDTO>> commentsDTOMap) {
    uid = null;
    type = null;
    BeanUtils.copyProperties(action, this);
    
    final List<DSActionData> targetActionData = action.getActionData();
    if (!CollectionUtils.isEmpty(targetActionData)) {
      actionData = targetActionData.stream().map(ad -> {
          UserDSActionData actionData = new UserDSActionData(ad, hasCompletedAction);
          updateCounts(actionData);
          return actionData;
        }).collect(Collectors.toList());
    }
    
    if (action.getPermission(DSActionConfig.Completion)) {
      totalActions++;
      if (hasCompletedAction.apply(uid)) {
        completed = true;
        completedActions++;
      }
    }
    
    if (type == DSActionType.Single && !CollectionUtils.isEmpty(actionData)) {
      updateActionIconImage();
    }
    
    if (commentsDTOMap != null) {
      commentsList = commentsDTOMap.remove(getUid());
    }
  }
  
  /**
   * Method to check if the first action has an icon image then update the icon image for the given
   * action.
   */
  private void updateActionIconImage() {
    UserDSActionData userDSActionData = actionData.get(0);
    final String imageLink = userDSActionData.getImageLink();
    if (!StringUtils.isEmpty(imageLink)) {
      this.imageLink = imageLink;
      this.altText = userDSActionData.getAltText();
      this.url = userDSActionData.getUrl();
      this.actionDataType = userDSActionData.getType();
    }
  }
  
  /**
   * Method to aggregate the total actions and the completed actions.
   * 
   * @param actionData
   *          - action data
   */
  private void updateCounts(UserDSActionData actionData) {
    totalActions += actionData.getTotalActions();
    completedActions += actionData.getCompletedActions();
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
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public long getTimeInMins() {
    return timeInMins;
  }
  
  public void setTimeInMins(long timeInMins) {
    this.timeInMins = timeInMins;
  }
  
  public DSActionType getType() {
    return type;
  }
  
  public void setType(DSActionType type) {
    this.type = type;
  }
  
  public List<UserDSActionData> getActionData() {
    return actionData;
  }
  
  public void setActionData(List<UserDSActionData> actionData) {
    this.actionData = actionData;
  }
  
  public Map<DSActionConfig, Boolean> getPermissions() {
    return permissions;
  }
  
  public void setPermissions(Map<DSActionConfig, Boolean> permissions) {
    this.permissions = permissions;
  }
  
  public boolean isCompleted() {
    return completed;
  }
  
  public void setCompleted(boolean completed) {
    this.completed = completed;
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
  
  public String getImageLink() {
    return imageLink;
  }
  
  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }
  
  public String getAltText() {
    return altText;
  }
  
  public void setAltText(String altText) {
    this.altText = altText;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public DSActionDataType getActionDataType() {
    return actionDataType;
  }
  
  public void setActionDataType(DSActionDataType actionDataType) {
    this.actionDataType = actionDataType;
  }
  
  public List<CommentsDTO> getCommentsList() {
    return commentsList;
  }
  
  public void setCommentsList(List<CommentsDTO> commentsList) {
    this.commentsList = commentsList;
  }

  /**
   * Filter the comments for the given email.
   * 
   * @param email
   *          - email to retain comments for
   */
  public void filterComments(String email) {
    if (commentsList != null) {
      commentsList.removeIf(c -> !c.getBy().getEmail().equals(email));
    }
  }

  public DSActionDataType getMediaType() {
    return mediaType;
  }

  public void setMediaType(DSActionDataType mediaType) {
    this.mediaType = mediaType;
  }

  public String getMediaLinkText() {
    return mediaLinkText;
  }

  public void setMediaLinkText(String mediaLinkText) {
    this.mediaLinkText = mediaLinkText;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  public String getMediaImageLink() {
    return mediaImageLink;
  }

  public void setMediaImageLink(String mediaImageLink) {
    this.mediaImageLink = mediaImageLink;
  }

  public String getMediaAltText() {
    return mediaAltText;
  }

  public void setMediaAltText(String mediaAltText) {
    this.mediaAltText = mediaAltText;
  }
}
