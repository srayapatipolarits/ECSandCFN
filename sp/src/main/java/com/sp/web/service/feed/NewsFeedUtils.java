package com.sp.web.service.feed;

import com.sp.web.Constants;
import com.sp.web.utils.GenericUtils;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         A collection of utility functions for the News feed.
 */
public class NewsFeedUtils {

  /**
   * Truncate the given list to the news feed limits.
   *  
   * @param listToTruncate
   *            - list to truncate
   * @return
   *      the truncated list
   */
  public static <T> List<T> truncateList(List<T> listToTruncate) {
    return GenericUtils.truncateList(listToTruncate, Constants.NEWS_FEED_COMMENT_LIMIT);
  }

  /**
   * Truncate the given list to the news feed limits.
   *  
   * @param listToTruncate
   *            - list to truncate
   * @return
   *      the truncated list
   */
  public static <T> List<T> truncateComments(List<T> listToTruncate) {
    return GenericUtils.truncateComments(listToTruncate, Constants.NEWS_FEED_COMMENT_LIMIT);
  }
  
}
