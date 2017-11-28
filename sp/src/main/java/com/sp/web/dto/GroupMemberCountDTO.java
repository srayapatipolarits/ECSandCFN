/**
 * 
 */
package com.sp.web.dto;

/**
 * @author pradeepruhil
 *
 */
public class GroupMemberCountDTO {
  
  private String name;
  
  private int totalMember;
  
  private int validMembers;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getTotalMember() {
    return totalMember;
  }
  
  public void setTotalMember(int totalMember) {
    this.totalMember = totalMember;
  }
  
  public int getValidMembers() {
    return validMembers;
  }
  
  public void setValidMembers(int validMembers) {
    this.validMembers = validMembers;
  }
  
}
