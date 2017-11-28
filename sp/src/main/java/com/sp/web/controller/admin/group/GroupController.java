package com.sp.web.controller.admin.group;

import static com.sp.web.controller.ControllerHelper.process;

import com.sp.web.controller.response.GroupResponse;
import com.sp.web.dto.UserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.teamdynamics.TeamDynamicForm;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The controller class for all the team management services.
 */
@Controller
public class GroupController {
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private GroupControllerHelper helper;
  
  @Autowired
  LoginHelper loginHelper;
  
  private static final GroupInfoComparator groupInfoComparator = new GroupInfoComparator();
  
  private static final Logger LOG = Logger.getLogger(GroupController.class);
  
  /**
   * The main assessment class.
   * 
   * @return the response to the get request
   */
  @RequestMapping(value = "/admin/group/all", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getAllTeams(Authentication token) {
    
    GroupResponse response = new GroupResponse();
    
    // get the user
    User user = GenericUtils.getUserFromAuthentication(token);
    
    // get all the groups for the given company
    List<UserGroup> groupList = groupRepository.findAllGroups(user.getCompanyId());
    
    // create the group info object to send in the response
    List<GroupInfo> groupInfoList = groupList.stream().map(this::getGroupInfo)
        .sorted(groupInfoComparator).collect(Collectors.toList());
    
    response.setGroupInfoList(groupInfoList);
    return response;
  }
  
  /**
   * Creates a group info object for the given user group object.
   * 
   * @param group
   *          - the group
   * @return the group info object
   */
  private GroupInfo getGroupInfo(UserGroup group) {
    // create a new group info object
    GroupInfo groupInfo = new GroupInfo(group);
    
    // add group lead information
    if (group.getGroupLead() != null) {
      User groupLead = userRepository.findByEmail(group.getGroupLead(), group.getCompanyId());
      if (groupLead != null) {
        groupInfo.setGroupLead(groupLead);
      }
    }
    return groupInfo;
  }
  
  /**
   * Method to update the group name.
   * 
   * @param token
   *          - token to get the user
   * @param name
   *          - new group name
   * @param oldName
   *          - old group name
   * @return response indicating failure or success of the update action.
   */
  @RequestMapping(value = "/admin/group/updateName", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse updateTeamName(Authentication token, @RequestParam String name,
      @RequestParam String oldName) {
    
    // create the response to send
    GroupResponse response = new GroupResponse();
    
    try {
      
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      // update the group name
      UserGroup group = groupRepository.updateName(user.getCompanyId(), oldName, name);
      
      if (group == null) {
        throw new InvalidRequestException("Could not update group :" + oldName + ": company :"
            + user.getCompanyId() + ": as group not found !!!");
      }
      
      // check if the currently logged in user is part of the group
      if (group.hasMember(user)) {
        loginHelper.updateUser(userRepository.findUserById(user.getId()));
      }
      response.addToGroupInfo(group);
      
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    // the response
    return response;
  }
  
  /**
   * Delete a group.
   * 
   * @param token
   *          - to get user
   * @param name
   *          - the group name
   * @return the response for the delete action
   */
  @RequestMapping(value = "/admin/group/delete", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse deleteGroup(Authentication token, @RequestParam String name) {
    
    GroupResponse response = new GroupResponse();
    
    try {
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      // update the group name
      UserGroup group = groupRepository.delete(user.getCompanyId(), name);
      
      // check if the currently logged in user is part of the group
      if (group.hasMember(user)) {
        loginHelper.updateUser(userRepository.findUserById(user.getId()));
      }
      
      // set success response
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    return response;
    
  }
  
  /**
   * Method to add a member to the group.
   * 
   * @param token
   *          - to get user information
   * @param groupNameList
   *          - group name list
   * @param memberEmailList
   *          - list of members to delete
   * @return the response for the add action
   */
  @RequestMapping(value = "/admin/group/addMember", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addMemberToGroup(Authentication token,
      @RequestParam List<String> groupNameList, @RequestParam List<String> memberEmailList,
      @RequestParam(defaultValue = "false") boolean isGroupLead) {
    
    GroupResponse response = new GroupResponse();
    
    try {
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      // update the group name
      String companyId = user.getCompanyId();
      User loggedinUserUpdate = null;
      final String userEmail = user.getEmail();
      
      Assert.notEmpty(groupNameList, "Group name required.");
      
      for (String groupName : groupNameList) {
        UserGroup group = groupRepository.findByNameIgnoreCase(companyId, groupName);
        if (group == null) {
          throw new InvalidRequestException("No group :" + groupName + " found in company :"
              + companyId);
        }
        
        for (String memberEmail : memberEmailList) {
          if (group.hasMember(memberEmail)) {
            LOG.debug("User already present in the group ignoring request.");
            continue;
          }
          User addedUser = groupRepository
              .addMember(companyId, groupName, memberEmail, isGroupLead);
          if (loggedinUserUpdate == null && memberEmail.equals(userEmail)) {
            loggedinUserUpdate = addedUser;
          }
        }
      }
      
      if (loggedinUserUpdate != null) {
        loginHelper.updateUser(loggedinUserUpdate);
      }
      // set success response
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    return response;
  }
  
  /**
   * Controller for adding multiple group associations to the member.
   * 
   * @param token
   *          - the logged in user
   * @param memberEmail
   *          - the member to update
   * @param groupAssociationList
   *          - the list of group associations for the user
   * @return the response to the add multi
   */
  @RequestMapping(value = "/admin/group/memberGroupUpdate", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse addMultipleMembersToGroup(Authentication token,
      @RequestParam List<String> groupAssociationList, @RequestParam String memberEmail) {
    
    return process(helper::updateMultipleGroups, token, memberEmail, groupAssociationList);
  }
  
  /**
   * Method to remove the member from the group.
   * 
   * @param token
   *          - to get the logged in user
   * @param name
   *          - group name
   * @param memberEmail
   *          - member to remove
   * @return the response for the remove action
   */
  @RequestMapping(value = "/admin/group/removeMember", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeMemberFromGroup(Authentication token, @RequestParam String name,
      @RequestParam List<String> memberEmail) {
    
    GroupResponse response = new GroupResponse();
    
    try {
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      UserGroup group = groupRepository.findByName(user.getCompanyId(), name);
      /* fetch the group information */
      if (group == null) {
        throw new InvalidRequestException("No group :" + name + " found in company :"
            + user.getCompanyId());
      }
      memberEmail.forEach(m -> {
        if (group.getGroupLead() != null && group.getGroupLead().equalsIgnoreCase(m)) {
          groupRepository.removeMember(user.getCompanyId(), name, m, true);
        } else {
          groupRepository.removeMember(user.getCompanyId(), name, m, false);
        }
        
      });
      
      // remove the member from the group
      
      if (memberEmail.contains(user.getEmail())) {
        loginHelper.updateUser(userRepository.findUserById(user.getId()));
      }
      
      // set success response
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    return response;
  }
  
  /**
   * Gets the group details for the requested group.
   * 
   * @param token
   *          - get the logged in user information
   * @param name
   *          - group name
   * @return the response for the get group details action
   */
  @RequestMapping(value = "/admin/group/groupDetails", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getMembersForGroup(Authentication token, @RequestParam String name) {
    
    GroupResponse response = new GroupResponse();
    
    try {
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      // get the user group for given company name and group name
      UserGroup group = groupRepository.findByName(user.getCompanyId(), name);
      
      if (group == null) {
        throw new InvalidRequestException("No group :" + name + " found in company :"
            + user.getCompanyId());
      }
      
      GroupInfo groupInfo = new GroupInfo(group);
      
      // get the user details for all the members
      List<UserDTO> memberSummaryList = userRepository.getUserSummary(group.getMemberList());
      
      // set the list of users
      groupInfo.setMemberSummaryList(memberSummaryList);
      
      if (group.getGroupLead() != null) {
        User groupLead = userRepository.findByEmail(group.getGroupLead(), group.getCompanyId());
        groupInfo.setGroupLead(groupLead);
      }
      
      // setting group info to response
      response.setGroupInfo(groupInfo);
      
      // set success response
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException(e));
    }
    
    return response;
  }
  
  /**
   * Gets the group details for the requested group.
   * 
   * @param token
   *          - get the logged in user information
   * @param name
   *          - group name
   * @param groupLead
   *          - optional group lead email to set the group lead
   * @return the response for the get group details action
   */
  @RequestMapping(value = "/admin/group/create", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createGroup(Authentication token, @RequestParam String name,
      @RequestParam(required = false) String groupLead) {
    
    GroupResponse response = new GroupResponse();
    
    try {
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      UserGroup group = groupRepository.createGroup(user.getCompanyId(), name, groupLead);
      
      response.setGroupInfo(getGroupInfo(group));
      
      if (group.hasMember(user)) {
        loginHelper.updateUser(userRepository.findUserById(user.getId()));
      }
      
      // set success
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new InvalidRequestException("Error creating the group :" + name));
    }
    return response;
  }
  
  /**
   * Returns the summary details of all the members in the group.
   * 
   * @param token
   *          - for logged in user information
   * @param name
   *          - group name
   * @return the response for the get group member summary details action
   */
  @RequestMapping(value = "/admin/group/memberSummary", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse memberSummaryForGroup(Authentication token, @RequestParam String name) {
    
    GroupResponse response = new GroupResponse();
    
    try {
      // get the user
      User user = GenericUtils.getUserFromAuthentication(token);
      
      // get the user group for given company name and group name
      UserGroup group = groupRepository.findByName(user.getCompanyId(), name);
      
      if (group == null) {
        throw new InvalidRequestException("No group :" + name + " found in company :"
            + user.getCompanyId());
      }
      
      // get the user details for all the members
      List<UserDTO> memberSummaryList = userRepository.getUserSummary(group.getMemberList());
      
      // set the member summary list to response
      response.setGroupSummaryList(memberSummaryList);
      
      // set success
      response.isSuccess();
      
    } catch (SPException exp) {
      LOG.warn("Unable to change the group name ", exp);
      response.addError(exp);
    } catch (Exception e) {
      LOG.warn("Unable to change the group name ", e);
      response.addError(new SPException("Error creating the group :" + name, e));
    }
    return response;
  }
  
  @RequestMapping(value = "/groupLead/getMembersAndGroups", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGroupLeadMemberAndGroups(Authentication token) {
    // process the add single user from user controller helper
    return process(helper::getGroupLeadMemberAndGroups, token);
  }
  
  /**
   * getGroupsWithMemberCount return the groups with theri members.
   * 
   * @param token
   *          , logged in user.
   */
  @RequestMapping(value = "/spectrum/groups/memberCount", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGroupsWithMembersCount(Authentication token) {
    // process the add single user from user controller helper
    return process(helper::getAllGroupsWithMemberCount, token);
  }
  
  @RequestMapping(value = "/admin/group/removeGroupLead", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse removeGroupLead(@RequestParam String groupLeadEmail, @RequestParam String name,
      Authentication token) {
    // process the remove group lead from the group controller helper
    return process(helper::removeGroupLead, token, groupLeadEmail, name);
  }
  
  @RequestMapping(value = "/sysAdmin/actionPlan/group/getGroupList", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGroupList(@RequestParam String companyId, Authentication token) {
    // process the remove group lead from the group controller helper
    return process(helper::getGroupList, token, companyId);
  }
  
  @RequestMapping(value = "/admin/group/getGroupList", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getGroupListForAdmin(@RequestParam String companyId, Authentication token) {
    // process the remove group lead from the group controller helper
    return process(helper::getGroupList, token, companyId);
  }
  
  @RequestMapping(value = "/groupLead/getMemberPortraits", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getMemberPortraits(@RequestBody TeamDynamicForm form, Authentication token) {
    // process the add single user from user controller helper
    return process(helper::getMemberPortraits, token, form);
  }
  
}
