package com.sp.web.dto.feed;

import com.sp.web.model.feed.DashboardMessage;

/**
 * @author Dax Abraham
 * 
 *         The dashboard message dto with only comments and likes.
 */
public class DashboardMessageCommentsLikesDTO extends DashboardMessageCommentsDTO {
  
  private int likeCount;
  
  /**
   * Constructor with dashboard message comments and likes.
   * 
   * @param message
   *          - message
   */
  public DashboardMessageCommentsLikesDTO(DashboardMessage message, String userId) {
    super(message, userId);
    this.likeCount = message.getLikeCount();
  }
  
  public int getLikeCount() {
    return likeCount;
  }
  
  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }
  
}
