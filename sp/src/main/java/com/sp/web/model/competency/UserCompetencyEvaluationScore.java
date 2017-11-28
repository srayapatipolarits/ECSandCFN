package com.sp.web.model.competency;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;

import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 * 
 *         The model class to store the user information along with competency scores.
 */
public class UserCompetencyEvaluationScore extends BaseCompetencyEvaluationScore {
  
  private static final long serialVersionUID = -1190420153247851595L;
  private BaseUserDTO reviewer;
  
  /**
   * Default constructor.
   */
  public UserCompetencyEvaluationScore() {
    super();
  }
  
  /**
   * Constructor from feedback user.
   * 
   * @param reviewer
   *          - reviewer
   */
  public UserCompetencyEvaluationScore(FeedbackUser reviewer) {
    this.reviewer = new BaseUserDTO(reviewer);
  }
  
  public BaseUserDTO getReviewer() {
    return reviewer;
  }
  
  public void setReviewer(BaseUserDTO reviewer) {
    this.reviewer = reviewer;
  }
  
  /**
   * Check if the user id matches with the reviewer's id.
   * 
   * @param reviewerId
   *          - reviewer id
   * @return true if same else false
   */
  public boolean checkUserId(String reviewerId) {
    return (reviewer != null && reviewer.getId().equals(reviewerId));
  }
  
  @Override
  public void updateFrom(UserCompetencyEvaluationDetails competencyEvaluationDetails) {
    Assert.isTrue(checkUserId(competencyEvaluationDetails.getReviewerId()),
        "Reviewer not authorized.");
    super.updateFrom(competencyEvaluationDetails);
  }
  
  /**
   * Check if the email of the reviewer is same as that of the user to check.
   * 
   * @param userToCheck
   *          - user to check
   * @return true if same else false
   */
  public boolean checkIfSameUser(User userToCheck) {
    return (reviewer != null && reviewer.getEmail().equals(userToCheck.getEmail()));
  }

  /**
   * Check if the email of the reviewer is same as that of the user to check.
   * 
   * @param userToCheck
   *          - user to check
   * @return true if same else false
   */
  public boolean checkIfSameUser(BaseUserDTO userToCheck) {
    return (reviewer != null && reviewer.getEmail().equals(userToCheck.getEmail()));
  }
}
