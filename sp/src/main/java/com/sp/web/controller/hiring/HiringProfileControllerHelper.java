package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.controller.profile.ProfileControllerHelper;
import com.sp.web.dto.hiring.user.HiringArchiveUserDTO;
import com.sp.web.dto.hiring.user.HiringCandidateDTO;
import com.sp.web.dto.hiring.user.HiringEmployeeDTO;
import com.sp.web.dto.hiring.user.HiringUserBaseDTO;
import com.sp.web.form.hiring.user.HiringReferenceForm;
import com.sp.web.form.hiring.user.HiringUserProfileForm;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPMedia;
import com.sp.web.model.Token;
import com.sp.web.model.User;
import com.sp.web.model.UserProfileSettings;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.hiring.role.HiringRole;
import com.sp.web.model.hiring.user.HiringComment;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 * 
 *         This is the helper for hiring profile controller.
 */
@Component
public class HiringProfileControllerHelper {
  
  @Autowired
  private HiringFactory hiringFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private ProfileControllerHelper profileHelper;
  
  @Autowired
  private HiringRoleFactory hiringRoleFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  /**
   * Helper method to get the profile details of a candidate.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get profile request
   */
  public SPResponse getProfileDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    HiringUser hiringUser = getHiringUser(user, userId);
    
    if (hiringUser.getType() == UserType.HiringCandidate) {
      resp.add(Constants.PARAM_HIRING_USER, new HiringCandidateDTO(hiringUser, hiringRoleFactory));
    } else {
      resp.add(Constants.PARAM_HIRING_USER, new HiringEmployeeDTO(hiringUser));
    }
    
    if (hiringUser.getUserStatus() == UserStatus.VALID) {
      profileHelper.getFullAnalysis(hiringUser, resp, false, user.getLocale());
    }
    return resp;
  }
  
  /**
   * This is the controller helper method to be called to complete the users profile details. From
   * an external user request for completing profile of candidate or employee.
   * 
   * @param user
   *          - hiring user
   * @param params
   *          - params
   * @return the response to get profile
   */
  public SPResponse getProfileDetailsExt(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HttpSession session = (HttpSession) params[0];
    
    Token token = (Token) session.getAttribute(Constants.PARAM_TOKEN);
    
    // get the candidate email
    HiringUser hiringUser = (HiringUser) user;
    
    resp.add(Constants.PARAM_HIRING_MEMBERS, new HiringUserBaseDTO(hiringUser));
    resp.add(Constants.PARAM_REFERENCE_TYPES, token.getParam(Constants.PARAM_REFERENCE_TYPES));
    return resp;
  }
  
  /**
   * Helper method to get the details of the archived user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getArchivedUserProfileDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    // get the candidate email
    String userId = (String) params[0];
    
    final HiringUserArchive archivedHiringUser = getArchivedUser(user, userId);
    
    resp.add(Constants.PARAM_HIRING_USER, new HiringArchiveUserDTO(archivedHiringUser,
        hiringRoleFactory));
    
    if (archivedHiringUser.getUserStatus() == UserStatus.VALID) {
      profileHelper.getFullAnalysis(archivedHiringUser, resp, false, user.getLocale());
    }
    return resp;
  }
  
  /**
   * Helper method to update the user profile.
   * 
   * @param user
   *          - user
   * @param params
   *          - params to process
   * @return the response to the update profile request
   */
  public SPResponse updateProfile(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringUser userToUpdate = updateUserData(user, params, null);
    
    // save to db
    hiringUserFactory.updateUser(userToUpdate);
    
    // if the user to update is admin user then update the user
    // in the user db as well
    if (userToUpdate.hasRole(RoleType.Hiring)) {
      User adminUser = userFactory.getUserByEmail(userToUpdate.getEmail());
      if (adminUser != null) {
        HiringUserProfileForm form = (HiringUserProfileForm) params[0];
        form.update(adminUser);
        userFactory.updateUserAndSession(adminUser);
      }
    }
    // send back success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to update the users profile for hiring users when coming from external URL's.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  @SuppressWarnings("unchecked")
  public SPResponse updateProfileExt(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringUser userToUpdate = updateUserData(user, params, (HiringUser) user);
    
    HttpSession session = (HttpSession) params[1];
    Token token = (Token) session.getAttribute(Constants.PARAM_TOKEN);
    
    List<String> referenceTypes = (List<String>) token.getParam(Constants.PARAM_REFERENCE_TYPES);
    
    if (userToUpdate.getUserStatus() == UserStatus.INVITATION_SENT) {
      if (CollectionUtils.isNotEmpty(referenceTypes)) {
        resp.add(Constants.PARAM_REFERENCE_TYPES, referenceTypes);
        userToUpdate.setUserStatus(UserStatus.ADD_REFERENCES);
      } else {
        userToUpdate.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      }
    }
    // save to db
    hiringUserFactory.updateUser(userToUpdate);
    
    // send back success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to update the hiring roles.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse addRole(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    String roleId = (String) params[1];
    
    boolean addMatch = (boolean) params[2];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    final HiringRole role = hiringRoleFactory.get(roleId);
    Assert.notNull(role, "Role not found.");
    // update the data and save
    if (hiringUser.addHiringRole(roleId)) {
      hiringUserFactory.updateUser(hiringUser);
    }
    
    if (addMatch) {
      resp.add("roleAndMatch", hiringRoleFactory.getUserRoleMatch(hiringUser, role));
    }
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to remove the hiring roles.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse removeRole(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    String roleId = (String) params[1];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    if (hiringUser.removeHiringRole(roleId)) {
      hiringUserFactory.updateUser(hiringUser);
    }
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to update the taglist for the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  @SuppressWarnings("unchecked")
  public SPResponse updateTags(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    List<String> tagList = (List<String>) params[1];
    tagList = tagList.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    hiringUser.setTagList(tagList);
    hiringUserFactory.updateUser(hiringUser);
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to add update the web url of the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse addUpdateUrl(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    SPMedia url = (SPMedia) params[1];
    
    boolean profileUrl = (boolean) params[2];
    int index = (int) params[3];
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    if (profileUrl) {
      if (index > -1) {
        hiringUser.updateProfileUrl(url, index);
      } else {
        hiringUser.addProfileUrl(url);
      }
      
    } else {
      hiringUser.addUrl(url);
    }
    
    hiringUserFactory.updateUser(hiringUser);
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to remove the web url of the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the update request
   */
  public SPResponse removeUrl(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    String userId = (String) params[0];
    String url = (String) params[1];
    boolean profileUrl = (boolean) params[2];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    if (profileUrl) {
      hiringUser.removeProfileUrl(url);
    } else {
      hiringUser.removeUrl(url);
    }
    hiringUserFactory.updateUser(hiringUser);
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * Helper method to add comments for the user.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the add comments
   */
  public SPResponse addComment(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    String comment = (String) params[1];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    HiringComment addComment = hiringUser.addComment(user, comment);
    hiringUserFactory.updateUser(hiringUser);
    
    // send success
    return resp.add(Constants.PARAM_COMMENT, addComment);
  }
  
  /**
   * Helper method to remove the comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse removeComment(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    String cid = (String) params[1];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    List<HiringComment> comments = hiringUser.getComments();
    
    HiringComment comment = getComment(user, cid, comments);
    
    // removing the comment and updating the db
    comments.remove(comment);
    hiringUserFactory.updateUser(hiringUser);
    
    // send success
    return resp.isSuccess();
  }
  
  /**
   * The helper method to update the user comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the update request
   */
  public SPResponse updateComment(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the roles
    String cid = (String) params[1];
    
    // get the updated comment
    String commentStr = (String) params[2];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // update the data and save
    HiringComment comment = getComment(user, cid, hiringUser.getComments());
    
    Assert.isTrue(comment.getBy().getId().equals(user.getId()),
        MessagesHelper.getMessage("error.comment.not.owner", user.getLocale()));
    
    // updating the comment and updating the db
    comment.setComment(commentStr);
    hiringUserFactory.updateUser(hiringUser);
    
    // send success
    return resp.add(Constants.PARAM_COMMENT, comment);
  }
  
  /**
   * Helper method to invite the candidate references.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params to process
   * @return the response to the invite request
   */
  @SuppressWarnings("unchecked")
  public SPResponse addReferences(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // candidate email
    HiringReferenceForm form = (HiringReferenceForm) params[0];
    HttpSession session = (HttpSession) params[1];
    
    Token token = (Token) session.getAttribute(Constants.PARAM_TOKEN);
    List<List<String>> referenceTypes = (List<List<String>>) token
        .getParam(Constants.PARAM_REFERENCE_TYPES);
    
    form.updateFeedbackFor(user.getId());
    form.validate(user, referenceTypes);
    final boolean referenceCheck = (boolean) token.getParam(Constants.PARAM_REFERENCE_CHECK);
    hiringFactory.addReferences(form.getReferenceList(), (HiringUser) user, referenceCheck);
    
    // send success
    resp.isSuccess();
    
    return resp;
  }
  
  /**
   * Helper method to get the user profile share id.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getProfileShareId(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    UserProfileSettings profileSettings = hiringUser.profileSettings();
    if (profileSettings == null) {
      profileSettings = hiringUser.getProfileSettings();
      hiringUserFactory.updateUser(hiringUser);
    }
    
    // send success
    return resp.add(Constants.PARAM_HIRING_PUBLIC_ID, profileSettings.getToken());
  }
  
  /**
   * Helper method to get the user roles.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the get request
   */
  public SPResponse getUserRoles(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    
    // send success
    return resp.add(Constants.PARAM_HIRING_ROLES, hiringUser.getHiringRoleIds());
  }
  
  /**
   * Get the hiring user for the given user id.
   * 
   * @param user
   *          - user
   * @param userId
   *          - user id
   * @return the hiring user
   */
  private HiringUser getHiringUser(User user, String userId) {
    Assert.hasText(userId, "User id required.");
    HiringUser hiringUser = hiringUserFactory.getUser(userId);
    Assert.notNull(hiringUser, MessagesHelper.getMessage("error.user.not.found", user.getLocale()));
    Assert.isTrue(hiringUser.getCompanyId().equals(user.getCompanyId()), "Unauthroized access.");
    return hiringUser;
  }
  
  /**
   * Get the archived user.
   * 
   * @param user
   *          - user requesting
   * @param userId
   *          - archived user id
   * @return the archived user
   */
  private HiringUserArchive getArchivedUser(User user, String userId) {
    Assert.hasText(userId, "User id required.");
    final HiringUserArchive archivedHiringUser = hiringUserFactory.getArchivedUser(userId);
    Assert.notNull(archivedHiringUser, "User not found.");
    Assert.isTrue(archivedHiringUser.getCompanyId().equals(user.getCompanyId()),
        "Unauthroized access.");
    return archivedHiringUser;
  }
  
  /**
   * Validate if the comment is by the currently logged in user.
   * 
   * @param user
   *          - logged in user
   * @param cid
   *          - index of the comment
   * @param comments
   *          - list of comments
   * @return the validated comment
   */
  private HiringComment getComment(User user, String cid, List<HiringComment> comments) {
    Optional<HiringComment> findFirst = comments.stream().filter(c -> c.getCid().equals(cid))
        .findFirst();
    Assert.isTrue(findFirst.isPresent(),
        MessagesHelper.getMessage("error.comment.not.found", user.getLocale()));
    return findFirst.get();
  }
  
  /**
   * Update the user data.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @param userToUpdate
   *          - user to update
   * @return updated user
   */
  private HiringUser updateUserData(User user, Object[] params, HiringUser userToUpdate) {
    // get the profile form
    HiringUserProfileForm form = (HiringUserProfileForm) params[0];
    
    if (userToUpdate == null) {
      form.validate();
      userToUpdate = hiringUserFactory.getUser(form.getId());
      Assert.notNull(userToUpdate,
          MessagesHelper.getMessage("error.user.not.found", user.getLocale()));
    }
    
    form.validateUpdate();
    
    // update the user
    form.update(userToUpdate);
    return userToUpdate;
  }
}
