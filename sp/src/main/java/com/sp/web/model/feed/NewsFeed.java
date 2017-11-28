package com.sp.web.model.feed;

import com.sp.web.service.feed.NewsFeedSupport;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Dax Abraham
 * 
 *         The entity to store the news feed.
 */
public class NewsFeed implements Serializable {
  
  private static final long serialVersionUID = 1379353068249932155L;
  private String feedId;
  private NewsFeedType type;
  private String feedRefId;
  private LocalDateTime createdOn;
  private LocalDateTime updatedOn;
  
  /**
   * Default constructor.
   */
  public NewsFeed() { /* Default Constructor */ }
  
  /**
   * Constructor from feed object.
   * 
   * @param type
   *          - type
   * @param feedSource
   *          - feed source object
   */
  public NewsFeed(NewsFeedType type, NewsFeedSupport feedSource) {
    feedId = UUID.randomUUID().toString();
    this.type = type;
    this.feedRefId = feedSource.getFeedRefId();
    createdOn = LocalDateTime.now();
    updatedOn = LocalDateTime.now();
  }
  
  public NewsFeedType getType() {
    return type;
  }
  
  public void setType(NewsFeedType type) {
    this.type = type;
  }
  
  public String getFeedRefId() {
    return feedRefId;
  }
  
  public void setFeedRefId(String feedRefId) {
    this.feedRefId = feedRefId;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }

  public String getFeedId() {
    return feedId;
  }

  public void setFeedId(String feedId) {
    this.feedId = feedId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((feedId == null) ? 0 : feedId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    NewsFeed other = (NewsFeed) obj;
    if (feedId == null) {
      if (other.feedId != null) {
        return false;
      }
    } else if (!feedId.equals(other.feedId)) {
      return false;
    }
    return true;
  }
  
}
