package com.sp.web.model;

import com.sp.web.dto.BaseUserDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Dax Abraham
 *
 *         The model class to store the user messages.
 */
public class UserMessage implements Serializable {
  
  private static final long serialVersionUID = -7339381136733000130L;
  
  private String uid;
  private String content;
  private SPFeature feature;
  private BaseUserDTO from;
  private LocalDateTime createdOn;
  
  /**
   * Default constructor.
   */
  public UserMessage() { } 
  
  
  /**
   * Constructor with UID.
   * 
   * @param uid
   *        - Unique id
   * @param message
   *        - message 
   * @param feature 
   *        - feature
   */
  public UserMessage(String uid, SPFeature feature, String message) {
    this.uid = uid;
    this.feature = feature;
    this.content = message;
    createdOn = LocalDateTime.now();
  }

  public String getUid() {
    return uid;
  }
  
  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public SPFeature getFeature() {
    return feature;
  }
  
  public void setFeature(SPFeature feature) {
    this.feature = feature;
  }
  
  public BaseUserDTO getFrom() {
    return from;
  }
  
  public void setFrom(BaseUserDTO from) {
    this.from = from;
  }


  public LocalDateTime getCreatedOn() {
    return createdOn;
  }


  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
}
