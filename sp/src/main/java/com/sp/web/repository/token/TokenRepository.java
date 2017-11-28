package com.sp.web.repository.token;

import com.sp.web.model.Token;

/**
 * TokenRepositoy provides operations for handling tokens in mongo respository.
 * 
 * @author pruhil
 * 
 */
public interface TokenRepository {
  
  /**
   * <code>persistToken</code> method will persist the token in the db
   * 
   * @param token
   *          to be persisted.
   */
  void persistToken(Token token);
  
  /**
   * <code>findTokenByID</code> method will return the TOken for id.
   * 
   * @param tokenID
   *          for the token
   * @return the Token
   */
  Token findTokenById(String tokenID);
  
  /**
   * <code>deleteToken</code> method will delete the token from the repository.
   * 
   * @param token
   *          to be deleted
   */
  void deleteToken(Token token);
  
  /**
   * <code>updateToken</code> method will update the token in the repository.
   * 
   * @param token
   *          to be updated
   * @return the updated token
   */
  Token updateToken(Token token);

  /**
   * Find by token id. 
   * 
   * @param tokenId
   *            - token
   * @return
   *    the token
   */
  Token findById(String tokenId);
}
