package com.sp.web.dto;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.UserStatus;
import com.sp.web.utils.MessagesHelper;

/**
 * @author Dax Abraham
 * 
 *         The DTO object for the feedback user entity used for account dashboard.
 */
public class AccountDashboardFeedbackUserDTO extends BaseUserDTO {

  private static final long serialVersionUID = 4642844163241831938L;
  
  private String createdOnStr;
  private String completedOnStr;

  /**
   * Constructor.
   * 
   * @param feedbackUser
   *            - feedback user
   */
  public AccountDashboardFeedbackUserDTO(FeedbackUser feedbackUser) {
    super(feedbackUser);
    if (feedbackUser.getCreatedOn() != null) {
      createdOnStr = MessagesHelper.formatDate(feedbackUser.getCreatedOn());
    }
    
    if (feedbackUser.getUserStatus() == UserStatus.VALID) {
      final AnalysisBean analysis = feedbackUser.getAnalysis();
      if (analysis != null && analysis.getCreatedOn() != null) {
        completedOnStr = MessagesHelper.formatDate(analysis.getCreatedOn().toLocalDate());
      }
    }
  }

  public String getCreatedOnStr() {
    return createdOnStr;
  }

  public void setCreatedOnStr(String createdOnStr) {
    this.createdOnStr = createdOnStr;
  }

  public String getCompletedOnStr() {
    return completedOnStr;
  }

  public void setCompletedOnStr(String completedOnStr) {
    this.completedOnStr = completedOnStr;
  }
}
