package com.sp.web.dto;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for comments.
 */
public class CommentDTO implements Serializable {
  
  private static final long serialVersionUID = -3450441225342281439L;
  private int cid;
  private String text;
  private UserMarkerDTO user;
  private UserMarkerDTO onBehalfUser;
  private ContentReference contentReference;
  private LocalDateTime createdOn;
  private List<CommentDTO> childComments;
  private int likeCount;
  private int commentCount;
  private boolean likedByUser;
  
  /**
   * Constructor.
   * 
   * @param comment
   *          - comment
   */
  public CommentDTO(Comment comment, String userId) {
    BeanUtils.copyProperties(comment, this, "childComments");
    List<Comment> existingChildComments = comment.getChildComments();
    if (!CollectionUtils.isEmpty(existingChildComments)) {
      childComments = existingChildComments.stream().map(c -> new CommentDTO(c, userId)).collect(Collectors.toList());
      commentCount = childComments.size();
    }
    likeCount = comment.getLikeCount();
    if (likeCount > 0) {
      likedByUser = comment.isLikedByUser(userId);
    }
  }

  public int getCid() {
    return cid;
  }
  
  public void setCid(int cid) {
    this.cid = cid;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public UserMarkerDTO getUser() {
    return user;
  }
  
  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
  
  public UserMarkerDTO getOnBehalfUser() {
    return onBehalfUser;
  }
  
  public void setOnBehalfUser(UserMarkerDTO onBehalfUser) {
    this.onBehalfUser = onBehalfUser;
  }
  
  public ContentReference getContentReference() {
    return contentReference;
  }
  
  public void setContentReference(ContentReference contentReference) {
    this.contentReference = contentReference;
  }
  
  public List<CommentDTO> getChildComments() {
    return childComments;
  }
  
  public void setChildComments(List<CommentDTO> childComments) {
    this.childComments = childComments;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public boolean isLikedByUser() {
    return likedByUser;
  }

  public void setLikedByUser(boolean likedByUser) {
    this.likedByUser = likedByUser;
  }
  
}
