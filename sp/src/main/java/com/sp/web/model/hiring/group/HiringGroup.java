package com.sp.web.model.hiring.group;

import com.sp.web.model.HiringUser;

import java.util.Set;

/**
 * 
 * @author Dax Abraham
 * 
 *         This is the entity class for the people analytics groups.
 */
public class HiringGroup {
  
  private String id;
  private String name;
  private String companyId;
  private Set<String> userIds;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public Set<String> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<String> userIds) {
    this.userIds = userIds;
  }
  
  public HiringUser add(HiringUser user) {
    userIds.add(user.getId());
    return user;
  }
  
  public boolean remove(String userId) {
    return userIds.remove(userId);
  }
}
