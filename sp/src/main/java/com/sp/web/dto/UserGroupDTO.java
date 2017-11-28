package com.sp.web.dto;

import com.sp.web.model.GroupAssociation;
import com.sp.web.model.UserGroup;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Dax Abraham
 *
 *         The DTO to share basic user group details.
 */
public class UserGroupDTO implements Serializable {

  private static final long serialVersionUID = 3008063514119436433L;
  private String id;
  private String name;

  /**
   * Constructor.
   * 
   * @param userGroup
   *            - user group
   */
  public UserGroupDTO(UserGroup userGroup) {
    BeanUtils.copyProperties(userGroup, this);
  }
  
  /**
   * Constructor from group association.
   * 
   * @param ga
   *        - group association
   */
  public UserGroupDTO(GroupAssociation ga) {
    this.id = ga.getGroupId();
    this.name = ga.getName();
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
  
}
