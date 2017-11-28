package com.sp.web.service.session;

import com.sp.web.account.AccountRepository;
import com.sp.web.model.SessionUpdateActionRequest;
import com.sp.web.repository.user.UserRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service class to remove user sessions.
 * 
 * @author vikram
 */
@Service("sessionManagementService")
@Profile({ "PROD" })
@Deprecated
public class SessionManagementServiceImpl implements SessionManagementService {
  
  private static final Logger LOG = Logger.getLogger(SessionManagementService.class);
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private SessionUpdateMessagePublisher messagePublisher;
  
  /**
   * method will create SessionUpdateActionRequest and invoke messagePublisher to push it to Topic.
   * 
   * @param userId
   * 
   * @param action
   * 
   * @param params
   * 
   */
  @Deprecated
  @Override
  public void updateSession(String userId, UserUpdateAction action, Map<String, Object> params) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering updateSession for user:" + userId);
    }
    
    SessionUpdateActionRequest request = new SessionUpdateActionRequest();
    request.setUserId(userId);
    request.setAction(action);
    request.setParams(params);
    messagePublisher.sendSessionUpdateMessage(request);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Message sent successfully to Topic for user:" + userId + " and action:" + action);
    }
  }
  
  /**
   * method will create SessionUpdateActionRequest and invoke messagePublisher to push it to Topic.
   * 
   * @param userId
   * 
   * @param action
   * 
   * @param params
   * 
   */
  @Deprecated
  @Override
  public void updateSessionForCompany(String companyId, UserUpdateAction action,
      Map<String, Object> params) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering updateSession for company id:" + companyId);
    }
    
    SessionUpdateActionRequest request = new SessionUpdateActionRequest();
    request.setCompanyId(companyId);
    request.setAction(action);
    request.setParams(params);
    messagePublisher.sendSessionUpdateMessage(request);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Message sent successfully to Topic for company id:" + companyId + " and action:"
          + action);
    }
  }
  
  /**
   * method will create SessionUpdateActionRequest and invoke messagePublisher to push it to Topic.
   * 
   * @param userId
   * 
   * @param action
   * 
   * @param params
   * 
   */
  @Deprecated
  @Override
  public void updateSession(String userId, UserUpdateAction action) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering updateSession for user:" + userId);
    }
    SessionUpdateActionRequest request = new SessionUpdateActionRequest();
    request.setUserId(userId);
    request.setAction(action);
    messagePublisher.sendSessionUpdateMessage(request);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Message sent successfully to Topic for user:" + userId + " and action:" + action);
    }
  }
}
