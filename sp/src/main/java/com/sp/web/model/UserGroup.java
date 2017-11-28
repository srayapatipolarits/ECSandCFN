package com.sp.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The entity class for User Groups.
 */
public class UserGroup {

  private String id;
  private String name;
  private String companyId;
  private String groupLead;
  private List<String> memberList;

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

  public String getGroupLead() {
    return groupLead;
  }

  public void setGroupLead(String groupLead) {
    this.groupLead = groupLead;
  }

  /**
   * Gets the member list.
   * 
   * @return
   *      member list or an empty member list if none existed
   */
  public List<String> getMemberList() {
    Optional.ofNullable(memberList).orElseGet(() -> {
        memberList = new ArrayList<String>();
        return memberList;
      }); 
    return memberList;
  }

  public void setMemberList(List<String> memberList) {
    this.memberList = memberList;
  }

  /**
   * This method adds a new member to the member list.
   * 
   * @param memberEmail
   *          - member to add
   */
  public void addMember(String memberEmail) {
    if (memberList == null) {
      memberList = new ArrayList<String>();
    }
    memberList.add(memberEmail);
  }

  /**
   * Removes the given member email from the member list.
   * 
   * @param memberEmail
   *          - member email
   * @return true if the member existed in the member list
   */
  public boolean removeMember(String memberEmail) {
    return (memberList == null) ? false : memberList.remove(memberEmail);
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * Check if the user is part of the group.
   * 
   * @param user
   *          - user
   * @return
   *      true if user is part of the group.
   */
  public boolean hasMember(User user) {
    return hasMember(user.getEmail());
  }

  /**
   * Check if the user is part of the group.
   * 
   * @param memberEmail
   *          - member email
   * @return
   *      true if user is part of the group.
   */
  public boolean hasMember(String memberEmail) {
    if (groupLead != null) {
      if (memberEmail.equals(groupLead)) {
        return true;
      }
    }
    return (memberList != null && memberList.contains(memberEmail)) ? true : false;
  }

  /**
   * Checks if the member is already present in the group and is the group lead.
   * 
   * @param memberEmail
   *            - member email
   * @param isGroupLead
   *            - flag to indicate if group lead or regular member
   * @return
   *    true or false if the user is found
   */
  public boolean hasMember(String memberEmail, boolean isGroupLead) {
    if (isGroupLead) {
      if (groupLead != null) {
        return groupLead.equals(memberEmail);
      }
    } else {
      return (memberList != null && memberList.contains(memberEmail)) ? true : false;
    }
    return false;
  }
  
  /**
   * @return 
   *    - the flag to indicate if group lead is present.
   */
  public boolean groupLeadPresent() {
    return groupLead != null;
  }
}
