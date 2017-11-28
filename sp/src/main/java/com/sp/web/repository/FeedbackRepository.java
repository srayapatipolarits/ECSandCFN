package com.sp.web.repository;

import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackArchiveRequest;
import com.sp.web.model.FeedbackRequest;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.model.User;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The feedback repository interface.
 */
public interface FeedbackRepository {

  /**
   * Add the given feedback user.
   * 
   * @param fbUser
   *          - the feedback user
   * @return the created feedback user
   */
  FeedbackUser addFeedbackUser(FeedbackUser fbUser);

  /**
   * Find the given feedback user.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the feedback user
   */
  FeedbackUser findByIdValidated(String feedbackUserId);

  /**
   * Update the given feedback user.
   * 
   * @param fbUser
   *          - feedback user to update
   * @return the updated feedback user
   */
  FeedbackUser updateFeedbackUser(FeedbackUser fbUser);

  /**
   * <code>getAllFeedbackRequest</code> method will return the all the feedback request for the
   * requsted by user.
   * 
   * @param requestedById
   *          mongo id for the requested by uesr.
   * @return the list of FeedbackRequest.
   */
  List<FeedbackRequest> getAllFeedbackRequest(String requestedById);
  
  /**
   * Method to get all the feedback requests.
   * 
   * @return
   *      the feedback request
   */
  List<FeedbackRequest> getAllFeedbackRequest();

  /**
   * <code>getAllArchivedFeedbackRequest</code> method will return all the archived feedback
   * request.
   * 
   * @param requestdById
   *            - requested by id
   * @return
   *    the feedback request
   */
  List<? extends FeedbackRequest> getAllArchivedFeedbackRequests(String requestedById);

  /**
   * <code>createFeedbackRequest</code> method will create a new feedback request
   * 
   * @param feedbackRequest
   *          to be created.
   */
  void createFeedbackRequest(FeedbackRequest feedbackRequest);

  /**
   * <code>findFeedbackRequest</code> method will fetch the feedback request created for the user.
   * 
   * @param feedbackRequestId
   *          feedbackr equest id
   * @return feedbak Request.
   */
  FeedbackRequest findFeedbackRequest(String feedbackRequestId);

  /**
   * <code>findAllFeebdackRequest</code> method will return all the feedback request for the user.
   * 
   * @param feedbackUserId
   *          recieved by the user.
   */
  List<FeedbackRequest> findAllFeebdackRequest(String feedbackUserId);

  /**
   * <code>getAllFeedbackUsers</code> return all the feedback user where feedback user id mathces
   * feedback suer id.
   * 
   * @param feedbackUserId
   *          feebdack user id
   * @return list of feedback users.
   */
  List<FeedbackUser> getAllFeedbackUsers(String feedbackUserId);

  /**
   * <code>updateFeedbackRequest</code> method will update the feedback request.
   * 
   * @param feedbackRequest
   *          update the feebdakc request.
   */
  void updateFeedbackReqest(FeedbackRequest feedbackRequest);

  /**
   * Gets the feedback request for the given feedback user id.
   * 
   * @param feedbackUserId
   *          - feedback user id
   * @return the feedback request for the feedback user
   */
  FeedbackRequest findFeedbackRequestByFeedbackUserId(String feedbackUserId);

  /**
   * Archive the feedback request for the feedback user.
   * 
   * @param feedbackRequest
   *          - feedback user id
   * @param user
   *          - user 
   * @return 
   *    the feedback archive request
   */
  FeedbackArchiveRequest archiveFeedbackRequest(FeedbackRequest feedbackRequest, User user);

  /**
   * Get all the feedback users.
   * 
   * @param user
   *        - user
   * @return
   *    all feedback users
   */
  List<FeedbackUser> getAllFeedbackUser(User user);

  /**
   * Get all feedback archive users.
   * 
   * @param user
   *          - user
   * @return
   *    the list of feedback archive users
   */
  List<FeedbackUserArchive> getAllFeedbackUserArchive(User user);

  /**
   * Find the feedback user by the given email and feedback for id.
   * 
   * @param email
   *          - email
   * @param id
   *          - feedback for id
   * @return
   *    the feedback user
   */
  FeedbackUser findFeedbackUserByFor(String email, String id);

  /**
   * Remove the given feedback user.
   * 
   * @param feedbackUser
   *          - feedback user
   */
  void remove(FeedbackUser feedbackUser);
  
  
  int getPendingFeedbackRequests(List<String> reqestedByIds);
  
   /**
   * Remove the given feedback user.
   * 
   * @param feedbackRequest
   *          - feedback requesr
   */
  void removeFeedbackRequest(FeedbackRequest feedbackRequest);
  
  /**
   * Remove the given feedback user.
   * 
   * @param feedbackRequest
   *          - feedback requesr
   */
  void removeFeedbacArchivedRequest(FeedbackArchiveRequest feedbackArchiveRequest);

  /**
   * Remove the given feedback Archive user.
   * 
   * @param feedbackUserArchive
   *          - feedbackArchive user
   */
  void removeFeedbackUserArchive(FeedbackUserArchive feedbackUserArchive);

  /**
   * Get the feedback user for the given member id and feedback for id and feature type.
   * 
   * @param memberId
   *          - member id
   * @param feedbackForId
   *          - feedback for id
   * @param featureType
   *          - feature type
   *      
   * @return
   *    the feedback user found else null
   */
  FeedbackUser findFeedbackUser(String memberId, String feedbackForId, FeatureType featureType);

  /**
   * Get all the feedback users for the given feature type and feedback for id.
   * 
   * @param feedbackFor
   *            - feedback for
   * @param featureType
   *            - feature type
   * @return
   *     the list of feedback users
   */
  List<FeedbackUser> findFeedbackUsers(String feedbackFor, FeatureType featureType);

  /**
   * Get the feedback user for the given email and feature.
   * 
   * @param email
   *          - email 
   * @param userFor
   *          - user for id
   * @param featureType
   *          - feature type
   * @return
   *    the feedback user found
   */
  FeedbackUser findFeedbackUserByEmail(String email, String userFor, FeatureType featureType);
}
