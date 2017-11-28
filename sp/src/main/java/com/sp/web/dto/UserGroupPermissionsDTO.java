package com.sp.web.dto;

import com.sp.web.model.UserGroup;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 *
 *         The DTO to share basic user group details.
 */
public class UserGroupPermissionsDTO implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 8685218467942885919L;
  private String id;
  private String name;
  private boolean excludeGroupLead;

  /**
   * Constructor.
   * 
   * @param userGroup
   *            - user group
   */
  public UserGroupPermissionsDTO(UserGroup userGroup) {
    BeanUtils.copyProperties(userGroup, this);
  }
  
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

  public boolean isExcludeGroupLead() {
    return excludeGroupLead;
  }

  public void setExcludeGroupLead(boolean excludeGroupLead) {
    this.excludeGroupLead = excludeGroupLead;
  }
  
}
