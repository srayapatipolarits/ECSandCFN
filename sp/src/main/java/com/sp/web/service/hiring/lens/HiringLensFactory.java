package com.sp.web.service.hiring.lens;

import com.sp.web.Constants;
import com.sp.web.controller.feedback.FeedbackControllerHelper;
import com.sp.web.controller.generic.GenericFactory;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.hiring.lens.HiringLensDTO;
import com.sp.web.dto.hiring.lens.HiringLensListingDTO;
import com.sp.web.form.hiring.lens.HiringLensForm;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.token.SPTokenFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Dax Abraham
 *
 *         The factory class for all people analytics lens requests.
 */
@Component
public class HiringLensFactory implements
    GenericFactory<HiringLensListingDTO, HiringLensDTO, HiringLensForm> {
  
  @Autowired
  HiringLensFactoryCache factoryCache;
  
  @Autowired
  FeedbackControllerHelper feedbackHelper;
  
  @Autowired
  HiringUserFactory hiringUserFactory;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  NotificationsProcessor notificationsProcessor;
  
  @Autowired
  private SPTokenFactory tokenFactory;
  
  /**
   * Get the feedback requests for the given hiring user id.
   * 
   * @param feedbackFor
   *          - feedback for id
   * @return the list of feedback requests
   */
  private List<HiringLensListingDTO> getAll(final String feedbackFor) {
    List<FeedbackUser> byUserFor = factoryCache.getByUserFor(feedbackFor);
    return byUserFor.stream().map(HiringLensListingDTO::new).collect(Collectors.toList());
  }
  
  @Override
  public List<HiringLensListingDTO> getAll(User user) {
    return null;
  }
  
  /**
   * Get all the feedback requests for the given hiring user.
   * 
   * @param user
   *          - hiring user
   * @return the feedback requests list
   */
  public List<HiringLensListingDTO> getAll(HiringUser user) {
    return getAll(user.getId());
  }
  
  /**
   * Gets all the lens requests for the given user.
   * 
   * @param user
   *          - user
   * @param form
   *          - form data
   * @return the list of lens requests
   */
  public List<HiringLensListingDTO> getAllRequests(User user, HiringLensForm form) {
    return getAll(form.getFeedbackFor());
  }
  
  @Override
  public HiringLensDTO get(User user, HiringLensForm form) {
    FeedbackUser feedbackUser = getFeedbackUser(user, form);
    Assert.isTrue(feedbackUser.getUserStatus() == UserStatus.VALID, "Lens not completed.");
    
    String feedbackFor = feedbackUser.getFeedbackFor();
    HiringUser hiringUser = null;
    if (form.isArchiveUser()) {
      hiringUser = hiringUserFactory.getArchivedUser(feedbackFor);
    } else {
      hiringUser = hiringUserFactory.getUser(feedbackFor);
    }
    Assert.notNull(hiringUser, "User not found.");
    Map<String, Object> lensResponse = feedbackHelper.getLensResponse(hiringUser, feedbackUser,
        user.getLocale());
    HiringLensDTO lensDTO = new HiringLensDTO(hiringUser, lensResponse);
    return lensDTO;
  }
  
  @Override
  public HiringLensDTO create(User user, HiringLensForm form) {
    HiringUser hiringUser = hiringUserFactory.getUser(form.getFeedbackFor());
    Assert.notNull(hiringUser, "User not found.");
    
    // validate the reference data for candidates
    validateReference(form, hiringUser);
    
    FeedbackUser create = form.create(hiringUser);
    factoryCache.save(create);
    
    if (form.isRequestNow()) {
      sendFeedbackRequest(hiringUser, create);
    }
    return new HiringLensDTO(create);
  }
  
  private void validateReference(HiringLensForm form, HiringUser hiringUser) {
    if (hiringUser.getType() == UserType.HiringCandidate) {
      form.validateReference(hiringUser);
    }
  }
  
  /**
   * Send the prism lens request to the user.
   * 
   * @param user
   *          - user
   * @param form
   *          - form data
   * @return the response to the send request.
   */
  public HiringLensDTO sendRequest(User user, HiringLensForm form) {
    
    // get the feedback user
    FeedbackUser feedbackUser = getFeedbackUser(user, form);
    Assert.isTrue(feedbackUser.getUserStatus() == UserStatus.INVITATION_NOT_SENT,
        "Invitation already sent.");
    
    // get the hiring user
    HiringUser hiringUser = hiringUserFactory.getUser(feedbackUser.getFeedbackFor());
    Assert.notNull(hiringUser, "User not found.");
    
    // send the feedback request
    sendFeedbackRequest(hiringUser, feedbackUser);
    
    // sending the feedback user details back
    return new HiringLensDTO(feedbackUser);
  }
  
  @Override
  public HiringLensDTO update(User user, HiringLensForm form) {
    FeedbackUser feedbackUser = getFeedbackUser(user, form);
    
    // get the hiring user
    HiringUser hiringUser = hiringUserFactory.getUser(feedbackUser.getFeedbackFor());
    Assert.notNull(hiringUser, "User not found.");
    
    // validate the reference data for candidates
    validateReference(form, hiringUser);
    
    form.update(hiringUser, feedbackUser);
    factoryCache.save(feedbackUser);
    
    return new HiringLensDTO(feedbackUser);
  }
  
  /**
   * Update the feedback users reminded on.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  public void updateRemindedOn(FeedbackUser feedbackUser) {
    feedbackUser.setRemindedOn(LocalDateTime.now());
    factoryCache.save(feedbackUser);
  }
  
  /**
   * Delete all the lens requests for the given user id.
   * 
   * @param feedbackFor
   *          - user id
   */
  public void deleteLensReqeusts(String feedbackFor) {
    List<FeedbackUser> byUserFor = factoryCache.getByUserFor(feedbackFor);
    byUserFor.forEach(this::delete);
  }
  
  @Override
  public void delete(User user, HiringLensForm form) {
    FeedbackUser feedbackUser = getFeedbackUser(user, form);
    delete(feedbackUser);
  }
  
  /**
   * Delete the given feedback user.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  private void delete(FeedbackUser feedbackUser) {
    factoryCache.delete(feedbackUser);
    // deleting all the tokens associated with the feedback user.
    final String tokenId = feedbackUser.getTokenId();
    if (tokenId != null) {
      tokenFactory.removeToken(tokenId);
    }
  }
  
  /**
   * Get the feedback user for the given id.
   * 
   * @param userId
   *          - user id
   * @return the feedback user
   */
  public FeedbackUser getFeedbackUser(String userId) {
    return factoryCache.getById(userId);
  }
  
  /**
   * Get the feedback user from the form. Also validate the access.
   * 
   * @param user
   *          - user
   * @param form
   *          - form
   * @return the feedback user
   */
  private FeedbackUser getFeedbackUser(User user, HiringLensForm form) {
    FeedbackUser feedbackUser = factoryCache.getById(form.getId());
    Assert.notNull(feedbackUser, "User not found.");
    Assert.isTrue(feedbackUser.getCompanyId().equals(user.getCompanyId()), "Unauthorized access.");
    return feedbackUser;
  }
  
  /**
   * Send the feedback request for the given user.
   * 
   * @param hiringUser
   *          - hiring user
   * @param feedbackUser
   *          - feedback user
   */
  private void sendFeedbackRequest(HiringUser hiringUser, FeedbackUser feedbackUser) {
    Map<String, Object> paramsMap = new HashMap<String, Object>();
    paramsMap.put(Constants.PARAM_FEEDBACK_USERID, feedbackUser.getId());
    Token token = tokenFactory.getToken(TokenType.PERPETUAL, paramsMap,
        TokenProcessorType.HIRING_LENS);
    final String tokenUrl = token.getTokenUrl();
    
    feedbackUser.setTokenId(token.getId());
    feedbackUser.setTokenUrl(tokenUrl);
    
    Map<String, Object> emailParamsMap = new HashMap<String, Object>();
    NotificationType notificationType = (hiringUser.getType() == UserType.HiringCandidate) ? NotificationType.HiringLensCandidateInvite
        : NotificationType.HiringLensEmployeeInvite;
    notificationsProcessor.process(notificationType, hiringUser, feedbackUser, emailParamsMap);
    feedbackUser.setUserStatus(UserStatus.ASSESSMENT_PENDING);
    feedbackUser.setCreatedOn(LocalDate.now());
    factoryCache.save(feedbackUser);
  }
}
