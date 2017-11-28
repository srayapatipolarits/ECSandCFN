package com.sp.web.dto.feed;

import com.sp.web.Constants;
import com.sp.web.dto.CommentDTO;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.model.feed.DashboardMessageType;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for dashboard message.
 */
public class DashboardMessageDTO implements Serializable {
  
  private static final long serialVersionUID = 365197315401445724L;
  private String id;
  private Comment message;
  private List<CommentDTO> comments = null;
  private int likeCount;
  private List<UserMarkerDTO> taggedMembers;
  private LocalDateTime updatedOn;
  private DashboardMessageType type;
  private int commentCount;
  private boolean blockCommenting;
  
  /**
   * Constructor.
   * 
   * @param message
   *            - dashboard message
   */
  public DashboardMessageDTO(DashboardMessage message, String userId) {
    BeanUtils.copyProperties(message, this, "comments");
    List<Comment> messageComments = message.getComments();
    if (!CollectionUtils.isEmpty(messageComments)) {
      // Setting the comments count
      commentCount = messageComments.size();
      int arrSize = (commentCount > Constants.NEWS_FEED_COMMENT_LIMIT) ? Constants.NEWS_FEED_COMMENT_LIMIT
          : commentCount;
      comments = new ArrayList<CommentDTO>(arrSize);
      for (int i = 0; i < arrSize; i++) {
        comments.add(new CommentDTO(messageComments.get(i), userId));
      }
    }
  }

  /**
   * Constructor for the new details page.
   * 
   * @param message
   *           - message
   * @param userId
   *           - user id
   * @param msgDetailsPage
   *            - flag to indicate message details page
   */
  public DashboardMessageDTO(DashboardMessage message, String userId, boolean msgDetailsPage) {
    BeanUtils.copyProperties(message, this, "comments");
    List<Comment> messageComments = message.getComments();
    if (!CollectionUtils.isEmpty(messageComments)) {
      // Setting the comments count
      commentCount = messageComments.size();
      int arrSize = (msgDetailsPage) ? Constants.NEWS_FEED_COMMENT_MSG_DETAILS_LIMIT
          : Constants.NEWS_FEED_COMMENT_LIMIT;
      if (commentCount < arrSize) {
        arrSize = commentCount;
      }
      comments = new ArrayList<CommentDTO>(arrSize);
      for (int i = 0; i < arrSize; i++) {
        comments.add(new CommentDTO(messageComments.get(i), userId));
      }
    }
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Comment getMessage() {
    return message;
  }
  
  public void setMessage(Comment message) {
    this.message = message;
  }
  
  public int getLikeCount() {
    return likeCount;
  }
  
  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }
  
  public List<UserMarkerDTO> getTaggedMembers() {
    return taggedMembers;
  }
  
  public void setTaggedMembers(List<UserMarkerDTO> taggedMembers) {
    this.taggedMembers = taggedMembers;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }

  public DashboardMessageType getType() {
    return type;
  }

  public void setType(DashboardMessageType type) {
    this.type = type;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public boolean isBlockCommenting() {
    return blockCommenting;
  }

  public void setBlockCommenting(boolean blockCommenting) {
    this.blockCommenting = blockCommenting;
  }

  public List<CommentDTO> getComments() {
    return comments;
  }

  public void setComments(List<CommentDTO> comments) {
    this.comments = comments;
  }
}
