package com.sp.web.model;

/**
 * Type of token Parameter contains the spring bean ID which.
 * 
 * @author pruhil
 * 
 */
public enum TokenType {

  /** Time based token. */
  TIME_BASED("timeBasedToken"),

  /** count base. */
  COUNT_BASED("countBasedToken"),

  /** Perpetual token. */
  PERPETUAL("perpetual");

  /** Token Spring bean id. */
  private String tokenBeanId;

  /**
   * Constructor.
   * 
   * @param token
   *          - token
   */
  private TokenType(String tokenBeanId) {
    this.tokenBeanId = tokenBeanId;
  }

  /**
   * <code>getTokenBandId</code> method returns the token ID.
   * 
   * @return
   *      the token bean id
   */
  public String getTokenBeanId() {
    return tokenBeanId;
  }

}
