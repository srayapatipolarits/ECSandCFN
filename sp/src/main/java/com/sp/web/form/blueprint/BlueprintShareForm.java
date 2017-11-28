package com.sp.web.form.blueprint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.form.ExternalUserForm;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The form for the blueprint share request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintShareForm {
  
  private List<String> userIdList;
  private List<ExternalUserForm> externalUserList;
  private boolean approvalRequest;
  private String comment;
  private boolean showProgress;
  
  public boolean isApprovalRequest() {
    return approvalRequest;
  }
  
  public void setApprovalRequest(boolean approvalRequest) {
    this.approvalRequest = approvalRequest;
  }
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public List<String> getUserIdList() {
    return userIdList;
  }
  
  public void setUserIdList(List<String> userIdList) {
    this.userIdList = userIdList;
  }
  
  public List<ExternalUserForm> getExternalUserList() {
    return externalUserList;
  }
  
  public void setExternalUserList(List<ExternalUserForm> externalUserList) {
    this.externalUserList = externalUserList;
  }
  
  /**
   * Do full validation.
   */
  public void validate() {
    validate(false);
  }
  
  /**
   * Validate the form.
   * 
   * @param isBasicValidation
   *          - flag for basic validation
   */
  public void validate(boolean isBasicValidation) {
    int userCount = 0;
    if (!CollectionUtils.isEmpty(userIdList)) {
      userCount = userIdList.size();
      userIdList.forEach(userId -> Assert.hasText(userId, "User id is required."));
    }
    
    if (!CollectionUtils.isEmpty(externalUserList)) {
      userCount += externalUserList.size();
      externalUserList.forEach(user -> user.validate(isBasicValidation));
    }
    
    Assert.isTrue(userCount > 0, "Atleast one user is required.");
    if (approvalRequest) {
      Assert.isTrue(userCount == 1, "Cannot send approval to more than one user.");
    }
    
  }

  /**
   * Helper method to add the user to the user list.
   * 
   * @param userId
   *          - user id
   */
  public void addUser(String userId) {
    if (CollectionUtils.isEmpty(userIdList)) {
      userIdList = new ArrayList<String>();
    }
    userIdList.add(userId);
  }

  public boolean isShowProgress() {
    return showProgress;
  }

  public void setShowProgress(boolean showProgress) {
    this.showProgress = showProgress;
  }

  /**
   * Helper method to add the external user.
   * 
   * @param externalUserForm
   *          - external user form
   */
  public void addExternalUser(ExternalUserForm externalUserForm) {
    if (CollectionUtils.isEmpty(externalUserList)) {
      externalUserList = new ArrayList<ExternalUserForm>();
    }
    externalUserList.add(externalUserForm);
  }
  
}
