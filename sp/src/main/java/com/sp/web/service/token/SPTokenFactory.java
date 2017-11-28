package com.sp.web.service.token;

import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;

import java.util.Map;

/**
 * <code>SPTokenFactory</code> provides operation for the tokens to be used in SP platform.
 * <br/>
 * It creates token of {@link TokenType} type and provides operation to process these tokens
 * 
 * @author pruhil
 * 
 */
public interface SPTokenFactory {

  /**
   * Create and get the token for the given token request.
   *  
   * @param tokenRequest
   *          - token request
   * @param tokenProcessorType
   *          - token processor type
   * @return
   *      the token created
   */
  Token getToken(TokenRequest tokenRequest, TokenProcessorType tokenProcessorType);
  
  /**
   * <code>getToken</code> method will return the Token for the respective token Type request.
   * 
   * @param tokenType
   *          which needs to returned.
   * @param paramsMap
   *          for the token for the inforation to which it belongs
   * @param tokenProcessorType
   *          which will be stored in the token which will process it handling.
   * @return the generated token
   */
  Token getToken(TokenType tokenType, Map<String, Object> paramsMap,
      TokenProcessorType tokenProcessorType);

  /**
   * <code>processToken</code> method will process the token. It will fetch the token from
   * repository, checks for its validity and returns the response which needs to be displayed by the
   * token controller.
   * 
   * @param token
   *          id string.
   * @return the view which needs to be rendered for the token.
   */
  Token processToken(String token);

  /**
   * Persists the given token.
   * 
   * @param token
   *          - token to persist
   */
  Token persistToken(Token token);

  /**
   * Delete the given token.
   * 
   * @param token
   *          - token to delete
   */
  void removeToken(Token token);

  /**
   * Remove the given token.
   * 
   * @param tokenId
   *          - token id
   */
  void removeToken(String tokenId);

  /**
   * Get the token for the given token id.
   * 
   * @param tokenId
   *          - token id
   * @return
   *    the token
   */
  Token findTokenById(String tokenId);
}
