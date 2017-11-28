package com.sp.web.model.pulse;

import java.time.LocalDate;

/**
 * @author Dax Abraham
 *
 *         The model class for all the pulse requests.
 */
public class PulseRequest {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String pulseQuestionSetId;
  private String companyId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
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

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }
}
