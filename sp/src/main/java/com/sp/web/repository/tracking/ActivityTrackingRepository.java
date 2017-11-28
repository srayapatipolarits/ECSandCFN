package com.sp.web.repository.tracking;

import com.sp.web.model.tracking.ActivityTracking;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The repository interface for activity tracking.
 */
public interface ActivityTrackingRepository extends GenericMongoRepository<ActivityTracking> {
  
  /**
   * All the activity feeds that have not been posted to dashboard after the given date.
   * 
   * @param companyId
   *          - company id
   * @param dateToCheck
   *          - date
   * @return the list of activity tracking
   */
  List<ActivityTracking> findFeedAfter(String companyId, LocalDateTime dateToCheck);
}
