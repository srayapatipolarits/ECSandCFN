package com.sp.web.mvc.signin;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.util.Date;

/**
 * SPRememberMe token contains the remember functionality for the SurePeople plateform. Compound
 * Indexes specifies the indexes for the doucment. Earch collection item will be unique for email
 * and series.
 * 
 * @author pradeepruhil
 *
 */
@Document
@CompoundIndexes({ @CompoundIndex(name = "i_username", def = "{'username': 1}"),
    @CompoundIndex(name = "i_series", def = "{'series': 1}") })
public class SPRemberMeToken extends PersistentRememberMeToken {
  
  /**
   * ID for the token.
   */
  @Id
  private final String id;
  
  /**
   * Constructor for the SPREmemeber token,
   * 
   * @param id
   *          to be stored
   * @param username
   *          is the email of the user.
   * @param series
   *          is the series for hashcode.
   * @param tokenValue
   *          token value
   * @param date
   *          on which token will be expired.
   */
  @PersistenceConstructor
  public SPRemberMeToken(String id, String username, String series, String tokenValue, Date date) {
    super(username, series, tokenValue, date);
    this.id = id;
  }
  
  public String getId() {
    return id;
  }
}
