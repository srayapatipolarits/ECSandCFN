package com.sp.web.controller.admin.group;

import com.sp.web.dto.UserDTO;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         This is the bean class that wraps the team information to display for the team listing
 *         page.
 */
public class GroupInfo {

  private String id;
  private String name;
  private UserDTO groupLead;
  private int numOfMembers;
  private List<UserDTO> memberSummaryList;

  /**
   * Constructor.
   * 
   * @param group
   *          - group
   */
  public GroupInfo(UserGroup group) {
    this.id = group.getId();
    setName(group.getName());
    setNumOfMembers(group.getMemberList() != null ? group.getMemberList().size() : 0);
    if (group.getGroupLead() != null) {
      numOfMembers++;
    }
  }

  /**
   * Default Constructor.
   */
  public GroupInfo() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getNumOfMembers() {
    return numOfMembers;
  }

  public void setNumOfMembers(int numOfMembers) {
    this.numOfMembers = numOfMembers;
  }

  public UserDTO getGroupLead() {
    return groupLead;
  }

  public void setGroupLead(UserDTO groupLead) {
    this.groupLead = groupLead;
  }

  public void setGroupLead(User groupLead) {
    this.groupLead = new UserDTO(groupLead);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<UserDTO> getMemberSummaryList() {
    return memberSummaryList;
  }

  public void setMemberSummaryList(List<UserDTO> memberSummaryList) {
    this.memberSummaryList = memberSummaryList;
  }
}
