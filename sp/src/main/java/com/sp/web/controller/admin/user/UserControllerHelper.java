package com.sp.web.controller.admin.user;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.Operation;
import com.sp.web.form.SignupForm;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserMessage;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.repository.log.LogsRepository;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.log.LogRequest;
import com.sp.web.service.sse.EventGateway;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The helper class to process the user controller requests.
 */
@Component
public class UserControllerHelper {
  
  @Autowired
  private GroupRepository groupRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private LoginHelper loginHelper;
  
  @Autowired
  private UserFactory userFactory;

  @Autowired
  private EventGateway eventGateway;
  
  @Autowired
  @Qualifier("notificationLog")
  private LogGateway logGateway;
  
  @Autowired
  private LogsRepository logsRepository;

  /**
   * The helper method to add a user.
   * 
   * @param user
   *          - the user to add
   * @param params
   *          - params
   * @return the response to the add request
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
    final List<GroupAssociation> groupAssociationList = (List<GroupAssociation>) params[2];
    
    // check if user already exists
    if (userRepository.findByEmail(addUserForm.getEmail()) != null) {
      throw new InvalidRequestException("User " + addUserForm.getEmail()
          + " already present in the system !!!");
    }
    
    // add the user
    final User userToAdd = addUserForm.getUserWithCompany(user.getCompanyId());
    
    // add the tags for the user
    userToAdd.setTagList(tagList);
    
    // create the user
    userRepository.createUser(userToAdd);
    
    // add the group associations for the user
    // group repository called because user has to be added
    // to the user group as well
    if (groupAssociationList != null) {
      groupAssociationList.stream().forEach(ga -> groupRepository.addMember(userToAdd, ga));
    }
    
    // set success in processing
    response.isSuccess();
    
    return response;
  }
  
  /**
   * Helper method to update the user permissions.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the update request
   */
  @PreAuthorize("hasRole('AccountAdministrator')")
  public SPResponse updatePermissions(User user, Object[] params) {
    
    // get the user to update
    final String userEmail = (String) params[0];
    
    // get the administrator flag
    final boolean isCompetencyAllowed = (boolean) params[1];
    
    // get the isWorkspacePulseAllowed flag
    final boolean isWorkspacePulseAllowed = (boolean) params[2];
    
    // get the isSpectrumAllowed flag
    final boolean isSpectrumAllowed = (boolean) params[3];
    
    // check if the user is currently set as administrator
    if (user.equals(userEmail)) {
      throw new InvalidRequestException("User :" + userEmail + ": is the current Administrator !!!");
    }
    
    // check if the user exists
    User userToUpdate = userRepository.findByEmailValidated(userEmail);
    userToUpdate.isSameCompany(user);
    final String userToUpdateId = userToUpdate.getId();

    // update the competency tool roles
    boolean competencyAdded = false;
    if (isCompetencyAllowed) {
      if (!userToUpdate.hasRole(RoleType.CompetencyAdmin)) {
        userToUpdate.addRole(RoleType.CompetencyAdmin);
        competencyAdded = true;
      }
    } else {
      userToUpdate.removeRole(RoleType.CompetencyAdmin);
      logsRepository
          .removeNotificationLogs(userToUpdateId, LogActionType.CompetencyAdminPermission);
    }
    
    // update the pulse tool roles
    boolean pulseAdded = false;
    if (isWorkspacePulseAllowed) {
      if (!userToUpdate.hasRole(RoleType.Pulse)) {
        userToUpdate.addRole(RoleType.Pulse);
        pulseAdded = true;
      }
    } else {
      userToUpdate.removeRole(RoleType.Pulse);
      logsRepository
          .removeNotificationLogs(userToUpdateId, LogActionType.WorkspacePulsePermission);
    }
    
    // update the pulse tool roles
    boolean spectrumAdded = false;
    if (isSpectrumAllowed) {
      if (!userToUpdate.hasRole(RoleType.Spectrum)) {
        userToUpdate.addRole(RoleType.Spectrum);
        spectrumAdded = true;
      }
    } else {
      userToUpdate.removeRole(RoleType.Spectrum);
      logsRepository.removeNotificationLogs(userToUpdateId, LogActionType.SpectrumPermission);
    }
    
    // update the user
    userRepository.updateRoles(userToUpdate);
    userFactory.updateUserAndSession(userToUpdate);
    
    if (competencyAdded) {
      LogRequest logRequest = new LogRequest(LogActionType.CompetencyAdminPermission, user,
          userToUpdate);
      logGateway.logNotification(logRequest);
    }
    
    if (pulseAdded) {
      LogRequest logRequest = new LogRequest(LogActionType.WorkspacePulsePermission, user,
          userToUpdate);
      logGateway.logNotification(logRequest);
    }

    if (spectrumAdded) {
      LogRequest logRequest = new LogRequest(LogActionType.SpectrumPermission, user, userToUpdate);
      logGateway.logNotification(logRequest);
    }
    
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to set and remove the LinkedIn URL.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return response to the update request
   */
  public SPResponse updateLinkedInUrl(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the linked in url
    String linkedInUrl = (String) params[0];
    
    // get the operation
    Operation op = (Operation) params[1];
    
    // get the user email
    String userEmail = (params.length > 2) ? ((String) params[2]) : null;
    
    User userToUpdate = user;
    
    if (userEmail != null) {
      Assert.hasText(userEmail, MessagesHelper.getMessage("memberEmail.required"));
      userToUpdate = userRepository.findByEmailValidated(userEmail);
      // validate the same company
      userToUpdate.isSameCompany(user);
    }
    
    // process the operation
    switch (op) {
    case SET:
      Assert.hasText(linkedInUrl, MessagesHelper.getMessage("user.linkedInUrl.required"));
      userToUpdate.setLinkedInUrl(linkedInUrl);
      break;
    case REMOVE:
      userToUpdate.setLinkedInUrl(null);
      break;
    default:
      throw new InvalidRequestException("Do not know how to process operation :" + op);
    }
    
    // update the user
    userRepository.updateUser(userToUpdate);
    
    // set the success response
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * updateAutoLearning will update the auto learning for the user.
   * 
   * @param user
   *          logged in user.
   * @param param
   *          contains the parameter.
   * @return the SPResponse.
   */
  public SPResponse updateAutoLearning(User user, Object[] param) {
    
    boolean autoUpdate = (boolean) param[0];
    
    user.getProfileSettings().setAutoUpdateLearning(autoUpdate);
    userRepository.updateUser(user);
    
    /* update the user in session */
    loginHelper.updateUser(user);
    return new SPResponse().isSuccess();
  }
  
  /**
   * Helper method to remove the user messages.
   * 
   * @param user
   *          - logged in user
   * @param param
   *          - params
   * @return
   *    the response to the remove request
   */
  @SuppressWarnings("unchecked")
  public SPResponse removeMessage(User user, Object[] param) {
    final SPResponse response = new SPResponse();
    List<String> uidList = (List<String>) param[0];
    
    // get all the user messages
    List<UserMessage> messages = user.getMessages();
    // find the list of messages to remove
    List<UserMessage> messagesToRemove = messages.stream()
        .filter(m -> uidList.contains(m.getUid())).collect(Collectors.toList());
    // removing all the messages
    messages.removeAll(messagesToRemove);
    // updating the user in session
    userFactory.updateUserAndSession(user);
    
    return response.isSuccess();
  }
  
}
