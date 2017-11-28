package com.sp.web.service.tracking;

import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.repository.tracking.ActivityTrackingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The activity tracking factory.
 */
@Component
public class ActivityTrackingFactory {
  
  @Autowired
  ActivityTrackingRepository activityTrackingRepository;
  
  /**
   * Create a new activity tracking instance.
   * 
   * @param activityTracking
   *          - activity tracking
   */
  public void create(ActivityTracking activityTracking) {
    activityTrackingRepository.save(activityTracking);
  }
  
  /**
   * Find the activity feeds after the given date.
   * 
   * @param companyId
   *            - company id
   * @param dateToCheck
   *            - date to check
   * @return
   *      the list of activity tracking
   */
  public List<ActivityTracking> findActivityFeedAfter(String companyId, LocalDateTime dateToCheck) {
    return activityTrackingRepository.findFeedAfter(companyId, dateToCheck);
  }

  /**
   * Updating the user activity tracking.
   * 
   * @param activityTracking
   *              - activity tracking
   */
  public void updateActivityTracking(ActivityTracking activityTracking) {
    activityTrackingRepository.save(activityTracking);
  }
  
}
