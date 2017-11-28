package com.sp.web.service.feed;

import com.sp.web.model.User;
import com.sp.web.model.feed.NewsFeed;

/**
 * @author Dax Abraham
 * 
 *         The interface for all the news feed processors.
 */
public interface NewsFeedProcessor {
  
  Object process(User user, NewsFeed newsFeed, boolean filterByUser);
}
