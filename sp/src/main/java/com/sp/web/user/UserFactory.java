package com.sp.web.user;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.controller.hiring.HiringFactory;
import com.sp.web.dao.competency.CompetencyProfileDao;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.FeedbackUserForm;
import com.sp.web.model.Account;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.Token;
import com.sp.web.model.TrackingType;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.badge.BadgeType;
import com.sp.web.model.blueprint.Blueprint;
import com.sp.web.model.goal.UserActionPlan;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tracking.SP360RequestTracking;
import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.archive.ArchiveRepository;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.repository.goal.UserGoalsRepository;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.log.LogsRepository;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.blueprint.BlueprintFactory;
import com.sp.web.service.external.rest.PartnerFactory;
import com.sp.web.service.goals.ActionPlanFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.service.tracking.ActivityTrackingHelper;
import com.sp.web.service.tutorial.SPTutorialFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The factory method for all user functionality.
 */
@Component
public class UserFactory {
  
  private static final Logger log = Logger.getLogger(UserFactory.class);
  
  @Autowired
  @Lazy
  UserRepository userRepository;
  
  @Autowired
  @Lazy
  HiringRepository hiringRepository;
  
  @Autowired
  @Lazy
  private SPGoalFactory spGoalFactory;
  
  @Autowired
  @Lazy
  HiringFactory hiringFactory;
  
  @Autowired
  @Lazy
  TrackingRepository trackingRepository;
  
  @Autowired
  @Lazy
  LoginHelper loginHelper;
  
  @Autowired
  @Lazy
  private SPGoalFactoryHelper goalFactoryHelper;
  
  @Autowired
  @Lazy
  SPTokenFactory tokenFactory;
  
  @Autowired
  @Lazy
  private GroupRepository groupsRepository;
  
  @Autowired
  @Lazy
  private AccountRepository accountRepository;
  
  @Autowired
  @Lazy
  private LogsRepository logsRepository;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private SPNoteFeedbackRepository noteFeedbackRepository;
  
  @Autowired
  @Lazy
  private UserGoalsRepository userGoalsRepository;
  
  @Autowired
  @Lazy
  private CompetencyFactory competencyFactory;
  
  @Autowired
  @Lazy
  private ActionPlanFactory actionPlanFactory;
  
  @Autowired
  @Lazy
  private BlueprintFactory blueprintFactory;
  
  @Autowired
  @Lazy
  private WorkspacePulseRepository pulseRepository;
  
  @Autowired
  @Lazy
  private ActivityTrackingHelper activityTrackingHelper;
  
  @Autowired
  @Lazy
  private TodoFactory todoFactory;
  
  @Autowired
  @Lazy
  private EventGateway eventGateway;
  
  @Autowired
  @Lazy
  private SPTutorialFactory tutorialFactory;
  
  private String defaultTutorialId;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  @Autowired
  @Lazy
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  @Lazy
  private ArchiveRepository archiveRepository;
  
  @Autowired
  private PartnerFactory partnerFactory;
  
  /**
   * Constructor.
   * 
   * @param environment
   *          - environment
   */
  @Inject
  public UserFactory(Environment environment) {
    String property = environment.getProperty(Constants.DEFAULT_TUTORIAL_ID_KEY);
    if (!StringUtils.isBlank(property)) {
      defaultTutorialId = property;
    }
  }
  
  /**
   * Get the user to copy profile from.
   * 
   * @param user
   *          - user
   * @param copyFromEmail
   *          - copy from email
   * @return the user to copy the profile from
   */
  public User getCopyProfileFromUser(User user, String copyFromEmail) {
    
    // check for individual user account
    User existingUser = userRepository.findByEmailAndRole(copyFromEmail,
        RoleType.IndividualAccountAdministrator);
    if (existingUser != null && existingUser.getAnalysis() != null) {
      // returning the user found
      return existingUser;
    }
    
    // check for hiring candidate
    List<HiringUser> hiringMemberList = hiringRepository.findByEmail(copyFromEmail);
    if (!hiringMemberList.isEmpty()) {
      // if user exists get the user that was created last
      existingUser = hiringMemberList.get(0);
      for (HiringUser hiringUser : hiringMemberList) {
        if (hiringUser.getCreatedOn().isAfter(existingUser.getCreatedOn())) {
          existingUser = hiringUser;
        }
      }
      if (existingUser.getAnalysis() != null) {
        return existingUser;
      }
      existingUser = null;
    }
    
    // check for archived candidates
    List<HiringUserArchive> hiringMemberArchiveList = hiringRepository
        .findByEmailHiringArchive(copyFromEmail);
    if (!hiringMemberArchiveList.isEmpty()) {
      // if user exists get the user that was created last
      existingUser = hiringMemberArchiveList.get(0);
      for (HiringUserArchive hiringUser : hiringMemberArchiveList) {
        if (hiringUser.getCreatedOn().isAfter(existingUser.getCreatedOn())) {
          existingUser = hiringUser;
        }
      }
      if (existingUser.getAnalysis() != null) {
        return existingUser;
      }
      existingUser = null;
    }
    
    // check for member list
    if (!(user instanceof HiringUser)) {
      List<User> memberList = userRepository.findAllMembersByEmail(copyFromEmail);
      if (!memberList.isEmpty()) {
        for (User member : memberList) {
          if (member.getCompanyId() != null) {
            existingUser = member;
          }
        }
        if (existingUser.getAnalysis() != null) {
          return existingUser;
        }
        existingUser = null;
      }
    }
    return existingUser;
  }
  
  /**
   * Do the profile copy.
   * 
   * @param user
   *          - user to copy to
   * @param userType
   *          - user type of the user
   * @param copyFromEmail
   *          - copy from the profile
   * @return the updated user
   */
  public User doCopyProfileFromUser(User user, UserType userType, String copyFromEmail) {
    User existingUser = getCopyProfileFromUser(user, copyFromEmail);
    
    // if user not found to copy from
    if (existingUser == null) {
      throw new InvalidRequestException("User not found for email :" + copyFromEmail);
    }
    
    if (user.getAnalysis() != null) {
      throw new InvalidRequestException("A valid SurePeople profile already exists for the user :"
          + copyFromEmail);
    }
    
    updateAnalysisAndInitializeUser(user, existingUser.getAnalysis());
    return user;
  }
  
  /**
   * Set the user analysis and update the other user parameters.
   * 
   * @param user
   *          - user
   * @param analysis
   *          - analysis
   */
  public void updateAnalysisAndInitializeUser(User user, AnalysisBean analysis) {
    
    User dbUser = userRepository.findGenericUserById(user.getId(), user);
    if (dbUser != null) {
      dbUser.setAnalysis(analysis);
      dbUser.setUserStatus(UserStatus.VALID);
      dbUser.setRemindedOn(null);
      userRepository.updateGenericUser(dbUser);
      postAnalysisProcessing(dbUser);
      loginHelper.updateUser(dbUser);
    }
  }
  
  /**
   * This method is invoked post analysis processing.
   * 
   * @param user
   *          - user
   */
  @Async
  private void postAnalysisProcessing(User user) {
    
    if (user instanceof HiringUser) {
      // user instance of regular user
      Account account = accountRepository.findAccountByCompanyId(user.getCompanyId());
      if (account != null && account.getPlan(SPPlanType.Primary) != null) {
        // check if there is a user existing
        User userByEmail = getUserByEmail(user.getEmail());
        if (userByEmail != null && userByEmail.getCompanyId().equals(user.getCompanyId())) {
          final UserStatus userStatus = userByEmail.getUserStatus();
          if (userStatus != UserStatus.VALID) {
            if (userByEmail.getUserStatus() == UserStatus.PROFILE_INCOMPLETE) {
              userByEmail.updateProfile(user);
              updateUser(userByEmail);
            } else {
              userByEmail.setAnalysis(user.getAnalysis());
              userByEmail.setUserStatus(UserStatus.VALID);
              updateUser(userByEmail);
              initializeUser(userByEmail);
            }
          }
          HiringUser hiringUser = ((HiringUser) user);
          hiringUser.setInErti(true);
          hiringUserFactory.updateUser(hiringUser);
        }
      }
      
      /* update to the partner status and upload the results */
      if (((HiringUser) user).isThirdParty()) {
        partnerFactory.updatePartner(user, ActionType.PartnerPrismStatus,
            ActionType.PartnerPrismResult);
      }
    } else {
      
      // initialize the user
      initializeUser(user);
      
      // update the auto learning setting, once user has completed the assessment.
      // userRepository.updateAutoLearning(user, Boolean.TRUE);
      if (user instanceof FeedbackUser) {
        FeedbackUser fbUser = (FeedbackUser) user;
        if (fbUser.getFeatureType() == FeatureType.PrismLens) {
          String userId = fbUser.getFeedbackFor();
          User userFor = userRepository.findUserById(userId);
          try {
            goalFactoryHelper.addFeedbackUserGoals(fbUser, userFor);
          } catch (Exception e) {
            log.warn("Unable to set the feedback user goals.", e);
          }
          final String companyId = user.getCompanyId();
          if (StringUtils.isNotBlank(companyId)) {
            SP360RequestTracking requestTracking = new SP360RequestTracking(userId, false,
                TrackingType.COMPLETED);
            requestTracking.setCompanyId(companyId);
            trackingRepository.storeTrackingInfomation(requestTracking);
            // adding tracking for activity logs
            Object[] args = new Object[1];
            args[0] = user;
            activityTrackingHelper.trackActivity(userFor, LogActionType.FeedbackCompleted, args);
            if (user.getType() == UserType.Member
                && fbUser.getFeatureType() == FeatureType.PrismLens) {
              User member = getUser(fbUser.getMemberId());
              if (member != null) {
                todoFactory.remove(member, fbUser.getId());
              }
            }
          }
        }
      } else {
        // user instance of regular user
        Account account = accountRepository.findAccountByCompanyId(user.getCompanyId());
        if (account != null && account.getPlan(SPPlanType.IntelligentHiring) != null) {
          hiringUserFactory.addFromErti(user, account.getPlan(SPPlanType.Primary) != null);
        }
      }
    }
  }
  
  private void initializeUser(User user) {
    if (!(user instanceof FeedbackUser)) {
      // create the certificate for the user
      userRepository.updateUserCertificate(user);
      user.getProfileSettings();
      // adding the default program
      addDefaultTutorial(user);
    }
    /* get the user goals for the user */
    spGoalFactory.addGoalsForUser(user);
    // select all the mandatory goals for the user
    spGoalFactory.enableMandatoryGoals(user);
  }
  
  /**
   * Adding the SurePeople Introduction program to the user.
   * 
   * @param user
   *          - user
   */
  private void addDefaultTutorial(User user) {
    if (defaultTutorialId != null) {
      try {
        UserTutorialActivity userTutorialActivity = tutorialFactory.getUserTutorialActivity(user);
        userTutorialActivity.addTutorial(defaultTutorialId);
        tutorialFactory.save(userTutorialActivity);
        
        /* assign the badge for tutorial */
        badgeFactory.addToBadgeProgress(user, defaultTutorialId, BadgeType.Tutorial);
      } catch (Exception e) {
        log.warn("Unable to set Default SurePeople Tutorial.", e);
      }
    }
  }
  
  /**
   * Method to update the user in the currently logged in session.
   * 
   * @param id
   *          - user id
   */
  public void updateUserInSession(String id) {
    Assert.hasText(id, "User id required.");
    loginHelper.updateUser(userRepository.findUserById(id));
  }
  
  /**
   * Updates the user in the repository.
   * 
   * @param user
   *          - user to update
   */
  public void updateUser(User user) {
    userRepository.updateUser(user);
  }
  
  /**
   * Updates the user in the repository and also in the session.
   * 
   * @param user
   *          - user to update
   */
  public void updateUserAndSession(User user) {
    updateUserAndSession(user, false);
  }
  
  /**
   * Updates the user in the repository and also in the session.
   * 
   * @param user
   *          - user to update
   * @param doLogout
   *          - flag to logout users
   */
  public void updateUserAndSession(User user, boolean doLogout) {
    userRepository.updateUser(user);
    Map<String, Object> actionParams = new HashMap<String, Object>();
    actionParams.put(Constants.DO_LOGOUT, doLogout);
    MessageEventRequest messageEventRequest = MessageEventRequest.newEvent(
        ActionType.UpdatePermission, actionParams, user);
    eventGateway.sendEvent(messageEventRequest);
  }
  
  /**
   * Get the user to process either the member email or the existing user.
   * 
   * @param memberEmail
   *          - member email
   * @param user
   *          - user
   * @return the user to process
   */
  public User getUserToProcess(String memberEmail, User user) {
    if (StringUtils.isBlank(memberEmail)) {
      return user;
    }
    return getUserForGroupLead(memberEmail, user);
  }
  
  /**
   * Gets the user for the given group lead. It validates if the user is a group lead and the user
   * requested belongs to the same group the user is group lead of.
   * 
   * @param userEmail
   *          - user email
   * @param groupLeadUser
   *          - group lead user
   * @return the validated user
   */
  public User getUserForGroupLead(String userEmail, User groupLeadUser) {
    
    boolean isAdmin = isAdminUser(groupLeadUser);
    
    // get a list of groups the user is group lead of
    List<GroupAssociation> groupLeadGroupAssociations = null;
    
    if (!isAdmin) {
      groupLeadGroupAssociations = groupLeadUser.getGroupAssociationList().stream()
          .filter(GroupAssociation::isGroupLead).collect(Collectors.toList());
      
      Assert.notEmpty(groupLeadGroupAssociations, "User not group lead.");
    }
    
    // get the user for the given user id
    User user = userRepository.findByEmail(userEmail);
    
    // validate if the user is present and from same company
    Assert.notNull(user, "User not found.");
    
    groupLeadUser.isSameCompany(user);
    
    if (!isAdmin) {
      // validate the group association
      validateGroupAssociation(groupLeadGroupAssociations, user);
    }
    
    return user;
  }
  
  /**
   * Validate the group association of the user.
   * 
   * @param groupLeadGroupAssociations
   *          - group association of group lead
   * @param user
   *          - user to validate
   */
  private void validateGroupAssociation(List<GroupAssociation> groupLeadGroupAssociations, User user) {
    Optional<GroupAssociation> findFirst = user.getGroupAssociationList().stream()
        .filter(ga -> groupLeadGroupAssociations.contains(ga)).findFirst();
    
    Assert.isTrue(findFirst.isPresent(), "User not in group lead");
  }
  
  /**
   * Check if the given user is an administrator user.
   * 
   * @param user
   *          - user
   * @return true if the user is an administrator else false
   */
  private boolean isAdminUser(User user) {
    return user.hasAnyRole(RoleType.AccountAdministrator, RoleType.Administrator);
  }
  
  /**
   * Get the feedback user for the given feedback user id.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the feedback user
   */
  public FeedbackUser getFeedbackUser(String feedbackUserId) {
    return userRepository.findFeedbackUser(feedbackUserId);
  }
  
  /**
   * Helper method to get the feedback user for the given feedback user form.
   * 
   * @param email
   *          - user email
   * @param featureType
   *          - feature type
   * @param companyId
   *          - company id
   * 
   * @param feedbackUserForm
   *          - feedback user form
   */
  public FeedbackUser getFeedbackUser(String email, FeatureType featureType, String companyId) {
    return userRepository.findFeedbackUser(email, featureType, companyId);
  }
  
  /**
   * Create a feedback user or get from db.
   * 
   * @param feedbackUserForm
   *          - feedback user form
   * @param featureType
   *          - feature type
   * @param companyId
   *          - company id
   * @return the feedback user
   */
  public FeedbackUser getOrCreateFeedbackUser(FeedbackUserForm feedbackUserForm,
      FeatureType featureType, String companyId) {
    FeedbackUser user = null;
    final String existingUserEmail = feedbackUserForm.getExistingUserEmail();
    if (!StringUtils.isBlank(existingUserEmail)) {
      user = getFeedbackUser(existingUserEmail, featureType, companyId);
      if (user == null) {
        // creating new feedback user
        User member = userRepository.findByEmailValidated(existingUserEmail);
        user = new FeedbackUser();
        user.updateFrom(member);
        user.setFeatureType(featureType);
        userRepository.updateUser(user);
      }
    } else {
      final String userEmail = feedbackUserForm.getEmail();
      Assert.hasText(userEmail, "Email is required.");
      user = getFeedbackUser(userEmail, featureType, companyId);
      if (user == null) {
        user = new FeedbackUser(UserType.External, featureType);
        user.setEmail(userEmail);
        user.setCompanyId(companyId);
      }
      user.updateFrom(feedbackUserForm);
      userRepository.updateUser(user);
    }
    return user;
  }
  
  /**
   * Get the feedback user DTO.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the feedback user DTO
   */
  public BaseUserDTO getFeedbackUserDTO(String feedbackUserId) {
    FeedbackUser feedbackUser = getFeedbackUser(feedbackUserId);
    return (feedbackUser != null) ? new BaseUserDTO(feedbackUser) : null;
  }
  
  /**
   * Get the user for the given user id.
   * 
   * @param userId
   *          - user id
   * @return the user for the given user id
   */
  public User getUser(String userId) {
    return userRepository.findUserById(userId);
  }
  
  /**
   * Generic method to remove the user.
   * 
   * @param user
   *          - user to remove
   * @return true if the user is removed
   */
  public boolean removeUser(User user) {
    return userRepository.removeUser(user);
  }
  
  /**
   * Helper method to remove all the feedback users for the blueprint.
   * 
   * @param user
   *          - user
   * @param blueprint
   *          - blueprint
   * @param tokenInvalidationReason
   *          - token invalidation reason
   */
  public void removeAllFeedbackUsers(User user, Blueprint blueprint, String tokenInvalidationReason) {
    List<FeedbackUser> fbUserList = userRepository.findAllFeedbackUsersByFeedbackForAndFeature(
        user.getId(), FeatureType.Blueprint);
    // remove all the feedback users
    fbUserList.stream().forEach(fbUser -> deleteFeedbackUser(tokenInvalidationReason, fbUser));
  }
  
  /**
   * Remove all the feedback users for the given feature type and company.
   * 
   * @param featureType
   *          - feature type
   * @param companyId
   *          - company id
   */
  public void removeAllFeedbackUsers(FeatureType featureType, String companyId) {
    userRepository.removeAllFeedbackUsers(featureType, companyId);
  }
  
  /**
   * Delete the given feedback user.
   * 
   * @param tokenInvalidationReason
   *          - deletion reason
   * @param fbUser
   *          - feedback user
   */
  private void deleteFeedbackUser(String tokenInvalidationReason, FeedbackUser fbUser) {
    userRepository.deleteFeedbackUser(fbUser);
    Token token = tokenFactory.findTokenById(fbUser.getTokenId());
    if (token != null) {
      token.invalidate(tokenInvalidationReason);
      tokenFactory.persistToken(token);
    }
    if (fbUser.getType() == UserType.Member) {
      todoFactory.remove(fbUser, fbUser.getId());
    }
  }
  
  /**
   * delete the feedback user.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  public void deleteFeedbackUser(FeedbackUser feedbackUser) {
    userRepository.deleteFeedbackUser(feedbackUser);
  }
  
  /**
   * Method to add the given role to all the account administrators in the company.
   * 
   * @param roleType
   *          - the role to add
   */
  public void addRoleToAdmins(String companyId, RoleType roleType) {
    Assert.hasText(companyId, "Company id is required.");
    
    // get all the account administrators
    List<User> users = userRepository
        .findByCompanyAndRole(companyId, RoleType.AccountAdministrator);
    
    // adding the role to the administrator
    for (User usr : users) {
      usr.addRole(roleType);
      userRepository.updateUser(usr);
    }
  }
  
  /**
   * Method to remove the role for all the account administrators of a company.
   * 
   * @param companyId
   *          - company id
   * @param roleType
   *          - role type
   */
  public void removeRoleFromAdmin(String companyId, RoleType roleType) {
    Assert.hasText(companyId, "Company id is required.");
    
    // get all the account administrators
    List<User> users = userRepository
        .findByCompanyAndRole(companyId, RoleType.AccountAdministrator);
    
    // adding the role to the administrator
    for (User usr : users) {
      usr.removeRole(roleType);
      userRepository.updateUser(usr);
    }
  }
  
  /**
   * Get all the users with the given competency profile id.
   * 
   * @param competencyProfileId
   *          - competency profile id
   * @return list of users
   */
  public List<User> getAllMembersWithCompetencyProfile(String competencyProfileId) {
    return userRepository.findAllMembersByCompetencyProfileId(competencyProfileId);
  }
  
  /**
   * Gets the list of members in the group.
   * 
   * @param groupId
   *          - group id
   * @return the list of members
   */
  public List<User> getAllGroupMembers(String groupId) {
    List<User> groupUserList = new ArrayList<User>();
    UserGroup findById = groupsRepository.findById(groupId);
    if (findById != null) {
      findById.getMemberList().stream().forEach(m -> groupUserList.add(getUserByEmail(m)));
      final String groupLeadId = findById.getGroupLead();
      if (groupLeadId != null) {
        groupUserList.add(getUserByEmail(groupLeadId));
      }
    }
    return groupUserList;
  }
  
  /**
   * Gets the user for the given email id.
   * 
   * @param email
   *          - user email
   * @return the user
   */
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }
  
  /**
   * Gets all the members for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the list of members for the company
   */
  public List<User> getAllMembersForCompany(String companyId) {
    return accountRepository.getAllMembersForCompany(companyId);
  }
  
  public List<FeedbackUser> getFeedbackUsersForCompany(String companyId) {
    return userRepository.findAllFeedbackUsersByCompany(companyId);
  }
  
  /**
   * cleanUser will clean the user and will reset everything from the user. It will reset, Notes,
   * notification, prims lens, activityes, feedbacks, groups, user goal progress from the user.
   * 
   * @param user
   *          which needs to be cleaned.
   */
  public void cleanUser(User user, User existingUser) {
    if (!CollectionUtils.isEmpty(existingUser.getGroupAssociationList())) {
      existingUser.getGroupAssociationList().stream()
          .forEach(uga -> removeGroupAssociation(user, existingUser, uga));
      user.getGroupAssociationList().clear();
    }
    
    /* Clear notification and activities for the user */
    logsRepository.removeActivityLogs(existingUser.getId());
    logsRepository.removeNotificationLogs(existingUser.getId());
    
    /* clear the prims lens request */
    feedbackRepository.getAllArchivedFeedbackRequests(existingUser.getId()).stream()
        .forEach(fbAr -> {
          feedbackRepository.removeFeedbacArchivedRequest((FeedbackArchiveRequest) fbAr);
          archiveRepository.archive(fbAr);
        });
    feedbackRepository.getAllFeedbackUsers(existingUser.getId()).stream().forEach(fbUser -> {
      feedbackRepository.remove(fbUser);
      archiveRepository.archive(fbUser);
    });
    feedbackRepository.getAllFeedbackRequest(existingUser.getId()).stream().forEach(fbR -> {
      feedbackRepository.removeFeedbackRequest(fbR);
      archiveRepository.archive(fbR);
    });
    userRepository.findFeedbackUsers(existingUser.getId(), FeatureType.PrismLens).stream()
        .forEach(fbU -> {
          feedbackRepository.remove(fbU);
          archiveRepository.archive(fbU);
        });
    feedbackRepository.getAllFeedbackUserArchive(existingUser).stream().forEach(faArcUser -> {
      feedbackRepository.removeFeedbackUserArchive(faArcUser);
      archiveRepository.archive(faArcUser);
    });
    
    /* clear the notes and feedback for the user */
    noteFeedbackRepository.removeAllNotesAndFeeback(existingUser.getId());
    
    /* clear the user goals */
    userGoalsRepository.remove(existingUser.getUserGoalId());
    
    /** clear the bookmarks. */
    trackingRepository.removeAllBoookMarksTracking(existingUser.getId());
    
    /* clear the competency */
    if (StringUtils.isNotEmpty(existingUser.getCompetencyProfileId())) {
      CompetencyProfileDao competencyProfile = competencyFactory.getCompetencyProfile(user
          .getCompetencyProfileId());
      competencyFactory.deleteCompetencyProfile(competencyProfile, true);
    }
    user.setCompetencyProfileId(null);
    
    /* clear the org plan */
    if (StringUtils.isNotEmpty(existingUser.getUserActionPlanId())) {
      UserActionPlan userActionPlan = actionPlanFactory.getUserActionPlan(existingUser);
      actionPlanFactory.deleteUserActionPlan(userActionPlan);
    }
    user.setUserActionPlanId(null);
    
    /* Clear the task for the pulse. */
    user.getTaskList().clear();
    
    /* remove the old pulse resut for the user */
    pulseRepository.getAllPulseAssessmentsForUser(existingUser.getId()).stream()
        .forEach(pulseRepository::delete);
    
    /* clear the blue print */
    if (StringUtils.isNotEmpty(existingUser.getBlueprintId())) {
      Blueprint validBlueprint = spGoalFactory.getValidBlueprint(existingUser);
      spGoalFactory.removeBlueprint(validBlueprint);
      blueprintFactory.removeBlueprintBackupForBlueprintId(existingUser.getBlueprintId());
    }
    user.setBlueprintId(null);
    user.setUserGoalId(null);
    if (!CollectionUtils.isEmpty(user.getRoles())) {
      user.getRoles().removeIf(
          rol -> !(rol == RoleType.User || rol == RoleType.AccountAdministrator));
      
    } else {
      user.addRole(RoleType.User);
    }
    // deleting all the competency data
    competencyFactory.deleteUser(user);
  }
  
  private void removeGroupAssociation(User user, User existingUser, GroupAssociation uga) {
    UserGroup userGroup = groupsRepository.findById(uga.getGroupId());
    if (user.getEmail().equalsIgnoreCase(userGroup.getGroupLead())) {
      groupsRepository.removeMember(existingUser.getCompanyId(), uga.getName(),
          existingUser.getEmail(), true);
    } else {
      groupsRepository.removeMember(existingUser.getCompanyId(), uga.getName(),
          existingUser.getEmail(), false);
    }
  }
  
  /**
   * Add the given message to the user.
   * 
   * @param user
   *          - user
   * @param feature
   *          - feature
   * @param message
   *          - message
   */
  public void addMessage(User user, SPFeature feature, String message) {
    user.addMessage(feature, message);
    updateUserAndSession(user);
  }
  
  /**
   * getSysPermissionUsers will return all the user who has role of
   * {@link RoleType#SysAdminMemberRole}.
   * 
   * @return the sysPermission Users
   */
  public List<User> getSysPermissionUsers() {
    return userRepository.findSysPermissionUsers();
  }
  
  /**
   * Get all the users for the company with the given role.
   * 
   * @param companyId
   *          - company id
   * @param userRoles
   *          - user roles
   * @return the list of users
   */
  public List<User> getAllMembersWithRole(String companyId, List<RoleType> userRoles) {
    List<User> allMembersForCompany = getAllMembersForCompany(companyId);
    RoleType[] roles = userRoles.toArray(new RoleType[0]);
    return allMembersForCompany.stream().filter(u -> u.hasAnyRole(roles))
        .collect(Collectors.toList());
  }
  
  /**
   * Get all the feedback users with the given feature type in the company.
   * 
   * @param type
   *          - feature type
   * @param companyId
   *          - company
   * @return the list of feedback users
   */
  public List<FeedbackUser> getAllFeedbackUsers(FeatureType type, String companyId) {
    return userRepository.findAllFeedbackUsers(type, companyId);
  }
  
  /**
   * Get all the feedback users for the given feature in the company with the member id.
   * 
   * @param type
   *          - type
   * @param companyId
   *          - company id
   * @param feedbackFor
   *          - feedback for id
   * @return the list of feedback users
   */
  public List<FeedbackUser> getAllFeedbackUsers(FeatureType type, String companyId,
      String feedbackFor) {
    return userRepository.findAllFeedbackUsers(type, companyId, feedbackFor);
  }
  
  /**
   * Remove all the blueprint requests for the given user.
   * 
   * @param companyId
   *          - company id
   * @param memberId
   *          - member id
   */
  public void removeBlueprintRequests(String companyId, String memberId) {
    List<FeedbackUser> feedbackUsers = getAllFeedbackUsers(FeatureType.Blueprint, companyId,
        memberId);
    feedbackUsers.forEach(todoFactory::remove);
  }
  
  public List<User> findUserByName(String value, String companyId) {
    return userRepository.findUserByName(value, companyId);
  }
  
  /**
   * Method to get all the users for the given user ids.
   * 
   * @param userIds
   *          - user ids
   * @return the list of members
   */
  public List<User> getUsers(List<String> userIds) {
    return userRepository.findAllUsers(userIds);
  }
  
  public List<User> getAllByCompanyAndRole(String companyId, RoleType role) {
    return userRepository.findByCompanyAndRole(companyId, role);
  }
  
  /**
   * Get the count of users with competency profile set.
   * 
   * @param companyId
   *          - company id
   * @return the count of people
   */
  public long getCompetencyUserCount(String companyId) {
    return userRepository.findCompetencyUserCount(companyId);
  }
  
  public List<User> getAllUsersWithCompetencyProfile(String companyId) {
    return userRepository.findAllUsersWithCompetencyProfile(companyId);
  }
  
  public List<User> getAllBasicUserInfo(String companyId) {
    return userRepository.getAllBasicUserInfo(companyId);
  }
  
  public HiringUser getHiringUser(String id) {
    return hiringUserFactory.getUser(id);
  }
}
