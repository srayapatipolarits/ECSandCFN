package com.sp.web.mvc.signin;

import com.sp.web.authentication.SPAuthority;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.SignInFailedException.SignInFailReason;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPUserDetail;
import com.sp.web.model.User;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * SPUserDetailService fetch the SPUserDetail and load the user from the email.
 * 
 * @author pradeepruhil
 *
 */
@Component("userDetailService")
public class SPUserDetailService implements UserDetailsService {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(SPUserDetailService.class);
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * loadByUserName method loads the User from email.
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    /*
     * Here add user data layer fetching from the MongoDB. I have used userRepository
     */
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new UsernameNotFoundException(email);
    } else {
      /* check the company and account detail whether the company is blocked and other stuff */
      
      if (user.getCompanyId() != null) {
        CompanyDao company = companyFactory.getCompany(user.getCompanyId());
        if (company == null) {
          throw new DisabledException("Company not found !!!");
        } else {
          if (company.isDeactivated()) {
            throw new AccountExpiredException(SignInFailReason.CompanyBlocked.toString());
          }
          if (company.isBlockAllMembers()) {
            if (!user.getRoles().contains(RoleType.AccountAdministrator)) {
              throw new LockedException(SignInFailReason.CompanyMemberBlocked.toString());
            }
          }
          // check features and add user role.
          // Role is tied up to feature getting selected for company
          Set<RoleType> roles = company.getRoleList();
          if (roles != null) {
            user.getRoles().addAll(roles);
          }
          
        }
      } else {
        if (user.isDeactivated()) {
          throw new AccountExpiredException(SignInFailReason.IndividualBlocked.toString());
        }
      }
      // Set the SPUSerDetail with the user.
      UserDetails details = new SPUserDetail(user, getAuthorities(user));
      return details;
    }
  }
  
  /**
   * Get the authority list.
   * 
   * @param user
   *          - the user to get the authorities for
   * @return - the list of authorities for the user
   */
  private Set<GrantedAuthority> getAuthorities(User user) {
    Set<GrantedAuthority> grantedAuthoritiesList = new HashSet<GrantedAuthority>();
    if (user.getRoles() != null) {
      for (RoleType role : user.getRoles()) {
        grantedAuthoritiesList.add(new SPAuthority(role));
        if (LOG.isDebugEnabled()) {
          LOG.debug("Adding role:" + role);
        }
      }
    }
    return grantedAuthoritiesList;
  }
}