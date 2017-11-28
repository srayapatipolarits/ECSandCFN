package com.sp.web.model.goal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.todo.ActionPlanStepActionTodoDTO;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 *
 *         The entity to store the action for the development strategy.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DSAction implements Serializable {
  
  private static final long serialVersionUID = 1791071693223667599L;
  
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
  private boolean active;
  private List<DSActionData> actionData;
  private Map<DSActionConfig, Boolean> permissions;
  
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
  
  public List<DSActionData> getActionData() {
    return actionData;
  }
  
  public void setActionData(List<DSActionData> actionData) {
    this.actionData = actionData;
  }
  
  public Map<DSActionConfig, Boolean> getPermissions() {
    return permissions;
  }
  
  public void setPermissions(Map<DSActionConfig, Boolean> permissions) {
    this.permissions = permissions;
  }
  
  public long getTimeInMins() {
    return timeInMins;
  }
  
  public void setTimeInMins(long timeInMins) {
    this.timeInMins = timeInMins;
  }
  
  /**
   * Get the permission for the given configuration.
   * 
   * @param config
   *          - configuration to get permission for
   * 
   * @return the value of the configuration if found else false
   */
  public boolean getPermission(DSActionConfig config) {
    if (permissions != null) {
      return permissions.getOrDefault(config, Boolean.FALSE).booleanValue();
    }
    return false;
  }
  
  public DSActionType getType() {
    return type;
  }
  
  public void setType(DSActionType type) {
    this.type = type;
  }
  
  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  /**
   * Sets the UID if not already set.
   * 
   * @param uidGenerator
   *          - the UID generator
   */
  public void addUID(Supplier<String> uidGenerator) {
    if (uid == null) {
      uid = uidGenerator.get();
    }
    
    if (!CollectionUtils.isEmpty(actionData)) {
      actionData.forEach(ad -> ad.addUID(uidGenerator));
    }
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  /**
   * Method to validate the UID.
   * 
   * @param uid
   *          - unique id
   * @return true if UID is present
   */
  public boolean validateUID(String uid) {
    if (uid != null) {
      if (uid.equalsIgnoreCase(this.uid)) {
        return true;
      }
    }
    return actionData.stream().filter(ad -> ad.validateUID(uid)).findFirst().isPresent();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((uid == null) ? 0 : uid.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof DSAction)) {
      return false;
    }
    DSAction other = (DSAction) obj;
    if (uid == null) {
      if (other.uid != null) {
        return false;
      }
    } else if (!uid.equals(other.uid)) {
      return false;
    }
    return true;
  }
  
  /**
   * Method to delete any action data if the UID is found in the delete list.
   * 
   * @param deleteList
   *          - UID to delete
   */
  public void delete(List<String> deleteList) {
    if (!CollectionUtils.isEmpty(actionData)) {
      Iterator<DSActionData> iter = actionData.iterator();
      while (iter.hasNext()) {
        DSActionData next = iter.next();
        if (deleteList.remove(next.getUid())) {
          iter.remove();
        }
      }
      // clean up
      if (actionData.isEmpty()) {
        actionData = null;
      }
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
  
  /**
   * Get the total count of actions in the category.
   * 
   * @return the count of actions
   */
  public int getActionCount() {
    int total = 0;
    if (active) {
      if (!CollectionUtils.isEmpty(actionData)) {
        total = actionData.stream().mapToInt(DSActionData::getActionCount).sum();
      }
      
      if (!CollectionUtils.isEmpty(permissions)) {
        total += getPermission(DSActionConfig.Completion) ? 1 : 0;
      }
    }
    return total;
  }
  
  /**
   * Add UID to the list of action id's if the current completion.
   * 
   * @param actionIdList
   *            - action Id list
   */
  public void addActionUids(List<String> actionIdList) {
    if (active) {
      if (!CollectionUtils.isEmpty(actionData)) {
        actionData.forEach(actData -> actData.addActionUids(actionIdList));
      }
      
      if (!CollectionUtils.isEmpty(permissions)) {
        if (getPermission(DSActionConfig.Completion)) {
          actionIdList.add(uid);
        }
      }
    }
  }

  /**
   * Update all the deactivated UID's.
   * 
   * @param uidList
   *          - UID list
   */
  public void updateDeactivatedUids(List<String> uidList) {
    if (!active) {
      updateActionUids(uidList);
    }
  }
  
  /**
   * Get all the action UID's for the given category.
   * 
   * @param uidList
   *          - uid list
   */
  public void updateActionUids(List<String> uidList) {
    if (!CollectionUtils.isEmpty(actionData)) {
      actionData.stream().forEach(ad -> ad.addActionUids(uidList));
    }
    
    if (!CollectionUtils.isEmpty(permissions)) {
      if (getPermission(DSActionConfig.Completion)) {
        uidList.add(uid);
      }
    }
  }

  /**
   * Add the todo if present in remaining actions.
   * 
   * @param remainingActions
   *            - remaining actions
   * @param actions
   *            - actions to add to
   */
  public void addTodo(Set<String> remainingActions, List<ActionPlanStepActionTodoDTO> actions) {
    if (active) {
      if (remainingActions.remove(uid)) {
        actions.add(new ActionPlanStepActionTodoDTO(this));
      }
      
      if (!CollectionUtils.isEmpty(actionData)) {
        actionData.forEach(ad -> ad.addTodo(remainingActions, actions));
      }
    }
  }
  
}
