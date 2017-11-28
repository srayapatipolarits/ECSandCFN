/**
 * 
 */
package com.sp.web.service.token;

import com.sp.web.model.Token;
import com.sp.web.repository.token.TokenRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * <code>AbstractTokenHandler</code> class provides common operation to all the token handlers
 * <p>
 * It provides below functionalties
 * <ul>
 * <li>Persis the token in the repository</li>
 * <li>delete the token from the repository</li>
 * <li>Generate the token ID by calling the Token Generation</li>
 * </ul>
 * </p>
 * 
 * @author pruhil
 * 
 */
public abstract class AbstractTokenHandler implements TokenHandler {

  /** Initializing the logger */
  private static final Logger LOG = Logger.getLogger(AbstractTokenHandler.class);

  /** Token repository to store the token in database */
  @Autowired
  private TokenRepository repository;

  /**
   * <code>persistToken</code> method will persist the token in mongo repository
   * 
   * @param token
   *          token to be stored
   * @return the persisted token with all the details.
   * @see com.sp.web.service.token.TokenHandler#persistToken(com.sp.web.model.Token )
   */
  @Override
  public Token persistToken(Token token) {
    LOG.debug("Storing the token in the database ");
    Token storedToken = token;
    repository.persistToken(storedToken);

    LOG.debug("Token is stored succesfully");
    return storedToken;
  }

  /**
   * <code>deleteToken</code> methdo will delete the token
   * 
   * @param token
   *          to be deleted
   * @return true if token is deleted successfully from the repository
   * 
   * @see com.sp.web.service.token.TokenHandler#deleteToken(com.sp.web.model.Token)
   */
  @Override
  public boolean deleteToken(Token token) {
    /* delete the token from repository */
    repository.deleteToken(token);
    return true;
  }

  /**
   * <code>generateTokenID</code> method will generate the token and the return the generated token.
   * 
   * @return the generated token iD
   */
  protected String generateTokenId() {
    // TODO need to inject token generation algoirthm
    return UUID.randomUUID().toString();
  }

}
