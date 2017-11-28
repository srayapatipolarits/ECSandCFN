package com.sp.web.service.token;

import com.sp.web.Constants;
import com.sp.web.model.TokenType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Dax Abraham
 *
 *         The time based token request.
 */
public class TimeBasedTokenRequest extends TokenRequest {

  /**
   * Constructor.
   * 
   * @param expiresTime
   *            - token expires time
   * @param timeUnit
   *            - expires time time unit 
   * @param paramsMap
   *            - token parameters
   */
  public TimeBasedTokenRequest(long expiresTime, TimeUnit timeUnit, Map<String, Object> paramsMap) {
    super(TokenType.TIME_BASED, paramsMap);
    setExpiresTime(expiresTime, timeUnit);
  }
  
  /**
   * Constructor.
   * 
   * @param expiresTime
   *            - token expires time
   * @param timeUnit
   *            - expires time time unit 
   */
  public TimeBasedTokenRequest(long expiresTime, TimeUnit timeUnit) {
    super(TokenType.TIME_BASED);
    setExpiresTime(expiresTime, timeUnit);
  }
  
  /**
   * Constructor.
   * 
   * @param expiresTime
   *            - token expires time
   */
  public TimeBasedTokenRequest(long expiresTime) {
    super(TokenType.TIME_BASED);
    setExpiresTime(expiresTime, TimeUnit.SECONDS);
  }
  
  /**
   * Constructor to crate time based token request with default expires time.
   */
  public TimeBasedTokenRequest() {
    this(Constants.DEFAULT_EXPIRES_TIME, Constants.DEFAULT_EXPIRES_TIME_UNIT);
  }

  /**
   * Set the token expires time.
   * 
   * @param expiresTime
   *            - token expires time
   * @param timeUnit
   *            - expires time time unit 
   */
  private void setExpiresTime(long expiresTime, TimeUnit timeUnit) {
    final Map<String, Object> paramsMap = getParamsMap();
    paramsMap.put(Constants.PARAM_EXPIRES_TIME, expiresTime);
    paramsMap.put(Constants.PARAM_TIME_UNIT, timeUnit);
  }
}
