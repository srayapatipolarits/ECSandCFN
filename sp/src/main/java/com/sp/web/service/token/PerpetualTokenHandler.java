package com.sp.web.service.token;

import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.TokenType;
import com.sp.web.utils.MessagesHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * <code>PerpetualTokenHandler</code> class will handle perpetual tokens. It will create
 * perpetualtoken which will be expired till the user wants.
 * 
 * @author pradeep
 *
 */
@Component("perpetual")
public class PerpetualTokenHandler extends AbstractTokenHandler {

  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(PerpetualTokenHandler.class);

  @Autowired
  private HttpSession httpSession;

  /**
   * @see com.sp.web.service.token.TokenHandler#getToken(java.util.Map,
   *      com.sp.web.model.TokenProcessorType)
   */
  @Override
  public Token getToken(Map<String, Object> paramMap, TokenProcessorType processorType) {

    Token token = new Token();
    token.setParamsMap(paramMap);
    token.setTokenProcessorType(processorType);
    token.setTokenType(TokenType.PERPETUAL);
    token.setCreatedTime(System.currentTimeMillis());
    token.setTokenId(generateTokenId());
    token.setTokenStatus(TokenStatus.ENABLED);

    LOG.info("Perpetual based Token created succesfully ");
    return token;
  }

  /**
   * <code>Perpetual</code> toekn will be valid until or unless token is deleted by the processor
   * after usage.
   * 
   * @see com.sp.web.service.token.TokenHandler#isValid(com.sp.web.model.Token)
   */
  @Override
  public boolean isValid(Token token) {
    
    if (token.getTokenStatus() == TokenStatus.INVALID) {
      throw new InvalidTokenException(MessagesHelper.getMessage("exception.token.invalid"), token);
    }
    
    return true;
  }

}
