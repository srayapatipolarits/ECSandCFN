package com.sp.web.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Dax Abraham The comments entity.
 */
public class Comments implements Serializable {

  /**
   * Default serial version id.
   */
  private static final long serialVersionUID = -7250987309471283791L;

  /* Date comment was created on */
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate createdOn;
  /* The id of the user that the comment was created by */
  private String by;
  /* The email of the by */
  private String byEmail;
  /* The comment */
  private String comment;
  private UserType userType;

  /**
   * Default constructor.
   */
  public Comments() {
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
  public Comments(String by, String comment) {
    this.by = by;
    this.comment = comment;
    this.createdOn = LocalDate.now();
  }

  /**
   * Constructor.
   * 
   * @param feedbackUser
   *            - feedback user
   * @param commentStr
   *            - comment
   */
  public Comments(User feedbackUser, String commentStr) {
    this(feedbackUser.getId(), commentStr);
    this.byEmail = feedbackUser.getEmail();
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public String getBy() {
    return by;
  }

  public void setBy(String by) {
    this.by = by;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getByEmail() {
    return byEmail;
  }

  public void setByEmail(String byEmail) {
    this.byEmail = byEmail;
  }

  public UserType getUserType() {
    return userType;
  }

  public void setUserType(UserType userType) {
    this.userType = userType;
  }
}
