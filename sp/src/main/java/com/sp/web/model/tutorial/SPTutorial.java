package com.sp.web.model.tutorial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sp.web.dto.user.UserMarkerDTO;
import com.sp.web.service.translation.Translable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The model class for Tutorials.
 */
public class SPTutorial implements Serializable {
  
  private static final long serialVersionUID = -243772611340609027L;
  private String id;
  @Translable
  private String name;
  @Translable
  private String description;
  private String imgUrl;
  private boolean active;
  private boolean forAllUsers;
  private List<String> stepIds;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdOn;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime updatedOn;
  private UserMarkerDTO updatedBy;
  private int uidCount;
  private int actionCount;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getImgUrl() {
    return imgUrl;
  }
  
  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public boolean isForAllUsers() {
    return forAllUsers;
  }
  
  public void setForAllUsers(boolean forAllUsers) {
    this.forAllUsers = forAllUsers;
  }
  
  public List<String> getStepIds() {
    return stepIds;
  }
  
  public void setStepIds(List<String> stepIds) {
    this.stepIds = stepIds;
  }
  
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDateTime getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDateTime updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public UserMarkerDTO getUpdatedBy() {
    return updatedBy;
  }
  
  public void setUpdatedBy(UserMarkerDTO updatedBy) {
    this.updatedBy = updatedBy;
  }
  
  /**
   * Gets the next UID.
   * 
   * @return the next UID
   */
  public String getNextUID() {
    return "" + uidCount++;
  }

  public int getActionCount() {
    return actionCount;
  }

  public void setActionCount(int actionCount) {
    this.actionCount = actionCount;
  }

  public int getUidCount() {
    return uidCount;
  }

  public void setUidCount(int uidCount) {
    this.uidCount = uidCount;
  }
  
}
