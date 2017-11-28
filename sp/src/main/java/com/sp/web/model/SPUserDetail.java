package com.sp.web.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @author pradeepruhil
 *
 */
public class SPUserDetail implements UserDetails {
  
  private static final long serialVersionUID = -7897026466898649303L;
  
  private User user;
  
  private final Set<? extends GrantedAuthority> authorities;
  
  /**
   * Constructor for user detail.
   */
  public SPUserDetail(User user, Set<GrantedAuthority> authorities) {
    this.user = user;
    this.authorities = authorities;
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
   */
  @Override
  public String getUsername() {
    return user.getEmail();
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
  
  /**
   * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
  
  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }
}
