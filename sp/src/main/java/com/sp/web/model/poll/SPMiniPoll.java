package com.sp.web.model.poll;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SPMiniPoll class holds the mini polls details.
 * 
 * @author pradeepruhil
 *
 */

public class SPMiniPoll implements Serializable {
  
  /**
   * 
   */
  private static final long serialVersionUID = -7067708743827134818L;
  
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime endDate;
  
  private boolean hideResults;
  
  private String question;
  
  private String instructionStr;
  
  private SPMiniPollType type;
  
  private List<String> options;
  
  private SPSelectionType selectionType;
  
  private SPMiniPollResult result;
  
  private boolean sendNotificationEmail;
  
  public LocalDateTime getEndDate() {
    return endDate;
  }
  
  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }
  
  public boolean isHideResults() {
    return hideResults;
  }
  
  public void setHideResults(boolean hideResults) {
    this.hideResults = hideResults;
  }
  
  public String getQuestion() {
    return question;
  }
  
  public void setQuestion(String question) {
    this.question = question;
  }
  
  public String getInstructionStr() {
    return instructionStr;
  }
  
  public void setInstructionStr(String instructionStr) {
    this.instructionStr = instructionStr;
  }
  
  public SPMiniPollType getType() {
    return type;
  }
  
  public void setType(SPMiniPollType type) {
    this.type = type;
  }
  
  public List<String> getOptions() {
    return options;
  }
  
  public void setOptions(List<String> options) {
    this.options = options;
  }
  
  public SPSelectionType getSelectionType() {
    return selectionType;
  }
  
  public void setSelectionType(SPSelectionType selectionType) {
    this.selectionType = selectionType;
  }
  
  public SPMiniPollResult getResult() {
    return result;
  }
  
  public void setResult(SPMiniPollResult result) {
    this.result = result;
  }
  
  public void setSendNotificationEmail(boolean sendNotificationEmail) {
    this.sendNotificationEmail = sendNotificationEmail;
  }
  
  public boolean isSendNotificationEmail() {
    return sendNotificationEmail;
  }
  
  public boolean isScale() {
    return type == SPMiniPollType.Scale;
  }
}
