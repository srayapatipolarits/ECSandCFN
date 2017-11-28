package com.sp.web.dto;

import com.sp.web.dto.user.UserAnalysisDTO;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to transfer the user information to the front end.
 */
public class GroupUserDTO extends UserAnalysisDTO {

  private static final long serialVersionUID = -33790588687529496L;
  
  /*
   * The set of groups the user belongs to.
   */
  Set<String> groups = new HashSet<String>();
  private UserStatus userStatus;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public GroupUserDTO(User user) {
    super(user);
  }

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param groupLeadUser
   *          - group lead user
   */
  public GroupUserDTO(User user, User groupLeadUser) {
    this(user);
    // add the groups that belong to the group lead user
    final ArrayList<GroupAssociation> groupAssociationList = groupLeadUser.getGroupAssociationList();
    user.getGroupAssociationList().stream().filter(groupAssociationList::contains)
        .forEach(ga -> groups.add(ga.getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getEmail().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof GroupUserDTO)) {
      return false;
    }
    return getEmail().equals(((GroupUserDTO) obj).getEmail());
  }

  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }

  public UserStatus getUserStatus() {
    return userStatus;
  }

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
  }
}
