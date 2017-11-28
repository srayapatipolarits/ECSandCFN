package com.sp.web.dto.goal;

import com.sp.web.model.goal.DSActionConfig;
import com.sp.web.model.goal.DSActionData;
import com.sp.web.model.goal.DSActionDataType;

import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Dax Abraham
 *
 *         The DTO class for action data.
 */
public class UserDSActionData {
  private String uid;
  private String title;
  private String description;
  private DSActionDataType type;
  private String linkText;
  private String url;
  private String imageLink;
  private String altText;
  private Map<DSActionConfig, Boolean> permissions;
  private boolean completed;
  private int totalActions = 0;
  private int completedActions = 0;
  
  /**
   * Constructor.
   * 
   * @param actionData
   *          - action data
   * @param hasCompletedAction
   *          - function to validate if the action has been completed
   */
  public UserDSActionData(DSActionData actionData, Function<String, Boolean> hasCompletedAction) {
    uid = null;
    BeanUtils.copyProperties(actionData, this);
    
    if (actionData.getPermission(DSActionConfig.Completion)) {
      totalActions++;
      if (hasCompletedAction.apply(uid)) {
        completed = true;
        completedActions++;
      }
    }
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
  
  public DSActionDataType getType() {
    return type;
  }
  
  public void setType(DSActionDataType type) {
    this.type = type;
  }
  
  public String getLinkText() {
    return linkText;
  }
  
  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }
  
  public String getUrl() {
    return url;
  }
  
  public void setUrl(String url) {
    this.url = url;
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
  
}
