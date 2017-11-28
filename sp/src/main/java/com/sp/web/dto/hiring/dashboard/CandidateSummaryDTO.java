package com.sp.web.dto.hiring.dashboard;

public class CandidateSummaryDTO extends EmployeeSummaryDTO {
  
  /**
   * 
   */
  private static final long serialVersionUID = 3166188521302138847L;
  
  private int referencesPending;
  
  public int getReferencesPending() {
    return referencesPending;
  }
  
  public void setReferencesPending(int referencesPending) {
    this.referencesPending = referencesPending;
  }
  
  public void increaseReferencesPending() {
    referencesPending += 1;
  }
}
