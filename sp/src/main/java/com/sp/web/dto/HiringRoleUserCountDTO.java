package com.sp.web.dto;

/**
 * The DTO class to hold the number of valid users per role.
 * 
 * @author pradeepruhil
 *
 */
public class HiringRoleUserCountDTO {
  
  String name;
  
  int totalMembers;
  
  int validMembers;
  
  /**
   * Default Constructor.
   */
  public HiringRoleUserCountDTO() { }
  
  /**
   * The constructor with the role name provided.
   * 
   * @param role
   *          - role name
   */
  public HiringRoleUserCountDTO(String role) {
    name = role;
  }

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getTotalMembers() {
    return totalMembers;
  }
  
  public void setTotalMembers(int totalMembers) {
    this.totalMembers = totalMembers;
  }
  
  public int getValidMembers() {
    return validMembers;
  }
  
  public void setValidMembers(int validMembers) {
    this.validMembers = validMembers;
  }

  /**
   * Increments the number of valid members count by 1.
   */
  public void incrementValidCount() {
    validMembers++;
  }
  
}
