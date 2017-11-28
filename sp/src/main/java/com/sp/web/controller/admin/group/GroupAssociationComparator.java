package com.sp.web.controller.admin.group;

import com.sp.web.model.GroupAssociation;

import java.util.Comparator;

/**
 * @author Dax Abraham
 *
 *         The comparator for Group associations.
 */
public class GroupAssociationComparator implements Comparator<GroupAssociation> {

  public static final GroupAssociationComparator comparator = new GroupAssociationComparator();
  
  @Override
  public int compare(GroupAssociation o1, GroupAssociation o2) {
    if ( o1 == null) {
      return -1;
    }
    
    if (o2 == null) {
      return 1;
    }
    int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    if (res == 0) {
      res = o1.getName().compareTo(o2.getName());
    }
    return res;
  }  
}
