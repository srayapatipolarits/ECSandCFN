package com.sp.web.repository.tutorial;

import com.sp.web.model.tutorial.UserTutorialActivity;
import com.sp.web.repository.generic.GenericMongoRepository;

public interface UserTutorialActivityRepository extends GenericMongoRepository<UserTutorialActivity> {

  /**
   * Get the user tutorial activity for the given user id.
   * 
   * @param userId
   *        - user id
   * @return
   *    the user tutorial activity
   */
  UserTutorialActivity findByUserId(String userId);
  
}
