package com.sp.web.service.feed;

import java.io.Serializable;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         This interface must be implemented by all the instances that would like to support the
 *         news feed.
 */
public interface NewsFeedSupport extends Serializable {

  /**
   * The unique id to identify the feed source.
   * 
   * @return
   *      the unique identifier 
   */
  String getFeedRefId();
  
  /**
   * @return
   *    - flag indicating if feed applicable for all company.
   */
  boolean isAllCompany();
  
  /**
   * @return
   *   - the list of members that have unfollowed the given feed.
   */
  List<String> getUnfollowMemberIds();
  
  /**
   * @return
   *    - get the list of members for whom the feed is applicable.
   */
  List<String> getMemberIds();
}
