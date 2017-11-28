package com.sp.web.dto.feed;

import com.sp.web.model.Comment;
import com.sp.web.model.feed.DashboardMessage;

import java.io.Serializable;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for dashboard message and comment.
 */
public class DashboardMessageCommentDTO  implements Serializable{
  
  private static final long serialVersionUID = 7809415515402495645L;
  private String id;
  private Comment comment = null;
  private int cid = -1;
  
  /**
   * Constructor.
   * 
   * @param message
   *            - dashboard message
   * @param comment
   *            - comment            
   */
  public DashboardMessageCommentDTO(DashboardMessage message, Comment comment) {
    this.id = message.getRefId();
    this.comment = comment;
  }

  /**
   * Constructor.
   * 
   * @param message
   *            - dashboard message
   * @param comment
   *            - comment            
   * @param cid
   *            - comment id
   */
  public DashboardMessageCommentDTO(DashboardMessage message, Comment comment, int cid) {
    this(message, comment);
    this.setCid(cid);
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }

  public Comment getComment() {
    return comment;
  }

  public void setComment(Comment comment) {
    this.comment = comment;
  }

  public int getCid() {
    return cid;
  }

  public void setCid(int cid) {
    this.cid = cid;
  }
  
}
