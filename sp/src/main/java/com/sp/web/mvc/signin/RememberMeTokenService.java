package com.sp.web.mvc.signin;

import com.sp.web.repository.login.RememberMeTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * RememberMeTokenService will be the service to identifiy if user token is valid or not.
 * 
 * @author pradeepruhil
 *
 */
@Component("mongoDBTokenRepository")
public class RememberMeTokenService implements PersistentTokenRepository {
  
  @Autowired
  RememberMeTokenRepository repository;
  
  @Override
  public void createNewToken(PersistentRememberMeToken token) {
    repository.save(new SPRemberMeToken(null, token.getUsername(), token.getSeries(), token
        .getTokenValue(), token.getDate()));
  }
  
  @Override
  public void updateToken(String series, String value, Date lastUsed) {
    SPRemberMeToken token = repository.findBySeries(series);
    repository
        .save(new SPRemberMeToken(token.getId(), token.getUsername(), series, value, lastUsed));
  }
  
  @Override
  public PersistentRememberMeToken getTokenForSeries(String seriesId) {
    return repository.findBySeries(seriesId);
  }
  
  @Override
  public void removeUserTokens(String tokenSeries) {
    SPRemberMeToken token = repository.findBySeries(tokenSeries);
    repository.remove(token);
  }
}
