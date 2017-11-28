package com.sp.web.model.hiring.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.model.User;
import com.sp.web.utils.GenericUtils;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Dax Abraham The comments entity.
 */
public class HiringComment implements Serializable {

  private static final long serialVersionUID = -7250987309471283791L;

  private String cid;
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate createdOn;
  private String comment;
  private UserMarkerDTO by;

  /**
   * Default constructor.
   */
  public HiringComment() {
    // default constructor
  }
  
  /**
   * Constructor.
   * 
   * @param by
   *          - by id
   * @param comment
   *          - the comment
   */
  public HiringComment(User by, String comment) {
    this.setBy(new UserMarkerDTO(by));
    this.comment = comment;
    this.createdOn = LocalDate.now();
    this.cid = GenericUtils.getId();
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public UserMarkerDTO getBy() {
    return by;
  }

  public void setBy(UserMarkerDTO by) {
    this.by = by;
  }
}
