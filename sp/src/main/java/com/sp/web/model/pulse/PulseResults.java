package com.sp.web.model.pulse;

import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The list of pulse results for the pulse requests.
 */
public class PulseResults {

  private String id;
  private LocalDate startDate;
  private LocalDate endDate;
  private String companyId;
  private String pulseRequestId;
  private String pulseQuestionSetId;
  private int numberOfRespondents;
  private int numberOfMembers;
  private Map<String, PulseScore> pulseScore;

  /**
   * Default Constructor.
   */
  public PulseResults() {}
  
  /**
   * Constructor to create the pulse result from the request.
   * 
   * @param pulseRequest
   *            - the pulse request
   */
  public PulseResults(PulseRequest pulseRequest) {
    BeanUtils.copyProperties(pulseRequest, this);
    this.pulseRequestId = pulseRequest.getId();
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

  public Map<String, PulseScore> getPulseScore() {
    return pulseScore;
  }

  public void setPulseScore(Map<String, PulseScore> pulseScore) {
    this.pulseScore = pulseScore;
  }

  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public String getPulseRequestId() {
    return pulseRequestId;
  }

  public void setPulseRequestId(String pulseRequestId) {
    this.pulseRequestId = pulseRequestId;
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
}
