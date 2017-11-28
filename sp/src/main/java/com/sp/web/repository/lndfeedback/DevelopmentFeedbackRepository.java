package com.sp.web.repository.lndfeedback;

import com.sp.web.model.RequestStatus;
import com.sp.web.model.User;
import com.sp.web.model.lndfeedback.DevelopmentFeedback;
import com.sp.web.model.lndfeedback.UserDevelopmentFeedbackResponse;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * DevelopmentFeedbackRepository.
 * 
 * @author pradeepruhil
 *
 */
public interface DevelopmentFeedbackRepository extends GenericMongoRepository<DevelopmentFeedback> {
  
  /**
   * findByUserId will return all the development feedback requested by the user.
   * 
   * @param id
   *          is the userId.
   * @return the list of development feedback for the user.
   */
  List<DevelopmentFeedback> findByUserId(String id);
  
  /**
   * findAllByParentRefId will find all feedback request by parent id.
   * 
   * @param parentRefId
   *          is the parent id for which development feedback is requested.
   * @param companyId
   *          is the company id for the development feedback.
   * @return the all the feedback for the parent ref id..
   */
  List<DevelopmentFeedback> findAllByParentRefId(String parentRefId, String companyId);
  
  /**
   * Get all the development feedbacks for the given parent reference id.
   * 
   * @param parentRefId
   *              - parent reference id
   * @return
   *      the list of development feedback
   */
  List<DevelopmentFeedback> findAllByParentRefId(String parentRefId);
  
  /**
   * findAllByDevFeedRefid method will find all the development feedback request with the company
   * id.
   * 
   * @param devFeedRefId
   *          is the dev feed ref id.
   * @param companyId
   *          is company id
   * @return list of development feedbacks.
   */
  List<DevelopmentFeedback> findAllByDevFeedRefId(String devFeedRefId, String companyId,
      String feature);
  
  /**
   * Find all the development feedback requests with the given feed reference id.
   * 
   * @param devFeedRefId
   *            - development feed reference id
   * @return
   *    the list of development feedback requests
   */
  List<DevelopmentFeedback> findAllByDevFeedRefId(String devFeedRefId);
  
  /**
   * findAllByDevFeedRefid method will find all the development feedback request with the company
   * id.
   * 
   * @param devFeedRefId
   *          is the dev feed ref id.
   * @param userId
   *          for which dev feedbacks to be sent.
   * @return list of development feedbacks.
   */
  List<DevelopmentFeedback> findAllByDevFeedRefIdUserId(String devFeedRefId, String userId,
      String feature);
  
  /**
   * Return the development feedback by token id.
   * 
   * @param tokenId
   *          to be check.
   * @return the development feedback.
   */
  DevelopmentFeedback findDevFeedbackbyTokenId(String tokenId);
  
  /**
   * deleteAllByDevFeedRefId will remove all the development feedbacks for the user.
   * 
   * @param devFeedRefId
   *          dev feedbacks.
   * @param companyId
   *          of the user.
   * @param feature
   *          sp features.
   */
  public void deleteAllByDevFeedRefId(String devFeedRefId, String companyId, String feature);
  
  /**
   * getAllFeedbackUserRequest will return all the feedback request as per the status.
   * 
   * @param email
   *          feedback user email.
   * @param requestStatus
   *          is development feedback status.
   * @return the list of development feedbacks.
   */
  List<DevelopmentFeedback> getAllFeedbackUserRequest(String email, RequestStatus requestStatus);
  
  /**
   * Get all the development feedback for the given user with the development ref id and feature.
   * 
   * @param member
   *          - user
   * @param devFeedRefId
   *          - dev feed ref id
   * @return list of developement feedback
   * 
   */
  List<DevelopmentFeedback> findByUserAndFeedRefId(User member, String devFeedRefId);
  
  /**
   * Get all the feedback with the given feed parent reference id.
   * 
   * @param member
   *          - member
   * @param feedParentRefId
   *          - feed parent reference id
   * @return list of development feedback
   */
  List<DevelopmentFeedback> findByUserAndFeedParentRefId(User member, String feedParentRefId);

  /**
   * Get the users development feedback response object.
   * 
   * @param userId
   *          - user id
   * @return
   *    the users development feedback response
   */
  UserDevelopmentFeedbackResponse getDevelopmentFeedbackResponse(String userId);

  /**
   * Update the users development feedback response.
   *  
   * @param feedbackResponse
   *          - user development feedback response
   */
  void update(UserDevelopmentFeedbackResponse feedbackResponse);
  
}
