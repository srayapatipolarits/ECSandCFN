package com.sp.web.service.token;

import com.sp.web.model.Token;

/**
 * TokenProcessor interface provides operation to process the token.
 * 
 * @author pruhil
 */
public interface TokenProcessor {

  /**
   * <code>processToken</code> method will process the token.
   * 
   * @param token
   *          to be processed
   */
  void processToken(Token token);
}
