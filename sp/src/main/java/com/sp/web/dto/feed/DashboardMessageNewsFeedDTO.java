package com.sp.web.dto.feed;

import com.sp.web.model.User;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.DashboardMessageType;
import com.sp.web.model.poll.SPMiniPollResult;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for dashboard message.
 */
public class DashboardMessageNewsFeedDTO extends DashboardMessageDTO {
  
  private static final long serialVersionUID = -8735506366788362004L;
  private boolean owner;
  private boolean tagged;
  private boolean likedByUser;
  private boolean hasCompleted;
  private List<Integer> userNewsFeedSelection;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   * @param message
   *          - dashboard message
   */
  public DashboardMessageNewsFeedDTO(User user, DashboardMessage message) {
    this(user.getId(), message, false);
  }
  
  /**
   * Constructor.
   * 
   * @param userId
   *          - user id
   * @param message
   *          - message
   * @param msgDetailsPage
   *          - message details page flag
   */
  public DashboardMessageNewsFeedDTO(String userId, DashboardMessage message, boolean msgDetailsPage) {
    super(message, userId, msgDetailsPage);
    owner = userId.equals(message.getOwnerId());
    tagged = message.isTagged(userId);
    likedByUser = message.isLikedByUser(userId);
    if (message.getType() == DashboardMessageType.MiniPolls) {
      SPMiniPollResult result = message.getMessage().getMiniPoll().getResult();
      if (result != null) {
        List<Integer> list = result.getUserPollSelection().get(userId);
        if (list != null) {
          setHasCompleted(true);
          setUserNewsFeedSelection(list);
        }
      }
    }
  }
  
  public boolean isOwner() {
    return owner;
  }
  
  public void setOwner(boolean owner) {
    this.owner = owner;
  }
  
  public boolean isTagged() {
    return tagged;
  }
  
  public void setTagged(boolean tagged) {
    this.tagged = tagged;
  }
  
  public boolean isLikedByUser() {
    return likedByUser;
  }
  
  public void setLikedByUser(boolean likedByUser) {
    this.likedByUser = likedByUser;
  }
  
  public boolean isHasCompleted() {
    return hasCompleted;
  }
  
  public void setHasCompleted(boolean hasCompleted) {
    this.hasCompleted = hasCompleted;
  }
  
  public List<Integer> getUserNewsFeedSelection() {
    return userNewsFeedSelection;
  }
  
  public void setUserNewsFeedSelection(List<Integer> userNewsFeedSelection) {
    this.userNewsFeedSelection = userNewsFeedSelection;
  }
  
}
