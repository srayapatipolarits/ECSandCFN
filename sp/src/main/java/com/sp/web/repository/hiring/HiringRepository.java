package com.sp.web.repository.hiring;

import com.sp.web.model.HiringUser;
import com.sp.web.model.UserType;
import com.sp.web.model.hiring.user.HiringUserArchive;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The hiring repository methods.
 */
public interface HiringRepository {

  /**
   * Get the list of hiring candidates for the company.
   * 
   * @param companyId
   *          - the company id
   * @return
   *      the list of hiring candidates for the company
   */
  List<HiringUser> getAllUsers(String companyId);

  /**
   * Get the list of candidate emails to delete.
   * 
   * @param companyId
   *          - company id
   * @param candidateEmails
   *          - candidate emails 
   * @return
   *      the response to the get request
   */
  List<HiringUser> getAllUsers(String companyId, List<String> candidateEmails);

  /**
   * Get all the users in the company for the given user type.
   * 
   * @param companyId
   *            - company id
   * @param type
   *            - user type
   * @return
   *    the list of users
   */
  List<HiringUser> getAllUsers(String companyId, UserType type);
  
  /**
   * Gets all the valid users in the system.
   * 
   * @param companyId
   *          - company id
   * @return
   *      the list of users
   */
  List<HiringUser> getAllValidUsers(String companyId);
  
  /**
   * Get the hiring user for the given email.
   * 
   * @param companyId
   *          - company
   * @param email
   *          - email
   * @return
   *      the hiring user with the given email
   */
  HiringUser findByEmail(String companyId, String email);

  /**
   * Find all the hiring candidates with the given email.
   * 
   * @param email
   *          - email
   * @return
   *      the list of hiring users
   */
  List<HiringUser> findByEmail(String email);

  /**
   * Add the given hiring user to the database.
   * 
   * @param hiringUser
   *          - hiring user to add
   * @return
   *      the newly added hiring user
   */
  HiringUser addHiringUser(HiringUser hiringUser);

  /**
   * Updates the given hiring user.
   * 
   * @param hiringUser
   *          - hiring user
   * @return
   *      the updated hiring user
   */
  HiringUser updateHiringUser(HiringUser hiringUser);

  /**
   * Gets the archived users for the given company.
   * 
   * @param companyId
   *          - company id
   * @return
   *      the list of archived candidates
   */
  Collection<? extends HiringUser> getAllArchivedUsers(String companyId);

  /**
   * Gets the hiring user but also validates for non null condition.
   * 
   * @param companyId
   *            - company id
   * @param candidateEmail
   *            - candidate email
   * @return
   *      the hiring candidate
   */
  HiringUser findByEmailValidated(String companyId, String candidateEmail);
  
  /**
   * Get the hiring user for the given id.
   * 
   * @param hiringUserId
   *          - hiring user id
   * @return
   *      the hiring user
   */
  HiringUser findById(String hiringUserId);

  /**
   * Get the hiring user for the given id.
   * 
   * @param hiringUserId
   *          - hiring user id
   * @return
   *      the hiring user
   */
  HiringUser findByIdValidated(String hiringUserId);

  /**
   * Get the hiring archive user.
   * 
   * @param companyId
   *          - company id
   * @param email
   *          - hiring user email
   * @return
   *      the hiring archive user
   */
  HiringUserArchive findArchivedCandidateByEmail(String companyId, String email);

  /**
   * Get all the archived candidates matching the candidate emails.
   * 
   * @param companyId
   *          - company id
   * @param candidateEmails
   *          - candidate email's
   * @return
   *      the list of archived candidates matching the email's
   */
  Collection<? extends HiringUserArchive> getAllArchiveUsers(String companyId,
      List<String> candidateEmails);
  
  /**
   * Find all the archived users for the given email id.
   * 
   * @param email
   *          - email
   * @return
   *      list of hiring archive users
   */
  List<HiringUserArchive> findByEmailHiringArchive(String email);
  
  /**
   * Gets all the hiring tags in the company.
   * 
   * @param companyId
   *          - Company 
   * @return
   *    the hiring tags 
   */
  Set<String> getAllTags(String companyId);

  /**
   * Gets all the hired members for the given company.
   * 
   * @param companyId
   *          - company id
   * @return
   *      the hired candidates for the company
   */
  List<HiringUserArchive> findAllHiredCandidates(String companyId);

  /**
   * Get the archived user for the given company.
   * 
   * @param companyId
   *          - company id
   * @param candidateId
   *          - candidate id
   * @return
   *      the hiring archived user
   */
  HiringUserArchive findArchivedUserById(String candidateId);

  /**
   * Get the archived user for the given company.
   * 
   * @param companyId
   *          - company id
   * @param candidateId
   *          - candidate id
   * @return
   *      the hiring archived user
   */
  HiringUserArchive findArchivedUserByIdValidated(String candidateId);
  
  /**
   * getAllArchivedUsersForRole method will return all the archived users who has the requested
   * role.
   * 
   * @param companyId
   *          for which archived users to be retrieved.
   * @param role
   *          of the user.
   * @return the archivedUsers.
   */
  Collection<? extends HiringUser> getAllArchivedUsersForRole(String companyId, String role);
  
  /**
   * getAllUsersForRole method will get all the users for the company belong to the role specified.
   * 
   * @param companyId
   *          companyId for which the user is to be retrieved.
   * @param role
   *          of the user.
   * @return the List of hiring Users.
   */
  List<HiringUser> findByRole(String companyId, String role);
  
  /**
   * getAllHiringRolesArchived users will return the hiring roles of archied users nly.
   * 
   * @param companyId
   *          companyId for which the user is to be retrieved.
   * @return
   *    the List of hiring roles
   */
  Set<String> getAllHiringRolesArchived(String companyId);

  /**
   * Gets all the hiring roles for the company.
   * 
   * @param companyId
   *          companyId for which the user is to be retrieved.
   * @return
   *    the List of hiring roles
   */
  Set<String> getAllHiringRolesCandidates(String companyId);

  /**
   * Method to delete the hiring user.
   * 
   * @param hiringUser
   *          - hiring user to delete
   */
  void delete(HiringUser hiringUser);
  
  /**
   * Method to delete the hiring user archive.
   * 
   * @param hiringUser
   *          - hiring user to delete
   */
  void delete(HiringUserArchive hiringUser);

  /**
   * Saving the archive user.
   * 
   * @param archiveUser
   *          - archive user
   */
  void save(HiringUserArchive archiveUser);

  /**
   * Find the hiring user for the given token id.
   * 
   * @param tokenId
   *          - token id
   * @return
   *    the hiring user
   */
  HiringUser findByToken(String tokenId);

  List<HiringUser> findAllUsers(List<String> userIds);

}
