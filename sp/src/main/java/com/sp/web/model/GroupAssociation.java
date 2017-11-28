package com.sp.web.model;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         Model class to store the user and group association.
 */
public class GroupAssociation implements Serializable {

  /**
   * Creates a new group association parsing the string "groupName:isGroupLead".
   * 
   * @param gaStr
   *          - string to parse
   * @return A new group association object
   */
  public static GroupAssociation parse(String gaStr) {
    String[] gaParts = gaStr.split(":");
    return new GroupAssociation(gaParts[0], (gaParts.length == 1) ? false
        : Boolean.parseBoolean(gaParts[1]));
  }
  
  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 8824871611069044109L;
  
  /**
   * Name of the group.
   */
  private String name;

  /**
   * Boolean to indicate if is group lead for the group.
   */
  private boolean isGroupLead;
  
  /**
   * The group id.
   */
  private String groupId;

  /** Default Constructor. */
  public GroupAssociation() {
  }

  /** Constructor is group lead defaults to false. */
  public GroupAssociation(String groupName) {
    this.name = groupName;
  }

  /**
   * Constructor.
   * 
   * @param name
   *          - name of group
   * @param isGroupLead
   *          - is group lead
   */
  public GroupAssociation(String name, boolean isGroupLead) {
    this.name = name;
    this.isGroupLead = isGroupLead;
  }

  /**
   * Constructor to add the group association with the group.
   * 
   * @param group
   *          - group data
   * @param isGroupLead
   *          - flag if is group lead
   */
  public GroupAssociation(UserGroup group, boolean isGroupLead) {
    this(group.getName(), isGroupLead);
    this.groupId = group.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof GroupAssociation)) {
      return false;
    }
    return name.equals(((GroupAssociation) obj).getName());
  }

  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  public boolean isGroupLead() {
    return isGroupLead;
  }

  public void setGroupLead(boolean isGroupLead) {
    this.isGroupLead = isGroupLead;
  }

  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + name + ":" + isGroupLead + "]";
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

}
