package com.sp.web.service.token;

import com.sp.web.model.TokenType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 *
 *         The base envelop class for token requests.
 */
public class TokenRequest {

  private TokenType type;
  private Map<String, Object> paramsMap;

  /**
   * Constructor for token type.
   * 
   * @param type
   *          - the token type
   */
  public TokenRequest(TokenType type) {
    this.type = type;
  }
  
  /**
   * The Constructor for params.
   * 
   * @param type
   *          - token type
   * @param paramsMap
   *          - params map
   */
  public TokenRequest(TokenType type, Map<String, Object> paramsMap) {
    this(type);
    this.paramsMap = paramsMap;
  }
  
  public TokenType getType() {
    return type;
  }

  public void setType(TokenType type) {
    this.type = type;
  }

  public Map<String, Object> getParamsMap() {
    return Optional.ofNullable(paramsMap).orElseGet(() -> {
        paramsMap = new HashMap<String, Object>();
        return paramsMap;
      });
  }

  public void setParamsMap(Map<String, Object> paramsMap) {
    this.paramsMap = paramsMap;
  }

  public void addParam(String key, Object value) {
    getParamsMap().put(key, value);
  }
}
