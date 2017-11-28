package com.sp.web.model.goal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.todo.ActionPlanStepActionTodoDTO;

import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Dax Abraham
 *
 *         The entity class to store the ds action category and the action data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DSActionCategory implements Serializable {
  
  private static final long serialVersionUID = 4203594895899568629L;
  private String uid;
  private String title;
  private String helpText;
  private GoalStatus status;
  private List<DSAction> actionList;
  
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
  
  public List<DSAction> getActionList() {
    return actionList;
  }
  
  public void setActionList(List<DSAction> actionList) {
    this.actionList = actionList;
  }
  
  public GoalStatus getStatus() {
    return status;
  }
  
  public void setStatus(GoalStatus status) {
    this.status = status;
  }
  
  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  /**
   * This method sets a unique id for the given category.
   * 
   * @param uidGenerator
   *          - the UID Generator
   * 
   */
  public void addUID(Supplier<String> uidGenerator) {
    if (uid == null) {
      uid = uidGenerator.get();
    }
    
    if (!CollectionUtils.isEmpty(actionList)) {
      actionList.forEach(action -> action.addUID(uidGenerator));
    }
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
    return actionList.stream().filter(a -> a.validateUID(uid)).findFirst().isPresent();
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
    
    if (!(obj instanceof DSActionCategory)) {
      return false;
    }
    
    DSActionCategory other = (DSActionCategory) obj;
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
   * Delete any actions if the UID is found in the delete list.
   * 
   * @param deleteList
   *          - UID list for deletion
   */
  public void delete(List<String> deleteList) {
    if (!CollectionUtils.isEmpty(actionList)) {
      Iterator<DSAction> iter = actionList.iterator();
      while (iter.hasNext()) {
        DSAction action = iter.next();
        if (deleteList.remove(action.getUid())) {
          iter.remove();
        } else {
          action.delete(deleteList);
        }
      }
      // clean up
      if (actionList.isEmpty()) {
        actionList = null;
      }
    }
  }
  
  /**
   * Get the total count of actions in the category.
   * 
   * @return the count of actions
   */
  public int getActionCount() {
    if (status == GoalStatus.ACTIVE) {
      if (!CollectionUtils.isEmpty(actionList)) {
        return actionList.stream().mapToInt(DSAction::getActionCount).sum();
      }
    }
    return 0;
  }
  
  /**
   * Add UID to the list of action id's if the current completion.
   * 
   * @param actionIdList
   *          - action Id list
   */
  public void addActionUids(List<String> actionIdList) {
    if (status == GoalStatus.ACTIVE) {
      if (!CollectionUtils.isEmpty(actionList)) {
        actionList.stream().forEach(dsAc -> dsAc.addActionUids(actionIdList));
      }
    }
  }
  
  /**
   * Find all the deactived uid's.
   * 
   * @param uidList
   *          - uid list
   */
  public void updateDeactivatedUids(List<String> uidList) {
    if (status == GoalStatus.ACTIVE) {
      if (!CollectionUtils.isEmpty(actionList)) {
        actionList.stream().forEach(dsAc -> dsAc.updateDeactivatedUids(uidList));
      }
    } else {
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
    if (!CollectionUtils.isEmpty(actionList)) {
      actionList.stream().forEach(dsAc -> dsAc.updateActionUids(uidList));
    }
  }
  
  /**
   * Add the todo if present in remaining actions.
   * 
   * @param remainingActions
   *          - remaining actions
   * @param actions
   *          - actions to add to
   */
  public void addTodo(Set<String> remainingActions, List<ActionPlanStepActionTodoDTO> actions) {
    if (status == GoalStatus.ACTIVE) {
      if (!CollectionUtils.isEmpty(actionList)) {
        actionList.forEach(a -> a.addTodo(remainingActions, actions));
      }
    }
  }
}
