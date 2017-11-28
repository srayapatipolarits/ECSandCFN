package com.sp.web.service.tracking;

import com.sp.web.model.User;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.tracking.ActivityTracking;

/**
 * @author Dax Abraham
 *
 *         The interface for activity tracking processor to track different activities.
 */
public interface ActivityTrackingProcessor {
  
  /**
   * Update the passed activity tracking with message etc.
   * 
   * @param user
   *          - user
   * @param actionType
   *          - action type
   * @param params
   *          - params
   * @param activityTracking
   *          - activity tracking 
   * @return
   *      the flag indicating if the activity should be tracked or not         
   */
  boolean updateActivityTracking(User user, LogActionType actionType, Object[] params,
      ActivityTracking activityTracking);
}
