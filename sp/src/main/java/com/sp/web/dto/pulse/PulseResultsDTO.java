package com.sp.web.dto.pulse;

import com.sp.web.model.pulse.PulseResults;
import com.sp.web.model.pulse.PulseScore;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The pulse result dto object.
 */
public class PulseResultsDTO {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String pulseQuestionSetId;
  private int numberOfRespondents;
  private int numberOfMembers;
  private Map<String, PulseScore> pulseScore;
  private boolean isInProgress;

  public PulseResultsDTO(PulseResults pulseResults) {
    BeanUtils.copyProperties(pulseResults, this);
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public String getStartDateFormatted() {
    return (startDate != null) ? MessagesHelper.formatDate(startDate) : null;
  }
  
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public String getEndDateFormatted() {
    return (endDate != null) ? MessagesHelper.formatDate(endDate) : null;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public String getPulseQuestionSetId() {
    return pulseQuestionSetId;
  }

  public void setPulseQuestionSetId(String pulseQuestionSetId) {
    this.pulseQuestionSetId = pulseQuestionSetId;
  }

  public Map<String, PulseScore> getPulseScore() {
    return pulseScore;
  }

  public void setPulseScore(Map<String, PulseScore> pulseScore) {
    this.pulseScore = pulseScore;
  }

  public int getNumberOfRespondents() {
    return numberOfRespondents;
  }

  public void setNumberOfRespondents(int numberOfRespondents) {
    this.numberOfRespondents = numberOfRespondents;
  }

  public int getNumberOfMembers() {
    return numberOfMembers;
  }

  public void setNumberOfMembers(int numberOfMembers) {
    this.numberOfMembers = numberOfMembers;
  }

  public boolean isInProgress() {
    return isInProgress;
  }

  public void setInProgress(boolean isInProgress) {
    this.isInProgress = isInProgress;
  }
}
