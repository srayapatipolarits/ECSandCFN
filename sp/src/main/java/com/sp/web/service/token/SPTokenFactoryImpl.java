package com.sp.web.service.token;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenType;
import com.sp.web.model.User;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.token.TokenRepository;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.GenericUtils;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

/**
 * <code>SPTokenFactoryImpl</code> implement the functionality of SPTokenFactory interface.
 * 
 * @author pruhil
 */
@Service("spTokenFactory")
public class SPTokenFactoryImpl implements SPTokenFactory {
  
  /**
   * Token URL prefix constant.
   */
  private static final String TOKEN_URL_PREFIX = "token.url.prefix";
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(SPTokenFactoryImpl.class);
  
  /** Token Repository to perform operation on the repository. */
  @Autowired
  private TokenRepository tokenRepository;
  
  @Inject
  private Environment enviornment;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  /**
   * @see com.sp.web.service.token.SPTokenFactory#getTokenUrl(com.sp.web.model. Token)
   */
  private String getTokenUrl(Token token) {
    return enviornment.getProperty(TOKEN_URL_PREFIX).concat(token.getTokenId());
  }
  
  /**
   * <code>getToken</code> method is the final method which follows the template design.
   * <p>
   * It performs the following operation.
   * <ul>
   * <li>Fetch the token handler for the token type for which token needs to generated.</li>
   * <li>Persist the token in the token Repository.</li>
   * <li>Return the requested token</li>
   * </ul>
   * </p>
   * 
   * @see com.sp.web.service.token.SPTokenFactory#getToken(TokenType, Map, TokenProcessorType),
   *      java.util.Map, com.sp.web.model.TokenProcessorType)
   */
  @Override
  public final Token getToken(TokenType tokenType, Map<String, Object> paramsMap,
      TokenProcessorType tokenProcessorType) {
    
    LOG.info("Enter getToken() method, tokenType " + tokenType + ", tokenProcessorType :"
        + tokenProcessorType + ", paramsMap " + paramsMap);
    
    /* get the token handler for the tokentype */
    TokenHandler tokenHandler = getTokenHandlerFromType(tokenType);
    
    /*
     * create the token by callling the respective token handler implemenation
     */
    Token token = tokenHandler.getToken(paramsMap, tokenProcessorType);
    token.setTokenUrl(getTokenUrl(token));
    token.setThemeKey(getThemeKey());
    
    /* Token created successfully */
    if (LOG.isDebugEnabled()) {
      LOG.debug("Token is successfully created for tokenType " + tokenType);
    }
    
    /* Persist the token in the repository */
    tokenHandler.persistToken(token);
    
    /* return the token */
    return token;
  }
  
  @Override
  public Token getToken(TokenRequest tokenRequest, TokenProcessorType tokenProcessorType) {
    return getToken(tokenRequest.getType(), tokenRequest.getParamsMap(), tokenProcessorType);
  }
  
  /**
   * <code>processToken</code> method will process the token. It will fetch the token from
   * repository, checks for its validity and returns the response which needs to be displayed by the
   * token controller.
   * 
   * @param token
   *          id string.
   * @return the view which needs to be rendered for the token.
   * @see com.sp.web.service.token.SPTokenFactory#processToken(java.lang.String)
   */
  @Override
  public final Token processToken(String token) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Enter processToken(), token :" + token);
    }
    
    /* fetch the token from the repository */
    Token repToken = tokenRepository.findTokenById(token);
    
    if (repToken == null) {
      throw new InvalidRequestException("Token not found :" + token);
    }
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Token retreived from repository ");
    }
    
    /* check if token is valid or not */
    if (!getTokenHandlerFromType(repToken.getTokenType()).isValid(repToken)) {
      throw new InvalidTokenException(MessagesHelper.getMessage("exception.token.invalid"),
          repToken);
    }
    
    /* Get the token processor and process the token request */
    getTokenProcessorFromType(repToken.getTokenProcessorType()).processToken(repToken);
    
    return repToken;
  }
  
  /**
   * <code>getTokenHandlerFromType</code> method returns the Token Type instance which will perform
   * the operation for creating token
   * 
   * @param tokenType
   *          handler instance to be returned.
   * @return the TokenHander
   */
  private TokenHandler getTokenHandlerFromType(TokenType tokenType) {
    
    LOG.info("Token handler requested :" + tokenType);
    
    return (TokenHandler) ApplicationContextUtils.getApplicationContext().getBean(
        tokenType.getTokenBeanId());
  }
  
  /**
   * <code>getTokenProcessorFromType</code> method returns the Token Processfor the requested Type
   * 
   * 
   * @param tokenProcessorType
   *          for instance to be returned.
   * @return the TokenProcessor
   */
  private TokenProcessor getTokenProcessorFromType(TokenProcessorType tokenProcessorType) {
    
    if (LOG.isInfoEnabled()) {
      LOG.info("Token processor requested :" + tokenProcessorType);
    }
    /* get the spring application context */
    return (TokenProcessor) ApplicationContextUtils.getBean(tokenProcessorType.getProcessorName());
  }
  
  @Override
  public Token persistToken(Token token) {
    tokenRepository.persistToken(token);
    return token;
  }
  
  @Override
  public void removeToken(Token token) {
    tokenRepository.deleteToken(token);
  }
  
  @Override
  public void removeToken(String tokenId) {
    Optional.ofNullable(findById(tokenId)).ifPresent(this::removeToken);
  }
  
  private Token findById(String tokenId) {
    return tokenRepository.findById(tokenId);
  }

  @Override
  public Token findTokenById(String tokenId) {
    return tokenRepository.findTokenById(tokenId);
  }
  
  /**
   * getThemeKey method will get the theme key for the logged in user and will set that in the
   * token.
   * 
   * @return the theme key for the logged in user or anonymous user.
   */
  private String getThemeKey() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    /* check if user is logged in or */
    if (authentication instanceof UsernamePasswordAuthenticationToken
        || authentication instanceof RememberMeAuthenticationToken) {
      User user = GenericUtils.getUserFromAuthentication(authentication);
      String companyId = user.getCompanyId();
      
      /* check if user a business user or individual user */
      if (StringUtils.isNotBlank(companyId)) {
        return companyId;
      }
    }
    
    return Constants.DEFAULT_THEME_NAME;
  }
}