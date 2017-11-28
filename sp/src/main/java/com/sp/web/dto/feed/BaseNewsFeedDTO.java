package com.sp.web.dto.feed;

import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.feed.NewsFeedType;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 * 
 *         The base news feed DTO that should contain the basic information.
 */
public class BaseNewsFeedDTO {
  
  private NewsFeedType type;
  private boolean isOwner;
  private boolean isTagged;
  private LocalDateTime updatedOn;

  /**
   * Constructor.
   * 
   * @param newsFeed
   *          - news feed
   */
  public BaseNewsFeedDTO(NewsFeed newsFeed) {
    BeanUtils.copyProperties(newsFeed, this);
  }

  public NewsFeedType getType() {
    return type;
  }
  
  public void setType(NewsFeedType type) {
    this.type = type;
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
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
}
