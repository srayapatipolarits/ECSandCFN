package com.sp.web.repository.login;

import com.sp.web.mvc.signin.SPRemberMeToken;

import java.util.List;

/**
 * RememberMeTokenRepository will fetcht he remember me token for the user.
 * 
 * @author pradeepruhil
 *
 */
public interface RememberMeTokenRepository {
  
  /**
   * findBySeries method will retrieve the token by the series.
   * 
   * @param series
   *          against which the token is to be retrieved.
   * @return the spReemember me token.
   */
  SPRemberMeToken findBySeries(String series);
  
  /**
   * Find the all token registered against the user.
   * 
   * @param username
   *          logged in user.
   * @return the all logged in user.
   */
  List<SPRemberMeToken> findByUsername(String username);
  
  /**
   * Save the remember me token.
   * 
   * @param spRemberMeToken
   *          spRemember Me token.
   */
  void save(SPRemberMeToken spRemberMeToken);
  
  /**
   * Remove the remember token.
   * 
   * @param token
   *          to be removed.
   */
  void remove(SPRemberMeToken token);
}