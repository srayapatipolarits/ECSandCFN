package com.sp.web.dto.pulse;

import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseQuestionSetStatus;
import com.sp.web.model.pulse.QuestionSetType;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 * 
 *         The pulse question set DTO.
 */
public class PulseQuestionSetDTO {
  
  private String name;
  private List<String> categoryKeys;
  private Map<String, List<PulseQuestionBean>> questions;
  private boolean isForAll;
  private List<String> companyId;
  private LocalDate createdOn;
  private String createdOnFormatted;
  private LocalDate updatedOn;
  private String updatedOnFormatted;
  private QuestionSetType questionSetType;
  private PulseQuestionSetStatus status;
  
  /**
   * Constructor.
   * 
   * @param pulseQuestionSet
   *            - the pulse question set
   */
  public PulseQuestionSetDTO(PulseQuestionSet pulseQuestionSet) {
    BeanUtils.copyProperties(pulseQuestionSet, this);
    this.createdOnFormatted = MessagesHelper.formatDate(getCreatedOn());
    this.updatedOnFormatted = MessagesHelper.formatDate(getUpdatedOn());
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public List<String> getCategoryKeys() {
    return categoryKeys;
  }
  
  public void setCategoryKeys(List<String> categoryKeys) {
    this.categoryKeys = categoryKeys;
  }
  
  public Map<String, List<PulseQuestionBean>> getQuestions() {
    return questions;
  }
  
  public void setQuestions(Map<String, List<PulseQuestionBean>> questions) {
    this.questions = questions;
  }
  
  public boolean isForAll() {
    return isForAll;
  }
  
  public void setForAll(boolean isForAll) {
    this.isForAll = isForAll;
  }
  
  public List<String> getCompanyId() {
    return companyId;
  }
  
  public void setCompanyId(List<String> companyId) {
    this.companyId = companyId;
  }
  
  public LocalDate getCreatedOn() {
    return createdOn;
  }
  
  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }
  
  public LocalDate getUpdatedOn() {
    return updatedOn;
  }
  
  public void setUpdatedOn(LocalDate updatedOn) {
    this.updatedOn = updatedOn;
  }
  
  public QuestionSetType getQuestionSetType() {
    return questionSetType;
  }
  
  public void setQuestionSetType(QuestionSetType questionSetType) {
    this.questionSetType = questionSetType;
  }
  
  public PulseQuestionSetStatus getStatus() {
    return status;
  }
  
  public void setStatus(PulseQuestionSetStatus status) {
    this.status = status;
  }

  public String getCreatedOnFormatted() {
    return createdOnFormatted;
  }

  public void setCreatedOnFormatted(String createdOnFormatted) {
    this.createdOnFormatted = createdOnFormatted;
  }

  public String getUpdatedOnFormatted() {
    return updatedOnFormatted;
  }

  public void setUpdatedOnFormatted(String updatedOnFormatted) {
    this.updatedOnFormatted = updatedOnFormatted;
  }
  
}
