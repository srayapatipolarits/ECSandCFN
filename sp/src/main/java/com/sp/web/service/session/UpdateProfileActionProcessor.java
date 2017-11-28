package com.sp.web.service.session;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.CompanyLogoDTO;
import com.sp.web.dto.UserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.navigation.NavigationFactory;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author vikram
 *
 *         The class to process UpdateProfile action.
 */
@Component("UpdateProfileActionProcessor")
public class UpdateProfileActionProcessor implements UpdateSessionActionProcessor {

  private static final Logger LOG = Logger.getLogger(UpdateProfileActionProcessor.class);

  @Autowired
  NavigationFactory navigationFactory;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private CompanyFactory companyFactory;

  /**
   * Method creates SessionUpdateActionResponse object based on params and 
   * populates updatesMap sessionid, actionMap. It sends updated user and updated navigation object
   * 
   */
  @Override
  public void doUpdate(User user, UserUpdateAction action, Map<String, Object> params,
      Map<String, Map<UserUpdateAction, Object>> updatesMap, SessionInformation session) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering UpdateProfileActionProcessor doUpdate");
    }
    
    Map<UserUpdateAction, Object> actionMap = updatesMap.get(session.getSessionId());
    
    // First request, new map will be created. For subsequent requests for same user, reuse the map
    if (actionMap == null) {
      actionMap = new HashMap<UserUpdateAction, Object>();
    }
    
    User updatedUser = userRepository.findUserById(user.getId());
    if (updatedUser == null) {
      throw new InvalidRequestException(user.getId() + " User not found in DB");
    }
    
    CompanyDao company = companyFactory.getCompany(user.getCompanyId());
    Set<RoleType> roles = company.getRoleList();
    if (roles != null) {
      updatedUser.getRoles().addAll(roles);
    }
    actionMap.put(UserUpdateAction.UpdateParams, params);

    //update user roles based on roles/features given to company during account update
    //excluding admin as his records are already updated during account update in accountfactory
    /*if (params != null) {
      if (params.containsKey(Constants.PARAM_COMPANY)
          && !updatedUser.getRoles().contains(RoleType.AccountAdministrator)) {
        
        Set<RoleType> roles = company.getRoleList();
        if (roles != null) {
          updatedUser.getRoles().addAll(roles);
        }
      }
      actionMap.put(UserUpdateAction.UpdateParams, params);
    }*/
    
    // adding user details
    Map<String, Object> userMap = new HashMap<String, Object>();
    userMap.put(Constants.PARAM_MEMBER, new UserDTO(updatedUser));
    
    // adding company details
    userMap.put(Constants.PARAM_COMPANY, new CompanyLogoDTO(company));
    
    actionMap.put(action, userMap);
    
    Map<String, Object> navigationMap = new HashMap<String, Object>();
    navigationMap.put(Constants.PARAM_NAVIGATION, navigationFactory.getNavigation(updatedUser));
    
    actionMap.put(UserUpdateAction.UpdateNavigation, navigationMap);
    
    updatesMap.put(session.getSessionId(), actionMap);
    
    // updating session user with updatedUser data
    BeanUtils.copyProperties(updatedUser, user);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Successfully updated actionMap and updatesMap for user:" + updatedUser.getId()
          + " and action:" + action);
    }
  }
}
