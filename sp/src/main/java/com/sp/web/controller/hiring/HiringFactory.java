package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.hiring.user.HiringCandidateListingDTO;
import com.sp.web.dto.hiring.user.HiringEmployeeListingDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.form.hiring.user.HiringAddForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.product.AccountFactory;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.service.hiring.lens.HiringLensFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The factory class provides all the helper methods for the hiring user flows.
 */
@Component
public class HiringFactory {
  
  private static final Logger log = Logger.getLogger(HiringFactory.class);
  
  @Autowired
  HiringRepository hiringRepository;
  
  @Autowired
  AccountFactory accountFactory;
  
  @Autowired
  SPTokenFactory tokenFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationsProcessor;
  
  @Autowired
  HiringLensFactory lensFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringRoleFactory roleFactory;
  
  /**
   * Method to add the users from the given add form.
   * 
   * @param user
   *          - user
   * @param hiringAddForm
   *          - the add user form
   * @return the users added as well as error
   */
  public Map<String, Object> addHiringUser(User user, HiringAddForm hiringAddForm) {
    List<String> emails = hiringAddForm.getEmails();
    StringBuffer sb = new StringBuffer();
    final UserType type = hiringAddForm.getType();
    List<Object> userList = new ArrayList<>();
    for (String email : emails) {
      try {
        HiringUser addHiringUser = addHiringUser(user,
            StringUtils.trimWhitespace(email.toLowerCase()), hiringAddForm, type, true);
        if (type == UserType.Member) {
          userList.add(new HiringEmployeeListingDTO(addHiringUser));
        } else {
          userList.add(new HiringCandidateListingDTO(addHiringUser, roleFactory));
        }
      } catch (Exception e) {
        log.warn("Could not add user :" + email, e);
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(email);
      }
    }
    Map<String, Object> addResponse = new HashMap<String, Object>();
    addResponse.put(Constants.PARAM_HIRING_MEMBERS, userList);
    if (sb.length() > 0) {
      addResponse.put(Constants.PARAM_ERROR,
          MessagesHelper.getMessage("add.error", user.getLocale(), sb.toString()));
    }
    return addResponse;
  }
  
  /**
   * Adds a hiring candidate.
   * 
   * @param user
   *          - user
   * @param email
   *          - hiring user form
   * @param hiringAddForm
   *          - form data
   * @param type
   *          - if send invite
   * @return the hiring user created
   */
  public HiringUser addHiringUser(User user, String email, HiringAddForm hiringAddForm,
      UserType type, boolean sendInvite) {
    // Get the account for the company
    String companyId = user.getCompanyId();
    
    // check if user is an existing user
    User existingSPUser = userFactory.getUserByEmail(email);
    
    // cannot send invite to existing users
    if (existingSPUser != null && existingSPUser.getCompanyId().equals(user.getCompanyId())) {
      throw new InvalidRequestException(
          "Error 1001: User already a SurePeople account holder cannot send invite.");
    }
    
    // check if candidate already exists
    HiringUser hiringUser = hiringRepository.findByEmail(companyId, email);
    if (hiringUser != null) {
      throw new InvalidRequestException("Error 1001: User already exists.");
    }
    
    // check if present in archive
    HiringUserArchive hiringUserArchiveExists = hiringRepository.findArchivedCandidateByEmail(
        companyId, email);
    if (hiringUserArchiveExists != null) {
      throw new InvalidRequestException("Error 1001: User already exists in archives.");
    }
    
    hiringUser = hiringAddForm.create(user, email);
    boolean isError = false;
    try {
      
      try {
        accountFactory.reserveSubscription(companyId, SPPlanType.IntelligentHiring);
        
        // add the candidate
        hiringRepository.addHiringUser(hiringUser);
        
        if (sendInvite) {
          HashMap<String, Object> tokenMap = new HashMap<String, Object>();
          tokenMap.put(Constants.PARAM_HIRING_USER_ID, hiringUser.getId());
          if (type == UserType.HiringCandidate) {
            tokenMap.put(Constants.PARAM_REFERENCE_TYPES, hiringAddForm.getReferenceTypes());
            tokenMap.put(Constants.PARAM_REFERENCE_CHECK, hiringAddForm.isReferenceCheck());
            sendInvite(hiringUser, user, tokenMap, NotificationType.HiringCandidateInvite);
          } else {
            sendInvite(hiringUser, user, tokenMap, NotificationType.HiringEmployeeInvite);
          }
        }
        
      } catch (Exception exp) {
        log.warn("Error reserving subscription.", exp);
        // removing the user from the DB
        isError = true;
        hiringRepository.delete(hiringUser);
        throw exp;
      }
    } finally {
      if (isError) {
        accountFactory.releaseHiringSubscription(companyId, 1);
        hiringRepository.delete(hiringUser);
      }
    }
    return hiringUser;
  }
  
  /**
   * Sends an invite to the hiring user.
   * 
   * @param hiringUser
   *          - hiring user
   * @param user
   *          - logged in user
   * @param tokenMap
   *          - token map
   * @param notificationType
   *          - notification type
   */
  public void sendInvite(HiringUser hiringUser, User user, HashMap<String, Object> tokenMap,
      NotificationType notificationType) {
    
    try {
      Token token = tokenFactory.getToken(TokenType.PERPETUAL, tokenMap,
          TokenProcessorType.HIRING_CANDIDATE);
      
      // saving the token URL for reminders etc.
      hiringUser.setTokenUrl(token.getTokenUrl());
      
      notificationsProcessor.process(notificationType, user, hiringUser);
      
      // get the due date
      hiringUser.setUserStatus(UserStatus.INVITATION_SENT);
      hiringRepository.updateHiringUser(hiringUser);
    } catch (SPException e) {
      log.warn("Error creating hiring user :" + hiringUser.getEmail(), e);
      throw e;
    } catch (Exception e) {
      log.warn("Error creating hiring user :" + hiringUser.getEmail(), e);
      throw new InvalidRequestException("Could not add hiring candidate.", e);
    }
  }
  
  /**
   * Invite all the references in the given reference list.
   * 
   * @param list
   *          - reference list
   * @param user
   *          - user
   * @param referenceCheck
   *          - flag to indicate reference check
   */
  public void addReferences(List<HiringLensForm> list, HiringUser user, boolean referenceCheck) {
    
    for (HiringLensForm lensForm : list) {
      lensForm.setRequestNow(referenceCheck);
      lensFactory.create(user, lensForm);
    }
    
    if (user.getUserStatus() == UserStatus.ADD_REFERENCES) {
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
    }
    hiringRepository.updateHiringUser(user);
  }
}
