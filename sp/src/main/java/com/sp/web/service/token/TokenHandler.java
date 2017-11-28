package com.sp.web.service.token;

import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;

import java.util.Map;

/**
 * TokenHandler interface provides operation to be performed on the token. It provides following
 * functionalities
 * <ul>
 * <li>create token for the requested token type</li>
 * <li>Store the token in the repository</li>
 * <li>delete token from the repository</li>
 * <li>validate the token it is currently valid or not</li>
 * <li>process the token by calling respective processor for the token
 * <li>
 * </ul>
 * 
 * @author pruhil
 * 
 */
public interface TokenHandler {

  /**
   * <code>getToken</code> method will return the token.
   * 
   * @param paramMap
   *          contains the values which needed for this token to process and also other information
   *          which will help to process this token
   * @param processorType
   *          TokenProcessorType
   * @return the Token
   */
  Token getToken(Map<String, Object> paramMap, TokenProcessorType processorType);

  /**
   * <code>persistToken</code> method will persist the token in mongo repository
   * 
   * @param token
   *          token to be stored
   * @return the persisted token with all the details.
   */
  Token persistToken(Token token);

  /**
   * <code>deleteToken</code> methdo will delete the token.
   * 
   * @param token
   *          to be deleted
   * @return true if token is deleted successfully from the repository
   */
  boolean deleteToken(Token token);

  /**
   * <code>validateToken</code> method will check wheather the token is valid or not.
   * 
   * @param token
   *          to be validated
   * @return the true if token is valid otherwise false.
   */
  boolean isValid(Token token);
}
