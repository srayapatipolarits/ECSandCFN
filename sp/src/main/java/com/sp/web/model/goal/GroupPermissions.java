package com.sp.web.model.goal;

import com.sp.web.model.GroupAssociation;

import java.io.Serializable;


/**
 * @author Dax Abraham
 * 
 *         The form to capture the group details as well as the flag to indicate exclusion of group
 *         lead.
 */
public class GroupPermissions implements Serializable {
  
  private static final long serialVersionUID = 8615908253398897298L;
  private String groupId;
  private boolean excludeGroupLead;

  /**
   * Constructor.
   */
  public GroupPermissions() { 
    /* Default Constructor */
  }

  /**
   * Constructor with group id.
   * 
   * @param groupId
   *          - group id
   */
  public GroupPermissions(String groupId) {
    this.groupId = groupId;
  }

  /**
   * Constructor with group id and exclude permissions flag.
   * 
   * @param groupId
   *            - group id
   * @param excludeGroupLead
   *            - exclude group lead flag
   *    
   */
  public GroupPermissions(String groupId, boolean excludeGroupLead) {
    this(groupId);
    this.excludeGroupLead = excludeGroupLead;
  }

  public String getGroupId() {
    return groupId;
  }
  
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  
  public boolean isExcludeGroupLead() {
    return excludeGroupLead;
  }
  
  public void setExcludeGroupLead(boolean excludeGroupLead) {
    this.excludeGroupLead = excludeGroupLead;
  }

  /**
   * Check whether the current group association is valid
   * for the given group permission.
   * 
   * @param ga
   *          - group association
   * @return
   *    true if applicable else false
   */
  public boolean isApplicable(GroupAssociation ga) {
    if (ga == null) {
      return false;
    }
    
    if (ga.getGroupId().equals(groupId)) {
      if (excludeGroupLead) {
        if (ga.isGroupLead()) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }
}
