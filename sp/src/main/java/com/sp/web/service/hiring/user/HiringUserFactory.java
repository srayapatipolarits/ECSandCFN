package com.sp.web.service.hiring.user;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.controller.admin.member.MemberControllerHelper;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.product.AccountFactory;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.external.ThirdPartyRepository;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.service.external.rest.PartnerFactory;
import com.sp.web.service.hiring.group.HiringGroupFactory;
import com.sp.web.service.hiring.lens.HiringLensFactory;
import com.sp.web.service.sse.ActionType;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.service.sse.MessageEventRequest;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory class for people analytics users.
 */
@Component
public class HiringUserFactory {
  
  private static final Logger log = Logger.getLogger(HiringUserFactory.class);
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  private AccountFactory accountFactory;
  
  @Autowired
  private HiringGroupFactory groupFactory;
  
  @Autowired
  private HiringLensFactory lensFactory;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  private MemberControllerHelper memberController;
  
  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  PartnerFactory partnerFactory;
  
  @Autowired
  ThirdPartyRepository partyRepository;
  
  public HiringUser getUser(String userId) {
    return hiringRepository.findById(userId);
  }
  
  public List<HiringUser> getAll(String companyId) {
    return hiringRepository.getAllUsers(companyId);
  }
  
  public List<HiringUser> getAll(String companyId, UserType type) {
    return hiringRepository.getAllUsers(companyId, type);
  }
  
  public List<HiringUser> getAllValid(String companyId) {
    return hiringRepository.getAllValidUsers(companyId);
  }
  
  public List<HiringUser> getUsersWithRole(String companyId, String roleId) {
    return hiringRepository.findByRole(companyId, roleId);
  }
  
  public List<HiringUser> getUsers(List<String> userIds) {
    return hiringRepository.findAllUsers(userIds);
  }
  
  /**
   * Remove the hiring role for the given user.
   * 
   * @param user
   *          - user
   * @param roleId
   *          - role id
   */
  public void removeRole(HiringUser user, String roleId) {
    if (user.removeHiringRole(roleId)) {
      hiringRepository.updateHiringUser(user);
    }
  }
  
  /**
   * Gets the hiring user by email.
   * 
   * @param email
   *          - email id
   * @param companyId
   *          - company id
   * @return the hiring user
   */
  public HiringUser getUserByEmail(String email, String companyId) {
    return hiringRepository.findByEmail(companyId, email);
  }
  
  /**
   * Gets the archived user by email.
   * 
   * @param email
   *          - email
   * @param companyId
   *          - company id
   * @return the hiring user
   */
  public HiringUser getArchiveUserByEmail(String email, String companyId) {
    return hiringRepository.findArchivedCandidateByEmail(companyId, email);
  }
  
  /**
   * Delete the given hiring user.
   * 
   * @param user
   *          - user to delete
   */
  public void delete(HiringUser user) {
    hiringRepository.delete(user);
    postDeleteProcessing(user);
  }
  
  /**
   * Delete the given hiring archive user.
   * 
   * @param user
   *          - user to delete
   */
  public void deleteArchiveUser(HiringUserArchive user) {
    hiringRepository.delete(user);
    postDeleteProcessing(user);
  }
  
  /**
   * Archive the given hiring user.
   * 
   * @param user
   *          - user
   * @param hiringUser
   *          - hiring user
   * @param isHired
   *          - flag to indicate hired or not
   * @return the new archive user created
   */
  public HiringUserArchive archiveUser(User user, HiringUser hiringUser, boolean isHired) {
    return archiveUser(user, hiringUser, isHired, null);
  }
  
  /**
   * Archive the given hiring user.
   * 
   * @param user
   *          - user
   * @param hiringUser
   *          - hiring user
   * @param isHired
   *          - flag to indicate hired or not
   * @param tags
   *          - the tags to update
   * @return the new archive user created
   */
  public HiringUserArchive archiveUser(User user, HiringUser hiringUser, boolean isHired,
      List<String> tags) {
    
    if (isHired) {
      Assert.isTrue(hiringUser.getUserStatus() == UserStatus.VALID,
          MessagesHelper.getMessage("error.archive.not.valid", user.getLocale()));
    }
    
    // check if the user is an administrator
    hiringUser.setHired(isHired);
    if (hiringUser.hasRole(RoleType.Hiring)) {
      removeAdministrator(hiringUser);
    }
    
    if (tags != null) {
      hiringUser.setTagList(tags);
    }
    
    // creating the archive user
    final HiringUserArchive archiveUser = new HiringUserArchive(hiringUser);
    removeExistingArchiveUser(archiveUser);
    hiringRepository.save(archiveUser);
    hiringRepository.delete(hiringUser);
    groupFactory.removeUserFromGroups(user, hiringUser.getId());
    notifyAdmins(ActionType.HiringUserArchive, hiringUser);
    return archiveUser;
  }
  
  /**
   * Remove hiring role for the given user.
   * 
   * @param hiringUser
   *          - hiring user
   */
  public void removeAdministrator(HiringUser hiringUser) {
    memberController.removeHiringAdmin(hiringUser);
    hiringUser.removeRole(RoleType.Hiring);
    updateUser(hiringUser);
  }
  
  /**
   * Get the archive user by email.
   * 
   * @param email
   *          - email
   * @param companyId
   *          - company id
   * @return the archived user
   */
  public HiringUserArchive getArchivedUserByEmail(String email, String companyId) {
    return hiringRepository.findArchivedCandidateByEmail(companyId, email);
  }
  
  public void updateUser(HiringUser userToUpdate) {
    hiringRepository.updateHiringUser(userToUpdate);
    notifyAdmins(ActionType.HiringUserUpdated, userToUpdate);
  }
  
  public Collection<? extends HiringUser> getAllArchivedUsers(String companyId) {
    return hiringRepository.getAllArchivedUsers(companyId);
  }
  
  public HiringUserArchive getArchivedUser(String userId) {
    return hiringRepository.findArchivedUserById(userId);
  }
  
  /**
   * Method to put the hiring archive user back to candidate or employee list.
   * 
   * @param hiringArchiveUser
   *          - hiring archive user
   */
  public void putBack(HiringUserArchive hiringArchiveUser) {
    // check if the user already exists in the candidate list
    HiringUser existingUser = hiringRepository.findByEmail(hiringArchiveUser.getCompanyId(),
        hiringArchiveUser.getEmail());
    if (existingUser != null) {
      throw new IllegalArgumentException(MessagesHelper.getMessage("error.hire.user."
          + existingUser.getType()));
    }
    HiringUser hiringUser = new HiringUser(hiringArchiveUser);
    hiringRepository.updateHiringUser(hiringUser);
    hiringRepository.delete(hiringArchiveUser);
    notifyAdmins(ActionType.HiringUserDelete, hiringArchiveUser);
  }
  
  /**
   * Reset the erti flag for the given user.
   * 
   * @param userEmail
   *          - user email
   * @param companyId
   *          - company id
   */
  public void handleErtiDelete(String userEmail, String companyId) {
    HiringUser hiringUser = getUserByEmail(userEmail, companyId);
    if (hiringUser != null) {
      hiringUser.setInErti(false);
      updateUser(hiringUser);
    }
  }
  
  /**
   * Post delete processing for both hiring user and hiring archive user.
   * 
   * @param user
   *          - user
   */
  private void postDeleteProcessing(HiringUser user) {
    if (user.getAnalysis() == null) {
      try {
        accountFactory.releaseHiringSubscription(user.getCompanyId(), 1);
      } catch (Exception e) {
        log.warn("Could not release the hiring subscriptions.", e);
      }
    }
    // removing all the feedback lens requests
    lensFactory.deleteLensReqeusts(user.getId());
    // remove the user from all the groups.
    groupFactory.removeUserFromGroups(user, user.getId());
    
    List<FeedbackUser> portraitShareUsers = feedbackRepository.findFeedbackUsers(user.getId(),
        FeatureType.PortraitShare);
    portraitShareUsers.forEach(this::deletePortraitShareUser);
    
    if (user.hasRole(RoleType.Hiring)) {
      memberController.removeHiringAdmin(user);
    }
    
    notifyAdmins(ActionType.HiringUserDelete, user);
    if (user.isThirdParty()) {
      removeThirdPartyUser(user);
    }
  }
  
  private void notifyAdmins(ActionType actionType, HiringUser user) {
    Map<String, Object> payload = new HashMap<String, Object>();
    payload.put(Constants.ENTITY_USER_ID, user.getId());
    payload.put(Constants.ENTITY_TYPE, user.getType());
    payload.put(Constants.ENTITY_ARCHIVE, user instanceof HiringUserArchive);
    final String companyId = user.getCompanyId();
    eventGateway.sendEvent(MessageEventRequest.newEvent(actionType, getAdminUsers(companyId),
        payload, companyId));
  }
  
  /**
   * removeThirdPartyUser will remove the thirdParty User associated with the hiring User.
   * 
   * @param hiringUser
   *          is the hiringUser.
   */
  private void removeThirdPartyUser(HiringUser hiringUser) {
    partnerFactory.updatePartner(hiringUser, ActionType.DeletePartner);
    
  }
  
  private List<String> getAdminUsers(String companyId) {
    return userFactory.getAllByCompanyAndRole(companyId, RoleType.Hiring).stream().map(User::getId)
        .collect(Collectors.toList());
  }
  
  private void deletePortraitShareUser(FeedbackUser portraitShareUser) {
    tokenFactory.removeToken(portraitShareUser.getTokenId());
    feedbackRepository.remove(portraitShareUser);
  }
  
  /**
   * Add the hiring user from ERTi user.
   * 
   * @param user
   *          - ERTi user
   * @param hasErti
   *          - flag to indicate if the erti product is present.
   */
  public void addFromErti(User user, boolean hasErti) {
    
    // checking if the hiring user exists
    HiringUser hiringUser = getUserByEmail(user.getEmail(), user.getCompanyId());
    if (hiringUser == null) {
      hiringUser = new HiringUser(user);
    } else {
      if (hiringUser.getType() == UserType.HiringCandidate) {
        archiveUser(user, hiringUser, false);
        hiringUser = new HiringUser(user);
      } else {
        hiringUser.updateProfile(user);
        hiringUser.setUserStatus(UserStatus.VALID);
      }
    }
    
    if (user.hasRole(RoleType.Hiring)) {
      hiringUser.addRole(RoleType.Hiring);
    }
    
    if (hasErti) {
      hiringUser.setInErti(true);
    }
    setAnalysisCompletedOn(hiringUser);
    updateUser(hiringUser);
  }
  
  /**
   * Fixes the created on under analysis for the user.
   * 
   * @param usr
   *          - user
   */
  private void setAnalysisCompletedOn(HiringUser usr) {
    final AnalysisBean analysis = usr.getAnalysis();
    if (analysis != null) {
      LocalDateTime createdOn = analysis.getCreatedOn();
      if (createdOn == null) {
        LocalDate usrCreatedOn = usr.getCreatedOn();
        if (usrCreatedOn == null) {
          usrCreatedOn = LocalDate.now();
        }
        analysis.setCreatedOn(LocalDateTime.of(usrCreatedOn, LocalTime.MIDNIGHT));
      }
    }
  }
  
  private void removeExistingArchiveUser(HiringUserArchive archiveUser) {
    final String email = archiveUser.getEmail();
    HiringUserArchive existingArchiveUser = getArchivedUserByEmail(email,
        archiveUser.getCompanyId());
    if (existingArchiveUser != null) {
      log.warn("Removing existing archive user. Email :" + email);
      deleteArchiveUser(existingArchiveUser);
    }
  }
}
