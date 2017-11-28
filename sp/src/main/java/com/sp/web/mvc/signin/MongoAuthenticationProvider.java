package com.sp.web.mvc.signin;

import com.sp.web.account.AccountRepository;
import com.sp.web.authentication.SPAuthority;
import com.sp.web.dao.CompanyDao;
import com.sp.web.exception.InvalidPasswordException;
import com.sp.web.exception.SignInFailedException;
import com.sp.web.exception.SignInFailedException.SignInFailReason;
import com.sp.web.exception.SignInNotFoundException;
import com.sp.web.model.MasterPassword;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.password.PasswordManagerService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

/**
 * Adapts {@link AccountRepository#authenticate(String, String)} to the SpringSecurity
 * AuthenticationProvider SPI. Allows the AccountRepository to drive authentication in a Spring
 * Security environment. The authenticated Account is treated as the
 * {@link Authentication#getPrincipal() Authentication Principal}.
 * 
 * @author pruhil
 */
@Service("authenticationProvider")
public class MongoAuthenticationProvider implements AuthenticationProvider {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(MongoAuthenticationProvider.class);
  
  /** Mongo Account Repository. */
  private UserRepository userRepository;
  
  /**
   * Password Encoder holding the {@link BCryptPasswordEncoder} class reference.
   */
  private final PasswordEncoder passwordEncoder;
  
  private CompanyFactory companyFactory;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private HttpSession httpSession;
  
  @Autowired
  private PasswordManagerService passwordManagerService;
  
  /**
   * Constructor injecting the account repository.
   * 
   * @param userRepository
   *          Mongo user repository to perform operation on user profile
   */
  @Inject
  public MongoAuthenticationProvider(UserRepository userRepository,
      PasswordEncoder passwordEncoder, CompanyFactory companyFactory) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.companyFactory = companyFactory;
  }
  
  /**
   * <code>authenticate</code> method is called by spring security which authenticate the user with
   * credentials stored in the database.
   */
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    
    LOG.info("Enter authenticate(), method ");
    Authentication token = (Authentication) authentication;
    try {
      /*
       * Authenticate the user by calling the authenticate method in accout repository
       */
      User users = authenticateUser((token.getName()), (String) token.getCredentials());
      
      return authenticatedToken(users, authentication);
    } catch (SignInNotFoundException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Sign in not found.", e);
      }
      throw new UsernameNotFoundException(token.getName(), e);
    } catch (InvalidPasswordException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Invalid password.", e);
      }
      throw new BadCredentialsException(e.getAttemptsMessage(), e);
    }
  }
  
  /**
   * Supports method return the username password authentication token.
   * 
   * @param authentication
   *          Authentication object
   * @return true or false if is a child of authentication
   */
  public boolean supports(Class<? extends Object> authentication) {
    return UsernamePasswordAuthenticationToken.class.equals(authentication);
  }
  
  // internal helpers
  private Authentication authenticatedToken(User user, Authentication original) {
    List<GrantedAuthority> authorities = getAuthorities(user);
    UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(
        user, null, authorities);
    authenticated.setDetails(original.getDetails());
    return authenticated;
  }
  
  /**
   * Get the authority list.
   * 
   * @param user
   *          - the user to get the authorities for
   * @return - the list of authorities for the user
   */
  private List<GrantedAuthority> getAuthorities(User user) {
    List<GrantedAuthority> grantedAuthoritiesList = new ArrayList<GrantedAuthority>();
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
  
  /**
   * @see com.sp.web.assessment.account.AccountRepository#authenticateUser(java.lang.String,
   *      java.lang.String)
   * @param email
   *          of the user
   * @param password
   *          of the user
   */
  private User authenticateUser(String email, String password) throws SignInNotFoundException,
      InvalidPasswordException {
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Enter authenticateUser(), email" + email);
    }
    
    User user = userRepository.findByEmail(email.trim().toLowerCase());
    /* check if user is user is present in the repository or not */
    
    if (user == null) {
      throw new SignInNotFoundException(email);
    }
    
    // check if the user is deactivated
    if (user.isDeactivated()) {
      throw new SignInFailedException(SignInFailReason.IndividualBlocked);
    }
    
    CompanyDao company = null;
    if (user.getCompanyId() != null) {
      company = companyFactory.getCompany(user.getCompanyId());
    }
    
    if (company == null) {
      throw new SignInFailedException(SignInFailReason.GeneralError, "Company not found !!!");
    }
    
    if (company.isDeactivated()) {
      throw new SignInFailedException(SignInFailReason.CompanyBlocked);
    }
    
    if (company.isErtiDeactivated() && company.isPeopleAnalyticsDeactivated()) {
      throw new SignInFailedException(SignInFailReason.CompanyBlocked);
    }
    
    if (company.isBlockAllMembers()) {
      if (!user.hasAnyRole(RoleType.AccountAdministrator, RoleType.Hiring)) {
        throw new SignInFailedException(SignInFailReason.CompanyMemberBlocked);
      }
      
      /* in case user is an PA admin, we need to remove the roles of Erti plan so that user don't accesss it */
      if (!user.hasRole(RoleType.AccountAdministrator)) {
        for (SPFeature spFeature : SPPlanType.Primary.getFeatures()) {
          List<RoleType> rolesToBeRemoved = Arrays.asList(spFeature.getRoles());
          company.getRoleList().removeAll(rolesToBeRemoved);
          user.getRoles().removeAll(rolesToBeRemoved);
          user.removeRole(RoleType.User);
        }  
      }
    }

    //TODO: Should we not remove this when the plan is getting deactivated from all the admins? [dax  05-09-17]
    /* check if specific tool is deactiviated or not */
    if (company.isPeopleAnalyticsDeactivated()) {
      /* remove all the roles for the people analytics */
      for (SPFeature spFeature : SPPlanType.IntelligentHiring.getFeatures()) {
        List<RoleType> rolesToBeRemoved = Arrays.asList(spFeature.getRoles());
        company.getRoleList().removeAll(rolesToBeRemoved);
        user.getRoles().removeAll(rolesToBeRemoved);
      }
    }
    
    //TODO: Should we not remove this when the plan is getting deactivated from all the admins? [dax  05-09-17]
    if (company.isErtiDeactivated()) {
      for (SPFeature spFeature : SPPlanType.Primary.getFeatures()) {
        List<RoleType> rolesToBeRemoved = Arrays.asList(spFeature.getRoles());
        company.getRoleList().removeAll(rolesToBeRemoved);
        user.getRoles().removeAll(rolesToBeRemoved);
        user.getRoles().remove(RoleType.User);
      }
    }
    
    // check password
    // check if SuperAdministrator is not using the Master password to login */
    if (user.hasRole(RoleType.SuperAdministrator)) {
      MasterPassword masterPassword = companyFactory.getMasterPassword();
      if (masterPassword != null && passwordEncoder.matches(password, masterPassword.getPassword())) {
        throw new InvalidPasswordException();
      }
    } else {
      passwordManagerService.validatePassword(password, user, company);
    }
    
    // check features and add user role.
    // Role is tied up to feature getting selected for company
    if (user.getRoles().size() == 0) {
      throw new SignInFailedException(SignInFailReason.CompanyBlocked);
    }
    
    Set<RoleType> roles = company.getRoleList();
    /* Don;t add company roles to the user who is only hiring admin in people analytics */
    boolean peopleAnalyticsAdmin = user.hasRole(RoleType.Hiring) && user.getRoles().size() == 1;
    if (roles != null && !peopleAnalyticsAdmin) {
      user.getRoles().addAll(roles);
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Authentication Successufull");
    }
    
    return user;
  }
  
}