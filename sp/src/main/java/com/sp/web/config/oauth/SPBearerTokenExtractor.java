package com.sp.web.config.oauth;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.User;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class SPBearerTokenExtractor extends BearerTokenExtractor {
  
  private static final Logger logger = Logger.getLogger(SPBearerTokenExtractor.class);
  
  protected String extractToken(HttpServletRequest request) {
    // first check the header...
    String token = extractHeaderToken(request);
    
    // Check in request body
    if (token == null) {
      token = extractRequestBodyToken(request);
    }
    
    // bearer type allows a request parameter as well
    if (token == null) {
      logger.debug("Token not found in headers. Trying request parameters.");
      token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
      if (token == null) {
        logger.debug("Token not found in request parameters.  Not an OAuth2 request.");
      } else {
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
            OAuth2AccessToken.BEARER_TYPE);
      }
    }
    
    return token;
  }
  
  /**
   * Extract the request body token to validate the user.
   * 
   * @param requestcontianing
   *          the http servlet request body.
   * @return the access token.
   */
  public String extractRequestBodyToken(HttpServletRequest request) {
    try {
      String jsonString = IOUtils.toString(request.getReader());
      logger.debug("Request body" + jsonString);
      if (StringUtils.isBlank(jsonString)) {
        return null;
      }
      
      SpeechletRequestEnvelope<?> fromJson = SpeechletRequestEnvelope.fromJson(jsonString);
      User user = fromJson.getSession().getUser();
      String accessToken = user.getAccessToken();
      if (accessToken != null) {
        logger.debug("access token found");
        request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, accessToken.trim());
        request.setAttribute("SPEECH_REQUEST", jsonString.getBytes());
        return accessToken;
      }
    } catch (IOException ex) {
      logger.error("Desieralization failes from amazon elxa", ex);
      return null;
    }
    return null;
  }
}
