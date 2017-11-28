package com.sp.web.dto.bm.ta;

import com.sp.web.dto.user.UserGroupAssociationDTO;
import com.sp.web.model.User;
import com.sp.web.model.bm.ta.ToneAnalysisResult;
import com.sp.web.model.bm.ta.UserToneAnalysis;

/**
 * 
 * @author Dax Abraham
 *
 *         The DTO class for user tone analysis.
 */
public class UserToneAnalysisDTO {
  
  private UserGroupAssociationDTO user;
  private ToneAnalysisResult aggregateAnalysis;
  
  /**
   * Default Constructor.
   */
  public UserToneAnalysisDTO() { }
  
  /**
   * Constructor from user and user tone analysis.
   * 
   * @param user
   *          - user
   * @param userToneAnalysis
   *          - user tone analysis
   */
  public UserToneAnalysisDTO(User user, UserToneAnalysis userToneAnalysis) {
    this.user = new UserGroupAssociationDTO(user);
    this.aggregateAnalysis = userToneAnalysis.getAggregateToneAnalysis();
  }

  public UserGroupAssociationDTO getUser() {
    return user;
  }
  
  public void setUser(UserGroupAssociationDTO user) {
    this.user = user;
  }
  
  public ToneAnalysisResult getAggregateAnalysis() {
    return aggregateAnalysis;
  }
  
  public void setAggregateAnalysis(ToneAnalysisResult aggregateAnalysis) {
    this.aggregateAnalysis = aggregateAnalysis;
  }
}
