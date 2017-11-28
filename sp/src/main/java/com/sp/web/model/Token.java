package com.sp.web.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Token bean captures the token details which are stored in the respository.
 * 
 * @author pruhil
 * 
 */
public class Token implements Serializable {
  
  /**
   * Default Serial version Id.
   */
  private static final long serialVersionUID = 1445884914586731414L;
  
  /** Mongo ID. */
  private String id;
  
  /** token ID. */
  private String tokenId;
  
  /** token processor. */
  private TokenProcessorType tokenProcessorType;
  
  /** token type for which this token belongs. */
  private TokenType tokenType;
  
  /** creation time for this doc. */
  private long createdTime;
  
  /** tokenCountUsed. */
  private int tokenUsedCount;
  
  /** params map. */
  private Map<String, Object> paramsMap = new HashMap<String, Object>();
  
  /** Token status. */
  private TokenStatus tokenStatus;
  
  /** The token URL. */
  private String tokenUrl;
  
  /** The cause for the token invalidation. */
  private String invalidationCause;
  
  /**
   * The view to redirect the user to complete the transaction.
   */
  private String redirectToView;
  
  private String themeKey;
  
  public String getTokenId() {
    return tokenId;
  }
  
  public void setTokenId(String tokenId) {
    this.tokenId = tokenId;
  }
  
  public TokenProcessorType getTokenProcessorType() {
    return tokenProcessorType;
  }
  
  public void setTokenProcessorType(TokenProcessorType tokenProcessorType) {
    this.tokenProcessorType = tokenProcessorType;
  }
  
  public TokenType getTokenType() {
    return tokenType;
  }
  
  public void setTokenType(TokenType tokenType) {
    this.tokenType = tokenType;
  }
  
  public long getCreatedTime() {
    return createdTime;
  }
  
  public void setCreatedTime(long createdTime) {
    this.createdTime = createdTime;
  }
  
  public int getTokenUsedCount() {
    return tokenUsedCount;
  }
  
  public void setTokenUsedCount(int tokenUsedCount) {
    this.tokenUsedCount = tokenUsedCount;
  }
  
  public Map<String, Object> getParamsMap() {
    return paramsMap;
  }
  
  public void setParamsMap(Map<String, Object> paramsMap) {
    this.paramsMap = paramsMap;
  }
  
  public TokenStatus getTokenStatus() {
    return tokenStatus;
  }
  
  public void setTokenStatus(TokenStatus tokenStatus) {
    this.tokenStatus = tokenStatus;
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getTokenUrl() {
    return tokenUrl;
  }
  
  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }
  
  /**
   * Helper method to get the parameter from the parameter map.
   * 
   * @param paramName
   *          - parameter name
   * @return the value for the given parameter
   */
  public Object getParam(String paramName) {
    return paramsMap.get(paramName);
  }
  
  /**
   * Get the parameter value or send default if none exists.
   * 
   * @param paramName
   *          - parameter name
   * @param defaultValue
   *          - default value if parameter is not available
   * @return
   *    the response to the get request
   */
  public Object getParam(String paramName, Object defaultValue) {
    return Optional.ofNullable(getParam(paramName)).orElse(defaultValue);
  }
  
  public String getInvalidationCause() {
    return invalidationCause;
  }
  
  public void setInvalidationCause(String invalidationCause) {
    this.invalidationCause = invalidationCause;
  }
  
  /**
   * Invalidates the token and sets the invalidation cause for the token.
   * 
   * @param invalidationClause
   *          - invalidation clause
   */
  public void invalidate(String invalidationClause) {
    tokenStatus = TokenStatus.INVALID;
    this.invalidationCause = invalidationClause;
  }

  /**
   * Invalidates the token and sets the invalidation cause for the token, along with the redirection
   * url.
   * 
   * @param invalidationClause
   *          - invalidation clause
   * @param redirectToView
   *          - the view to redirect to on subsequent request
   */
  public void invalidate(String invalidationClause, String redirectToView) {
    invalidate(invalidationClause);
    this.redirectToView = redirectToView;
  }
  
  /**
   * Helper method to check if the token is in validated state.
   * 
   * @return true if the toke is valid
   */
  public boolean isValid() {
    return (tokenStatus == TokenStatus.ENABLED);
  }
  
  public String getRedirectToView() {
    return redirectToView;
  }
  
  public void setRedirectToView(String redirectToView) {
    this.redirectToView = redirectToView;
  }
  
  /**
   * Helper method to get the parameter as a string.
   * 
   * @param paramName
   *          - the parameter name
   * @return the string value for the parameter
   */
  public String getParamAsString(String paramName) {
    return (String) getParam(paramName);
  }
  
  /**
   * Helper method to add the given parameter to the params map.
   * 
   * @param name
   *          - parameter name
   * @param value
   *          - value
   */
  public void addParam(String name, Object value) {
    if (paramsMap == null) {
      paramsMap = new HashMap<String, Object>();
    }
    paramsMap.put(name, value);
  }
  
  public void setThemeKey(String themeKey) {
    this.themeKey = themeKey;
  }
  
  public String getThemeKey() {
    return themeKey;
  }
}
