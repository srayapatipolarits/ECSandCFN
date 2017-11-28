package com.sp.web.service.feed;

import com.sp.web.dto.feed.NewsFeedDTO;
import com.sp.web.model.User;
import com.sp.web.model.feed.NewsFeed;

import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The default news feed processor.
 */
@Component("defaultNewsFeedProcessor")
public class DefaultNewsFeedProcessor implements NewsFeedProcessor {
  
  @Override
  public Object process(User user, NewsFeed newsFeed, boolean filterByUser) {
    if (!filterByUser) {
      return new NewsFeedDTO(newsFeed);
    }
    return null;
  }
  
}
