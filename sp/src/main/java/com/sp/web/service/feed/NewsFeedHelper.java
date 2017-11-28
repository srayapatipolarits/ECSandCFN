package com.sp.web.service.feed;

import com.sp.web.model.feed.CompanyNewsFeed;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.NewsFeed;
import com.sp.web.model.feed.NewsFeedType;

import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The news feed helper class to create, update, etc the news feed.
 */
public class NewsFeedHelper {
  
  private NewsFeedFactory newsFeedFactory;
  private String companyId;
  //private CompanyNewsFeed companyNewsFeed;
  
  /**
   * Constructor.
   * 
   * @param companyId
   *          - company id
   * @param newsFeedFactory
   *          - news feed factory
   */
  public NewsFeedHelper(String companyId, NewsFeedFactory newsFeedFactory) {
    this.newsFeedFactory = newsFeedFactory;
    this.companyId = companyId;
    //companyNewsFeed = newsFeedFactory.getCompanyNewsFeed(companyId);
  }
  
  /**
   * Create a new company news feed of the given type.
   * 
   * @param type
   *          - news feed type
   * @param feedSource
   *          - source object
   * @return the news feed object
   */
  public NewsFeed createNewsFeed(NewsFeedType type, NewsFeedSupport feedSource) {
    
    Assert.notNull(feedSource, "Source required.");
    
    NewsFeed newsFeed = new NewsFeed(type, feedSource);
    CompanyNewsFeed companyNewsFeed = newsFeedFactory.getCompanyNewsFeed(companyId);
    companyNewsFeed.add(newsFeed);
    newsFeedFactory.updateCompanyNewsFeed(companyNewsFeed);
    return newsFeed;
  }
  
  /**
   * Get the news feed given by the feed reference id.
   * 
   * @param feedRefId
   *          - feed reference id
   * @return - the news feed
   */
  // public NewsFeed findNewsFeedByFeedRef(String feedRefId) {
  // return companyNewsFeed.findNewsFeedByFeedRef(feedRefId);
  // }
  
  /**
   * Update the news feed for the given feed source.
   * 
   * @param newsFeed
   *          - news feed
   */
  public synchronized void updateNewsFeed(NewsFeedSupport newsFeed) {
    CompanyNewsFeed companyNewsFeed = newsFeedFactory.getCompanyNewsFeed(companyId);
    companyNewsFeed.updateNewsFeed(newsFeed);
    newsFeedFactory.updateCompanyNewsFeed(companyNewsFeed);
  }
  
  /**
   * Delete the news feed for the feed source.
   * 
   * @param feedSource
   *          - feed source
   */
  public synchronized void deleteNewsFeed(NewsFeedSupport feedSource) {
    CompanyNewsFeed companyNewsFeed = newsFeedFactory.getCompanyNewsFeed(companyId);
    companyNewsFeed.deleteNewsFeed(feedSource);
    newsFeedFactory.updateCompanyNewsFeed(companyNewsFeed);
  }
  
  /**
   * Add the given news feed to the company news feed.
   * 
   * @param newsFeed
   *          - news feed to add
   */
  public synchronized void addNewsFeed(NewsFeed newsFeed) {
    CompanyNewsFeed companyNewsFeed = newsFeedFactory.getCompanyNewsFeed(companyId);
    companyNewsFeed.add(newsFeed);
    newsFeedFactory.updateCompanyNewsFeed(companyNewsFeed);
  }
  
  /**
   * Get a read only instance of the news feed.
   * 
   * @return - read only instance of company news feed
   */
  public List<NewsFeed> getNewsFeeds() {
    CompanyNewsFeed companyNewsFeed = newsFeedFactory.getCompanyNewsFeed(companyId);
    return Collections.unmodifiableList(companyNewsFeed.getNewsFeeds());
  }
  
  /**
   * Get the news feeds from the given start idx.
   * 
   * @param startIdx
   *          - start index
   * @return get the sublist
   */
  public List<NewsFeed> getNewsFeeds(int startIdx) {
    final List<NewsFeed> newsFeeds = getNewsFeeds();
    if (startIdx > 0) {
      return newsFeeds.subList(startIdx, newsFeeds.size());
    }
    return newsFeeds;
  }

  /**
   * Delete the message from the news feed and the database.
   * 
   * @param message
   *          - message to delete
   */
  public void deleteDashboardMessage(DashboardMessage message) {
    deleteNewsFeed(message);
    newsFeedFactory.deleteDashboardMessage(message);
  }
  
}
