package com.sp.web.model.competency;

import com.sp.web.dto.BaseUserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Dax Abraham
 * 
 *         The model class to store the user competency evaluation.
 */
public class UserEvaluationResult implements Serializable {
  
  private static final long serialVersionUID = 386605700172025720L;
  private BaseUserDTO member;
  private BaseCompetencyEvaluationScore selfEvaluation;
  private UserCompetencyEvaluationScore managerEvaluation;
  private List<UserCompetencyEvaluationScore> peerEvaluation;
  
  /**
   * Default constructor.
   */
  public UserEvaluationResult() { /* Default */
  }
  
  /**
   * Constructor from user.
   * 
   * @param member
   *          - user
   */
  public UserEvaluationResult(User member) {
    this.member = new BaseUserDTO(member);
  }
  
  public BaseUserDTO getMember() {
    return member;
  }
  
  public void setMember(BaseUserDTO member) {
    this.member = member;
  }
  
  public BaseCompetencyEvaluationScore getSelfEvaluation() {
    return selfEvaluation;
  }
  
  public void setSelfEvaluation(BaseCompetencyEvaluationScore selfEvaluation) {
    this.selfEvaluation = selfEvaluation;
  }
  
  public UserCompetencyEvaluationScore getManagerEvaluation() {
    return managerEvaluation;
  }
  
  public void setManagerEvaluation(UserCompetencyEvaluationScore managerEvaluation) {
    this.managerEvaluation = managerEvaluation;
  }
  
  public List<UserCompetencyEvaluationScore> getPeerEvaluation() {
    return peerEvaluation;
  }
  
  public void setPeerEvaluation(List<UserCompetencyEvaluationScore> peerEvaluation) {
    this.peerEvaluation = peerEvaluation;
  }
  
  /**
   * Adds to the peer evaluation request list.
   * 
   * @param userCompetencyEvaluationScore
   *          - user competency evaluation score
   */
  public void addPeer(UserCompetencyEvaluationScore userCompetencyEvaluationScore) {
    if (peerEvaluation == null) {
      peerEvaluation = new ArrayList<UserCompetencyEvaluationScore>();
    }
    peerEvaluation.add(userCompetencyEvaluationScore);
  }
  
  /**
   * Updates the evaluation score for the given type.
   * 
   * @param type
   *          - evaluation type
   * @param competencyEvaluationDetails
   *          - competency evaluation details
   */
  public void update(EvaluationType type,
      UserCompetencyEvaluationDetails competencyEvaluationDetails) {
    switch (type) {
    case Self:
      selfEvaluation = new BaseCompetencyEvaluationScore(competencyEvaluationDetails);
      break;
    
    case Manager:
      Assert.notNull(managerEvaluation, "Evaluation not initiated.");
      managerEvaluation.updateFrom(competencyEvaluationDetails);
      break;
    
    case Peer:
      Assert.notEmpty(peerEvaluation, "Evaluation not initiated.");
      final String reviewerId = competencyEvaluationDetails.getReviewerId();
      Optional<UserCompetencyEvaluationScore> findFirst = peerEvaluation.stream()
          .filter(pe -> pe.checkUserId(reviewerId)).findFirst();
      if (findFirst.isPresent()) {
        findFirst.get().updateFrom(competencyEvaluationDetails);
      } else {
        throw new InvalidRequestException("Peer request not initiated.");
      }
      break;
    
    default:
      throw new InvalidRequestException(type + ": not supported.");
    }
  }
  
  /**
   * Method to validate if the managers user id matches.
   * 
   * @param managerId
   *          - manger id
   */
  public void validateManager(String managerId) {
    Assert.notNull(managerEvaluation, "Manager evalution not set.");
    Assert.isTrue(managerEvaluation.checkUserId(managerId), "Unauthorized request.");
  }
  
  /**
   * Get the reviewer.
   * 
   * @param reviewerId
   *          - reviewer id
   * @return the reviewer
   */
  public BaseUserDTO getReviewer(EvaluationType type, String reviewerId) {
    BaseUserDTO reviewer = null;
    switch (type) {
    case Self:
      reviewer = member;
      break;
    
    case Manager:
      Assert.notNull(managerEvaluation, "Manager evaluation not set.");
      reviewer = managerEvaluation.getReviewer();
      break;
    
    case Peer:
      Assert.notNull(peerEvaluation, "Peer review not found.");
      Optional<UserCompetencyEvaluationScore> findFirst = peerEvaluation.stream()
          .filter(pe -> pe.checkUserId(reviewerId)).findFirst();
      Assert.isTrue(findFirst.isPresent(), "Peer review not found.");
      reviewer = findFirst.get().getReviewer();
      break;
    
    default:
      throw new InvalidRequestException("Not supported.");
    }
    return reviewer;
  }
  
  /**
   * Method to check if the user is already in the manager request or peer request list.
   * 
   * @param userToCheck
   *          - user to check
   * 
   */
  public void checkDuplicate(FeedbackUser userToCheck) {
    // checking for manager
    if (managerEvaluation != null) {
      doCheckDuplicate(managerEvaluation, userToCheck);
    }
    
    // check all the peer evaluations
    if (!CollectionUtils.isEmpty(peerEvaluation)) {
      peerEvaluation.forEach(peerRequest -> doCheckDuplicate(peerRequest, userToCheck));
    }
  }
  
  /**
   * Check if the user is already existing in the request list.
   * 
   * @param existingUser
   *          - existing user
   * @param userToCheck
   *          - user to check
   */
  private void doCheckDuplicate(UserCompetencyEvaluationScore existingUser, FeedbackUser userToCheck) {
    Assert.isTrue(!existingUser.checkIfSameUser(userToCheck), "User already part of evaluation.");
  }
  
  /**
   * Adding the evaluation request.
   * 
   * @param evaluationType
   *          - evaluation type
   * @param userToAdd
   *          - user to add
   */
  public void addRequest(EvaluationType evaluationType, FeedbackUser userToAdd) {
    // checking if it is a duplicate request
    checkDuplicate(userToAdd);
    
    // adding the request
    switch (evaluationType) {
    
    case Manager:
      managerEvaluation = new UserCompetencyEvaluationScore(userToAdd);
      break;
      
    case Peer:
      addPeer(new UserCompetencyEvaluationScore(userToAdd));
      break;
      
    default:
      throw new InvalidRequestException("Not supported.");
    }
  }
}
