package com.sp.web.dto.user;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.model.User;
import com.sp.web.model.UserType;

public class UserAnalysisDTO extends UserMarkerDTO {
  
  private static final long serialVersionUID = 126690313986370269L;
  private AnalysisBean analysis;
  private UserType type;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - user
   */
  public UserAnalysisDTO(User user) {
    super(user);
  }
  
  public AnalysisBean getAnalysis() {
    return analysis;
  }
  
  public void setAnalysis(AnalysisBean analysis) {
    this.analysis = analysis;
  }
  
  public void setType(UserType type) {
    this.type = type;
  }
  
  public UserType getType() {
    return type;
  }
}
