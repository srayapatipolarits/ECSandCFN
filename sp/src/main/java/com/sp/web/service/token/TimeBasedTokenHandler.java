package com.sp.web.service.token;

import com.sp.web.Constants;
import com.sp.web.exception.InvalidTokenException;
import com.sp.web.model.Token;
import com.sp.web.model.TokenProcessorType;
import com.sp.web.model.TokenStatus;
import com.sp.web.model.TokenType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

/**
 * <code>TimeBasedToken</code> method will create a time based token and provides operation to handle the token and
 * validate the token.
 * 
 * @author pruhil
 */
@Component("timeBasedToken")
public class TimeBasedTokenHandler extends AbstractTokenHandler {

  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(TimeBasedTokenHandler.class);

  @Autowired
  private HttpSession httpSession;

  @Autowired
  private Environment environment;

  /**
   * <code>getToken</code> method will return the token.
   * 
   * @param paramMap
   *          contains the values which needed for this token to process and also other information which will help to
   *          process this token
   * @param processorType
   *          TokenProcessorType
   * @return the Token
   * @see com.sp.web.service.token.TokenHandler#getToken(java.util.Map, com.sp.web.model.TokenProcessorType)
   */
  @Override
  public Token getToken(Map<String, Object> paramMap, TokenProcessorType processorType) {

    /* create a new token object */

    Token token = new Token();
    /* check if expires time and time unit is present in the parms */
    if (!paramMap.containsKey(Constants.PARAM_EXPIRES_TIME)) {
      LOG.warn("Expres time is not present, using the default expires time for TokenRpocesstype :" + processorType);
      long expiresTime = Long.valueOf(environment.getProperty("token.default.expires.time"));
      paramMap.put(Constants.PARAM_EXPIRES_TIME, expiresTime);
    }

    // check if time unit is present in the param map or not
    if (!paramMap.containsKey(Constants.PARAM_TIME_UNIT)) {
      LOG.warn("Time unit is not present, using the default time unit for TokenRpocesstype :" + processorType);
      TimeUnit timeUnit = TimeUnit.valueOf(environment.getProperty("token.default.time.unit"));
      paramMap.put(Constants.PARAM_TIME_UNIT, timeUnit);
    }

    token.setParamsMap(paramMap);
    token.setTokenProcessorType(processorType);
    token.setTokenType(TokenType.TIME_BASED);
    token.setCreatedTime(System.currentTimeMillis());
    token.setTokenId(generateTokenId());
    token.setTokenStatus(TokenStatus.ENABLED);

    LOG.info("Time based Token created succesfully ");
    return token;
  }

  /**
   * (non-Javadoc)
   * 
   * @see com.sp.web.service.token.TokenHandler#validateToken(com.sp.web.model. Token)
   */
  @Override
  public boolean isValid(Token token) {

    if (token.getTokenStatus() == TokenStatus.INVALID) {
      throw new InvalidTokenException("Token has expired.", token);
    }

    /* Get the timeunit in which expires time is stored in the token */
    TimeUnit timeUnit = TimeUnit.valueOf((String) token.getParam(Constants.PARAM_TIME_UNIT));

    /* get the time at which token is created */
    long createdTime = token.getCreatedTime();

    /* get the current system time */
    long currentTime = System.currentTimeMillis();

    /* Get the expires time in milliseoncds */
    long tokenExpiresTime = Long.valueOf(token.getParam(Constants.PARAM_EXPIRES_TIME).toString());

    /* find the time in millies for the time mentieoned in token */
    long timeInMillisforExp = getTimeMilliesForExpiresTime(tokenExpiresTime, timeUnit);

    /*
     * check if time different between current and created time is greater then time in millis for the specified in
     * token
     */
    if ((currentTime - createdTime) > timeInMillisforExp) {
      token.setTokenStatus(TokenStatus.INVALID);
      token.setInvalidationCause(Constants.ERROR_MESSAGE_TOKEN_EXPIRED);
      persistToken(token);
      throw new InvalidTokenException(Constants.ERROR_MESSAGE_TOKEN_EXPIRED, token);
    } else {
      return true;
    }
  }

  /**
   * <code>getTimeMillisForExpiresTime</code> method will return the milliconds data for the time requested in days,
   * hours and minutes
   * 
   * @param expiresTimeDuration
   *          contains the timein millis
   * @param timeUnit
   *          of type {@link TimeUnit}
   * @return time in long as milliseconds.
   */
  private long getTimeMilliesForExpiresTime(long expiresTimeDuration, TimeUnit timeUnit) {
    long timeInMillis = 0;
    switch (timeUnit) {
    case DAYS:
      timeInMillis = TimeUnit.DAYS.toMillis(expiresTimeDuration);
      break;
    case HOURS:
      timeInMillis = TimeUnit.HOURS.toMillis(expiresTimeDuration);
      break;
    case MINUTES:
      timeInMillis = TimeUnit.MINUTES.toMillis(expiresTimeDuration);
      break;
    default:
      break;
    }
    return timeInMillis;
  }
}
