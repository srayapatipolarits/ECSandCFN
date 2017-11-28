package com.sp.web.model.feed;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.SPMedia;
import com.sp.web.model.User;

/**
 * @author Dax Abraham
 * 
 *         The entity to store activity news feeds.
 */
public class ActivityNewsFeed extends SPNewsFeed {
  
  private UserMarkerDTO user;

  public ActivityNewsFeed() {
    super();
  }
  
  /**
   * Constructor.
   * 
   * @param text
   *          - text
   * @param user
   *          - user
   */
  public ActivityNewsFeed(User user, String text) {
    this(user, text, null);
  }

  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param text
   *          - text
   * @param media
   *          - media
   */
  public ActivityNewsFeed(User user, String text, SPMedia media) {
    super(text, media);
    this.user = new UserMarkerDTO(user);
  }

  public UserMarkerDTO getUser() {
    return user;
  }
  
  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }

  /**
   * Add the user.
   * 
   * @param user
   *          - user
   */
  public void addUser(User user) {
    this.user = new UserMarkerDTO(user);
  }
  
}
