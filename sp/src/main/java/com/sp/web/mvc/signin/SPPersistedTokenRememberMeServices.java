package com.sp.web.mvc.signin;

import com.sp.web.model.User;
import com.sp.web.utils.GenericUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.ReflectionUtils;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SPPersistedTokenRememberMeService class extends the login success and logout method to
 * incorporate the user.
 * 
 * @author pradeepruhil
 *
 */
public class SPPersistedTokenRememberMeServices extends PersistentTokenBasedRememberMeServices {
  
  private PersistentTokenRepository tokenRepository;
  
  public SPPersistedTokenRememberMeServices() {
    
  }
  
  public SPPersistedTokenRememberMeServices(String key, UserDetailsService userDetailsService,
      PersistentTokenRepository tokenRepository) {
    super(key, userDetailsService, tokenRepository);
    this.tokenRepository = tokenRepository;
  }
  
  /**
   * <code>onLoginSucess</code> method will create the persistent token.
   * 
   * @see org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices#
   *      onLoginSuccess(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
   */
  @Override
  protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication successfulAuthentication) {
    User user = GenericUtils.getUserFromAuthentication(successfulAuthentication);
    
    logger.debug("Creating new persistent login for user " + user.getEmail());
    
    PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(user.getEmail(),
        generateSeriesData(), generateTokenData(), new Date());
    try {
      tokenRepository.createNewToken(persistentToken);
      addCookie(persistentToken, request, response);
    } catch (DataAccessException e) {
      logger.error("Failed to save persistent token ", e);
    }
  }
  
  /**
   * addCookie method will add the cookie in the response.
   * 
   * @param token
   *          persistent remember me token.
   * @param request
   *          containing the request.
   * @param response
   *          response.
   */
  private void addCookie(PersistentRememberMeToken token, HttpServletRequest request,
      HttpServletResponse response) {
    setCookie(new String[] { token.getSeries(), token.getTokenValue() }, getTokenValiditySeconds(),
        request, response);
  }
  
  /**
   * Overriding the logout method to remove the token for the series not for all the users.
   */
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    /* get the token series to remove that only from the db */
    String rememberMeCookie = extractRememberMeCookie(request);
    super.logout(request, response, authentication);
    if (authentication != null) {
      if (rememberMeCookie != null) {
        String[] cookieTokens = decodeCookie(rememberMeCookie);
        if (cookieTokens.length > 0) {
          final String presentedSeries = cookieTokens[0];
          tokenRepository.removeUserTokens(presentedSeries);
        }
        
      }
      
    }
    
  }
  
  /**
   * setter method for the token repository.
   * 
   * @param tokenRepository
   *          the tokenRepository to set
   */
  public void setTokenRepository(PersistentTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }
  
  /**
   * Sets the cookie on the response.
   *
   * By default a secure cookie will be used if the connection is secure. You can set the
   * {@code useSecureCookie} property to {@code false} to override this. If you set it to
   * {@code true}, the cookie will always be flagged as secure. If Servlet 3.0 is used, the cookie
   * will be marked as HttpOnly.
   *
   * @param tokens
   *          the tokens which will be encoded to make the cookie value.
   * @param maxAge
   *          the value passed to {@link Cookie#setMaxAge(int)}
   * @param request
   *          the request
   * @param response
   *          the response to add the cookie to.
   */
  protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request,
      HttpServletResponse response) {
    String cookieValue = encodeCookie(tokens);
    Cookie cookie = new Cookie(getCookieName(), cookieValue);
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    ReflectionUtils.invokeMethod(
        ReflectionUtils.findMethod(Cookie.class, "setHttpOnly", boolean.class), cookie,
        Boolean.FALSE);
    
    response.addCookie(cookie);
  }
  
}
