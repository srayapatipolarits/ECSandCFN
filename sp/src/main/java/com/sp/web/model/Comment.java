package com.sp.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.poll.SPMiniPoll;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The entity class for the comments.
 */
public class Comment implements Serializable {
  
  private static final long serialVersionUID = 5456159373664050772L;
  
  private int cid;
  private LocalDateTime createdOn;
  private String text;
  private UserMarkerDTO user;
  private UserMarkerDTO onBehalfUser;
  private ContentReference contentReference;
  private LocalDateTime updatedOn;
  @JsonIgnoreProperties
  private Map<String, UserMarkerDTO> likedByMembers;
  private List<Comment> childComments;
  @JsonIgnoreProperties
  private HashSet<String> notificationEmails;
  @JsonIgnoreProperties
  private int childCommentCounter = 0;
  
  private SPMiniPoll miniPoll;
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public ContentReference getContentReference() {
    return contentReference;
  }
  
  public void setContentReference(ContentReference contentReference) {
    this.contentReference = contentReference;
  }
  
  public int getCid() {
    return cid;
  }
  
  public void setCid(int cid) {
    this.cid = cid;
  }
  
  public UserMarkerDTO getUser() {
    return user;
  }
  
  public void setUser(UserMarkerDTO user) {
    this.user = user;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  /**
   * Create a new comment with the data passed.
   * 
   * @param user
   *          - user
   * @param message
   *          - message
   * @return the new comment
   */
  public static Comment newCommment(User user, String message) {
    Comment comment = new Comment();
    comment.setCreatedOn(LocalDateTime.now());
    comment.setUser(new UserMarkerDTO(user));
    comment.setText(message);
    return comment;
  }
  
  /**
   * Creating an empty comment with the given message.
   * 
   * @param message
   *          - message
   * @return the comment message
   */
  public static Comment newCommment(String message) {
    Comment comment = new Comment();
    comment.setText(message);
    return comment;
  }
  
  public UserMarkerDTO getOnBehalfUser() {
    return onBehalfUser;
  }
  
  public void setOnBehalfUser(UserMarkerDTO onBehalfUser) {
    this.onBehalfUser = onBehalfUser;
  }
  
  public Map<String, UserMarkerDTO> getLikedByMembers() {
    return likedByMembers;
  }
  
  public void setLikedByMembers(Map<String, UserMarkerDTO> likedByMembers) {
    this.likedByMembers = likedByMembers;
  }
  
  public List<Comment> getChildComments() {
    return childComments;
  }
  
  public void setChildComments(List<Comment> childComments) {
    this.childComments = childComments;
  }
  
  /**
   * Add the new comment to the list of child comments.
   * 
   * @param newComment
   *          - new comment to add
   */
  public synchronized void addComment(Comment newComment) {
    if (childComments == null) {
      childComments = new LinkedList<Comment>();
    }
    newComment.setCid(childCommentCounter++);
    childComments.add(0, newComment);
  }
  
  /**
   * Add the user to the notification email set.
   * 
   * @param user
   *          - user
   */
  public synchronized void addToNotifications(User user) {
    if (getNotificationEmails() == null) {
      setNotificationEmails(new HashSet<String>());
    }
    getNotificationEmails().add(user.getEmail());
  }
  
  public HashSet<String> getNotificationEmails() {
    return notificationEmails;
  }
  
  public void setNotificationEmails(HashSet<String> notificationEmails) {
    this.notificationEmails = notificationEmails;
  }
  
  public boolean hasReference() {
    return contentReference != null && contentReference.isValid();
  }
  
  /**
   * Update the like user.
   * 
   * @param likeUser
   *          - like user
   * @param childCid
   *          - child comment id
   * @param isLike 
   *          - is like flag
   * @return the count of like users
   */
  public int updateLike(User likeUser, int childCid, MutableBoolean isLike) {
    if (childCid >= 0) {
      Comment comment = getComment(childCid);
      return comment.updateLike(likeUser, isLike);
    } else {
      if (likedByMembers == null) {
        likedByMembers = new HashMap<String, UserMarkerDTO>();
      }
      final String likeUserId = likeUser.getId();
      if (likedByMembers.containsKey(likeUserId)) {
        likedByMembers.remove(likeUserId);
        isLike.setFalse();
      } else {
        likedByMembers.put(likeUserId, new UserMarkerDTO(likeUser));
      }
      return likedByMembers.size();
    }
  }
  
  /**
   * Update the like user for the comment.
   * 
   * @param likeUser
   *          - like user
   * @param isLike
   *          - is like flag 
   * @return the count of like users
   */
  public int updateLike(User likeUser, MutableBoolean isLike) {
    return updateLike(likeUser, -1, isLike);
  }
  
  /**
   * Get the child comment.
   * 
   * @param childCid
   *          - child comment id
   * @return the child comment
   */
  public Comment getComment(int childCid) {
    Assert.isTrue(!CollectionUtils.isEmpty(childComments), "Comment not found.");
    Optional<Comment> findFirst = childComments.stream().filter(c -> c.getCid() == childCid)
        .findFirst();
    Assert.isTrue(findFirst.isPresent(), "Comment not found.");
    return findFirst.get();
  }
  
  /**
   * Check if the user id is the same as the comment owner user id.
   * 
   * @param userId
   *          - user id
   * @return true if same user else false
   */
  public boolean isOwner(String userId) {
    return user.getId().equals(userId);
  }
  
  /**
   * Delete the given child comment.
   * 
   * @param childComment
   *          - comment to delete
   */
  public void deleteComment(Comment childComment) {
    if (!CollectionUtils.isEmpty(childComments)) {
      childComments.remove(childComment);
      final String email = childComment.getUser().getEmail();
      Optional<Comment> findFirst = childComments.stream()
          .filter(c -> c.getUser().sameEmail(email)).findFirst();
      if (!findFirst.isPresent()) {
        removeFromNotifications(email);
      }
    }
  }
  
  /**
   * Remove the given email from the email notifications list.
   * 
   * @param email
   *          - email to remove
   */
  private void removeFromNotifications(String email) {
    if (!CollectionUtils.isEmpty(notificationEmails)) {
      notificationEmails.remove(email);
    }
  }
  
  public int getLikeCount() {
    return (likedByMembers != null) ? likedByMembers.size() : 0;
  }
  
  public boolean isLikedByUser(String userId) {
    return (likedByMembers != null) ? likedByMembers.containsKey(userId) : false;
  }
  
  public int getChildCommentCounter() {
    return childCommentCounter;
  }
  
  public void setChildCommentCounter(int childCommentCounter) {
    this.childCommentCounter = childCommentCounter;
  }
  
  public void setMiniPoll(SPMiniPoll miniPoll) {
    this.miniPoll = miniPoll;
  }
  
  public SPMiniPoll getMiniPoll() {
    return miniPoll;
  }
}
