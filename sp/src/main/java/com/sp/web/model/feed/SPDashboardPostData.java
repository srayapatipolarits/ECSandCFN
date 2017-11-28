package com.sp.web.model.feed;

import com.sp.web.Constants;
import com.sp.web.dto.user.UserMarkerDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The entity that stores all the SurePeople activity feeds.
 */
public class SPDashboardPostData {
  
  private String id;
  private List<SPActivityFeed> activityFeeds;
  private Map<String, Set<String>> companyActivityFeedMap;
  private List<UserMarkerDTO> authorList;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public List<SPActivityFeed> getActivityFeeds() {
    return activityFeeds;
  }
  
  public void setActivityFeeds(List<SPActivityFeed> activityFeeds) {
    this.activityFeeds = activityFeeds;
  }
  
  public Map<String, Set<String>> getCompanyActivityFeedMap() {
    return companyActivityFeedMap;
  }
  
  public void setCompanyActivityFeedMap(Map<String, Set<String>> companyActivityFeedMap) {
    this.companyActivityFeedMap = companyActivityFeedMap;
  }

  /**
   * Create the default instance for the activity feeds.
   * 
   * @return
   *      the default activity feeds instance
   */
  public static SPDashboardPostData defaultInstance() {
    SPDashboardPostData dashboardPostData = new SPDashboardPostData();
    dashboardPostData.setId(Constants.DEFAULT_COMPANY_ID);
    dashboardPostData.setActivityFeeds(new ArrayList<SPActivityFeed>());
    dashboardPostData.setCompanyActivityFeedMap(new HashMap<String, Set<String>>());
    dashboardPostData.setAuthorList(new ArrayList<UserMarkerDTO>());
    return dashboardPostData;
  }

  public List<UserMarkerDTO> getAuthorList() {
    return authorList;
  }

  public void setAuthorList(List<UserMarkerDTO> authorList) {
    this.authorList = authorList;
  }

  /**
   * Remove the given author.
   * 
   * @param author
   *          - author
   * @return
   *    flag for the remove operation
   */
  public boolean removeAuthor(UserMarkerDTO author) {
    return authorList.remove(author);
  }

  /**
   * @param companyId
   *          - get the company activity feed for given company id.
   * @return 
   *    the company activity feed
   */
  public Set<String> getCompanyActivityFeed(String companyId) {
    return companyActivityFeedMap.get(companyId);
  }

  public void setCompanyActivityFeed(String companyId, Set<String> companyActivityFeed) {
    companyActivityFeedMap.put(companyId, companyActivityFeed);
  }
  
}
