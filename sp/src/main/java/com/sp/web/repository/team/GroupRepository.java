package com.sp.web.repository.team;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The repository interface for all the team access methods.
 */
public interface GroupRepository {

  /**
   * Find all the groups.
   * 
   * @param companyId
   *          - the company id
   * @return the list of teams for the company
   */
  List<UserGroup> findAllGroups(String companyId);

  /**
   * Update the name of the group.
   * 
   * @param companyId
   *          - company id
   * @param oldName
   *          - old group name
   * @param name
   *          - new group name
   * @return The updated group
   * @throws InvalidRequestException
   *           In case of new name already existing in the system
   */
  UserGroup updateName(String companyId, String oldName, String name)
      throws InvalidRequestException;

  /**
   * Delete the group.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return group deleted
   * 
   * @throws InvalidRequestException
   *           In case of an error performing the operation
   */
  UserGroup delete(String companyId, String name) throws InvalidRequestException;

  /**
   * Method to add member to the group.
   * 
   * @param companyId
   *          - the company id
   * @param name
   *          - group name
   * @param memberEmail
   *          - member to add
   * @return the updated user group
   * 
   * @throws InvalidRequestException
   *           In case of an error performing the operation
   */
  User addMember(String companyId, String name, String memberEmail, boolean isGroupLead)
      throws InvalidRequestException;

  /**
   * Adds the group association for the user.
   * 
   * @param user
   *          - user to modify
   * @param groupAssociation
   *          - group association
   * @return the updated user
   */
  User addMember(final User user, GroupAssociation groupAssociation);
  
  /**
   * Method to add member to the group.
   * 
   * @param companyId
   *          - the company id
   * @param name
   *          - group name
   * @param user
   *          - member to add
   * @return the updated user group
   * 
   * @throws InvalidRequestException
   *           In case of an error performing the operation
   */
  public User addMember(String companyId, String name, User user, boolean isGroupLead)
      throws InvalidRequestException;

  /**
   * Method to remove the given member from the group.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @param memberEmail
   *          - member to remove
   * @param isGroupLead
   *          - flag to indicate if team lead
   * @return the updated user group object
   */
  UserGroup removeMember(String companyId, String name, String memberEmail, boolean isGroupLead);

  /**
   * Method to remove the given member from the group, the member to delete is assumed as not to be
   * the team lead.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @param memberEmail
   *          - member to remove
   * @return the updated user group object
   */
  UserGroup removeMember(String companyId, String name, String memberEmail);

  /**
   * Gets all the members of the group.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return - list of members of the group
   */
  List<User> getMembers(String companyId, String name);

  /**
   * Gets the group for the given group name.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return the User Group for the given group name
   */
  UserGroup findByName(String companyId, String name);
  
  /**
   * Gets the group for the given group name, ignores case while checking.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return the User Group for the given group name
   */
  UserGroup findByNameIgnoreCase(String companyId, String name);
  
  /**
   * Create a new group with the given group name.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @return the newly created group
   */
  UserGroup createGroup(String companyId, String name);

  /**
   * Create a new group with the given group name and set the group lead with the group lead email
   * provided.
   * 
   * @param companyId
   *          - company id
   * @param name
   *          - group name
   * @param groupLead
   *          - group lead email
   * @return the newly created group
   */
  UserGroup createGroup(String companyId, String name, String groupLead);

  /**
   * Saves the given user group to the database.
   * 
   * @param group
   *          - user group to save
   * @return the saved user group
   */
  UserGroup save(UserGroup group);

  /**
   * Delete the group.
   * 
   * @param group
   *        - group
   */
  void deleteGroup(UserGroup group);

  /**
   * Find the group with the given group id.
   * 
   * @param groupId
   *          - group id
   * @return
   *    the user group for the given group id
   */
  UserGroup findById(String groupId);

}
