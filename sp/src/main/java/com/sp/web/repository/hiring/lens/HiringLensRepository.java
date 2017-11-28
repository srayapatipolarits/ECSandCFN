package com.sp.web.repository.hiring.lens;

import com.sp.web.model.FeedbackUser;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 * 
 *         The repository interface for the hiring lens interface.
 */
public interface HiringLensRepository extends GenericMongoRepository<FeedbackUser> {

  /**
   * Get all the requests for the given user.
   * 
   * @param feedbackFor
   *          - feedback for
   * @return
   *      the list of users
   */
  List<FeedbackUser> findByUserFor(String feedbackFor);
  
}
