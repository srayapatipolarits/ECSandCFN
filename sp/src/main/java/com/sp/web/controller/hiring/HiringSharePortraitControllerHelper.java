package com.sp.web.controller.hiring;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.controller.profile.ProfileControllerHelper;
import com.sp.web.dto.hiring.user.HiringCandidateDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.form.hiring.user.ShareUserProfileForm;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.service.hiring.lens.HiringLensFactory;
import com.sp.web.service.hiring.role.HiringRoleFactory;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.token.SPTokenFactory;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The controller helper for the hiring portrait share controller.
 */
@Component
public class HiringSharePortraitControllerHelper {
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationsProcessor;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  @Autowired
  private HiringLensFactory lensFactory;
  
  @Autowired
  private HiringRoleFactory hiringRoleFactory;
  
  @Autowired
  private ProfileControllerHelper profileHelper;
  
  /**
   * Helper method to remove the comment.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse getUserShareList(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, userId);
    Assert.isTrue(hiringUser.getUserStatus() == UserStatus.VALID, "User assessment not completed.");
    
    List<FeedbackUser> shareUsers = feedbackRepository.findFeedbackUsers(hiringUser.getId(),
        FeatureType.PortraitShare);
    
    // send success
    return resp.add(Constants.PARAM_FEEDBACK_USER, shareUsers.stream().map(UserMarkerDTO::new)
        .collect(Collectors.toList()));
  }
  
  /**
   * Helper method to get the profile details of a candidate.
   * 
   * @param user
   *          - user
   * 
   * @return the response to the get profile request
   */
  public SPResponse getUserSharePortraitDetails(User user) {
    final SPResponse resp = new SPResponse();
    
    HiringUser hiringUser = getHiringUserFromFeedbackUser(user);
    
    // adding the user details
    resp.add(Constants.PARAM_HIRING_USER, new HiringCandidateDTO(hiringUser));
    
    // adding the prism portrait details
    if (hiringUser.getUserStatus() == UserStatus.VALID) {
      profileHelper.getFullAnalysis(hiringUser, resp, false, user.getLocale());
      // adding the roles and match
      resp.add("rolesAndMatch", hiringRoleFactory.getUserRoleMatch(hiringUser));
    }
    
    // adding the lens details
    resp.add(Constants.PARAM_FEEDBACK_REQUEST, lensFactory.getAll(hiringUser));
    
    return resp;
  }
  
  /**
   * Helper method to get the profile details of a candidate.
   * 
   * @param user
   *          - user
   * 
   * @param params
   *          - params
   * 
   * @return the response to the get profile request
   */
  public SPResponse getUserSharePortraitLensDetails(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    HiringLensForm form = (HiringLensForm) params[0];
    form.validateGet();
    
    HiringUser hiringUser = getHiringUserFromFeedbackUser(user);
    
    return resp.add("hiringLens", lensFactory.get(hiringUser, form));
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
  public SPResponse shareUserProfile(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    ShareUserProfileForm form = (ShareUserProfileForm) params[0];
    form.validate();
    
    // get the user
    HiringUser hiringUser = getHiringUser(user, form.getUserFor());
    Assert.isTrue(hiringUser.getUserStatus() == UserStatus.VALID, "User assessment not completed.");
    
    // check if the user already exists
    FeedbackUser feedbackUser = feedbackRepository.findFeedbackUserByEmail(form.getEmail().toLowerCase(),
        form.getUserFor(), FeatureType.PortraitShare);
    if (feedbackUser != null) {
      throw new IllegalArgumentException(MessagesHelper.getMessage(
          "error.hire.user.share.portrait", user.getLocale()));
    }
    
    // creating the feedback user
    feedbackUser = form.create(hiringUser);
    feedbackRepository.addFeedbackUser(feedbackUser);
    Token token = tokenFactory.getToken(new PortraitShareTokenRequest(feedbackUser),
        TokenProcessorType.HIRING_SHARE_PORTRAIT);
    feedbackUser.updateFromToken(token);
    feedbackRepository.updateFeedbackUser(feedbackUser);
    
    Map<String, Object> emailParams = new HashMap<String, Object>();
    emailParams.put(
        "bandCopy",
        MessagesHelper.getMessage("user.portrait.share.band.copy", hiringUser.getLocale(),
            user.getFirstName(), user.getLastName(), hiringUser.getFirstName()));
    // send the email
    notificationsProcessor.process(NotificationType.HiringShareUserPortrait, hiringUser,
        feedbackUser, emailParams);
    
    // send success
    return resp.add(Constants.PARAM_FEEDBACK_USER, new UserMarkerDTO(feedbackUser));
  }
  
  /**
   * Helper method to remove the user share request.
   * 
   * @param user
   *          - logged in user
   * @param params
   *          - params
   * @return the response to the remove request
   */
  public SPResponse deleteUserShare(User user, Object[] params) {
    final SPResponse resp = new SPResponse();
    
    // get the candidate email
    String userId = (String) params[0];
    Assert.hasText(userId, "User id required.");
    
    // get the user
    FeedbackUser feedbackUser = feedbackRepository.findByIdValidated(userId);
    Assert.notNull(feedbackUser, "User not found.");
    
    // removing the token and the feedback user
    tokenFactory.removeToken(feedbackUser.getTokenId());
    feedbackRepository.remove(feedbackUser);
    
    // send success
    return resp.isSuccess();
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
   * Get the hiring user from the given feedback user.
   * 
   * @param user
   *          - feedback user
   * @return the hiring user
   */
  private HiringUser getHiringUserFromFeedbackUser(User user) {
    Assert.isTrue(user instanceof FeedbackUser, "Unauthorized request.");
    HiringUser hiringUser = hiringUserFactory.getUser(((FeedbackUser) user).getFeedbackFor());
    Assert.notNull(hiringUser, "User not found.");
    Assert.isTrue(hiringUser.getType() == UserType.HiringCandidate, "Not candidate.");
    return hiringUser;
  } 
}
