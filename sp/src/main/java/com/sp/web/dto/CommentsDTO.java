package com.sp.web.dto;

import com.sp.web.model.Comments;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to send the comments information to the front end.
 */
public class CommentsDTO {

  private LocalDate createdOn;
  private BaseUserDTO by;
  private String comment;

  public CommentsDTO(Comments comment) {
    BeanUtils.copyProperties(comment, this);
  }

  public String getCreatedOnFormatted() {
    return MessagesHelper.formatDate(createdOn);
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public BaseUserDTO getBy() {
    return by;
  }

  public void setBy(BaseUserDTO by) {
    this.by = by;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
