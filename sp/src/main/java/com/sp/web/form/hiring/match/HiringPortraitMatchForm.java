package com.sp.web.form.hiring.match;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The for form for the hiring portrait match request.
 */
public class HiringPortraitMatchForm {
  
  private String id;
  private List<String> userIds;
  private List<String> groupIds;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public List<String> getUserIds() {
    return userIds;
  }
  
  public void setUserIds(List<String> userIds) {
    this.userIds = userIds;
  }
  
  public List<String> getGroupIds() {
    return groupIds;
  }
  
  public void setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
  }
  
  /**
   * Method to validate the form data.
   */
  public void validate() {
    Assert.hasText(id, "Id required.");
    Assert.isTrue(!(CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(groupIds)),
        "Atleast one user or group required.");
  }
  
}
