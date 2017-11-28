package com.sp.web.repository.team;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.WriteResult;
import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.log.LogActionType;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The Mongo DB implementation of the team repository.
 */
@Repository
public class MongoGroupRepository implements GroupRepository {

  private static final String COMPANY_ENTITY_KEY = "companyId";

  private static final Logger LOG = Logger.getLogger(MongoGroupRepository.class);

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  UserRepository userRepository;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;

  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.team.TeamRepository#findAllTeams(java.lang.String)
   */
  @Override
  public List<UserGroup> findAllGroups(String companyId) {
    return mongoTemplate.find(query(where(COMPANY_ENTITY_KEY).is(companyId)), UserGroup.class);
  }

  @Override
  public UserGroup updateName(String companyId, String oldName, String name)
      throws InvalidRequestException {

    // check if a group exists with the given name
    checkIfGroupExists(companyId, name);
    
    // check if the name already exists in the system
    UserGroup group = getValidGroup(companyId, oldName);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Group found :" + group);
    }
    /* Update the group name in the user */
    final GroupAssociation oldGroupAssociation = new GroupAssociation(oldName);
    List<String> memberList = getMemberList(group);
    group.setName(name);
    if (memberList != null && !memberList.isEmpty()) {
      List<User> member = userRepository.findByEmail(memberList);

      if (member != null) {
        member.stream().forEach(mb -> {
            List<GroupAssociation> gaList = mb.getGroupAssociationList();
            int oldGAIndex = gaList.indexOf(oldGroupAssociation);
            if (oldGAIndex != -1) {
              GroupAssociation existingGa = gaList.remove(oldGAIndex);
              userRepository.removeGroupAssociation(mb, oldName);
              userRepository.addGroupAssociation(mb, group, existingGa.isGroupLead());
            }
          });
      }
    }

    // replacing the name
    Update updateObj = new Update();
    updateObj.set("name", name);
    WriteResult result = mongoTemplate.updateFirst(query(where(COMPANY_ENTITY_KEY).is(companyId)
        .andOperator(where("name").is(oldName))), updateObj, UserGroup.class);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Changed group name to :" + name + ": number of records updated :" + result.getN());
    }
    return findByName(companyId, name);
  }

  /**
   * Returns the user group for the given group name.
   * 
   * @param companyId
   *          - the company name
   * @param name
   *          - the name of the group
   * @return the user group object
   */
  public UserGroup findByName(String companyId, String name) {
    return mongoTemplate.findOne(
        query(where(COMPANY_ENTITY_KEY).is(companyId).andOperator(where("name").is(name))),
        UserGroup.class);
  }

  @Override
  public UserGroup findByNameIgnoreCase(String companyId, String name) {
    
    /* regex, this regex, fails for "AA,  ,AAA so groups with all these names and search by a will show group exist */
    List<UserGroup> userGroups = mongoTemplate.find(query(where(COMPANY_ENTITY_KEY).is(companyId)
        .andOperator(where("name").regex(name, "i"))), UserGroup.class);
    
    if (userGroups.size() == 0) {
      return null;
    }
    /* check further for the regex*/
    for (UserGroup ug : userGroups) {
      if (ug.getName().equalsIgnoreCase(name)) {
        return ug;
      }
    }
    return null;
  }
  
  @Override
  public UserGroup delete(String companyId, String name) throws InvalidRequestException {

    UserGroup group = getValidGroup(companyId, name);

    List<String> memberList = getMemberList(group); 
    if (memberList != null && memberList.size() > 0) {

      List<User> memberUsers = userRepository.findByEmail(memberList);
      if (memberUsers != null) {
        memberUsers.stream().forEach(mu -> userRepository.removeGroupAssociation(mu, name));
      }
    }

    WriteResult result = mongoTemplate.remove(group);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Changed group name to :" + name + ": number of records updated :" + result.getN());
    }

    return group;
  }

  /**
   * Gets the list of members for the given group including the group lead.
   * 
   * @param group
   *          - group
   * @return
   *      the list of members
   */
  private List<String> getMemberList(UserGroup group) {
    List<String> memberList = group.getMemberList();
    Optional.ofNullable(group.getGroupLead()).ifPresent(memberList::add);
    return memberList;
  }

  /**
   * Method to get the group if group is not found then throws an exception.
   * 
   * @param companyId
   *          - company name
   * @param name
   *          - group name
   * @return - the valid group name
   */
  private UserGroup getValidGroup(String companyId, String name) {
    UserGroup group = findByName(companyId, name);

    if (group == null) {
      throw new InvalidRequestException("No group :" + name + " found in company :" + companyId);
    }
    return group;
  }

  @Override
  public User addMember(String companyId, String name, String memberEmail, boolean isGroupLead)
      throws InvalidRequestException {

    // check and get the user
    User user = userRepository.findByEmail(memberEmail);

    if (user == null) {
      throw new InvalidRequestException("User :" + memberEmail + ": not found in the system !!!");
    }
    
    return addMember(companyId, name, user, isGroupLead);

  }

  @Override
  public User addMember(User user, GroupAssociation groupAssociation) {
    return addMember(user.getCompanyId(), groupAssociation.getName(), user,
        groupAssociation.isGroupLead());
  }

  @Override
  public User addMember(String companyId, String name, User user, boolean isGroupLead)
      throws InvalidRequestException {

    // check and get the user group to add the user to
    UserGroup group = getValidGroup(companyId, name);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Group found :" + group);
    }

    Update updateObj = new Update();

    List<String> memberList = group.getMemberList();

    String memberEmail = user.getEmail();

    // check if the new member to add is the group lead
    if (isGroupLead) {
      // check if group lead already exists
      String groupLeadEmail = group.getGroupLead();
      if (groupLeadEmail != null) {

        throw new InvalidRequestException("User :" + groupLeadEmail
            + ": already team lead of the group :" + name + " !!!");

        // Functionality of moving the team lead to the
        // member has be removed post conversation with Kesh Date: 13/12/2014

        // // check if the current group lead is the same as the
        // // group lead sent
        // if (groupLeadEmail.equalsIgnoreCase(memberEmail)) {
        // throw new InvalidRequestException("User :" + memberEmail
        // + ": already team lead of the group :" + name + " !!!");
        // }
        //
        // // move the existing group lead as a group member
        // User groupLeadUser = userRepository.findByEmail(groupLeadEmail);
        // if (groupLeadUser != null) {
        // memberList.add(groupLeadEmail);
        // updateObj.set("memberList", memberList);
        // userRepository.addGroupAssociation(groupLeadUser, name, false);
        // }
      }

      // setting the new group lead
      updateObj.set("groupLead", memberEmail);
      group.setGroupLead(memberEmail);

      // set the group association object for the user
      // and set the user as the group lead
      userRepository.addGroupAssociation(user, group, isGroupLead);

      // also check if the member is existing in the member list
      if (memberList.remove(memberEmail)) {
        // Update the member list with the group lead email removed
        // from the member list
        updateObj.set("memberList", memberList);
      }
      
      LogRequest logRequest = new LogRequest(LogActionType.GroupLead, user, user);
      logRequest.addParam(Constants.PARAM_NOTIFICATION_URL_PARAM, group.getId());
      logRequest.addParam(Constants.PARAM_NOTIFICATION_MESSAGE, MessagesHelper.getMessage(
          LogActionType.GroupLead.getMessageKey(), user.getLocale(), group.getName()));
      logGateway.logNotification(logRequest);
    } else {
      // check if the user is already present in the user group as a member or
      // team lead
      if (memberList.contains(memberEmail)) {
        throw new InvalidRequestException("User :" + memberEmail + ": already part of group :"
            + name + " !!!");
      } else if (group.getGroupLead() != null ? group.getGroupLead().equals(memberEmail) : false) {
        // removing the group lead association for the member
        updateObj.set("groupLead", null);
      }

      // add the member to the team
      memberList.add(memberEmail);

      // set the group association object for the user
      userRepository.addGroupAssociation(user, group, false);

      // add the member to the group
      updateObj.set("memberList", memberList);
    }
    
    WriteResult result = mongoTemplate.upsert(query(where(COMPANY_ENTITY_KEY).is(companyId)
        .andOperator(where("name").is(name))), updateObj, UserGroup.class);

    if (LOG.isDebugEnabled()) {
      LOG.debug("The number records updated :" + result.getN());
    }
    userFactory.updateUserAndSession(user);
    return user;
  }

  @Override
  public UserGroup removeMember(String companyId, String name, String memberEmail,
      boolean isGroupLead) {

    // check and get the user group to add the user to
    UserGroup group = getValidGroup(companyId, name);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Group found :" + group);
    }

    Update updateObj = new Update();
    User userToModify = null;

    // check if the user is there in the user list
    if (isGroupLead) {
      String groupLeadEmail = group.getGroupLead();

      if (groupLeadEmail != null) {
        // check if given member email is group lead
        if (groupLeadEmail.equalsIgnoreCase(memberEmail)) {
          // move the group lead to the group list
          updateObj.set("groupLead", null);
          // validate if user exists
          userToModify = userRepository.findByEmailValidated(memberEmail);

          // add the member to the member list
          // group.addMember(memberEmail);
          // updateObj.set("memberList", group.getMemberList());

        } else {
          throw new InvalidRequestException("User :" + memberEmail
              + ": not group lead for the group :" + name + " in company :" + companyId + " !!!");
        }
      } else {
        throw new InvalidRequestException("User :" + memberEmail
            + ": not group lead as group lead not set for the group :" + name + " in company :"
            + companyId + " !!!");
      }
    } else {

      // user not group lead

      // check if member is present in the list
      if (group.removeMember(memberEmail)) {
        // remove the member from the group
        updateObj.set("memberList", group.getMemberList());
        userToModify = userRepository.findByEmail(memberEmail);
      } else {
        throw new InvalidRequestException("User :" + memberEmail + ": not part of the group :"
            + name + " in company :" + companyId + " !!!");
      }
    }

    WriteResult result = mongoTemplate.upsert(query(where(COMPANY_ENTITY_KEY).is(companyId)
        .andOperator(where("name").is(name))), updateObj, UserGroup.class);

    if (LOG.isDebugEnabled()) {
      LOG.debug("The number records updated :" + result.getN());
    }

    // if (groupLeadUser != null) {
    // // updating group lead association
    // userRepository.addGroupAssociation(groupLeadUser, name, false);
    // }

    // update the user group association
    userRepository.removeGroupAssociation(userToModify, name);
    // if (userToModify != null) {
    // // update the user group association
    // userRepository.removeGroupAssociation(userToModify, name);
    // }
    userFactory.updateUserAndSession(userToModify);
    return group;
  }

  @Override
  public UserGroup removeMember(String companyId, String name, String memberEmail) {
    return removeMember(companyId, name, memberEmail, false);
  }

  @Override
  public List<User> getMembers(String companyId, String name) {
    // check and get the user group to add the user to
    UserGroup group = getValidGroup(companyId, name);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Group found :" + group);
    }
    
    List<String> userList = getMemberList(group);
    
    return userRepository.findByEmail(userList);
  }

  @Override
  public UserGroup createGroup(String companyId, String name) {
    return createGroup(companyId, name, null);
  }

  @Override
  public UserGroup createGroup(String companyId, String name, String groupLead) {

    // check if a group exists with the given name
    checkIfGroupExists(companyId, name);

    // create a new user group to add to db
    UserGroup group = new UserGroup();
    group.setName(name);
    group.setCompanyId(companyId);

    User groupLeadUser = null;

    // set group lead if group lead email
    // present
    if (groupLead != null && groupLead.trim().length() > 0) {
      // check if user exists
      groupLeadUser = userRepository.findByEmail(groupLead);
      if (groupLeadUser != null) {
        group.setGroupLead(groupLeadUser.getEmail());
      } else {
        throw new InvalidRequestException("Group lead user :" + groupLead
            + ": not found in company :" + companyId);
      }
    }
    // creating the group in db
    mongoTemplate.insert(group);

    if (groupLeadUser != null) {
      // update the group association for the group lead
      // after the group has been created
      userRepository.addGroupAssociation(groupLeadUser, group, true);
    }

    // returning the newly created group
    return group;
  }

  private void checkIfGroupExists(String companyId, String name) {
    // check if a group with the name already exists in the system
    UserGroup findByNameIgnoreCase = findByNameIgnoreCase(companyId, name);
    if (findByNameIgnoreCase != null) {
      throw new InvalidRequestException("Group already exists.");
    }
  }

  @Override
  public UserGroup save(UserGroup group) {
    mongoTemplate.save(group);
    return group;
  }

  @Override
  public void deleteGroup(UserGroup group) {
    mongoTemplate.remove(group);
  }

  @Override
  public UserGroup findById(String groupId) {
    return mongoTemplate.findById(groupId, UserGroup.class);
  }
}
