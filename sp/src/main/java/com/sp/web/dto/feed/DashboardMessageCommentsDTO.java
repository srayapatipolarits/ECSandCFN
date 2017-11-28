package com.sp.web.dto.feed;

import com.sp.web.dto.CommentDTO;
import com.sp.web.model.Comment;
import com.sp.web.model.feed.DashboardMessage;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class for dashboard message.
 */
public class DashboardMessageCommentsDTO {
  
  private String id;
  private List<CommentDTO> comments = null;
  
  /**
   * Constructor.
   * 
   * @param message
   *          - dashboard message
   */
  public DashboardMessageCommentsDTO(DashboardMessage message, String userId) {
    this.id = message.getRefId();
    List<Comment> messageComments = message.getComments();
    if (!CollectionUtils.isEmpty(messageComments)) {
      this.setComments(messageComments.stream().map(c -> new CommentDTO(c, userId))
          .collect(Collectors.toList()));
    }
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }

  public List<CommentDTO> getComments() {
    return comments;
  }

  public void setComments(List<CommentDTO> comments) {
    this.comments = comments;
  }
}
