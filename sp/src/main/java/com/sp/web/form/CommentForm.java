package com.sp.web.form;

import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.ContentReference;
import com.sp.web.model.User;
import com.sp.web.model.poll.SPMiniPoll;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * Comment form class contains the form for the adding a new comment to note.
 * 
 * @author pradeep ruhil
 *
 */
public class CommentForm {
  
  private String comment;
  private ContentReference contentReference;
  private int cid = -1;
  private SPMiniPoll miniPoll;
  
  public String getComment() {
    return comment;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  /**
   * update the existing comment.
   * 
   * @param existingComment
   *          existing comment.
   */
  public void updateComment(Comment existingComment) {
    existingComment.setText(comment);
    existingComment.setContentReference(contentReference);
    existingComment.setUpdatedOn(LocalDateTime.now());
  }
  
  public ContentReference getContentReference() {
    return contentReference;
  }
  
  public void setContentReference(ContentReference contentReference) {
    this.contentReference = contentReference;
  }
  
  /**
   * Validate if the data in the form is valid.
   */
  public void validate() {
    Assert.isTrue(!(StringUtils.isEmpty(comment) && contentReference == null),
        "Content or content reference not found.");
    if (miniPoll != null) {
      Assert.hasText(miniPoll.getQuestion(), "Question not present for the minipoll");
    }
  }
  
  /**
   * Create a new comment object from the given data.
   * 
   * @return the new comment object
   */
  public Comment newComment() {
    Comment comment = new Comment();
    comment.setCreatedOn(LocalDateTime.now());
    comment.setContentReference(contentReference);
    comment.setText(this.comment);
    if (miniPoll != null) {
      comment.setMiniPoll(miniPoll);
    }
    return comment;
  }
  
  /**
   * Create a new comment.
   * 
   * @param user
   *          - user to set
   * @return the new comment
   */
  public Comment newComment(User user) {
    final Comment newComment = newComment();
    newComment.setUser(new UserMarkerDTO(user));
    return newComment;
  }
  
  public int getCid() {
    return cid;
  }
  
  public void setCid(int cid) {
    this.cid = cid;
  }
  
  public void setMiniPoll(SPMiniPoll miniPoll) {
    this.miniPoll = miniPoll;
  }
  
  public SPMiniPoll getMiniPoll() {
    return miniPoll;
  }
  
}
