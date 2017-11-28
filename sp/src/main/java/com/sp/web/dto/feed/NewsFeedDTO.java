package com.sp.web.dto.feed;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.SPMedia;
import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.feed.NewsFeedType;
import com.sp.web.model.feed.SPNewsFeed;

/**
 * @author Dax Abraham
 * 
 *         The news feed dto class.
 */
public class NewsFeedDTO extends BaseNewsFeedDTO {
  
  private NewsFeedType type;
  private String text;
  private SPMedia media;
  private UserMarkerDTO user;
  private boolean isOwner;
  private boolean isTagged;
  
  /**
   * Constructor.
   * 
   * @param newsFeed
   *            - news feed
   */
  public NewsFeedDTO(NewsFeed newsFeed) {
    super(newsFeed);
    // set the updated on field for the news feed
    if (newsFeed instanceof SPNewsFeed) {
      setUpdatedOn(((SPNewsFeed) newsFeed).getCreatedOn());
    }
  }

  public NewsFeedType getType() {
    return type;
  }
  
  public void setType(NewsFeedType type) {
    this.type = type;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public SPMedia getMedia() {
    return media;
  }
  
  public void setMedia(SPMedia media) {
    this.media = media;
  }
  
  public UserMarkerDTO getUser() {
    return user;
  }
  
  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }

  public boolean isOwner() {
    return isOwner;
  }

  public void setOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  public boolean isTagged() {
    return isTagged;
  }

  public void setTagged(boolean isTagged) {
    this.isTagged = isTagged;
  }
  
}
