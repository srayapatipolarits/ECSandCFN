/**
 * 
 */
package com.sp.web.respository.systemadmin;

import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.model.UserType;
import com.sp.web.model.audit.AuditLogBean;
import com.sp.web.model.library.TipOfTheDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author pradeepruhil
 *
 */
public interface SystemAdminRepository {
  
  void updateUserProperty(UserType userType, Map<String, String> propertyMap, String id);
  
  /**
   * @param userType
   * @param personalityType
   * @param id
   */
  void updateUserPersonality(UserType userType, PersonalityType personalityType, String id);
  
  /**
   * @param feedbackUser
   * @param type
   * @return
   */
  Object getUserFromType(Map<String, String> feedbackUser, UserType type);
  
  void addAuditLogs(AuditLogBean auditLog);
  
  List<AuditLogBean> findAuditLogs();
  
  /**
   * @param findAuditLogs
   */
  void removeAuditLogs(List<AuditLogBean> findAuditLogs);
  
  /**
   * Get the list of all the tips of the day from the database.
   * @return
   */
  List<TipOfTheDay> getAllTipsOfTheDay();

  /**
   * @param email
   */
  List<AuditLogBean> findAuditLogs(String email);

  /**
   * @param localDate
   * @param localDate2
   * @return
   */
  List<AuditLogBean> findAuditLogs(LocalDate localDate, LocalDate localDate2);

  /**
   * @param email
   * @param startDate
   * @param endDate
   * @return
   */
  List<AuditLogBean> findAuditLogs(String email, LocalDate startDate, LocalDate endDate);
}