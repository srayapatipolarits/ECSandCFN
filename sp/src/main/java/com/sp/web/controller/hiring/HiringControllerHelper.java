package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.controller.admin.member.MemberControllerHelper;
import com.sp.web.controller.i18n.MessagesFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.hiring.group.HiringGroupBaseDTO;
import com.sp.web.dto.hiring.role.HiringRoleBaseDTO;
import com.sp.web.dto.hiring.user.HiringCandidateListingDTO;
import com.sp.web.dto.hiring.user.HiringEmployeeListingDTO;
import com.sp.web.dto.hiring.user.HiringUserArchiveListingDTO;
import com.sp.web.dto.hiring.user.HiringUserBaseDTO;
import com.sp.web.form.hiring.user.HireCandidateForm;
import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.AccountFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.relationship.RelationshipReportManager;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The controller helper.
 */
@Component
public class HiringControllerHelper {
  
  // private static Logger log = Logger.getLogger(HiringControllerHelper.class);
  
  @Autowired
  AccountRepository accountRepository;
  
  @Autowired
  HiringRepository hiringRepository;
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  HiringFactory hiringFactory;
  
  @Autowired
  RelationshipReportManager relationshipReportManager;
  
  @Autowired
  @Qualifier("activityLog")
  LogGateway activityLogGateway;
  
  @Autowired
  AccountFactory accountFactory;
  
  @Autowired
  private MessagesFactory messagesFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private HiringRoleFactory roleFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringGroupFactory groupFactory;
  
  @Autowired
  private MemberControllerHelper memberControllerHelper;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * The helper method to get the available subscriptions for the company account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get request
   */
  public SPResponse getAvailableSubscriptions(User user) {
    SPResponse resp = new SPResponse();
    return resp.add(Constants.PARAM_AVAILABLE_HIRING_SUBSCRIPTIONS, accountRepository
        .findValidatedAccountByCompanyId(user.getCompanyId()).getPlan(SPPlanType.IntelligentHiring)
        .getNumMember());
  }
  
  /**
   * The helper method to get the available admin subscriptions for the company account.
   * 
   * @param user
   *          - logged in user
   * @return the response to the get request
   */
  public SPResponse getAvailableAdminSubscriptions(User user) {
    SPResponse resp = new SPResponse();
    List<User> adminUsersMap = accountRepository.findAdminsForPlan(SPPlanType.IntelligentHiring,
        user.getCompanyId());
    
    return resp.add(Constants.PARAM_AVAILABLE_HIRING_SUBSCRIPTIONS, accountRepository
        .findValidatedAccountByCompanyId(user.getCompanyId()).getPlan(SPPlanType.IntelligentHiring)
        .getNumAdmin()
        - adminUsersMap.size());
  }
  
  /**
   * The helper method to get all the hiring candidates for the company.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getAll(User user, Object[] params) {
    UserType type = (UserType) params[0];
    
    final String companyId = user.getCompanyId();
    final Map<String, List<HiringGroupBaseDTO>> userGroupsMap = groupFactory
        .getUserGroupMap(companyId);
    
    if (type == UserType.Member) {
      return new SPResponse().add(
          Constants.PARAM_HIRING_MEMBERS,
          hiringUserFactory.getAll(companyId, type).stream()
              .map(usr -> new HiringEmployeeListingDTO(usr, userGroupsMap))
              .collect(Collectors.toList()));
    } else {
      return new SPResponse().add(
          Constants.PARAM_HIRING_MEMBERS,
          hiringUserFactory.getAll(companyId, type).stream()
              .map(u -> new HiringCandidateListingDTO(u, roleFactory, userGroupsMap))
              .collect(Collectors.toList()));
    }
  }
  
  /**
   * The helper method to get all the members that can be assigned as admins.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getMembersForAdmin(User user, Object[] params) {
    
    final String companyId = user.getCompanyId();
    
    return new SPResponse().add(Constants.PARAM_HIRING_MEMBERS,
        hiringUserFactory.getAll(companyId, UserType.Member).stream().filter(this::validForAdmin)
            .map(usr -> new HiringUserBaseDTO(usr)).collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get all the members.
   * 
   * @param user
   *          - user
   * @return the response to the get all request
   */
  public SPResponse getAllMembers(User user, Object[] params) {
    boolean validOnly = (boolean) params[0];
    return new SPResponse().add(
        Constants.PARAM_HIRING_MEMBERS,
        ((validOnly) ? hiringUserFactory.getAllValid(user.getCompanyId()) : hiringUserFactory
            .getAll(user.getCompanyId())).stream().map(HiringUserBaseDTO::new)
            .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get all the archived candidates for the company.
   * 
   * @param user
   *          logged in user
   * @return the response to the get request
   */
  public SPResponse archiveGetAll(User user) {
    SPResponse resp = new SPResponse();
    return resp
        .add(
            Constants.PARAM_HIRING_MEMBERS_ARCHIVED,
            hiringUserFactory.getAllArchivedUsers(user.getCompanyId()).stream()
                .map(u -> new HiringUserArchiveListingDTO(u, roleFactory))
                .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get all the hiring roles for the company.
   * 
   * @param user
   *          - logged in user
   * @return the hiring roles in the company
   */
  public SPResponse getRolesAndTags(User user) {
    SPResponse resp = new SPResponse();
    final String companyId = user.getCompanyId();
    List<HiringRole> allRoles = roleFactory.getAll(companyId);
    resp.add(Constants.PARAM_HIRING_ROLES,
        allRoles.stream().map(HiringRoleBaseDTO::new).collect(Collectors.toList()));
    return resp.add(Constants.PARAM_TAG_LIST, hiringRepository.getAllTags(companyId));
  }
  
  /**
   * Helper method to delete the candidate/employee.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - list of params
   * @return the response to the delete request
   */
  public SPResponse delete(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    
    // get the list of email's to delete
    @SuppressWarnings("unchecked")
    final List<String> userIds = (List<String>) params[0];
    final String companyId = user.getCompanyId();
    
    Assert.notEmpty(userIds, "User ids required.");
    
    userIds.stream().map(hiringUserFactory::getUser).filter(u -> filterUsers(u, companyId))
        .forEach(hiringUserFactory::delete);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to delete the archive user.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - list of params
   * @return the response to the delete request
   */
  public SPResponse deleteHiringArchiveUser(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    
    // get the list of email's to delete
    @SuppressWarnings("unchecked")
    final List<String> userIds = (List<String>) params[0];
    final String companyId = user.getCompanyId();
    
    // delete the hiring candidates
    Assert.notEmpty(userIds, "User ids required.");
    
    userIds.stream().map(hiringUserFactory::getArchivedUser).filter(u -> filterUsers(u, companyId))
        .forEach(hiringUserFactory::deleteArchiveUser);
    
    return resp.isSuccess();
  }
  
  /**
   * The helper method to archive the hiring candidates/employees.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the archive request
   */
  @SuppressWarnings("unchecked")
  public SPResponse archiveUser(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the list of email's to delete
    final List<String> userIds = (List<String>) params[0];
    final boolean isHired = (boolean) params[1];
    List<String> tagList = (List<String>) params[2];
    final List<String> tags = Optional.ofNullable(tagList)
        .map(list -> list.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList()))
        .orElse(null);
    final String companyId = user.getCompanyId();
    
    Assert.notEmpty(userIds, "User ids required.");
    
    userIds.stream().map(hiringUserFactory::getUser).filter(u -> filterUsers(u, companyId))
        .forEach(usr -> hiringUserFactory.archiveUser(user, usr, isHired, tags));
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to validate the list of candidates.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the check request
   */
  public SPResponse checkCandidate(User user, Object[] params) {
    SPResponse resp = new SPResponse();
    
    // get the list of email's to delete
    @SuppressWarnings("unchecked")
    final List<String> candidateEmails = (List<String>) params[0];
    
    Assert.notEmpty(candidateEmails, "Candidate emails required.");
    
    Map<String, Object> error = resp.createError();
    
    // validate the hiring user list for the company
    final String companyId = user.getCompanyId();
    
    for (String email : candidateEmails) {
      HiringUser hiringUser = null;
      hiringUser = hiringUserFactory.getUserByEmail(email, companyId);
      if (hiringUser != null) {
        if (hiringUser.getType() == UserType.HiringCandidate) {
          error.put(email,
              MessagesHelper.getMessage("error.add.check.candidate.user", user.getLocale()));
        } else {
          error.put(email,
              MessagesHelper.getMessage("error.add.check.employee.user", user.getLocale()));
        }
        continue;
      }
      
      hiringUser = hiringUserFactory.getArchiveUserByEmail(email, companyId);
      if (hiringUser != null) {
        error.put(email,
            MessagesHelper.getMessage("error.add.check.archive.user", user.getLocale()));
        continue;
      }
      
      User userByEmail = userFactory.getUserByEmail(email);
      if (userByEmail != null && userByEmail.getCompanyId().equals(companyId)) {
        error.put(email, MessagesHelper.getMessage("error.add.check.erti.user", user.getLocale()));
      }
    }
    
    if (!resp.hasErrors()) {
      resp.isSuccess();
    }
    
    return resp;
  }
  
  /**
   * Helper method to add the hiring candidate.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return response for the add request
   */
  public SPResponse add(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // profile form
    HiringAddForm hiringAddForm = (HiringAddForm) params[0];
    
    hiringAddForm.validate(user, roleFactory);
    Map<String, Object> addResponse = hiringFactory.addHiringUser(user, hiringAddForm);
    
    return resp.add(addResponse);
  }
  
  /**
   * The helper method to get the analysis of the candidate and the member.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response for the compare request
   */
  public SPResponse compareProfile(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // candidate email
    String user1Id = (String) params[0];
    
    // member email
    String user2Id = (String) params[1];
    
    HiringUser hiringUser1 = getValidHiringUser(user1Id, user);
    HiringUser hiringUser2 = getValidHiringUser(user2Id, user);
    
    resp.add("user1Analysis", hiringUser1.getAnalysis());
    resp.add("user2Analysis", hiringUser2.getAnalysis());
    
    /* Add the personality keys to the response */
    resp.add(messagesFactory.getMessagesWith("profileKeys", LocaleHelper.locale()));
    
    /* Add the personality type keys to the response */
    resp.add(messagesFactory.getPersonalityTypeMessages());
    
    /* Add the traits type keys to the response */
    resp.add(messagesFactory.getTraitsMessages(user.getUserLocale()));
    
    return resp;
  }
  
  /**
   * Helper method to get relationship comparison content between the candidate and the member.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the compare request
   */
  public SPResponse compareRelation(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // candidate email
    String user1Id = (String) params[0];
    // member email
    String user2Id = (String) params[1];
    // get the personality types to compare
    final RangeType user1RangeType = (RangeType) params[2];
    final RangeType user2RangeType = (RangeType) params[3];
    
    HiringUser hiringUser1 = getValidHiringUser(user1Id, user);
    HiringUser hiringUser2 = getValidHiringUser(user2Id, user);
    
    resp.add(relationshipReportManager.getCompareReport(hiringUser1, hiringUser2, user1RangeType,
        user2RangeType, user.getLocale()));
    
    return resp;
  }
  
  /**
   * Helper method to move the user from the archive user to the candidate list.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the move request
   */
  @SuppressWarnings("unchecked")
  public SPResponse archivePutBack(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // the candidate email to move
    List<String> userIds = (List<String>) params[0];
    Assert.notEmpty(userIds, "User id required.");
    
    final String companyId = user.getCompanyId();
    
    // get the archive user
    userIds.stream().map(hiringUserFactory::getArchivedUser).filter(u -> filterUsers(u, companyId))
        .forEach(hiringUserFactory::putBack);
    
    return resp.isSuccess();
  }
  
  /**
   * Helper method to hire a candidate and the move the user as an employee.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update status request
   */
  public SPResponse hireCandidate(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HireCandidateForm form = (HireCandidateForm) params[0];
    form.validate();
    
    HiringUser hiringUser = hiringUserFactory.getUser(form.getId());
    Assert.notNull(hiringUser, "User not found.");
    Assert.isTrue(filterUsers(hiringUser, user.getCompanyId()), "Unauthorized access.");
    Assert.isTrue(hiringUser.getUserStatus() == UserStatus.VALID, "Assessment pending.");
    Assert.isTrue(hiringUser.getType() == UserType.HiringCandidate, "Not a candidate.");
    
    if (!form.isSameEmail(hiringUser)) {
      final HiringUser existingUser = hiringUserFactory.getUserByEmail(form.getEmail(),
          user.getCompanyId());
      if (existingUser != null) {
        throw new IllegalArgumentException(MessagesHelper.getMessage("error.hire.user."
            + existingUser.getType(), user.getLocale()));
      }
    }
    
    // creating the new user
    HiringUser employeeUser = new HiringUser(hiringUser);
    form.update(employeeUser);
    
    // archiving the existing user
    hiringUserFactory.archiveUser(user, hiringUser, true);
    hiringUserFactory.updateUser(employeeUser);
    return resp.isSuccess();
  }
  
  /**
   * Helper method to hire a candidate and the move the user as an employee.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update status request
   */
  public SPResponse addToErti(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userId = (String) params[0];
    Assert.hasText(userId, "User id required.");
    
    HiringUser hiringUser = getValidEmployee(user, userId);
    
    memberControllerHelper.addMemberFromHiring(user, hiringUser);
    
    // creating the new user
    return resp.isSuccess();
  }
  
  /**
   * Helper method to add an administrator to the system.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add request
   */
  public SPResponse addAdministrator(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userId = (String) params[0];
    Assert.hasText(userId, "User id required.");
    
    HiringUser hiringUser = getValidEmployee(user, userId);
    
    Assert.isTrue(!hiringUser.hasRole(RoleType.Hiring), "User already administrator.");
    
    memberControllerHelper.addHiringAdmin(user, hiringUser);
    
    // creating the new user
    return resp.isSuccess();
  }
  
  /**
   * Helper method to add an administrator to the system.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add request
   */
  public SPResponse removeAdministrator(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userId = (String) params[0];
    Assert.hasText(userId, "User id required.");
    
    User userToRemove = userFactory.getUser(userId);
    Assert.notNull(userToRemove, "User not found.");
    
    HiringUser hiringUser = hiringUserFactory.getUserByEmail(userToRemove.getEmail(),
        userToRemove.getCompanyId());
    Assert.notNull(hiringUser, "Hiring user not found.");
    
    // Why below check is required, hiring role is to be added in User collection , not in
    // HiringUser
    // Assert.isTrue(hiringUser.hasRole(RoleType.Hiring), "User not an administrator.");
    
    hiringUserFactory.removeAdministrator(hiringUser);
    
    // creating the new user
    return resp.isSuccess();
  }

  /**
   * Helper method to check if the company has ERTi feature or not.
   * 
   * @param user
   *        - user
   * @return
   *      the response for ERTi check.
   */
  public SPResponse hasErti(User user) {
    final SPResponse resp = new SPResponse();
    
    // get the company
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    boolean hasErti = company.hasFeature(SPFeature.Erti);
    if (hasErti) {
      hasErti = !company.isErtiDeactivated();
    }
    return resp.add("hasErti", hasErti);
  }
  
  /**
   * Get a valid employee from the same company.
   * 
   * @param user
   *          - user
   * @param userId
   *          - user id
   * @return the employee if found
   */
  private HiringUser getValidEmployee(User user, String userId) {
    HiringUser hiringUser = getValidHiringUser(userId, user);
    Assert.isTrue(hiringUser.getType() == UserType.Member, "Not a member.");
    return hiringUser;
  }
  
  /**
   * Filter the non null users and users from different company.
   * 
   * @param user
   *          - user
   * @param companyId
   *          - company id
   * @return true if user is correct else false
   */
  private boolean filterUsers(HiringUser user, String companyId) {
    return (user != null && user.getCompanyId().equals(companyId));
  }
  
  /**
   * Get the hiring user for the given user id.
   * 
   * @param userId
   *          - user id
   * @param user
   *          - user
   * @return the hiring user
   */
  private HiringUser getValidHiringUser(String userId, User user) {
    HiringUser hiringUser = hiringUserFactory.getUser(userId);
    Assert.notNull(hiringUser, "User not found.");
    Assert.notNull(hiringUser.getAnalysis(), "Assessment pending. " + hiringUser.getEmail());
    Assert.isTrue(filterUsers(hiringUser, user.getCompanyId()), "Unauthorized access.");
    return hiringUser;
  }
  
  /**
   * Validate if the user is valid as an admin user.
   * 
   * @param user
   *          - user to check
   * @return true if valid else false
   */
  private boolean validForAdmin(HiringUser user) {
    return ((user.getUserStatus() == UserStatus.VALID) && !user.hasRole(RoleType.Hiring));
  }
}
