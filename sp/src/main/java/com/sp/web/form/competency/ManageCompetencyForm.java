package com.sp.web.form.competency;

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
public class ManageCompetencyForm {
  
  private List<String> competencyProfileId;
  private List<String> userIdList;
  private List<GroupPermissions> groupPermissionsList;
  
  /**
   * @param competencyProfileId the competencyProfileId to set
   */
  public void setCompetencyProfileId(List<String> competencyProfileId) {
    this.competencyProfileId = competencyProfileId;
  }
  
  /**
   * @return the competencyProfileId
   */
  public List<String> getCompetencyProfileId() {
    return competencyProfileId;
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
    Assert.notEmpty(competencyProfileId, "Competency profile not found.");
    if (CollectionUtils.isEmpty(userIdList)) {
      Assert.notEmpty(groupPermissionsList,
          "User or group list not found in request.");
    }
    
    
  }
  
}
