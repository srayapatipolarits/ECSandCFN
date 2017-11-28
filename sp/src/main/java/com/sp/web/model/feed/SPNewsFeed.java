package com.sp.web.model.feed;

import com.sp.web.model.SPMedia;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Dax Abraham
 * 
 *         The SurePeople news feed update entity.
 */
public class SPNewsFeed extends NewsFeed {
  
  private boolean allCompany;
  private String text;
  private SPMedia media;
  
  public SPNewsFeed() {
  }
  
  /**
   * Constructor.
   * 
   * @param text
   *          - text
   */
  public SPNewsFeed(String text) {
    setFeedId(UUID.randomUUID().toString());
    setFeedRefId(getFeedId());
    setType(NewsFeedType.SPUpdate);
    this.text = text;
    this.allCompany = true;
    setCreatedOn(LocalDateTime.now());
    setUpdatedOn(LocalDateTime.now());
  }

  /**
   * Constructor from text and media.
   * 
   * @param text
   *          - text
   * @param media
   *          - media
   */
  public SPNewsFeed(String text, SPMedia media) {
    this(text);
    this.media = media;
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

  public boolean isAllCompany() {
    return allCompany;
  }

  public void setAllCompany(boolean allCompany) {
    this.allCompany = allCompany;
  }

}
