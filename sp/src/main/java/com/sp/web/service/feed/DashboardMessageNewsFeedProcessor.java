package com.sp.web.service.feed;

import com.sp.web.dto.feed.DashboardMessageNewsFeedDTO;
import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.NewsFeed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The default news feed processor.
 */
@Component("dashboardMessageNewsFeedProcessor")
public class DashboardMessageNewsFeedProcessor implements NewsFeedProcessor {
  
  @Autowired
  NewsFeedFactory newsFeedFactory;
  
  @Override
  public Object process(User user, NewsFeed newsFeed, boolean filterByUser) {
    DashboardMessage dashboardMessage = newsFeedFactory.getDashboardMessage(newsFeed.getFeedRefId());
    if (dashboardMessage != null) {
      final String userId = user.getId();
      // check if this is a filter by user request
      if (filterByUser) {
        if (dashboardMessage.filterByUser(userId)) {
          return new DashboardMessageNewsFeedDTO(user, dashboardMessage);
        }
      } else {
        if (dashboardMessage.isApplicable(userId)) {
          return new DashboardMessageNewsFeedDTO(user, dashboardMessage);          
        }
      }
    }
    return null;
  }
  
}
