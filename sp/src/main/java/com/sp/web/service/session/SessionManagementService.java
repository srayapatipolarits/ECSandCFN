package com.sp.web.service.session;

import java.util.Map;

/**
 * Service class to remove user sessions.
 * 
 * @author vikram
 */
@Deprecated
public interface SessionManagementService {
  
  /**
   * updateSession method will update the session for the user id passed.
   * 
   * @param userId
   *          logged in user.
   * @param action
   *          user update action for which respective action will be performed for the user.
   * @param params
   *          params required for the session update.
   */
  @Deprecated
  public void updateSession(String userId, UserUpdateAction action, Map<String, Object> params);
  
  /**
   * udpate session for the passed user id.
   * 
   * @param userId
   *          for which session is to be updated.
   * @param action
   *          user udpate action.
   */
  @Deprecated
  public void updateSession(String userId, UserUpdateAction action);
  
  /**
   * updateSessionForCompany method will update the session for the company
   * 
   * @param companyId
   *          for the user.
   * @param action
   *          update action to be performed.
   * @param params
   *          contains the paramteres required during the udpate session.
   */
  @Deprecated
  public void updateSessionForCompany(String companyId, UserUpdateAction action,
      Map<String, Object> params);
  
}
