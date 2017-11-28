package com.sp.web.model.pulse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The Entity class for the pulse questions.
 */
public class PulseQuestionSet {

  private String id;
  private String name;
  private List<String> categoryKeys;
  private Map<String, List<PulseQuestionBean>> questions;
  private boolean isForAll;
  private List<String> companyId;
  private LocalDate createdOn;
  private LocalDate updatedOn;
  private QuestionSetType questionSetType;
  private PulseQuestionSetStatus status;

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

  public Map<String, List<PulseQuestionBean>> getQuestions() {
    return questions;
  }

  public void setQuestions(Map<String, List<PulseQuestionBean>> questions) {
    this.questions = questions;
  }

  public List<String> getCategoryKeys() {
    return categoryKeys;
  }

  public void setCategoryKeys(List<String> categoryKeys) {
    this.categoryKeys = categoryKeys;
  }

  @Override
  public String toString() {
    return "[id:" + id + "],[name:" + name + "],[categoryKeys:" + categoryKeys + "], [questions:"
        + questions + "]";
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
  
  
}
