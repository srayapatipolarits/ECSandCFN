package com.sp.web.controller.admin.group;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.dto.GroupMemberCountDTO;
import com.sp.web.dto.GroupUserDTO;
import com.sp.web.dto.UserGroupDTO;
import com.sp.web.dto.teamdynamics.TeamDynamicsResultDTO;
import com.sp.web.dto.user.UserAnalysisDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.teamdynamics.TeamDynamicForm;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.teamdynamics.TeamDynamicsFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class for the group controller.
 */
@Component
public class GroupControllerHelper {
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  GroupRepository groupRepository;
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  @Lazy
  UserFactory userFactory;
  
  @Autowired
  private TeamDynamicsFactory dynamicsFactory;
  
  /**
   * Helper method to update all the groups for the given member.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - user and group params to update
   * @return the response to the group add request
   */
  public SPResponse updateMultipleGroups(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the member to update
    String memberEmail = (String) params[0];
    // get the group association list to update
    @SuppressWarnings("unchecked")
    List<String> strGroupAssociationList = (List<String>) params[1];
    
    // create the group associations
    List<GroupAssociation> groupAssociations = strGroupAssociationList.stream()
        .map(GroupAssociation::parse).collect(Collectors.toList());
    
    // get the user
    User userToUpdate = userRepository.findByEmailValidated(memberEmail);
    // validate same company
    user.isSameCompany(userToUpdate);
    
    // create the list of group associations to add and update
    List<GroupAssociation> updateList = new ArrayList<GroupAssociation>();
    List<GroupAssociation> removeList = new ArrayList<GroupAssociation>();
    for (GroupAssociation ga : userToUpdate.getGroupAssociationList()) {
      if (groupAssociations.contains(ga)) {
        GroupAssociation requestGa = groupAssociations.remove(groupAssociations.indexOf(ga));
        if (requestGa.isGroupLead() != ga.isGroupLead()) {
          updateList.add(requestGa);
        }
      } else {
        removeList.add(ga);
      }
    }
    // add the remaining update list
    updateList.addAll(groupAssociations);
    
    // iterate over the update list and update the group associations
    updateList.stream().forEach(ga -> groupRepository.addMember(userToUpdate, ga));
    
    // iterate over the remove list and remove the group associations
    removeList.stream().forEach(
        ga -> groupRepository.removeMember(userToUpdate.getCompanyId(), ga.getName(),
            userToUpdate.getEmail(), ga.isGroupLead()));
    
    // set the success response
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Helper method to get the list of members for a given user. Along with the group names.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get group lead and member details
   */
  public SPResponse getGroupLeadMemberAndGroups(User user) {
    final SPResponse resp = new SPResponse();
    
    User groupLeadUser = userRepository.findByEmailValidated(user.getEmail());
    
    // check if the user has the role group lead
    if (!groupLeadUser.hasRole(RoleType.GroupLead)) {
      throw new InvalidRequestException(MessagesHelper.getMessage(
          "prism.relationship-advisor.groupLeadError", user.getLocale()));
    }
    
    // create an empty list to add members to
    final HashSet<GroupUserDTO> memberList = new HashSet<GroupUserDTO>();
    final HashSet<String> groupNameSet = new HashSet<String>();
    
    // get the company name of the user
    // required for the group info retrieval
    String companyId = groupLeadUser.getCompanyId();
    
    // iterate over all the users group associations
    // where user is a group lead add them to
    // member list
    groupLeadUser
        .getGroupAssociationList()
        .stream()
        .filter(ga -> ga.isGroupLead())
        .forEach(
            ga -> addMembersToList(memberList, groupNameSet, ga.getName(), companyId, groupLeadUser));
    
    // add the member list to the response
    resp.add(Constants.PARAM_MEMBER_LIST, memberList);
    
    // add the group names to the response
    resp.add(Constants.PARAM_GROUP_LIST, groupNameSet);
    
    return resp;
  }
  
  /**
   * Adds the users from the given group to the member list.
   * 
   * @param memberList
   *          - the array list to add the members to
   * @param groupNameSet
   *          - the set of group names
   * @param name
   *          - name of the group
   * @param companyId
   *          - the company id
   * @param groupLeadUser
   *          - the group lead user
   */
  private void addMembersToList(HashSet<GroupUserDTO> memberList, HashSet<String> groupNameSet,
      String name, String companyId, User groupLeadUser) {
    groupNameSet.add(name);
    groupRepository.getMembers(companyId, name).stream().filter(u -> !groupLeadUser.equals(u))
        .forEach(user -> memberList.add(new GroupUserDTO(user, groupLeadUser)));
  }
  
  /**
   * <code>getAllGroupsWithMemeberCount</code> method will return all the user groups in the company
   * with there members.
   * 
   * @param user
   *          list of users.
   * @return all the user gorups.
   */
  public SPResponse getAllGroupsWithMemberCount(User user) {
    
    SortedMap<String, GroupMemberCountDTO> membersCount = new TreeMap<String, GroupMemberCountDTO>();
    
    List<User> allUsers = accountRepository.getAllMembersForCompany(user.getCompanyId());
    SPResponse response = new SPResponse();
    GroupMemberCountDTO allGroupMembers = new GroupMemberCountDTO();
    allUsers.stream().forEach(usr -> {
      
      ArrayList<GroupAssociation> groupAssociationList = usr.getGroupAssociationList();
      groupAssociationList.stream().forEach(gra -> {
        String name = gra.getName();
        GroupMemberCountDTO groupMemberCountDTO = membersCount.get(name);
        if (groupMemberCountDTO == null) {
          groupMemberCountDTO = new GroupMemberCountDTO();
          membersCount.put(name, groupMemberCountDTO);
        }
        groupMemberCountDTO.setTotalMember(groupMemberCountDTO.getTotalMember() + 1);
        if (usr.getUserStatus() == UserStatus.VALID) {
          groupMemberCountDTO.setValidMembers(groupMemberCountDTO.getValidMembers() + 1);
        }
      });
      allGroupMembers.setTotalMember(allGroupMembers.getTotalMember() + 1);
      if (usr.getUserStatus() == UserStatus.VALID) {
        allGroupMembers.setValidMembers(allGroupMembers.getValidMembers() + 1);
      }
    });
    response.add("All Groups", allGroupMembers);
    response.add("members", membersCount);
    response.isSuccess();
    return response;
    
  }
  
  /**
   * <code>removeGroupLead</code> method will remove the group lead from the user and add the member
   * back to the member list
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the group name and group lead email
   * @return the response.
   */
  public SPResponse removeGroupLead(User user, Object[] param) {
    String groupName = (String) param[1];
    String groupLeadEmail = (String) param[0];
    
    /* fetch the group information */
    UserGroup userGroup = groupRepository.findByNameIgnoreCase(user.getCompanyId(), groupName);
    if (userGroup == null) {
      throw new InvalidRequestException("No group :" + groupName + " found in company :"
          + user.getCompanyId());
    }
    
    if (userGroup.getGroupLead() == null
        || (userGroup.getGroupLead() != null && !userGroup.getGroupLead().equalsIgnoreCase(
            groupLeadEmail))) {
      throw new InvalidRequestException("User :" + groupLeadEmail + ": not part of the group :"
          + groupName + " in company :" + user.getCompanyId() + " !!!");
    }
    
    /* remove the user form the group lead */
    groupRepository.removeMember(user.getCompanyId(), groupName, groupLeadEmail, true);
    
    /* Add the member back to the member list */
    groupRepository.addMember(user.getCompanyId(), groupName, groupLeadEmail, false);
    
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
  }
  
  /**
   * Helper method to get the list of groups for the given company.
   * 
   * @param user
   *          - logged in user
   * @param param
   *          - company id
   * @return the list of groups for the company
   */
  public SPResponse getGroupList(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    
    String companyId = (String) param[0];
    
    // validating the company
    accountRepository.findCompanyByIdValidated(companyId);
    
    List<UserGroup> findAllGroups = groupRepository.findAllGroups(companyId);
    
    return resp.add(Constants.PARAM_GROUP_LIST,
        findAllGroups.stream().collect(Collectors.mapping(UserGroupDTO::new, Collectors.toList())));
  }
  
  /**
   * Helper method to get the member portraits for the members in the group.
   * 
   * @param user
   *          - logged in user
   * @param param
   *          - params
   * @return the list of members along with their portratis
   */
  public SPResponse getMemberPortraits(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    
    TeamDynamicForm teamDynamicForm = (TeamDynamicForm) param[0];
    
    teamDynamicForm.validate();
    
    // getting the group
    TeamDynamicsResultDTO teamDynamics = dynamicsFactory.getTeamDynamics(teamDynamicForm);
    resp.add("teamDynamics", teamDynamics);
    return resp;
  }
}
