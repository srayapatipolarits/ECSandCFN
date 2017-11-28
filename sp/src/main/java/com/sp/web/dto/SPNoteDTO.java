package com.sp.web.dto;

import com.sp.web.model.goal.SPNote;
import com.sp.web.model.goal.SPNoteFeedbackType;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author vikram
 *
 *         The spnote DTO.
 */
public class SPNoteDTO implements Serializable {
  
  private static final long serialVersionUID = -8825922475683658846L;
  
  private String id;
  
  // private String goalId;
  
  private String content;
  
  private String userid;
  
  private LocalDateTime createdOn = null;
  
  private String formattedDate;
  
  private SPNoteFeedbackType type;
  
  /**
   * Default constructor.
   * 
   * @param spnote
   *          - the sp note object
   */
  public SPNoteDTO(SPNote spnote) {
    BeanUtils.copyProperties(spnote, this);
    // formatting date for the front end
    if (createdOn != null) {
      formattedDate = MessagesHelper.formatDate(LocalDate.from(createdOn), "MMM dd, yyyy");
    }
  }
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public String getUserid() {
    return userid;
  }
  
  public void setUserid(String userid) {
    this.userid = userid;
  }
  
  public SPNoteFeedbackType getType() {
    return type;
  }
  
  public void setType(SPNoteFeedbackType type) {
    this.type = type;
  }
    
  public String getFormattedDate() {
    return formattedDate;
  }
  
  public void setFormattedDate(String formattedDate) {
    this.formattedDate = formattedDate;
  }

  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }
  
}
