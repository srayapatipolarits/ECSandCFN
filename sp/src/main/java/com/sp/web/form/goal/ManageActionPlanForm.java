package com.sp.web.form.goal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.model.goal.GroupPermissions;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The form class to capture the parameters for assignment of action plans.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManageActionPlanForm {
  
  private List<String> actionPlanIdList;
  private List<String> userIdList;
  private List<GroupPermissions> groupPermissionsList;
  
  public List<String> getActionPlanIdList() {
    return actionPlanIdList;
  }
  
  public void setActionPlanIdList(List<String> actionPlanIdList) {
    this.actionPlanIdList = actionPlanIdList;
  }
  
  public List<String> getUserIdList() {
    return userIdList;
  }
  
  public void setUserIdList(List<String> userIdList) {
    this.userIdList = userIdList;
  }
  
  public List<GroupPermissions> getGroupPermissionsList() {
    return groupPermissionsList;
  }
  
  public void setGroupPermissionsList(List<GroupPermissions> groupPermissionsList) {
    this.groupPermissionsList = groupPermissionsList;
  }

  /**
   * Method to validate the contents of the form.
   */
  public void validate() {
    if (CollectionUtils.isEmpty(userIdList)) {
      Assert.notEmpty(groupPermissionsList,
          "User or group list not found in request.");
    }
    
    Assert.notEmpty(actionPlanIdList, "Organization plan not found in request.");
  }
  
}
