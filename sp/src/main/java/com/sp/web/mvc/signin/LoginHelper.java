/**
 * 
 */
package com.sp.web.mvc.signin;

import com.sp.web.authentication.SPAuthority;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPUserDetail;
import com.sp.web.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * LoginHelper class provides helper method for login.
 * 
 * @author pradeep
 *
 */
@Component
public class LoginHelper implements Serializable {
  
  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = 1135556275246217406L;
  
  /** Authentication provider for SP */
  @Autowired
  AuthenticationProvider authenticationProvider;
  
  @Autowired
  private HttpServletRequest httpServletRequest;
  
  /**
   * Method to authenticate the user after signup
   * 
   * @param user
   *          - user to signup
   * @param request
   *          - http request
   */
  public void authenticateUserAndSetSession(String userName, String password) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName,
        password);
    
    // generate session if one doesn't exist
    httpServletRequest.getSession();
    
    token.setDetails(new WebAuthenticationDetails(httpServletRequest));
    Authentication authenticatedUser = authenticationProvider.authenticate(token);
    
    SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
  }
  
  /**
   * Method to authenticate the user after signup
   * 
   * @param user
   *          - user to signup
   * @param request
   *          - http request
   */
  public void authenticateUserAndSetSession(User user) {
    
    List<? extends GrantedAuthority> authorities = getAuthorities(user);
    UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(
        user, null, authorities);
    authenticated.setDetails(new WebAuthenticationDetails(httpServletRequest));
    
    // generate session if one doesn't exist
    httpServletRequest.getSession();
    
    authenticated.setDetails(new WebAuthenticationDetails(httpServletRequest));
    
    SecurityContextHolder.getContext().setAuthentication(authenticated);
  }
  
  /**
   * <code>updatedUser</code> in the authentication with updated profile
   * 
   * @param updatedUser
   *          detail to be set in the authentication context
   */
  public void updateUser(User updatedUser) {
    Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
    UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(
        updatedUser, null, getAuthorities(updatedUser));
    
    authenticated.setDetails(previousAuthentication.getDetails());
    SecurityContextHolder.getContext().setAuthentication(authenticated);
  }
  
  /**
   * @param user
   *          - the user to get the authorities for
   * @return - the list of authorities for the user
   */
  private List<? extends GrantedAuthority> getAuthorities(User user) {
    List<GrantedAuthority> grantedAuthoritiesList = new ArrayList<GrantedAuthority>();
    if (user.getRoles() != null) {
      for (RoleType role : user.getRoles()) {
        grantedAuthoritiesList.add(new SPAuthority(role));
      }
    }
    return grantedAuthoritiesList;
  }
  
  public void updateAuthenticationDetail(Authentication authentication, User user) {
    
    Object principal = authentication.getPrincipal();
    if (principal instanceof SPUserDetail) {
      
      /* updadae the autheorieties */
      SPUserDetail spUserDetail = (SPUserDetail) principal;
      spUserDetail = new SPUserDetail(user, getAuthoritiesSet(user));
      RememberMeAuthenticationToken previousRememberMe = (RememberMeAuthenticationToken) authentication;
      
      RememberMeAuthenticationToken authenticated = new RememberMeAuthenticationToken(
          "SurePeople!@112212", spUserDetail, getAuthorities(user));
      
      authenticated.setDetails(previousRememberMe.getDetails());
      SecurityContextHolder.getContext().setAuthentication(authenticated);
      
    } else {
      updateUser(user);
    }
  }
  
  /**
   * Get the authority list.
   * 
   * @param user
   *          - the user to get the authorities for
   * @return - the list of authorities for the user
   */
  private Set<GrantedAuthority> getAuthoritiesSet(User user) {
    Set<GrantedAuthority> grantedAuthoritiesList = new HashSet<GrantedAuthority>();
    if (user.getRoles() != null) {
      for (RoleType role : user.getRoles()) {
        grantedAuthoritiesList.add(new SPAuthority(role));
      }
    }
    return grantedAuthoritiesList;
  }
}
