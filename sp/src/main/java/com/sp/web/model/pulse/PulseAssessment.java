package com.sp.web.model.pulse;

import java.util.List;
import java.util.Map;

/**
 * @author Dax Abraham
 *
 *         The model entity to store the pulse assessment response.
 */
public class PulseAssessment {

  private String id;
  private String pulseRequestId;
  private String memberId;
  private Map<String, List<PulseSelection>> assessment;

  /**
   * Default constructor.
   */
  public PulseAssessment() {}
  
  /**
   * Constructor.
   * 
   * @param pulseRequestId
   *            - request id
   * @param memberId
   *            - member id
   */
  public PulseAssessment(String pulseRequestId, String memberId) {
    this.pulseRequestId = pulseRequestId;
    this.memberId = memberId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPulseRequestId() {
    return pulseRequestId;
  }

  public void setPulseRequestId(String pulseRequestId) {
    this.pulseRequestId = pulseRequestId;
  }

  public String getMemberId() {
    return memberId;
  }

  public void setMemberId(String memberId) {
    this.memberId = memberId;
  }

  public Map<String, List<PulseSelection>> getAssessment() {
    return assessment;
  }

  public void setAssessment(Map<String, List<PulseSelection>> assessment) {
    this.assessment = assessment;
  }
}
