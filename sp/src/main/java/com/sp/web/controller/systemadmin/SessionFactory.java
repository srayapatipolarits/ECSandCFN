package com.sp.web.controller.systemadmin;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.authentication.SPAuthority;
import com.sp.web.dto.UserDTO;
import com.sp.web.dto.UserSessionDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPUserDetail;
import com.sp.web.model.SessionUpdateActionRequest;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.session.UpdateSessionActionProcessor;
import com.sp.web.service.session.UserUpdateAction;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

/**
 * @author Dax Abraham
 *
 *         The factory class to help with the session information management.
 */
@Component
public class SessionFactory {
  
  private static final Logger LOG = Logger.getLogger(SessionFactory.class);
  
  @Autowired
  @Qualifier("sessionRegistry")
  private SessionRegistry sessionRegistry;
  
  /**
   * First Map is user id and sessionMap. Session Map is session id and actionMap
   * 
   */
  private static final Map<String, Map<String, Map<UserUpdateAction, Object>>> pendingUpdates = new HashMap<String, Map<String, Map<UserUpdateAction, Object>>>();
  
  private ReentrantLock lock = new ReentrantLock();
  
  @Autowired
  private AccountRepository accountRepository;
  
  /**
   * Get the list of logged in users in the system.
   * 
   * @return - list of logged in users
   */
  public List<UserSessionDTO> getLoggedInUsers() {
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    ArrayList<UserSessionDTO> userList = new ArrayList<UserSessionDTO>();
    for (Object principal : allPrincipals) {
      if (principal instanceof User || principal instanceof SPUserDetail) {
        User user = principal instanceof User ? (User) principal : ((SPUserDetail) principal)
            .getUser();
        UserSessionDTO sessionUser = new UserSessionDTO(user);
        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);
        List<String> collect = allSessions.stream().map(SessionInformation::getLastRequest)
            .map(d -> {
              return MessagesHelper.formatDate(d, "MMM dd, yyyy h:mma");
            }).collect(Collectors.toList());
        Collections.reverse(collect);
        sessionUser.setSessionTimeList(collect);
        userList.add(sessionUser);
      }
    }
    return userList;
  }
  
  /**
   * Checks action performed on logged in user and invokes respective processor. processor populates
   * the actionMap & updatesMap which finally gets set in PendingUpdates Map
   * 
   * @param request
   *          - the update action request.
   * 
   */
  public void doUpdate(SessionUpdateActionRequest request) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering SessionFactory doUpdate.");
    }
    
    String userId = request.getUserId();
    
    if (StringUtils.isEmpty(userId) || request.getAction() == null) {
      throw new InvalidRequestException("User Id or UserupdateAction Empty");
    }
    
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    
    for (Object principal : allPrincipals) {
      if (principal instanceof User || principal instanceof SPUserDetail) {
        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);
        User user = principal instanceof User ? (User) principal : ((SPUserDetail) principal)
            .getUser();
        
        Map<String, Map<UserUpdateAction, Object>> updatesMap = null;
        
        // allsessions will be more than 1 if same user logs in from multiple devices/browsers
        for (SessionInformation session : allSessions) {
          
          String id = user.getId();
          
          // check if user in session matches with user on whom action is performed
          if (id.equalsIgnoreCase(userId)) {
            
            // going into a synchronized block
            // acquiring the lock
            try {
              lock.lock();
              
              // For subsequent request for same user, we need to use existing map for that user
              updatesMap = pendingUpdates.get(user.getId());
              
              // First Request from user, new map will be created.
              if (updatesMap == null) {
                updatesMap = new HashMap<String, Map<UserUpdateAction, Object>>();
                pendingUpdates.put(userId, updatesMap);
              }
              // Get processor based on action
              UpdateSessionActionProcessor processor = (UpdateSessionActionProcessor) ApplicationContextUtils
                  .getApplicationContext().getBean(
                      request.getAction().getUpdateSessionActionProcessor());
              processor.doUpdate(user, request.getAction(), request.getParams(), updatesMap,
                  session);
              
            } catch (SPException spe) {
              LOG.warn("Error processing the UpdateSessionActionProcessor request.", spe);
              throw spe;
            } catch (Exception e) {
              LOG.warn("Error processing the UpdateSessionActionProcessor request.", e);
              throw new SPException(e);
            } finally {
              // Unlocking the synchronize block
              lock.unlock();
            }
          }
        }
        /*
         * if(updatesMap!=null) { pendingUpdates.put(userId, updatesMap); }
         */
        
      }
    }
    
  }
  
  /**
   * Updates SPResponse object with pending action updates for logged in user
   * 
   * @param user
   *          - the user on whom action is performed
   * @param response
   *          - the SPResponse
   * 
   */
  public static void updatePendingActions(User user, SPResponse response) {
    
    HttpSession session = getSession();
    Map<String, Map<UserUpdateAction, Object>> updatesMap = pendingUpdates.get(user.getId());
    String sessionId = session.getId();
    
    // Same user can login multiple times from different devices/browser.
    // We need to update SPResponse for correct session
    if (updatesMap != null) {
      Map<UserUpdateAction, Object> obj = updatesMap.get(sessionId);
      
      if (obj != null) {
        response.add(Constants.ACTIONS, obj);
        obj.forEach((upAction, Obj) -> {
          if (upAction == UserUpdateAction.PulseUpdate
              || upAction == UserUpdateAction.UpdatePermission) {
            Map userMap = (Map) obj.get(upAction);
            UserDTO userDto = (UserDTO) userMap.get(Constants.PARAM_MEMBER);
            user.setRoles(userDto.getRoles());
            // we only need to add Actions map 1 time in that request flow
            updatesMap.remove(sessionId);
            // update security context
            updateUser(user);
          }
        });
        
      }
      
    }
  }
  
  /**
   * Gets logged in user current session
   * 
   * @return - the HttpSession
   */
  public static HttpSession getSession() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes();
    return attr.getRequest().getSession();
  }
  
  /**
   * Method to clean up all the stale requests in the pendingupdates map. It gets the updatesMap
   * based on the user present in registry, gets sessions for that user. If no sessions found, it
   * removes user from the map If sessions found and they are expired, it removes session/action map
   * from updatesmap
   * 
   */
  public void cleanSessionUpdateRequests() {
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    for (Object principal : allPrincipals) {
      if (principal instanceof User || principal instanceof SPUserDetail) {
        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);
        User user = principal instanceof User ? (User) principal : ((SPUserDetail) principal)
            .getUser();
        Map updatesMap = pendingUpdates.get(user.getId());
        if (updatesMap != null) {
          if (allSessions.isEmpty()) {
            pendingUpdates.remove(user.getId());
          } else {
            for (SessionInformation session : allSessions) {
              if (session.isExpired()) {
                updatesMap.remove(session.getSessionId());
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * <code>updatedUser</code> in the authentication with updated profile
   * 
   * @param updatedUser
   *          detail to be set in the authentication context
   */
  public static void updateUser(User updatedUser) {
    Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
    UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(
        updatedUser, null, getAuthorities(updatedUser));
    
    if (previousAuthentication != null) {
      authenticated.setDetails(previousAuthentication.getDetails());
    }
    
    SecurityContextHolder.getContext().setAuthentication(authenticated);
  }
  
  /**
   * @param user
   *          - the user to get the authorities for
   * @return - the list of authorities for the user
   */
  private static List<GrantedAuthority> getAuthorities(User user) {
    List<GrantedAuthority> grantedAuthoritiesList = new ArrayList<GrantedAuthority>();
    if (user.getRoles() != null) {
      for (RoleType role : user.getRoles()) {
        grantedAuthoritiesList.add(new SPAuthority(role));
      }
    }
    return grantedAuthoritiesList;
  }
  
  /**
   * Processes request for all users of the company
   * 
   * @param request
   *          - the update action request.
   * 
   */
  public void doCompanyUpdate(SessionUpdateActionRequest request) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering SessionFactory doCompanyUpdate.");
    }
    
    String companyId = request.getCompanyId();
    
    if (StringUtils.isEmpty(companyId) || request.getAction() == null) {
      throw new InvalidRequestException("company Id or UserupdateAction Empty");
    }
    
    List<User> users = accountRepository.getAllMembersForCompany(companyId);
    users.stream().forEach(usr -> {
      SessionUpdateActionRequest userRequest = new SessionUpdateActionRequest();
      userRequest.setUserId(usr.getId());
      userRequest.setAction(request.getAction());
      userRequest.setParams(request.getParams());
      doUpdate(userRequest);
    });
    
  }
  
  /**
   * getUserForSession method will return the user for the session id passed.
   * 
   * @param sessionId
   *          for which user to be retreived.
   * @return the user.
   */
  public User getUserForSession(String sessionId) {
    SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
    if (sessionInformation != null) {
      Object principal = sessionInformation.getPrincipal();
      User user = principal instanceof User ? (User) principal : ((SPUserDetail) principal).getUser();
      return user;
    }
    return null;
  }
  
}
