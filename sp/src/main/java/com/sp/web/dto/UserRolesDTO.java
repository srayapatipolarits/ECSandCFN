package com.sp.web.dto;

import com.sp.web.model.RoleType;
import com.sp.web.model.User;

import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The User DTO class with roles.
 * 
 */
public class UserRolesDTO extends BaseUserDTO {
  
  private static final long serialVersionUID = -4555963971975254400L;
  private Set<RoleType> roles;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public UserRolesDTO(User user) {
    super(user);
  }

  public Set<RoleType> getRoles() {
    return roles;
  }
  
  public void setRoles(Set<RoleType> roles) {
    this.roles = roles;
  }
  
}
