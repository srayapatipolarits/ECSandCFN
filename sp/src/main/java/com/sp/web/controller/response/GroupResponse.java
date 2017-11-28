package com.sp.web.controller.response;

import com.sp.web.controller.admin.group.GroupInfo;
import com.sp.web.dto.UserDTO;
import com.sp.web.model.UserGroup;
import com.sp.web.mvc.SPResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The response class for all the team controller methods.
 */
public class GroupResponse extends SPResponse {

  private static final String GROUP_SUMMARY_LIST = "groupSummaryList";
  private static final String GROUP_INFO_LIST = "groupInfoList";
  private static final String GROUP_INFO = "groupInfo";

//  @SuppressWarnings("unchecked")
//  public List<GroupInfo> getGroupInfoList() {
//    return (List<GroupInfo>) get(GROUP_INFO_LIST);
//  }

  public void setGroupInfoList(List<GroupInfo> groupInfoList) {
    add(GROUP_INFO_LIST, groupInfoList);
  }

  /**
   * The group to the group info list.
   * 
   * @param group
   *          - group to add to the list
   */
  public void addToGroupInfo(UserGroup group) {
    @SuppressWarnings("unchecked")
    List<GroupInfo> groupInfoList = (List<GroupInfo>) get(GROUP_INFO_LIST);
    if ( groupInfoList == null) {
      groupInfoList = new ArrayList<GroupInfo>();
      setGroupInfoList(groupInfoList);
    }
    groupInfoList.add(new GroupInfo(group));
  }

//  public GroupInfo getGroupInfo() {
//    return (GroupInfo) get(GROUP_INFO);
//  }

  public void setGroupInfo(GroupInfo groupInfo) {
    add(GROUP_INFO, groupInfo);
  }

//  @SuppressWarnings("unchecked")
//  public List<UserSummary> getGroupSummaryList() {
//    return (List<UserSummary>) get(GROUP_SUMMARY_LIST);
//  }

  public void setGroupSummaryList(List<UserDTO> groupSummaryList) {
    add(GROUP_SUMMARY_LIST, groupSummaryList);
  }
}
