package com.sp.web.model.goal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.todo.ActionPlanStepActionTodoDTO;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 *
 *         The entity to store the dev strategy action data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DSActionData implements Serializable {
  
  private static final long serialVersionUID = 4856716159591011514L;
  private String uid;
  private String title;
  private String description;
  private DSActionDataType type;
  private String linkText;
  private String url;
  private String imageLink;
  private String altText;
  private Map<DSActionConfig, Boolean> permissions;
  
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
  
  public Map<DSActionConfig, Boolean> getPermissions() {
    return permissions;
  }
  
  public void setPermissions(Map<DSActionConfig, Boolean> permissions) {
    this.permissions = permissions;
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

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
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

  /**
   * Sets the UID if not already set.
   * 
   * @param uidGenerator
   *            - the UID generator
   */
  public void addUID(Supplier<String> uidGenerator) {
    if (uid == null) {
      uid = uidGenerator.get();
    }
  }
  
  /**
   * Method to validate the UID.
   * 
   * @param uid
   *          - unique id
   * @return
   *    true if UID is present
   */
  public boolean validateUID(String uid) {
    if (uid != null && this.uid != null) {
      return uid.equalsIgnoreCase(this.uid);
    }
    return false;
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
    if (!(obj instanceof DSActionData)) {
      return false;
    }
    DSActionData other = (DSActionData) obj;
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
   * Get the total count of actions in the category.
   * 
   * @return the count of actions
   */
  public int getActionCount() {
    if (!CollectionUtils.isEmpty(permissions)) {
      return getPermission(DSActionConfig.Completion) ? 1 : 0;
    }
    return 0;
  }
  
  /**
   * Add UID to the list of action id's if the current completion.
   * 
   * @param actionIdList
   *            - action Id list
   */
  public void addActionUids(List<String> actionIdList) {
    if (!CollectionUtils.isEmpty(permissions)) {
      if (getPermission(DSActionConfig.Completion)) {
        actionIdList.add(uid);
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
    if (remainingActions.remove(uid)) {
      actions.add(new ActionPlanStepActionTodoDTO(this));
    }
  }
}
