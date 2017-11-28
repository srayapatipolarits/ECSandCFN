package com.sp.web.controller.admin.member;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dao.goal.ActionPlanDao;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dao.goal.UserGoalProgressDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.PracticeCompOrgViewDTO;
import com.sp.web.dto.UserDTO;
import com.sp.web.dto.UserRolesDTO;
import com.sp.web.dto.competency.BaseCompetencyProfileDTO;
import com.sp.web.dto.goal.ActionPlanSummaryDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.SignupForm;
import com.sp.web.model.Account;
import com.sp.web.model.Company;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.goal.ActionPlanProgress;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.AccountFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.log.LogsRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.tracking.UserActivityTrackingRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.note.NoteFactory;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class MemberControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(MemberControllerHelper.class);
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  @Autowired
  private Environment environment;
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private NoteFactory noteFactory;
  
  @Autowired
  private SPGoalFactory goalFactory;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  private UserActivityTrackingRepository userActivtyRepository;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;
  
  @Autowired
  private LogsRepository logsRepository;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private AccountFactory accountFactory;
  
  /**
   * Helper method to add a single member to the company.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to add the user
   * @return the response
   */
  public SPResponse addSingle(User user, Object[] params) {
    final SPResponse response = new SPResponse();
    
    // get the user form to add
    SignupForm addUserForm = (SignupForm) params[0];
    
    // get the tag list
    @SuppressWarnings("unchecked")
    List<String> tagList = (List<String>) params[1];
    
    // get the group association list
    @SuppressWarnings("unchecked")
    final List<String> groupAssociationStrList = (List<String>) params[2];
    
    final boolean isAdministrator = (boolean) params[3];
    
    // check if user already exists
    User existingUser = userRepository.findByEmail(addUserForm.getEmail());
    if (existingUser != null) {
      if (user.getCompanyId().equals(existingUser.getCompanyId())) {
        throw new InvalidRequestException(
            MessagesHelper.getMessage("exception.add.member.exists.inCompany"));
      } else {
        throw new InvalidRequestException(MessagesHelper.getMessage("exception.add.member.exists"));
      }
    }
    
    // Capitalize the tag list
    if (tagList != null) {
      tagList = tagList.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
    }
    
    // checking if user is present in hiring
    HiringUser hiringUser = hiringUserFactory.getUserByEmail(addUserForm.getEmail(),
        user.getCompanyId());
    if (hiringUser == null) {
      // checking for archive
      hiringUser = hiringUserFactory.getArchiveUserByEmail(addUserForm.getEmail(),
          user.getCompanyId());
    }
    
    // add the user
    addMember(user, tagList, groupAssociationStrList, isAdministrator,
        addUserForm.getUserWithCompany(user.getCompanyId(), hiringUser));
    
    // change the status of the hiring user to
    // hired and moved to member
    if (hiringUser != null) {
      hiringUser.setInErti(true);
      if (hiringUser instanceof HiringUserArchive) {
        if (hiringUser.getType() == UserType.HiringCandidate) {
          hiringUser.removeRole(RoleType.HiringCandidate);
          hiringUser.addRole(RoleType.HiringEmployee);
          hiringUser.setType(UserType.Member);
        }
        hiringUserFactory.updateUser(hiringUser);
      } else {
        if (hiringUser.getType() == UserType.HiringCandidate) {
          HiringUser hiringEmployee = new HiringUser(hiringUser);
          hiringEmployee.setInErti(true);
          hiringUserFactory.updateUser(hiringEmployee);
          hiringUser.setHired(true);
          hiringUserFactory.archiveUser(user, hiringUser, false);
        } else {
          hiringUser.setInErti(true);
          hiringUserFactory.updateUser(hiringUser);
        }
      }
    }
    
    response.isSuccess();
    return response;
  }
  
  /**
   * Checks the user is present in the system.
   * 
   * @param email
   *          - user email
   * @param companyId
   *          - company id
   */
  private boolean checkUserExists(String email) {
    return (userRepository.findByEmail(email) != null);
  }
  
  /**
   * Adds a member to the company.
   * 
   * @param user
   *          - the logged in user
   * @param tagList
   *          - tag list for user
   * @param groupAssociationStrList
   *          - group list for the user
   * @param isAdministrator
   *          - is user administrator
   * @param userToAdd
   *          - user to add
   */
  public void addMember(User user, List<String> tagList, List<String> groupAssociationStrList,
      boolean isAdministrator, final User userToAdd) {
    
    // check if company has users to add
    Account companyAccount = accountRepository.findAccountByCompanyId(user.getCompanyId());
    boolean isSubscriptionReserved = false;
    if (companyAccount.getSpPlanMap().get(SPPlanType.Primary).getNumMember() < 1) {
      throw new InvalidRequestException("No subscriptions left for company :" + user.getCompanyId());
    } else {
      // reserve the subscription for the user
      companyAccount.reserveSubscritption(SPPlanType.Primary);
      accountRepository.updateAccount(companyAccount);
      isSubscriptionReserved = true;
    }
    
    // check if the user is an administrator for the company
    // if (isAdministrator) {
    // userToAdd.addRole(RoleType.Administrator);
    // }
    
    // add the tags for the user
    userToAdd.setTagList(tagList);
    
    /* add the locale settings for the new user */
    final Locale locale = user.getLocale();
    userToAdd.getProfileSettings().setLocale(locale);
    try {
      // create the user
      userRepository.createUser(userToAdd);
      
      // add the group associations for the user
      // group repository called because user has to be added
      // to the user group as well
      if (groupAssociationStrList != null) {
        groupAssociationStrList.stream().forEach(
            ga -> groupRepository.addMember(userToAdd, GroupAssociation.parse(ga)));
      }
      
    } catch (SPException e) {
      // remove the user if already created
      if (userToAdd.getId() != null) {
        userRepository.removeUser(userToAdd);
      }
      
      if (isSubscriptionReserved) {
        companyAccount.addSubscription(SPPlanType.Primary);
        accountRepository.updateAccount(companyAccount);
      }
      throw e;
    }
    
    // sending add member notification
    sendAddMemberNotification(user, userToAdd, NotificationType.AddMember);
  }
  
  private void sendAddMemberNotification(User user, final User userToAdd,
      NotificationType notificationType) {
    final Locale locale = userToAdd.getLocale();
    // create the token and get the token url
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_USER_ID, userToAdd.getId());
    paramsMap.put(Constants.PARAM_USER_EMAIL, userToAdd.getEmail());
    Token token = spTokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
        TokenProcessorType.ADD_MEMBER);
    
    // setting the token to the user
    userToAdd.setTokenUrl(token.getTokenUrl());
    userRepository.updateUser(userToAdd);
    
    /* Get the user company name, to be used in the email to be sent */
    // get the company and add any tasks from the company
    Company company = accountRepository.findCompanyById(user.getCompanyId());
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("hasName", userToAdd.getFirstName() != null);
    params.put(Constants.PARAM_COMPANY, company);
    params.put(Constants.PARAM_LOCALE, locale.toString());
    params.put(Constants.PARAM_SUBJECT, MessagesHelper.getMessage(
        Constants.NOTIFICATION_SUBJECT_PREFIX + notificationType, locale, company.getName()));
    
    notificationsProcessor.process(notificationType, user, userToAdd, params);
  }
  
  /**
   * Helper method to add multiple users to the system.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response for the add request
   */
  public SPResponse addMultiple(User user, Object[] params) {
    SPResponse response = new SPResponse();
    
    // get the user form to add
    @SuppressWarnings("unchecked")
    List<String> memberList = (List<String>) params[0];
    
    // get the tag list
    @SuppressWarnings("unchecked")
    List<String> tagListTemp = (List<String>) params[1];
    if (tagListTemp != null) {
      tagListTemp = tagListTemp.stream().map(WordUtils::capitalizeFully)
          .collect(Collectors.toList());
    }
    
    final List<String> tagList = tagListTemp;
    
    // get the group association list
    @SuppressWarnings("unchecked")
    List<String> groupAssociationStrList = (List<String>) params[2];
    
    List<String> successList = new ArrayList<String>();
    List<String> failureList = new ArrayList<String>();
    // add the members
    try {
      memberList.stream().forEach(
          memberEmail -> {
            memberEmail = org.springframework.util.StringUtils.trimWhitespace(memberEmail.toLowerCase());
            if (checkUserExists(memberEmail)) {
              failureList.add(memberEmail);
            } else {
              addMember(user, tagList, groupAssociationStrList, false,
                  new User(memberEmail, user.getCompanyId()));
              successList.add(memberEmail);
            }
          });
    } catch (SPException e) {
      response.addError(e);
    }
    response.add("successList", successList);
    response.add("failureList", failureList);
    return response;
  }
  
  /**
   * Validate the email list provided.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - list of email's
   * @return - the response to the validate command
   */
  public SPResponse validateEmails(User user, Object[] params) {
    SPResponse response = new SPResponse();
    
    // get the user form to add
    @SuppressWarnings("unchecked")
    List<String> memberList = (List<String>) params[0];
    
    List<String> successList = new ArrayList<String>();
    List<String> failureList = new ArrayList<String>();
    
    memberList.stream().forEach(memberEmail -> {
      memberEmail = org.springframework.util.StringUtils.trimWhitespace(memberEmail.toLowerCase());
      if (checkUserExists(memberEmail)) {
        failureList.add(memberEmail);
      } else {
        successList.add(memberEmail);
      }
    });
    response.add("successList", successList);
    response.add("failureList", failureList);
    
    // sending the response back
    return response;
  }
  
  /**
   * This the helper method to retrieve the list of members in the company the user is part of.
   * 
   * @param user
   *          - admin user
   * @return the response for the get member request
   */
  public SPResponse getMembers(User user) {
    SPResponse response = new SPResponse();
    // get all the members of the company
    List<User> memberList = accountRepository.getAllMembersForCompany(user.getCompanyId());
    // create and add the members to the DTO
    final GroupAssociation dummyGa = new GroupAssociation("");
    return response.add(
        Constants.PARAM_MEMBER_LIST,
        memberList.stream()
            .filter(usr -> !(usr.hasRole(RoleType.Hiring) && (usr.getRoles().size() <= 1)))
            .collect(Collectors.mapping(u -> getUserDTO(u, dummyGa), Collectors.toList())));
  }
  
  /**
   * Method to create a user dto object for the given user object.
   * 
   * @param user
   *          - logged in user
   * @param dummyGa
   *          - dummy group association
   * @return the user dto object
   */
  private UserDTO getUserDTO(User user, GroupAssociation dummyGa) {
    UserDTO userDTO = new UserDTO(user);
    // 12/12/2014 as requested by kunal adding a blank
    // group association for user if none exists
    if (userDTO.getGroupAssociationList().isEmpty()) {
      userDTO.addGroupAssociation(dummyGa);
    }
    // adding
    return userDTO;
  }
  
  /**
   * This is the helper method to delete a single user.
   * 
   * @param user
   *          - logged in user
   * @param args
   *          - the arguments
   * @return the response of the delete call
   */
  public SPResponse deleteMember(User user, Object[] args) {
    SPResponse resp = new SPResponse();
    
    // get the user to delete
    String userEmail = (String) args[0];
    
    // Deleting the given user
    delete(user, resp, userEmail);
    
    return resp.isSuccess();
  }
  
  /**
   * Deletes the list of members passed to the method.
   * 
   * @param user
   *          - logged in user
   * @param args
   *          - member list to delete
   * @return the response to the delete request
   */
  public SPResponse deleteMembers(User user, Object[] args) {
    SPResponse resp = new SPResponse();
    
    @SuppressWarnings("unchecked")
    Optional<List<String>> memberList = Optional.ofNullable((List<String>) args[0]);
    
    memberList.ifPresent(e -> e.forEach(memberEmail -> delete(user, resp, memberEmail)));
    
    return resp.hasErrors() ? resp : resp.isSuccess();
  }
  
  /**
   * Delete the user with the member email.
   * 
   * @param user
   *          - user
   * @param resp
   *          - response to update for error
   * @param memberEmail
   *          - member email
   */
  private void delete(User user, SPResponse resp, String memberEmail) {
    try {
      delete(user, memberEmail);
    } catch (SPException exp) {
      resp.addError(exp);
    }
  }
  
  /**
   * Helper method to delete the user.
   * 
   * @param user
   *          - logged in user
   * @param userEmail
   *          - user email to delete
   */
  private void delete(User user, String userEmail) {
    // check if the user exists
    User userToDelete = userRepository.findByEmailValidated(userEmail);
    
    // check the user if himself
    if (user.equals(userToDelete)) {
      throw new InvalidRequestException("Cannot delete yourself !!!");
    }
    
    // validate the company is same as the logged in users company
    user.isSameCompany(userToDelete);
    
    /*
     * check if the user to be deleted has role of account adminstrator and only account
     * adminstrator can deleted it *.
     */
    if (userToDelete.hasRole(RoleType.AccountAdministrator)) {
      
      /*
       * check if user who is deleted the has role fo accouut adminstrator or not
       */
      if (!user.getRoles().contains(RoleType.AccountAdministrator)) {
        throw new InvalidRequestException("Unauthroized request !!!");
      }
    }
    
    if (userToDelete.hasRole(RoleType.Hiring)) {
      userToDelete.getRoles().clear();
      userToDelete.addRole(RoleType.Hiring);
      // get the hiring user
      hiringUserFactory.handleErtiDelete(userEmail, userToDelete.getCompanyId());
      userFactory.updateUserAndSession(userToDelete, true);
    } else {
      // delete the user
      noteFactory.deleteNotes(userToDelete);
      userRepository.deleteUser(userToDelete);
      userActivtyRepository.deleteActivityTracking(userToDelete.getId());
      hiringUserFactory.handleErtiDelete(userEmail, userToDelete.getCompanyId());
    }
  }
  
  /**
   * Helper method to get all the distinct tags for the company.
   * 
   * @param user
   *          - user
   * @return the distinct tags in the company
   */
  public SPResponse getAllTags(User user) {
    return new SPResponse().add(Constants.PARAM_TAG_LIST,
        userRepository.getAllTags(user.getCompanyId()));
  }
  
  /**
   * <code>getAllValidMembers</code> will return all the valid members for the company.
   * 
   * @param user
   *          logged in user
   * @return the valid members
   */
  public SPResponse getAllValidMembers(User user, Object[] params) {
    
    boolean isAccountAdmin = (boolean) params[0];
    
    List<User> userList = userFactory.getAllBasicUserInfo(user.getCompanyId());
    
    // Check if account Admin. Else remove the logged in user from the user list
    if (!isAccountAdmin) {
      userList.remove(user);
    }
    
    /* filter the users who have not completed their assessment */
    List<BaseUserDTO> validUsers = userList.stream()
        .filter(usr -> usr.getUserStatus() == UserStatus.VALID).map(BaseUserDTO::new)
        .collect(Collectors.toList());
    
    return new SPResponse().add("allMembers", validUsers);
  }
  
  /**
   * This the helper method to retrieve the list of members which can be assigned to the company in
   * the company the user is part of.
   * 
   * @param user
   *          - admin user
   * @return the response for the get member request
   */
  public SPResponse getMembersForAdmins(User user, Object[] param) {
    SPPlanType planType = (SPPlanType) param[0];
    return getMembersForAdmin(user.getCompanyId(), planType);
  }
  
  /**
   * Common method to provide the get members for admin section.
   * 
   * @param companyId
   *          list of company id.
   * @param planType
   *          for the which members are to be fetched.
   * @return the members.
   */
  public SPResponse getMembersForAdmin(String companyId, SPPlanType planType) {
    Predicate<User> filterExistingUser = existingPlanAdminUser(planType);
    
    // get all the members of the company
    List<? extends User> memberList = null;
    
    switch (planType) {
    case IntelligentHiring:
      memberList = hiringUserFactory.getAll(companyId, UserType.Member);
      break;
    case Primary:
      memberList = userFactory.getAllByCompanyAndRole(companyId, RoleType.User);
      break;
    
    default:
      break;
    }
    // create and add the members to the DTO
    List<BaseUserDTO> memberDTOList = Optional.ofNullable(memberList)
        .orElse(Collections.emptyList()).stream().filter(filterExistingUser).map(BaseUserDTO::new)
        .collect(Collectors.toList());
    
    return new SPResponse().add(Constants.PARAM_MEMBER_LIST, memberDTOList);
  }
  
  /**
   * Filter to return the existing account administrators.
   * 
   * @return the existing users
   */
  private Predicate<User> existingPlanAdminUser(SPPlanType planType) {
    return usr -> (!usr.getRoles().contains(planType.getAdminRoleForPlan()) && !usr.isDeactivated());
  }
  
  /**
   * <code>addAdminMember</code> method will return all the accounts requested presne tin the
   * system.
   * 
   * @param user
   *          system adminsitrator user
   * @param param
   *          contains the type of accounts to be fetched.
   * @return the json response;
   */
  public SPResponse addAdminMember(User user, Object[] param) {
    
    String userEmail = (String) param[0];
    SPPlanType planType = (SPPlanType) param[1];
    
    return addAdminMember(userEmail, user.getCompanyId(), planType);
  }
  
  /**
   * <code>addAdminMember</code> method will add the admin role to the user.
   * 
   * @param userEmail
   *          for which admin role is to be passed.
   * @param companyId
   *          of the user.
   * @param planType
   *          for which admin role is to be added.
   * @return the response.
   */
  public SPResponse addAdminMember(String userEmail, String companyId, SPPlanType planType) {
    // check if the user exists
    User userToUpdate = userRepository.findByEmailValidated(userEmail, companyId);
    if (userToUpdate == null) {
      throw new InvalidRequestException("No user found for requeted email -" + userEmail);
    }
    /* check if available admin slots are availabel for that account */
    Account account = accountRepository.findValidatedAccountByCompanyId(companyId);
    
    SPPlan spPlan = account.getSpPlanMap().get(planType);
    int numAdmin = spPlan.getNumAdmin();
    if (numAdmin == 0) {
      throw new InvalidRequestException("Admin Plan is not subscribed for the company");
    }
    
    /* find existing admin users for the company */
    Set<SPPlanType> planSets = new HashSet<>();
    planSets.add(planType);
    Map<SPPlanType, List<User>> adminUserMap = accountRepository.findAdminsForPlans(planSets,
        companyId);
    int adminExistInSystem = adminUserMap.get(planType).size();
    
    if (adminExistInSystem >= spPlan.getNumAdmin()) {
      throw new InvalidRequestException(
          "All Admin Slots are filled!!. Please buy more license for admin slots ");
    }
    
    userToUpdate.addRole(planType.getAdminRoleForPlan());
    
    /* get all the company feature for the user and add the user roles for that feature */
    Set<SPFeature> featureList = spPlan.getFeatures();
    featureList.stream().flatMap(feature -> Arrays.stream(feature.getRoles()))
        .filter(r -> r.isAdminRole()).forEach(userToUpdate::addRole);
    userRepository.updateUser(userToUpdate);
    
    /*
     * List<User> expireSessionUser = new ArrayList<>(); expireSessionUser.add(userToUpdate);
     * managementService.expireSessionForUsers(expireSessionUser);
     */
    
    // update logged in user
    userFactory.updateUserAndSession(userToUpdate);
    
    LogRequest logRequest = new LogRequest(planType.getLogActionType(), userToUpdate, userToUpdate);
    logGateway.logNotification(logRequest);
    
    SPResponse spResponse = new SPResponse();
    return spResponse.isSuccess();
  }
  
  /**
   * <code>removeAdminMember</code> method will return all the accounts requested presne tin the
   * system.
   * 
   * @param user
   *          system adminsitrator user
   * @param param
   *          contains the type of accounts to be fetched.
   * @return the json response;
   */
  public SPResponse removeAdminMember(User user, Object[] param) {
    
    String userEmail = (String) param[0];
    SPPlanType planType = (SPPlanType) param[1];
    
    return removeAdminMember(userEmail, user.getCompanyId(), planType);
  }
  
  /**
   * remvoeAdminMember method will remove the admin for the plan.
   * 
   * @param userEmail
   *          from which admin role is to be removed.
   * @param companyId
   *          of the user
   * @param planType
   *          for which admin role is to be removed.
   * @return the response.
   */
  public SPResponse removeAdminMember(String userEmail, String companyId, SPPlanType planType) {
    
    // check if the user exists
    User userToUpdate = userRepository.findByEmailValidated(userEmail, companyId);
    if (userToUpdate == null) {
      throw new InvalidRequestException("No user found for requeted email -" + userEmail);
    }
    /* check if available admin slots are availabel for that account */
    Account account = accountRepository.findValidatedAccountByCompanyId(companyId);
    
    SPPlan spPlan = account.getSpPlanMap().get(planType);
    int numAdmin = spPlan.getNumAdmin();
    if (numAdmin == 0) {
      throw new InvalidRequestException("Admin Plan is not subscribed for the company");
    }
    
    /* find existing admin users for the company */
    Set<SPPlanType> planSets = new HashSet<>();
    planSets.add(planType);
    Map<SPPlanType, List<User>> adminUserMap = accountRepository.findAdminsForPlans(planSets,
        companyId);
    int adminExistInSystem = adminUserMap.get(planType).size();
    
    if (planType == SPPlanType.Primary && adminExistInSystem == 1) {
      throw new InvalidRequestException("At least one admin should be available for the account");
    }
    
    userToUpdate.removeRole(planType.getAdminRoleForPlan());
    
    /* get all the company feature for the user and add the user roles for that feature */
    CompanyDao company = companyFactory.getCompany(companyId);
    company.getFeatureList().stream().forEach(feat -> {
      if (ArrayUtils.contains(planType.getFeatures(), feat)) {
        for (RoleType role : feat.getRoles()) {
          if (role.isAdminRole()) {
            userToUpdate.removeRole(role);
          }
        }
      }
    });
    userRepository.updateUser(userToUpdate);
    /*
     * List<User> expireSessionUser = new ArrayList<>(); expireSessionUser.add(userToUpdate);
     * managementService.expireSessionForUsers(expireSessionUser);
     */
    // update logged in user
    userFactory.updateUserAndSession(userToUpdate);
    
    logsRepository.removeNotificationLogs(userToUpdate.getId(), planType.getLogActionType());
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to block the user or unblock the user.
   * 
   * @param user
   *          - user
   * @param param
   *          - params
   * @return the status for the block request
   */
  @SuppressWarnings("unchecked")
  public SPResponse blockUser(User user, Object[] param) {
    final SPResponse resp = new SPResponse();
    
    List<String> userList = (List<String>) param[0];
    final boolean blockUser = (boolean) param[1];
    
    Assert.notEmpty(userList, "No users found.");
    
    // also removing self if existing in the list
    userList.remove(user.getEmail());
    
    // get the list of users
    userList.stream().map(userFactory::getUserByEmail).filter(u -> u != null)
        .forEach(u -> blockUser(u, blockUser));
    
    return resp.isSuccess();
  }
  
  /**
   * Updates the status for the given user.
   * 
   * @param user
   *          - user to update
   * @param blockUser
   *          - user flag to update
   */
  private void blockUser(User user, boolean blockUser) {
    user.setDeactivated(blockUser);
    if (blockUser) {
      userFactory.updateUserAndSession(user, true);
    } else {
      userFactory.updateUser(user);
    }
  }
  
  /**
   * Helper method to get all the members in the given company.
   * 
   * @param companyId
   *          - company id
   * @return the list of members
   */
  public SPResponse getAllMembers(String companyId) {
    final SPResponse resp = new SPResponse();
    List<User> memberList = userFactory.getAllMembersForCompany(companyId);
    return resp.add(
        Constants.PARAM_MEMBER_LIST,
        memberList.stream().filter(u -> u.hasRole(RoleType.User))
            .collect(Collectors.mapping(UserRolesDTO::new, Collectors.toList())));
  }
  
  /**
   * getMemberLndCompDetails method will return the lnd details for the user.
   * 
   * @param user
   *          logged in user.
   * @param params
   *          contains the member email.
   * @return the spresponse.
   */
  public SPResponse getMemberLndCompDetails(User user, Object[] params) {
    
    String memberEmail = (String) params[0];
    Assert.notNull(memberEmail, "Invalid Request");
    
    User memberUser = userRepository.findByEmail(memberEmail);
    
    Assert.notNull(memberUser, "No user found for eamil " + memberEmail);
    
    PracticeCompOrgViewDTO compOrgViewDTO = new PracticeCompOrgViewDTO();
    compOrgViewDTO.setAutoLearning(memberUser.getProfileSettings().isAutoUpdateLearning());
    String userGoalId = memberUser.getUserGoalId();
    
    if (StringUtils.isNotBlank(userGoalId)) {
      UserGoalDao userGoal = goalFactory.getUserGoal(userGoalId, user.getUserLocale());
      
      if (userGoal != null) {
        
        List<UserGoalProgressDao> selectedGoalsProgressList = userGoal
            .getSelectedGoalsProgressList();
        
        List<BaseGoalDto> goals = selectedGoalsProgressList.stream().map(ugp -> {
          SPGoal goal = ugp.getGoal();
          return new BaseGoalDto(goal);
        }).collect(Collectors.toList());
        compOrgViewDTO.setGoals(goals);
      }
    }
    
    if (!StringUtils.isEmpty(memberUser.getCompetencyProfileId())) {
      CompetencyProfileDao competencyProfile = competencyFactory.getCompetencyProfile(memberUser
          .getCompetencyProfileId());
      compOrgViewDTO.setCompetencyProfile(new BaseCompetencyProfileDTO(competencyProfile));
    }
    
    if (StringUtils.isNotBlank(memberUser.getUserActionPlanId())) {
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(memberUser);
      Map<String, ActionPlanProgress> completeActionsMap = userActionPlan
          .getActionPlanProgressMap();
      final List<ActionPlanSummaryDTO> actionPlans = compOrgViewDTO.getOrCreateActionPlans();
      
      completeActionsMap.forEach((actId, actionPlanProgress) -> {
        ActionPlanDao actionPlan = actionPlanFactory.getActionPlan(actId);
        if (LOG.isDebugEnabled()) {
          LOG.debug("Action Plan Dao is " + actionPlan + ", actId " + actId);
        }
        if (actionPlan != null) {
          actionPlans.add(new ActionPlanSummaryDTO(actionPlan, actionPlanProgress
              .getCompletedCount()));
        }
      });
    }
    
    SPResponse response = new SPResponse();
    response.add("memberLndCompDetails", compOrgViewDTO);
    return response;
  }
  
  /**
   * Add a user from the details present in the hiring user.
   * 
   * @param user
   *          - user
   * @param hiringUser
   *          - hiring user
   * @return the new user created
   */
  public User addMemberFromHiring(User user, HiringUser hiringUser) {
    // checking if the hiring user already exists
    User userToAdd = userFactory.getUserByEmail(hiringUser.getEmail());
    if (userToAdd != null) {
      // check if the existing user is an hiring administrator
      if (!userToAdd.hasRole(RoleType.Hiring)) {
        throw new IllegalArgumentException(MessagesHelper.getMessage("error.add.check.erti.user",
            user.getLocale()));
      } else {
        userToAdd.addRole(RoleType.User);
        userFactory.updateUserAndSession(userToAdd);
        // sending add member notification
        sendAddMemberNotification(user, userToAdd, NotificationType.AddMember);
      }
    } else {
      userToAdd = new User(hiringUser);
      userToAdd.addRole(RoleType.User);
      userToAdd.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
      addMember(user, null, null, false, userToAdd);
    }
    
    hiringUser.setInErti(true);
    hiringUserFactory.updateUser(hiringUser);
    return userToAdd;
  }
  
  /**
   * Add a hiring administrator.
   * 
   * @param user
   *          - user
   * @param hiringUser
   *          - hiring user
   */
  public void addHiringAdmin(User user, HiringUser hiringUser) {
    User existingUser = null;
    User newUser = null;
    final String companyId = user.getCompanyId();
    List<User> users = userRepository.findByCompanyAndRole(companyId, RoleType.Hiring);
    Account account = accountRepository.findAccountByCompanyId(companyId);
    SPPlan plan = account.getPlan(SPPlanType.IntelligentHiring);
    Assert.isTrue(plan.getNumAdmin() > users.size(),
        "People Analytics administrator subscriptions exhausted.");
    
    try {
      // check if user exists
      existingUser = userFactory.getUserByEmail(hiringUser.getEmail());
      if (existingUser != null) {
        // check if the existing user is already hiring admin
        
        Assert.isTrue(existingUser.isSameCompany(hiringUser),
            "User already part of some other company");
        Assert.isTrue(!existingUser.hasRole(RoleType.Hiring), "User already administrator.");
        // adding the hiring admin role to the user
        existingUser.addRole(RoleType.Hiring);
        sendAddMemberNotification(user, existingUser, NotificationType.AddPAAdminMemberErtiUser);
        userFactory.updateUserAndSession(existingUser);
      } else {
        newUser = new User(hiringUser);
        newUser.addRole(RoleType.Hiring);
        newUser.setUserStatus(UserStatus.PROFILE_INCOMPLETE);
        newUser.getProfileSettings().setLocale(user.getLocale());
        userFactory.updateUser(newUser);
        sendAddMemberNotification(user, newUser, NotificationType.AddPAAdminMember);
      }
      
    } catch (Exception e) {
      LOG.warn("Error adding hiring admin.", e);
      throw e;
    }
    hiringUser.addRole(RoleType.Hiring);
    hiringUserFactory.updateUser(hiringUser);
  }
  
  /**
   * Remove the user from hiring administration.
   * 
   * @param hiringUser
   *          - hiring user
   */
  public void removeHiringAdmin(HiringUser hiringUser) {
    
    // check if there are at least 1 administrator
    List<User> users = userRepository.findByCompanyAndRole(hiringUser.getCompanyId(),
        RoleType.Hiring);
    Assert.isTrue(users.size() > 1, "At least one administrator required.");
    
    // get the admin user
    User existingUser = userFactory.getUserByEmail(hiringUser.getEmail());
    Assert.notNull(existingUser, "User not found.");
    
    existingUser.removeRole(RoleType.Hiring);
    if (!existingUser.hasRole(RoleType.User)) {
      // deleting the user from the DB
      userFactory.removeUser(existingUser);
    } else {
      userFactory.updateUser(existingUser);
    }
    hiringUser.removeRole(RoleType.Hiring);
    hiringUserFactory.updateUser(hiringUser);
    
  }
}
