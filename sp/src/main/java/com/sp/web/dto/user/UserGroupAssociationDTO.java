package com.sp.web.dto.user;

import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class to return basic user information and the user group associations.
 */
public class UserGroupAssociationDTO extends UserMarkerDTO {
  
  private static final long serialVersionUID = -5688394836259785242L;
  private Set<String> groups;
  
  /**
   * Constructor from user.
   * 
   * @param user
   *          - user
   */
  public UserGroupAssociationDTO(User user) {
    super(user);
    ArrayList<GroupAssociation> groupAssociationList = user.getGroupAssociationList();
    if (!CollectionUtils.isEmpty(groupAssociationList)) {
      groups = new HashSet<String>();
      groupAssociationList.stream().map(GroupAssociation::getName).forEach(groups::add);
    }
  }

  public Set<String> getGroups() {
    return groups;
  }
  
  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }
  
}
