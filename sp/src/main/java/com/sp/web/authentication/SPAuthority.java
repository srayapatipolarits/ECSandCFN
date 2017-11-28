/**
 * 
 */
package com.sp.web.authentication;

import com.sp.web.model.RoleType;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Dax Abraham
 * 
 *         The implementation of the granted authority for SP.
 */
public class SPAuthority implements GrantedAuthority {

  /**
   * The serial version UID
   */
  private static final long serialVersionUID = 30332371311831086L;

  /** The authority object */
  private String authority;

  public SPAuthority(String authority) {
    this.authority = authority;
  }

  public SPAuthority(RoleType role) {
    this.authority = role.name();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.core.GrantedAuthority#getAuthority()
   */
  @Override
  public String getAuthority() {
    return authority;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return authority.hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!(obj instanceof SPAuthority))
      return false;
    return ((SPAuthority) obj).getAuthority().equals(authority);
  }
}
