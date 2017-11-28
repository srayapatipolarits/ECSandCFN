package com.sp.web.model.feed;

/**
 * @author Dax Abraham
 * 
 *         The Enumeration for identifying the different types of news feed.
 */
public enum NewsFeedType {
  PublicChannel("publiChannelNewsFeedProcessor"), DashboardMessage(
      "dashboardMessageNewsFeedProcessor"), MemberActivity("defaultNewsFeedProcessor"), SPUpdate(
      "defaultNewsFeedProcessor");
  
  private String handler;
  
  private NewsFeedType(String handler) {
    this.handler = handler;
  }
  
  public String getHandler() {
    return handler;
  }
}
