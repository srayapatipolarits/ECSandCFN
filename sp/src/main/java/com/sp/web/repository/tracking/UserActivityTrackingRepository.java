package com.sp.web.repository.tracking;

import com.sp.web.model.usertracking.TopPracticeTracking;
import com.sp.web.model.usertracking.UserActivityTracking;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * UserActivityTracking will track the user activity.
 * 
 * @author pradeepruhil
 *
 */
public interface UserActivityTrackingRepository extends
    GenericMongoRepository<UserActivityTracking> {
  
  /**
   * findUserActivityTracking will return list of userActivityTracking of a company with the
   * duration specified.
   * 
   * @param companyId
   *          for which user activity are to be retrieved.
   * @param startDate
   *          of the user activity tracking.
   * @param endDate
   *          end date of the user activities.F
   * @return the list of user activity tracking
   */
  List<UserActivityTracking> findUserActivityTracking(String companyId, LocalDate startDate,
      LocalDate endDate);
  
  /**
   * findUserActivityTrackingByIdAndDate method will return the user activity tracking by user id
   * and for the date passed.
   * 
   * @param userId
   *          of the user
   * @param date
   *          for which user activity is to be retrieved.
   * @return the UserActivityTracking.
   */
  UserActivityTracking findUserActivityTrackingByIdDate(String userId, LocalDate date);
  
  /**
   * findTopPracticeArea will return the top practice tracking.
   * 
   * @param companyId
   *          of the company
   * @param startDate
   *          of the year.
   * @return the top practice area.
   */
  TopPracticeTracking findTopPracticeArea(String companyId, LocalDate startDate);
  
  /**
   * findTopPracticeArea will return the practice area by year.
   * 
   * @param companyId
   *          of company
   * @param startDate
   *          ;
   * @return
   */
  List<TopPracticeTracking> findTopPracticeAreaFromDate(String companyId, LocalDate startDate);
  
  /**
   * saveTopPracticeArea will save the top practice into the database.
   * 
   * @param findTopPracticeArea
   *          top practice area.
   */
  void saveTopPracticeArea(TopPracticeTracking findTopPracticeArea);
  
  /**
   * deleteActivityTracking method will archive the activity tracking for the deleted user.
   */
  void deleteActivityTracking(String userId);
  
}
