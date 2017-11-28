package com.sp.web.service.session.mock;

import com.sp.web.model.User;
import com.sp.web.service.session.SessionManagementService;
import com.sp.web.service.session.UserUpdateAction;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class to remove user sessions.
 * 
 * @author Dax Abraham
 */
@Service("sessionManagementService")
@Profile({ "Test" })
public class MockSessionManagementService implements SessionManagementService {
  
  private static final Logger LOG = Logger.getLogger(MockSessionManagementService.class);
  
  public void expireSessionForUsers(List<User> userList) {
    LOG.info("Expire session for user called.");
  }
  
  public void expireSessionForCompany(String companyId) {
    LOG.info("Expire session for company called.");
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.service.session.SessionManagementService#updateSession(java.lang.String,
   * com.sp.web.service.session.UserUpdateAction, java.util.Map)
   */
  @Override
  public void updateSession(String userId, UserUpdateAction action, Map<String, Object> params) {
    // TODO Auto-generated method stub
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.service.session.SessionManagementService#updateSession(java.lang.String,
   * com.sp.web.service.session.UserUpdateAction)
   */
  @Override
  public void updateSession(String userId, UserUpdateAction action) {
    // TODO Auto-generated method stub
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * com.sp.web.service.session.SessionManagementService#updateSessionForCompany(java.lang.String,
   * com.sp.web.service.session.UserUpdateAction, java.util.Map)
   */
  @Override
  public void updateSessionForCompany(String companyId, UserUpdateAction action,
      Map<String, Object> params) {
    // TODO Auto-generated method stub
    
  }
  
}
