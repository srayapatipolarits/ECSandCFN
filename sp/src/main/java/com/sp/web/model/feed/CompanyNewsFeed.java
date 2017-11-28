package com.sp.web.model.feed;

import com.sp.web.service.feed.NewsFeedSupport;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The entity to store all the users news feeds.
 */
public class CompanyNewsFeed implements Serializable {
  
  private static final long serialVersionUID = -854363637623881145L;
  private String id;
  private String companyId;
  private LinkedList<NewsFeed> newsFeeds;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * Add the given news feed to the news feed list.
   * 
   * @param newsFeed
   *          - news feed
   */
  public synchronized void add(NewsFeed newsFeed) {
    newsFeeds.add(0, newsFeed);
  }

  public LinkedList<NewsFeed> getNewsFeeds() {
    return newsFeeds;
  }

  public void setNewsFeeds(LinkedList<NewsFeed> newsFeeds) {
    this.newsFeeds = newsFeeds;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * Update the news feed for the feed source.
   * 
   * @param feedSource
   *            - feed source
   */
  public void updateNewsFeed(NewsFeedSupport feedSource) {
    updateNewsFeed(findNewsFeedByFeedRefValidated(feedSource.getFeedRefId()));
  }

  /**
   * Update the given news feed object.
   * 
   * @param newsFeed
   *            - news feed
   */
  public void updateNewsFeed(NewsFeed newsFeed) {
    newsFeeds.remove(newsFeed);
    newsFeeds.add(0, newsFeed);
    newsFeed.setUpdatedOn(LocalDateTime.now());
  }

  /**
   * Get the news feed object from the given feed ref id.
   * 
   * @param feedRefId
   *          - feed ref id
   * @return
   *    the news feed
   */
  public NewsFeed findNewsFeedByFeedRef(String feedRefId) {
    List<NewsFeed> unmodifiableList = Collections.unmodifiableList(newsFeeds);
    Optional<NewsFeed> findFirst = unmodifiableList.stream()
        .filter(nf -> feedRefId.equals(nf.getFeedRefId())).findFirst();
    if (findFirst.isPresent()) {
      return findFirst.get();
    }
    return null;
  }

  /**
   * Get the news feed object also validate if it is present.
   * 
   * @param feedRefId
   *          - feed ref id
   * @return
   *    the news feed
   */
  private NewsFeed findNewsFeedByFeedRefValidated(String feedRefId) {
    NewsFeed newsFeed = findNewsFeedByFeedRef(feedRefId);
    Assert.notNull(newsFeed, "News feed not found.");
    return newsFeed;
  }
  
  /**
   * Delete the given news feed.
   * 
   * @param feedSource
   *            - news feed to delete
   */
  public void deleteNewsFeed(NewsFeedSupport feedSource) {
    newsFeeds.remove(findNewsFeedByFeedRefValidated(feedSource.getFeedRefId()));
  }

}
