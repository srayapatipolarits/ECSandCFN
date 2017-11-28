package com.sp.web.repository.user;

import com.sp.web.dto.UserDTO;
import com.sp.web.form.AddressForm;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.ExternalUser;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.model.MasterPassword;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.model.account.SPPlanType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <code>UserRepository</code> contains the operation to perform on the user.
 * 
 * @author Dax Abraham/Pradeep R
 *
 */
public interface UserRepository {
  
  public User createUser(User user);
  
  /**
   * <code>findByEmail</code> method will find the whether the email is already registered in the
   * system or not
   * 
   * @param email
   *          to be searched
   * @return the USer associated with the email.
   */
  User findByEmail(String email);
  
  /**
   * Check whether the email exists in the company.
   * 
   * @param email
   *          - email
   * @param companyId
   *          - company id
   * @return the user with the given email in the company
   */
  public User findByEmail(String email, String companyId);
  
  /**
   * Method to get list of users for all the email ids in the list provided.
   * 
   * @param userList
   *          - the list of user email id's
   * @return list of user for the given email id list
   */
  public List<User> findByEmail(List<String> userList);
  
  /**
   * Check whether the email exists in the company.
   * 
   * @param email
   *          - email
   * @param companyId
   *          - company id
   * @return the user with the given email in the company
   */
  public User findByEmailValidated(String memberEmail, String companyId);
  
  /**
   * Gets the user for the given user email but also validates the user if the user is present or
   * not.
   * 
   * @param userEmail
   *          - the user email
   * @return the user for the given user email
   */
  public User findByEmailValidated(String userEmail);
  
  /**
   * Method to update the user account with the user analysis, will update the analysis stored in
   * the user model object.
   * 
   * @param user
   *          - user model to update
   */
  public User updateUserAnalysis(User user);
  
  /**
   * Gets the summary information for all the users in the provided user list.
   * 
   * @param userEmailList
   *          - list of users
   * @return Gets the summary information for the users in the user list
   */
  public List<UserDTO> getUserSummary(List<String> userEmailList);
  
  /**
   * Gets the user summary for the given user email.
   * 
   * @param userEmail
   *          - the user email
   * @return the user summary for the user
   */
  public UserDTO getUserSummary(String userEmail);
  
  /**
   * Method to update the user profile from the given profile form.
   * 
   * @param userProfile
   *          - user profile to update
   * @param address
   *          - user address to update
   * @return the updated user object
   */
  public User updateUserProfile(UserProfileForm userProfile, AddressForm address);
  
  /**
   * Method to update the user role.
   * 
   * @param userToUpdate
   *          - user to update
   * @param roleToAdd
   *          - role to add
   * @return - the updated user
   */
  public User addRole(User userToUpdate, RoleType roleToAdd);
  
  /**
   * Method to update the user role by removing the given role.
   * 
   * @param userToUpdate
   *          - the user to update
   * @param roleToRemove
   *          - the role to remove
   * @return the updated user
   */
  public User removeRole(User userToUpdate, RoleType roleToRemove);
  
  /**
   * Adds the given tag to the user.
   * 
   * @param userToModify
   *          - user to modify
   * @param tagName
   *          - tag to add
   * @return updated user
   */
  public User addTag(User userToModify, String tagName);
  
  /**
   * Removes the given tag from the user.
   * 
   * @param userToModify
   *          - user to modify
   * @param tagName
   *          - tag name
   * @return update user
   */
  public User removeTag(User userToModify, String tagName);
  
  /**
   * Adds the group association to the user profile.
   * 
   * @param userToModify
   *          - user to modify
   * @param group
   *          - group
   * @param isGroupLead
   *          - group lead flag
   * @return the updated user
   */
  public User addGroupAssociation(User userToModify, UserGroup group, boolean isGroupLead);
  
  /**
   * Removes the group association for the user.
   * 
   * @param userToModify
   *          - user to update
   * @param groupName
   *          - group name
   * @return update user
   */
  public User removeGroupAssociation(User userToModify, String groupName);
  
  /**
   * Removes the given user from the database.
   * 
   * @param userToRemove
   *          - user to remove
   */
  public boolean removeUser(User userToRemove);
  
  /**
   * <code>updateProfileImage</code>, updates the profile image of the user
   * 
   * @param imageToUpdate
   *          image to be updated.
   * @return the updated image.
   */
  public User updateProfileImage(User imageToUpdate);
  
  /**
   * <code>updateUserStatus</code> method will update the user status.
   */
  public User updateUserStatus(User userStatus);
  
  /**
   * Delete the user.
   * 
   * @param userToDelete
   *          - user to delete
   */
  public void deleteUser(User userToDelete);
  
  /**
   * <code>updatePassword</code> method will update the password in the system.
   * 
   * @param email
   *          for which use to update the password
   * @param password
   *          of the user
   * @return the updated puser
   */
  public boolean updatePassword(String email, String password);
  
  /**
   * Get the number of members for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the number of employees of the company
   */
  public long getNumberOfMembers(String companyId, SPPlanType planType);
  
  /**
   * Updates the user in the database.
   * 
   * @param user
   *          - user to update
   */
  public void updateUser(User user);
  
  /**
   * Updates the roles for the user.
   * 
   * @param user
   *          - user to update
   */
  public User updateRoles(User user);
  
  /**
   * The number of active members for the company.
   * 
   * @param companyId
   *          - company id
   * @param planType
   *          - plan type
   * @return the number of active members
   */
  public int getNumberOfActiveMembers(String companyId, SPPlanType planType);
  
  /**
   * Find the user by the given account id.
   * 
   * @param accountId
   *          - the account id
   * @return the user for the account
   */
  User findByAccountId(String accountId);
  
  /**
   * Get all the users with the given account id's.
   * 
   * @param accountIdList
   *          - account Id list
   * @return the list of users
   */
  List<User> findByAccountId(List<String> accountIdList);
  
  /**
   * <code>findUsers</code> method will find all the users available in user collection which
   * matches this property value.
   * 
   * @param propertyName
   *          name of the property which is to be served
   * @param propertyValue
   *          name of the property value which is to be served.
   * @return the list of users
   */
  public List<User> findUsers(String propertyName, String propertyValue);
  
  /**
   * <code>findExternalUserByEmail</code> will find the external user by email.
   * 
   * @param email
   *          of the external user
   * @return the external user.
   */
  public ExternalUser findExternalUserByEmail(String email);
  
  /**
   * <code>createExternalUser</code> method will create a new external user in the system.
   * 
   * @param externalUser
   *          to be created.
   */
  public void createExternalUser(ExternalUser externalUser);
  
  /**
   * Removes all the deleted users for the account.
   * 
   * @param companyId
   *          - company
   */
  void removeAllDeletedUsers(String companyId);
  
  /**
   * Feedback user.
   * 
   * @param - id - user id
   * @param featureType
   *          - feature type
   */
  public List<FeedbackUser> findFeedbackUsers(String id, FeatureType featureType);
  
  /**
   * Find all the feedback users.
   * 
   * @param email
   *          - email
   * @return the list of feedback users for this user
   */
  public List<FeedbackUser> findFeedbackUsers(String email);
  
  /**
   * Get all the archived feedback users.
   * 
   * @param id
   *          - user id
   * @return list of feedback archive users
   */
  public List<FeedbackUserArchive> findFeedbackUsersArchive(String id);
  
  /**
   * Updates the users of different types could be Feedback, Hiring or User.
   * 
   * @param user
   *          - user to update
   */
  public void updateGenericUser(Object user);
  
  public <T> T findGenericUserById(String id, T user);
  
  public User findUserById(String id);
  
  /**
   * Find the archived feedback user.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the feedback user archive
   */
  public FeedbackUserArchive findArchivedFeedbackUser(String feedbackUserId);
  
  /**
   * Gets the list of members for the given company with the provided status.
   * 
   * @param companyId
   *          - company id
   * @param userStatus
   *          - status
   * @return the list of members
   */
  public List<User> findMemberByStatus(String companyId, UserStatus userStatus);
  
  /**
   * Gets all the distinct tags applied for the company.
   * 
   * @param companyId
   *          - company
   * @return the list of distinct tags
   */
  public Set<String> getAllTags(String companyId);
  
  /**
   * Find by email and role.
   * 
   * @param email
   *          - email
   * @param role
   *          - role
   * @return the user for the given email and role
   */
  public User findByEmailAndRole(String email, RoleType role);
  
  /**
   * Get the list of users with the given email.
   * 
   * @param email
   *          - email
   * @return the list of users
   */
  public List<User> findAllMembersByEmail(String email);
  
  /**
   * Get the user for the given token id.
   * 
   * @param tokenId
   *          - token id
   * @return the user for the given token
   */
  public User findByToken(String tokenId);
  
  /**
   * Updates the user and creates a new unique user certificate.
   * 
   * @param user
   *          - user
   */
  public void updateUserCertificate(User user);
  
  /**
   * Find the user by the given certificate.
   * 
   * @param certificateNumber
   *          - certificate number
   * @return the user with the given certificate
   */
  public User findByCertificate(String certificateNumber);
  
  /**
   * Get the number of deleted members for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the list of deleted member accounts
   */
  public int getNumberOfDeletedMembers(String companyId, SPPlanType planType);
  
  /**
   * Get the list of all the users in the system.
   * 
   * @return - the list of users
   */
  public List<User> findAllMembers(boolean withBlocked);
  
  /**
   * <code>updateAutoLearning</code> will enable the auto learning for the user.
   * 
   * @param user
   *          whose auto update learning setting is to be changed.
   * @param updateAutoFlag
   *          containing the value true or false.
   */
  public void updateAutoLearning(User user, Boolean updateAutoFlag);
  
  /**
   * <code>findUserByGoalId</code> method will find the user on the basis of goalId.
   * 
   * @param id
   *          - user id
   * @return the user with the given goal id
   */
  public User findUserByGoalId(String id);
  
  /**
   * Get the feedback user with the given feedback user id.
   * 
   * @param feedbackUserId
   *          - user id
   * @return the feedback user with the given feedback user id
   */
  public FeedbackUser findFeedbackUser(String feedbackUserId);
  
  /**
   * Get the feedback user with the given email and feature type.
   * 
   * @param email
   *          - email
   * @param featureType
   *          - feature type
   * @param companyId
   *          - company id
   * @return the feedback user
   */
  public FeedbackUser findFeedbackUser(String email, FeatureType featureType, String companyId);
  
  /**
   * <code>findValidUsersByTitle</code> method will return all the valid users having the title
   * passsed.
   * 
   * @param companyId
   *          for which users are to be searched.
   * @param title
   *          to be find.
   * @return the list of users matched.
   */
  public List<User> findValidUsersByTitle(String companyId, String title);
  
  /**
   * Get a list of feedback user with the member id and feature type.
   * 
   * @param feedbackFor
   *          - feedback for user
   * @param featureType
   *          - feature type
   * @return the list of feedback user
   */
  public List<FeedbackUser> findAllFeedbackUsersByFeedbackForAndFeature(String feedbackFor,
      FeatureType featureType);
  
  /**
   * @param companyId
   *          for which users to be find
   * @param roleType
   *          of the user.
   * @return the user role.
   */
  public List<User> findByCompanyAndRole(String companyId, RoleType roleType);
  
  /**
   * Delete the feedback user.
   * 
   * @param user
   *          - user to delete
   */
  public void deleteFeedbackUser(FeedbackUser user);
  
  /**
   * Get all the members with the given competency profile id.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @return list of members
   */
  public List<User> findAllMembersByCompetencyProfileId(String competencyProfileId);
  
  /**
   * Remove all the feedback users for the company with the given feature.
   * 
   * @param featureType
   *          - feature type
   * @param companyId
   *          - company id
   */
  public void removeAllFeedbackUsers(FeatureType featureType, String companyId);
  
  /**
   * Get all the feedback users for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the feedback user for the company
   */
  public List<FeedbackUser> findAllFeedbackUsersByCompany(String companyId);
  
  /**
   * findSysPermissionUsers will return all the users having sysPermisssoin users.
   * 
   * @return the list of users have sys permission users.
   */
  public List<User> findSysPermissionUsers();
  
  /**
   * getMasterPassword method will return the master password.
   * 
   * @return the master password.
   */
  public MasterPassword getMasterPassword();
  
  /**
   * update the master password.
   * 
   * @param masterPassword
   *          master password to be updated.
   */
  public void updateMasterPassword(MasterPassword masterPassword);
  
  /**
   * Get all the feedback users for the given type in the company.
   * 
   * @param type
   *          - feature type
   * @param companyId
   *          - company id
   * @return the feedback users
   */
  public List<FeedbackUser> findAllFeedbackUsers(FeatureType type, String companyId);
  
  /**
   * Get all the feedback users for the given company for member.
   * 
   * @param type
   *          - type
   * @param companyId
   *          - company id
   * @param feedbackFor
   *          - feedback for id
   * @return list of feedback users
   */
  public List<FeedbackUser> findAllFeedbackUsers(FeatureType type, String companyId,
      String feedbackFor);
  
  /**
   * Method to get all the users for the given user ids.
   * 
   * @param userIds
   *          - user ids
   * @return the list of users
   */
  public List<User> findAllUsers(List<String> userIds);
  
  /**
   * Returns the list of user having the same name or exact name.
   * 
   * @param value
   *          is the name of the user
   * @param companyId
   *          of the user.
   * @return the user.
   */
  public List<User> findUserByName(String value, String companyId);
  
  /**
   * Get the users with competency profile count.
   * 
   * @param companyId
   *          - company id
   * @return the count of people with competency assigned
   */
  public long findCompetencyUserCount(String companyId);
  
  /**
   * Gets all the users of the company with competency profile.
   * 
   * @param companyId
   *          - company id
   * @return the list of users
   */
  public List<User> findAllUsersWithCompetencyProfile(String companyId);


  public List<User> findValidUsers(String companyId);
  

  /**
   * Get the basic user information for all the users of the company, returns only the valid users i.e.
   * UserStatus.VALID.
   * 
   * @param companyId
   *          - company id
   * @return
   *     - valid users
   */
  public List<User> getAllBasicUserInfo(String companyId);

}
