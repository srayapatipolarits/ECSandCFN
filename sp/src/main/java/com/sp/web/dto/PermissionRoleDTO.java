package com.sp.web.dto;

import com.sp.web.model.RoleType;
import com.sp.web.model.SPRoleGroups;

import java.util.ArrayList;
import java.util.List;

/**
 * PermissionROleDTO contains the dto for response for the permissions.
 * @author pradeepruhil
 *
 */
public class PermissionRoleDTO {
  
  private SPRoleGroups roleGroups;
  
  private List<SysUserRoles> roles;
  
  public SPRoleGroups getRoleGroups() {
    return roleGroups;
  }
  
  public void setRoleGroups(SPRoleGroups roleGroups) {
    this.roleGroups = roleGroups;
  }
  
  public List<SysUserRoles> getRoles() {
    if(roles == null) {
      roles = new ArrayList<>();
    }
    return roles;
  }
  
  public void setRoles(List<SysUserRoles> roles) {
    this.roles = roles;
  }
  
  public static class SysUserRoles {
    private RoleType roleType;
    
    private boolean exist;

    public RoleType getRoleType() {
      return roleType;
    }

    public void setRoleType(RoleType roleType) {
      this.roleType = roleType;
    }

    public boolean isExist() {
      return exist;
    }

    public void setExist(boolean exist) {
      this.exist = exist;
    }
  }
  
}
